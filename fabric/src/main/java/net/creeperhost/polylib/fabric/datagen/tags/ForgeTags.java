package net.creeperhost.polylib.fabric.datagen.tags;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ForgeTags
{
    // Create a variable for the block tag forge:ores
    public static TagKey<Block> ORES = create("forge:ores");
    // Create a variable for the block tag forge:ores_in_ground/stone
    public static TagKey<Block> ORES_GROUND_STONE = create("forge:ores_in_ground/stone");
    // Create a variable for the block tag forge:ores_in_ground/deepslate
    public static TagKey<Block> ORES_GROUND_DEEPSLATE = create("forge:ores_in_ground/deepslate");

    // Create a method to create a tag key
    private static TagKey<Block> create(String string)
    {
        // Create a tag key with the block registry and the resource location of the tag
        return TagKey.create(BuiltInRegistries.BLOCK.key(), new ResourceLocation(string));
    }
}
