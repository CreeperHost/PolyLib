package net.creeperhost.polylib.client.modulargui.lib;

import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An interface for getting and / or controlling an elements scroll state.
 * Also used by slider elements.
 * <p>
 * Created by brandon3055 on 01/09/2023
 */
public interface ScrollState {

    /**
     * @return the current scroll position. (Between 0 and 1)
     */
    double getPos();

    /**
     * @param pos Set the current scroll position (Between 0 and 1)
     */
    void setPos(double pos);

    /**
     * This is primarily for things like scroll windows.
     * The value returned represents the viewable content area divided by the total content area.
     * <p>
     * This is also what control scroll speed via scroll wheel. and is used to calibrate the "middle click drag" scrolling.
     *
     * @return scroll ration, viewable content size / total content size (Range 0 to 1)
     */
    double scrollRatio();

    /**
     * Creates a basic scroll state which stores its scroll position internally.
     * Useful for things like simple slide control elements.
     */
    static ScrollState create(double ratio) {
        return new ScrollState() {
            double pos = 0;
            @Override
            public double getPos() {
                return pos;
            }

            @Override
            public void setPos(double pos) {
                this.pos = pos;
            }

            @Override
            public double scrollRatio() {
                return ratio;
            }
        };
    }

    static ScrollState create(Supplier<Double> getPos, Consumer<Double> setPos, Supplier<Double> getRatio) {
        return new ScrollState() {
            @Override
            public double getPos() {
                return getPos.get();
            }

            @Override
            public void setPos(double pos) {
                setPos.accept(pos);
            }

            @Override
            public double scrollRatio() {
                return getRatio.get();
            }
        };
    }
}
