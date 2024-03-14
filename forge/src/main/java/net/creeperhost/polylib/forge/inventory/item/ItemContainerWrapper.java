package net.creeperhost.polylib.forge.inventory.item;

import net.creeperhost.polylib.Serializable;
import net.creeperhost.polylib.forge.AutoSerializable;
import net.creeperhost.polylib.inventory.item.SerializableContainer;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated (forRemoval = true)
public class ItemContainerWrapper extends InvWrapper implements ICapabilityProvider, AutoSerializable
{
    private final SerializableContainer serializable;

    public ItemContainerWrapper(SerializableContainer inv)
    {
        super(inv);
        this.serializable = inv;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg)
    {
        return capability == ForgeCapabilities.ITEM_HANDLER ? LazyOptional.of(() -> this).cast() : LazyOptional.empty();
    }

    @Override
    public Serializable getSerializable()
    {
        return serializable;
    }
}
