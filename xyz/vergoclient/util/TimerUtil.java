package xyz.vergoclient.util;

public class TimerUtil {
	
	public long lastMS = System.currentTimeMillis();;
	
	public void reset() {
		lastMS = System.currentTimeMillis();
	}
	
	public boolean hasTimeElapsed(long time, boolean reset) {
		
		if (lastMS > System.currentTimeMillis()) {
			lastMS = System.currentTimeMillis();
		}
		
		if (System.currentTimeMillis()-lastMS > time) {
			
			if (reset)
				reset();
			
			return true;
				
			
		}else {
			return false;
		}
		
	}

	private long time = System.nanoTime() / 1000000L;

	public boolean reach(long time) {
		return this.time() >= time;
	}

	public void reset1() {
		this.time = System.nanoTime() / 1000000L;
	}

	public boolean sleep(long time) {
		if (this.time() >= time) {
			this.reset();
			return true;
		}
		return false;
	}

	public long time() {
		return System.nanoTime() / 1000000L - this.time;
	}
	
}
