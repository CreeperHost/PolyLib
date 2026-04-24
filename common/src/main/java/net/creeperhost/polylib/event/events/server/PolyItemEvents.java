package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class PolyItemEvents
{
    // ── Tier 4: Item use lifecycle ───────────────────────────────────────────

    /**
     * Fired when a living entity starts using an item (bow draw, eating, etc.).
     * Call {@link CancelContext#cancel()} to prevent use from starting.
     * Includes {@code duration} — the total use-ticks of this item.
     * <p>
     * NeoForge: {@code LivingEntityUseItemEvent.Start}<br>
     * Fabric: mixin into {@code LivingEntity#startUsingItem}
     */
    public static final PolyEvent<UseStart> ITEM_USE_START = PolyEvent.create(handlers -> (entity, stack, duration, ctx) ->
    {
        for (var h : handlers)
        {
            h.onUseStart(entity, stack, duration, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired each tick while a living entity is using an item.
     * Call {@link CancelContext#cancel()} to stop the use mid-tick.
     * {@code ticksRemaining} counts down from the total duration.
     * <p>
     * NeoForge: {@code LivingEntityUseItemEvent.Tick}<br>
     * Fabric: mixin into {@code LivingEntity#updatingUsingItem} tick path
     */
    public static final PolyEvent<UseTick> ITEM_USE_TICK = PolyEvent.create(handlers -> (entity, stack, ticksRemaining, ctx) ->
    {
        for (var h : handlers)
        {
            h.onUseTick(entity, stack, ticksRemaining, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a living entity finishes using an item (use completed fully).
     * Informational only — cannot be cancelled at this stage.
     * <p>
     * NeoForge: {@code LivingEntityUseItemEvent.Finish}<br>
     * Fabric: mixin into {@code LivingEntity#completeUsingItem}
     */
    public static final PolyEvent<UseFinish> ITEM_USE_FINISH = PolyEvent.create(
            handlers -> (entity, stack) -> handlers.forEach(h -> h.onUseFinish(entity, stack)));

    /**
     * Fired when an {@link ItemEntity} expires naturally (lifespan runs out).
     * Informational only — cannot be cancelled.
     * <p>
     * NeoForge: {@code ItemExpireEvent}<br>
     * Fabric: mixin into {@code ItemEntity#tick}
     */
    public static final PolyEvent<ItemExpire> ITEM_EXPIRE = PolyEvent.create(
            handlers -> item -> handlers.forEach(h -> h.onItemExpire(item)));

    /**
     * Fired when a fishing hook retrieves its catch.
     * Call {@link CancelContext#cancel()} to prevent the items being spawned.
     * <p>
     * NeoForge: {@code ItemFishedEvent} (cancellable)<br>
     * Fabric: mixin into {@code FishingHook#retrieve}
     */
    public static final PolyEvent<ItemFishing> ITEM_FISHING = PolyEvent.create(handlers -> (player, drops, ctx) ->
    {
        for (var h : handlers)
        {
            h.onItemFishing(player, drops, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when items are placed in a grindstone and the output is computed.
     * Use {@link GrindstoneContext} to read or modify the output and XP.
     * <p>
     * NeoForge: {@code GrindstoneEvent.OnPlaceItem}<br>
     * Fabric: mixin into {@code GrindstoneMenu#slotsChanged}
     */
    public static final PolyEvent<Grindstone> GRINDSTONE = PolyEvent.create(
            handlers -> (topItem, bottomItem, ctx) -> handlers.forEach(h -> h.onGrindstone(topItem, bottomItem, ctx)));

    /**
     * Fired when the anvil computes a repair or rename result.
     * Use {@link AnvilContext} to read or modify the output, XP cost, and material cost.
     * <p>
     * NeoForge: {@code AnvilUpdateEvent}<br>
     * Fabric: mixin into {@code AnvilMenu#createResult}
     */
    public static final PolyEvent<Anvil> ANVIL_UPDATE = PolyEvent.create(
            handlers -> (left, right, name, ctx) -> handlers.forEach(h -> h.onAnvilUpdate(left, right, name, ctx)));

    private PolyItemEvents() {}

    @FunctionalInterface
    public interface UseStart
    {
        void onUseStart(LivingEntity entity, ItemStack stack, int duration, CancelContext ctx);
    }

    @FunctionalInterface
    public interface UseTick
    {
        void onUseTick(LivingEntity entity, ItemStack stack, int ticksRemaining, CancelContext ctx);
    }

    @FunctionalInterface
    public interface UseFinish
    {
        void onUseFinish(LivingEntity entity, ItemStack stack);
    }

    @FunctionalInterface
    public interface ItemExpire
    {
        void onItemExpire(ItemEntity item);
    }

    @FunctionalInterface
    public interface ItemFishing
    {
        void onItemFishing(Player player, List<ItemStack> drops, CancelContext ctx);
    }

    @FunctionalInterface
    public interface Grindstone
    {
        void onGrindstone(ItemStack topItem, ItemStack bottomItem, GrindstoneContext ctx);
    }

    @FunctionalInterface
    public interface Anvil
    {
        void onAnvilUpdate(ItemStack left, ItemStack right, String name, AnvilContext ctx);
    }

    /** Mutable context for grindstone output/XP modification. */
    public static final class GrindstoneContext
    {
        private final ItemStack topItem;
        private final ItemStack bottomItem;
        private ItemStack output;
        private int xp;

        public GrindstoneContext(ItemStack topItem, ItemStack bottomItem, ItemStack output, int xp)
        {
            this.topItem = topItem;
            this.bottomItem = bottomItem;
            this.output = output;
            this.xp = xp;
        }

        public ItemStack getTopItem() { return topItem; }

        public ItemStack getBottomItem() { return bottomItem; }

        public ItemStack getOutput() { return output; }

        public void setOutput(ItemStack output) { this.output = output; }

        public int getXp() { return xp; }

        public void setXp(int xp) { this.xp = xp; }
    }

    /** Mutable context for anvil output/cost modification. */
    public static final class AnvilContext
    {
        private ItemStack output;
        private int xpCost;
        private int materialCost;

        public AnvilContext(ItemStack output, int xpCost, int materialCost)
        {
            this.output = output;
            this.xpCost = xpCost;
            this.materialCost = materialCost;
        }

        public ItemStack getOutput() { return output; }

        public void setOutput(ItemStack output) { this.output = output; }

        public int getXpCost() { return xpCost; }

        public void setXpCost(int xpCost) { this.xpCost = xpCost; }

        public int getMaterialCost() { return materialCost; }

        public void setMaterialCost(int materialCost) { this.materialCost = materialCost; }
    }

    // ── Tier 10: Fuel ────────────────────────────────────────────────────────

    /**
     * Fired to query how long an item burns as fuel. Modify {@code burnTime[0]} to change.
     * <p>
     * NeoForge: {@code FurnaceFuelBurnTimeEvent} (MOD bus)<br>
     * Fabric: mixin on {@code FurnaceBlockEntity#getBurnDuration}
     */
    public static final PolyEvent<FuelBurnTime> FUEL_BURN_TIME = PolyEvent.create(
            handlers -> (stack, burnTime) -> handlers.forEach(h -> h.onFuelBurnTime(stack, burnTime)));

    @FunctionalInterface
    public interface FuelBurnTime
    {
        /** {@code burnTime[0]} — burn time in ticks (mutable). Set to 0 to make non-fuel. */
        void onFuelBurnTime(ItemStack stack, int[] burnTime);
    }

    // ── Tier 13 ───────────────────────────────────────────────────────────────

    /**
     * Fired when a player drags/stacks one inventory item onto another. Informational only.
     * <p>
     * NeoForge: {@code ItemStackedOnOtherEvent}<br>
     * Fabric: mixin on {@code AbstractContainerMenu#doClick} stack-merge branch
     */
    public static final PolyEvent<ItemStackedOnOther> ITEM_STACKED_ON_OTHER = PolyEvent.create(
            handlers -> (player, carried, target, targetSlot, carriedSlot) ->
                    handlers.forEach(h -> h.onItemStackedOnOther(player, carried, target, targetSlot, carriedSlot)));

    /**
     * Fired after an item is successfully crafted/renamed in an anvil. Informational only.
     * <p>
     * NeoForge: {@code AnvilCraftEvent.Post}<br>
     * Fabric: mixin on the result slot's {@code onTake} callback in {@code AnvilMenu}
     */
    public static final PolyEvent<AnvilCraft> ANVIL_CRAFT = PolyEvent.create(
            handlers -> (player, left, right, output, cost) ->
                    handlers.forEach(h -> h.onAnvilCraft(player, left, right, output, cost)));

    @FunctionalInterface
    public interface ItemStackedOnOther
    {
        void onItemStackedOnOther(Player player, ItemStack carried, ItemStack target,
                                  Slot targetSlot, Slot carriedSlot);
    }

    @FunctionalInterface
    public interface AnvilCraft
    {
        void onAnvilCraft(Player player, ItemStack left, ItemStack right, ItemStack output, int cost);
    }
}
