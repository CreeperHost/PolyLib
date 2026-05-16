package net.creeperhost.polylib.mixin.server.chunkmap;

import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTracker;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerHolder;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerReference;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.level.TicketStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * TicketStorage mixin: notifies the tracker whenever tickets are added or removed
 * so the client-side ticket colouring stays accurate.
 *
 * <p>This mixin also holds a reference to the {@link PolyChunkTracker} set by
 * {@link PolyDistanceManagerMixin} during {@code DistanceManager} init.
 */
@Mixin(TicketStorage.class)
public class PolyTicketStorageMixin implements PolyChunkTrackerReference
{
    @Unique private PolyChunkTracker polylib$tracker;

    @Shadow @Final
    @SuppressWarnings("rawtypes")
    private it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap<List<Ticket>> tickets;

    // ── Ticket add ────────────────────────────────────────────────────────────

    @Inject(
            method = "addTicket(JLnet/minecraft/server/level/Ticket;)Z",
            at = @At("RETURN")
    )
    private void polylib$onAddTicket(long pos, Ticket ticket, CallbackInfoReturnable<Boolean> cir)
    {
        if (polylib$tracker != null) {
            polylib$tracker.setTickets(pos, tickets.getOrDefault(pos, List.of()));
        }
    }

    // ── Ticket remove ─────────────────────────────────────────────────────────

    @Inject(
            method = "removeTicket(JLnet/minecraft/server/level/Ticket;)Z",
            at = @At("RETURN")
    )
    private void polylib$onRemoveTicket(long pos, Ticket ticket, CallbackInfoReturnable<Boolean> cir)
    {
        // Even if it returns false, it may have removed the ticket without changing the level
        if (polylib$tracker != null) {
            polylib$tracker.setTickets(pos, tickets.getOrDefault(pos, List.of()));
        }
    }

    @Inject(
            method = "removeTicketIf",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectMap$Entry;getValue()Ljava/lang/Object;",
                    ordinal = 3,
                    remap = false
            )
    )
    private void polylib$onRemoveTicketIf(java.util.function.BiPredicate<Ticket, Long> predicate, it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap<List<Ticket>> map, CallbackInfo ci, @com.llamalad7.mixinextras.sugar.Local it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<List<Ticket>> entry)
    {
        if (polylib$tracker != null) {
            polylib$tracker.setTickets(entry.getLongKey(), entry.getValue());
        }
    }

    // ── PolyChunkTrackerReference ─────────────────────────────────────────────

    @Override
    public void polylib$setTracker(PolyChunkTracker tracker)
    {
        this.polylib$tracker = tracker;
    }

    @Override
    public PolyChunkTracker polylib$getTracker()
    {
        return this.polylib$tracker;
    }
}
