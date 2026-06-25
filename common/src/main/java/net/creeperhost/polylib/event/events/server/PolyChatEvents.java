package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * Events related to player chat messages and command execution.
 */
public final class PolyChatEvents
{
    /**
     * Fired when a player sends a chat message to the server.
     * Cancel to suppress the message; modify {@code message[0]} to change content.
     * <p>
     * NeoForge: {@code ServerChatEvent}<br>
     * Fabric: mixin on {@code PlayerList#broadcastChatMessage}
     */
    public static final PolyEvent<ServerChat> SERVER_CHAT = PolyEvent.create(handlers -> (player, message, ctx) ->
    {
        for (var h : handlers)
        {
            h.onServerChat(player, message, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a command is being executed (player or command block).
     * Cancel to prevent execution; modify {@code command[0]} to change command text.
     * <p>
     * NeoForge: {@code CommandEvent}<br>
     * Fabric: mixin on {@code Commands#performPrefixedCommand}
     */
    public static final PolyEvent<CommandExecute> COMMAND_EXECUTE = PolyEvent.create(
            handlers -> (source, command, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onCommandExecute(source, command, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    private PolyChatEvents() {}

    @FunctionalInterface
    public interface ServerChat
    {
        /** {@code message[0]} — current chat Component (mutable). Cancel to suppress. */
        void onServerChat(ServerPlayer player, Component[] message, CancelContext ctx);
    }

    @FunctionalInterface
    public interface CommandExecute
    {
        /** {@code command[0]} — raw command string (mutable). Cancel to prevent execution. */
        void onCommandExecute(CommandSourceStack source, String[] command, CancelContext ctx);
    }


    /**
     * Gate event: return {@code false} from any handler to suppress a system/game message
     * from being broadcast.
     * <p>
     * NeoForge: mixin on {@code MinecraftServer#sendSystemMessage} at HEAD<br>
     * Fabric: {@code ServerMessageEvents.ALLOW_GAME_MESSAGE}
     */
    public static final PolyEvent<AllowGameMessage> ALLOW_GAME_MESSAGE = PolyEvent.create(
            handlers -> (server, message, overlay) ->
            {
                for (var h : handlers)
                    if (!h.allowGameMessage(server, message, overlay)) return false;
                return true;
            });

    /**
     * Observer fired when the server broadcasts a game/system message to all players.
     * <p>
     * NeoForge: mixin on {@code MinecraftServer#sendSystemMessage} at RETURN<br>
     * Fabric: {@code ServerMessageEvents.GAME_MESSAGE}
     */
    public static final PolyEvent<ServerGameMessage> SERVER_GAME_MESSAGE = PolyEvent.create(
            handlers -> (server, message, overlay) ->
                    handlers.forEach(h -> h.onServerGameMessage(server, message, overlay)));

    /**
     * Decoration hook: modify the outgoing chat {@link Component} before it is sent.
     * {@code message[0]} starts as the player's original component; each handler may
     * replace it by writing a new value into the array.
     * <p>
     * NeoForge: {@code ServerChatEvent} (mutate the component before the normal chat
     * dispatch path)<br>
     * Fabric: {@code ServerMessageDecoratorEvent.EVENT}
     */
    public static final PolyEvent<ChatDecorate> CHAT_DECORATE = PolyEvent.create(
            handlers -> (player, message) ->
            {
                for (var h : handlers) h.onChatDecorate(player, message);
            });


    @FunctionalInterface
    public interface AllowGameMessage
    {
        /** Return {@code false} to suppress the message. */
        boolean allowGameMessage(MinecraftServer server, Component message, boolean overlay);
    }

    @FunctionalInterface
    public interface ServerGameMessage
    {
        void onServerGameMessage(MinecraftServer server, Component message, boolean overlay);
    }

    @FunctionalInterface
    public interface ChatDecorate
    {
        /**
         * {@code message[0]} holds the current Component. Replace it to alter the chat output.
         *
         * @param player the sending player, or {@code null} for server-generated messages
         */
        void onChatDecorate(@Nullable ServerPlayer player, Component[] message);
    }
}
