package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.events.server.PolyLevelEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyLevelEvents#DIFFICULTY_CHANGE}.
 */
@Mixin(MinecraftServer.class)
public abstract class FabricDifficultyChangeMixin
{
    @Inject(method = "setDifficulty", at = @At("HEAD"))
    private void polylib$onSetDifficulty(Difficulty difficulty, boolean force, CallbackInfo ci)
    {
        MinecraftServer self = (MinecraftServer)(Object) this;
        Difficulty oldDifficulty = self.getWorldData().getDifficulty();
        if (oldDifficulty != difficulty)
        {
            PolyLevelEvents.DIFFICULTY_CHANGE.invoker().onDifficultyChange(difficulty, oldDifficulty);
        }
    }
}
