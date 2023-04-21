package net.creeperhost.polylib.helpers;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public class ItemTagHelper
{
    public static Iterable<Holder<Item>> getValues(String string)
    {
        return getValues(new ResourceLocation(string));
    }

    public static Iterable<Holder<Item>> getValues(ResourceLocation resourceLocation)
    {
        TagKey<Item> tagKey = TagKey.create(BuiltInRegistries.ITEM.key(), resourceLocation);
        return getValues(tagKey);
    }

    public static Iterable<Holder<Item>> getValues(TagKey<Item> tagKey)
    {
        return BuiltInRegistries.ITEM.getTagOrEmpty(tagKey);
    }

    public static Stream<TagKey<Item>> getAllTags(ItemStack itemStack)
    {
        return itemStack.getItem().builtInRegistryHolder().tags();
    }
}
