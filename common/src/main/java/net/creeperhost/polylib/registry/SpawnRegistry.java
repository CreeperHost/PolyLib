package net.creeperhost.polylib.registry;

import net.creeperhost.polylib.mixins.InvokerSpawnPlacements;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Supplier;

public class SpawnRegistry
{
    public static void registerSpawnPlacement(Supplier<EntityType<?>> entityType, SpawnPlacements.Type type, Heightmap.Types types, SpawnPlacements.SpawnPredicate<?> spawnPredicate)
    {
        InvokerSpawnPlacements.callRegister(entityType.get(), type, types, spawnPredicate);
    }
}
