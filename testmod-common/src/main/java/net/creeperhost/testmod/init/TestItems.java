package net.creeperhost.testmod.init;

import net.creeperhost.polylib.register.LazyItem;
import net.creeperhost.testmod.items.TestItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class TestItems
{
    public static LazyItem<Item> TEST_ITEM = new LazyItem<>(Identifier.fromNamespaceAndPath("testmod", "test_item"), TestItem::new, Item.Properties::new);
}
