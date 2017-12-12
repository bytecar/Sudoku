package com.vkartik.research.Sudoku.utils;

public class timer {

	public timer() {
		TimerIsRunning = false;
		ElapsedMs = 0;
	}

	public void Start() {
		TimerIsRunning = true;
		StartReading = System.currentTimeMillis();
	}

	public void Stop() {
		TimerIsRunning = false;
		ElapsedMs += System.currentTimeMillis() - StartReading;
	}

	public void Reset() {
		TimerIsRunning = false;
		ElapsedMs = 0;
	}

	public boolean IsRunning() {
		return TimerIsRunning;
	}

	public double ElapsedTime() {
		if (!TimerIsRunning)
			return (double) ElapsedMs / 1000;
		else
			return (double) (ElapsedMs + System.currentTimeMillis() - StartReading) / 1000;
	}

	private boolean TimerIsRunning;
	private long ElapsedMs;
	private long StartReading;

}
