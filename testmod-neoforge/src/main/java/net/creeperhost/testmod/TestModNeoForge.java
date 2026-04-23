package net.creeperhost.testmod;

import net.creeperhost.polylib.neoforge.registry.NeoPolyRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("testmod")
public class TestModNeoForge
{
    public TestModNeoForge(IEventBus bus) {
        TestModCommon.init();
        NeoPolyRegistry.registerToBus(bus);
    }
}
