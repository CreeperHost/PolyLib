package net.creeperhost.polylib.init;

import net.creeperhost.polylib.client.config.ConfigPanelRegistry;
import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.creeperhost.polylib.client.screen.chunkmap.PolyChunkMapKeys;
import net.creeperhost.polylib.event.events.client.PolyClientTickEvents;
import net.creeperhost.polylib.event.events.client.PolyScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;

public class InternalEventListenerClient
{
    public static void init()
    {
        PolyScreenEvents.SCREEN_OPENED.register((mc, screen, width, height) -> ModularGuiInjector.initPost(screen));
        PolyClientTickEvents.CLIENT_TICK_END.register(client -> {
            ModularGuiInjector.tick(client);
            ConfigPanelRegistry.tickKeybinds();
            PolyChunkMapKeys.tick();
        });
        PolyScreenEvents.SCREEN_RENDER_POST.register(ModularGuiInjector::renderPost);
        PolyScreenEvents.SCREEN_KEY_PRESS_AFTER.register((screen, keyCode, scanCode, modifiers) -> {
            ModularGuiInjector.keyPressed(Minecraft.getInstance(), screen, new KeyEvent(keyCode, scanCode, modifiers));
        });
        PolyScreenEvents.SCREEN_KEY_RELEASE_AFTER.register((screen, keyCode, scanCode, modifiers) -> {
            ModularGuiInjector.keyReleased(Minecraft.getInstance(), screen, new KeyEvent(keyCode, scanCode, modifiers));
        });
        PolyScreenEvents.SCREEN_CHAR_TYPED_AFTER.register((screen, codePoint, modifiers) -> {
            ModularGuiInjector.charTyped(Minecraft.getInstance(), screen, new CharacterEvent(codePoint));
        });
        PolyScreenEvents.SCREEN_MOUSE_SCROLL_AFTER.register((screen, mouseX, mouseY, scrollX, scrollY) -> {
            ModularGuiInjector.mouseScrolled(Minecraft.getInstance(), screen, mouseX,
                    mouseY, scrollX, scrollY);
        });
        PolyScreenEvents.SCREEN_MOUSE_CLICK_AFTER.register((screen, mouseX, mouseY, button) -> {
            ModularGuiInjector.mouseClicked(Minecraft.getInstance(), screen, new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)), false);
        });
        PolyScreenEvents.SCREEN_MOUSE_RELEASE_AFTER.register((screen, mouseX, mouseY, button) -> {
            ModularGuiInjector.mouseReleased(Minecraft.getInstance(), screen, new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)));
        });
    }
}
