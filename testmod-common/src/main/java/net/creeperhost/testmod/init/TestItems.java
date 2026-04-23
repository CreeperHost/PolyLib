package net.creeperhost.testmod.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.creeperhost.testmod.TestModCommon;
import net.creeperhost.testmod.items.TestItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class TestItems
{
    public static final PolyRegistry<Item> ITEMS = PolyRegistry.create(Registries.ITEM, TestModCommon.MOD_ID);

    public static final Supplier<Item> TEST_ITEM = ITEMS.register("test_item", "Test Item", () -> new TestItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TestModCommon.MOD_ID, "test_item")))));
    public static final Supplier<Item> TEST_ITEM_2 = ITEMS.register("test_item_two", "Test Item Two", () -> new TestItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TestModCommon.MOD_ID, "test_item_two")))));
    public static final Supplier<Item> TEST_BLOCK = ITEMS.register("test_block", "Test Block", () -> new BlockItem(TestBlocks.TEST_BLOCK.get(), new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TestModCommon.MOD_ID, "test_block")))));

    public static void init() {
        ITEMS.init();
    }
}
