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

    /**
     * Rounds the number of decimal places based on the given multiplier.<br>
     * e.g.<br>
     * Input: 17.5245743<br>
     * multiplier: 1000<br>
     * Output: 17.534<br>
     * multiplier: 10<br>
     * Output 17.5<br><br>
     *
     * @param number     The input value.
     * @param multiplier The multiplier.
     * @return The input rounded to a number of decimal places based on the multiplier.
     */
    public static double round(double number, double multiplier) {
        return Math.round(number * multiplier) / multiplier;
    }
}
