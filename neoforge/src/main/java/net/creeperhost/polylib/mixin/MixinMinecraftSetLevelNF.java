package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.client.PolyClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * NeoForge bridge for {@link PolyClientLifecycleEvents#CLIENT_LEVEL_CHANGED}.
 * <p>
 * Hooks {@link Minecraft#setLevel} and {@link Minecraft#clearClientLevel} to fire the
 * event with the new level reference (or {@code null} on disconnect).
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraftSetLevelNF
{
    @Inject(method = "setLevel", at = @At("TAIL"))
    private void polylib$onSetLevel(ClientLevel level, CallbackInfo ci)
    {
        PolyClientLifecycleEvents.CLIENT_LEVEL_CHANGED.invoker().onClientLevelChanged((Minecraft) (Object) this, level);
    }

    @Inject(method = "clearClientLevel", at = @At("TAIL"))
    private void polylib$onClearLevel(@Nullable Screen screen, CallbackInfo ci)
    {
        PolyClientLifecycleEvents.CLIENT_LEVEL_CHANGED.invoker().onClientLevelChanged((Minecraft) (Object) this, null);
    }
}
