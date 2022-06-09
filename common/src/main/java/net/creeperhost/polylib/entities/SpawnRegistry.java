package net.creeperhost.polylib.entities;

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
    public static void registerSpawn(Supplier<EntityType<?>> entityType, Predicate<BiomeModifications.BiomeContext> predicate, SpawnPlacements.SpawnPredicate<?> spawnPredicate, int minCluster, int maxCluster, int weight)
    {
            BiomeModifications.addProperties(predicate, (biomeContext, mutable) -> mutable.getSpawnProperties().addSpawn(MobCategory.MONSTER,
                    new MobSpawnSettings.SpawnerData(entityType.get(), minCluster, maxCluster, weight)));

            InvokerSpawnPlacements.callRegister(entityType.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPredicate);
    }

    public static class Defaults
    {
        public static boolean checkAnimalSpawnRules(EntityType<?> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource random)
        {
            if (!levelAccessor.getBlockState(blockPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON)) return false;
            return isBrightEnoughToSpawn(levelAccessor, blockPos);
        }

        public static boolean isBrightEnoughToSpawn(BlockAndTintGetter blockAndTintGetter, BlockPos blockPos)
        {
            return blockAndTintGetter.getRawBrightness(blockPos, 0) > 8;
        }
    }
}
