package net.creeperhost.testmod.init;

import net.creeperhost.polylib.chunkmap.client.PolyChunkMapClient;
import net.creeperhost.polylib.chunkmap.common.data.PolyChunkMapData;
import net.creeperhost.polylib.debug.PolyDebugDisplayer;
import net.creeperhost.polylib.debug.PolyDebugEntry;
import net.creeperhost.polylib.debug.PolyDebugEntryType;
import net.creeperhost.polylib.debug.PolyDebugRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static net.creeperhost.testmod.TestModCommon.MOD_ID;

/**
 * Registers test {@link PolyDebugEntry} instances via {@link PolyDebugRegistry}
 * to exercise the PolyLib F3 debug screen integration on both NeoForge and Fabric.
 *
 * <p>Three entries are registered, covering the key surface areas:
 * <ul>
 *   <li>{@code testmod:polylib_info} — always-visible, shows registry state and platform</li>
 *   <li>{@code testmod:world_info}   — only visible in-world, shows position and chunk data</li>
 *   <li>{@code testmod:reduced_test} — hides when the reducedDebugInfo gamerule is active</li>
 * </ul>
 *
 * <p>To test: load a world and press <b>F3</b>. All three sections should appear in the
 * right-hand debug column. Press <b>F3+N</b> to cycle debug profiles; the performance
 * profile should only show {@code polylib_info}.
 */
public final class TestDebugEntries
{
    // Stored as constants so callers can reference the tokens if needed
    public static PolyDebugEntryType POLYLIB_INFO;
    public static PolyDebugEntryType WORLD_INFO;
    public static PolyDebugEntryType REDUCED_TEST;

    private TestDebugEntries() {}

    public static void init()
    {
        // ── 1. PolyLib Info ──────────────────────────────────────────────────
        // Always visible, included in both DEFAULT and PERFORMANCE profiles.
        // Shows the registry entry count and current loader name.
        POLYLIB_INFO = PolyDebugRegistry.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "polylib_info"),
                new PolyDebugEntry()
                {
                    @Override
                    public void display(PolyDebugDisplayer d,
                                        @Nullable Level level,
                                        @Nullable LevelChunk clientChunk,
                                        @Nullable LevelChunk serverChunk)
                    {
                        d.addLine("PolyLib Debug");
                        d.addLine("  Entries: " + PolyDebugRegistry.getAll().size());
                        d.addLine("  Platform: " + net.creeperhost.polylib.platform.Services.PLATFORM.getPlatformName());
                    }
                },
                /*defaultEnabled*/      true,
                /*inDefaultProfile*/    true,
                /*inPerformanceProfile*/true,
                "debug." + MOD_ID + ".polylib_info",
                "PolyLib Info"
        );

        // ── 2. World Info ────────────────────────────────────────────────────
        // Only fires when in a world (isAllowed returns false at the title screen).
        // Shows dimension key, XYZ, and whether both chunk views are loaded.
        // NOT in the Performance profile.
        WORLD_INFO = PolyDebugRegistry.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "world_info"),
                new PolyDebugEntry()
                {
                    @Override
                    public boolean isAllowed(Minecraft mc, boolean reduced)
                    {
                        return mc.level != null && mc.player != null;
                    }

                    @Override
                    public void display(PolyDebugDisplayer d,
                                        @Nullable Level level,
                                        @Nullable LevelChunk clientChunk,
                                        @Nullable LevelChunk serverChunk)
                    {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null || mc.player == null) return;

                        Vec3 pos = mc.player.position();
                        BlockPos bpos = mc.player.blockPosition();
                        String dim = mc.level.dimension().identifier().toString();

                        d.addLine("[TestMod] World Info");
                        d.addLine("  Dim: " + dim);
                        d.addLine(String.format("  XYZ: %.2f / %.5f / %.2f", pos.x, pos.y, pos.z));
                        d.addLine("  Block: " + bpos.getX() + ", " + bpos.getY() + ", " + bpos.getZ());
                        d.addLine("  ClientChunk: " + (clientChunk != null ? "loaded" : "null"));
                        d.addLine("  ServerChunk: " + (serverChunk != null ? "loaded" : "null"));
                        if (clientChunk != null)
                            d.addLine("  Sections: " + clientChunk.getSectionsCount());
                    }
                },
                /*defaultEnabled*/      true,
                /*inDefaultProfile*/    true,
                /*inPerformanceProfile*/false,
                "debug." + MOD_ID + ".world_info",
                "World Info"
        );

        // ── 3. Reduced Debug Test ────────────────────────────────────────────
        // Demonstrates isAllowed(boolean reduced) — disappears when the
        // reducedDebugInfo gamerule is enabled (/gamerule reducedDebugInfo true).
        REDUCED_TEST = PolyDebugRegistry.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "reduced_test"),
                new PolyDebugEntry()
                {
                    @Override
                    public boolean isAllowed(Minecraft mc, boolean reduced)
                    {
                        return !reduced;
                    }

                    @Override
                    public void display(PolyDebugDisplayer d,
                                        @Nullable Level level,
                                        @Nullable LevelChunk clientChunk,
                                        @Nullable LevelChunk serverChunk)
                    {
                        d.addLine("[TestMod] Reduced Debug Test");
                        d.addLine("  reducedDebugInfo = OFF");
                        d.addLine("  (run: /gamerule reducedDebugInfo true to hide)");
                    }
                },
                /*defaultEnabled*/      true,
                /*inDefaultProfile*/    true,
                /*inPerformanceProfile*/false,
                "debug." + MOD_ID + ".reduced_test",
                "Reduced Debug Test"
        );
        // ── 4. Chunk Map Stats ───────────────────────────────────────────────
        // Shows whether the server has granted chunk-map access and how many
        // chunks are currently cached for the player's dimension.
        // This is the minimal test for the full server→client pipeline.
        PolyDebugRegistry.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "chunk_map_stats"),
                new PolyDebugEntry()
                {
                    @Override
                    public boolean isAllowed(Minecraft mc, boolean reduced)
                    {
                        return mc.level != null;
                    }

                    @Override
                    public void display(PolyDebugDisplayer d,
                                       @Nullable Level level,
                                       @Nullable LevelChunk clientChunk,
                                       @Nullable LevelChunk serverChunk)
                    {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) return;

                        boolean permitted = PolyChunkMapClient.isPermitted();
                        d.addLine("[PolyLib] Chunk Map");
                        d.addLine("  Server: " + (permitted ? "§aGRANTED§r" : "§cDENIED§r"));

                        if (permitted)
                        {
                            Map<Long, PolyChunkMapData> chunks =
                                    PolyChunkMapClient.getChunks(mc.level.dimension());
                            d.addLine("  Chunks: " + chunks.size());

                            // Count by FullChunkStatus
                            int inaccessible = 0, full = 0, blockTicking = 0, entityTicking = 0;
                            for (PolyChunkMapData c : chunks.values())
                            {
                                switch (c.status())
                                {
                                    case INACCESSIBLE  -> inaccessible++;
                                    case FULL          -> full++;
                                    case BLOCK_TICKING -> blockTicking++;
                                    case ENTITY_TICKING -> entityTicking++;
                                }
                            }
                            d.addLine("  EntityTicking: " + entityTicking);
                            d.addLine("  BlockTicking:  " + blockTicking);
                            d.addLine("  Full:          " + full);
                            d.addLine("  Inaccessible:  " + inaccessible);

                            // Chunk at player's feet
                            if (mc.player != null)
                            {
                                BlockPos bp = mc.player.blockPosition();
                                long packedHere = net.minecraft.world.level.ChunkPos.pack(
                                        bp.getX() >> 4, bp.getZ() >> 4);
                                PolyChunkMapData here = chunks.get(packedHere);
                                if (here != null)
                                    d.addLine("  ThisChunk: " + here.status()
                                            + " t=" + here.tickets().size());
                                else
                                    d.addLine("  ThisChunk: (no data yet)");
                            }
                        }
                    }
                },
                /*defaultEnabled*/      true,
                /*inDefaultProfile*/    true,
                /*inPerformanceProfile*/false,
                "debug." + MOD_ID + ".chunk_map_stats",
                "Chunk Map Stats"
        );
    }
}
