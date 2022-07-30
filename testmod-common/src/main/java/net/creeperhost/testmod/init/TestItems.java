package net.creeperhost.testmod.init;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.creeperhost.testmod.TestMod;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

public class TestItems
{
    public static final CreativeModeTab CREATIVE_MODE_TAB = CreativeTabRegistry.create(new ResourceLocation(TestMod.MOD_ID, "_creativetab"), () -> new ItemStack(Items.DIAMOND));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(TestMod.MOD_ID, Registry.ITEM_REGISTRY);

    public static final RegistrySupplier<Item> TEST_ITEM = ITEMS.register("test", () -> new Item(new Item.Properties().tab(CREATIVE_MODE_TAB)));

    public static final RegistrySupplier<Item> INVENTORY_TEST_ITEMBLOCK = ITEMS.register("inventory_test_block", () -> new BlockItem(TestBlocks.INVENTORY_TEST_BLOCK.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));
    public static final RegistrySupplier<Item> MULTIBLOCK_TEST_ITEMBLOCK = ITEMS.register("multiblock_test_block", () -> new BlockItem(TestBlocks.MULTIBLOCK_TEST_BLOCK.get(), new Item.Properties().tab(CREATIVE_MODE_TAB)));

}
