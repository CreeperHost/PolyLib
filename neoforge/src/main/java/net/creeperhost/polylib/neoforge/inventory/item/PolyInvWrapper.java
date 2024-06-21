package net.creeperhost.polylib.neoforge.inventory.item;

import net.creeperhost.polylib.inventory.items.ContainerAccessControl;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public class PolyInvWrapper extends InvWrapper {

    public PolyInvWrapper(Container inv) {
        super(inv);
    }

    //Use ContainerAccessControl controls in capability wrapper
    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        if(getInv() instanceof ContainerAccessControl containerAccessControl)
        {
            if(!containerAccessControl.canPlaceItem(slot, stack))
            {
                return stack;
            }
        }
        //TODO remove in 1.21, This is to support legacy code for now
        if(getInv() instanceof net.creeperhost.polylib.inventory.item.ContainerAccessControl containerAccessControl)
        {
            if(!containerAccessControl.canPlaceItem(slot, stack))
            {
                return stack;
            }
        }
        return super.insertItem(slot, stack, simulate);
    }

    //Use ContainerAccessControl controls in capability wrapper
    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if(getInv() instanceof ContainerAccessControl containerAccessControl)
        {
            ItemStack stack = getInv().getItem(slot);

            if(!containerAccessControl.canTakeItemThroughFace(slot, stack, Direction.UP))
            {
                return ItemStack.EMPTY;
            }
        }

        //TODO remove in 1.21, This is to support legacy code for now
        if(getInv() instanceof net.creeperhost.polylib.inventory.item.ContainerAccessControl containerAccessControl)
        {
            ItemStack stack = getInv().getItem(slot);

            if(!containerAccessControl.canTakeItemThroughFace(slot, stack, Direction.UP))
            {
                return ItemStack.EMPTY;
            }
        }
        return super.extractItem(slot, amount, simulate);
    }
}
