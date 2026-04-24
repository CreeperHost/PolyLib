package net.creeperhost.polylib.event.events.server;

import com.mojang.brigadier.CommandDispatcher;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * Fired when the server is registering commands.
 * Use this to register commands cross-platform via Brigadier.
 * <p>
 * NeoForge: {@code RegisterCommandsEvent}<br>
 * Fabric: {@code CommandRegistrationCallback}
 */
public final class PolyServerCommandEvents
{
    public static final PolyEvent<Register> REGISTER_COMMANDS = PolyEvent.create(
            handlers -> (dispatcher, buildContext, selection) ->
                    handlers.forEach(h -> h.onRegisterCommands(dispatcher, buildContext, selection)));

    private PolyServerCommandEvents() {}

    @FunctionalInterface
    public interface Register
    {
        void onRegisterCommands(
                CommandDispatcher<CommandSourceStack> dispatcher,
                CommandBuildContext buildContext,
                Commands.CommandSelection selection);
    }
}
