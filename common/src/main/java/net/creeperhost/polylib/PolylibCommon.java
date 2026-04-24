package net.creeperhost.polylib;

import net.creeperhost.polylib.config.ConfigBuilder;
import net.creeperhost.polylib.config.PolyConfig;
import net.creeperhost.polylib.init.InternalEventListener;
import net.creeperhost.polylib.network.PolyLibNetwork;
import net.creeperhost.polylib.platform.Services;

public class PolylibCommon
{
    public static ConfigBuilder configBuilder;
    public static PolyConfig configData;

    public static void init()
    {
        PolyLibNetwork.init();
        InternalEventListener.init();
        if (Services.PLATFORM.isClient()) {
            PolyLibClient.init();
        }
    }

    public static void registerConfig()
    {
        Constants.LOG.info("Registering Common Config");
        configBuilder = new ConfigBuilder(Constants.MOD_ID + ".json", Services.PLATFORM.getConfigFolder().resolve(Constants.MOD_ID + ".json"), PolyConfig.class);
        configData = (PolyConfig) configBuilder.getConfigData();
    }
}