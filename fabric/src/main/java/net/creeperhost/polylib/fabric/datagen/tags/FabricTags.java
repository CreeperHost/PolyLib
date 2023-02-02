package net.creeperhost.polylib.fabric.datagen.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class FabricTags
{
    // Create a tag for all ores
    public static TagKey<Block> ORES = create("c:ores");
    // Create a tag for all ores that can be found in stone
    public static TagKey<Block> ORES_GROUND_STONE = create("c:ores_in_ground/stone");
    // Create a tag for all ores that can be found in deepslate
    public static TagKey<Block> ORES_GROUND_DEEPSLATE = create("c:ores_in_ground/deepslate");

    private static TagKey<Block> create(String string)
    {
        // Create the tag
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(string));
    }
}
