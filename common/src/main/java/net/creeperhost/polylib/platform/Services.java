package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.Constants;
import net.creeperhost.polylib.platform.services.*;
import net.creeperhost.polylib.registry.IRegistryFactory;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final INetworkHelper NETWORK = load(INetworkHelper.class);
    public static final IRegisterHelper REGISTER_HELPER = load(IRegisterHelper.class);
    public static final IRegistryFactory REGISTRY = load(IRegistryFactory.class);
    public static final IPlayerDataHelper PLAYER_DATA = load(IPlayerDataHelper.class);

    public static final IClientHelper CLIENT = PLATFORM.isClient() ? load(IClientHelper.class) : null;

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
