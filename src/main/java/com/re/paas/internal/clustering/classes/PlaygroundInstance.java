package com.re.paas.internal.clustering.classes;

public class PlaygroundInstance {

	
	private String data;
	
	public void start() {
                try {
					System.out.println("Message: " + data);					
                	synchronized (this) {
						while (data == null)
							wait();
						System.out.println("Exec1..");
					}
                	System.out.println("Exec2..");
             	
				} catch (InterruptedException e) {
					System.out.println("Message: " + data);
				}
	}
	
	public void update(String msg){
		data = msg;
	}

}
