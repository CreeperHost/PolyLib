package net.creeperhost.polylib.register;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;
import java.util.function.Supplier;

public class LazyItem<I extends Item> implements ItemLike
{
    private final Identifier registryName;
    private final Function<Item.Properties, ? extends I> func;
    private final Supplier<Item.Properties> properties;

    public LazyItem(Identifier registryName, Function<Item.Properties, ? extends I> func, Supplier<Item.Properties> properties)
    {
        this.registryName = registryName;
        this.func = func;
        this.properties = properties;
    }

    public Function<Item.Properties, ? extends I> getFunc()
    {
        return func;
    }

    public Supplier<Item.Properties> getProperties()
    {
        return properties;
    }

    public Identifier getRegistryName()
    {
        return registryName;
    }

    @Override
    public @NonNull Item asItem()
    {
        return BuiltInRegistries.ITEM.getValue(registryName);
    }
}
