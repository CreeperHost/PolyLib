package net.creeperhost.polylib;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface Serializable
{
    void deserialize(ValueInput input);

    void serialize(ValueOutput output);
}
