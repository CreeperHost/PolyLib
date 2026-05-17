package net.creeperhost.polylib.debug.neoforge;

import net.creeperhost.polylib.debug.PolyDebugDisplayer;
import net.creeperhost.polylib.debug.PolyDebugEntry;
import net.creeperhost.polylib.debug.PolyDebugEntryType;
import net.creeperhost.polylib.debug.PolyDebugRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.client.event.RegisterDebugEntriesEvent;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

/**
 * NeoForge bridge that wires {@link PolyDebugRegistry} into the vanilla
 * {@code DebugScreenEntries} system via {@link RegisterDebugEntriesEvent}.
 *
 * <p>Registered on the <strong>mod event bus</strong> from
 * {@link net.creeperhost.polylib.PolyLibClientNeoForge}.
 *
 * <p>Each {@link PolyDebugEntryType} is adapted to a {@link DebugScreenEntry}
 * that delegates {@code isAllowed()} and {@code display()} back to the
 * {@link PolyDebugEntry} implementation.  Toggle state and persistence are
 * handled entirely by vanilla's {@code DebugOptions} / {@code options.txt}
 * mechanism — no extra storage is needed.
 */
public final class NeoForgeDebugBridge
{
    private NeoForgeDebugBridge() {}

    /**
     * Fired once during mod startup on the mod event bus.
     * Iterates every registered {@link PolyDebugEntryType} and registers an
     * adapter {@link DebugScreenEntry} for it.
     */
    public static void onRegisterDebugEntries(RegisterDebugEntriesEvent event)
    {
        for (PolyDebugEntryType type : PolyDebugRegistry.getAll())
        {
            DebugScreenEntry mcEntry = buildEntry(type.entry());
            event.register(type.id(), mcEntry);

            // Map our boolean defaultEnabled to the correct DebugScreenEntryStatus:
            //   true  → IN_OVERLAY  (shows in the F3 panel by default)
            //   false → NEVER       (hidden until player manually enables it)
            DebugScreenEntryStatus profileStatus =
                    type.defaultEnabled() ? DebugScreenEntryStatus.IN_OVERLAY : DebugScreenEntryStatus.NEVER;

            if (type.inDefaultProfile())
                event.includeInProfile(type.id(), DebugScreenProfile.DEFAULT, profileStatus);

            if (type.inPerformanceProfile())
                event.includeInProfile(type.id(), DebugScreenProfile.PERFORMANCE, profileStatus);
        }
    }

    // ── Adapter factory ───────────────────────────────────────────────────────

    /**
     * Wraps a {@link PolyDebugEntry} in a vanilla {@link DebugScreenEntry}.
     * <p>
     * The MC {@code DebugScreenDisplayer} is adapted to {@link PolyDebugDisplayer}
     * via an anonymous inline implementation so common-module code never
     * imports any NeoForge/MC rendering classes directly.
     */
    private static DebugScreenEntry buildEntry(PolyDebugEntry poly)
    {
        return new DebugScreenEntry()
        {
            @Override
            public boolean isAllowed(boolean reducedDebugInfo)
            {
                return poly.isAllowed(Minecraft.getInstance(), reducedDebugInfo);
            }

            @Override
            public void display(DebugScreenDisplayer mcDisplayer,
                                @Nullable Level level,
                                @Nullable LevelChunk clientChunk,
                                @Nullable LevelChunk serverChunk)
            {
                poly.display(wrapDisplayer(mcDisplayer), level, clientChunk, serverChunk);
            }
        };
    }

    /**
     * Adapts a MC {@link DebugScreenDisplayer} into a {@link PolyDebugDisplayer}.
     */
    private static PolyDebugDisplayer wrapDisplayer(DebugScreenDisplayer mc)
    {
        return new PolyDebugDisplayer()
        {
            @Override
            public void addLine(String text)
            {
                mc.addLine(text);
            }

            @Override
            public void addPriorityLine(String text)
            {
                mc.addPriorityLine(text);
            }

            @Override
            public void addToGroup(Identifier group, Collection<String> lines)
            {
                mc.addToGroup(group, lines);
            }
        };
    }
}
