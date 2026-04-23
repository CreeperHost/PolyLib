package net.creeperhost.testmod;

import net.creeperhost.polylib.neoforge.registry.NeoPolyRegistry;
import net.creeperhost.testmod.datagen.TestModNeoLangProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(TestModCommon.MOD_ID)
public class TestModNeoForge
{
    public TestModNeoForge(IEventBus bus) {
        TestModCommon.init();
        NeoPolyRegistry.registerToBus(bus);
        bus.addListener(TestModNeoForge::onGatherData);
    }

    private static void onGatherData(GatherDataEvent.Client event) {
        event.addProvider(new TestModNeoLangProvider(event.getGenerator().getPackOutput()));
    }
}