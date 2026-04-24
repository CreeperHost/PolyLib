package net.creeperhost.polylib.mixin;

import com.mojang.brigadier.context.CommandContext;
import net.creeperhost.polylib.event.events.server.PolyLevelEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric bridge for {@link PolyLevelEvents#GAME_RULE_CHANGE}.
 * Fires at the tail of the {@code /gamerule} command handler so the change is already applied.
 */
@Mixin(GameRuleCommand.class)
public abstract class FabricGameRuleChangeMixin
{
    @Inject(method = "setRule", at = @At("TAIL"))
    private static <T> void polylib$onSetRule(CommandContext<CommandSourceStack> ctx, GameRule<T> rule,
            CallbackInfoReturnable<Integer> cir)
    {
        MinecraftServer server = ctx.getSource().getServer();
        GameRules rules = server.getGameRules();
        PolyLevelEvents.GAME_RULE_CHANGE.invoker().onGameRuleChange(server, rule, rules);
    }
}
