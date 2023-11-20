package net.creeperhost.polylib.client;

import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.lib.Assembly;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Axis;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * This class contains a common set of colours and gui components for use in most CreeperHost gui's
 * The goal of this class is not to start out with everything we will ever need, but rather to provide a place to put new bits and pieces as we find need for them.
 * <p>
 * Created by brandon3055 on 17/09/2023
 */
public class PolyPalette {

    //Not sure how to make this work well with dynamic textures... Maybe we just wont do textured elements here.
//    public static GuiElement<?> background(GuiElement<?> parent) {
//    }

    public static GuiButton button(GuiElement<?> parent, Component label) {
        return button(parent, () -> label);
    }

    public static GuiButton button(GuiElement<?> parent, @Nullable Supplier<Component> label) {
        return GuiButton.vanillaAnimated(parent, label);
    }

//    /**
//     * Typically a red button, Use for things like "Delete" buttons.
//     */
//    public static GuiButton buttonCaution(GuiElement<?> parent, Component label) {
//        return buttonCaution(parent, () -> label);
//    }
//
//    /**
//     * Typically a red button, Use for things like "Delete" buttons.
//     */
//    public static GuiButton buttonCaution(GuiElement<?> parent, @Nullable Supplier<Component> label) {
//        //Have not needed this yet, Not sure how I want to implement it. Feel free to add something.
//    }

    public static Assembly<? extends GuiElement<?>, GuiSlider> scrollBar(GuiElement<?> parent, Axis axis) {
        return GuiSlider.vanillaScrollBar(parent, axis);
    }

    /**
     * Colours and components for flat / un-textured guis. Typically, used for fullscreen guis
     */
    public static class Flat {

        /**
         * Primary Gui Background
         */
        public static GuiElement<?> background(GuiParent<?> parent) {
            return new GuiRectangle(parent).fill(0xF0000000);
        }

        /**
         * Optional background used for certain content within the gui
         */
        public static GuiElement<?> contentArea(GuiElement<?> parent) {
            return new GuiRectangle(parent).fill(0x80202020);
        }

        public static GuiButton button(GuiElement<?> parent, Component label) {
            return button(parent, () -> label);
        }

        public static GuiButton button(GuiElement<?> parent, @Nullable Supplier<Component> label) {
            GuiButton button = new GuiButton(parent);
            GuiRectangle background = new GuiRectangle(button)
                    .fill(() -> button.isDisabled() ? 0x88202020 : (button.isMouseOver() || button.toggleState() || button.isPressed() ? 0xFF909090 : 0xFF505050));
            Constraints.bind(background, button);

            if (label != null) {
                GuiText text = new GuiText(button, label);
                button.setLabel(text);
                Constraints.bind(text, button, 0, 2, 0, 2);
            }
            return button;
        }

        /**
         * Typically a red button, Use for things like "Delete" buttons.
         */
        public static GuiButton buttonCaution(GuiElement<?> parent, Component label) {
            return buttonCaution(parent, () -> label);
        }

        /**
         * Typically a red button, Use for things like "Delete" buttons.
         */
        public static GuiButton buttonCaution(GuiElement<?> parent, @Nullable Supplier<Component> label) {
            GuiButton button = new GuiButton(parent);
            GuiRectangle background = new GuiRectangle(button)
                    .fill(() -> button.isDisabled() ? 0x88202020 : (button.isMouseOver() || button.toggleState() || button.isPressed() ? 0xFFAA4444 : 0xFF881111));
            Constraints.bind(background, button);

            if (label != null) {
                GuiText text = new GuiText(button, label);
                button.setLabel(text);
                Constraints.bind(text, button, 0, 2, 0, 2);
            }
            return button;
        }

        /**
         * Typically a green / friendly colour button, Use for things like "Accept" / "OK" buttons.
         */
        public static GuiButton buttonPrimary(GuiElement<?> parent, Component label) {
            return buttonPrimary(parent, () -> label);
        }

        /**
         * Typically a green / friendly colour button, Use for things like "Accept" / "OK" buttons.
         */
        public static GuiButton buttonPrimary(GuiElement<?> parent, @Nullable Supplier<Component> label) {
            GuiButton button = new GuiButton(parent);
            GuiRectangle background = new GuiRectangle(button)
                    .fill(() -> button.isDisabled() ? 0x88202020 : (button.isMouseOver() || button.toggleState() || button.isPressed() ? 0xFF44AA44 : 0xFF118811));
            Constraints.bind(background, button);

            if (label != null) {
                GuiText text = new GuiText(button, label);
                button.setLabel(text);
                Constraints.bind(text, button, 0, 2, 0, 2);
            }
            return button;
        }

        public static Assembly<? extends GuiElement<?>, GuiSlider> scrollBar(GuiElement<?> parent, Axis axis) {
            GuiRectangle background = new GuiRectangle(parent);
            GuiSlider slider = new GuiSlider(background, axis);
            Constraints.bind(slider, background);
            background.fill(() -> background.isMouseOver() || slider.isDragging() ? 0x80505050 : 0x20505050);
            GuiRectangle handle = new GuiRectangle(slider);
            handle.fill(() -> handle.isMouseOver() || slider.isDragging() ? 0xFFFFFFFF : 0x88FFFFFF);
            slider.installSlider(handle)
                    .bindSliderLength()
                    .bindSliderWidth();
            return new Assembly<>(background, slider).addParts(handle);
        }
    }
}