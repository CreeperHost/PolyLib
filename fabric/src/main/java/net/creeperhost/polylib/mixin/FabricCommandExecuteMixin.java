package net.creeperhost.polylib.mixin;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.events.server.PolyChatEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric bridge for {@link PolyChatEvents#COMMAND_EXECUTE}.
 * Fires at the start of every prefixed command execution (player or command-block commands).
 * Cancel to prevent execution.
 * <p>
 * Note: modifying {@code command[0]} has no effect on Fabric because the string has already
 * been parsed by the time the inject fires. Use NeoForge for command redirection.
 */
@Mixin(Commands.class)
public abstract class FabricCommandExecuteMixin
{
    @Inject(method = "performPrefixedCommand", at = @At("HEAD"), cancellable = true)
    private void polylib$onPerformPrefixedCommand(CommandSourceStack source, String command,
            CallbackInfo ci)
    {
        String[] cmd = { command };
        CancelContext ctx = new CancelContext();
        PolyChatEvents.COMMAND_EXECUTE.invoker().onCommandExecute(source, cmd, ctx);
        if (ctx.isCancelled()) ci.cancel();
    }
}
