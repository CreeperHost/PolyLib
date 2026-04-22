package net.creeperhost.polylib.client.modulargui;

import net.creeperhost.polylib.client.modulargui.lib.GuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A simple ModularGui screen implementation.
 * This is simply a wrapper for a {@link ModularGui} that takes a {@link GuiProvider}
 * This should be suitable for most basic gui screens.
 * <p>
 * Created by brandon3055 on 19/08/2023
 */
public class ModularGuiScreen extends Screen {

    protected final ModularGui modularGui;

    public ModularGuiScreen(GuiProvider provider) {
        super(Component.empty());
        this.modularGui = new ModularGui(provider);
        this.modularGui.setScreen(this);
    }

    public ModularGuiScreen(GuiProvider builder, Screen parentScreen) {
        super(Component.empty());
        this.modularGui = new ModularGui(builder, parentScreen);
        this.modularGui.setScreen(this);
    }

    public ModularGui getModularGui() {
        return modularGui;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return modularGui.getGuiTitle();
    }

    @Override
    public boolean isPauseScreen() {
        return modularGui.isPauseScreen();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return modularGui.closeOnEscape();
    }

    @Override
    protected void init() {
        modularGui.onScreenInit(minecraft, font, width, height);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        modularGui.onScreenInit(minecraft, font, width, height);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        GuiRender render = new GuiRender(graphics);
        modularGui.render(render, partialTicks);
        modularGui.renderOverlay(render, partialTicks);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        if (modularGui.renderBackground()) {
            super.extractBackground(guiGraphics, i, j, f);
        }
    }

    @Override
    public void tick() {
        modularGui.tick();
    }

    @Override
    public void removed() {
        super.removed();
        modularGui.onGuiClose();
    }

    //=== Input Pass-though ===//

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        modularGui.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        return modularGui.mouseClicked(mouseButtonEvent, bl) || super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        return modularGui.mouseReleased(mouseButtonEvent) || super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return modularGui.mouseScrolled(mouseX, mouseY, scrollX, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        return modularGui.keyPressed(keyEvent) || super.keyPressed(keyEvent);
    }

    @Override
    public boolean keyReleased(KeyEvent keyEvent) {
        return modularGui.keyReleased(keyEvent) || super.keyReleased(keyEvent);
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        return modularGui.charTyped(characterEvent) || super.charTyped(characterEvent);
    }
}
