package net.creeperhost.testmod.init;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.testmod.TestMod;
import net.creeperhost.testmod.blocks.inventorytestblock.ContainerInventoryTestBlock;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;

public class TestContainers
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(TestMod.MOD_ID, Registry.MENU_REGISTRY);
    public static final RegistrySupplier<MenuType<ContainerInventoryTestBlock>> TEST_INVENTORY_CONTAINER = CONTAINERS.register("container_breeder", () -> MenuRegistry.ofExtended(ContainerInventoryTestBlock::new));

}
