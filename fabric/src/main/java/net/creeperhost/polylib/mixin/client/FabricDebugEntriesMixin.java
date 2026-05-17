package net.creeperhost.polylib.mixin.client;

import net.creeperhost.polylib.debug.PolyDebugDisplayer;
import net.creeperhost.polylib.debug.PolyDebugEntry;
import net.creeperhost.polylib.debug.PolyDebugEntryType;
import net.creeperhost.polylib.debug.PolyDebugRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;

/**
 * Fabric bridge for {@link PolyDebugRegistry}.
 * <p>
 * Injects at the tail of {@code DebugScreenEntries}'s static initialiser so
 * that all entries registered via {@link PolyDebugRegistry} are appended to
 * the vanilla {@code ENTRIES_BY_ID} map before the first F3 screen render.
 * <p>
 * Because {@code register(Identifier, DebugScreenEntry)} is private-static,
 * we obtain the {@code ENTRIES_BY_ID} field via the {@link EntryMapAccessor}
 * interface and call {@link Map#put} directly.  This matches exactly what the
 * private method does (see bytecode: {@code ENTRIES_BY_ID.put(id, entry); return id;}).
 */
@Mixin(DebugScreenEntries.class)
public abstract class FabricDebugEntriesMixin
{
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void polylib$registerEntries(CallbackInfo ci)
    {
        Map<Identifier, DebugScreenEntry> entriesById = EntryMapAccessor.polylib$getEntriesById();

        for (PolyDebugEntryType type : PolyDebugRegistry.getAll())
        {
            entriesById.put(type.id(), buildMcEntry(type.entry()));
        }
    }

    // ── Adapter factory ───────────────────────────────────────────────────────

    private static DebugScreenEntry buildMcEntry(PolyDebugEntry poly)
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
