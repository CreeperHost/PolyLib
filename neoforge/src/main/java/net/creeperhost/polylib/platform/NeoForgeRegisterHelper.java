package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.platform.services.IRegisterHelper;
import net.creeperhost.polylib.register.LazyItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NeoForgeRegisterHelper implements IRegisterHelper {

    @Override
    public LazyItem<Item> registerItem(LazyItem<Item> item) {
        DeferredRegister.Items ITEMS = DeferredRegister.createItems(item.getRegistryName().getNamespace());
        ITEMS.registerItem(item.getRegistryName().getPath(), item.getFunc(), item.getProperties());
        return item;
    }
}
