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

    public static void init() {
        //TODO
//        ClientGuiEvent.INIT_POST.register(ModularGuiInjector::initPost);
//        ClientGuiEvent.RENDER_POST.register(ModularGuiInjector::renderPost);
//        ClientScreenInputEvent.KEY_PRESSED_PRE.register(ModularGuiInjector::keyPressed);
//        ClientScreenInputEvent.KEY_RELEASED_PRE.register(ModularGuiInjector::keyReleased);
//        ClientScreenInputEvent.CHAR_TYPED_PRE.register(ModularGuiInjector::charTyped);
//        ClientScreenInputEvent.MOUSE_SCROLLED_PRE.register(ModularGuiInjector::mouseScrolled);
//        ClientScreenInputEvent.MOUSE_RELEASED_PRE.register(ModularGuiInjector::mouseReleased);
//        ClientScreenInputEvent.MOUSE_CLICKED_PRE.register(ModularGuiInjector::mouseClicked);
//        ClientTickEvent.CLIENT_POST.register(ModularGuiInjector::tick);
    }

    private static void initPost(Screen screen) {
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

    private static void renderPost(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (activeGui == null) return;
        GuiRender render = new GuiRender(graphics);
//        if (screen instanceof AbstractContainerScreen<?>) {
//            render.pose().translate(0, 0, 275); //Ensure we render on top of inventory stacks.
//        }
        activeGui.render(render, partialTick);
        activeGui.renderOverlay(render, partialTick);
    }

    //TODO
//    private static EventResult keyPressed(Minecraft client, Screen screen, KeyEvent event) {
//        if (activeGui == null) return EventResult.pass();
//        return activeGui.keyPressed(event) ? EventResult.interruptFalse() : EventResult.pass();
//    }
//
//    private static EventResult keyReleased(Minecraft client, Screen screen, KeyEvent event) {
//        if (activeGui == null) return EventResult.pass();
//        return activeGui.keyReleased(event) ? EventResult.interruptFalse() : EventResult.pass();
//    }
//
//    private static EventResult charTyped(Minecraft client, Screen screen, CharacterEvent event) {
//        if (activeGui == null) return EventResult.pass();
//        return activeGui.charTyped(event) ? EventResult.interruptFalse() : EventResult.pass();
//    }
//
//    private static EventResult mouseScrolled(Minecraft client, Screen screen, double mouseX, double mouseY, double amountX, double amountY) {
//        if (activeGui == null) return EventResult.pass();
//        return activeGui.mouseScrolled(mouseX, mouseY, amountX, amountY) ? EventResult.interruptFalse() : EventResult.pass();
//    }
//
//    private static EventResult mouseReleased(Minecraft client, Screen screen, MouseButtonEvent event) {
//        if (activeGui == null) return EventResult.pass();
//        return activeGui.mouseReleased(event) ? EventResult.interruptFalse() : EventResult.pass();
//    }
//
//    private static EventResult mouseClicked(Minecraft client, Screen screen, MouseButtonEvent event, boolean bl) {
//        if (activeGui == null) return EventResult.pass();
//        return activeGui.mouseClicked(event, bl) ? EventResult.interruptFalse() : EventResult.pass();
//    }

    private static void tick(Minecraft instance) {
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
