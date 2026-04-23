package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.platform.services.IRegisterHelper;
import net.creeperhost.polylib.register.creativetab.ICreativeTabOutput;
import net.creeperhost.polylib.register.creativetab.LazyCreativeTab;
import net.creeperhost.polylib.register.LazyItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class FabricRegisterHelper implements IRegisterHelper {

    @Override
    public LazyItem<Item> registerItem(LazyItem<Item> item) {
        Registry.register(BuiltInRegistries.ITEM, item.getRegistryName(),
                item.getFunc().apply(item.getProperties().get().setId(ResourceKey.create(Registries.ITEM, item.getRegistryName()))));
        return item;
    }

    @Override
    public LazyCreativeTab registerCreativeTab(LazyCreativeTab tab) {
        CreativeModeTab creativeTab = CreativeModeTab.builder()
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
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, tab.getRegistryName(), creativeTab);
        return tab;
    }
}
