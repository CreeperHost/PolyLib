package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.platform.services.IRegisterHelper;
import net.creeperhost.polylib.register.creativetab.ICreativeTabOutput;
import net.creeperhost.polylib.register.creativetab.LazyCreativeTab;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

public class FabricRegisterHelper implements IRegisterHelper {

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
