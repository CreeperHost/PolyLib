package net.creeperhost.polylib.debug;

import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.List;

/**
 * Platform-agnostic display interface passed to {@link PolyDebugEntry#display}.
 * <p>
 * Maps directly onto {@code DebugScreenDisplayer} on both NeoForge and Fabric.
 * Use {@link #addLine} for normal sequential lines, {@link #addPriorityLine}
 * to force a line to the top of its column, and {@link #addToGroup} to append
 * lines beneath an existing vanilla or mod-registered entry group.
 */
public interface PolyDebugDisplayer
{
    /** Appends a text line to the current display column in normal order. */
    void addLine(String text);

    /**
     * Appends a text line at the top priority position — it will appear
     * before normal lines in the column regardless of registration order.
     */
    void addPriorityLine(String text);

    /**
     * Appends a collection of lines to an existing named group.
     * The group is identified by its {@link Identifier} key
     * (e.g. {@code DebugEntrySystemSpecs#GROUP}).
     * If the group does not exist, the lines are silently dropped.
     *
     * @param group namespaced key of the target group
     * @param lines lines to append inside that group
     */
    void addToGroup(Identifier group, Collection<String> lines);

    /**
     * Convenience overload — appends a single line to a group.
     *
     * @param group namespaced key of the target group
     * @param text  single text line to append
     */
    default void addToGroup(Identifier group, String text)
    {
        addToGroup(group, List.of(text));
    }
}
