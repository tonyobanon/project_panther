package com.re.paas.internal.clustering.classes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

public class Playground {
	
	private static PlaygroundInstance o;

	public static void main(String[] args)
			throws InterruptedException, URISyntaxException, NoSuchAlgorithmException, IOException {

		System.out.println(new InetSocketAddress("127.0.0.1", 8080));
		
		
		// TODO Auto-generated method stub
		// We are running one JVM for every four cores
		// int cores = Runtime.getRuntime().availableProcessors();
		// System.out.println("Number of Cores: " + cores);
		// double jvmCount = (double) (cores / 2);
		// double roundOff = Math.floor(jvmCount);

		// long memorySize =
		// ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax();

		// MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		// Object attribute = mBeanServer.getAttribute(new
		// ObjectName("java.lang","type","OperatingSystem"),
		// "TotalPhysicalMemorySize");
		// System.out.println("Total memory: "+ attribute.toString() +" B");

		// Long totalHeapSize = Runtime.getRuntime().maxMemory();

		// Double h = (totalHeapSize / (1024.0 * 1024.0 * 1024.0));
		// System.out.println(totalHeapSize);
		// System.out.println(h + " GB");

		// System.out.println(ManagementFactory.getRuntimeMXBean().getName());
		// String[] lines = IOUtils.readLines(new
		// File("C:/Users/Tony/Desktop/abc.txt"));
		// System.out.println(new
		// File("C:/Users/Tony/Desktop/abc.txt").exists());
		// System.out.println(lines.length);

		// String[] caches = {"sessions", "games"};

		// System.out.println(ClassLoader.getSystemClassLoader().);
		// NetworkInterface nic = NetworkInterface.getByName("eth0");
		// byte[] mac = nic.getHardwareAddress();
		// System.out.println(Utils.toMACAddress(mac));

		/*
		 * 
		 * Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
		 * new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * OperatingSystemMXBean bean =
		 * (com.sun.management.OperatingSystemMXBean) ManagementFactory
		 * .getOperatingSystemMXBean();
		 * 
		 * 
		 * System.out.println(bean.getSystemCpuLoad());
		 * System.out.println(bean.getFreePhysicalMemorySize());
		 * 
		 * 
		 * 
		 * System.out.println();
		 * 
		 * } }, 1, 1, TimeUnit.SECONDS);
		 * 
		 */

		// System.out.println(new
		// SubnetUtils("192.34.78.3/16").getInfo().getNetworkAddress());

		// byte[] b = Utils.randomBytes(24);

		// for (int o : b){
		// System.out.println(o);
		// }

		// System.out.println(3 % 3);

		
		/*
		
		Thread t = new Thread(() -> {
			 o = new PlaygroundInstance();
			 o.start();
		});
		
		t.start();
		
		
		Thread.currentThread().sleep(3000);
		
		o.update("Tony");
		
		Thread.currentThread().sleep(3000);
		
		t.interrupt();
		
		*/
		
		
		// OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		
		//System.out.println((int) (8 / 2)); 
		//System.out.println((int) (7 % 2));
		
		
		
		
		
	}

}
