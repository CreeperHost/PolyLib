package net.creeperhost.polylib.neoforge.mixins;

import net.creeperhost.polylib.events.DifficultyChangedEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.ClientLevelData.class)
public abstract class MixinClientLevelData
{
    @Shadow
    public abstract Difficulty getDifficulty();

    @Inject(method = "setDifficulty", at = @At("HEAD"))
    public void setDifficulty(Difficulty newDifficulty, CallbackInfo ci)
    {
        DifficultyChangedEvent.DIFFICULTY_CHANGED.invoker().onDifficultyChanged(newDifficulty, getDifficulty());
    }
}
