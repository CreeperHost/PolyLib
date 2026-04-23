package net.creeperhost.polylib.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface IRegistryFactory {
    
    /**
     * Creates a platform-specific PolyRegistry instance.
     */
    <T> PolyRegistry<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId);
    
}
