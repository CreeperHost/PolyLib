package net.creeperhost.testmod.forge;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.creeperhost.testmod.TestMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;


@Mod(TestMod.MOD_ID)
public class TestModNeoForge
{
    public TestModNeoForge(IEventBus bus)
    {
        TestMod.init();

        if(Platform.getEnvironment() == Env.CLIENT)
        {
            NeoForgeClientEvents.init(bus);
        }
    }
}
