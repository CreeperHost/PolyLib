package net.creeperhost.testmod.init;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.blocks.inventorytestblock.ContainerInventoryTestBlock;
import net.creeperhost.testmod.blocks.mguitestblock.MGuiTestBlockContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.Registries;


public class TestContainers
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(TestMod.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<ContainerInventoryTestBlock>> TEST_INVENTORY_CONTAINER =
            CONTAINERS.register("continer_inventory_test_block",
                    () -> MenuRegistry.ofExtended(ContainerInventoryTestBlock::new));

    public static final RegistrySupplier<MenuType<MGuiTestBlockContainerMenu>> MGUI_TEST_BLOCK_CONTAINER =
            CONTAINERS.register("mgui_test_block_container",
                    () -> MenuRegistry.ofExtended(MGuiTestBlockContainerMenu::new));

}
