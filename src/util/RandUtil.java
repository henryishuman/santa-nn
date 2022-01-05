package util;

import java.util.Random;

public class RandUtil {
	private static Random rand = new Random();
	
	public static int getInt(int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}
	
	public static double getDouble(int min, int max) {
		return rand.nextDouble() * (max - min) + min;
	}
	
	public static boolean coinFlip() {
		return rand.nextDouble() >= 0.5;
	}
	
	public static <T> T pickRandomOrNotNull(T o1, T o2) {
		if (o1 == null) return o2;
		if (o2 == null) return o1;
		if (RandUtil.coinFlip()) {
			return o1;
		} else {
			return o2;
		}
	}
	
	public static <T> T pickRandomFromArray(T[] array) {
		return array[getInt(0, array.length-1)];
	}
}
