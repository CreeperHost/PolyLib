package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.platform.services.IRegisterHelper;
import net.creeperhost.polylib.register.LazyItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class FabricRegisterHelper implements IRegisterHelper {

    @Override
    public LazyItem<Item> registerItem(LazyItem<Item> item) {
        Registry.register(BuiltInRegistries.ITEM, item.getRegistryName(),
                item.getFunc().apply(item.getProperties().get().setId(ResourceKey.create(Registries.ITEM, item.getRegistryName()))));
        return item;
    }
}
