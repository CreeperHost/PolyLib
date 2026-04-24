package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyLevelEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLevelEvents#LEVEL_SAVE}.
 * Fires when a server level is saved.
 */
@Mixin(ServerLevel.class)
public abstract class FabricLevelSaveMixin
{
    @Inject(method = "save", at = @At("TAIL"))
    private void polylib$onSave(@Nullable ProgressListener progressListener, boolean flush, boolean skip, CallbackInfo ci)
    {
        PolyLevelEvents.LEVEL_SAVE.invoker().onSave((ServerLevel) (Object) this);
    }
}
