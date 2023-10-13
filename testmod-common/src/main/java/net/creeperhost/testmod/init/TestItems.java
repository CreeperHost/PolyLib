package net.creeperhost.testmod.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.polylib.registry.CreativeTabRegistry;
import net.creeperhost.testmod.TestMod;
import net.minecraft.core.Registry;
import net.minecraft.world.item.*;

public class TestItems
{
    public static final CreativeModeTab CREATIVE_MODE_TAB = CreativeTabRegistry.of(TestMod.MOD_ID, "_creativetab", () -> new ItemStack(Items.DIAMOND));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(TestMod.MOD_ID, Registry.ITEM_REGISTRY);

    public static final RegistrySupplier<Item> INVENTORY_TEST_ITEMBLOCK = ITEMS.register("inventory_test_block", () -> new BlockItem(TestBlocks.INVENTORY_TEST_BLOCK.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));
    public static final RegistrySupplier<Item> MULTIBLOCK_TEST_ITEMBLOCK = ITEMS.register("multiblock_test_block", () -> new BlockItem(TestBlocks.MULTIBLOCK_TEST_BLOCK.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));

    public static final RegistrySupplier<Item> MGUI_TEST_ITEMBLOCK = ITEMS.register("mgui_test_block", () -> new BlockItem(TestBlocks.MGUI_TEST_BLOCK.get(), new Item.Properties()));
}
