package net.creeperhost.polylib.platform.services;

import net.creeperhost.polylib.register.LazyBlock;
import net.creeperhost.polylib.register.creativetab.LazyCreativeTab;
import net.creeperhost.polylib.register.LazyItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface IRegisterHelper {
    LazyItem<Item> registerItem(LazyItem<Item> item);
    <B extends Block> LazyBlock<B> registerBlock(LazyBlock<B> block);
    LazyCreativeTab registerCreativeTab(LazyCreativeTab tab);
}
