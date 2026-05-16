package net.creeperhost.testmod.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.creeperhost.testmod.TestModCommon;
import net.creeperhost.testmod.blocks.creativepower.PowerContainer;
import net.creeperhost.testmod.blocks.inventorytestblock.ContainerInventoryTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

import net.creeperhost.testmod.blocks.mirror.MirrorContainer;

public class TestContainers
{
    public static final PolyRegistry<MenuType<?>> CONTAINERS = PolyRegistry.create(Registries.MENU, TestModCommon.MOD_ID);
    public static final Supplier<MenuType<ContainerInventoryTest>> TEST_INVENTORY_CONTAINER =
            CONTAINERS.registerMenu("test_container", ContainerInventoryTest::new);

    public static final Supplier<MenuType<PowerContainer>> CREATIVE_POWER_CONTAINER =
            CONTAINERS.registerMenu("creative_power_container", PowerContainer::new);

    public static final Supplier<MenuType<MirrorContainer>> MIRROR_CONTAINER =
            CONTAINERS.registerMenu("mirror_container", MirrorContainer::new);



    public static void init() {
        CONTAINERS.init();
    }

}
