package net.creeperhost.polylib.helpers;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockTagHelper
{
    public static Iterable<Holder<Block>> getValues(String string)
    {
        return getValues(Identifier.withDefaultNamespace(string));
    }

    public static Iterable<Holder<Block>> getValues(Identifier resourceLocation)
    {
        TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, resourceLocation);
        return getValues(tagKey);
    }

    public static Iterable<Holder<Block>> getValues(TagKey<Block> tagKey)
    {
        return BuiltInRegistries.BLOCK.getTagOrEmpty(tagKey);
    }
}
