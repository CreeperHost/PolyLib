package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.CancelContext;
import net.creeperhost.polylib.event.events.client.PolyClientInteractionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyClientInteractionEvents#CLIENT_BLOCK_BREAK_BEFORE}.
 * ({@code ClientPlayerBlockBreakEvents.BEFORE} does not exist in Fabric API 0.146.1+26.1.2.)
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class FabricClientBlockBreakBeforeMixin
{
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
}
