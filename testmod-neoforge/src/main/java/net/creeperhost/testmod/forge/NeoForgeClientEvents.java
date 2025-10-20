package net.creeperhost.testmod.forge;

import net.creeperhost.polylib.client.modulargui.ModularGuiScreen;
import net.creeperhost.testmod.client.gui.ModularGuiTest;
import net.creeperhost.testmod.client.gui.TestModTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterTextureAtlasesEvent;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;
import net.neoforged.neoforge.common.NeoForge;

public class NeoForgeClientEvents
{
    public static void init(IEventBus iEventBus) {
        NeoForge.EVENT_BUS.addListener(NeoForgeClientEvents::registerClientCommands);
        iEventBus.addListener(NeoForgeClientEvents::atlasStitched);
        iEventBus.addListener(NeoForgeClientEvents::registerTextureAtlas);
    }

    private static void registerClientCommands(RegisterClientCommandsEvent event) {
        var testGui = Commands.literal("modular_gui_test").executes(context -> {
            Minecraft.getInstance().setScreen(new ModularGuiScreen(new ModularGuiTest()));
            return 0;
        });
        event.getDispatcher().register(testGui);
    }

    private static void registerTextureAtlas(RegisterTextureAtlasesEvent event) {
        AtlasManager.AtlasConfig config = new AtlasManager.AtlasConfig(TestModTextures.TEXTURE_ID, TestModTextures.DEFINITION_LOCATION, false);
        event.register(config);
    }

    private static void atlasStitched(TextureAtlasStitchedEvent event) {
        if (event.getAtlas().location().equals(TestModTextures.TEXTURE_ID)) {
            TestModTextures.setAtlas(event.getAtlas());
        }
    }
}
