package dev.iwilkey.terrafort.clock;

import java.util.ArrayList;

/*
 *
 *	Sample use:
 
		new ThreadRequest(new SequencedEvent() {
			@Override
			public void onStart() {}
			@Override
			public void onLoop() {
				
			}
			@Override
			public void onKill() {}
		}, 200);
		
 * 
 * 
 * 
 * 
 * 
 * 
 */

public class ThreadRequest {
	
	public static ArrayList<ThreadRequest> requests = new ArrayList<>();

	Thread thread;
	SequencedEvent event;
	
	public ThreadRequest(final SequencedEvent event, final long time) {
		this.event = event;
		thread = new Thread() {
			public void run() {
				event.onStart();
				while(true) {
					event.onLoop();
					try {
						sleep(time);
					} catch (InterruptedException ignored) {}
				}
			}
		};
		
		requests.add(this);
		thread.start();
	}
	
	@SuppressWarnings("deprecation")
	public void kill() {
		System.out.println(thread + ": Thread killed.");
		thread.stop();
		event.onKill();
	}

}
