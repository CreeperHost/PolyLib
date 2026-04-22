package net.creeperhost.polylib.helpers;

import net.minecraft.network.chat.Component;

public class ComponentHelper
{
    public static Component literal(String string)
    {
        return Component.literal(string);
    }

    public static Component translatable(String string)
    {
        return Component.translatable(string);
    }
}
