package io.github.iwilkey.teknetcore.utils;

import java.util.Random;

public class MathUtilities {
	public static int randomIntBetween(int low, int high) {
		Random r = new Random();
		return r.nextInt(high - low) + low;
	}
	public static double randomDoubleBetween(double low, double high) {
		Random r = new Random();
		return low + r.nextDouble() * (high - low);
	}
	public static boolean locationInEstateRegion(long ex, long ez, long px, long pz, float size) {
		return (px >= ex - size && px <= ex + size) && (pz >= ez - size && pz <= ez + size);
	}
}
