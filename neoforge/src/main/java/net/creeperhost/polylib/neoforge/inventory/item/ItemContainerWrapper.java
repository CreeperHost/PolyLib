package net.creeperhost.polylib.neoforge.inventory.item;

import net.creeperhost.polylib.Serializable;
import net.creeperhost.polylib.neoforge.AutoSerializable;
import net.creeperhost.polylib.inventory.item.SerializableContainer;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

@Deprecated
public class ItemContainerWrapper extends InvWrapper implements AutoSerializable
{
    private final SerializableContainer serializable;

    public ItemContainerWrapper(SerializableContainer inv)
    {
        super(inv);
        this.serializable = inv;
    }

    @Override
    public Serializable getSerializable()
    {
        return serializable;
    }
}
