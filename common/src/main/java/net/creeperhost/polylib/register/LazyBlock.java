package net.creeperhost.polylib.register;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class LazyBlock<B extends Block> implements ItemLike {

    private final Identifier registryName;
    private final Function<BlockBehaviour.Properties, ? extends B> func;
    private final Supplier<BlockBehaviour.Properties> properties;
    private final boolean withBlockItem;

    public LazyBlock(Identifier registryName, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties) {
        this(registryName, func, properties, true);
    }

    public LazyBlock(Identifier registryName, Function<BlockBehaviour.Properties, ? extends B> func, Supplier<BlockBehaviour.Properties> properties, boolean withBlockItem) {
        this.registryName = registryName;
        this.func = func;
        this.properties = properties;
        this.withBlockItem = withBlockItem;
    }

    public Identifier getRegistryName() {
        return registryName;
    }

    public Function<BlockBehaviour.Properties, ? extends B> getFunc() {
        return func;
    }

    public Supplier<BlockBehaviour.Properties> getProperties() {
        return properties;
    }

    public boolean isWithBlockItem() {
        return withBlockItem;
    }

    @SuppressWarnings("unchecked")
    public B asBlock() {
        return (B) BuiltInRegistries.BLOCK.getValue(registryName);
    }

    @Override
    public @NonNull Item asItem() {
        return BuiltInRegistries.ITEM.getValue(registryName);
    }
}
