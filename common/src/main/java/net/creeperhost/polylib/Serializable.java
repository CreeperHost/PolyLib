package net.creeperhost.polylib;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface Serializable
{
    void deserialize(HolderLookup.Provider provider, CompoundTag nbt);

    CompoundTag serialize(HolderLookup.Provider provider, CompoundTag nbt);
}
