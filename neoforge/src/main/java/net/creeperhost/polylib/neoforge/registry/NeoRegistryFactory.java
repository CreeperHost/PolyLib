package net.creeperhost.polylib.neoforge.registry;

import net.creeperhost.polylib.registry.IRegistryFactory;
import net.creeperhost.polylib.registry.PolyRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class NeoRegistryFactory implements IRegistryFactory {
    
    @Override
    public <T> PolyRegistry<T> create(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        return new NeoPolyRegistry<>(registryKey, modId);
    }
    
}
