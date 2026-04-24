package net.creeperhost.polylib;

import net.creeperhost.polylib.init.InternalEventListenerClient;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class PolyLibClient
{
    public static void init()
    {
        InternalEventListenerClient.init();
    }

    public static Player getClientPlayer()
    {
        if (Services.PLATFORM.isClient())
        {
            return _getClientPlayer();
        }
        return null;
    }

    private static Player _getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }
}
