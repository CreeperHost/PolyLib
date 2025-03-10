package net.creeperhost.testmod;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.platform.Platform;
import net.creeperhost.polylib.PolyLib;
import net.creeperhost.polylib.events.ClientRenderEvents;
import net.creeperhost.testmod.client.PlacementRenderer;
import net.creeperhost.testmod.client.gui.TestGui;
import net.creeperhost.testmod.init.TestBlocks;
import net.creeperhost.testmod.init.TestContainers;
import net.creeperhost.testmod.init.TestItems;
import net.creeperhost.testmod.init.TestScreens;
import net.fabricmc.api.EnvType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TestMod
{
    public static final String MOD_ID = "testmod";
    public static final Logger LOGGER = LogManager.getLogger();


    public static void init()
    {
        TestBlocks.BLOCKS.register();
        TestBlocks.TILES_ENTITIES.register();
        TestItems.CREATIVE_MODE_TABS.register();
        TestItems.ITEMS.register();
        TestContainers.CONTAINERS.register();
        PolyLib.initPolyItemData();

        if(Platform.getEnv() == EnvType.CLIENT)
        {
            TestModClient.init();
        }
    }
}
