package com.re.paas.internal.networking;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

import com.google.common.net.InetAddresses;
import com.re.paas.api.classes.Exceptions;

public class IPAddresses {

	/**
	 * Checks if the specified IPv4 falls within the private IP address block,
	 * as defined in RFC 1918.
	 */
	public static Boolean isWithinPrivateRange(InetAddress o) {

		String ha = o.getHostAddress();
		
		try {

			if(o instanceof Inet4Address) {
				if (new CIDRUtils("10.0.0.0/8").isInRange(ha)
						|| new CIDRUtils("172.16.0.0/12").isInRange(ha)
						|| new CIDRUtils("192.168.0.0/16").isInRange(ha)) {
					return true;
				}
			} else {
				return new CIDRUtils("fd00::/8").isInRange(ha);
			}
			
		} catch (UnknownHostException e) {
			Exceptions.throwRuntime(e);
		}
		
		return false;
	}

	public static InetAddress generateAddress(String cidr) {

		SubnetInfo subnetInfo = new SubnetUtils(cidr).getInfo();

		InetAddress networkAddress = null;
		try {
			networkAddress = InetAddress.getByName(subnetInfo.getNetworkAddress());
		} catch (UnknownHostException e) {
			Exceptions.throwRuntime(e);
		}

		Integer random = new Random().nextInt(Integer.MAX_VALUE);
		InetAddress addr = networkAddress;

		for (int i = 0; i < random; i++) {
			addr = InetAddresses.increment(addr);
		}

		return addr;
	}

	public static InetAddress getAddress(String address) {
		try {
			return InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			return (InetAddress) Exceptions.throwRuntime(e);
		}
	}
}
