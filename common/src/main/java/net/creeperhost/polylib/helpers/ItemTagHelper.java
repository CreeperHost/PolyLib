package net.creeperhost.polylib.helpers;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemTagHelper
{
    public static Iterable<Holder<Item>> getValues(String string)
    {
        return getValues(new ResourceLocation(string));
    }

    public static Iterable<Holder<Item>> getValues(ResourceLocation resourceLocation)
    {
        TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, resourceLocation);
        return getValues(tagKey);
    }

    public static Iterable<Holder<Item>> getValues(TagKey<Item> tagKey)
    {
        return Registry.ITEM.getTagOrEmpty(tagKey);
    }
}
