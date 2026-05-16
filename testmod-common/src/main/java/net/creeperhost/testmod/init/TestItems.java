package net.creeperhost.testmod.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.creeperhost.testmod.TestModCommon;
import net.creeperhost.testmod.items.ItemPowered;
import net.creeperhost.testmod.items.TestItem;
import net.creeperhost.testmod.items.BatteryItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class TestItems
{
    public static final PolyRegistry<Item> ITEMS = PolyRegistry.create(Registries.ITEM, TestModCommon.MOD_ID);

    public static final Supplier<Item> TEST_ITEM = ITEMS.registerItem("test_item", "Test Item", TestItem::new);
    public static final Supplier<Item> TEST_ITEM_2 = ITEMS.registerItem("test_item_two", "Test Item Two", TestItem::new);
    public static final Supplier<Item> BATTERY = ITEMS.registerItem("battery", "Battery", BatteryItem::new);

    public static final Supplier<Item> POWERED_ITEM = ITEMS.registerItem("powered_item", "Powered Item", ItemPowered::new);


    public static final Supplier<Item> TEST_BLOCK = ITEMS.registerItem("test_block", "Test Block", props -> new BlockItem(TestBlocks.TEST_BLOCK.get(), props));
    public static final Supplier<Item> CREATIVE_POWER_BLOCK_ITEM = ITEMS.registerItem("creative_power_blockitem", "Creative Power block", props -> new BlockItem(TestBlocks.CREATIVE_POWER_BLOCK.get(), props));
    public static final Supplier<Item> MULTIBLOCK_ITEM = ITEMS.registerItem("multiblock_test_block", "Multiblock block", props -> new BlockItem(TestBlocks.MULTIBLOCK_BLOCK.get(), props));

    public static void init() {
        ITEMS.init();
    }
}
