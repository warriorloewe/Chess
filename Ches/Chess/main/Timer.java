package main;

public class Timer implements Runnable{
	private int tick = 0;
	private int time = 0;
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

	public int getTick() {
		return tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
