package net.creeperhost.testmod.init;

import net.creeperhost.polylib.event.events.server.PolyBlockEntityEvents;
import net.creeperhost.polylib.event.events.server.PolyBlockEvents;
import net.creeperhost.polylib.event.events.server.PolyBrewingEvents;
import net.creeperhost.polylib.event.events.server.PolyChatEvents;
import net.creeperhost.polylib.event.events.server.PolyChunkEvents;
import net.creeperhost.polylib.event.events.server.PolyDataEvents;
import net.creeperhost.polylib.event.events.server.PolyEnchantEvents;
import net.creeperhost.polylib.event.events.server.PolyEntityEvents;
import net.creeperhost.polylib.event.events.server.PolyExplosionEvents;
import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.creeperhost.polylib.event.events.server.PolyLevelEvents;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.creeperhost.polylib.event.events.server.PolyMobEffectEvents;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.creeperhost.polylib.event.events.server.PolyPlayerTickEvents;
import net.creeperhost.polylib.event.events.server.PolyServerCommandEvents;
import net.creeperhost.polylib.event.events.server.PolyServerLifecycleEvents;
import net.creeperhost.polylib.event.events.server.PolyServerTickEvents;
import net.creeperhost.polylib.event.events.server.PolySleepEvents;
import net.creeperhost.polylib.event.events.server.PolySoundEvents;
import net.creeperhost.polylib.event.events.server.PolySpawnEvents;
import net.creeperhost.polylib.event.events.server.PolyRegistryEvents;
import net.creeperhost.polylib.event.events.server.PolyVillageEvents;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataManager;
import net.creeperhost.testmod.TestModCommon;
import static net.creeperhost.testmod.TestModCommon.LOGGER;

public class TestEvents
{
    private static boolean testEvents = false;

    public static void init()
    {
        if (!testEvents) return;
        // --- Existing player events ---

        PolyPlayerEvents.LOGIN.register(player -> LOGGER.info("[TestMod] {} joined. Ticks played: {}", player.getScoreboardName(), PlayerServerDataManager.get(player, TestModCommon.TICKS_PLAYED)));

        PolyPlayerEvents.LOGOUT.register(player -> LOGGER.info("[TestMod] {} left the server.", player.getScoreboardName()));

        PolyPlayerEvents.RESPAWN.register((player, endConquered) -> LOGGER.info("[TestMod] {} respawned (endConquered={}).", player.getScoreboardName(), endConquered));

        PolyPlayerEvents.START_TRACKING.register((tracked, tracker) -> LOGGER.info("[TestMod] {} is now tracking {}.", tracker.getScoreboardName(), tracked.getScoreboardName()));

        // --- New player events ---

        PolyPlayerEvents.DEATH.register((player, source, ctx) -> LOGGER.info("[TestMod] {} is dying from {}.", player.getScoreboardName(), source.typeHolder().unwrapKey().map(k -> k.identifier().toString()).orElse("unknown")));

        PolyPlayerEvents.ATTACK_ENTITY.register((player, target) -> LOGGER.info("[TestMod] {} attacked {}.", player.getScoreboardName(), target.getType().toShortString()));

        PolyPlayerEvents.BREAK_BLOCK.register((player, level, pos, state) -> LOGGER.info("[TestMod] {} broke {} at {}.", player.getScoreboardName(), state.getBlock().getDescriptionId(), pos));

        PolyPlayerEvents.CLONE.register((newPlayer, oldPlayer, isWasDeath) -> LOGGER.info("[TestMod] {} cloned from {} (death={}).", newPlayer.getScoreboardName(), oldPlayer.getScoreboardName(), isWasDeath));

        // --- Server lifecycle ---

        PolyServerLifecycleEvents.SERVER_STARTED.register(server -> LOGGER.info("[TestMod] Server started: {}", server.getServerVersion()));

        PolyServerLifecycleEvents.SERVER_STOPPING.register(server -> LOGGER.info("[TestMod] Server stopping."));

        PolyServerLifecycleEvents.SERVER_STOPPED.register(server -> LOGGER.info("[TestMod] Server stopped."));

        // --- Server tick (sample: log every 1200 ticks) ---

        PolyServerTickEvents.TICK_END.register(server ->
        {
            if (server.getTickCount() % 1200 == 0)
            {
                LOGGER.info("[TestMod] Server tick {}", server.getTickCount());
            }
        });

        // --- Level events ---

        PolyLevelEvents.LEVEL_LOAD.register(level -> LOGGER.info("[TestMod] Level loaded: {}", level.dimension().identifier()));

        PolyLevelEvents.LEVEL_UNLOAD.register(level -> LOGGER.info("[TestMod] Level unloaded: {}", level.dimension().identifier()));

        PolyLevelEvents.LEVEL_SAVE.register(level -> LOGGER.info("[TestMod] Level saved: {}", level.dimension().identifier()));

        // --- Chunk events (sample: log first 5 chunks) ---

        PolyChunkEvents.CHUNK_LOAD.register((level, chunk) ->
        {
            LOGGER.info("[TestMod] Chunk loaded: {} in {}", chunk.getPos(), level.dimension().identifier());
        });

        // --- Living events ---

        PolyLivingEvents.DEATH.register((entity, source, ctx) -> LOGGER.info("[TestMod] {} died from {}.", entity.getType().toShortString(), source.typeHolder().unwrapKey().map(k -> k.identifier().toString()).orElse("unknown")));

        PolyLivingEvents.XP_DROP.register((entity, amount) -> LOGGER.info("[TestMod] {} dropped {} XP.", entity.getType().toShortString(), amount));

        // --- DCH living events ---

        PolyLivingEvents.DAMAGE_PRE.register((entity, source, amount, ctx) -> LOGGER.info("[TestMod] {} incoming damage {} from {} (pre-armor, cancellable).", entity.getType().toShortString(), amount, source.typeHolder().unwrapKey().map(k -> k.identifier().toString()).orElse("?")));

        PolyLivingEvents.DAMAGE_POST.register((entity, source, amount) -> LOGGER.info("[TestMod] {} took {} damage from {} (post-armor).", entity.getType().toShortString(), amount, source.typeHolder().unwrapKey().map(k -> k.identifier().toString()).orElse("?")));

        PolyLivingEvents.DAMAGE_FINAL_PRE.register((entity, source, amount, ctx) -> LOGGER.info("[TestMod] {} final damage {} from {} (post-armor, pre-apply).", entity.getType().toShortString(), amount, source.typeHolder().unwrapKey().map(k -> k.identifier().toString()).orElse("?")));

        PolyLivingEvents.DROPS.register((entity, source, drops) -> LOGGER.info("[TestMod] {} dropped {} items on death.", entity.getType().toShortString(), drops.size()));

        PolyLivingEvents.FALL.register((entity, distance, multiplier) -> LOGGER.info("[TestMod] {} fell {} blocks (multiplier={}).", entity.getType().toShortString(), distance, multiplier));

        // --- DCH player events (new) ---

        PolyPlayerEvents.ENTITY_INTERACT.register((player, target, hand) -> LOGGER.info("[TestMod] {} interacted with {} using {}.", player.getScoreboardName(), target.getType().toShortString(), hand.name()));

        PolyPlayerEvents.ITEM_TOSS.register((player, item) -> LOGGER.info("[TestMod] {} tossed {}.", player.getScoreboardName(), item.getItem().getItem().getDescriptionId()));

        PolyPlayerEvents.ITEM_PICKUP.register((player, item, ctx) -> LOGGER.info("[TestMod] {} picking up {}.", player.getScoreboardName(), item.getItem().getItem().getDescriptionId()));

        PolyPlayerEvents.XP_PICKUP.register((player, orb) -> LOGGER.info("[TestMod] {} picking up {} XP.", player.getScoreboardName(), orb.getValue()));

        // --- DCH entity events ---

        PolyEntityEvents.JOIN_LEVEL.register((entity, level, ctx) -> LOGGER.info("[TestMod] {} joining level {}.", entity.getType().toShortString(), level.dimension().identifier()));

        PolyEntityEvents.ENTITY_TICK.register(entity ->
        {
            // Only log at very low rate to avoid spam: 1 in 10000 ticks per entity type
            if (entity.tickCount % 10000 == 0)
            {
                LOGGER.info("[TestMod] ENTITY_TICK fired for {}.", entity.getType().toShortString());
            }
        });

        // --- Container events ---

        PolyPlayerEvents.CONTAINER_CLOSE.register((player, container) ->
                LOGGER.info("[TestMod] {} closed container {}.", player.getScoreboardName(), container.getClass().getSimpleName()));

        // --- Sound events ---

        PolySoundEvents.ENTITY_SOUND.register((entity, sound, source, volume, pitch, ctx) ->
                LOGGER.info("[TestMod] Sound {} at {} (source={} vol={} pitch={}).",
                        sound.unwrapKey().map(k -> k.identifier().toString()).orElse("?"),
                        entity.getType().toShortString(), source.getName(), volume, pitch));

        // --- Tier 1: Player tick (rate-limited) ---

        PolyPlayerTickEvents.PLAYER_TICK_START.register(player ->
        {
            if (player.tickCount % 1200 == 0)
                LOGGER.info("[TestMod] PLAYER_TICK_START: {} at tick {}", player.getScoreboardName(), player.tickCount);
        });

        PolyPlayerTickEvents.PLAYER_TICK_END.register(player ->
        {
            if (player.tickCount % 1200 == 0)
                LOGGER.info("[TestMod] PLAYER_TICK_END: {} at tick {}", player.getScoreboardName(), player.tickCount);
        });

        // --- Tier 1: Block events ---

        PolyBlockEvents.BREAK.register((player, level, pos, state, ctx) ->
                LOGGER.info("[TestMod] BLOCK_BREAK: {} broke {} at {}", player.getScoreboardName(), state.getBlock().getDescriptionId(), pos));

        PolyBlockEvents.PLACE.register((entity, level, pos, state, ctx) ->
                LOGGER.info("[TestMod] BLOCK_PLACE: {} placed {} at {}", entity == null ? "unknown" : entity.getType().toShortString(), state.getBlock().getDescriptionId(), pos));

        PolyBlockEvents.CROP_GROW_PRE.register((level, pos, state, ctx) ->
                LOGGER.info("[TestMod] CROP_GROW_PRE: {} at {}", state.getBlock().getDescriptionId(), pos));

        PolyBlockEvents.CROP_GROW_POST.register((level, pos, state) ->
                LOGGER.info("[TestMod] CROP_GROW_POST: {} at {}", state.getBlock().getDescriptionId(), pos));

        PolyBlockEvents.FARMLAND_TRAMPLE.register((entity, level, pos, ctx) ->
                LOGGER.info("[TestMod] FARMLAND_TRAMPLE: {} trampled farmland at {}", entity.getType().toShortString(), pos));

        // --- Tier 1: Living events ---

        PolyLivingEvents.HEAL.register((entity, amount, ctx) ->
                LOGGER.info("[TestMod] LIVING_HEAL: {} healed {} hp", entity.getType().toShortString(), amount));

        PolyLivingEvents.CHANGE_TARGET.register((entity, newTarget, ctx) ->
                LOGGER.info("[TestMod] CHANGE_TARGET: {} -> {}", entity.getType().toShortString(), newTarget != null ? newTarget.getType().toShortString(): "No new target"));

        PolyLivingEvents.CONVERSION_PRE.register((entity, ctx) ->
                LOGGER.info("[TestMod] CONVERSION_PRE: {} is about to convert", entity.getType().toShortString()));

        PolyLivingEvents.CONVERSION_POST.register((original, converted) ->
                LOGGER.info("[TestMod] CONVERSION_POST: {} converted to {}", original.getType().toShortString(), converted.getType().toShortString()));

        PolyLivingEvents.KNOCKBACK.register((entity, strength, ratioX, ratioZ, ctx) ->
                LOGGER.info("[TestMod] KNOCKBACK: {} strength={}", entity.getType().toShortString(), strength));

        PolyLivingEvents.USE_TOTEM.register((entity, source, ctx) ->
                LOGGER.info("[TestMod] USE_TOTEM: {} used totem against {}", entity.getType().toShortString(),
                        source.typeHolder().unwrapKey().map(k -> k.identifier().toString()).orElse("?")));

        // --- Tier 1: Mob effect events ---

        PolyMobEffectEvents.ALLOW_APPLY.register((entity, effectInstance, ctx) ->
                LOGGER.info("[TestMod] MOB_EFFECT_ALLOW_APPLY: {} on {}", effectInstance.getEffect().unwrapKey().map(k -> k.identifier().toString()).orElse("?"), entity.getType().toShortString()));

        PolyMobEffectEvents.APPLIED.register((entity, effectInstance) ->
                LOGGER.info("[TestMod] MOB_EFFECT_APPLIED: {} on {}", effectInstance.getEffect().unwrapKey().map(k -> k.identifier().toString()).orElse("?"), entity.getType().toShortString()));

        PolyMobEffectEvents.REMOVED.register((entity, effectInstance) ->
                LOGGER.info("[TestMod] MOB_EFFECT_REMOVED: {} from {}", effectInstance.getEffect().unwrapKey().map(k -> k.identifier().toString()).orElse("?"), entity.getType().toShortString()));

        PolyMobEffectEvents.EXPIRED.register((entity, effectInstance) ->
                LOGGER.info("[TestMod] MOB_EFFECT_EXPIRED: {} on {}", effectInstance.getEffect().unwrapKey().map(k -> k.identifier().toString()).orElse("?"), entity.getType().toShortString()));

        // --- Tier 1: Entity mount ---

        PolyEntityEvents.MOUNT.register((entity, vehicle, isMounting, ctx) ->
                LOGGER.info("[TestMod] ENTITY_MOUNT: {} {} {}", entity.getType().toShortString(), isMounting ? "mounting" : "dismounting", vehicle.getType().toShortString()));

        // --- Tier 1: Explosion events ---

        PolyExplosionEvents.START.register((level, ctx) ->
                LOGGER.info("[TestMod] EXPLOSION_START in {}", level.dimension().identifier()));

        PolyExplosionEvents.DETONATE.register((level, blocks, entities) ->
                LOGGER.info("[TestMod] EXPLOSION_DETONATE: {} blocks, {} entities affected", blocks.size(), entities.size()));

        PolyExplosionEvents.KNOCKBACK.register((level, entity, velocity) ->
                LOGGER.info("[TestMod] EXPLOSION_KNOCKBACK: {} velocity={}", entity.getType().toShortString(), velocity));

        // =====================================================================
        // Tier 2 event log registrations
        // =====================================================================

        // --- Tier 2: Entity events ---

        PolyEntityEvents.LEAVE_LEVEL.register((entity, level) ->
                LOGGER.info("[TestMod] LEAVE_LEVEL: {} left {}", entity.getType().toShortString(), level.dimension().identifier()));

        PolyEntityEvents.TELEPORT.register((entity, target, ctx) ->
                LOGGER.info("[TestMod] TELEPORT: {} -> ({}, {}, {})", entity.getType().toShortString(), target[0], target[1], target[2]));

        PolyEntityEvents.TRAVEL_DIMENSION.register((entity, destination, ctx) ->
                LOGGER.info("[TestMod] TRAVEL_DIMENSION: {} -> {}", entity.getType().toShortString(), destination.identifier()));

        PolyEntityEvents.MOB_GRIEFING.register((entity, ctx) ->
                LOGGER.info("[TestMod] MOB_GRIEFING: {} canGrief={}", entity.getType().toShortString(), ctx.canGrief()));

        PolyEntityEvents.PROJECTILE_IMPACT.register((projectile, hitResult, ctx) ->
                LOGGER.info("[TestMod] PROJECTILE_IMPACT: {} hitType={}", projectile.getType().toShortString(), hitResult.getType().name()));

        PolyEntityEvents.STRUCK_BY_LIGHTNING.register((entity, lightning, ctx) ->
                LOGGER.info("[TestMod] STRUCK_BY_LIGHTNING: {} struck", entity.getType().toShortString()));

        // --- Tier 2: Player events ---

        PolyPlayerEvents.CHANGE_DIMENSION.register((player, from, to) ->
                LOGGER.info("[TestMod] CHANGE_DIMENSION: {} {} -> {}", player.getScoreboardName(), from.identifier(), to.identifier()));

        PolyPlayerEvents.CHANGE_GAME_MODE.register((player, current, next, ctx) ->
                LOGGER.info("[TestMod] CHANGE_GAME_MODE: {} {} -> {}", player.getScoreboardName(), current.getName(), next.getName()));

        PolyPlayerEvents.XP_CHANGE.register((player, amount, ctx) ->
                LOGGER.info("[TestMod] XP_CHANGE: {} +{} xp", player.getScoreboardName(), amount));

        PolyPlayerEvents.XP_LEVEL_CHANGE.register((player, levels, ctx) ->
                LOGGER.info("[TestMod] XP_LEVEL_CHANGE: {} {}levels", player.getScoreboardName(), levels));

        PolyPlayerEvents.USE_ITEM.register((player, hand, stack, ctx) ->
                LOGGER.info("[TestMod] USE_ITEM: {} used {} in {}", player.getScoreboardName(), stack.getItem().getDescriptionId(), hand.name()));

        PolyPlayerEvents.RIGHT_CLICK_BLOCK.register((player, hand, pos, face, ctx) ->
                LOGGER.info("[TestMod] RIGHT_CLICK_BLOCK: {} clicked {} face={}", player.getScoreboardName(), pos, face.name()));

        PolyPlayerEvents.ANIMAL_TAME.register((animal, player, ctx) ->
                LOGGER.info("[TestMod] ANIMAL_TAME: {} taming {}", player.getScoreboardName(), animal.getType().toShortString()));

        PolyPlayerEvents.ITEM_CRAFTED.register((player, crafted, grid) ->
                LOGGER.info("[TestMod] ITEM_CRAFTED: {} crafted {}", player.getScoreboardName(), crafted.getItem().getDescriptionId()));

        PolyPlayerEvents.SET_SPAWN.register((player, pos, dimension, forced, ctx) ->
                LOGGER.info("[TestMod] SET_SPAWN: {} pos={} dim={} forced={}", player.getScoreboardName(), pos, dimension.identifier(), forced));

        // --- Tier 2: Spawn events ---

        PolySpawnEvents.FINALIZE_SPAWN.register((mob, level, ctx) ->
                LOGGER.info("[TestMod] FINALIZE_SPAWN: {}", mob.getType().toShortString()));

        PolySpawnEvents.MOB_DESPAWN.register((mob, ctx) ->
                LOGGER.info("[TestMod] MOB_DESPAWN: {} (result={})", mob.getType().toShortString(), ctx.getResult()));

        // --- Tier 2: Data events ---

        PolyDataEvents.TAGS_UPDATED.register(provider ->
                LOGGER.info("[TestMod] TAGS_UPDATED: tag data reloaded."));

        // --- Tier 2: Combat events ---

        PolyLivingEvents.CRITICAL_HIT.register((player, target, ctx) ->
                LOGGER.info("[TestMod] CRITICAL_HIT: {} on {} isCrit={} mult={}", player.getScoreboardName(), target.getType().toShortString(), ctx.isCritical(), ctx.getDamageMultiplier()));

        PolyLivingEvents.ARROW_NOCK.register((player, bow, hand, ctx) ->
                LOGGER.info("[TestMod] ARROW_NOCK: {} drawing {} in {}", player.getScoreboardName(), bow.getItem().getDescriptionId(), hand.name()));

        PolyLivingEvents.ARROW_LOOSE.register((player, bow, charge, ctx) ->
                LOGGER.info("[TestMod] ARROW_LOOSE: {} released bow charge={}", player.getScoreboardName(), charge));

        PolyLivingEvents.SHIELD_BLOCK.register((entity, source, blocked, ctx) ->
                LOGGER.info("[TestMod] SHIELD_BLOCK: {} blocked {} damage={}", entity.getType().toShortString(),
                        source.typeHolder().unwrapKey().map(k -> k.identifier().toString()).orElse("?"), blocked));

        // --- Tier 2: Sleep events ---

        PolySleepEvents.START_SLEEPING.register((entity, pos) ->
                LOGGER.info("[TestMod] START_SLEEPING: {} at {}", entity.getType().toShortString(), pos));

        PolySleepEvents.STOP_SLEEPING.register((entity, pos) ->
                LOGGER.info("[TestMod] STOP_SLEEPING: {} woke up (was at {})", entity.getType().toShortString(), pos));
        PolySleepEvents.ALLOW_SLEEPING.register((player, pos, ctx) ->
                LOGGER.info("[TestMod] ALLOW_SLEEPING: {} at {} problem={}", player.getScoreboardName(), pos, ctx.getProblem()));

        // =====================================================================
        // Tier 3 events
        // =====================================================================

        // --- Tier 3: Living entity events ---

        PolyLivingEvents.ENTITY_EQUIP_CHANGE.register((entity, slot, from, to) ->
                LOGGER.info("[TestMod] ENTITY_EQUIP_CHANGE: {} slot={} from={} to={}", entity.getType().toShortString(), slot.name(), from.getItem().getDescriptionId(), to.getItem().getDescriptionId()));

        PolyLivingEvents.ITEM_USE_START.register((entity, item, ctx) ->
                LOGGER.info("[TestMod] ITEM_USE_START: {} using {}", entity.getType().toShortString(), item.getItem().getDescriptionId()));

        PolyLivingEvents.ITEM_USE_FINISH.register((entity, item, resultHolder) ->
                LOGGER.info("[TestMod] ITEM_USE_FINISH: {} finished using {}", entity.getType().toShortString(), item.getItem().getDescriptionId()));

        PolyLivingEvents.MOB_SPLIT.register((parent, ctx) ->
                LOGGER.info("[TestMod] MOB_SPLIT: {}", parent.getType().toShortString()));

        PolyLivingEvents.BABY_SPAWN.register((parentA, parentB, causedBy, child, ctx) ->
                LOGGER.info("[TestMod] BABY_SPAWN: {} + {}", parentA.getType().toShortString(), parentB.getType().toShortString()));

        PolyLivingEvents.DROWN.register((entity, ctx) ->
                LOGGER.info("[TestMod] DROWN: {}", entity.getType().toShortString()));

        PolyLivingEvents.BREATHE.register((entity, ctx) ->
                LOGGER.debug("[TestMod] BREATHE: {} canBreathe={}", entity.getType().toShortString(), ctx.canBreathe()));

        PolyLivingEvents.DESTROY_BLOCK.register((entity, level, pos, state, ctx) ->
                LOGGER.info("[TestMod] DESTROY_BLOCK: {} at {}", entity.getType().toShortString(), pos));

        PolyLivingEvents.GET_PROJECTILE.register((entity, resultHolder) ->
                LOGGER.debug("[TestMod] GET_PROJECTILE: {} -> {}", entity.getType().toShortString(), resultHolder[0].getItem().getDescriptionId()));

        PolyLivingEvents.SWAP_ITEMS.register((entity, toMain, toOff, ctx) ->
                LOGGER.info("[TestMod] SWAP_ITEMS: {} mainHand={} offHand={}", entity.getType().toShortString(), toMain.getItem().getDescriptionId(), toOff.getItem().getDescriptionId()));

        // --- Tier 3: Item events ---

        PolyItemEvents.ITEM_EXPIRE.register(item ->
                LOGGER.info("[TestMod] ITEM_EXPIRE: {}", item.getItem().getItem().getDescriptionId()));

        PolyItemEvents.ITEM_FISHING.register((player, drops, ctx) ->
                LOGGER.info("[TestMod] ITEM_FISHING: {} caught {} items", player.getScoreboardName(), drops.size()));

        PolyItemEvents.GRINDSTONE.register((top, bottom, ctx) ->
                LOGGER.info("[TestMod] GRINDSTONE: top={} bottom={} output={}", top.getItem().getDescriptionId(), bottom.getItem().getDescriptionId(), ctx.getOutput().getItem().getDescriptionId()));

        PolyItemEvents.ANVIL_UPDATE.register((left, right, name, ctx) ->
                LOGGER.info("[TestMod] ANVIL_UPDATE: left={} right={} name={} xp={}", left.getItem().getDescriptionId(), right.getItem().getDescriptionId(), name, ctx.getXpCost()));

        // --- Tier 3: Block events ---

        PolyBlockEvents.PORTAL_SPAWN.register((level, pos, state, ctx) ->
                LOGGER.info("[TestMod] PORTAL_SPAWN at {}", pos));

        PolyBlockEvents.PISTON_PRE.register((level, pos, dir, extending, ctx) ->
                LOGGER.info("[TestMod] PISTON_PRE at {} dir={} extending={}", pos, dir.name(), extending));

        PolyBlockEvents.PISTON_POST.register((level, pos, dir, extending) ->
                LOGGER.info("[TestMod] PISTON_POST at {} dir={} extending={}", pos, dir.name(), extending));

        PolyBlockEvents.NOTE_BLOCK_PLAY.register((level, pos, state, ctx) ->
                LOGGER.info("[TestMod] NOTE_BLOCK_PLAY at {}", pos));

        PolyBlockEvents.FLUID_SOURCE.register((level, pos, ctx) ->
                LOGGER.info("[TestMod] FLUID_SOURCE at {}", pos));

        // --- Tier 3: Level events ---

        PolyLevelEvents.SLEEP_FINISHED.register(level ->
                LOGGER.info("[TestMod] SLEEP_FINISHED in {}", level.dimension()));

        // =====================================================================
        // Tier 4 event log registrations — server tick / chunk / item use / commands
        // =====================================================================

        // --- Tier 4: Server tick (TICK_START + level ticks, rate-limited) ---

        PolyServerTickEvents.TICK_START.register(server ->
        {
            if (server.getTickCount() % 1200 == 0)
                LOGGER.info("[TestMod] SERVER_TICK_START: tick {}", server.getTickCount());
        });

        PolyServerTickEvents.LEVEL_TICK_START.register(level ->
        {
            if (level.getServer().getTickCount() % 6000 == 0)
                LOGGER.info("[TestMod] LEVEL_TICK_START: {}", level.dimension().identifier());
        });

        PolyServerTickEvents.LEVEL_TICK_END.register(level ->
        {
            if (level.getServer().getTickCount() % 6000 == 0)
                LOGGER.info("[TestMod] LEVEL_TICK_END: {}", level.dimension().identifier());
        });

        // --- Tier 4: Chunk unload ---

        PolyChunkEvents.CHUNK_UNLOAD.register((level, chunk) ->
                LOGGER.info("[TestMod] CHUNK_UNLOAD: {} in {}", chunk.getPos(), level.dimension().identifier()));

        // --- Tier 4: Item use lifecycle (PolyItemEvents) ---

        PolyItemEvents.ITEM_USE_START.register((entity, stack, duration, ctx) ->
                LOGGER.info("[TestMod] ITEM_USE_START: {} started using {} (duration={})", entity.getType().toShortString(), stack.getItem().getDescriptionId(), duration));

        PolyItemEvents.ITEM_USE_TICK.register((entity, stack, ticksRemaining, ctx) ->
        {
            // Only log every 20 ticks to avoid flooding
            if (ticksRemaining % 20 == 0)
                LOGGER.info("[TestMod] ITEM_USE_TICK: {} using {} ({} ticks left)", entity.getType().toShortString(), stack.getItem().getDescriptionId(), ticksRemaining);
        });

        PolyItemEvents.ITEM_USE_FINISH.register((entity, stack) ->
                LOGGER.info("[TestMod] ITEM_USE_FINISH: {} finished using {}", entity.getType().toShortString(), stack.getItem().getDescriptionId()));

        // --- Tier 4: Command registration ---

        PolyServerCommandEvents.REGISTER_COMMANDS.register((dispatcher, buildContext, selection) ->
                LOGGER.info("[TestMod] REGISTER_COMMANDS fired (selection={})", selection.name()));

        // =====================================================================
        // Tier 5 — Piston post
        // =====================================================================

        // Already registered above (PISTON_POST)

        // =====================================================================
        // Tier 7: Player extended
        // =====================================================================

        PolyPlayerEvents.BREAK_SPEED.register((player, state, pos, speed) ->
        {
            // Only log occasionally to avoid spam
        });

        PolyPlayerEvents.HARVEST_CHECK.register((player, state, result) ->
        {
            // Informational — no-op
        });

        PolyPlayerEvents.BONE_MEAL.register((player, level, pos, state, ctx) ->
                LOGGER.info("[TestMod] BONE_MEAL: {} at {}", player.getScoreboardName(), pos));

        PolyPlayerEvents.SWEEP_ATTACK.register((player, target, ctx) ->
                LOGGER.info("[TestMod] SWEEP_ATTACK: {} hit {}", player.getScoreboardName(), target.getType().toShortString()));

        PolyPlayerEvents.LEFT_CLICK_BLOCK.register((player, pos, face, ctx) ->
        {
            // Informational — no-op
        });

        PolyPlayerEvents.RIGHT_CLICK_EMPTY.register((player, hand) ->
                LOGGER.info("[TestMod] RIGHT_CLICK_EMPTY: {} hand={}", player.getScoreboardName(), hand.name()));

        PolyPlayerEvents.ADVANCEMENT_EARN.register((player, advancement) ->
                LOGGER.info("[TestMod] ADVANCEMENT_EARN: {} earned {}", player.getScoreboardName(),
                        advancement.id()));

        PolyPlayerEvents.CONTAINER_OPEN.register((player, menu) ->
                LOGGER.info("[TestMod] CONTAINER_OPEN: {} opened {}", player.getScoreboardName(),
                        menu.getClass().getSimpleName()));

        PolyPlayerEvents.WAKE_UP.register((player, updateLevel) ->
                LOGGER.info("[TestMod] WAKE_UP: {} updateLevel={}", player.getScoreboardName(), updateLevel));

        PolyPlayerEvents.ITEM_SMELTED.register((player, stack) ->
                LOGGER.info("[TestMod] ITEM_SMELTED: {} smelted {}", player.getScoreboardName(),
                        stack.getItem().getDescriptionId()));

        // =====================================================================
        // Tier 8: Chat / World / Block / Lifecycle
        // =====================================================================

        PolyChatEvents.SERVER_CHAT.register((player, message, ctx) ->
                LOGGER.info("[TestMod] SERVER_CHAT: {} sent {}", player.getScoreboardName(), message[0].getString()));

        PolyChatEvents.COMMAND_EXECUTE.register((source, command, ctx) ->
                LOGGER.info("[TestMod] COMMAND_EXECUTE: {}", command[0]));

        PolyPlayerEvents.STAT_AWARD.register((player, stat, value, ctx) ->
        {
            // Don't spam — only log kill stats
            if (stat.toString().contains("kill"))
                LOGGER.info("[TestMod] STAT_AWARD: {} stat={} value={}", player.getScoreboardName(), stat, value);
        });

        PolyLevelEvents.GAME_RULE_CHANGE.register((server, rule, rules) ->
                LOGGER.info("[TestMod] GAME_RULE_CHANGE: rule={}", rule));

        PolyLevelEvents.DIFFICULTY_CHANGE.register((difficulty, oldDifficulty) ->
                LOGGER.info("[TestMod] DIFFICULTY_CHANGE: {} -> {}", oldDifficulty.name(), difficulty.name()));

        PolyBlockEvents.BLOCK_DROPS.register((level, pos, state, drops) ->
        {
            if (!drops.isEmpty())
                LOGGER.info("[TestMod] BLOCK_DROPS: {} drops at {}", drops.size(), pos);
        });

        PolyBlockEvents.ENTITY_MULTI_PLACE.register((level, entity, placedState, ctx) ->
                LOGGER.info("[TestMod] ENTITY_MULTI_PLACE: {} placed {} (multi-block)", entity.getType().toShortString(), placedState.getBlock()));

        PolyServerLifecycleEvents.DATAPACK_SYNC.register((server, player) ->
                LOGGER.info("[TestMod] DATAPACK_SYNC: player={}", player == null ? "ALL" : player.getScoreboardName()));

        PolyServerLifecycleEvents.RELOAD_START.register((server, rm) ->
                LOGGER.info("[TestMod] RELOAD_START"));

        PolyServerLifecycleEvents.RELOAD_END.register((server, rm, success) ->
                LOGGER.info("[TestMod] RELOAD_END: success={}", success));

        // =====================================================================
        // Tier 9: Living / Mob / Spawn
        // =====================================================================

        PolyLivingEvents.SPAWN_CLUSTER_SIZE.register((mob, size) ->
        {
            // Informational — no-op
        });

        PolyLivingEvents.ENDERMAN_ANGER.register((enderman, player, ctx) ->
                LOGGER.info("[TestMod] ENDERMAN_ANGER: enderman at {} angered by {}", enderman.blockPosition(), player.getScoreboardName()));

        PolyLivingEvents.ENTITY_KILLED_OTHER.register((killer, victim) ->
                LOGGER.info("[TestMod] ENTITY_KILLED_OTHER: {} killed {}", killer.getType().toShortString(), victim.getType().toShortString()));

        PolyLivingEvents.ARMOR_HURT.register((entity, source, ctx) ->
        {
            // Informational — no-op
        });

        PolyLivingEvents.ELYTRA_ALLOW.register((entity, result) ->
        {
            // Informational — no-op
        });

        PolyPlayerEvents.TRADE_WITH_VILLAGER.register((player, offer, merchant) ->
                LOGGER.info("[TestMod] TRADE_WITH_VILLAGER: {} traded", player.getScoreboardName()));

        PolyPlayerEvents.PHANTOM_SPAWN.register((player, ctx) ->
                LOGGER.info("[TestMod] PHANTOM_SPAWN check for {}", player.getScoreboardName()));

        PolyLevelEvents.POTENTIAL_SPAWNS.register((level, category, pos, spawns) ->
        {
            // Informational — no-op
        });

        // =====================================================================
        // Tier 10: Enchanting / Fuel / Tooltip / Chunks / Player file
        // =====================================================================

        PolyEnchantEvents.GET_ENCHANT_LEVEL.register((stack, targetEnchant, enchantments) ->
        {
            // Informational — no-op
        });

        PolyEnchantEvents.ENCHANT_TABLE_LEVEL.register((stack, power, originalLevel, level) ->
        {
            // Informational — no-op
        });

        PolyItemEvents.FUEL_BURN_TIME.register((stack, burnTime) ->
        {
            // Informational — no-op
        });

        PolyPlayerEvents.BREWED_POTION.register((player, stack) ->
                LOGGER.info("[TestMod] BREWED_POTION: {} brewed {}", player.getScoreboardName(), stack.getItem().getDescriptionId()));

        PolyPlayerEvents.NAME_FORMAT.register((player, name) ->
        {
            // Informational — no-op
        });

        PolyPlayerEvents.TAB_LIST_NAME_FORMAT.register((player, name) ->
        {
            // Informational — no-op
        });

        PolyPlayerEvents.PLAYER_LOAD.register((player, uuid) ->
                LOGGER.info("[TestMod] PLAYER_LOAD: {} ({})", player.getScoreboardName(), uuid));

        PolyPlayerEvents.PLAYER_SAVE.register((player, uuid) ->
                LOGGER.info("[TestMod] PLAYER_SAVE: {} ({})", player.getScoreboardName(), uuid));

        PolyBlockEntityEvents.BLOCK_ENTITY_LOAD.register(be ->
        {
            // Very high frequency — no-op
        });

        PolyBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register(be ->
        {
            // Very high frequency — no-op
        });

        PolyChunkEvents.CHUNK_WATCH.register((player, pos, level) ->
        {
            // Very high frequency — no-op
        });

        PolyChunkEvents.CHUNK_UNWATCH.register((player, pos, level) ->
        {
            // Very high frequency — no-op
        });

        // ===================================================================
        // Tier 15
        // ===================================================================

        PolyServerLifecycleEvents.SERVER_AFTER_SETUP.register(server ->
                LOGGER.info("[TestMod] SERVER_AFTER_SETUP: server ready"));

        PolyServerLifecycleEvents.MOD_MISMATCH.register((ids, anyUnresolved) ->
                LOGGER.info("[TestMod] MOD_MISMATCH: mismatched={} anyUnresolved={}", ids, anyUnresolved));

        PolyServerLifecycleEvents.REGISTER_GAME_TESTS.register(event ->
        {
            // Informational — no-op
        });

        PolyServerLifecycleEvents.REGISTER_STRUCTURE_CONVERSIONS.register(event ->
        {
            // Informational — no-op
        });

        PolyServerLifecycleEvents.LOGIN_QUERY_START.register((listener, server, sender, syncCallback) ->
                LOGGER.info("[TestMod] LOGIN_QUERY_START"));

        PolyServerLifecycleEvents.CONFIGURATION_DISCONNECT.register((listener, server) ->
                LOGGER.info("[TestMod] CONFIGURATION_DISCONNECT"));

        PolyServerLifecycleEvents.PLAYER_NEGOTIATION.register(connection ->
                LOGGER.info("[TestMod] PLAYER_NEGOTIATION"));

        PolyEntityEvents.ENTITY_TICK_END.register(entity ->
        {
            // Very high frequency — no-op
        });

        PolySpawnEvents.MOB_SPAWN_CONTEXT.register((mob, level, pos, spawnType, difficulty) ->
                LOGGER.info("[TestMod] MOB_SPAWN_CONTEXT: {} at {}", mob.getType().getDescriptionId(), pos));

        PolyLivingEvents.MOB_KILL_XP.register((victim, killer, xp) ->
        {
            // Informational — no-op
        });

        PolyPlayerEvents.ITEM_PICKUP_POST.register((player, itemEntity, stack) ->
                LOGGER.info("[TestMod] ITEM_PICKUP_POST: {} picked up {}", player.getScoreboardName(), stack.getItem().getDescriptionId()));

        PolyRegistryEvents.REGISTRY_ID_REMAP.register(remapState ->
                LOGGER.info("[TestMod] REGISTRY_ID_REMAP: state={}", remapState));

        // ---- PolyBrewingEvents ----
        PolyBrewingEvents.BREW_PRE.register((items, ctx) ->
                LOGGER.info("[TestMod] BREW_PRE: {} slots", items.size()));

        PolyBrewingEvents.BREW_POST.register(items ->
                LOGGER.info("[TestMod] BREW_POST: brewed {} items", items.stream().filter(s -> !s.isEmpty()).count()));

        // ---- PolyChatEvents (Tier 22) ----
        PolyChatEvents.ALLOW_GAME_MESSAGE.register((server, message, overlay) ->
        {
            // Observe only — always allow
            LOGGER.info("[TestMod] ALLOW_GAME_MESSAGE: overlay={}", overlay);
            return true;
        });

        PolyChatEvents.SERVER_GAME_MESSAGE.register((server, message, overlay) ->
                LOGGER.info("[TestMod] SERVER_GAME_MESSAGE: overlay={}", overlay));

        PolyChatEvents.CHAT_DECORATE.register((player, message) ->
                LOGGER.info("[TestMod] CHAT_DECORATE: player={}", player == null ? "null" : player.getScoreboardName()));

        // ---- PolyEntityEvents (Tier 22) ----
        PolyEntityEvents.ENTITY_INVULNERABILITY_CHECK.register((entity, source, originalResult) ->
        {
            // Observe only — return original unchanged
            return originalResult;
        });

        PolyEntityEvents.ENTITY_CHANGE_LEVEL_POST.register((original, replacement, from, to) ->
                LOGGER.info("[TestMod] ENTITY_CHANGE_LEVEL_POST: {} {} -> {}",
                        replacement.getType().toShortString(),
                        from.dimension(),
                        to.dimension()));

        // ---- PolyServerLifecycleEvents (Tier 22) ----
        PolyServerLifecycleEvents.SORT_RELOAD_LISTENERS.register(event ->
                LOGGER.info("[TestMod] SORT_RELOAD_LISTENERS fired"));

        // ---- PolyVillageEvents ----
        PolyVillageEvents.SIEGE_SPAWN.register((siege, level, player, pos, ctx) ->
                LOGGER.info("[TestMod] SIEGE_SPAWN: near {} at {}",
                        player.getScoreboardName(), pos));

        // =====================================================================
        // New player events (STOP_TRACKING .. ADVANCEMENT_REVOKE)
        // =====================================================================

        PolyPlayerEvents.STOP_TRACKING.register((player, entity) ->
                LOGGER.info("[TestMod] STOP_TRACKING: {} stopped tracking {}",
                        player.getScoreboardName(), entity.getType().toShortString()));

        PolyPlayerEvents.PERMISSIONS_CHANGED.register(player ->
                LOGGER.info("[TestMod] PERMISSIONS_CHANGED: {}", player.getScoreboardName()));

        PolyPlayerEvents.CLIENT_INFORMATION_UPDATED.register((player, info) ->
                LOGGER.info("[TestMod] CLIENT_INFORMATION_UPDATED: {} locale={}",
                        player.getScoreboardName(), info.language()));

        PolyPlayerEvents.DESTROY_ITEM.register((player, stack, slot) ->
                LOGGER.info("[TestMod] DESTROY_ITEM: {} broke {} (slot={})",
                        player.getScoreboardName(), stack.getItem().getDescriptionId(),
                        slot == null ? "none" : slot.getName()));

        PolyPlayerEvents.ENCHANT_ITEM.register((player, stack, cost) ->
                LOGGER.info("[TestMod] ENCHANT_ITEM: {} enchanted {} (cost={})",
                        player.getScoreboardName(), stack.getItem().getDescriptionId(), cost));

        PolyPlayerEvents.FLYABLE_FALL.register((player, ctx) ->
                LOGGER.info("[TestMod] FLYABLE_FALL: {} fell during creative flight",
                        player.getScoreboardName()));

        // Fires every tick while sleeping — observe only to avoid log spam
        PolyPlayerEvents.CAN_CONTINUE_SLEEPING.register((player, result) ->
        {
            // no-op
        });

        PolyPlayerEvents.RESPAWN_POSITION.register((player, pos, dimension) ->
                LOGGER.info("[TestMod] RESPAWN_POSITION: {} respawning at {} in {}",
                        player.getScoreboardName(), pos[0], dimension[0].identifier()));

        PolyPlayerEvents.ADVANCEMENT_REVOKE.register((player, advancement) ->
                LOGGER.info("[TestMod] ADVANCEMENT_REVOKE: {} revoked {}",
                        player.getScoreboardName(), advancement.id()));

        // =====================================================================
        // New block events (FLUID_PLACE_BLOCK .. NOTE_BLOCK_CHANGE)
        // =====================================================================

        PolyBlockEvents.FLUID_PLACE_BLOCK.register((level, pos, liquidPos, state, ctx) ->
                LOGGER.info("[TestMod] FLUID_PLACE_BLOCK: {} at {} (liquid at {})",
                        state.getBlock().getDescriptionId(), pos, liquidPos));

        // Fires on every block update — observe only to avoid log spam
        PolyBlockEvents.NEIGHBOR_NOTIFY.register((level, pos, state, notifiedSides, forceRedstoneUpdate) ->
        {
            // no-op
        });

        PolyBlockEvents.BLOCK_GROW_FEATURE.register((level, pos, feature) ->
                LOGGER.info("[TestMod] BLOCK_GROW_FEATURE: at {}", pos));

        PolyBlockEvents.ALTER_GROUND.register((level, pos, provider) ->
                LOGGER.info("[TestMod] ALTER_GROUND: at {}", pos));

        PolyBlockEvents.NOTE_BLOCK_CHANGE.register((level, pos, oldInstrument, newInstrument, oldNote, newNote, ctx) ->
                LOGGER.info("[TestMod] NOTE_BLOCK_CHANGE: {} note {} -> instrument {} note {} at {}",
                        oldInstrument.name(), oldNote, newInstrument.name(), newNote, pos));

        // =====================================================================
        // New data event (LOOT_TABLE_LOAD)
        // =====================================================================

        PolyDataEvents.LOOT_TABLE_LOAD.register(id ->
                LOGGER.info("[TestMod] LOOT_TABLE_LOAD: {}", id));

        // =====================================================================
        // New item events (ITEM_STACKED_ON_OTHER, ANVIL_CRAFT)
        // =====================================================================

        PolyItemEvents.ITEM_STACKED_ON_OTHER.register((player, carried, target, targetSlot, carriedSlot) ->
                LOGGER.info("[TestMod] ITEM_STACKED_ON_OTHER: {} stacked {} onto {}",
                        player.getScoreboardName(), carried.getItem().getDescriptionId(),
                        target.getItem().getDescriptionId()));

        PolyItemEvents.ANVIL_CRAFT.register((player, left, right, output, cost) ->
                LOGGER.info("[TestMod] ANVIL_CRAFT: {} crafted {} from {} + {} (cost={})",
                        player.getScoreboardName(), output.getItem().getDescriptionId(),
                        left.getItem().getDescriptionId(), right.getItem().getDescriptionId(), cost));
    }
}
