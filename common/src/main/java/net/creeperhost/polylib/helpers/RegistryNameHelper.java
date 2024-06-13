package net.creeperhost.polylib.helpers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class RegistryNameHelper
{
    public static Optional<ResourceLocation> getRegistryName(Item item)
    {
        return Optional.of(BuiltInRegistries.ITEM.getKey(item));
    }

    public static Optional<ResourceLocation> getRegistryName(Block block)
    {
        return Optional.of(BuiltInRegistries.BLOCK.getKey(block));
    }

    public static Optional<ResourceLocation> getRegistryName(EntityType<?> entityType)
    {
        return Optional.of(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
    }

    public static Optional<ResourceLocation> getRegistryName(Potion potion)
    {
        return Optional.of(BuiltInRegistries.POTION.getKey(potion));
    }
}
