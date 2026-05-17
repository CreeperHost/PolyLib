package net.creeperhost.polylib.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

/**
 * Callback interface for a custom F3 debug screen entry.
 * <p>
 * Implement this interface and register an instance via
 * {@link PolyDebugRegistry#register}.  Both methods are called on the
 * <strong>render thread</strong> (client-side only), each frame while the
 * F3 screen is visible.
 *
 * <h3>Example</h3>
 * <pre>{@code
 * PolyDebugRegistry.register(
 *     Identifier.fromNamespaceAndPath("mymod", "stats"),
 *     new PolyDebugEntry() {
 *         public boolean isAllowed(Minecraft mc, boolean reduced) {
 *             return mc.level != null;  // only show in-world
 *         }
 *         public void display(PolyDebugDisplayer d, Level level, LevelChunk cc, LevelChunk sc) {
 *             d.addLine("MyMod stat: " + MyStats.getValue());
 *         }
 *     },
 *     true, true, false, "debug.mymod.stats", "My Stats"
 * );
 * }</pre>
 */
public interface PolyDebugEntry
{
    /**
     * Called every frame to determine whether this entry should contribute
     * any output to the F3 screen right now.
     * <p>
     * Receives the full {@link Minecraft} instance so implementations can
     * check world state, player state, feature flags, etc.  Also receives
     * {@code reducedDebugInfo} (the F3+Q reduced-info flag from game rules).
     * Return {@code false} to suppress all lines for this frame.
     *
     * @param mc      the current Minecraft client instance (never null)
     * @param reduced {@code true} when the game rule {@code reducedDebugInfo} is active
     * @return {@code true} to allow display, {@code false} to hide entirely
     */
    default boolean isAllowed(Minecraft mc, boolean reduced)
    {
        return true;
    }

    /**
     * Called each frame (when {@link #isAllowed} returns {@code true}) to
     * write lines into the F3 panel via the provided {@link PolyDebugDisplayer}.
     * <p>
     * {@code clientChunk} and {@code serverChunk} are the chunk the player is
     * currently standing in from the client and server perspectives respectively
     * (nullable if not in a world or the chunk is not loaded).
     *
     * @param displayer    output sink for this entry's debug lines
     * @param level        the client level, or {@code null} if not in a world
     * @param clientChunk  client-side chunk at player position, may be null
     * @param serverChunk  server-side chunk at player position, may be null
     */
    void display(PolyDebugDisplayer displayer,
                 @Nullable Level level,
                 @Nullable LevelChunk clientChunk,
                 @Nullable LevelChunk serverChunk);
}
