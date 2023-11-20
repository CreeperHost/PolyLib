package net.creeperhost.testmod.init;

import dev.architectury.registry.menu.MenuRegistry;
import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.creeperhost.testmod.blocks.inventorytestblock.ScreenInventoryTestBlock;
import net.creeperhost.testmod.blocks.mguitestblock.MGuiTestBlockContainerMenu;
import net.creeperhost.testmod.blocks.mguitestblock.MGuiTestBlockGui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TestScreens
{
    public static void init()
    {
        MenuRegistry.registerScreenFactory(TestContainers.TEST_INVENTORY_CONTAINER.get(), ScreenInventoryTestBlock::new);
        MenuRegistry.registerScreenFactory(TestContainers.MGUI_TEST_BLOCK_CONTAINER.get(), MGuiTestBlockGui::create);
    }
}
