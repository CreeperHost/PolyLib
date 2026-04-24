package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyLevelEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Mixin(NaturalSpawner.class)
public abstract class FabricPotentialSpawnsMixin
{
    @Inject(method = "mobsAt", at = @At("RETURN"), cancellable = true)
    private static void polylib$onMobsAt(
            ServerLevel level, StructureManager structureManager, ChunkGenerator generator,
            MobCategory category, BlockPos pos, Holder<Biome> biome,
            CallbackInfoReturnable<WeightedList<MobSpawnSettings.SpawnerData>> cir)
    {
        List<Weighted<MobSpawnSettings.SpawnerData>> original = cir.getReturnValue().unwrap();
        Map<MobSpawnSettings.SpawnerData, Integer> originalWeights = new IdentityHashMap<>();
        List<MobSpawnSettings.SpawnerData> mutableList = new ArrayList<>(original.size());
        for (Weighted<MobSpawnSettings.SpawnerData> w : original)
        {
            mutableList.add(w.value());
            originalWeights.put(w.value(), w.weight());
        }

        PolyLevelEvents.POTENTIAL_SPAWNS.invoker().onPotentialSpawns(level, category, pos, mutableList);

        // Rebuild only if handlers modified the list
        boolean changed = mutableList.size() != original.size();
        if (!changed)
        {
            for (int i = 0; i < mutableList.size(); i++)
            {
                if (mutableList.get(i) != original.get(i).value()) { changed = true; break; }
            }
        }
        if (!changed) return;

        List<Weighted<MobSpawnSettings.SpawnerData>> rebuilt = new ArrayList<>(mutableList.size());
        for (MobSpawnSettings.SpawnerData sd : mutableList)
        {
            int weight = originalWeights.getOrDefault(sd, 10);
            rebuilt.add(new Weighted<>(sd, weight));
        }
        cir.setReturnValue(WeightedList.of(rebuilt));
    }
}
