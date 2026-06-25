package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Client-side interaction events for block breaking and attack input.
 */
public final class PolyClientInteractionEvents
{
    /**
     * Fired on the client before a block-break action is processed.
     * Cancel via {@link CancelContext} to suppress the break.
     * <p>
     * NeoForge: mixin {@code MixinClientBlockBreakNF} on
     * {@code MultiPlayerGameMode#destroyBlock(BlockPos)} at HEAD.<br>
     * Fabric: mixin {@code FabricClientBlockBreakBeforeMixin} (no native BEFORE event in this API version).
     */
    public static final PolyEvent<BlockBreakBefore> CLIENT_BLOCK_BREAK_BEFORE = PolyEvent.create(
            handlers -> (player, level, pos, state, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onBlockBreakBefore(player, level, pos, state, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    /**
     * Fired on the client after a block is successfully broken. Not cancellable.
     * <p>
     * NeoForge: mixin {@code MixinClientBlockBreakNF} on
     * {@code MultiPlayerGameMode#destroyBlock(BlockPos)} at RETURN (only when {@code true}).<br>
     * Fabric: {@code ClientPlayerBlockBreakEvents.AFTER}.
     */
    public static final PolyEvent<BlockBreakAfter> CLIENT_BLOCK_BREAK_AFTER = PolyEvent.create(
            handlers -> (player, level, pos, state) ->
                    handlers.forEach(h -> h.onBlockBreakAfter(player, level, pos, state)));

    /**
     * Fired on the client when a block-break action is stopped/aborted. Not cancellable.
     * <p>
     * NeoForge: mixin {@code MixinClientBlockBreakNF} on
     * {@code MultiPlayerGameMode#stopDestroyBlock()}.<br>
     * Fabric: mixin {@code FabricClientBlockBreakCanceledMixin} (no native CANCELED event).
     */
    public static final PolyEvent<BlockBreakCanceled> CLIENT_BLOCK_BREAK_CANCELED = PolyEvent.create(
            handlers -> (player, level, pos, state) ->
                    handlers.forEach(h -> h.onBlockBreakCanceled(player, level, pos, state)));

    /**
     * Fired before the client processes an attack action.
     * Cancel via {@link CancelContext} to suppress the attack.
     * <p>
     * NeoForge: mixin {@code MixinClientPreAttackNF} on
     * {@code MultiPlayerGameMode#attack(Player, Entity)} at HEAD.<br>
     * Fabric: {@code ClientPreAttackCallback.EVENT} (return {@code false} = cancel).
     */
    public static final PolyEvent<PreAttack> CLIENT_PRE_ATTACK = PolyEvent.create(
            handlers -> (client, player, clickCount, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onPreAttack(client, player, clickCount, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    private PolyClientInteractionEvents() {}

    @FunctionalInterface
    public interface BlockBreakBefore
    {
        void onBlockBreakBefore(LocalPlayer player, ClientLevel level, BlockPos pos,
                                BlockState state, CancelContext ctx);
    }

    @FunctionalInterface
    public interface BlockBreakAfter
    {
        void onBlockBreakAfter(LocalPlayer player, ClientLevel level, BlockPos pos, BlockState state);
    }

    @FunctionalInterface
    public interface BlockBreakCanceled
    {
        void onBlockBreakCanceled(LocalPlayer player, ClientLevel level, BlockPos pos, BlockState state);
    }

    @FunctionalInterface
    public interface PreAttack
    {
        void onPreAttack(Minecraft client, LocalPlayer player, int clickCount, CancelContext ctx);
    }
}
