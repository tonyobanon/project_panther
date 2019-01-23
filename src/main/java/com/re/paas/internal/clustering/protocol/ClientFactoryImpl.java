package com.re.paas.internal.clustering.protocol;

import java.net.InetAddress;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import com.re.paas.api.annotations.Prototype;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.protocol.Client;
import com.re.paas.api.clustering.protocol.ClientFactory;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.classes.NumberRotator;
import com.re.paas.internal.utils.ObjectUtils;

/**
 * This is the reference Implementation of {@link ClientFactory} with support
 * for context-aware atomicity, using double-checked locking.
 * 
 * @author Tony
 */
@Prototype
public class ClientFactoryImpl implements ClientFactory {

	private static final short MAX_ROTATED_CONNECTIONS = -1;

	private static final short CLIENT_COUNT_BUCKET_SIZE = 25;
	private static final short NODE_COUNT_BUCKET_SIZE = 10;

	private static final double CLIENT_COUNT_RESIZE_MULTIPLIER = 2;
	private static final double NODE_COUNT_RESIZE_MULTIPLIER = 2;

	// Keeps track of the current client indexes for each node
	private static AtomicReferenceArray<Short> clientIndexes;
	
	// Stores the client instances for each node
	private static AtomicReferenceArray<AtomicReferenceArray<ClientImpl>> clients;

	// Keeps track of which clients are available for cluster requests
	private static AtomicReferenceArray<AtomicReferenceArray<Boolean>> clientStatuses;

	// Stores rotators that keeps track of the currently assigned clientId
	private static AtomicReferenceArray<NumberRotator> clientRotators;
	
	private static AtomicReferenceArray<Stack<Short>> unusedClientIndexes;

	public ClientFactoryImpl() {

		// Read Configuration
		// .... Todo

		
		clientIndexes = new AtomicReferenceArray<>(NODE_COUNT_BUCKET_SIZE);
		clients = new AtomicReferenceArray<AtomicReferenceArray<ClientImpl>>(NODE_COUNT_BUCKET_SIZE);

		if (rotationEnabled()) {
			clientRotators = new AtomicReferenceArray<>(NODE_COUNT_BUCKET_SIZE);
			unusedClientIndexes = new AtomicReferenceArray<>(NODE_COUNT_BUCKET_SIZE);
		} else {
			clientStatuses = new AtomicReferenceArray<AtomicReferenceArray<Boolean>>(NODE_COUNT_BUCKET_SIZE);
		}

		Sync.init();
	}

	/**
	 * This is called when a new node is to be registered, but the array sizes are
	 * not sufficient to store data for the new node. In this function, all arrays,
	 * including those in the {@link Sync} class are resized. This operation issues
	 * a tier 1 lock that halts all operations on this {@link ClientFactory}
	 * instance
	 * 
	 * @param multiplier multiplier which will determine the new length of the arrays
	 */
	private static void resizeNodeArrays(double multiplier) {

		Sync.tier1Lock.lock();

		Sync.__awaitTier2Lock();

		if (!rotationEnabled()) {
			Sync.__awaitTier3Lock();
		}

		int len = clientIndexes.length();
		int newLength = (int) (len * multiplier);

		clientIndexes = ObjectUtils.cloneArrayReference(clientIndexes, newLength);
		clients = ObjectUtils.cloneMultiArrayReference(clients, newLength);

		ReentrantLock[] newtier2Lock = new ReentrantLock[newLength];
		System.arraycopy(Sync.tier2Lock, 0, newtier2Lock, 0, len);
		Sync.tier2Lock = newtier2Lock;

		if (rotationEnabled()) {
			
			clientRotators = ObjectUtils.cloneArrayReference(clientRotators, newLength);
			unusedClientIndexes = ObjectUtils.cloneArrayReference(unusedClientIndexes, newLength);

		} else {

			clientStatuses = ObjectUtils.cloneMultiArrayReference(clientStatuses, newLength);

			ReentrantLock[][] newtier3Lock = new ReentrantLock[newLength][];
			System.arraycopy(Sync.tier3Lock, 0, newtier3Lock, 0, len);
			Sync.tier3Lock = newtier3Lock;
		}

		Sync.tier1Lock.unlock();
		Sync.tier1Lock.notify();
	}

	/**
	 * This is called when a new client connection is to be made to a node in the
	 * cluster, but the array sizes (for this nodeId) are not sufficient to store
	 * data for the new connection. In this function, the relevant arrays, including
	 * those in the {@link Sync} class are resized. The caller of this function
	 * should issues a tier 2 lock on its behalf.
	 * 
	 * @param nodeId     The Node Id
	 * @param multiplier multiplier which will determine the new length of the
	 *                   arrays
	 *                   
	 * @return {@link Integer} The new 
	 */
	private static int resizeClientArrays(Short nodeId, double multiplier) {

		if (!rotationEnabled()) {
			Sync.__awaitTier3Lock(nodeId);
		}
		
		int length = clients.get(nodeId).length();
		int newLength = (int) (length * multiplier);

		if (newLength > Short.MAX_VALUE) {
			// Add Flag to indicate that this nodeId can no longer scale up
			newLength = Short.MAX_VALUE;
		}

		clients.set(nodeId, ObjectUtils.cloneArrayReference(clients.get(nodeId), newLength));

		if (!rotationEnabled()) {
			
			clientStatuses.set(nodeId, ObjectUtils.cloneArrayReference(clientStatuses.get(nodeId), newLength));

			ReentrantLock[] newLocks = new ReentrantLock[newLength];
			System.arraycopy(Sync.tier3Lock[nodeId], 0, newLocks, 0, length);
			Sync.tier3Lock[nodeId] = newLocks;
		}
		
		return newLength;
	}

	/**
	 * This is called when a new node is to be registered. It sets up the arrays to
	 * be used by the new node, including those in the {@link Sync} class. It also
	 * sets the connection count for this nodeId to 0.<br>
	 * 
	 * @param nodeId The Node Id
	 */
	private static void initNode(Short nodeId) {

		Sync.awaitTier1Lock();

		clientIndexes.set(nodeId, (short) 0);
		clients.set(nodeId, new AtomicReferenceArray<ClientImpl>(CLIENT_COUNT_BUCKET_SIZE));

		Sync.tier2Lock[nodeId] = new ReentrantLock();

		if (rotationEnabled()) {
			
			Stack<Short> stack = new Stack<>();
			stack.ensureCapacity(Short.MAX_VALUE - getMaxRotatedClients());

			unusedClientIndexes.set(nodeId, stack);

		} else {
			clientStatuses.set(nodeId, new AtomicReferenceArray<Boolean>(CLIENT_COUNT_BUCKET_SIZE));
			Sync.tier3Lock[nodeId] = new ReentrantLock[CLIENT_COUNT_BUCKET_SIZE];
		}
	}

	private static boolean hasClientIndex(Short nodeId, Short clientId) {
		try {
			@SuppressWarnings("unused")
			Client client = clients.get(nodeId).get(clientId);
			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * 
	 * @param nodeId Node whose connection should rotate
	 * @param limit  Limit (Exclusive)
	 */
	private static void startClientRotation(Short nodeId, NumberRotator rotator) {
		clientRotators.set(nodeId, rotator);
	}

	/**
	 * This function checks if a rotator is added for the specified node. For
	 * correctness, it is imperative that call check rotationEnabled() first before
	 * calling this.
	 * 
	 * @param nodeId
	 * @return
	 */
	private static boolean hasRotatedClients(Short nodeId) {
		NumberRotator rotator = clientRotators.get(nodeId);
		return rotator != null;
	}

	private static NumberRotator getClientRotator(Short nodeId) {
		return clientRotators.get(nodeId);
	}

	private static short getRotatedClient(Short nodeId) {
		return (short) clientRotators.get(nodeId).next();
	}

	static boolean rotationEnabled() {
		return getMaxRotatedClients() > 0;
	}

	/**
	 * This registers a new client connection for the specified node. This operation
	 * issues a tier 2 lock
	 * 
	 * @param nodeId Node Id
	 * @return
	 */
	private static short addClient(Short nodeId) {

		Sync.awaitTier2Lock(nodeId);

		Sync.tier2Lock[nodeId].lock();

		Short clientIndex = clientIndexes.get(nodeId);
		Short len = (short) (clientIndex + 1);

		if (len > Short.MAX_VALUE) {

			if (rotationEnabled()) {

				Stack<Short> stack = unusedClientIndexes.get(nodeId);

				/*
				 * First, check if the unusedClientIndexes stack is full. This is an indicator
				 * that all client indexes within the range: getMaxRotatedClients() and
				 * Short.MAX_VALUE - 1 are ready to be re-used
				 */

				if (stack.size() == stack.capacity()) {

					stack.clear();

					clientIndex = getMaxRotatedClients();
					len = (short) (clientIndex + 1);

					clientIndexes.set(nodeId, len);

				} else {

					if (stack.isEmpty()) {
						while (true) {
							LockSupport.parkNanos(500);
							if (!stack.isEmpty()) {
								break;
							}
						}
					}

					// Here, we use a random clientId in unusedClientIndexes
					// Note: for each index we use, we set the client id to -1 in the
					// stack to avoid using that same client id in the future

					int index;

					do {
						index = Utils.nextRandomInt(stack.size());
						clientIndex = stack.elementAt(index);
					} while (clientIndex < 0);

					stack.set(index, (short) -1);
					len = (short) (clientIndex + 1);
				}

			} else {
				return -1;
			}

		} else {
			clientIndexes.set(nodeId, len);
		}

		if (!hasClientIndex(nodeId, clientIndex)) {
			resizeClientArrays(nodeId, CLIENT_COUNT_RESIZE_MULTIPLIER);
		}

		Sync.tier2Lock[nodeId].unlock();
		Sync.tier2Lock[nodeId].notify();

		ClientImpl client;

		if (rotationEnabled() && hasRotatedClients(nodeId)) {

			Short nexusClientId = getRotatedClient(nodeId);
			ClientImpl nexus = clients.get(nodeId).get(nexusClientId);

			client = new ClientImpl(nodeId, clientIndex, nexus);

		} else {
			client = new ClientImpl(nodeId, clientIndex);
		}

		clients.get(nodeId).set(clientIndex, client);

		if (!rotationEnabled()) {

			Sync.tier3Lock[nodeId][clientIndex] = new ReentrantLock();
			clientStatuses.get(nodeId).set(clientIndex, false);

		} else if (/* !hasRotatedClients(nodeId) && */ len == getMaxRotatedClients()) {
			startClientRotation(nodeId, new NumberRotator(0, len));
		}

		return clientIndex;
	}

	@Override
	public void addNode(Short nodeId) {

		if (nodeId >= clientIndexes.length()) {
			resizeNodeArrays(NODE_COUNT_RESIZE_MULTIPLIER);
		}

		initNode(nodeId);
		addClient(nodeId);
	}

	/**
	 * This releases a node, and deallocates resources used by it.
	 * 
	 * @param nodeId
	 * 
	 * @author Tony
	 */
	@Override
	public void releaseNode(Short nodeId) {

		Sync.awaitTier2Lock(nodeId);

		Sync.tier2Lock[nodeId].lock();

		short len = clientIndexes.get(nodeId);

		if (!rotationEnabled()) {

			Sync.__awaitTier3Lock(nodeId);

			for (short i = 0; i < len; i++) {
				clients.get(nodeId).get(i).close();
			}

		} else {

			for (short i = 0; i < len; i++) {
				ClientImpl client = clients.get(nodeId).get(i);

				if (!hasRotatedClients(nodeId) || getClientRotator(nodeId).isWithinRange(i) || client.ownsChannel()) {
					client.close();
				}
			}
		}

		clients.set(nodeId, null);
		clientIndexes.set(nodeId, null);

		if (rotationEnabled()) {

			clientRotators.set(nodeId, null);
			unusedClientIndexes.set(nodeId, null);

		} else {
			clientStatuses.set(nodeId, null);
			Sync.tier3Lock[nodeId] = null;
		}

		Sync.tier2Lock[nodeId].unlock();
		Sync.tier2Lock[nodeId].notify();

		Sync.tier2Lock[nodeId] = null;
	}

	/**
	 * Releases the specified client. This function is used to clean up clients if
	 * rotation is enabled
	 * 
	 * @param nodeId
	 * @param clientId
	 */
	private static void releaseClient(Short nodeId, Short clientId) {

		if (!hasRotatedClients(nodeId)) {
			return;
		}

		if (getClientRotator(nodeId).isWithinRange(clientId)) {
			return;
		}

		ClientImpl client = clients.get(nodeId).get(clientId);

		if (client.ownsChannel()) {
			client.close();
		}

		clients.get(nodeId).set(clientId, null);
		unusedClientIndexes.get(nodeId).push(clientId);
	}

	/**
	 * This function retrieves an available client that can be used to make call(s)
	 * to the specified node.
	 * 
	 * @param nodeId
	 * @return
	 */
	private static short getAvailableClient(Short nodeId) {

		Sync.awaitTier2Lock(nodeId);

		AtomicReferenceArray<Boolean> statuses = clientStatuses.get(nodeId);
		for (int i = 0; i < statuses.length(); i++) {

			if (statuses.get(i) == null) {
				continue;
			}

			Sync.awaitTier3Lock(nodeId, (short) i);

			if (statuses.get(i) && clients.get(nodeId).get(i).getProvisional() == null) {
				return (short) i;
			}
		}
		return -1;
	}

	private static short getAvailableClientOrCreate(Short nodeId) {

		Short clientId = getAvailableClient(nodeId);

		if (clientId == -1) {
			clientId = addClient(nodeId);
		}

		if (clientId == -1) {
			LockSupport.parkNanos(500);
			return getAvailableClientOrCreate(nodeId);
		}

		return clientId;
	}

	@Override
	public Client getClient(Short nodeId) {

		if (NodeRegistry.get().getNodeId().equals(nodeId)) {
			return new ClientImpl(nodeId);
		}

		if (rotationEnabled()) {
			Client client = clients.get(nodeId).get(addClient(nodeId));
			return client;
		}

		short clientId = getAvailableClientOrCreate(nodeId);

		Client client = clients.get(nodeId).get(clientId);
		return client;
	}

	@Override
	public Client getClient(InetAddress host, Integer port) {
		return new ClientImpl(host, port);
	}

	@Override
	public boolean isRotated() {
		return rotationEnabled();
	}

	@Override
	public Short maxRotatedClients() {
		return getMaxRotatedClients();
	}

	private static Short getMaxRotatedClients() {
		return MAX_ROTATED_CONNECTIONS;
	}

	/**
	 * This is invoked by {@link ClientImpl} just when a request is about to be sent
	 * out.
	 *
	 * @param client
	 */
	static void clientInUse(ClientImpl client) {

		if (rotationEnabled()) {
			return;
		}

		Short nodeId = client.getNodeId();
		Short clientId = client.getClientId();

		Sync.awaitTier3Lock(nodeId, clientId);

		Boolean b = clientStatuses.get(nodeId).get(clientId);

		if (b == null || b) {
			clientId = getAvailableClientOrCreate(nodeId);
			client.setProvisional(clients.get(nodeId).get(clientId));
		}

		updateConnectionStatus(nodeId, clientId, true);
	}

	/**
	 * This is invoked by {@link ClientImpl} just when a request completes.
	 *
	 * @param client
	 */
	static void clientFree(ClientImpl client) {

		Short nodeId = client.getNodeId();
		Short clientId = client.getClientId();

		if (rotationEnabled()) {
			releaseClient(nodeId, clientId);
			return;
		}

		ClientImpl provisional = client.getProvisional();

		if (provisional != null) {
			clientId = provisional.getClientId();
			client.setProvisional(null);
		}

		updateConnectionStatus(nodeId, clientId, false);
	}

	private static void updateConnectionStatus(Short nodeId, Short clientId, boolean b) {

		Sync.awaitTier2Lock(nodeId);
		Sync.tier3Lock[nodeId][clientId].lock();

		clientStatuses.get(nodeId).set(clientId, b);

		Sync.tier3Lock[nodeId][clientId].unlock();
		Sync.tier3Lock[nodeId][clientId].notify();
	}

	private static class Sync {

		private static volatile ReentrantLock tier1Lock;
		private static volatile ReentrantLock[] tier2Lock;
		private static volatile ReentrantLock[][] tier3Lock;

		private static void init() {

			tier1Lock = new ReentrantLock(true);
			tier2Lock = new ReentrantLock[NODE_COUNT_BUCKET_SIZE];

			if (!rotationEnabled()) {
				tier3Lock = new ReentrantLock[NODE_COUNT_BUCKET_SIZE][CLIENT_COUNT_BUCKET_SIZE];
			}
		}

		

		/**
		 * This thread will wait, if from another thread the following method(s) are
		 * called:
		 * <li>{@link ClientFactoryImpl#resizeNodeArrays(double)}</li>
		 * 
		 */
		private static void awaitTier1Lock() {
			ObjectUtils.awaitLock(Sync.tier1Lock);
		}

		/**
		 * This thread will wait, if from another thread the following method(s) are
		 * called:
		 * <li>{@link ClientFactoryImpl#resizeNodeArrays(double)}</li>
		 * <li>{@link ClientFactoryImpl#addClient(Short)}</li>
		 * <li>{@link ClientFactoryImpl#resizeClientArrays(Short, double)}</li>
		 * <li>{@link ClientFactoryImpl#releaseNode(Short)}</li>
		 * 
		 */
		private static void awaitTier2Lock(Short nodeId) {
			ObjectUtils.awaitLock(Sync.tier1Lock);
			ObjectUtils.awaitLock(Sync.tier2Lock[nodeId]);
		}

		private static void __awaitTier2Lock() {
			for (int i = 0; i < Sync.tier2Lock.length; i++) {
				if (Sync.tier2Lock[i] == null) {
					continue;
				}
				ObjectUtils.awaitLock(Sync.tier2Lock[i]);
			}
		}

		/**
		 * This thread will wait, if from another thread the following method(s) are
		 * called:
		 * <li>{@link ClientFactoryImpl#resizeNodeArrays(double)}</li>
		 * <li>{@link ClientFactoryImpl#addClient(Short)}</li>
		 * <li>{@link ClientFactoryImpl#resizeClientArrays(Short, double)}</li>
		 * <li>{@link ClientFactoryImpl#releaseNode(Short)}</li>
		 * <li>{@link ClientFactoryImpl#updateConnectionStatus(Short, Short, boolean)}</li>
		 * 
		 */
		private static void awaitTier3Lock(Short nodeId, Short clientId) {
			ObjectUtils.awaitLock(Sync.tier1Lock);
			ObjectUtils.awaitLock(Sync.tier2Lock[nodeId]);
			ObjectUtils.awaitLock(Sync.tier3Lock[nodeId][clientId]);
		}

		private static void __awaitTier3Lock(Short nodeId) {

			for (int j = 0; j < Sync.tier3Lock[nodeId].length; j++) {
				if (Sync.tier3Lock[nodeId][j] == null) {
					continue;
				}
				ObjectUtils.awaitLock(Sync.tier3Lock[nodeId][j]);
			}
		}

		private static void __awaitTier3Lock() {
			for (int i = 0; i < Sync.tier3Lock.length; i++) {
				if (Sync.tier3Lock[i] == null) {
					continue;
				}
				__awaitTier3Lock((short) i);
			}
		}
	}

}
