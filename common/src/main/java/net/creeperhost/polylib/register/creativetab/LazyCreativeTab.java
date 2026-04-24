package net.creeperhost.polylib.register.creativetab;

import net.creeperhost.polylib.data.lang.PolyLangContributions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Deprecated
public class LazyCreativeTab {

    private final Identifier registryName;
    private final Component title;
    private final Supplier<ItemStack> icon;
    private final Consumer<ICreativeTabOutput> populator;

    public LazyCreativeTab(Identifier registryName, Component title, Supplier<ItemStack> icon, Consumer<ICreativeTabOutput> populator) {
        this.registryName = registryName;
        this.title = title;
        this.icon = icon;
        this.populator = populator;
    }

    /**
     * Convenience constructor that derives the standard {@code itemGroup.<namespace>.<path>}
     * translation key from the registry name and auto-contributes it to
     * {@link PolyLangContributions} for datagen.
     */
    public LazyCreativeTab(Identifier registryName, String defaultEnglish, Supplier<ItemStack> icon, Consumer<ICreativeTabOutput> populator) {
        this.registryName = registryName;
        String key = "itemGroup." + registryName.getNamespace() + "." + registryName.getPath();
        this.title = Component.translatable(key);
        this.icon = icon;
        this.populator = populator;
        PolyLangContributions.contribute(key, defaultEnglish);
    }

    public Identifier getRegistryName() {
        return registryName;
    }

    public Component getTitle() {
        return title;
    }

    public Supplier<ItemStack> getIcon() {
        return icon;
    }

    public Consumer<ICreativeTabOutput> getPopulator() {
        return populator;
    }
}
