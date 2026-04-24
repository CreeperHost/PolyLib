package net.creeperhost.testmod.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.creeperhost.testmod.TestModCommon;
import net.creeperhost.testmod.blocks.inventorytestblock.ContainerInventoryTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class TestContainers
{
    public static final PolyRegistry<MenuType<?>> CONTAINERS = PolyRegistry.create(Registries.MENU, TestModCommon.MOD_ID);
    public static final Supplier<MenuType<ContainerInventoryTest>> TEST_INVENTORY_CONTAINER =
            CONTAINERS.registerMenu("test_container", ContainerInventoryTest::new);

    public static void init() {
        CONTAINERS.init();
    }

}
