package net.creeperhost.polylib.mixin.server.chunkmap;

import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTracker;
import net.creeperhost.polylib.chunkmap.server.tracker.PolyChunkTrackerHolder;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * Attaches a {@link PolyChunkTracker} to each {@link ServerLevel} and ticks it
 * once per level tick so pending off-thread stage updates are flushed.
 */
@Mixin(ServerLevel.class)
public class PolyServerLevelMixin implements PolyChunkTrackerHolder
{
    @Unique
    private final PolyChunkTracker polylib$chunkTracker =
            new PolyChunkTracker((ServerLevel) (Object) this);

    @Inject(method = "tick", at = @At("HEAD"))
    private void polylib$onTick(BooleanSupplier hasTimeLeft, CallbackInfo ci)
    {
        if (!net.creeperhost.polylib.PolyFeatures.isChunkMapEnabled()) return;
        polylib$chunkTracker.tick();
    }

    @Override
    public PolyChunkTracker polylib$getChunkTracker()
    {
        return polylib$chunkTracker;
    }
}
