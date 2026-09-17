package net.creeperhost.testmod;

import net.creeperhost.polylib.config.NeoForgeConfigHelper;
import net.creeperhost.polylib.config.ConfigBuilder;
import net.creeperhost.testmod.datagen.TestModNeoLangProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class TestModNeoForgeClient
{
    public static void init(ModContainer container, IEventBus bus)
    {
        NeoForgeConfigHelper.register(container, new ConfigBuilder("TestMod", EditorTestConfig.class));

        bus.addListener(TestModNeoForgeClient::onGatherData);
    }

    private static void onGatherData(GatherDataEvent.Client event) {
        TestModCommon.LOGGER.info("[TESTMOD-DEBUG] onGatherData fired, contributions={}", net.creeperhost.polylib.data.lang.PolyLangContributions.getAll().size());
        event.addProvider(new TestModNeoLangProvider(event.getGenerator().getPackOutput()));
    }
}
