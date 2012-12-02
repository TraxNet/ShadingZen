package org.traxnet.shadingzen.core;

public abstract class Task {
	public abstract void run(final long deltatime);
	public abstract void executeSync();
}
