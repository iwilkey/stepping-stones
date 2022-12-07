package io.github.iwilkey.teknetcore.utils;

import java.util.ArrayList;

import io.github.iwilkey.teknetcore.TeknetCore;

public class SequenceUtilities {
	
	public static interface SequenceFunction {
		public void onIteration(Object... objects);
	}
	
	public static class Sequence {
		public SequenceFunction func;
		public int times;
		public int ticksSince;
		public float secondDelay, secondsSince;
		public Sequence(int times, float secondDelay, SequenceFunction function) {
			this.func = function;
			this.secondDelay = secondDelay;
			this.times = times;
			ticksSince = 0;
			secondsSince = secondDelay;
		}
	}
	
	private static ArrayList<Sequence> SEQUENCE_STATE,
		toRemove;
	
	public SequenceUtilities() {
		SEQUENCE_STATE = new ArrayList<>();
		toRemove = new ArrayList<>();
	}

	public static void tick() {
		toRemove.clear();
		for(Sequence s : SEQUENCE_STATE) {
			if(s.times <= 0) {
				toRemove.add(s);
				continue;
			}
			if(s.secondsSince >= s.secondDelay) {
				s.func.onIteration();
				s.times--;
				s.secondsSince = 0;
				s.ticksSince = 0;
			}
			s.ticksSince++;
			s.secondsSince = (1.0f / TeknetCore.SERVER_TPS) * s.ticksSince;
		}
		SEQUENCE_STATE.removeAll(toRemove);
	}
	
	public static void startSequence(Sequence s) {
		SEQUENCE_STATE.add(s);
	}
	
}
