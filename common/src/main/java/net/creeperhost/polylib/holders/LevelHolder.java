package net.creeperhost.polylib.holders;

import net.minecraft.world.level.Level;

@Deprecated(forRemoval = true)
public class LevelHolder
{
    private final Level level;

    public LevelHolder(Level level)
    {
        this.level = level;
    }

    public Level getLevel()
    {
        return level;
    }
}
