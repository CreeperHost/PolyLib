package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * NeoForge bridge for {@link PolyClientLifecycleEvents#CLIENT_STARTED}.
 * Fires exactly once when the game loop begins (first call to {@code Minecraft#run}).
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraftRun
{
    private static final AtomicBoolean firedClientStarted = new AtomicBoolean(false);

    @Inject(method = "run", at = @At("HEAD"))
    private void polylib$onClientStarted(CallbackInfo ci)
    {
        if (firedClientStarted.compareAndSet(false, true))
        {
            PolyClientLifecycleEvents.CLIENT_STARTED.invoker().onClientStarted((Minecraft) (Object) this);
        }
    }
}
