package net.creeperhost.polylib.helpers;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockTagHelper
{
    public static Iterable<Holder<Block>> getValues(String string)
    {
        return getValues(new ResourceLocation(string));
    }

    public static Iterable<Holder<Block>> getValues(ResourceLocation resourceLocation)
    {
        TagKey<Block> tagKey = TagKey.create(BuiltInRegistries.BLOCK.key(), resourceLocation);
        return getValues(tagKey);
    }

    public static Iterable<Holder<Block>> getValues(TagKey<Block> tagKey)
    {
        return BuiltInRegistries.BLOCK.getTagOrEmpty(tagKey);
    }
}
