package net.creeperhost.polylib.platform.services;

import net.creeperhost.polylib.register.creativetab.LazyCreativeTab;
import net.creeperhost.polylib.register.LazyItem;
import net.minecraft.world.item.Item;

public interface IRegisterHelper {
    LazyItem<Item> registerItem(LazyItem<Item> item);
    LazyCreativeTab registerCreativeTab(LazyCreativeTab tab);
}
