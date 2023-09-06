package net.creeperhost.polylib.client.modulargui.lib.geometry;

/**
 * Created by brandon3055 on 02/09/2023
 */
public enum Axis {
    X,
    Y;

    public Axis opposite() {
        return this == X ? Y : X;
    }
}
