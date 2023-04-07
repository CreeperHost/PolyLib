package net.creeperhost.polylib.forge;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

@Deprecated(forRemoval = true)
public class BasicCapabilityProvider<T> implements ICapabilityProvider
{
    private final LazyOptional<T> instanceLazy;
    private final Capability<T> capability;

    public BasicCapabilityProvider(Capability<T> capability, T instance)
    {
        this.instanceLazy = LazyOptional.of(() -> instance);
        this.capability = capability;
    }

    @Nonnull
    @Override
    public <U> LazyOptional<U> getCapability(@Nonnull Capability<U> cap, @Nullable Direction side)
    {
        return cap == this.capability ? instanceLazy.cast() : LazyOptional.empty();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        return cap == this.capability ? instanceLazy.cast() : LazyOptional.empty();
    }

    public void invalidate()
    {
        instanceLazy.invalidate();
    }
}
