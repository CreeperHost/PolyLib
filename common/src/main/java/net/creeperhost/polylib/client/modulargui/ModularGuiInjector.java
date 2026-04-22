package net.creeperhost.polylib.client.modulargui;

import net.creeperhost.polylib.client.modulargui.lib.GuiProvider;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class is designed to allow you to inject modular gui elements into / overtop any standard gui.
 * <p>
 * Created by brandon3055 on 30/03/2024
 */
public class ModularGuiInjector<T extends Screen> {

    private static Map<Predicate<Screen>, Function<? extends Screen, GuiProvider>> providerMap = new HashMap<>();
    private static ModularGui activeGui = null;
    private static double prevMouseX = 0;
    private static double prevMouseY = 0;

    public static void registerInjection(Predicate<Screen> screenPredicate, Function<Screen, GuiProvider> guiFunction) {
        providerMap.put(screenPredicate, guiFunction);
    }

    public static void initPost(Screen screen) {
        if (activeGui != null) activeGui = null;
        Predicate<Screen> key = providerMap.keySet()
                .stream()
                .filter(e -> e.test(screen))
                .findAny()
                .orElse(null);
        if (key == null) return;

        activeGui = new ModularGui(providerMap.get(key).apply(unsafeCast(screen)));
        activeGui.setScreen(screen);
        activeGui.onScreenInit(Minecraft.getInstance(), Minecraft.getInstance().font, screen.width, screen.height);
        prevMouseX = activeGui.computeMouseX();
        prevMouseY = activeGui.computeMouseY();
    }

    public static <T> T unsafeCast(@Nullable Object object) {
        return (T) object;
    }

    public static void renderPost(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (activeGui == null) return;
        GuiRender render = new GuiRender(graphics);
//        if (screen instanceof AbstractContainerScreen<?>) {
//            render.pose().translate(0, 0, 275); //Ensure we render on top of inventory stacks.
//        }
        activeGui.render(render, partialTick);
        activeGui.renderOverlay(render, partialTick);
    }

    public static boolean keyPressed(Minecraft client, Screen screen, KeyEvent event) {
        if (activeGui == null) return false;
        return activeGui.keyPressed(event) ? true : false;
    }

    public static boolean keyReleased(Minecraft client, Screen screen, KeyEvent event) {
        if (activeGui == null) return false;
        return activeGui.keyReleased(event) ? true : false;
    }

    public static boolean charTyped(Minecraft client, Screen screen, CharacterEvent event) {
        if (activeGui == null) return false;
        return activeGui.charTyped(event) ? true : false;
    }

    public static boolean mouseScrolled(Minecraft client, Screen screen, double mouseX, double mouseY, double amountX, double amountY) {
        if (activeGui == null) return false;
        return activeGui.mouseScrolled(mouseX, mouseY, amountX, amountY) ? true : false;
    }

    public static boolean mouseReleased(Minecraft client, Screen screen, MouseButtonEvent event) {
        if (activeGui == null) return false;
        return activeGui.mouseReleased(event) ? true : false;
    }

    public static boolean mouseClicked(Minecraft client, Screen screen, MouseButtonEvent event, boolean bl) {
        if (activeGui == null) return false;
        return activeGui.mouseClicked(event, bl) ? true : false;
    }

    public static void tick(Minecraft instance) {
        if (activeGui == null) return;

        //Because apparently there is no mouse move event, only drag.
        double newX = activeGui.computeMouseX();
        double newY = activeGui.computeMouseY();
        if (newX != prevMouseX || newY != prevMouseY) {
            activeGui.mouseMoved(newX, newY);
            prevMouseX = newX;
            prevMouseY = newY;
        }
        activeGui.tick();
    }

    @Nullable
    public static ModularGui getActiveGui() {
        return activeGui;
    }
}
