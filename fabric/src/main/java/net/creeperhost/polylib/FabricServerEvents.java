package net.creeperhost.polylib;

// TODO: depends on feat/accessibility PR being merged — AccessibilityPrefsManager lives there
import net.creeperhost.polylib.accessibility.AccessibilityPrefsManager;
import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyChatEvents;
import net.creeperhost.polylib.event.events.server.PolyBlockEvents;
import net.creeperhost.polylib.event.events.server.PolyBlockEntityEvents;
import net.creeperhost.polylib.event.events.server.PolyChunkEvents;
import net.creeperhost.polylib.event.events.server.PolyEntityEvents;
import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.creeperhost.polylib.event.events.server.PolyLevelEvents;
import net.creeperhost.polylib.event.events.server.PolyLivingEvents;
import net.creeperhost.polylib.event.events.server.PolyMobEffectEvents;
import net.creeperhost.polylib.event.events.server.PolyPlayerEvents;
import net.creeperhost.polylib.event.events.server.PolyDataEvents;
import net.creeperhost.polylib.event.events.server.PolyBrewingEvents;
import net.creeperhost.polylib.event.events.server.PolyRegistryEvents;
import net.creeperhost.polylib.event.events.server.PolyVillageEvents;
import net.creeperhost.polylib.event.events.server.PolySpawnEvents;
import net.creeperhost.polylib.event.events.server.PolySleepEvents;
import net.creeperhost.polylib.event.events.server.PolyServerCommandEvents;
import net.creeperhost.polylib.event.events.server.PolyServerLifecycleEvents;
import net.creeperhost.polylib.event.events.server.PolyServerTickEvents;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataManager;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.effect.ServerMobEffectEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.dimension.v1.DimensionEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

import java.util.UUID;

/**
 * Fabric server-side lifecycle hooks for PolyLib player settings.
 * Registered in {@link PolyLibFabric#onInitialize()}.
 * Respawn/death copy is handled automatically by the {@code copyOnDeath()} flag on each AttachmentType.
 */
public final class FabricServerEvents
{
    private FabricServerEvents() {}

    public static void register()
    {
        // ----- Player events (existing) -----

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
        {
            ServerPlayer player = handler.getPlayer();
            PlayerClientSettingsManager.onPlayerLogin(player);
            PlayerServerDataManager.onPlayerLogin(player);
            PolyPlayerEvents.LOGIN.invoker().onLogin(player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
        {
            ServerPlayer player = handler.getPlayer();
            UUID uuid = player.getUUID();
            AccessibilityPrefsManager.clearPlayer(uuid);
            PlayerClientSettingsManager.onPlayerLogout(uuid);
            PlayerServerDataManager.onPlayerLogout(uuid);
            PolyPlayerEvents.LOGOUT.invoker().onLogout(player);
        });

        // ----- Player events (new) -----

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) ->
        {
            if (entity instanceof ServerPlayer sp)
            {
                CancelContext ctx = new CancelContext();
                PolyPlayerEvents.DEATH.invoker().onDeath(sp, source, ctx);
                return !ctx.isCancelled();
            }
            return true;
        });

        AttackEntityCallback.EVENT.register((player, world, hand, target, hitResult) ->
        {
            if (player instanceof ServerPlayer sp)
            {
                PolyPlayerEvents.ATTACK_ENTITY.invoker().onAttackEntity(sp, target);
            }
            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) ->
        {
            if (player instanceof ServerPlayer sp)
            {
                PolyPlayerEvents.BREAK_BLOCK.invoker().onBreakBlock(sp, world, pos, state);
            }
        });

        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
            PolyPlayerEvents.CLONE.invoker().onClone(newPlayer, oldPlayer, !alive));

        EntityTrackingEvents.START_TRACKING.register((trackedEntity, player) ->
        {
            if (trackedEntity instanceof ServerPlayer tracked)
            {
                PolyPlayerEvents.START_TRACKING.invoker().onStartTracking(tracked, player);
            }
        });

        EntityTrackingEvents.STOP_TRACKING.register((trackedEntity, player) ->
        {
            if (trackedEntity instanceof ServerPlayer tracked)
            {
                PolyPlayerEvents.STOP_TRACKING.invoker().onStopTracking(tracked, player);
            }
        });

        // ----- Server lifecycle -----

        ServerLifecycleEvents.SERVER_STARTED.register(server ->
            PolyServerLifecycleEvents.SERVER_STARTED.invoker().onStarted(server));

        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
            PolyServerLifecycleEvents.SERVER_STOPPING.invoker().onStopping(server));

        ServerLifecycleEvents.SERVER_STOPPED.register(server ->
            PolyServerLifecycleEvents.SERVER_STOPPED.invoker().onStopped(server));

        // ----- Server tick -----

        ServerTickEvents.START_SERVER_TICK.register(server ->
            PolyServerTickEvents.TICK_START.invoker().onTickStart(server));

        ServerTickEvents.END_SERVER_TICK.register(server ->
            PolyServerTickEvents.TICK_END.invoker().onTickEnd(server));

        ServerTickEvents.START_LEVEL_TICK.register(level ->
            PolyServerTickEvents.LEVEL_TICK_START.invoker().onLevelTickStart(level));

        ServerTickEvents.END_LEVEL_TICK.register(level ->
            PolyServerTickEvents.LEVEL_TICK_END.invoker().onLevelTickEnd(level));

        // ----- Level (world) events -----

        ServerLevelEvents.LOAD.register((server, level) ->
            PolyLevelEvents.LEVEL_LOAD.invoker().onLoad(level));

        ServerLevelEvents.UNLOAD.register((server, level) ->
            PolyLevelEvents.LEVEL_UNLOAD.invoker().onUnload(level));

        // LEVEL_SAVE has no native Fabric equivalent — bridged via mixin (FabricLevelSaveMixin)

        // ----- Chunk events -----

        ServerChunkEvents.CHUNK_LOAD.register((level, chunk, fromExisting) ->
            PolyChunkEvents.CHUNK_LOAD.invoker().onLoad(level, chunk));

        ServerChunkEvents.CHUNK_UNLOAD.register((level, chunk) ->
            PolyChunkEvents.CHUNK_UNLOAD.invoker().onUnload(level, chunk));

        // ----- Living entity events -----

        // Fabric fires ALLOW_DAMAGE before armor calc — matches NeoForge LivingIncomingDamageEvent.
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) ->
        {
            CancelContext ctx = new CancelContext();
            PolyLivingEvents.DAMAGE_PRE.invoker().onDamagePre(entity, source, amount, ctx);
            return !ctx.isCancelled();
        });

        // afterDamage(entity, source, damageDealt, damageTaken, killed)
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, damageDealt, damageTaken, killed) ->
            PolyLivingEvents.DAMAGE_POST.invoker().onDamagePost(entity, source, damageTaken));

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) ->
        {
            if (!(entity instanceof ServerPlayer))
            {
                CancelContext ctx = new CancelContext();
                PolyLivingEvents.DEATH.invoker().onDeath(entity, source, ctx);
                return !ctx.isCancelled();
            }
            return true;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) ->
            PolyLivingEvents.DROPS.invoker().onDrops(entity, source, java.util.Collections.emptyList()));

        // FALL and XP_DROP have no native Fabric equivalent — bridged via mixins
        // (FabricLivingEntityFallMixin, FabricLivingEntityXpMixin)

        // ----- Entity events -----

        // Note: Fabric LOAD fires synchronously when entity is added to a level.
        // Cancellation is not supported via this event; ctx.isCancelled() is ignored.
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) ->
        {
            CancelContext ctx = new CancelContext();
            PolyEntityEvents.JOIN_LEVEL.invoker().onJoinLevel(entity, level, ctx);
        });

        // ENTITY_INTERACT — right-click entity. Returns PASS so other handlers still fire.
        UseEntityCallback.EVENT.register((player, world, hand, target, hitResult) ->
        {
            if (player instanceof ServerPlayer sp)
            {
                PolyPlayerEvents.ENTITY_INTERACT.invoker().onEntityInteract(sp, target, hand);
            }
            return InteractionResult.PASS;
        });

        // ENTITY_TICK, ITEM_TOSS, ITEM_PICKUP, XP_PICKUP, DAMAGE_FINAL_PRE have no direct
        // Fabric API event — TODO: add mixin bridges in a follow-up.

        // ----- Tier 1: Block break (cancellable pre-break) -----

        // BEFORE fires before the block is broken; return false to cancel
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) ->
        {
            if (player instanceof ServerPlayer sp)
            {
                CancelContext ctx = new CancelContext();
                PolyBlockEvents.BREAK.invoker().onBreak(sp, world, pos, state, ctx);
                return !ctx.isCancelled();
            }
            return true;
        });

        // ----- Tier 1: Mob effect events -----

        ServerMobEffectEvents.ALLOW_ADD.register((effect, entity, ctx2) ->
        {
            CancelContext ctx = new CancelContext();
            PolyMobEffectEvents.ALLOW_APPLY.invoker().onAllowApply(entity, effect, ctx);
            return !ctx.isCancelled();
        });

        ServerMobEffectEvents.AFTER_ADD.register((effect, entity, ctx2) ->
            PolyMobEffectEvents.APPLIED.invoker().onApplied(entity, effect));

        ServerMobEffectEvents.BEFORE_REMOVE.register((effect, entity, ctx2) ->
            PolyMobEffectEvents.REMOVED.invoker().onRemoved(entity, effect));

        // EXPIRED: no native Fabric equivalent — bridged via FabricMobEffectExpiredMixin

        // ----- Tier 1: Mob conversion post -----

        ServerLivingEntityEvents.MOB_CONVERSION.register((original, converted, params) ->
            PolyLivingEvents.CONVERSION_POST.invoker().onConversionPost(original, converted));

        // ----- Command registration -----

        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) ->
            PolyServerCommandEvents.REGISTER_COMMANDS.invoker().onRegisterCommands(dispatcher, buildContext, selection));

        // =====================================================================
        // Tier 2: Fabric native bridges
        // =====================================================================

        // ----- Tier 2: Entity events -----

        ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) ->
            PolyEntityEvents.LEAVE_LEVEL.invoker().onLeaveLevel(entity, level));

        // TELEPORT: bridged via FabricEntityTeleportMixin (no native Fabric event)

        // TRAVEL_DIMENSION: fires AFTER the change — cancel not supported on Fabric
        ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_LEVEL.register((original, newEntity, origin, destination) ->
            PolyEntityEvents.TRAVEL_DIMENSION.invoker().onTravelDimension(
                    original, destination.dimension(), new CancelContext()));

        // MOB_GRIEFING: deferred — see TODO in FabricMobGriefingMixin stub

        // PROJECTILE_IMPACT, STRUCK_BY_LIGHTNING: mixin bridges only

        // ----- Tier 2: Player events -----

        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) ->
            PolyPlayerEvents.CHANGE_DIMENSION.invoker().onChangeDimension(
                    player, origin.dimension(), destination.dimension()));

        // CHANGE_GAME_MODE: FabricPlayerGameModeMixin
        // XP_CHANGE: FabricPlayerXpChangeMixin
        // XP_LEVEL_CHANGE: FabricPlayerXpLevelMixin

        UseItemCallback.EVENT.register((player, world, hand) ->
        {
            if (player instanceof ServerPlayer sp)
            {
                CancelContext ctx = new CancelContext();
                PolyPlayerEvents.USE_ITEM.invoker().onUseItem(sp, hand, player.getItemInHand(hand), ctx);
                if (ctx.isCancelled()) return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
        {
            if (player instanceof ServerPlayer sp)
            {
                CancelContext ctx = new CancelContext();
                PolyPlayerEvents.RIGHT_CLICK_BLOCK.invoker().onRightClickBlock(
                        sp, hand, hitResult.getBlockPos(), hitResult.getDirection(), ctx);
                if (ctx.isCancelled()) return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        // ANIMAL_TAME: FabricAnimalTameMixin
        // ITEM_CRAFTED: FabricItemCraftedMixin
        // SET_SPAWN: FabricPlayerSetSpawnMixin

        // ----- Tier 2: Spawn events -----

        // FINALIZE_SPAWN: FabricFinalizeSpawnMixin
        // MOB_DESPAWN: FabricMobDespawnMixin

        // ----- Tier 2: Data events -----

        // TAGS_UPDATED: NeoForge only; no clean Fabric equivalent (TODO)

        // LOOT_TABLE_MODIFY: deferred (LootTable.Builder API not available in this MC version)

        // ----- Tier 2: Sleep events -----

        EntitySleepEvents.START_SLEEPING.register((entity, sleepingPos) ->
        {
            if (entity instanceof net.minecraft.world.entity.LivingEntity le)
                PolySleepEvents.START_SLEEPING.invoker().onStartSleeping(le, sleepingPos);
        });

        EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) ->
        {
            if (entity instanceof net.minecraft.world.entity.LivingEntity le)
                PolySleepEvents.STOP_SLEEPING.invoker().onStopSleeping(le, sleepingPos);
        });

        EntitySleepEvents.ALLOW_SLEEPING.register((player, sleepingPos) ->
        {
            if (player instanceof ServerPlayer sp)
            {
                PolySleepEvents.SleepContext ctx = new PolySleepEvents.SleepContext(null);
                PolySleepEvents.ALLOW_SLEEPING.invoker().onAllowSleeping(sp, sleepingPos, ctx);
                return ctx.getProblem();
            }
            return null;
        });

        // ----- Tier 2: Combat events -----
        // CRITICAL_HIT: NeoForge only (TODO: Fabric mixin — complex Player#attack local capture)
        // ARROW_NOCK: FabricArrowNockMixin
        // ARROW_LOOSE: FabricArrowLooseMixin
        // SHIELD_BLOCK: FabricShieldBlockMixin

        // ===================================================================
        // Tier 7-8: Fabric native registrations
        // ===================================================================

        // LEFT_CLICK_BLOCK: AttackBlockCallback (Fabric native)
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) ->
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.LEFT_CLICK_BLOCK.invoker().onLeftClickBlock(player, pos, direction, ctx);
            return ctx.isCancelled() ? InteractionResult.FAIL : InteractionResult.PASS;
        });

        // SERVER_CHAT: FabricServerChatMixin
        // COMMAND_EXECUTE: FabricCommandExecuteMixin
        // BONE_MEAL: FabricBoneMealMixin
        // SWEEP_ATTACK: FabricSweepAttackMixin
        // ADVANCEMENT_EARN: FabricAdvancementEarnMixin
        // CONTAINER_OPEN: FabricContainerOpenMixin
        // WAKE_UP: EntitySleepEvents.STOP_SLEEPING already fires; use WAKE_UP via mixin for accuracy
        // ITEM_SMELTED: FabricItemSmeltedMixin
        // STAT_AWARD: FabricStatAwardMixin

        // DATAPACK_SYNC (Fabric native):
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
            PolyServerLifecycleEvents.DATAPACK_SYNC.invoker().onDatapackSync(
                    ((net.minecraft.server.level.ServerLevel) player.level()).getServer(), player));

        // RELOAD_START / RELOAD_END (Fabric native):
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, rm) ->
            PolyServerLifecycleEvents.RELOAD_START.invoker().onReloadStart(server, rm));

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, rm, success) ->
            PolyServerLifecycleEvents.RELOAD_END.invoker().onReloadEnd(server, rm, success));

        // DIFFICULTY_CHANGE: FabricDifficultyChangeMixin
        // GAME_RULE_CHANGE: FabricGameRuleChangeMixin
        // BLOCK_DROPS: FabricBlockDropsMixin
        // ENTITY_MULTI_PLACE: NeoForge only (no Fabric equivalent)

        // ===================================================================
        // Tier 9: Fabric native registrations
        // ===================================================================

        // SPAWN_CLUSTER_SIZE: FabricSpawnClusterSizeMixin
        // ENDERMAN_ANGER: FabricEndermanAngerMixin
        // ENTITY_KILLED_OTHER: FabricEntityKilledOtherMixin
        // ARMOR_HURT: FabricArmorHurtMixin
        // TRADE_WITH_VILLAGER: FabricTradeWithVillagerMixin
        // PHANTOM_SPAWN: FabricPhantomSpawnMixin
        // POTENTIAL_SPAWNS: FabricPotentialSpawnsMixin
        // BREAK_SPEED: FabricBreakSpeedMixin
        // HARVEST_CHECK: FabricHarvestCheckMixin

        // ELYTRA_ALLOW (Fabric native):
        EntityElytraEvents.ALLOW.register(entity ->
        {
            boolean[] result = { true };
            PolyLivingEvents.ELYTRA_ALLOW.invoker().onElytraAllow(entity, result);
            return result[0];
        });

        // ===================================================================
        // Tier 10: Fabric native registrations
        // ===================================================================

        // BLOCK_ENTITY_LOAD: FabricBlockEntityLoadMixin
        // BLOCK_ENTITY_UNLOAD: FabricBlockEntityUnloadMixin
        // FUEL_BURN_TIME: FabricFuelBurnTimeMixin
        // BREWED_POTION: FabricBrewedPotionMixin
        // NAME_FORMAT: FabricNameFormatMixin
        // TAB_LIST_NAME_FORMAT: FabricTabListNameFormatMixin
        // PLAYER_LOAD: FabricPlayerLoadMixin
        // PLAYER_SAVE: FabricPlayerSaveMixin
        // GET_ENCHANT_LEVEL: FabricGetEnchantLevelMixin
        // ENCHANT_TABLE_LEVEL: FabricEnchantTableLevelMixin
        // CHUNK_WATCH: FabricChunkWatchMixin
        // CHUNK_UNWATCH: FabricChunkWatchMixin

        // ===================================================================
        // Tier 15: Fabric native registrations
        // ===================================================================

        // SERVER_AFTER_SETUP (Fabric - use SERVER_STARTED as closest equivalent):
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                PolyServerLifecycleEvents.SERVER_AFTER_SETUP.invoker().onServerAfterSetup(server));

        // MOB_KILL_XP: FabricMobKillXpMixin (ServerLivingEntityEvents.MOB_KILL_XP absent in this Fabric API version)

        // CONFIGURATION_DISCONNECT (Fabric native):
        ServerConfigurationConnectionEvents.DISCONNECT.register((handler, server) ->
                PolyServerLifecycleEvents.CONFIGURATION_DISCONNECT.invoker()
                        .onConfigurationDisconnect(handler, server));

        // LOGIN_QUERY_START (Fabric native):
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, syncCallback) ->
                PolyServerLifecycleEvents.LOGIN_QUERY_START.invoker()
                        .onLoginQueryStart(handler, server, sender, syncCallback));

        // REGISTRY_ID_REMAP (Fabric native — register for common registries):
        net.fabricmc.fabric.api.event.registry.RegistryIdRemapCallback
                .event(net.minecraft.core.registries.BuiltInRegistries.BLOCK)
                .register(state -> PolyRegistryEvents.REGISTRY_ID_REMAP.invoker().onRegistryIdRemap(state));
        net.fabricmc.fabric.api.event.registry.RegistryIdRemapCallback
                .event(net.minecraft.core.registries.BuiltInRegistries.ITEM)
                .register(state -> PolyRegistryEvents.REGISTRY_ID_REMAP.invoker().onRegistryIdRemap(state));

        // ENTITY_TICK_END: MixinEntityTickEndFabric
        // MOB_SPAWN_CONTEXT: MixinMobSpawnContextFabric
        // ITEM_PICKUP_POST: MixinItemPickupPostFabric
        // MOD_MISMATCH: NeoForge-only (no Fabric equivalent)
        // REGISTER_GAME_TESTS: NeoForge-only
        // REGISTER_STRUCTURE_CONVERSIONS: NeoForge-only
        // PLAYER_NEGOTIATION: NeoForge-only

        // ===================================================================
        // Tier 22: Fabric native registrations
        // ===================================================================

        // ENTITY_CHANGE_LEVEL_POST (Fabric native) — fires for all entities incl. players;
        // PolyPlayerEvents.CHANGE_DIMENSION handles the player case separately.
        ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_LEVEL.register(
                (original, replacement, from, to) ->
                {
                    if (!(original instanceof net.minecraft.server.level.ServerPlayer))
                    {
                        PolyEntityEvents.ENTITY_CHANGE_LEVEL_POST.invoker()
                                .onEntityChangeLevelPost(original, replacement, from, to);
                    }
                });

        // ALLOW_GAME_MESSAGE (Fabric native)
        ServerMessageEvents.ALLOW_GAME_MESSAGE.register(
                (server, message, overlay) ->
                        PolyChatEvents.ALLOW_GAME_MESSAGE.invoker()
                                .allowGameMessage(server, message, overlay));

        // SERVER_GAME_MESSAGE (Fabric native)
        ServerMessageEvents.GAME_MESSAGE.register(
                (server, message, overlay) ->
                        PolyChatEvents.SERVER_GAME_MESSAGE.invoker()
                                .onServerGameMessage(server, message, overlay));

        // CHAT_DECORATE (Fabric native) — fires for player chat messages
        ServerMessageDecoratorEvent.EVENT.register(
                ServerMessageDecoratorEvent.CONTENT_PHASE,
                (player, message) ->
                {
                    Component[] msg = { message };
                    PolyChatEvents.CHAT_DECORATE.invoker().onChatDecorate(player, msg);
                    return msg[0];
                });

        // MODIFY_DIMENSION_ATTRIBUTES (Fabric native)
        DimensionEvents.MODIFY_ATTRIBUTES.register(
                (dimTypeHolder, builder, provider) ->
                        PolyRegistryEvents.MODIFY_DIMENSION_ATTRIBUTES.invoker()
                                .onModifyDimensionAttributes(dimTypeHolder, builder, provider));

        // ENTITY_INVULNERABILITY_CHECK: NeoForge-only (no isInvulnerableTo method in this MC version)
        // SIEGE_SPAWN: FabricVillageSiegeMixin
        // BREW_PRE / BREW_POST: FabricBrewingMixin
        // SORT_RELOAD_LISTENERS: NeoForge Mod Bus only
        // ---- New events (require mixins on Fabric) ----
        // PERMISSIONS_CHANGED: FabricPermissionsChangedMixin (PlayerList#op / deop)
        // CLIENT_INFORMATION_UPDATED: FabricClientInformationUpdatedMixin (ServerGamePacketListenerImpl#handleClientInformation)
        // DESTROY_ITEM: FabricDestroyItemMixin (LivingEntity#broadcastBreakEvent)
        // ENCHANT_ITEM: FabricEnchantItemMixin (EnchantmentMenu#clickMenuButton)
        // FLYABLE_FALL: FabricFlyableFallMixin (Player#causeFallDamage when isCreative)
        // CAN_CONTINUE_SLEEPING: FabricCanContinueSleepingMixin (Player#stopSleepInBed tick path)
        // RESPAWN_POSITION: FabricRespawnPositionMixin (ServerPlayer#findRespawnPositionAndUseSpawnBlock)
        // ADVANCEMENT_REVOKE: FabricAdvancementRevokeMixin (PlayerAdvancements#revoke)
        // FLUID_PLACE_BLOCK: FabricFluidPlaceBlockMixin (FlowingFluid#spreadTo)
        // NEIGHBOR_NOTIFY: FabricNeighborNotifyMixin (Level#updateNeighborsAt)
        // BLOCK_GROW_FEATURE: FabricBlockGrowFeatureMixin (BonemealableBlock#performBonemeal)
        // ALTER_GROUND: FabricAlterGroundMixin (AlterGroundDecorator#place)
        // NOTE_BLOCK_CHANGE: FabricNoteBlockChangeMixin (NoteBlock#use)
        // LOOT_TABLE_LOAD: FabricLootTableLoadMixin (LootDataManager loading)
        // ITEM_STACKED_ON_OTHER: FabricItemStackedOnOtherMixin (AbstractContainerMenu#doClick stack-merge)
        // ANVIL_CRAFT: FabricAnvilCraftMixin (AnvilMenu result slot onTake)
    }
}
