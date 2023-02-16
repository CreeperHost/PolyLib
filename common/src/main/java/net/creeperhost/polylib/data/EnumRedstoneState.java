package net.creeperhost.polylib.data;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum EnumRedstoneState
{
    IGNORED("ignored", new ItemStack(Items.BARRIER)), REQUIRED("required",
        new ItemStack(Items.REDSTONE_TORCH)), INVERTED("inverted", new ItemStack(Items.REDSTONE));

    private final String name;
    private final ItemStack renderStack;

    EnumRedstoneState(String name, ItemStack renderStack)
    {
        this.name = name;
        this.renderStack = renderStack;
    }

    public String getName()
    {
        return name;
    }

    public ItemStack getRenderStack()
    {
        return renderStack;
    }
}
