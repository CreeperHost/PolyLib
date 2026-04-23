package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.platform.services.IRegisterHelper;
import net.creeperhost.polylib.register.creativetab.ICreativeTabOutput;
import net.creeperhost.polylib.register.creativetab.LazyCreativeTab;
import net.creeperhost.polylib.register.LazyItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

public class NeoForgeRegisterHelper implements IRegisterHelper {

    private static final Map<String, DeferredRegister.Items> ITEM_REGISTERS = new HashMap<>();
    private static final Map<String, DeferredRegister<CreativeModeTab>> TAB_REGISTERS = new HashMap<>();

    @Override
    public LazyItem<Item> registerItem(LazyItem<Item> item) {
        String namespace = item.getRegistryName().getNamespace();
        DeferredRegister.Items dr = ITEM_REGISTERS.computeIfAbsent(namespace, ns -> {
            DeferredRegister.Items reg = DeferredRegister.createItems(ns);
            reg.register(getEventBus());
            return reg;
        });
        dr.registerItem(item.getRegistryName().getPath(), item.getFunc(), item.getProperties());
        return item;
    }

    @Override
    public LazyCreativeTab registerCreativeTab(LazyCreativeTab tab) {
        String namespace = tab.getRegistryName().getNamespace();
        DeferredRegister<CreativeModeTab> dr = TAB_REGISTERS.computeIfAbsent(namespace, ns -> {
            DeferredRegister<CreativeModeTab> reg = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ns);
            reg.register(getEventBus());
            return reg;
        });
        dr.register(tab.getRegistryName().getPath(), () ->
                CreativeModeTab.builder()
                        .title(tab.getTitle())
                        .icon(tab.getIcon())
                        .displayItems((params, output) -> {
                            ICreativeTabOutput wrapper = new ICreativeTabOutput() {
                                @Override
                                public void accept(net.minecraft.world.level.ItemLike item) { output.accept(item); }
                                @Override
                                public void accept(net.minecraft.world.item.ItemStack stack) { output.accept(stack); }
                            };
                            tab.getPopulator().accept(wrapper);
                        })
                        .build()
        );
        return tab;
    }

    private static IEventBus getEventBus() {
        return ModLoadingContext.get().getActiveContainer().getEventBus();
    }
}
