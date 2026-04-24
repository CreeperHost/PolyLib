package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.server.MinecraftServer;
import java.util.List;

public final class PolyLevelEvents
{
    public static final PolyEvent<Load> LEVEL_LOAD = PolyEvent.create(handlers -> level -> handlers.forEach(h -> h.onLoad(level)));
    public static final PolyEvent<Unload> LEVEL_UNLOAD = PolyEvent.create(handlers -> level -> handlers.forEach(h -> h.onUnload(level)));
    public static final PolyEvent<Save> LEVEL_SAVE = PolyEvent.create(handlers -> level -> handlers.forEach(h -> h.onSave(level)));

    /**
     * Fired when players finish sleeping and the day advances.
     * Informational only.
     * <p>
     * NeoForge: {@code SleepFinishedTimeEvent}<br>
     * Fabric: mixin into {@code ServerLevel#updateSleepingPlayerList}
     */
    public static final PolyEvent<SleepFinished> SLEEP_FINISHED = PolyEvent.create(
            handlers -> level -> handlers.forEach(h -> h.onSleepFinished(level)));

    // ── Tier 8 ────────────────────────────────────────────────────────────────

    /**
     * Fired when a game rule value changes. Informational.
     * <p>
     * NeoForge: {@code GameRuleChangedEvent} (MOD bus)<br>
     * Fabric: mixin on {@code GameRule#set}
     */
    public static final PolyEvent<GameRuleChange> GAME_RULE_CHANGE = PolyEvent.create(
            handlers -> (server, rule, rules) -> handlers.forEach(h -> h.onGameRuleChange(server, rule, rules)));

    /**
     * Fired when the world difficulty changes. Informational.
     * <p>
     * NeoForge: {@code DifficultyChangeEvent} (GAME bus)<br>
     * Fabric: mixin on {@code MinecraftServer#setDifficulty}
     */
    public static final PolyEvent<DifficultyChange> DIFFICULTY_CHANGE = PolyEvent.create(
            handlers -> (difficulty, oldDifficulty) -> handlers.forEach(h -> h.onDifficultyChange(difficulty, oldDifficulty)));

    // ── Tier 9 ────────────────────────────────────────────────────────────────

    /**
     * Fired when gathering potential spawn entries for a chunk. Modify the list to add/remove spawns.
     * <p>
     * NeoForge: {@code LevelEvent.PotentialSpawns}<br>
     * Fabric: mixin on {@code NaturalSpawner#getFilteredSpawnableEntities}
     */
    public static final PolyEvent<PotentialSpawns> POTENTIAL_SPAWNS = PolyEvent.create(
            handlers -> (level, category, pos, spawns) ->
                    handlers.forEach(h -> h.onPotentialSpawns(level, category, pos, spawns)));

    private PolyLevelEvents() {}

    @FunctionalInterface
    public interface Load
    {
        void onLoad(ServerLevel level);
    }

    @FunctionalInterface
    public interface Unload
    {
        void onUnload(ServerLevel level);
    }

    @FunctionalInterface
    public interface Save
    {
        void onSave(ServerLevel level);
    }

    @FunctionalInterface
    public interface SleepFinished
    {
        void onSleepFinished(ServerLevel level);
    }

    @FunctionalInterface
    public interface GameRuleChange
    {
        void onGameRuleChange(MinecraftServer server, GameRule<?> rule, GameRules rules);
    }

    @FunctionalInterface
    public interface DifficultyChange
    {
        void onDifficultyChange(Difficulty difficulty, Difficulty oldDifficulty);
    }

    @FunctionalInterface
    public interface PotentialSpawns
    {
        /** {@code spawns} is a mutable list — add/remove {@link MobSpawnSettings.SpawnerData} entries. */
        void onPotentialSpawns(ServerLevel level, MobCategory category, BlockPos pos,
                               List<MobSpawnSettings.SpawnerData> spawns);
    }
}
