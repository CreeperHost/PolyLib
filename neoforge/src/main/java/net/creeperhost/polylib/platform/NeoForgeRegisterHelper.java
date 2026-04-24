package net.creeperhost.polylib.platform;

import net.creeperhost.polylib.platform.services.IRegisterHelper;
import net.creeperhost.polylib.register.creativetab.ICreativeTabOutput;
import net.creeperhost.polylib.register.creativetab.LazyCreativeTab;
import net.creeperhost.polylib.registry.IMenuFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NeoForgeRegisterHelper implements IRegisterHelper {

    private static final Map<String, DeferredRegister<CreativeModeTab>> TAB_REGISTERS = new HashMap<>();

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

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(IMenuFactory<T> factory) {
        return IMenuTypeExtension.create((id, inv, buf) -> factory.create(id, inv, buf));
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider menuProvider, Consumer<RegistryFriendlyByteBuf> dataWriter) {
        player.openMenu(menuProvider, dataWriter);
    }

    private static IEventBus getEventBus() {
        return ModLoadingContext.get().getActiveContainer().getEventBus();
    }
}
