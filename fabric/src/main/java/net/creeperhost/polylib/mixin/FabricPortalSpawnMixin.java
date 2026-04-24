package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyBlockEvents;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyBlockEvents#PORTAL_SPAWN}.
 * Fires before portal blocks are placed.
 */
@Mixin(PortalShape.class)
public abstract class FabricPortalSpawnMixin
{
    @Inject(method = "createPortalBlocks", at = @At("HEAD"), cancellable = true)
    private void polylib$onCreatePortalBlocks(LevelAccessor level, CallbackInfo ci)
    {
        // We don't have the exact pos/state at HEAD easily; pass dummies
        // The event fires with null pos/state as best-effort on Fabric
        CancelContext ctx = new CancelContext();
        PolyBlockEvents.PORTAL_SPAWN.invoker().onPortalSpawn(level, null, null, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
