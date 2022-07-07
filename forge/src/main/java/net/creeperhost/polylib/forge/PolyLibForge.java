package net.creeperhost.polylib.forge;

import dev.architectury.platform.forge.EventBuses;
import net.creeperhost.polylib.PolyLib;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PolyLib.MOD_ID)
public class PolyLibForge
{
    public PolyLibForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(PolyLib.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        PolyLib.init();
    }
}
