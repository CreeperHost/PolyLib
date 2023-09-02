package net.creeperhost.polylib.helpers;

/**
 * Created by brandon3055 on 02/09/2023
 */
public class MathUtil {

    public static int clamp(int value, int min, int max) {
        return value > max ? max : value < min ? min :value;
    }

    public static long clamp(long value, long min, long max) {
        return value > max ? max : value < min ? min :value;
    }

    public static float clamp(float value, float min, float max) {
        return value > max ? max : value < min ? min :value;
    }

    public static double clamp(double value, double min, double max) {
        return value > max ? max : value < min ? min :value;
    }

}
