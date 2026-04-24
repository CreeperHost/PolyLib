package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientInteractionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyClientInteractionEvents#CLIENT_BLOCK_BREAK_CANCELED}.
 * ({@code ClientPlayerBlockBreakEvents.CANCELED} does not exist in Fabric API 0.146.1+26.1.2.)
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class FabricClientBlockBreakCanceledMixin
{
    @Inject(method = "stopDestroyBlock", at = @At("HEAD"))
    private void polylib$blockBreakCanceled(CallbackInfo ci)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !(mc.level instanceof ClientLevel cl)) return;
        PolyClientInteractionEvents.CLIENT_BLOCK_BREAK_CANCELED.invoker()
                .onBlockBreakCanceled(mc.player, cl, net.minecraft.core.BlockPos.ZERO,
                        cl.getBlockState(net.minecraft.core.BlockPos.ZERO));
    }
}
