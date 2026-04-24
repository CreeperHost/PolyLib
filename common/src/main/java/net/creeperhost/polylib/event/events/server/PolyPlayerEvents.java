package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class PolyPlayerEvents
{
    // --- Existing events ---

    public static final PolyEvent<Login> LOGIN = PolyEvent.create(handlers -> player -> handlers.forEach(h -> h.onLogin(player)));
    public static final PolyEvent<Logout> LOGOUT = PolyEvent.create(handlers -> player -> handlers.forEach(h -> h.onLogout(player)));
    public static final PolyEvent<Respawn> RESPAWN = PolyEvent.create(handlers -> (player, endConquered) -> handlers.forEach(h -> h.onRespawn(player, endConquered)));
    public static final PolyEvent<StartTracking> START_TRACKING = PolyEvent.create(handlers -> (tracked, tracker) -> handlers.forEach(h -> h.onStartTracking(tracked, tracker)));

    // --- New events ---

    /**
     * Fired when a player is about to die. Call {@link CancelContext#cancel()} to prevent death.
     */
    public static final PolyEvent<Death> DEATH = PolyEvent.create(handlers -> (player, source, ctx) ->
    {
        for (var h : handlers)
        {
            h.onDeath(player, source, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /** Fired when a player attacks an entity. */
    public static final PolyEvent<AttackEntity> ATTACK_ENTITY = PolyEvent.create(handlers -> (player, target) -> handlers.forEach(h -> h.onAttackEntity(player, target)));

    /** Fired after a player successfully breaks a block. */
    public static final PolyEvent<BreakBlock> BREAK_BLOCK = PolyEvent.create(handlers -> (player, level, pos, state) -> handlers.forEach(h -> h.onBreakBlock(player, level, pos, state)));

    /**
     * Fired when a new player entity is cloned from an existing one (death/end portal).
     * {@code isWasDeath} is true when the clone was triggered by death, false for end portal.
     */
    public static final PolyEvent<Clone> CLONE = PolyEvent.create(handlers -> (newPlayer, oldPlayer, isWasDeath) -> handlers.forEach(h -> h.onClone(newPlayer, oldPlayer, isWasDeath)));

    /** Fired when a player right-click interacts with an entity. */
    public static final PolyEvent<EntityInteract> ENTITY_INTERACT = PolyEvent.create(handlers -> (player, target, hand) -> handlers.forEach(h -> h.onEntityInteract(player, target, hand)));

    /** Fired when a player tosses (Q-drops) an item. */
    public static final PolyEvent<ItemToss> ITEM_TOSS = PolyEvent.create(handlers -> (player, item) -> handlers.forEach(h -> h.onItemToss(player, item)));

    /**
     * Fired when a player is about to pick up an item entity.
     * Call {@link CancelContext#cancel()} to prevent the pickup.
     */
    public static final PolyEvent<ItemPickup> ITEM_PICKUP = PolyEvent.create(handlers -> (player, item, ctx) ->
    {
        for (var h : handlers)
        {
            h.onItemPickup(player, item, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /** Fired when a player picks up an experience orb. */
    public static final PolyEvent<XpPickup> XP_PICKUP = PolyEvent.create(handlers -> (player, orb) -> handlers.forEach(h -> h.onXpPickup(player, orb)));

    /** Fired when a player closes a container/menu. */
    public static final PolyEvent<ContainerClose> CONTAINER_CLOSE = PolyEvent.create(handlers -> (player, container) -> handlers.forEach(h -> h.onContainerClose(player, container)));

    // --- Tier 2 events ---

    /**
     * Fired after a player changes dimension. Informational — not cancellable.
     * <p>
     * NeoForge: {@code PlayerEvent.PlayerChangedDimensionEvent}<br>
     * Fabric: {@code ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_WORLD}
     */
    public static final PolyEvent<ChangeDimension> CHANGE_DIMENSION = PolyEvent.create(
            handlers -> (player, from, to) -> handlers.forEach(h -> h.onChangeDimension(player, from, to)));

    /**
     * Fired before a player's game mode changes.
     * Call {@link CancelContext#cancel()} to prevent the change.
     * <p>
     * NeoForge: {@code PlayerEvent.PlayerChangeGameModeEvent}<br>
     * Fabric: mixin into {@code ServerPlayerGameMode#changeGameModeForPlayer}
     */
    public static final PolyEvent<ChangeGameMode> CHANGE_GAME_MODE = PolyEvent.create(handlers -> (player, current, next, ctx) ->
    {
        for (var h : handlers)
        {
            h.onChangeGameMode(player, current, next, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired before a player gains or loses XP points.
     * Call {@link CancelContext#cancel()} to prevent the change.
     * <p>
     * NeoForge: {@code PlayerXpEvent.XpChange}<br>
     * Fabric: mixin into {@code Player#giveExperiencePoints}
     */
    public static final PolyEvent<XpChange> XP_CHANGE = PolyEvent.create(handlers -> (player, amount, ctx) ->
    {
        for (var h : handlers)
        {
            h.onXpChange(player, amount, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired before a player's XP level changes.
     * Call {@link CancelContext#cancel()} to prevent the change.
     * <p>
     * NeoForge: {@code PlayerXpEvent.LevelChange}<br>
     * Fabric: mixin into {@code Player#giveExperienceLevels}
     */
    public static final PolyEvent<XpLevelChange> XP_LEVEL_CHANGE = PolyEvent.create(handlers -> (player, levels, ctx) ->
    {
        for (var h : handlers)
        {
            h.onXpLevelChange(player, levels, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a player right-clicks with an item in hand (no block/entity target).
     * Call {@link CancelContext#cancel()} to prevent the use.
     * <p>
     * NeoForge: {@code PlayerInteractEvent.RightClickItem}<br>
     * Fabric: {@code UseItemCallback.EVENT}
     */
    public static final PolyEvent<UseItem> USE_ITEM = PolyEvent.create(handlers -> (player, hand, stack, ctx) ->
    {
        for (var h : handlers)
        {
            h.onUseItem(player, hand, stack, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a player right-clicks on a block.
     * Call {@link CancelContext#cancel()} to prevent the interaction.
     * <p>
     * NeoForge: {@code PlayerInteractEvent.RightClickBlock}<br>
     * Fabric: {@code UseBlockCallback.EVENT}
     */
    public static final PolyEvent<RightClickBlock> RIGHT_CLICK_BLOCK = PolyEvent.create(handlers -> (player, hand, pos, face, ctx) ->
    {
        for (var h : handlers)
        {
            h.onRightClickBlock(player, hand, pos, face, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a player is about to tame an animal.
     * Call {@link CancelContext#cancel()} to prevent taming.
     * <p>
     * NeoForge: {@code AnimalTameEvent}<br>
     * Fabric: mixin into {@code TamableAnimal#tame}
     */
    public static final PolyEvent<AnimalTame> ANIMAL_TAME = PolyEvent.create(handlers -> (animal, player, ctx) ->
    {
        for (var h : handlers)
        {
            h.onAnimalTame(animal, player, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a player crafts an item. Informational — not cancellable.
     * <p>
     * NeoForge: {@code PlayerEvent.ItemCraftedEvent}<br>
     * Fabric: mixin into {@code RecipeBookMenu#handlePlacement}
     */
    public static final PolyEvent<ItemCrafted> ITEM_CRAFTED = PolyEvent.create(
            handlers -> (player, crafted, craftingGrid) -> handlers.forEach(h -> h.onItemCrafted(player, crafted, craftingGrid)));

    /**
     * Fired before a player's spawn point is set (bed, respawn anchor, {@code /spawnpoint}).
     * Call {@link CancelContext#cancel()} to prevent the spawn point change.
     * <p>
     * NeoForge: {@code PlayerSetSpawnEvent}<br>
     * Fabric: mixin into {@code ServerPlayer#setRespawnPosition}
     */
    public static final PolyEvent<SetSpawn> SET_SPAWN = PolyEvent.create(handlers -> (player, pos, dimension, forced, ctx) ->
    {
        for (var h : handlers)
        {
            h.onSetSpawn(player, pos, dimension, forced, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    private PolyPlayerEvents()
    {
    }

    @FunctionalInterface
    public interface Login
    {
        void onLogin(ServerPlayer player);
    }

    @FunctionalInterface
    public interface Logout
    {
        void onLogout(ServerPlayer player);
    }

    @FunctionalInterface
    public interface Respawn
    {
        void onRespawn(ServerPlayer player, boolean isEndConquered);
    }

    @FunctionalInterface
    public interface StartTracking
    {
        void onStartTracking(ServerPlayer tracked, ServerPlayer tracker);
    }

    @FunctionalInterface
    public interface Death
    {
        void onDeath(ServerPlayer player, DamageSource source, CancelContext ctx);
    }

    @FunctionalInterface
    public interface AttackEntity
    {
        void onAttackEntity(ServerPlayer player, Entity target);
    }

    @FunctionalInterface
    public interface BreakBlock
    {
        void onBreakBlock(ServerPlayer player, Level level, BlockPos pos, BlockState state);
    }

    @FunctionalInterface
    public interface Clone
    {
        void onClone(ServerPlayer newPlayer, ServerPlayer oldPlayer, boolean isWasDeath);
    }

    @FunctionalInterface
    public interface EntityInteract
    {
        void onEntityInteract(ServerPlayer player, Entity target, InteractionHand hand);
    }

    @FunctionalInterface
    public interface ItemToss
    {
        void onItemToss(ServerPlayer player, ItemEntity item);
    }

    @FunctionalInterface
    public interface ItemPickup
    {
        void onItemPickup(ServerPlayer player, ItemEntity item, CancelContext ctx);
    }

    @FunctionalInterface
    public interface XpPickup
    {
        void onXpPickup(ServerPlayer player, ExperienceOrb orb);
    }

    @FunctionalInterface
    public interface ContainerClose
    {
        void onContainerClose(ServerPlayer player, AbstractContainerMenu container);
    }

    @FunctionalInterface
    public interface ChangeDimension
    {
        void onChangeDimension(ServerPlayer player, ResourceKey<Level> from, ResourceKey<Level> to);
    }

    @FunctionalInterface
    public interface ChangeGameMode
    {
        void onChangeGameMode(ServerPlayer player, GameType current, GameType next, CancelContext ctx);
    }

    @FunctionalInterface
    public interface XpChange
    {
        void onXpChange(ServerPlayer player, int amount, CancelContext ctx);
    }

    @FunctionalInterface
    public interface XpLevelChange
    {
        void onXpLevelChange(ServerPlayer player, int levels, CancelContext ctx);
    }

    @FunctionalInterface
    public interface UseItem
    {
        void onUseItem(ServerPlayer player, InteractionHand hand, ItemStack stack, CancelContext ctx);
    }

    @FunctionalInterface
    public interface RightClickBlock
    {
        void onRightClickBlock(ServerPlayer player, InteractionHand hand, BlockPos pos, Direction face, CancelContext ctx);
    }

    @FunctionalInterface
    public interface AnimalTame
    {
        void onAnimalTame(Animal animal, ServerPlayer player, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ItemCrafted
    {
        /** {@code craftingGrid} is the container holding the ingredients (may be {@code null} on Fabric). */
        void onItemCrafted(ServerPlayer player, ItemStack crafted, Container craftingGrid);
    }

    @FunctionalInterface
    public interface SetSpawn
    {
        /** {@code pos} is null when resetting to world spawn. */
        void onSetSpawn(ServerPlayer player, BlockPos pos, ResourceKey<Level> dimension, boolean forced, CancelContext ctx);
    }

    // ── Tier 7: Player interaction extended ──────────────────────────────────

    /**
     * Fired to compute how fast a player breaks a block. Modify {@code speed[0]} to change.
     * <p>
     * NeoForge: {@code PlayerEvent.BreakSpeed}<br>
     * Fabric: mixin on {@code Player#getDestroySpeed}
     */
    public static final PolyEvent<BreakSpeed> BREAK_SPEED = PolyEvent.create(
            handlers -> (player, state, pos, speed) ->
                    handlers.forEach(h -> h.onBreakSpeed(player, state, pos, speed)));

    /**
     * Fired to determine if a player can harvest (get drops from) a block.
     * Modify {@code result[0]} to override.
     * <p>
     * NeoForge: {@code PlayerEvent.HarvestCheck}<br>
     * Fabric: mixin on {@code Player#hasCorrectToolForDrops}
     */
    public static final PolyEvent<HarvestCheck> HARVEST_CHECK = PolyEvent.create(
            handlers -> (player, state, result) ->
                    handlers.forEach(h -> h.onHarvestCheck(player, state, result)));

    /**
     * Fired when bone meal is used on a block. Cancel to prevent effect.
     * <p>
     * NeoForge: {@code BonemealEvent}<br>
     * Fabric: mixin on {@code BoneMealItem#applyBonemeal}
     */
    public static final PolyEvent<BoneMeal> BONE_MEAL = PolyEvent.create(handlers -> (player, level, pos, state, ctx) ->
    {
        for (var h : handlers)
        {
            h.onBoneMeal(player, level, pos, state, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a player performs a sweep attack. Cancel to suppress the sweep.
     * <p>
     * NeoForge: {@code SweepAttackEvent}<br>
     * Fabric: mixin on {@code Player#attack} sweep loop
     */
    public static final PolyEvent<SweepAttack> SWEEP_ATTACK = PolyEvent.create(handlers -> (player, target, ctx) ->
    {
        for (var h : handlers)
        {
            h.onSweepAttack(player, target, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a player left-clicks a block (starts mining). Cancel to suppress.
     * <p>
     * NeoForge: {@code PlayerInteractEvent.LeftClickBlock}<br>
     * Fabric: {@code AttackBlockCallback}
     */
    public static final PolyEvent<LeftClickBlock> LEFT_CLICK_BLOCK = PolyEvent.create(handlers -> (player, pos, face, ctx) ->
    {
        for (var h : handlers)
        {
            h.onLeftClickBlock(player, pos, face, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a player right-clicks on empty air. Informational only.
     * <p>
     * NeoForge: {@code PlayerInteractEvent.RightClickEmpty}<br>
     * Fabric: no native equivalent
     */
    public static final PolyEvent<RightClickEmpty> RIGHT_CLICK_EMPTY = PolyEvent.create(
            handlers -> (player, hand) -> handlers.forEach(h -> h.onRightClickEmpty(player, hand)));

    /**
     * Fired when a player earns an advancement. Informational only.
     * <p>
     * NeoForge: {@code AdvancementEvent.AdvancementEarnEvent}<br>
     * Fabric: mixin on {@code PlayerAdvancements#award}
     */
    public static final PolyEvent<AdvancementEarn> ADVANCEMENT_EARN = PolyEvent.create(
            handlers -> (player, advancement) -> handlers.forEach(h -> h.onAdvancementEarn(player, advancement)));

    /**
     * Fired when a player opens a container menu. Informational only.
     * <p>
     * NeoForge: {@code PlayerContainerEvent.Open}<br>
     * Fabric: mixin on {@code Player#openMenu}
     */
    public static final PolyEvent<ContainerOpen> CONTAINER_OPEN = PolyEvent.create(
            handlers -> (player, menu) -> handlers.forEach(h -> h.onContainerOpen(player, menu)));

    /**
     * Fired when a player wakes up from sleeping. Informational only.
     * <p>
     * NeoForge: {@code PlayerWakeUpEvent}<br>
     * Fabric: {@code EntitySleepEvents.STOP_SLEEPING}
     */
    public static final PolyEvent<WakeUp> WAKE_UP = PolyEvent.create(
            handlers -> (player, updateLevel) -> handlers.forEach(h -> h.onWakeUp(player, updateLevel)));

    /**
     * Fired when a player took a smelted result from a furnace. Informational only.
     * <p>
     * NeoForge: {@code PlayerEvent.ItemSmeltedEvent}<br>
     * Fabric: mixin on {@code AbstractFurnaceBlockEntity} result slot take
     */
    public static final PolyEvent<ItemSmelted> ITEM_SMELTED = PolyEvent.create(
            handlers -> (player, stack) -> handlers.forEach(h -> h.onItemSmelted(player, stack)));

    // ── Tier 8: Player extended ───────────────────────────────────────────────

    /**
     * Fired when a statistic is being awarded to a player. Cancel to prevent the award.
     * <p>
     * NeoForge: {@code StatAwardEvent}<br>
     * Fabric: mixin on {@code ServerStatsCounter#increment}
     */
    public static final PolyEvent<StatAward> STAT_AWARD = PolyEvent.create(handlers -> (player, stat, value, ctx) ->
    {
        for (var h : handlers)
        {
            h.onStatAward(player, stat, value, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    // ── Tier 9: Mob/NPC extended ──────────────────────────────────────────────

    /**
     * Fired when a player trades with a villager or wandering trader. Informational only.
     * <p>
     * NeoForge: {@code TradeWithVillagerEvent}<br>
     * Fabric: mixin on {@code AbstractVillager#notifyTrade}
     */
    public static final PolyEvent<TradeWithVillager> TRADE_WITH_VILLAGER = PolyEvent.create(
            handlers -> (player, offer, merchant) -> handlers.forEach(h -> h.onTradeWithVillager(player, offer, merchant)));

    /**
     * Fired when phantom spawning is being checked for a player. Cancel to suppress.
     * <p>
     * NeoForge: {@code PlayerSpawnPhantomsEvent}<br>
     * Fabric: mixin on {@code PhantomSpawner#tick}
     */
    public static final PolyEvent<PhantomSpawn> PHANTOM_SPAWN = PolyEvent.create(handlers -> (player, ctx) ->
    {
        for (var h : handlers)
        {
            h.onPhantomSpawn(player, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    // ── Tier 10: Name / Save ─────────────────────────────────────────────────

    /**
     * Fired to format a player's display name. Modify {@code name[0]} to override.
     * <p>
     * NeoForge: {@code PlayerEvent.NameFormat}<br>
     * Fabric: mixin on {@code ServerPlayer#getDisplayName}
     */
    public static final PolyEvent<NameFormat> NAME_FORMAT = PolyEvent.create(
            handlers -> (player, name) -> handlers.forEach(h -> h.onNameFormat(player, name)));

    /**
     * Fired to format a player's tab-list name. Modify {@code name[0]} to override.
     * <p>
     * NeoForge: {@code PlayerEvent.TabListNameFormat}<br>
     * Fabric: mixin on tab-list update path
     */
    public static final PolyEvent<TabListNameFormat> TAB_LIST_NAME_FORMAT = PolyEvent.create(
            handlers -> (player, name) -> handlers.forEach(h -> h.onTabListNameFormat(player, name)));

    /**
     * Fired when player NBT data has been loaded from disk. Informational.
     * <p>
     * NeoForge: {@code PlayerEvent.LoadFromFile}<br>
     * Fabric: mixin on {@code ServerPlayer#readAdditionalSaveData}
     */
    public static final PolyEvent<PlayerFileEvent> PLAYER_LOAD = PolyEvent.create(
            handlers -> (player, uuid) -> handlers.forEach(h -> h.onPlayerFile(player, uuid)));

    /**
     * Fired when player NBT data is being saved to disk. Informational.
     * <p>
     * NeoForge: {@code PlayerEvent.SaveToFile}<br>
     * Fabric: mixin on {@code ServerPlayer#addAdditionalSaveData}
     */
    public static final PolyEvent<PlayerFileEvent> PLAYER_SAVE = PolyEvent.create(
            handlers -> (player, uuid) -> handlers.forEach(h -> h.onPlayerFile(player, uuid)));

    /**
     * Fired when a player brews a potion. Informational only.
     * <p>
     * NeoForge: {@code PlayerBrewedPotionEvent}<br>
     * Fabric: mixin on {@code BrewingStandBlockEntity#doBrew}
     */
    public static final PolyEvent<BrewedPotion> BREWED_POTION = PolyEvent.create(
            handlers -> (player, stack) -> handlers.forEach(h -> h.onBrewedPotion(player, stack)));

    // ── Tier 15 ───────────────────────────────────────────────────────────────

    /**
     * Fires after a player has successfully picked up an item entity.
     * Completes the pair alongside the already-implemented {@link #ITEM_PICKUP} (Pre).
     * <p>
     * NeoForge: {@code ItemEntityPickupEvent.Post}<br>
     * Fabric: mixin on {@code ItemEntity#playerTouch} at RETURN
     */
    public static final PolyEvent<ItemPickupPost> ITEM_PICKUP_POST = PolyEvent.create(
            handlers -> (player, itemEntity, stack) ->
                    handlers.forEach(h -> h.onItemPickupPost(player, itemEntity, stack)));

    // ── Tier 11 ───────────────────────────────────────────────────────────────

    /**
     * Fired when a player stops tracking an entity (complement to {@link #START_TRACKING}).
     * <p>
     * NeoForge: {@code PlayerEvent.StopTracking}<br>
     * Fabric: {@code EntityTrackingEvents.STOP_TRACKING}
     */
    public static final PolyEvent<StopTracking> STOP_TRACKING = PolyEvent.create(
            handlers -> (player, entity) -> handlers.forEach(h -> h.onStopTracking(player, entity)));

    /**
     * Fired when a player's permission level (op level) has changed.
     * <p>
     * NeoForge: {@code PermissionsChangedEvent}<br>
     * Fabric: mixin on {@code PlayerList#op} and {@code PlayerList#deop}
     */
    public static final PolyEvent<PermissionsChanged> PERMISSIONS_CHANGED = PolyEvent.create(
            handlers -> player -> handlers.forEach(h -> h.onPermissionsChanged(player)));

    /**
     * Fired when a player's client settings have been updated (language, render distance, etc.).
     * <p>
     * NeoForge: {@code ClientInformationUpdatedEvent}<br>
     * Fabric: mixin on {@code ServerGamePacketListenerImpl#handleClientInformation}
     */
    public static final PolyEvent<ClientInfoUpdated> CLIENT_INFORMATION_UPDATED = PolyEvent.create(
            handlers -> (player, info) -> handlers.forEach(h -> h.onClientInformationUpdated(player, info)));

    /**
     * Fired when a player's item is destroyed (reaches 0 durability). Informational only.
     * <p>
     * NeoForge: {@code PlayerDestroyItemEvent}<br>
     * Fabric: mixin on {@code LivingEntity#broadcastBreakEvent}
     */
    public static final PolyEvent<DestroyItem> DESTROY_ITEM = PolyEvent.create(
            handlers -> (player, stack, slot) -> handlers.forEach(h -> h.onDestroyItem(player, stack, slot)));

    /**
     * Fired when a player enchants an item at the enchanting table. Informational only.
     * <p>
     * NeoForge: {@code PlayerEnchantItemEvent}<br>
     * Fabric: mixin on {@code EnchantmentMenu#clickMenuButton}
     */
    public static final PolyEvent<EnchantItem> ENCHANT_ITEM = PolyEvent.create(
            handlers -> (player, stack, cost) -> handlers.forEach(h -> h.onEnchantItem(player, stack, cost)));

    /**
     * Fired when a player "falls" while in creative flight mode. Cancellable.
     * <p>
     * NeoForge: {@code PlayerFlyableFallEvent}<br>
     * Fabric: mixin on {@code Player#causeFallDamage} when {@code isCreative()}
     */
    public static final PolyEvent<FlyableFall> FLYABLE_FALL = PolyEvent.create(handlers -> (player, ctx) ->
    {
        for (var h : handlers)
        {
            h.onFlyableFall(player, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired each tick to check whether a sleeping player can continue sleeping.
     * Set {@code result[0]} to a non-null {@code BedSleepingProblem} to wake the player.
     * <p>
     * NeoForge: {@code CanContinueSleepingEvent}<br>
     * Fabric: mixin on {@code Player#stopSleepInBed} tick path
     */
    public static final PolyEvent<CanContinueSleeping> CAN_CONTINUE_SLEEPING = PolyEvent.create(
            handlers -> (player, result) -> handlers.forEach(h -> h.onCanContinueSleeping(player, result)));

    /**
     * Fired to allow overriding a player's respawn position/dimension.
     * Modify {@code pos[0]} or {@code dimension[0]} to override.
     * <p>
     * NeoForge: {@code PlayerRespawnPositionEvent}<br>
     * Fabric: mixin on {@code ServerPlayer#findRespawnPositionAndUseSpawnBlock}
     */
    public static final PolyEvent<RespawnPosition> RESPAWN_POSITION = PolyEvent.create(
            handlers -> (player, pos, dimension) -> handlers.forEach(h -> h.onRespawnPosition(player, pos, dimension)));

    /**
     * Fired when a player's advancement is revoked. Informational only.
     * <p>
     * NeoForge: {@code AdvancementEvent.AdvancementRevokedEvent}<br>
     * Fabric: mixin on {@code PlayerAdvancements#revoke}
     */
    public static final PolyEvent<AdvancementRevoke> ADVANCEMENT_REVOKE = PolyEvent.create(
            handlers -> (player, advancement) -> handlers.forEach(h -> h.onAdvancementRevoke(player, advancement)));

    // ── Tier 7 interfaces ────────────────────────────────────────────────────

    @FunctionalInterface
    public interface BreakSpeed
    {
        /** {@code speed[0]} — current break speed (mutable). Increase to speed up, 0 to halt. */
        void onBreakSpeed(Player player, BlockState state, BlockPos pos, float[] speed);
    }

    @FunctionalInterface
    public interface HarvestCheck
    {
        /** {@code result[0]} — default harvest decision (mutable). Set false to deny drops. */
        void onHarvestCheck(Player player, BlockState state, boolean[] result);
    }

    @FunctionalInterface
    public interface BoneMeal
    {
        void onBoneMeal(Player player, Level level, BlockPos pos, BlockState state, CancelContext ctx);
    }

    @FunctionalInterface
    public interface SweepAttack
    {
        void onSweepAttack(Player player, Entity target, CancelContext ctx);
    }

    @FunctionalInterface
    public interface LeftClickBlock
    {
        void onLeftClickBlock(Player player, BlockPos pos, Direction face, CancelContext ctx);
    }

    @FunctionalInterface
    public interface RightClickEmpty
    {
        void onRightClickEmpty(Player player, InteractionHand hand);
    }

    @FunctionalInterface
    public interface AdvancementEarn
    {
        void onAdvancementEarn(ServerPlayer player, AdvancementHolder advancement);
    }

    @FunctionalInterface
    public interface ContainerOpen
    {
        void onContainerOpen(Player player, AbstractContainerMenu menu);
    }

    @FunctionalInterface
    public interface WakeUp
    {
        void onWakeUp(Player player, boolean updateLevel);
    }

    @FunctionalInterface
    public interface ItemSmelted
    {
        void onItemSmelted(Player player, ItemStack stack);
    }

    // ── Tier 8 interfaces ────────────────────────────────────────────────────

    @FunctionalInterface
    public interface StatAward
    {
        void onStatAward(Player player, Stat<?> stat, int value, CancelContext ctx);
    }

    // ── Tier 9 interfaces ────────────────────────────────────────────────────

    @FunctionalInterface
    public interface TradeWithVillager
    {
        void onTradeWithVillager(Player player, MerchantOffer offer, AbstractVillager merchant);
    }

    @FunctionalInterface
    public interface PhantomSpawn
    {
        void onPhantomSpawn(ServerPlayer player, CancelContext ctx);
    }

    // ── Tier 10 interfaces ────────────────────────────────────────────────────

    @FunctionalInterface
    public interface NameFormat
    {
        /** {@code name[0]} — current display name (mutable). */
        void onNameFormat(ServerPlayer player, Component[] name);
    }

    @FunctionalInterface
    public interface TabListNameFormat
    {
        /** {@code name[0]} — current tab-list name (mutable). */
        void onTabListNameFormat(ServerPlayer player, Component[] name);
    }

    @FunctionalInterface
    public interface PlayerFileEvent
    {
        /** {@code uuid} — the player's UUID string. */
        void onPlayerFile(ServerPlayer player, String uuid);
    }

    @FunctionalInterface
    public interface BrewedPotion
    {
        void onBrewedPotion(Player player, ItemStack stack);
    }

    // ── Tier 15 interfaces ────────────────────────────────────────────────────

    @FunctionalInterface
    public interface ItemPickupPost
    {
        /**
         * @param player     The player that picked up the item.
         * @param itemEntity The item entity that was consumed (already removed from world).
         * @param stack      The ItemStack that was merged into the player's inventory.
         */
        void onItemPickupPost(Player player, ItemEntity itemEntity, ItemStack stack);
    }

    // ── Tier 11 interfaces ────────────────────────────────────────────────────

    @FunctionalInterface
    public interface StopTracking
    {
        void onStopTracking(ServerPlayer player, Entity entity);
    }

    @FunctionalInterface
    public interface PermissionsChanged
    {
        void onPermissionsChanged(ServerPlayer player);
    }

    @FunctionalInterface
    public interface ClientInfoUpdated
    {
        void onClientInformationUpdated(ServerPlayer player, ClientInformation info);
    }

    @FunctionalInterface
    public interface DestroyItem
    {
        /** {@code slot} may be null if the broken item was not in an equipment slot. */
        void onDestroyItem(Player player, ItemStack stack, @Nullable EquipmentSlot slot);
    }

    @FunctionalInterface
    public interface EnchantItem
    {
        void onEnchantItem(Player player, ItemStack stack, int cost);
    }

    @FunctionalInterface
    public interface FlyableFall
    {
        void onFlyableFall(Player player, CancelContext ctx);
    }

    @FunctionalInterface
    public interface CanContinueSleeping
    {
        /** {@code result[0]} — null = no problem; set to a {@code BedSleepingProblem} to wake the player. */
        void onCanContinueSleeping(Player player, Player.BedSleepingProblem[] result);
    }

    @FunctionalInterface
    public interface RespawnPosition
    {
        /**
         * @param pos       Mutable 1-element array — current BlockPos respawn target (may be null for world spawn).
         * @param dimension Mutable 1-element array — respawn dimension key.
         */
        void onRespawnPosition(ServerPlayer player, BlockPos[] pos, ResourceKey<Level>[] dimension);
    }

    @FunctionalInterface
    public interface AdvancementRevoke
    {
        void onAdvancementRevoke(ServerPlayer player, AdvancementHolder advancement);
    }
}

