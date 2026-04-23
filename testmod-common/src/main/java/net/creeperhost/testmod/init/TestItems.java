package net.creeperhost.testmod.init;

import net.creeperhost.polylib.registry.PolyRegistry;
import net.creeperhost.testmod.items.TestItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class TestItems
{
    public static final PolyRegistry<Item> ITEMS = PolyRegistry.create(Registries.ITEM, "testmod");

    public static final Supplier<Item> TEST_ITEM = ITEMS.register("test_item", "Test Item", TestItem::new);
    public static final Supplier<Item> TEST_ITEM_2 = ITEMS.register("test_item_two", "Test Item Two", TestItem::new);

    public static void init() {
        ITEMS.init();
    }
}
