package net.creeperhost.polylib.client.modulargui.lib;

import net.creeperhost.polylib.client.modulargui.lib.geometry.Borders;
import net.creeperhost.polylib.client.modulargui.lib.geometry.ConstrainedGeometry;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

/**
 * This class contains a bunch of static helper methods that can be used to quickly apply common constraints.
 * The plan is to keep adding common constraints to this class as they pop up.
 * <p>
 * Created by brandon3055 on 28/08/2023
 */
public class Constraints {

    /**
     * Bind an elements geometry to a reference element.
     *
     * @param element   The element to be bound.
     * @param reference The element to be bound to.
     */
    public static void bindTo(ConstrainedGeometry<?> element, ConstrainedGeometry<?> reference) {
        bindTo(element, reference, 0.0);
    }

    /**
     * Bind an elements geometry to a reference element with border offsets. (Border offsets may be positive or negative)
     *
     * @param element   The element to be bound.
     * @param reference The element to be bound to.
     */
    public static void bindTo(ConstrainedGeometry<?> element, ConstrainedGeometry<?> reference, double borders) {
        bindTo(element, reference, borders, borders, borders, borders);
    }

    /**
     * Bind an elements geometry to a reference element with border offsets. (Border offsets may be positive or negative)
     * 
     * @param element   The element to be bound.
     * @param reference The element to be bound to.
     */
    public static void bindTo(ConstrainedGeometry<?> element, ConstrainedGeometry<?> reference, double top, double left, double bottom, double right) {
        element.constrain(TOP, Constraint.relative(reference.get(TOP), top));
        element.constrain(LEFT, Constraint.relative(reference.get(LEFT), left));
        element.constrain(BOTTOM, Constraint.relative(reference.get(BOTTOM), -bottom));
        element.constrain(RIGHT, Constraint.relative(reference.get(RIGHT), -right));
    }

    /**
     * Bind an elements geometry to a reference element with border offsets. (Border offsets may be positive or negative)
     * The border offsets are dynamic, meaning if the values stored in the {@link Borders} object are updated, this binding will reflect those changes automatically.
     *
     * @param element   The element to be bound.
     * @param reference The element to be bound to.
     * @param borders   Border offsets.
     */
    public static void bindTo(ConstrainedGeometry<?> element, ConstrainedGeometry<?> reference, Borders borders) {
        element.constrain(TOP, Constraint.relative(reference.get(TOP), borders::top));
        element.constrain(LEFT, Constraint.relative(reference.get(LEFT), borders::left));
        element.constrain(BOTTOM, Constraint.relative(reference.get(BOTTOM), () -> -borders.bottom()));
        element.constrain(RIGHT, Constraint.relative(reference.get(RIGHT), () -> -borders.right()));
    }



    //TODO, Full of helpers for Gui v2 style constraints (No need to pass in parent! Yay!)
    // Then may be able to remove some legacy helpers from ConstrainedGeometry
}
