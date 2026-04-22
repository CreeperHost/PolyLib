package net.creeperhost.polylib;

import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class NeoForgeClientEvents
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void eventInitScreenEvent(ScreenEvent.Init.Post event)
    {
        ModularGuiInjector.initPost(event.getScreen());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void event(ClientTickEvent.Post event)
    {
        ModularGuiInjector.tick(Minecraft.getInstance());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void eventGuiRenderPost(ScreenEvent.Render.Post event)
    {
        ModularGuiInjector.renderPost(event.getScreen(), event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

}
