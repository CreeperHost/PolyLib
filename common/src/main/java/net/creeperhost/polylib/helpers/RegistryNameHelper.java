package net.creeperhost.polylib.helpers;

import net.minecraft.core.Registry;
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
        return Optional.of(Registry.ITEM.getKey(item));
    }

    public static Optional<ResourceLocation> getRegistryName(Block block)
    {
        return Optional.of(Registry.BLOCK.getKey(block));
    }

    public static Optional<ResourceLocation> getRegistryName(EntityType<?> entityType)
    {
        return Optional.of(Registry.ENTITY_TYPE.getKey(entityType));
    }

    public static Optional<ResourceLocation> getRegistryName(Enchantment enchantment)
    {
        return Optional.ofNullable(Registry.ENCHANTMENT.getKey(enchantment));
    }

    public static Optional<ResourceLocation> getRegistryName(Potion potion)
    {
        return Optional.of(Registry.POTION.getKey(potion));
    }
}
