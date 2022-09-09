package net.creeperhost.polylib.registry;

import dev.architectury.registry.level.biome.BiomeModifications;
import net.creeperhost.polylib.mixins.InvokerSpawnPlacements;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class SpawnRegistry
{
    @Deprecated(forRemoval = true, since = "1.20")
    public static void registerSpawn(Supplier<EntityType<?>> entityType, Predicate<BiomeModifications.BiomeContext> predicate, SpawnPlacements.SpawnPredicate<?> spawnPredicate, int minCluster, int maxCluster, int weight)
    {
        BiomeModifications.addProperties(predicate, (biomeContext, mutable) -> mutable.getSpawnProperties().addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(entityType.get(), minCluster, maxCluster, weight)));

        registerSpawnPlacement(entityType, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPredicate);
    }

    public static void registerSpawnPlacement(Supplier<EntityType<?>> entityType, SpawnPlacements.Type type, Heightmap.Types types, SpawnPlacements.SpawnPredicate<?> spawnPredicate)
    {
        InvokerSpawnPlacements.callRegister(entityType.get(), type, types, spawnPredicate);
    }
}
