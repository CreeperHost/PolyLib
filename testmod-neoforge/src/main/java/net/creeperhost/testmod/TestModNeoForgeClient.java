package net.creeperhost.testmod;

import net.creeperhost.polylib.config.NeoForgeConfigHelper;
import net.creeperhost.testmod.datagen.TestModNeoLangProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class TestModNeoForgeClient
{
    public static void init(ModContainer container, IEventBus bus)
    {
        NeoForgeConfigHelper.register(
                container,
                parent -> new ConfirmScreen(
                        confirmed -> Minecraft.getInstance().setScreen(parent),
                        Component.literal("Testmod"),
                        Component.literal("No config screen registered yet."),
                        Component.literal("OK"),
                        Component.empty()
                )
        );

        bus.addListener(TestModNeoForgeClient::onGatherData);
    }

    private static void onGatherData(GatherDataEvent.Client event) {
        TestModCommon.LOGGER.info("[TESTMOD-DEBUG] onGatherData fired, contributions={}", net.creeperhost.polylib.data.lang.PolyLangContributions.getAll().size());
        event.addProvider(new TestModNeoLangProvider(event.getGenerator().getPackOutput()));
    }
}
