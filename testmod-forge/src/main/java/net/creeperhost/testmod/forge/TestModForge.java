package net.creeperhost.testmod.forge;

import dev.architectury.platform.forge.EventBuses;
import net.creeperhost.testmod.TestMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TestMod.MOD_ID)
public class TestModForge
{
    public TestModForge()
    {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(TestMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        TestMod.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ForgeClientEvents::init);
    }
}
