package net.creeperhost.polylib.neoforge.inventory.item;

import net.creeperhost.polylib.Serializable;
import net.creeperhost.polylib.neoforge.AutoSerializable;
import net.creeperhost.polylib.inventory.item.SerializableContainer;
//import net.neoforged.neoforge.common.capabilities.Capabilities;
//import net.neoforged.neoforge.common.capabilities.Capability;
//import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
//import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

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