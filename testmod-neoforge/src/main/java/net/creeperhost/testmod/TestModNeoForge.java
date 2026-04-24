package net.creeperhost.testmod;

import net.creeperhost.polylib.config.NeoForgeConfigHelper;
import net.creeperhost.testmod.datagen.TestModNeoLangProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(TestModCommon.MOD_ID)
public class TestModNeoForge
{
    public TestModNeoForge(ModContainer container, IEventBus bus) {
        TestModCommon.LOGGER.info("[TESTMOD-DEBUG] constructor called, dist={}", FMLLoader.getCurrent().getDist());
        TestModCommon.init();

        if (FMLLoader.getCurrent().getDist().isClient()) {
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
        }

        bus.addListener(TestModNeoForge::onGatherData);
    }

    private static void onGatherData(GatherDataEvent.Client event) {
        TestModCommon.LOGGER.info("[TESTMOD-DEBUG] onGatherData fired, contributions={}", net.creeperhost.polylib.data.lang.PolyLangContributions.getAll().size());
        event.addProvider(new TestModNeoLangProvider(event.getGenerator().getPackOutput()));
    }
}
