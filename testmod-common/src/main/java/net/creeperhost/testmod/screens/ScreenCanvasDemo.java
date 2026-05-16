package net.creeperhost.testmod.screens;

import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.creeperhost.polylib.client.modulargui.elements.*;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.GuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.minecraft.network.chat.Component;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;
import static net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam.*;

/**
 * Demonstrates {@link GuiPannableCanvas} with two draggable {@link GuiWindow} instances.
 *
 * <p>Open in-game: KP_3 (numpad 3) — registered in {@link net.creeperhost.testmod.init.TestClientEvents}.
 *
 * <p>Expected:
 * <ul>
 *   <li>Middle-mouse drag pans the canvas</li>
 *   <li>Scroll wheel zooms toward the cursor</li>
 *   <li>Each GuiWindow title bar can be dragged to reposition it within canvas space</li>
 * </ul>
 */
public class ScreenCanvasDemo extends ModularGuiScreen
{
    public ScreenCanvasDemo()
    {
        super(new Provider());
    }

    private static class Provider implements GuiProvider
    {
        @Override
        public void buildGui(ModularGui gui)
        {
            gui.initFullscreenGui();
            gui.setGuiTitle(Component.literal("Canvas Demo"));

            GuiElement<?> root = gui.getRoot();

            // Outer border / label
            new GuiText(root, Component.literal(
                    "Canvas Demo — middle-drag to pan, scroll to zoom, drag window title bars"))
                    .setShadow(true).setTextColour(0xFFFFFFFF)
                    .constrain(TOP,    literal(4))
                    .constrain(HEIGHT, literal(8))
                    .constrain(LEFT,   literal(4))
                    .constrain(RIGHT,  relative(root.get(RIGHT), -4));

            // The pannable canvas fills the whole screen below the label
            GuiPannableCanvas<?> canvas = new GuiPannableCanvas<>(root);
            canvas
                    .constrain(TOP,    literal(16))
                    .constrain(BOTTOM, match(root.get(BOTTOM)))
                    .constrain(LEFT,   match(root.get(LEFT)))
                    .constrain(RIGHT,  match(root.get(RIGHT)));

            GuiRectangle canvasBg = new GuiRectangle(canvas).fill(0xFF111111);
            Constraints.bind(canvasBg, canvas);

            // ── Window A ─────────────────────────────────────────────────────
            GuiWindow winA = new GuiWindow(canvas);
            winA.setTitle(Component.literal("Window A"));
            winA
                    .constrain(TOP,    literal(40))
                    .constrain(LEFT,   literal(60))
                    .constrain(WIDTH,  literal(140))
                    .constrain(HEIGHT, literal(80));

            new GuiText(winA.getContentElement(),
                    Component.literal("Drag the title bar to move me."))
                    .setShadow(false).setTextColour(0xFFCCCCCC)
                    .constrain(TOP,    relative(winA.getContentElement().get(TOP), 8))
                    .constrain(HEIGHT, literal(8))
                    .constrain(LEFT,   relative(winA.getContentElement().get(LEFT), 4))
                    .constrain(RIGHT,  relative(winA.getContentElement().get(RIGHT), -4));

            // ── Window B ─────────────────────────────────────────────────────
            GuiWindow winB = new GuiWindow(canvas);
            winB.setTitle(Component.literal("Window B"));
            winB
                    .constrain(TOP,    literal(160))
                    .constrain(LEFT,   literal(240))
                    .constrain(WIDTH,  literal(140))
                    .constrain(HEIGHT, literal(80));

            new GuiText(winB.getContentElement(),
                    Component.literal("I'm another window!"))
                    .setShadow(false).setTextColour(0xFFCCCCCC)
                    .constrain(TOP,    relative(winB.getContentElement().get(TOP), 8))
                    .constrain(HEIGHT, literal(8))
                    .constrain(LEFT,   relative(winB.getContentElement().get(LEFT), 4))
                    .constrain(RIGHT,  relative(winB.getContentElement().get(RIGHT), -4));
        }
    }
}
