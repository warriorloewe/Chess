package main;

public class Timer implements Runnable{
	public int time = 0;
	public long lastRecordedTime = 0;
	public boolean running = false;
	
	public Timer(int time) {
		this.time = time;
		Thread run = new Thread(this);
		run.start();
	}
	
	public void run() {
		lastRecordedTime = System.currentTimeMillis();
		while(true) {
			if(running) {
				time -= (System.currentTimeMillis() - lastRecordedTime);
			}
			lastRecordedTime = System.currentTimeMillis();
			try {
				Thread.sleep(10);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
