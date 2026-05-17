package net.creeperhost.polylib.mixin.client;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Mixin accessor that exposes the private-static {@code ENTRIES_BY_ID} field
 * of {@link DebugScreenEntries} so that {@link FabricDebugEntriesMixin} can
 * insert PolyLib debug entries after vanilla's static initialiser runs.
 * <p>
 * Mutable access is intentional: we need to {@link Map#put} into the live map,
 * not a copy.  The field is a {@code HashMap} (confirmed via bytecode) that is
 * later read (but not replaced) by {@code allEntries()} and {@code getEntry()}.
 */
@Mixin(DebugScreenEntries.class)
public interface EntryMapAccessor
{
    @Accessor("ENTRIES_BY_ID")
    static Map<Identifier, DebugScreenEntry> polylib$getEntriesById()
    {
        throw new AssertionError("Mixin accessor not applied");
    }
}
