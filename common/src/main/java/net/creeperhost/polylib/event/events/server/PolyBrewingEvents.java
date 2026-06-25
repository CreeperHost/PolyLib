package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

/**
 * Events related to brewing stand operations.
 * <p>
 * NeoForge: {@code PotionBrewEvent.Pre} / {@code PotionBrewEvent.Post}<br>
 * Fabric: mixin into {@code BrewingStandBlockEntity#doTick}
 */
public final class PolyBrewingEvents
{

    /**
     * Fired before a brewing operation completes.
     * Call {@link CancelContext#cancel()} to prevent the brew from being applied.
     * <p>
     * {@code items} is the live item list of the brewing stand (slots 0-2 are outputs,
     * slot 3 is the ingredient, slot 4 is the fuel). Handlers may modify the list in place
     * to alter the brew result.
     * <p>
     * NeoForge: {@code PotionBrewEvent.Pre} (cancellable)<br>
     * Fabric: mixin into {@code BrewingStandBlockEntity#doTick} at INVOKE of the actual brew
     */
    public static final PolyEvent<BrewPre> BREW_PRE = PolyEvent.create(handlers -> (items, ctx) ->
    {
        for (var h : handlers)
        {
            h.onBrewPre(items, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired after a brewing operation has completed successfully.
     * Observer only — not cancellable.
     * <p>
     * NeoForge: {@code PotionBrewEvent.Post}<br>
     * Fabric: mixin into {@code BrewingStandBlockEntity#doTick} at INVOKE (post)
     */
    public static final PolyEvent<BrewPost> BREW_POST = PolyEvent.create(
            handlers -> (items) -> handlers.forEach(h -> h.onBrewPost(items)));

    private PolyBrewingEvents() {}


    @FunctionalInterface
    public interface BrewPre
    {
        void onBrewPre(NonNullList<ItemStack> items, CancelContext ctx);
    }

    @FunctionalInterface
    public interface BrewPost
    {
        void onBrewPost(NonNullList<ItemStack> items);
    }
}
