package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.platform.services.IRegisterHelper;
import net.creeperhost.polylib.register.LazyItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

public class NeoForgeRegisterHelper implements IRegisterHelper {

    private static final Map<String, DeferredRegister.Items> REGISTERS = new HashMap<>();

    @Override
    public LazyItem<Item> registerItem(LazyItem<Item> item) {
        String namespace = item.getRegistryName().getNamespace();
        DeferredRegister.Items dr = REGISTERS.computeIfAbsent(namespace, ns -> {
            DeferredRegister.Items reg = DeferredRegister.createItems(ns);
            IEventBus bus = ModLoadingContext.get().getActiveContainer().getEventBus();
            reg.register(bus);
            return reg;
        });
        dr.registerItem(item.getRegistryName().getPath(), item.getFunc(), item.getProperties());
        return item;
    }
}
