package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.platform.services.IRegisterHelper;
import net.creeperhost.polylib.register.LazyBlock;
import net.creeperhost.polylib.register.creativetab.ICreativeTabOutput;
import net.creeperhost.polylib.register.creativetab.LazyCreativeTab;
import net.creeperhost.polylib.register.LazyItem;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class FabricRegisterHelper implements IRegisterHelper {

    @Override
    public LazyItem<Item> registerItem(LazyItem<Item> item) {
        Registry.register(BuiltInRegistries.ITEM, item.getRegistryName(),
                item.getFunc().apply(item.getProperties().get().setId(ResourceKey.create(Registries.ITEM, item.getRegistryName()))));
        return item;
    }

    @Override
    public <B extends Block> LazyBlock<B> registerBlock(LazyBlock<B> block) {
        B registered = Registry.register(BuiltInRegistries.BLOCK, block.getRegistryName(),
                block.getFunc().apply(block.getProperties().get().setId(ResourceKey.create(Registries.BLOCK, block.getRegistryName()))));
        if (block.isWithBlockItem()) {
            Registry.register(BuiltInRegistries.ITEM, block.getRegistryName(),
                    new BlockItem(registered, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, block.getRegistryName()))));
        }
        return block;
    }

    @Override
    public LazyCreativeTab registerCreativeTab(LazyCreativeTab tab) {
        CreativeModeTab creativeTab = FabricCreativeModeTab.builder()
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
