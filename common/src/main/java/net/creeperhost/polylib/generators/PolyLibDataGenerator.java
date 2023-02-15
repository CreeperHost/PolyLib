package net.creeperhost.polylib.generators;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class PolyLibDataGenerator
{
    public static Map<TagKey<Block>, Block> BLOCK_TAGS = new HashMap<>();
    public static Map<TagKey<Item>, Item> ITEM_TAGS = new HashMap<>();

    public static void registerBlockTag(TagKey<Block> tagKey, Block block)
    {
        BLOCK_TAGS.put(tagKey, block);
    }

    public static void registerItemTag(TagKey<Item> tagKey, Item item)
    {
        ITEM_TAGS.put(tagKey, item);
    }
}
