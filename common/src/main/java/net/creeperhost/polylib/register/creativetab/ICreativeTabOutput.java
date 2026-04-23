package net.creeperhost.polylib.register.creativetab;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface ICreativeTabOutput {
    void accept(ItemLike item);
    void accept(ItemStack stack);
}
