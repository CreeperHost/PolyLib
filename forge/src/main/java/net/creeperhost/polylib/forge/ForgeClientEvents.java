package net.creeperhost.polylib.forge;

import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PolyLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents
{
    @SuppressWarnings("removal")
    @SubscribeEvent
    static void renderWorldLastEvent(RenderLevelLastEvent event)
    {
        ClientRenderEvents.LAST.invoker().onDifficultyChanged(event.getPoseStack());
    }
}
