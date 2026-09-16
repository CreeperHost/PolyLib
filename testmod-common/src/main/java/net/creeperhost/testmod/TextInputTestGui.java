package net.creeperhost.testmod;

import net.creeperhost.polylib.client.modulargui.ModularGui;
import net.creeperhost.polylib.client.modulargui.elements.GuiButton;
import net.creeperhost.polylib.client.modulargui.elements.GuiElement;
import net.creeperhost.polylib.client.modulargui.elements.GuiTextField;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.GuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.TextState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.sdl.SDLKeyboard;

/** Client regression checks using the real SDL window, followed by a manual typing screen. */
public class TextInputTestGui implements GuiProvider {
    private boolean editable = true;
    private boolean tested;

    @Override
    public void buildGui(ModularGui gui) {
        gui.initStandardGui(280, 160);
        gui.setGuiTitle(Component.literal("PolyLib text input regression"));
        GuiElement<?> root = gui.getRoot();
        GuiTextField first = field(root, "first", 15);
        GuiTextField second = field(root, "second", 50);
        second.setEditable(() -> editable);
        GuiButton toggle = GuiButton.vanilla(root, Component.literal("Toggle second editable"))
                .onClick(() -> {
                    editable = !editable;
                    first.setFocus(false);
                    second.setFocus(true);
                });
        Constraints.size(toggle, 250, 20);
        Constraints.placeInside(toggle, root, Constraints.LayoutPos.TOP_LEFT, 15, 90);
        GuiButton close = GuiButton.vanilla(root, Component.literal("Close and check SDL stopped"))
                .onClick(() -> Minecraft.getInstance().gui.setScreen(new TitleScreen()));
        Constraints.size(close, 250, 20);
        Constraints.placeInside(close, root, Constraints.LayoutPos.TOP_LEFT, 15, 120);
        gui.onClose(() -> {
            expectInput(false, "screen closure");
            if (first.isFocused() || second.isFocused()) throw new AssertionError("Focus survived closure");
        });
        gui.onTick(() -> {
            if (tested) return;
            tested = true;
            first.setFocus(true);
            expectInput(true, "focus gain");
            second.setFocus(true);
            first.setFocus(false);
            expectInput(true, "old field loses focus after new field gains it");
            second.setFocus(false);
            expectInput(false, "focus loss");
            second.setEditable(false);
            second.setFocus(true);
            expectInput(false, "focus read-only field");
            second.setEditable(true);
            expectInput(true, "enable editing while focused");
            second.setEditable(false);
            expectInput(false, "disable editing while focused");
            second.setEditable(() -> editable);
            expectInput(true, "editable supplier");
            editable = false;
            second.tick(0, 0);
            expectInput(false, "supplier becomes read-only");
            editable = true;
            second.tick(0, 0);
            expectInput(true, "supplier becomes editable");
            second.setFocus(false);
            first.setFocus(true);
            TestModCommon.LOGGER.info("[TEXT-INPUT-TEST] Lifecycle checks passed; type into both fields, then close.");
        });
    }

    private GuiTextField field(GuiElement<?> root, String name, int y) {
        var field = GuiTextField.create(root, 0xFF000000, 0xFFFFFFFF, 0xFFE0E0E0);
        Constraints.size(field.container, 250, 22);
        Constraints.placeInside(field.container, root, Constraints.LayoutPos.TOP_LEFT, 15, y);
        field.primary.setMaxLength(100).setSuggestion(Component.literal("Type into " + name + " field"));
        field.primary.setTextState(TextState.simpleState("", text ->
                TestModCommon.LOGGER.info("[TEXT-INPUT-TEST] {} value: {}", name, text)));
        return field.primary;
    }

    private static void expectInput(boolean expected, String step) {
        boolean actual = SDLKeyboard.SDL_TextInputActive(Minecraft.getInstance().getWindow().handle());
        if (actual != expected) throw new AssertionError(step + ": expected SDL input=" + expected + ", got " + actual);
        TestModCommon.LOGGER.info("[TEXT-INPUT-TEST] PASS {}", step);
    }
}
