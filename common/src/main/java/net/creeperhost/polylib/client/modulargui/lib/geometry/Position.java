package net.creeperhost.polylib.client.modulargui.lib.geometry;

import java.util.function.Supplier;

/**
 * Created by brandon3055 on 24/08/2023
 */
public interface Position {

    double x();

    double y();

    default Position offset(double x, double y) {
        return create(x() + x, y() + y);
    }

    static Position create(double x, double y) {
        return new Immutable(x, y);
    }

    /**
     * Creates a new position, bound to the specified parent's position.
     * */
    static Position create(GuiParent<?> parent) {
        return new Bound(parent);
    }

    record Immutable(double xPos, double yPos) implements Position {
        @Override
        public double x() {
            return xPos;
        }

        @Override
        public double y() {
            return yPos;
        }
    }

    record Bound(GuiParent<?> parent) implements Position {
        @Override
        public double x() {
            return parent.xMin();
        }

        @Override
        public double y() {
            return parent.yMin();
        }
    }

    record Dynamic(Supplier<Double> getX, Supplier<Double> getY) implements Position {
        @Override
        public double x() {
            return getX.get();
        }

        @Override
        public double y() {
            return getY.get();
        }
    }

}
