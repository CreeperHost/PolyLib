package net.creeperhost.testmod.forge;

import net.creeperhost.testmod.TestMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;


@Mod(TestMod.MOD_ID)
public class TestModNeoForge
{
    public TestModNeoForge(IEventBus bus)
    {
        TestMod.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> NeoForgeClientEvents::init);
    }
}
