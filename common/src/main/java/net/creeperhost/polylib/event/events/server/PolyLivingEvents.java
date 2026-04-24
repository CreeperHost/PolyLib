package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public final class PolyLivingEvents
{
    /**
     * Fired before incoming damage is applied (before armor/enchantment calc).
     * Call {@link CancelContext#cancel()} to prevent the damage.
     */
    public static final PolyEvent<DamagePre> DAMAGE_PRE = PolyEvent.create(handlers -> (entity, source, amount, ctx) ->
    {
        for (var h : handlers)
        {
            h.onDamagePre(entity, source, amount, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /** Fired after damage has been applied. */
    public static final PolyEvent<DamagePost> DAMAGE_POST = PolyEvent.create(handlers -> (entity, source, amount) -> handlers.forEach(h -> h.onDamagePost(entity, source, amount)));

    /**
     * Fired when a living entity is about to die.
     * Call {@link CancelContext#cancel()} to prevent the death.
     */
    public static final PolyEvent<Death> DEATH = PolyEvent.create(handlers -> (entity, source, ctx) ->
    {
        for (var h : handlers)
        {
            h.onDeath(entity, source, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /** Fired when a living entity lands from a fall. distance is in blocks (double). */
    public static final PolyEvent<Fall> FALL = PolyEvent.create(handlers -> (entity, distance, multiplier) -> handlers.forEach(h -> h.onFall(entity, distance, multiplier)));

    /** Fired when a living entity generates drops on death. */
    public static final PolyEvent<Drops> DROPS = PolyEvent.create(handlers -> (entity, source, drops) -> handlers.forEach(h -> h.onDrops(entity, source, drops)));

    /** Fired when a living entity is about to drop XP on death. */
    public static final PolyEvent<XpDrop> XP_DROP = PolyEvent.create(handlers -> (entity, amount) -> handlers.forEach(h -> h.onXpDrop(entity, amount)));

    /**
     * Fired before damage is applied but after armor/enchantment reduction (post-armor, pre-application).
     * This matches NeoForge {@code LivingDamageEvent.Pre}.
     * Call {@link CancelContext#cancel()} to prevent the damage.
     */
    public static final PolyEvent<DamageFinalPre> DAMAGE_FINAL_PRE = PolyEvent.create(handlers -> (entity, source, amount, ctx) ->
    {
        for (var h : handlers)
        {
            h.onDamageFinalPre(entity, source, amount, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a living entity is being healed.
     * Call {@link CancelContext#cancel()} to prevent the healing.
     */
    public static final PolyEvent<Heal> HEAL = PolyEvent.create(handlers -> (entity, amount, ctx) ->
    {
        for (var h : handlers)
        {
            h.onHeal(entity, amount, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a mob is changing its attack target.
     * Call {@link CancelContext#cancel()} to prevent the target change.
     */
    public static final PolyEvent<ChangeTarget> CHANGE_TARGET = PolyEvent.create(handlers -> (entity, newTarget, ctx) ->
    {
        for (var h : handlers)
        {
            h.onChangeTarget(entity, newTarget, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired before a living entity converts to another type (e.g. zombie to drowned).
     * Call {@link CancelContext#cancel()} to prevent the conversion.
     * <p>Note: Fabric cancel not supported without a mixin.
     */
    public static final PolyEvent<ConversionPre> CONVERSION_PRE = PolyEvent.create(handlers -> (entity, ctx) ->
    {
        for (var h : handlers)
        {
            h.onConversionPre(entity, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired after a living entity has converted to another type.
     * <p>Fabric: bridged via {@code ServerLivingEntityEvents.MOB_CONVERSION}.
     */
    public static final PolyEvent<ConversionPost> CONVERSION_POST = PolyEvent.create(
            handlers -> (original, converted) -> handlers.forEach(h -> h.onConversionPost(original, converted)));

    /**
     * Fired when a living entity is being knocked back.
     * Call {@link CancelContext#cancel()} to prevent the knockback.
     */
    public static final PolyEvent<Knockback> KNOCKBACK = PolyEvent.create(handlers -> (entity, strength, ratioX, ratioZ, ctx) ->
    {
        for (var h : handlers)
        {
            h.onKnockback(entity, strength, ratioX, ratioZ, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a living entity is about to use a Totem of Undying.
     * Call {@link CancelContext#cancel()} to prevent the totem from triggering.
     * <p>Note: Fabric cancel support is best-effort (see mixin).
     */
    public static final PolyEvent<UseTotem> USE_TOTEM = PolyEvent.create(handlers -> (entity, source, ctx) ->
    {
        for (var h : handlers)
        {
            h.onUseTotem(entity, source, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a player performs a melee attack to determine if it is a critical hit.
     * Use {@link CriticalHitContext} to override whether the hit is critical and the damage multiplier.
     * <p>
     * NeoForge: {@code CriticalHitEvent}<br>
     * Fabric: TODO (non-trivial local capture in {@code Player#attack})
     */
    public static final PolyEvent<CriticalHit> CRITICAL_HIT = PolyEvent.create(
            handlers -> (player, target, ctx) -> handlers.forEach(h -> h.onCriticalHit(player, target, ctx)));

    /**
     * Fired when a player begins drawing a bow.
     * Call {@link CancelContext#cancel()} to prevent drawing.
     * <p>
     * NeoForge: {@code ArrowNockEvent}<br>
     * Fabric: mixin into {@code BowItem#use}
     */
    public static final PolyEvent<ArrowNock> ARROW_NOCK = PolyEvent.create(handlers -> (player, bow, hand, ctx) ->
    {
        for (var h : handlers)
        {
            h.onArrowNock(player, bow, hand, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a player releases a drawn bow (fires the arrow).
     * Call {@link CancelContext#cancel()} to prevent firing.
     * <p>
     * NeoForge: {@code ArrowLooseEvent}<br>
     * Fabric: mixin into {@code BowItem#releaseUsing}
     */
    public static final PolyEvent<ArrowLoose> ARROW_LOOSE = PolyEvent.create(handlers -> (player, bow, charge, ctx) ->
    {
        for (var h : handlers)
        {
            h.onArrowLoose(player, bow, charge, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a living entity attempts to block damage with a shield.
     * Call {@link CancelContext#cancel()} to bypass the block (shield doesn't absorb).
     * <p>
     * Note: On Fabric, the exact {@code damageBlocked} float is unavailable — {@code 0f} is passed.
     * <p>
     * NeoForge: {@code LivingShieldBlockEvent}<br>
     * Fabric: mixin into {@code LivingEntity#isDamageSourceBlocked}
     */
    public static final PolyEvent<ShieldBlock> SHIELD_BLOCK = PolyEvent.create(handlers -> (entity, source, damageBlocked, ctx) ->
    {
        for (var h : handlers)
        {
            h.onShieldBlock(entity, source, damageBlocked, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    private PolyLivingEvents() {}

    // ── Tier 3 events ───────────────────────────────────────────────────────

    /**
     * Fired when a living entity changes an equipment slot (armour, hand, etc.).
     * Informational only.
     * <p>
     * NeoForge: {@code LivingEquipmentChangeEvent}<br>
     * Fabric: mixin into {@code LivingEntity#onEquipItem}
     */
    public static final PolyEvent<EquipChange> ENTITY_EQUIP_CHANGE = PolyEvent.create(
            handlers -> (entity, slot, from, to) -> handlers.forEach(h -> h.onEquipChange(entity, slot, from, to)));

    /**
     * Fired when a living entity starts using an item (e.g. eating, drawing bow).
     * Call {@link CancelContext#cancel()} to prevent the use from starting.
     * <p>
     * NeoForge: {@code LivingEntityUseItemEvent.Start}<br>
     * Fabric: mixin into {@code LivingEntity#startUsingItem}
     */
    public static final PolyEvent<ItemUseStart> ITEM_USE_START = PolyEvent.create(handlers -> (entity, item, ctx) ->
    {
        for (var h : handlers)
        {
            h.onItemUseStart(entity, item, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a living entity finishes using an item.
     * Modify {@code resultHolder[0]} to override the resulting stack.
     * <p>
     * NeoForge: {@code LivingEntityUseItemEvent.Finish}<br>
     * Fabric: mixin into {@code LivingEntity#completeUsingItem}
     */
    public static final PolyEvent<ItemUseFinish> ITEM_USE_FINISH = PolyEvent.create(
            handlers -> (entity, item, resultHolder) -> handlers.forEach(h -> h.onItemUseFinish(entity, item, resultHolder)));

    /**
     * Fired when a slime-like mob is about to split into smaller copies.
     * Call {@link CancelContext#cancel()} to prevent the split.
     * <p>
     * NeoForge: {@code MobSplitEvent}<br>
     * Fabric: mixin into {@code Slime#remove}
     */
    public static final PolyEvent<MobSplit> MOB_SPLIT = PolyEvent.create(handlers -> (parent, ctx) ->
    {
        for (var h : handlers)
        {
            h.onMobSplit(parent, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when two animals are about to produce a baby.
     * Call {@link CancelContext#cancel()} to prevent the baby from spawning.
     * <p>
     * NeoForge: {@code BabyEntitySpawnEvent}<br>
     * Fabric: mixin into {@code Animal#spawnChildFromBreeding}
     */
    public static final PolyEvent<BabySpawn> BABY_SPAWN = PolyEvent.create(handlers -> (parentA, parentB, causedBy, child, ctx) ->
    {
        for (var h : handlers)
        {
            h.onBabySpawn(parentA, parentB, causedBy, child, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a living entity is about to take drowning damage.
     * Call {@link CancelContext#cancel()} to prevent the drowning damage this tick.
     * <p>
     * NeoForge: {@code LivingDrownEvent}<br>
     * Fabric: mixin stub (best-effort)
     */
    public static final PolyEvent<Drown> DROWN = PolyEvent.create(handlers -> (entity, ctx) ->
    {
        for (var h : handlers)
        {
            h.onDrown(entity, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired each tick to determine how a living entity breathes.
     * Use {@link BreatheContext} to modify air consumption and replenishment.
     * Informational/modifiable — not cancellable.
     * <p>
     * NeoForge: {@code LivingBreatheEvent}<br>
     * Fabric: mixin stub (best-effort)
     */
    public static final PolyEvent<Breathe> BREATHE = PolyEvent.create(
            handlers -> (entity, ctx) -> handlers.forEach(h -> h.onBreathe(entity, ctx)));

    /**
     * Fired when a living entity destroys a block (e.g. Enderman picking up a block).
     * Call {@link CancelContext#cancel()} to prevent the block from being destroyed.
     * <p>
     * NeoForge: {@code LivingDestroyBlockEvent}<br>
     * Fabric: mixin into block-destruction logic
     */
    public static final PolyEvent<DestroyBlock> DESTROY_BLOCK = PolyEvent.create(handlers -> (entity, level, pos, state, ctx) ->
    {
        for (var h : handlers)
        {
            h.onDestroyBlock(entity, level, pos, state, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a living entity is determining which projectile to use.
     * Modify {@code resultHolder[0]} to override the chosen projectile stack.
     * <p>
     * NeoForge: {@code LivingGetProjectileEvent}<br>
     * Fabric: mixin into {@code LivingEntity#getProjectile}
     */
    public static final PolyEvent<GetProjectile> GET_PROJECTILE = PolyEvent.create(
            handlers -> (entity, resultHolder) -> handlers.forEach(h -> h.onGetProjectile(entity, resultHolder)));

    /**
     * Fired when a living entity swaps items between main and offhand (press F).
     * Call {@link CancelContext#cancel()} to prevent the swap.
     * <p>
     * NeoForge: {@code LivingSwapItemsEvent.Hands}<br>
     * Fabric: mixin into swap-hand packet handler
     */
    public static final PolyEvent<SwapItems> SWAP_ITEMS = PolyEvent.create(handlers -> (entity, toMainHand, toOffHand, ctx) ->
    {
        for (var h : handlers)
        {
            h.onSwapItems(entity, toMainHand, toOffHand, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    @FunctionalInterface
    public interface DamagePre
    {
        void onDamagePre(LivingEntity entity, DamageSource source, float amount, CancelContext ctx);
    }

    @FunctionalInterface
    public interface DamagePost
    {
        void onDamagePost(LivingEntity entity, DamageSource source, float amount);
    }

    @FunctionalInterface
    public interface Death
    {
        void onDeath(LivingEntity entity, DamageSource source, CancelContext ctx);
    }

    @FunctionalInterface
    public interface Fall
    {
        void onFall(LivingEntity entity, double distance, float multiplier);
    }

    @FunctionalInterface
    public interface Drops
    {
        void onDrops(LivingEntity entity, DamageSource source, List<ItemEntity> drops);
    }

    @FunctionalInterface
    public interface XpDrop
    {
        void onXpDrop(LivingEntity entity, int amount);
    }

    @FunctionalInterface
    public interface DamageFinalPre
    {
        void onDamageFinalPre(LivingEntity entity, DamageSource source, float amount, CancelContext ctx);
    }

    @FunctionalInterface
    public interface Heal
    {
        void onHeal(LivingEntity entity, float amount, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ChangeTarget
    {
        void onChangeTarget(LivingEntity entity, LivingEntity newTarget, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ConversionPre
    {
        void onConversionPre(LivingEntity entity, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ConversionPost
    {
        void onConversionPost(LivingEntity original, LivingEntity converted);
    }

    @FunctionalInterface
    public interface Knockback
    {
        void onKnockback(LivingEntity entity, float strength, double ratioX, double ratioZ, CancelContext ctx);
    }

    @FunctionalInterface
    public interface UseTotem
    {
        void onUseTotem(LivingEntity entity, DamageSource source, CancelContext ctx);
    }

    @FunctionalInterface
    public interface CriticalHit
    {
        void onCriticalHit(ServerPlayer player, Entity target, CriticalHitContext ctx);
    }

    @FunctionalInterface
    public interface ArrowNock
    {
        void onArrowNock(ServerPlayer player, ItemStack bow, InteractionHand hand, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ArrowLoose
    {
        void onArrowLoose(ServerPlayer player, ItemStack bow, int charge, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ShieldBlock
    {
        void onShieldBlock(LivingEntity entity, DamageSource source, float damageBlocked, CancelContext ctx);
    }

    /** Mutable result context for critical-hit determination. */
    public static final class CriticalHitContext
    {
        private boolean isCritical;
        private float damageMultiplier;

        public CriticalHitContext(boolean isCritical, float multiplier)
        {
            this.isCritical = isCritical;
            this.damageMultiplier = multiplier;
        }

        public boolean isCritical() { return isCritical; }

        public void setCritical(boolean value) { isCritical = value; }

        public float getDamageMultiplier() { return damageMultiplier; }

        public void setDamageMultiplier(float value) { damageMultiplier = value; }
    }

    @FunctionalInterface
    public interface EquipChange
    {
        void onEquipChange(LivingEntity entity, EquipmentSlot slot, ItemStack from, ItemStack to);
    }

    @FunctionalInterface
    public interface ItemUseStart
    {
        void onItemUseStart(LivingEntity entity, ItemStack item, CancelContext ctx);
    }

    @FunctionalInterface
    public interface ItemUseFinish
    {
        void onItemUseFinish(LivingEntity entity, ItemStack item, ItemStack[] resultHolder);
    }

    @FunctionalInterface
    public interface MobSplit
    {
        void onMobSplit(LivingEntity parent, CancelContext ctx);
    }

    @FunctionalInterface
    public interface BabySpawn
    {
        void onBabySpawn(LivingEntity parentA, LivingEntity parentB, Player causedBy, AgeableMob child, CancelContext ctx);
    }

    @FunctionalInterface
    public interface Drown
    {
        void onDrown(LivingEntity entity, CancelContext ctx);
    }

    @FunctionalInterface
    public interface Breathe
    {
        void onBreathe(LivingEntity entity, BreatheContext ctx);
    }

    @FunctionalInterface
    public interface DestroyBlock
    {
        void onDestroyBlock(LivingEntity entity, LevelAccessor level, BlockPos pos, BlockState state, CancelContext ctx);
    }

    @FunctionalInterface
    public interface GetProjectile
    {
        void onGetProjectile(LivingEntity entity, ItemStack[] resultHolder);
    }

    @FunctionalInterface
    public interface SwapItems
    {
        void onSwapItems(LivingEntity entity, ItemStack toMainHand, ItemStack toOffHand, CancelContext ctx);
    }

    // ── Tier 9 ────────────────────────────────────────────────────────────────

    /**
     * Fired when determining the cluster size for a mob spawn. Modify {@code size} field to change.
     * <p>
     * NeoForge: {@code SpawnClusterSizeEvent}<br>
     * Fabric: mixin on {@code Mob#getMaxSpawnClusterSize}
     */
    public static final PolyEvent<SpawnClusterSize> SPAWN_CLUSTER_SIZE = PolyEvent.create(
            handlers -> (mob, size) -> handlers.forEach(h -> h.onSpawnClusterSize(mob, size)));

    /**
     * Fired when an Enderman begins to get angry at a player. Cancel to prevent.
     * <p>
     * NeoForge: {@code EnderManAngerEvent}<br>
     * Fabric: mixin on {@code EnderMan#isLookingAtMe}
     */
    public static final PolyEvent<EndermanAnger> ENDERMAN_ANGER = PolyEvent.create(handlers -> (enderman, player, ctx) ->
    {
        for (var h : handlers)
        {
            h.onEndermanAnger(enderman, player, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a living entity kills another living entity. Informational.
     * <p>
     * NeoForge: mixin on {@code LivingEntity#die} (no native event)<br>
     * Fabric: mixin on {@code LivingEntity#die}
     */
    public static final PolyEvent<EntityKilledOther> ENTITY_KILLED_OTHER = PolyEvent.create(
            handlers -> (killer, victim) -> handlers.forEach(h -> h.onEntityKilledOther(killer, victim)));

    /**
     * Fired when armor items are about to receive damage. Modify damage via {@code ArmorHurtContext}.
     * <p>
     * NeoForge: {@code ArmorHurtEvent}<br>
     * Fabric: mixin on {@code LivingEntity#hurtArmor}
     */
    public static final PolyEvent<ArmorHurt> ARMOR_HURT = PolyEvent.create(
            handlers -> (entity, source, ctx) -> handlers.forEach(h -> h.onArmorHurt(entity, source, ctx)));

    // ── Tier 10 ───────────────────────────────────────────────────────────────

    /**
     * Fired to determine if a living entity may begin elytra gliding. Modify {@code result[0]} to override.
     * <p>
     * NeoForge: mixin on {@code LivingEntity#isFallFlying} / {@code Player#tryToStartFallFlying}<br>
     * Fabric: {@code EntityElytraEvents.ALLOW}
     */
    public static final PolyEvent<ElytraAllow> ELYTRA_ALLOW = PolyEvent.create(
            handlers -> (entity, result) -> handlers.forEach(h -> h.onElytraAllow(entity, result)));

    // ── Tier 15 ───────────────────────────────────────────────────────────────

    /**
     * Allows modifying the XP value dropped when a living entity is killed.
     * Modify {@code xp[0]} to override the amount.
     * <p>
     * NeoForge: {@code LivingExperienceDropEvent} (folded with XP_DROP handler)<br>
     * Fabric: {@code ServerLivingEntityEvents.MOB_KILL_XP}
     */
    public static final PolyEvent<MobKillXp> MOB_KILL_XP = PolyEvent.create(
            handlers -> (victim, killer, xp) -> handlers.forEach(h -> h.onMobKillXp(victim, killer, xp)));

    @FunctionalInterface
    public interface SpawnClusterSize
    {
        /** {@code size[0]} — default cluster size (mutable). */
        void onSpawnClusterSize(Mob mob, int[] size);
    }

    @FunctionalInterface
    public interface EndermanAnger
    {
        void onEndermanAnger(EnderMan enderman, Player player, CancelContext ctx);
    }

    @FunctionalInterface
    public interface EntityKilledOther
    {
        void onEntityKilledOther(LivingEntity killer, LivingEntity victim);
    }

    /**
     * Mutable context for the armor-hurt event.
     * Use {@link #setDamage(EquipmentSlot, float)} to modify per-slot damage.
     */
    public static final class ArmorHurtContext
    {
        private final java.util.EnumMap<EquipmentSlot, Float> damageMap;

        public ArmorHurtContext(java.util.EnumMap<EquipmentSlot, Float> damageMap)
        {
            this.damageMap = new java.util.EnumMap<>(damageMap);
        }

        public float getDamage(EquipmentSlot slot) {
            Float v = damageMap.get(slot);
            return v == null ? 0f : v;
        }

        public void setDamage(EquipmentSlot slot, float damage) { damageMap.put(slot, damage); }

        public java.util.Map<EquipmentSlot, Float> getDamageMap() { return java.util.Collections.unmodifiableMap(damageMap); }
    }

    @FunctionalInterface
    public interface ArmorHurt
    {
        void onArmorHurt(LivingEntity entity, DamageSource source, ArmorHurtContext ctx);
    }

    @FunctionalInterface
    public interface ElytraAllow
    {
        /** {@code result[0]} true = allow glide, false = deny. */
        void onElytraAllow(LivingEntity entity, boolean[] result);
    }

    /** Mutable context for breathe-event air consumption and replenishment. */
    public static final class BreatheContext
    {
        private boolean canBreathe;
        private int consumeAirAmount;
        private int refillAirAmount;

        public BreatheContext(boolean canBreathe, int consumeAirAmount, int refillAirAmount)
        {
            this.canBreathe = canBreathe;
            this.consumeAirAmount = consumeAirAmount;
            this.refillAirAmount = refillAirAmount;
        }

        public boolean canBreathe() { return canBreathe; }

        public void setCanBreathe(boolean canBreathe) { this.canBreathe = canBreathe; }

        public int getConsumeAirAmount() { return consumeAirAmount; }

        public void setConsumeAirAmount(int consumeAirAmount) { this.consumeAirAmount = consumeAirAmount; }

        public int getRefillAirAmount() { return refillAirAmount; }

        public void setRefillAirAmount(int refillAirAmount) { this.refillAirAmount = refillAirAmount; }
    }

    // ── Tier 15 interfaces ────────────────────────────────────────────────────

    @FunctionalInterface
    public interface MobKillXp
    {
        /**
         * @param victim The living entity that was killed.
         * @param killer The entity that performed the kill (may be null).
         * @param xp     Mutable int wrapper — set {@code xp[0]} to override XP dropped.
         */
        void onMobKillXp(LivingEntity victim, Entity killer, int[] xp);
    }
}
