package net.creeperhost.polylib.forge;

import dev.architectury.platform.forge.EventBuses;
import net.creeperhost.polylib.PolyLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PolyLib.MOD_ID)
public class PolyLibForge
{
    public PolyLibForge()
    {
        // Submit our event bus to let architectury register our content on the right time
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(PolyLib.MOD_ID, eventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ForgeClientEvents::init);

        PolyLib.init();
    }
}
