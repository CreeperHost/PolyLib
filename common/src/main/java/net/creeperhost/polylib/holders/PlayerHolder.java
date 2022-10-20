package net.creeperhost.polylib.holders;

import net.minecraft.world.entity.player.Player;

public class PlayerHolder
{
    private final Player player;

    public PlayerHolder(Player player)
    {
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }
}
