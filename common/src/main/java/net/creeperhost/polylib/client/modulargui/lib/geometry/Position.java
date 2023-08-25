package net.creeperhost.polylib.client.modulargui.lib.geometry;

/**
 * Created by brandon3055 on 24/08/2023
 */
public record Position(double x, double y) {

    public Position offset(double x, double y) {
        return create(this.x + x, this.y + y);
    }

    public static Position create(double x, double y) {
        return new Position(x, y);
    }

}
