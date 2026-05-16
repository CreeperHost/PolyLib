package net.creeperhost.polylib;

public class PolyFeatures {
    private static volatile boolean chunkMapEnabled = false;

    public static boolean isChunkMapEnabled() {
        return chunkMapEnabled;
    }

    public static void enableChunkMap() {
        chunkMapEnabled = true;
    }
}
