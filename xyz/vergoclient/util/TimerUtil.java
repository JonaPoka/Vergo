package xyz.vergoclient.util;

public class TimerUtil {
	
	public long lastMS = System.currentTimeMillis();;
	
	public void reset() {
		lastMS = System.currentTimeMillis();
	}
	
	public boolean hasTimeElapsed(long time, boolean reset) {
		
		if (lastMS > System.currentTimeMillis()) {
			lastMS = System.currentTimeMillis();
//			Command.sendPrivateChatMessage("Fixed timer, did you set the clock on your pc back or something?");
		}
		
		if (System.currentTimeMillis()-lastMS > time) {
			
			if (reset)
				reset();
			
			return true;
				
			
		}else {
			return false;
		}
		
	}
	
}
