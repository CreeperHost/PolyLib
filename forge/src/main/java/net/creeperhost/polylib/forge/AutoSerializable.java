package net.creeperhost.polylib.forge;

import net.creeperhost.polylib.Serializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface AutoSerializable extends INBTSerializable<CompoundTag>
{
    Serializable getSerializable();

    @Override
    default CompoundTag serializeNBT()
    {
        return getSerializable().serialize(new CompoundTag());
    }

    @Override
    default void deserializeNBT(CompoundTag compoundTag)
    {
        getSerializable().deserialize(compoundTag);
    }
}
