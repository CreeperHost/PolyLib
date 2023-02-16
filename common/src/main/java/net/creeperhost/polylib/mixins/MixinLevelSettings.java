package net.creeperhost.polylib.mixins;

import net.creeperhost.polylib.events.DifficultyChangedEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.LevelSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelSettings.class)
public class MixinLevelSettings
{
    @Shadow
    @Final
    private Difficulty difficulty;

    @Inject(method = "withDifficulty", at = @At("HEAD"))
    public void setDifficulty(Difficulty newDifficulty, CallbackInfoReturnable<LevelSettings> cir)
    {
        DifficultyChangedEvent.DIFFICULTY_CHANGED.invoker().onDifficultyChanged(newDifficulty, difficulty);
    }
}
