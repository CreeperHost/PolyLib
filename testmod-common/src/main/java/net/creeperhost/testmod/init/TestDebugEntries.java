package net.creeperhost.testmod.init;

import net.creeperhost.polylib.debug.PolyDebugDisplayer;
import net.creeperhost.polylib.debug.PolyDebugEntry;
import net.creeperhost.polylib.debug.PolyDebugEntryType;
import net.creeperhost.polylib.debug.PolyDebugRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

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
    public static PolyDebugEntryType POLYLIB_INFO;
    public static PolyDebugEntryType WORLD_INFO;
    public static PolyDebugEntryType REDUCED_TEST;

    private TestDebugEntries() {}

    public static void init()
    {
        // ── 1. PolyLib Info ──────────────────────────────────────────────────
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
    }
}
