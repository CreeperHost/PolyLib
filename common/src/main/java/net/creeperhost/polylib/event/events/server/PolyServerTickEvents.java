package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public final class PolyServerTickEvents
{
    public static final PolyEvent<TickStart> TICK_START = PolyEvent.create(handlers -> server -> handlers.forEach(h -> h.onTickStart(server)));
    public static final PolyEvent<TickEnd> TICK_END = PolyEvent.create(handlers -> server -> handlers.forEach(h -> h.onTickEnd(server)));
    public static final PolyEvent<LevelTickStart> LEVEL_TICK_START = PolyEvent.create(handlers -> level -> handlers.forEach(h -> h.onLevelTickStart(level)));
    public static final PolyEvent<LevelTickEnd> LEVEL_TICK_END = PolyEvent.create(handlers -> level -> handlers.forEach(h -> h.onLevelTickEnd(level)));

    private PolyServerTickEvents() {}

    @FunctionalInterface
    public interface TickStart
    {
        void onTickStart(MinecraftServer server);
    }

    @FunctionalInterface
    public interface TickEnd
    {
        void onTickEnd(MinecraftServer server);
    }

    @FunctionalInterface
    public interface LevelTickStart
    {
        void onLevelTickStart(ServerLevel level);
    }

    @FunctionalInterface
    public interface LevelTickEnd
    {
        void onLevelTickEnd(ServerLevel level);
    }
}
