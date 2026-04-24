package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyClientInteractionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge bridges for client block-break events.
 * <ul>
 *   <li>{@link PolyClientInteractionEvents#CLIENT_BLOCK_BREAK_BEFORE} — {@code destroyBlock} HEAD, cancellable</li>
 *   <li>{@link PolyClientInteractionEvents#CLIENT_BLOCK_BREAK_AFTER} — {@code destroyBlock} RETURN, only when {@code true}</li>
 *   <li>{@link PolyClientInteractionEvents#CLIENT_BLOCK_BREAK_CANCELED} — {@code stopDestroyBlock} HEAD</li>
 * </ul>
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MixinClientBlockBreakNF
{
    @Shadow private BlockPos destroyBlockPos;

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void polylib$blockBreakBefore(BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !(mc.level instanceof ClientLevel cl)) return;
        BlockState state = cl.getBlockState(pos);
        CancelContext ctx = new CancelContext();
        PolyClientInteractionEvents.CLIENT_BLOCK_BREAK_BEFORE.invoker()
                .onBlockBreakBefore(mc.player, cl, pos, state, ctx);
        if (ctx.isCancelled()) cir.setReturnValue(false);
    }

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void polylib$blockBreakAfter(BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (!cir.getReturnValueZ()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !(mc.level instanceof ClientLevel cl)) return;
        BlockState state = cl.getBlockState(pos);
        PolyClientInteractionEvents.CLIENT_BLOCK_BREAK_AFTER.invoker()
                .onBlockBreakAfter(mc.player, cl, pos, state);
    }

    @Inject(method = "stopDestroyBlock", at = @At("HEAD"))
    private void polylib$blockBreakCanceled(CallbackInfo ci)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !(mc.level instanceof ClientLevel cl)) return;
        // Use the tracked destroy position; fall back to ZERO only if mining hasn't started yet
        BlockPos pos = this.destroyBlockPos != null ? this.destroyBlockPos : BlockPos.ZERO;
        BlockState state = cl.getBlockState(pos);
        PolyClientInteractionEvents.CLIENT_BLOCK_BREAK_CANCELED.invoker()
                .onBlockBreakCanceled(mc.player, cl, pos, state);
    }
}
