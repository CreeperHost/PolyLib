package net.creeperhost.polylib;

import net.creeperhost.polylib.config.ConfigBuilder;
import net.creeperhost.polylib.config.PolyConfig;
import net.creeperhost.polylib.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;

public class CommonClass
{
    public static ConfigBuilder configBuilder;
    public static PolyConfig configData;

    public static void init()
    {
        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        Constants.LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        if (Services.PLATFORM.isModLoaded("examplemod")) {

            Constants.LOG.info("Hello to examplemod");
        }
    }

    public static void registerConfig()
    {
        Constants.LOG.info("Registering Common Config");
        configBuilder = new ConfigBuilder(Constants.MOD_ID + ".json", Services.PLATFORM.getConfigFolder().resolve(Constants.MOD_ID + ".json"), PolyConfig.class);
        configData = (PolyConfig) configBuilder.getConfigData();
    }
}