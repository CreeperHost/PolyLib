package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyRenderStateEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyRenderStateEvents#INVALIDATE_RENDER_STATE}.
 * <p>
 * Fires after {@code LevelRenderer#invalidateCompiledGeometry} completes. In
 * 26.2 this replaced the old full render-state rebuild hook.
 */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRendererAllChangedNF
{
    @Inject(method = "invalidateCompiledGeometry", at = @At("RETURN"))
    private void polylib$onAllChanged(ClientLevel level, Options options, Camera camera, BlockColors blockColors, CallbackInfo ci)
    {
        PolyRenderStateEvents.INVALIDATE_RENDER_STATE.invoker().onInvalidate();
    }
}
