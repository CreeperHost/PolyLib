package net.creeperhost.polylib.helpers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class RegistryNameHelper
{
    public static Optional<Identifier> getRegistryName(Item item)
    {
        return Optional.of(BuiltInRegistries.ITEM.getKey(item));
    }

    public static Optional<Identifier> getRegistryName(Block block)
    {
        return Optional.of(BuiltInRegistries.BLOCK.getKey(block));
    }

    public static Optional<Identifier> getRegistryName(EntityType<?> entityType)
    {
        return Optional.of(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
    }

    public static Optional<Identifier> getRegistryName(Potion potion)
    {
        return Optional.of(BuiltInRegistries.POTION.getKey(potion));
    }
}
