package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyRenderStateEvents;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyRenderStateEvents#INVALIDATE_RENDER_STATE}.
 * <p>
 * Fires after {@link LevelRenderer#allChanged()} completes — this method is
 * called whenever render state must be fully rebuilt (resource reload, graphics
 * settings change, etc.).
 */
@Mixin(LevelRenderer.class)
public abstract class MixinLevelRendererAllChangedNF
{
    @Inject(method = "allChanged", at = @At("RETURN"))
    private void polylib$onAllChanged(CallbackInfo ci)
    {
        PolyRenderStateEvents.INVALIDATE_RENDER_STATE.invoker().onInvalidate();
    }
}
