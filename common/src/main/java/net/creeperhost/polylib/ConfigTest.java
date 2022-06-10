package net.creeperhost.polylib;

import blue.endless.jankson.Comment;
import net.creeperhost.polylib.config.ConfigBuilder;
import net.creeperhost.polylib.config.ConfigData;

//THIS IS A TEST CLASS
public class ConfigTest
{
    public static void init()
    {
        ConfigBuilder configBuilder = new ConfigBuilder("test", ConfigDataTest.class);
    }

    public static class ConfigDataTest extends ConfigData
    {
        @Comment("TEST")
        public int IM_A_TEST = 1;
    }

}
