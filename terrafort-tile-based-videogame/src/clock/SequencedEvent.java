package dev.iwilkey.terrafort.clock;

public interface SequencedEvent {
	public void onStart();
	public void onLoop();
	public void onKill();
}
