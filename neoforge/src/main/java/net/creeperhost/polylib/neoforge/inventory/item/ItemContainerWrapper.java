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

public class ItemContainerWrapper extends InvWrapper implements ICapabilityProvider, AutoSerializable
{
    private final SerializableContainer serializable;

    public ItemContainerWrapper(SerializableContainer inv)
    {
        super(inv);
        this.serializable = inv;
    }

//    @Override
//    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg)
//    {
//        return capability == Capabilities.ITEM_HANDLER ? LazyOptional.of(() -> this).cast() : LazyOptional.empty();
//    }

    @Override
    public Serializable getSerializable()
    {
        return serializable;
    }

    @Nullable
    @Override
    public Object getCapability(Object object, Object object2)
    {
        return null;
    }
}
