package net.creeperhost.testmod.init;

import net.creeperhost.polylib.platform.Services;
import net.creeperhost.polylib.register.LazyItem;
import net.creeperhost.testmod.items.TestItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class TestItems
{
    public static LazyItem<Item> TEST_ITEM = Services.REGISTER_HELPER.registerItem(new LazyItem<>(Identifier.fromNamespaceAndPath("testmod", "test_item"), TestItem::new, Item.Properties::new));
    public static LazyItem<Item> TEST_ITEM_2 = Services.REGISTER_HELPER.registerItem(new LazyItem<>(Identifier.fromNamespaceAndPath("testmod", "test_item_two"), TestItem::new, Item.Properties::new));

    public static void init() {}
}
