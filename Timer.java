package project;

public class Timer{
	private float milliSecondsPerCycle;		// The number of milliseconds that make up one cycle.
	private long lastUpdate;				// checks when the clock was updated before.
	private int elapsedCycles;				// Number of cycles elapsed.
	private float excessCycles;				// Excess time for the next elapsed clock.
	private boolean isPaused;	


	//returns the current value of the most precise available system timer, in nanoseconds.
	public static final long getCurrentTime(){
		return (System.nanoTime()/1000000L);
	}
	
	// sets boolean whether to pause or not.
	public void setPaused(boolean paused){
		this.isPaused = paused;
	}

	//returns true of it is paused.
	public boolean isPaused(){
		return isPaused;
	}

	//Whether or not a cycle has elapsed.
	public boolean hasElapsedCycle(){
		if(elapsedCycles > 0){
			this.elapsedCycles--;
			return true;
		}
		return false;
	}
	
	//The number of cycles per second.
	public void setCyclesPerSecond(float cyclesPerSecond){
		this.milliSecondsPerCycle = (1.0f/cyclesPerSecond)*1000;
	}
	
	//Updates the clock status.
	public void update(){
		long currentUpdate = getCurrentTime();
		float delta = (float)(currentUpdate-lastUpdate) + excessCycles;

		if(!isPaused){
			this.elapsedCycles += (int)Math.floor(delta/milliSecondsPerCycle);
			this.excessCycles = delta%milliSecondsPerCycle;
		}
		this.lastUpdate = currentUpdate;
	}

	//Resets the clock status
	public void reset(){
		this.lastUpdate = getCurrentTime();
		this.elapsedCycles = 0;
		this.isPaused = false;
		this.excessCycles = 0.0f;
	}

	//The number of cycles that elapse per second.
	public Timer(float cyclesPerSecond){
		this.milliSecondsPerCycle = (1.0f/cyclesPerSecond)*1000;
		reset();
	}
}
