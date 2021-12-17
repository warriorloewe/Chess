package main;

public class Timer implements Runnable{
	public int tick = 0;
	public int time = 0;
	public void startTimer() {
		Thread run = new Thread(this);
		run.start();
	}
	
	public void run() {
		while(true) {
			tick++;
			if(tick % 1000 == 0) {
				time++;
			}
			try {
				Thread.sleep(1);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
