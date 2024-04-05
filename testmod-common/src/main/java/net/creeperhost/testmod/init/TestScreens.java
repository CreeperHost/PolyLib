package net.creeperhost.testmod.init;

import dev.architectury.registry.menu.MenuRegistry;
import net.creeperhost.polylib.client.modulargui.ModularGuiContainer;
import net.creeperhost.polylib.client.modulargui.ModularGuiInjector;
import net.creeperhost.testmod.blocks.inventorytestblock.ScreenInventoryTestBlock;
import net.creeperhost.testmod.blocks.mguitestblock.MGuiTestBlockContainerMenu;
import net.creeperhost.testmod.blocks.mguitestblock.MGuiTestBlockGui;
import net.creeperhost.testmod.client.gui.ChestGuiInjection;
import net.creeperhost.testmod.client.gui.MainMenuGuiInjection;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class TestScreens
{
    //Simply creating this instance is all you need to do for this to work.
    private static ModularGuiInjector<TitleScreen> mainMenuInjection = new ModularGuiInjector<>(e -> e instanceof TitleScreen, e -> new MainMenuGuiInjection());
    private static ModularGuiInjector<ContainerScreen> chestScreenInjection = new ModularGuiInjector<>(e -> e instanceof ContainerScreen, e -> new ChestGuiInjection());

    public static void init()
    {
        MenuRegistry.registerScreenFactory(TestContainers.TEST_INVENTORY_CONTAINER.get(), ScreenInventoryTestBlock::create);
        MenuRegistry.registerScreenFactory(TestContainers.MGUI_TEST_BLOCK_CONTAINER.get(), MGuiTestBlockGui::create);
    }
}
