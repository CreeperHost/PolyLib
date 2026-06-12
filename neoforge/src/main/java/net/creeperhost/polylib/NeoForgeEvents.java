package net.creeperhost.polylib;

import net.creeperhost.polylib.accessibility.AccessibilityPrefsManager;
import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyBlockEvents;
import net.creeperhost.polylib.event.events.server.PolyEntityEvents;
import net.creeperhost.polylib.event.events.server.PolyChunkEvents;
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
import net.creeperhost.polylib.event.events.server.PolySoundEvents;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataManager;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.minecraft.util.TriState;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.level.ExplosionKnockbackEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;
import net.creeperhost.polylib.event.events.server.PolyDataEvents;
import net.creeperhost.polylib.event.events.server.PolySpawnEvents;
import net.creeperhost.polylib.event.events.server.PolySleepEvents;
import net.creeperhost.polylib.event.events.server.PolyItemEvents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.GrindstoneEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingDestroyBlockEvent;
import net.neoforged.neoforge.event.entity.living.LivingDrownEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;
import net.neoforged.neoforge.event.entity.living.LivingSwapItemsEvent;
import net.neoforged.neoforge.event.entity.living.MobSplitEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.level.NoteBlockEvent;
import net.neoforged.neoforge.event.level.PistonEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.level.block.CreateFluidSourceEvent;
// ── T7-T10 imports ────────────────────────────────────────────────────────────
import net.creeperhost.polylib.event.events.server.PolyChatEvents;
import net.creeperhost.polylib.event.events.server.PolyBlockEntityEvents;
import net.creeperhost.polylib.event.events.server.PolyEnchantEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.event.DifficultyChangeEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.StatAwardEvent;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.living.ArmorHurtEvent;
import net.neoforged.neoforge.event.entity.living.EnderManAngerEvent;
import net.neoforged.neoforge.event.entity.living.SpawnClusterSizeEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerRespawnPositionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSpawnPhantomsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent;
import net.neoforged.neoforge.event.entity.player.TradeWithVillagerEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.level.GameRuleChangedEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
// ── T15 imports ───────────────────────────────────────────────────────────────
import net.creeperhost.polylib.event.events.server.PolySpawnEvents;
import net.neoforged.neoforge.event.RegisterStructureConversionsEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerNegotiationEvent;
// ── T22 imports ───────────────────────────────────────────────────────────────
import net.creeperhost.polylib.event.events.server.PolyBrewingEvents;
import net.creeperhost.polylib.event.events.server.PolyVillageEvents;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.brewing.PotionBrewEvent;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.village.VillageSiegeEvent;
// ── New event imports ─────────────────────────────────────────────────────────
import net.neoforged.neoforge.event.entity.player.PermissionsChangedEvent;
import net.neoforged.neoforge.event.entity.player.ClientInformationUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerDestroyItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerFlyableFallEvent;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.entity.player.AnvilCraftEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.event.level.AlterGroundEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * NeoForge server-side game-bus event handlers for PolyLib lifecycle hooks.
 */
@EventBusSubscriber(modid = Constants.MOD_ID)
public class NeoForgeEvents
{
    // -------------------------------------------------------------------------
    // Player events (existing)
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PlayerClientSettingsManager.onPlayerLogin(sp);
            PlayerServerDataManager.onPlayerLogin(sp);
            PolyPlayerEvents.LOGIN.invoker().onLogin(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            UUID uuid = sp.getUUID();
            AccessibilityPrefsManager.clearPlayer(uuid);
            PlayerClientSettingsManager.onPlayerLogout(uuid);
            PlayerServerDataManager.onPlayerLogout(uuid);
            PolyPlayerEvents.LOGOUT.invoker().onLogout(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PlayerClientSettingsManager.onPlayerRespawn(sp.getUUID(), sp);
            PlayerServerDataManager.onPlayerRespawn(sp.getUUID(), sp);
            PolyPlayerEvents.RESPAWN.invoker().onRespawn(sp, event.isEndConquered());
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event)
    {
        if (event.getTarget() instanceof ServerPlayer tracked
                && event.getEntity() instanceof ServerPlayer tracker)
        {
            PlayerClientSettingsManager.syncTrackingRange(tracked, tracker);
            PolyPlayerEvents.START_TRACKING.invoker().onStartTracking(tracked, tracker);
        }
    }

    // -------------------------------------------------------------------------
    // Player events (new)
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.DEATH.invoker().onDeath(sp, event.getSource(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerAttackEntity(AttackEntityEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolyPlayerEvents.ATTACK_ENTITY.invoker().onAttackEntity(sp, event.getTarget());
        }
    }

    @SubscribeEvent
    public static void onPlayerBreakBlock(BreakBlockEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer sp)
        {
            // Fire PolyBlockEvents.BREAK (cancellable, pre-break)
            CancelContext ctx = new CancelContext();
            PolyBlockEvents.BREAK.invoker().onBreak(sp, event.getLevel(), event.getPos(), event.getState(), ctx);
            if (ctx.isCancelled())
            {
                event.setCanceled(true);
                return;
            }
            // Fire PolyPlayerEvents.BREAK_BLOCK (observer, server-level only)
            if (event.getLevel() instanceof ServerLevel sl)
            {
                PolyPlayerEvents.BREAK_BLOCK.invoker().onBreakBlock(sp, sl, event.getPos(), event.getState());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        if (event.getEntity() instanceof ServerPlayer newPlayer
                && event.getOriginal() instanceof ServerPlayer oldPlayer)
        {
            PolyPlayerEvents.CLONE.invoker().onClone(newPlayer, oldPlayer, event.isWasDeath());
        }
    }

    // -------------------------------------------------------------------------
    // Server lifecycle
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event)
    {
        PolyServerLifecycleEvents.SERVER_STARTED.invoker().onStarted(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event)
    {
        PolyServerLifecycleEvents.SERVER_STOPPING.invoker().onStopping(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event)
    {
        PolyServerLifecycleEvents.SERVER_STOPPED.invoker().onStopped(event.getServer());
    }

    // -------------------------------------------------------------------------
    // Server tick
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onServerTickStart(ServerTickEvent.Pre event)
    {
        PolyServerTickEvents.TICK_START.invoker().onTickStart(event.getServer());
    }

    @SubscribeEvent
    public static void onServerTickEnd(ServerTickEvent.Post event)
    {
        PolyServerTickEvents.TICK_END.invoker().onTickEnd(event.getServer());
    }

    @SubscribeEvent
    public static void onLevelTickStart(LevelTickEvent.Pre event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            PolyServerTickEvents.LEVEL_TICK_START.invoker().onLevelTickStart(sl);
        }
    }

    @SubscribeEvent
    public static void onLevelTickEnd(LevelTickEvent.Post event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            PolyServerTickEvents.LEVEL_TICK_END.invoker().onLevelTickEnd(sl);
        }
    }

    // -------------------------------------------------------------------------
    // Level (world) events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            PolyLevelEvents.LEVEL_LOAD.invoker().onLoad(sl);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            PolyLevelEvents.LEVEL_UNLOAD.invoker().onUnload(sl);
        }
    }

    @SubscribeEvent
    public static void onLevelSave(LevelEvent.Save event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            PolyLevelEvents.LEVEL_SAVE.invoker().onSave(sl);
        }
    }

    // -------------------------------------------------------------------------
    // Chunk events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event)
    {
        if (event.getLevel() instanceof ServerLevel sl
                && event.getChunk() instanceof LevelChunk chunk)
        {
            PolyChunkEvents.CHUNK_LOAD.invoker().onLoad(sl, chunk);
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event)
    {
        if (event.getLevel() instanceof ServerLevel sl
                && event.getChunk() instanceof LevelChunk chunk)
        {
            PolyChunkEvents.CHUNK_UNLOAD.invoker().onUnload(sl, chunk);
        }
    }

    // -------------------------------------------------------------------------
    // Living entity events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onLivingDamagePre(LivingIncomingDamageEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.DAMAGE_PRE.invoker().onDamagePre(event.getEntity(), event.getSource(), event.getAmount(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event)
    {
        PolyLivingEvents.DAMAGE_POST.invoker().onDamagePost(event.getEntity(), event.getSource(), event.getHealthDamage());
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event)
    {
        // Only fire on non-player entities here; players are handled by onPlayerDeath above
        if (!(event.getEntity() instanceof ServerPlayer))
        {
            CancelContext ctx = new CancelContext();
            PolyLivingEvents.DEATH.invoker().onDeath(event.getEntity(), event.getSource(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event)
    {
        PolyLivingEvents.FALL.invoker().onFall(event.getEntity(), event.getDistance(), event.getDamageMultiplier());
    }


    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event)
    {
        PolyLivingEvents.DROPS.invoker().onDrops(event.getEntity(), event.getSource(), new java.util.ArrayList<>(event.getDrops()));
    }

    @SubscribeEvent
    public static void onLivingXpDrop(LivingExperienceDropEvent event)
    {
        PolyLivingEvents.XP_DROP.invoker().onXpDrop(event.getEntity(), event.getDroppedExperience());
        // T15: MOB_KILL_XP — folded here to avoid double subscription
        int[] xp = { event.getDroppedExperience() };
        PolyLivingEvents.MOB_KILL_XP.invoker().onMobKillXp(event.getEntity(), event.getAttackingPlayer(), xp);
        event.setDroppedExperience(xp[0]);
    }

    // -------------------------------------------------------------------------
    // New DCH events — Player
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolyPlayerEvents.ENTITY_INTERACT.invoker().onEntityInteract(sp, event.getTarget(), event.getHand());
        }
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayer sp)
        {
            PolyPlayerEvents.ITEM_TOSS.invoker().onItemToss(sp, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event)
    {
        if (event.getPlayer() instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.ITEM_PICKUP.invoker().onItemPickup(sp, event.getItemEntity(), ctx);
            if (ctx.isCancelled()) event.setCanPickup(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void onXpPickup(PlayerXpEvent.PickupXp event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolyPlayerEvents.XP_PICKUP.invoker().onXpPickup(sp, event.getOrb());
        }
    }

    // -------------------------------------------------------------------------
    // New DCH events — Entity
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event)
    {
        PolyEntityEvents.ENTITY_TICK.invoker().onEntityTick(event.getEntity());
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            CancelContext ctx = new CancelContext();
            PolyEntityEvents.JOIN_LEVEL.invoker().onJoinLevel(event.getEntity(), sl, ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    // -------------------------------------------------------------------------
    // New DCH events — Living (post-armor damage)
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onLivingDamageFinalPre(LivingDamageEvent.Pre event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.DAMAGE_FINAL_PRE.invoker().onDamageFinalPre(
                event.getEntity(), event.getSource(), event.getNewDamage(), ctx);
        if (ctx.isCancelled()) event.setNewDamage(0.0f);
    }

    // -------------------------------------------------------------------------
    // Sound events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onPlayLevelSoundAtEntity(PlayLevelSoundEvent.AtEntity event)
    {
        CancelContext ctx = new CancelContext();
        PolySoundEvents.ENTITY_SOUND.invoker().onEntitySound(
                event.getEntity(), event.getSound(), event.getSource(),
                event.getNewVolume(), event.getNewPitch(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    // -------------------------------------------------------------------------
    // Container events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolyPlayerEvents.CONTAINER_CLOSE.invoker().onContainerClose(sp, event.getContainer());
        }
    }

    // -------------------------------------------------------------------------
    // Tier 1: Player tick events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onPlayerTickStart(PlayerTickEvent.Pre event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolyPlayerTickEvents.PLAYER_TICK_START.invoker().onTick(sp);
        }
    }

    @SubscribeEvent
    public static void onPlayerTickEnd(PlayerTickEvent.Post event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolyPlayerTickEvents.PLAYER_TICK_END.invoker().onTick(sp);
        }
    }

    // -------------------------------------------------------------------------
    // Tier 1: Block events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyBlockEvents.PLACE.invoker().onPlace(event.getEntity(), event.getLevel(), event.getPos(), event.getPlacedBlock(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onCropGrowPre(CropGrowEvent.Pre event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            CancelContext ctx = new CancelContext();
            PolyBlockEvents.CROP_GROW_PRE.invoker().onCropGrow(sl, event.getPos(), event.getState(), ctx);
            if (ctx.isCancelled()) event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
        }
    }

    @SubscribeEvent
    public static void onCropGrowPost(CropGrowEvent.Post event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            PolyBlockEvents.CROP_GROW_POST.invoker().onCropGrowPost(sl, event.getPos(), event.getOriginalState());
        }
    }

    @SubscribeEvent
    public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            CancelContext ctx = new CancelContext();
            PolyBlockEvents.FARMLAND_TRAMPLE.invoker().onFarmlandTrample(event.getEntity(), sl, event.getPos(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    // -------------------------------------------------------------------------
    // Tier 1: Living entity events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.HEAL.invoker().onHeal(event.getEntity(), event.getAmount(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingChangeTarget(LivingChangeTargetEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.CHANGE_TARGET.invoker().onChangeTarget(
                event.getEntity(), event.getNewAboutToBeSetTarget(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingConversionPre(LivingConversionEvent.Pre event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.CONVERSION_PRE.invoker().onConversionPre(event.getEntity(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingConversionPost(LivingConversionEvent.Post event)
    {
        PolyLivingEvents.CONVERSION_POST.invoker().onConversionPost(event.getEntity(), event.getOutcome());
    }

    @SubscribeEvent
    public static void onLivingKnockback(LivingKnockBackEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.KNOCKBACK.invoker().onKnockback(
                event.getEntity(), event.getStrength(), event.getRatioX(), event.getRatioZ(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingUseTotem(LivingUseTotemEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.USE_TOTEM.invoker().onUseTotem(event.getEntity(), event.getSource(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    // -------------------------------------------------------------------------
    // Tier 1: Mob effect events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event)
    {
        CancelContext ctx = new CancelContext();
        PolyMobEffectEvents.ALLOW_APPLY.invoker().onAllowApply(event.getEntity(), event.getEffectInstance(), ctx);
        if (ctx.isCancelled()) event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
    }

    @SubscribeEvent
    public static void onMobEffectAdded(MobEffectEvent.Added event)
    {
        PolyMobEffectEvents.APPLIED.invoker().onApplied(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onMobEffectRemoved(MobEffectEvent.Remove event)
    {
        PolyMobEffectEvents.REMOVED.invoker().onRemoved(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onMobEffectExpired(MobEffectEvent.Expired event)
    {
        PolyMobEffectEvents.EXPIRED.invoker().onExpired(event.getEntity(), event.getEffectInstance());
    }

    // -------------------------------------------------------------------------
    // Tier 1: Entity mount
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event)
    {
        if (event.getLevel() instanceof ServerLevel)
        {
            CancelContext ctx = new CancelContext();
            PolyEntityEvents.MOUNT.invoker().onMount(
                    event.getEntityMounting(), event.getEntityBeingMounted(), event.isMounting(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    // -------------------------------------------------------------------------
    // Tier 1: Explosion events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.Start event)
    {
        if (event.getLevel() instanceof ServerLevel)
        {
            CancelContext ctx = new CancelContext();
            PolyExplosionEvents.START.invoker().onStart(event.getLevel(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event)
    {
        if (event.getLevel() instanceof ServerLevel)
        {
            PolyExplosionEvents.DETONATE.invoker().onDetonate(
                    event.getLevel(), event.getAffectedBlocks(), event.getAffectedEntities());
        }
    }

    @SubscribeEvent
    public static void onExplosionKnockback(ExplosionKnockbackEvent event)
    {
        if (event.getLevel() instanceof ServerLevel)
        {
            PolyExplosionEvents.KNOCKBACK.invoker().onKnockback(
                    event.getLevel(), event.getAffectedEntity(), event.getKnockbackVelocity());
        }
    }

    // -------------------------------------------------------------------------
    // Command registration
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        PolyServerCommandEvents.REGISTER_COMMANDS.invoker().onRegisterCommands(
                event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    // =========================================================================
    // Tier 2 bridges
    // =========================================================================

    // -------------------------------------------------------------------------
    // Tier 2: Entity events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onEntityLeaveLevel(net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            PolyEntityEvents.LEAVE_LEVEL.invoker().onLeaveLevel(event.getEntity(), sl);
        }
    }

    @SubscribeEvent
    public static void onEntityTeleport(net.neoforged.neoforge.event.entity.EntityTeleportEvent event)
    {
        if (!(event.getEntity().level() instanceof ServerLevel)) return;
        double[] target = { event.getTargetX(), event.getTargetY(), event.getTargetZ() };
        CancelContext ctx = new CancelContext();
        PolyEntityEvents.TELEPORT.invoker().onTeleport(event.getEntity(), target, ctx);
        if (ctx.isCancelled())
        {
            event.setCanceled(true);
        }
        else
        {
            event.setTargetX(target[0]);
            event.setTargetY(target[1]);
            event.setTargetZ(target[2]);
        }
    }

    @SubscribeEvent
    public static void onEntityTravelToDimension(net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyEntityEvents.TRAVEL_DIMENSION.invoker().onTravelDimension(event.getEntity(), event.getDimension(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onMobGriefing(net.neoforged.neoforge.event.entity.EntityMobGriefingEvent event)
    {
        if (!(event.getEntity().level() instanceof ServerLevel)) return;
        PolyEntityEvents.GriefingContext ctx = new PolyEntityEvents.GriefingContext(event.canGrief());
        PolyEntityEvents.MOB_GRIEFING.invoker().onMobGriefing(event.getEntity(), ctx);
        event.setCanGrief(ctx.canGrief());
    }

    @SubscribeEvent
    public static void onProjectileImpact(net.neoforged.neoforge.event.entity.ProjectileImpactEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyEntityEvents.PROJECTILE_IMPACT.invoker().onProjectileImpact(event.getProjectile(), event.getRayTraceResult(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntityStruckByLightning(net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyEntityEvents.STRUCK_BY_LIGHTNING.invoker().onStruckByLightning(event.getEntity(), event.getLightning(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    // -------------------------------------------------------------------------
    // Tier 2: Player events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
            PolyPlayerEvents.CHANGE_DIMENSION.invoker().onChangeDimension(sp, event.getFrom(), event.getTo());
    }

    @SubscribeEvent
    public static void onPlayerChangeGameMode(PlayerEvent.PlayerChangeGameModeEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.CHANGE_GAME_MODE.invoker().onChangeGameMode(
                    sp, event.getCurrentGameMode(), event.getNewGameMode(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerXpChange(PlayerXpEvent.XpChange event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.XP_CHANGE.invoker().onXpChange(sp, event.getAmount(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerXpLevelChange(PlayerXpEvent.LevelChange event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.XP_LEVEL_CHANGE.invoker().onXpLevelChange(sp, event.getLevels(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.USE_ITEM.invoker().onUseItem(sp, event.getHand(), event.getItemStack(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.RIGHT_CLICK_BLOCK.invoker().onRightClickBlock(
                    sp, event.getHand(), event.getPos(), event.getFace(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAnimalTame(net.neoforged.neoforge.event.entity.living.AnimalTameEvent event)
    {
        if (event.getTamer() instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.ANIMAL_TAME.invoker().onAnimalTame(event.getAnimal(), sp, ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
            PolyPlayerEvents.ITEM_CRAFTED.invoker().onItemCrafted(sp, event.getCrafting(), event.getInventory());
    }

    @SubscribeEvent
    public static void onPlayerSetSpawn(net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyPlayerEvents.SET_SPAWN.invoker().onSetSpawn(
                    sp, event.getNewSpawn(), event.getSpawnLevel(), event.isForced(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    // -------------------------------------------------------------------------
    // Tier 2: Spawn events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onFinalizeSpawn(net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolySpawnEvents.FINALIZE_SPAWN.invoker().onFinalizeSpawn(event.getEntity(), event.getLevel(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onMobDespawn(net.neoforged.neoforge.event.entity.living.MobDespawnEvent event)
    {
        PolySpawnEvents.DespawnContext ctx = new PolySpawnEvents.DespawnContext();
        PolySpawnEvents.MOB_DESPAWN.invoker().onMobDespawn(event.getEntity(), ctx);
        switch (ctx.getResult())
        {
            case KEEP   -> event.setResult(net.neoforged.neoforge.event.entity.living.MobDespawnEvent.Result.DENY);
            case REMOVE -> event.setResult(net.neoforged.neoforge.event.entity.living.MobDespawnEvent.Result.ALLOW);
            default     -> {} // DEFAULT: no change
        }
    }

    // -------------------------------------------------------------------------
    // Tier 2: Data events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onTagsUpdated(net.neoforged.neoforge.event.TagsUpdatedEvent event)
    {
        if (event.getUpdateCause() == net.neoforged.neoforge.event.TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD)
            PolyDataEvents.TAGS_UPDATED.invoker().onTagsUpdated(event.getLookupProvider());
    }

    // -------------------------------------------------------------------------
    // Tier 2: Living combat events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onCriticalHit(net.neoforged.neoforge.event.entity.player.CriticalHitEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolyLivingEvents.CriticalHitContext ctx = new PolyLivingEvents.CriticalHitContext(
                    event.isCriticalHit(), event.getDamageMultiplier());
            PolyLivingEvents.CRITICAL_HIT.invoker().onCriticalHit(sp, event.getTarget(), ctx);
            event.setCriticalHit(ctx.isCritical());
            event.setDamageMultiplier(ctx.getDamageMultiplier());
        }
    }

    @SubscribeEvent
    public static void onArrowNock(net.neoforged.neoforge.event.entity.player.ArrowNockEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyLivingEvents.ARROW_NOCK.invoker().onArrowNock(sp, event.getBow(), event.getHand(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onArrowLoose(net.neoforged.neoforge.event.entity.player.ArrowLooseEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            CancelContext ctx = new CancelContext();
            PolyLivingEvents.ARROW_LOOSE.invoker().onArrowLoose(sp, event.getBow(), event.getCharge(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingShieldBlock(net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.SHIELD_BLOCK.invoker().onShieldBlock(
                event.getEntity(), event.getDamageContainer().getSource(),
                event.getBlockedDamage(), ctx);
        if (ctx.isCancelled()) event.setBlockedDamage(0f);
    }

    // -------------------------------------------------------------------------
    // Tier 2: Sleep events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onCanPlayerSleep(net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolySleepEvents.SleepContext ctx = new PolySleepEvents.SleepContext(event.getProblem());
            PolySleepEvents.ALLOW_SLEEPING.invoker().onAllowSleeping(sp, event.getPos(), ctx);
            event.setProblem(ctx.getProblem());
        }
    }

    // -------------------------------------------------------------------------
    // Tier 3: Living entity events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onLivingEquipChange(LivingEquipmentChangeEvent event)
    {
        PolyLivingEvents.ENTITY_EQUIP_CHANGE.invoker().onEquipChange(
                event.getEntity(), event.getSlot(), event.getFrom(), event.getTo());
    }

    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.ITEM_USE_START.invoker().onItemUseStart(event.getEntity(), event.getItem(), ctx);
        PolyItemEvents.ITEM_USE_START.invoker().onUseStart(event.getEntity(), event.getItem(), event.getDuration(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onItemUseTick(LivingEntityUseItemEvent.Tick event)
    {
        CancelContext ctx = new CancelContext();
        PolyItemEvents.ITEM_USE_TICK.invoker().onUseTick(event.getEntity(), event.getItem(), event.getDuration(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event)
    {
        ItemStack[] resultHolder = { event.getResultStack() };
        PolyLivingEvents.ITEM_USE_FINISH.invoker().onItemUseFinish(event.getEntity(), event.getItem(), resultHolder);
        PolyItemEvents.ITEM_USE_FINISH.invoker().onUseFinish(event.getEntity(), event.getItem());
        event.setResultStack(resultHolder[0]);
    }

    @SubscribeEvent
    public static void onMobSplit(MobSplitEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.MOB_SPLIT.invoker().onMobSplit(event.getParent(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBabySpawn(BabyEntitySpawnEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.BABY_SPAWN.invoker().onBabySpawn(
                event.getParentA(), event.getParentB(),
                event.getCausedByPlayer(), event.getChild(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingDrown(LivingDrownEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.DROWN.invoker().onDrown(event.getEntity(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingBreathe(LivingBreatheEvent event)
    {
        PolyLivingEvents.BreatheContext ctx = new PolyLivingEvents.BreatheContext(
                event.canBreathe(), event.getConsumeAirAmount(), event.getRefillAirAmount());
        PolyLivingEvents.BREATHE.invoker().onBreathe(event.getEntity(), ctx);
        event.setCanBreathe(ctx.canBreathe());
        event.setConsumeAirAmount(ctx.getConsumeAirAmount());
        event.setRefillAirAmount(ctx.getRefillAirAmount());
    }

    @SubscribeEvent
    public static void onLivingDestroyBlock(LivingDestroyBlockEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.DESTROY_BLOCK.invoker().onDestroyBlock(
                event.getEntity(), event.getEntity().level(), event.getPos(), event.getState(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLivingGetProjectile(LivingGetProjectileEvent event)
    {
        net.minecraft.world.item.ItemStack[] resultHolder = { event.getProjectileItemStack() };
        PolyLivingEvents.GET_PROJECTILE.invoker().onGetProjectile(event.getEntity(), resultHolder);
        event.setProjectileItemStack(resultHolder[0]);
    }

    @SubscribeEvent
    public static void onLivingSwapItems(LivingSwapItemsEvent.Hands event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.SWAP_ITEMS.invoker().onSwapItems(
                event.getEntity(), event.getItemSwappedToMainHand(), event.getItemSwappedToOffHand(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    // -------------------------------------------------------------------------
    // Tier 3: Item events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onItemExpire(ItemExpireEvent event)
    {
        PolyItemEvents.ITEM_EXPIRE.invoker().onItemExpire(event.getEntity());
    }

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event)
    {
        CancelContext ctx = new CancelContext();
        if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player)
        {
            java.util.List<net.minecraft.world.item.ItemStack> drops = new java.util.ArrayList<>(event.getDrops());
            PolyItemEvents.ITEM_FISHING.invoker().onItemFishing(player, drops, ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onGrindstone(GrindstoneEvent.OnPlaceItem event)
    {
        PolyItemEvents.GrindstoneContext ctx = new PolyItemEvents.GrindstoneContext(
                event.getTopItem(), event.getBottomItem(), event.getOutput(), event.getXp());
        PolyItemEvents.GRINDSTONE.invoker().onGrindstone(event.getTopItem(), event.getBottomItem(), ctx);
        event.setOutput(ctx.getOutput());
        event.setXp(ctx.getXp());
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event)
    {
        PolyItemEvents.AnvilContext ctx = new PolyItemEvents.AnvilContext(
                event.getOutput(), event.getXpCost(), event.getMaterialCost());
        PolyItemEvents.ANVIL_UPDATE.invoker().onAnvilUpdate(event.getLeft(), event.getRight(), event.getName(), ctx);
        event.setOutput(ctx.getOutput());
        event.setXpCost(ctx.getXpCost());
        event.setMaterialCost(ctx.getMaterialCost());
    }

    // -------------------------------------------------------------------------
    // Tier 3: Block events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onPortalSpawn(BlockEvent.PortalSpawnEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyBlockEvents.PORTAL_SPAWN.invoker().onPortalSpawn(event.getLevel(), event.getPos(), event.getState(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPistonPre(PistonEvent.Pre event)
    {
        CancelContext ctx = new CancelContext();
        boolean extending = event.getPistonMoveType() == PistonEvent.PistonMoveType.EXTEND;
        PolyBlockEvents.PISTON_PRE.invoker().onPistonPre(event.getLevel(), event.getPos(), event.getDirection(), extending, ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPistonPost(PistonEvent.Post event)
    {
        boolean extending = event.getPistonMoveType() == PistonEvent.PistonMoveType.EXTEND;
        PolyBlockEvents.PISTON_POST.invoker().onPistonPost(event.getLevel(), event.getPos(), event.getDirection(), extending);
    }

    @SubscribeEvent
    public static void onNoteBlockPlay(NoteBlockEvent.Play event)
    {
        CancelContext ctx = new CancelContext();
        PolyBlockEvents.NOTE_BLOCK_PLAY.invoker().onNoteBlockPlay(event.getLevel(), event.getPos(), event.getState(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onCreateFluidSource(CreateFluidSourceEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyBlockEvents.FLUID_SOURCE.invoker().onFluidSource(event.getLevel(), event.getPos(), ctx);
        if (ctx.isCancelled()) event.setCanConvert(false);
    }

    // -------------------------------------------------------------------------
    // Tier 3: Level events
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            PolyLevelEvents.SLEEP_FINISHED.invoker().onSleepFinished(sl);
        }
    }

    // =========================================================================
    // Tier 7: Player extended
    // =========================================================================

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event)
    {
        float[] speed = { event.getNewSpeed() };
        BlockPos pos = event.getPosition().orElse(null);
        PolyPlayerEvents.BREAK_SPEED.invoker().onBreakSpeed(
                event.getEntity(), event.getState(), pos, speed);
        event.setNewSpeed(speed[0]);
    }

    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event)
    {
        boolean[] result = { event.canHarvest() };
        PolyPlayerEvents.HARVEST_CHECK.invoker().onHarvestCheck(
                event.getEntity(), event.getTargetBlock(), result);
        event.setCanHarvest(result[0]);
    }

    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyPlayerEvents.BONE_MEAL.invoker().onBoneMeal(
                event.getPlayer(), event.getLevel(), event.getPos(), event.getState(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onSweepAttack(SweepAttackEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyPlayerEvents.SWEEP_ATTACK.invoker().onSweepAttack(
                event.getEntity(), event.getTarget(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        CancelContext ctx = new CancelContext();
        PolyPlayerEvents.LEFT_CLICK_BLOCK.invoker().onLeftClickBlock(
                event.getEntity(), event.getPos(), event.getFace(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event)
    {
        PolyPlayerEvents.RIGHT_CLICK_EMPTY.invoker().onRightClickEmpty(
                event.getEntity(), event.getHand());
    }

    @SubscribeEvent
    public static void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolyPlayerEvents.ADVANCEMENT_EARN.invoker().onAdvancementEarn(
                    sp, event.getAdvancement());
        }
    }

    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event)
    {
        PolyPlayerEvents.CONTAINER_OPEN.invoker().onContainerOpen(
                event.getEntity(), event.getContainer());
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event)
    {
        PolyPlayerEvents.WAKE_UP.invoker().onWakeUp(
                event.getEntity(), event.updateLevel());
    }

    @SubscribeEvent
    public static void onItemSmelted(PlayerEvent.ItemSmeltedEvent event)
    {
        PolyPlayerEvents.ITEM_SMELTED.invoker().onItemSmelted(
                event.getEntity(), event.getSmelting());
    }

    // =========================================================================
    // Tier 8: Chat / World
    // =========================================================================

    @SubscribeEvent
    public static void onCommandEvent(CommandEvent event)
    {
        String[] cmd = { event.getParseResults().getReader().getString() };
        CancelContext ctx = new CancelContext();
        PolyChatEvents.COMMAND_EXECUTE.invoker().onCommandExecute(
                event.getParseResults().getContext().getSource(), cmd, ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onGameRuleChanged(GameRuleChangedEvent event)
    {
        PolyLevelEvents.GAME_RULE_CHANGE.invoker().onGameRuleChange(
                event.getServer(), event.getGameRule(), event.getGameRules());
    }

    @SubscribeEvent
    public static void onDifficultyChange(DifficultyChangeEvent event)
    {
        PolyLevelEvents.DIFFICULTY_CHANGE.invoker().onDifficultyChange(
                event.getDifficulty(), event.getOldDifficulty());
    }

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event)
    {
        PolyBlockEvents.BLOCK_DROPS.invoker().onBlockDrops(
                event.getLevel(), event.getPos(), event.getState(), event.getDrops());
    }

    @SubscribeEvent
    public static void onEntityMultiPlace(BlockEvent.EntityMultiPlaceEvent event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            CancelContext ctx = new CancelContext();
            PolyBlockEvents.ENTITY_MULTI_PLACE.invoker().onEntityMultiPlace(
                    sl, event.getEntity(), event.getPlacedBlock(), ctx);
            if (ctx.isCancelled()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event)
    {
        PolyServerLifecycleEvents.DATAPACK_SYNC.invoker().onDatapackSync(
                event.getPlayerList().getServer(), event.getPlayer());
    }

    @SubscribeEvent
    public static void onStatAward(StatAwardEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyPlayerEvents.STAT_AWARD.invoker().onStatAward(
                event.getEntity(), event.getStat(), event.getValue(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    // =========================================================================
    // Tier 9: Living / Mob
    // =========================================================================

    @SubscribeEvent
    public static void onSpawnClusterSize(SpawnClusterSizeEvent event)
    {
        int[] size = { event.getSize() };
        PolyLivingEvents.SPAWN_CLUSTER_SIZE.invoker().onSpawnClusterSize(
                event.getEntity(), size);
        event.setSize(size[0]);
    }

    @SubscribeEvent
    public static void onEnderManAnger(EnderManAngerEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyLivingEvents.ENDERMAN_ANGER.invoker().onEndermanAnger(
                event.getEntity(), event.getPlayer(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onArmorHurt(ArmorHurtEvent event)
    {
        java.util.EnumMap<EquipmentSlot, Float> dmgMap = new java.util.EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : EquipmentSlot.values())
        {
            Float v = event.getNewDamage(slot);
            if (v != null) dmgMap.put(slot, v);
        }
        PolyLivingEvents.ArmorHurtContext ctx = new PolyLivingEvents.ArmorHurtContext(dmgMap);
        PolyLivingEvents.ARMOR_HURT.invoker().onArmorHurt(event.getEntity(), event.getDamageSource(), ctx);
        ctx.getDamageMap().forEach((slot, v) -> event.setNewDamage(slot, v));
    }

    @SubscribeEvent
    public static void onTradeWithVillager(TradeWithVillagerEvent event)
    {
        PolyPlayerEvents.TRADE_WITH_VILLAGER.invoker().onTradeWithVillager(
                event.getEntity(), event.getMerchantOffer(), event.getAbstractVillager());
    }

    @SubscribeEvent
    public static void onPhantomSpawn(PlayerSpawnPhantomsEvent event)
    {
        CancelContext ctx = new CancelContext();
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolyPlayerEvents.PHANTOM_SPAWN.invoker().onPhantomSpawn(sp, ctx);
            if (ctx.isCancelled()) event.setResult(PlayerSpawnPhantomsEvent.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onPotentialSpawns(LevelEvent.PotentialSpawns event)
    {
        if (event.getLevel() instanceof ServerLevel sl)
        {
            java.util.List<net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData> list =
                    new java.util.ArrayList<>();
            event.getSpawnerDataList().forEach(w -> list.add(w.value()));
            PolyLevelEvents.POTENTIAL_SPAWNS.invoker().onPotentialSpawns(
                    sl, event.getMobCategory(), event.getPos(), list);
        }
    }

    // =========================================================================
    // Tier 10: Enchanting / Fuel / Player name/file / Chunks
    // =========================================================================

    @SubscribeEvent
    public static void onGetEnchantLevel(GetEnchantmentLevelEvent event)
    {
        ItemStack stack = event.getStack() instanceof ItemStack is ? is : ItemStack.EMPTY;
        PolyEnchantEvents.GET_ENCHANT_LEVEL.invoker().onGetEnchantLevel(
                stack, event.getTargetEnchant(), event.getEnchantments());
    }

    @SubscribeEvent
    public static void onEnchantTableLevel(EnchantmentLevelSetEvent event)
    {
        int[] level = { event.getEnchantLevel() };
        PolyEnchantEvents.ENCHANT_TABLE_LEVEL.invoker().onEnchantTableLevel(
                event.getItem(), event.getPower(), event.getOriginalLevel(), level);
        event.setEnchantLevel(level[0]);
    }

    @SubscribeEvent
    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event)
    {
        int[] time = { event.getBurnTime() };
        PolyItemEvents.FUEL_BURN_TIME.invoker().onFuelBurnTime(event.getItemStack(), time);
        event.setBurnTime(time[0]);
    }

    @SubscribeEvent
    public static void onBrewedPotion(PlayerBrewedPotionEvent event)
    {
        PolyPlayerEvents.BREWED_POTION.invoker().onBrewedPotion(event.getEntity(), event.getStack());
    }

    @SubscribeEvent
    public static void onNameFormat(PlayerEvent.NameFormat event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            net.minecraft.network.chat.Component[] name = { event.getDisplayname() };
            PolyPlayerEvents.NAME_FORMAT.invoker().onNameFormat(sp, name);
            event.setDisplayname(name[0]);
        }
    }

    @SubscribeEvent
    public static void onTabListNameFormat(PlayerEvent.TabListNameFormat event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            net.minecraft.network.chat.Component base = event.getDisplayName();
            if (base == null) base = sp.getDisplayName();
            net.minecraft.network.chat.Component[] name = { base };
            PolyPlayerEvents.TAB_LIST_NAME_FORMAT.invoker().onTabListNameFormat(sp, name);
            event.setDisplayName(name[0]);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoad(PlayerEvent.LoadFromFile event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolyPlayerEvents.PLAYER_LOAD.invoker().onPlayerFile(sp, event.getPlayerUUID().toString());
        }
    }

    @SubscribeEvent
    public static void onPlayerSave(PlayerEvent.SaveToFile event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
        {
            PolyPlayerEvents.PLAYER_SAVE.invoker().onPlayerFile(sp, event.getPlayerUUID().toString());
        }
    }

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event)
    {
        PolyChunkEvents.CHUNK_WATCH.invoker().onWatch(
                event.getPlayer(), event.getPos(), event.getLevel());
    }

    @SubscribeEvent
    public static void onChunkUnWatch(ChunkWatchEvent.UnWatch event)
    {
        PolyChunkEvents.CHUNK_UNWATCH.invoker().onUnWatch(
                event.getPlayer(), event.getPos(), event.getLevel());
    }

    // ── Tier 15 ───────────────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onServerAfterSetup(ServerStartedEvent event)
    {
        // NeoForge fires ServerStartedEvent at the same logical point as Fabric AFTER_SETUP.
        PolyServerLifecycleEvents.SERVER_AFTER_SETUP.invoker().onServerAfterSetup(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerNegotiation(PlayerNegotiationEvent event)
    {
        PolyServerLifecycleEvents.PLAYER_NEGOTIATION.invoker().onPlayerNegotiation(event.getConnection());
    }

    @SubscribeEvent
    public static void onEntityTickEnd(EntityTickEvent.Post event)
    {
        PolyEntityEvents.ENTITY_TICK_END.invoker().onEntityTickEnd(event.getEntity());
    }

    @SubscribeEvent
    public static void onMobSpawnContext(FinalizeSpawnEvent event)
    {
        // FinalizeSpawnEvent extends MobSpawnEvent: fires when spawn is confirmed, provides entity/level/pos
        BlockPos pos = BlockPos.containing(event.getX(), event.getY(), event.getZ());
        PolySpawnEvents.MOB_SPAWN_CONTEXT.invoker()
                .onMobSpawnContext(event.getEntity(), event.getLevel(), pos, null, null);
    }

    @SubscribeEvent
    public static void onItemPickupPost(ItemEntityPickupEvent.Post event)
    {
        PolyPlayerEvents.ITEM_PICKUP_POST.invoker().onItemPickupPost(
                event.getPlayer(), event.getItemEntity(), event.getCurrentStack());
    }

    @SubscribeEvent
    public static void onRegisterStructureConversions(RegisterStructureConversionsEvent event)
    {
        PolyServerLifecycleEvents.REGISTER_STRUCTURE_CONVERSIONS.invoker().onRegisterStructureConversions(event);
    }

    // ── Tier 7-10: Chat / Command events (previously unwired) ─────────────────

    @SubscribeEvent
    public static void onServerChatEvent(ServerChatEvent event)
    {
        Component[] msg = { event.getMessage() };

        // Decoration pass (CHAT_DECORATE) — mutate the component before gatekeeping
        PolyChatEvents.CHAT_DECORATE.invoker().onChatDecorate(event.getPlayer(), msg);
        event.setMessage(msg[0]);

        // Gate pass (SERVER_CHAT) — can cancel the message entirely
        CancelContext ctx = new CancelContext();
        PolyChatEvents.SERVER_CHAT.invoker().onServerChat(event.getPlayer(), msg, ctx);
        event.setMessage(msg[0]);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    // ── Tier 22 ───────────────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onEntityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event)
    {
        boolean result = PolyEntityEvents.ENTITY_INVULNERABILITY_CHECK.invoker()
                .onInvulnerabilityCheck(event.getEntity(), event.getSource(), event.isInvulnerable());
        event.setInvulnerable(result);
    }

    @SubscribeEvent
    public static void onVillageSiege(VillageSiegeEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyVillageEvents.SIEGE_SPAWN.invoker().onSiegeSpawn(
                event.getSiege(), event.getLevel(), event.getPlayer(), event.getAttemptedSpawnPos(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPotionBrewPre(PotionBrewEvent.Pre event)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(event.getLength(), ItemStack.EMPTY);
        for (int i = 0; i < event.getLength(); i++) items.set(i, event.getItem(i));
        CancelContext ctx = new CancelContext();
        PolyBrewingEvents.BREW_PRE.invoker().onBrewPre(items, ctx);
        if (ctx.isCancelled())
        {
            event.setCanceled(true);
            return;
        }
        for (int i = 0; i < event.getLength(); i++) event.setItem(i, items.get(i));
    }

    @SubscribeEvent
    public static void onPotionBrewPost(PotionBrewEvent.Post event)
    {
        NonNullList<ItemStack> items = NonNullList.withSize(event.getLength(), ItemStack.EMPTY);
        for (int i = 0; i < event.getLength(); i++) items.set(i, event.getItem(i));
        PolyBrewingEvents.BREW_POST.invoker().onBrewPost(items);
    }

    // ── New events: player ───────────────────────────────────────────────────

    @SubscribeEvent
    public static void onStopTracking(PlayerEvent.StopTracking event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
            PolyPlayerEvents.STOP_TRACKING.invoker().onStopTracking(sp, event.getTarget());
    }

    @SubscribeEvent
    public static void onPermissionsChanged(PermissionsChangedEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
            PolyPlayerEvents.PERMISSIONS_CHANGED.invoker().onPermissionsChanged(sp);
    }

    @SubscribeEvent
    public static void onClientInformationUpdated(ClientInformationUpdatedEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer sp)
            PolyPlayerEvents.CLIENT_INFORMATION_UPDATED.invoker()
                    .onClientInformationUpdated(sp, event.getUpdatedInformation());
    }

    @SubscribeEvent
    public static void onPlayerDestroyItem(PlayerDestroyItemEvent event)
    {
        net.minecraft.world.InteractionHand hand = event.getHand();
        EquipmentSlot slot = hand == null ? null
                : hand == net.minecraft.world.InteractionHand.MAIN_HAND
                        ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        PolyPlayerEvents.DESTROY_ITEM.invoker().onDestroyItem(event.getEntity(), event.getOriginal(), slot);
    }

    @SubscribeEvent
    public static void onPlayerEnchantItem(PlayerEnchantItemEvent event)
    {
        // NeoForge does not expose the XP levels spent in this event; pass 0.
        PolyPlayerEvents.ENCHANT_ITEM.invoker().onEnchantItem(event.getEntity(), event.getEnchantedItem(), 0);
    }

    @SubscribeEvent
    public static void onPlayerFlyableFall(PlayerFlyableFallEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyPlayerEvents.FLYABLE_FALL.invoker().onFlyableFall((Player) event.getEntity(), ctx);
        // PlayerFlyableFallEvent is not cancellable in this NeoForge version
    }

    @SubscribeEvent
    public static void onCanContinueSleeping(CanContinueSleepingEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            Player.BedSleepingProblem[] result = {event.getProblem()};
            PolyPlayerEvents.CAN_CONTINUE_SLEEPING.invoker().onCanContinueSleeping(serverPlayer, result);
            if (result[0] != null) event.setContinueSleeping(false);
        }
    }

    @SubscribeEvent
    public static void onRespawnPosition(PlayerRespawnPositionEvent event)
    {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        // getTeleportTransition().newLevel() gives the target ServerLevel; extract its dimension key.
        ServerLevel targetLevel = event.getTeleportTransition().newLevel();
        ResourceKey<Level> originalDim = targetLevel.dimension();
        @SuppressWarnings("unchecked")
        ResourceKey<net.minecraft.world.level.Level>[] dimension = new ResourceKey[] { originalDim };
        BlockPos[] pos = { null }; // Position override requires rebuilding TeleportTransition — not supported here.
        PolyPlayerEvents.RESPAWN_POSITION.invoker().onRespawnPosition(sp, pos, dimension);
        if (dimension[0] != originalDim)
        {
            event.setRespawnLevel(dimension[0]);
        }
    }

    @SubscribeEvent
    public static void onAdvancementRevoke(AdvancementEvent.AdvancementProgressEvent event)
    {
        if (event.getProgressType() == AdvancementEvent.AdvancementProgressEvent.ProgressType.REVOKE
                && event.getEntity() instanceof ServerPlayer sp)
        {
            PolyPlayerEvents.ADVANCEMENT_REVOKE.invoker().onAdvancementRevoke(sp, event.getAdvancement());
        }
    }

    // ── New events: block ────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event)
    {
        CancelContext ctx = new CancelContext();
        PolyBlockEvents.FLUID_PLACE_BLOCK.invoker().onFluidPlaceBlock(
                (net.minecraft.world.level.Level) event.getLevel(), event.getPos(),
                event.getLiquidPos(), event.getNewState(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event)
    {
        PolyBlockEvents.NEIGHBOR_NOTIFY.invoker().onNeighborNotify(
                (net.minecraft.world.level.Level) event.getLevel(), event.getPos(),
                event.getState(), event.getNotifiedSides(), event.getForceRedstoneUpdate());
    }

    @SubscribeEvent
    public static void onBlockGrowFeature(BlockGrowFeatureEvent event)
    {
        if (!(event.getLevel() instanceof net.minecraft.server.level.ServerLevel sl)) return;
        PolyBlockEvents.BLOCK_GROW_FEATURE.invoker().onBlockGrowFeature(
                sl, event.getPos(), event.getFeature());
    }

    @SubscribeEvent
    public static void onAlterGround(AlterGroundEvent event)
    {
        // TreeDecorator.Context.level() is LevelSimulatedReader; cast succeeds in world-gen context.
        if (event.getContext().level() instanceof ServerLevel sl)
        {
            java.util.List<BlockPos> positions = event.getPositions();
            BlockPos pos = positions.isEmpty() ? BlockPos.ZERO : positions.get(0);
            PolyBlockEvents.ALTER_GROUND.invoker().onAlterGround(sl, pos, null);
        }
    }

    @SubscribeEvent
    public static void onNoteBlockChange(NoteBlockEvent.Change event)
    {
        net.minecraft.world.level.block.state.properties.NoteBlockInstrument instrument =
                event.getState().getValue(net.minecraft.world.level.block.NoteBlock.INSTRUMENT);
        CancelContext ctx = new CancelContext();
        PolyBlockEvents.NOTE_BLOCK_CHANGE.invoker().onNoteBlockChange(
                (net.minecraft.world.level.Level) event.getLevel(), event.getPos(),
                instrument, instrument, event.getOldNote().ordinal(), event.getNote().ordinal(), ctx);
        if (ctx.isCancelled()) event.setCanceled(true);
    }

    // ── New events: data ─────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event)
    {
        PolyDataEvents.LOOT_TABLE_LOAD.invoker().onLootTableLoad(event.getName());
    }

    // ── New events: item ─────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onItemStackedOnOther(ItemStackedOnOtherEvent event)
    {
        // getCarriedSlotAccess() returns SlotAccess, not Slot — pass null for carriedSlot.
        PolyItemEvents.ITEM_STACKED_ON_OTHER.invoker().onItemStackedOnOther(
                event.getPlayer(), event.getCarriedItem(), event.getStackedOnItem(),
                event.getSlot(), null);
    }

    @SubscribeEvent
    public static void onAnvilCraft(AnvilCraftEvent.Post event)
    {
        // Cost is not directly exposed on AnvilCraftEvent; pass 0.
        PolyItemEvents.ANVIL_CRAFT.invoker().onAnvilCraft(
                event.getEntity(), event.getLeft(), event.getRight(), event.getOutput(), 0);
    }
}
