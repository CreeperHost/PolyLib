package net.creeperhost.polylib.event.events.server;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

import java.util.List;

public final class PolyBlockEvents
{
    /**
     * Fired before a player breaks a block. Call {@link CancelContext#cancel()} to prevent the break.
     * <p>
     * NeoForge: {@code BreakBlockEvent} (cancellable)<br>
     * Fabric: {@code PlayerBlockBreakEvents.BEFORE} (boolean gate)
     */
    public static final PolyEvent<Break> BREAK = PolyEvent.create(handlers -> (player, level, pos, state, ctx) ->
    {
        for (var h : handlers)
        {
            h.onBreak(player, level, pos, state, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when any entity places a block. Call {@link CancelContext#cancel()} to prevent the placement.
     * <p>
     * NeoForge: {@code BlockEvent.EntityPlaceEvent} (cancellable)<br>
     * Fabric: no native equivalent — NeoForge-only cancel support
     */
    public static final PolyEvent<Place> PLACE = PolyEvent.create(handlers -> (entity, level, pos, placed, ctx) ->
    {
        for (var h : handlers)
        {
            h.onPlace(entity, level, pos, placed, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired before a crop grows (natural or bonemeal). Call {@link CancelContext#cancel()} to prevent growth.
     * <p>
     * NeoForge: {@code CropGrowEvent.Pre} (result-based cancel)<br>
     * Fabric: mixin into {@code CropBlock#grow}
     */
    public static final PolyEvent<CropGrow> CROP_GROW_PRE = PolyEvent.create(handlers -> (level, pos, state, ctx) ->
    {
        for (var h : handlers)
        {
            h.onCropGrow(level, pos, state, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired after a crop has grown.
     * <p>
     * NeoForge: {@code CropGrowEvent.Post}<br>
     * Fabric: mixin into {@code CropBlock#grow} RETURN
     */
    public static final PolyEvent<CropGrowPost> CROP_GROW_POST = PolyEvent.create(
            handlers -> (level, pos, state) -> handlers.forEach(h -> h.onCropGrowPost(level, pos, state)));

    /**
     * Fired when an entity tramples farmland. Call {@link CancelContext#cancel()} to prevent trampling.
     * <p>
     * NeoForge: {@code BlockEvent.FarmlandTrampleEvent} (cancellable)<br>
     * Fabric: no native equivalent — NeoForge-only cancel support
     */
    public static final PolyEvent<FarmlandTrample> FARMLAND_TRAMPLE = PolyEvent.create(handlers -> (entity, level, pos, ctx) ->
    {
        for (var h : handlers)
        {
            h.onFarmlandTrample(entity, level, pos, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    private PolyBlockEvents()
    {
    }

    // ── Tier 3 events ───────────────────────────────────────────────────────

    /**
     * Fired when a Nether portal frame is about to generate portal blocks.
     * Call {@link CancelContext#cancel()} to prevent the portal from spawning.
     * <p>
     * NeoForge: {@code BlockEvent.PortalSpawnEvent}<br>
     * Fabric: mixin into {@code PortalShape#createPortalBlocks}
     */
    public static final PolyEvent<PortalSpawn> PORTAL_SPAWN = PolyEvent.create(handlers -> (level, pos, state, ctx) ->
    {
        for (var h : handlers)
        {
            h.onPortalSpawn(level, pos, state, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired before a piston extends or retracts.
     * Call {@link CancelContext#cancel()} to prevent the piston from moving.
     * <p>
     * NeoForge: {@code PistonEvent.Pre}<br>
     * Fabric: mixin into {@code PistonBaseBlock#triggerEvent}
     */
    public static final PolyEvent<PistonPre> PISTON_PRE = PolyEvent.create(handlers -> (level, pos, direction, extending, ctx) ->
    {
        for (var h : handlers)
        {
            h.onPistonPre(level, pos, direction, extending, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired after a piston has extended or retracted. Not cancellable.
     * <p>
     * NeoForge: {@code PistonEvent.Post}<br>
     * Fabric: mixin into {@code PistonBaseBlock#triggerEvent} at RETURN
     */
    public static final PolyEvent<PistonPost> PISTON_POST = PolyEvent.create(
            handlers -> (level, pos, direction, extending) ->
                    handlers.forEach(h -> h.onPistonPost(level, pos, direction, extending)));

    /**
     * Fired when a note block is about to play its note.
     * Call {@link CancelContext#cancel()} to silence the note.
     * <p>
     * NeoForge: {@code NoteBlockEvent.Play}<br>
     * Fabric: mixin into {@code NoteBlock#triggerEvent}
     */
    public static final PolyEvent<NoteBlockPlay> NOTE_BLOCK_PLAY = PolyEvent.create(handlers -> (level, pos, state, ctx) ->
    {
        for (var h : handlers)
        {
            h.onNoteBlockPlay(level, pos, state, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a flowing fluid is about to convert into a source block.
     * Call {@link CancelContext#cancel()} to prevent the conversion.
     * <p>
     * NeoForge: {@code CreateFluidSourceEvent} (uses {@code setCanConvert(false)})<br>
     * Fabric: mixin into {@code FlowingFluid#spreadTo}
     */
    public static final PolyEvent<FluidSource> FLUID_SOURCE = PolyEvent.create(handlers -> (level, pos, ctx) ->
    {
        for (var h : handlers)
        {
            h.onFluidSource(level, pos, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    @FunctionalInterface
    public interface Break
    {
        void onBreak(ServerPlayer player, LevelAccessor level, BlockPos pos, BlockState state, CancelContext ctx);
    }

    @FunctionalInterface
    public interface Place
    {
        void onPlace(Entity entity, LevelAccessor level, BlockPos pos, BlockState placed, CancelContext ctx);
    }

    @FunctionalInterface
    public interface CropGrow
    {
        void onCropGrow(Level level, BlockPos pos, BlockState state, CancelContext ctx);
    }

    @FunctionalInterface
    public interface CropGrowPost
    {
        void onCropGrowPost(Level level, BlockPos pos, BlockState state);
    }

    @FunctionalInterface
    public interface FarmlandTrample
    {
        void onFarmlandTrample(Entity entity, Level level, BlockPos pos, CancelContext ctx);
    }

    @FunctionalInterface
    public interface PortalSpawn
    {
        void onPortalSpawn(LevelAccessor level, BlockPos pos, BlockState state, CancelContext ctx);
    }

    @FunctionalInterface
    public interface PistonPre
    {
        void onPistonPre(LevelAccessor level, BlockPos pos, Direction direction, boolean extending, CancelContext ctx);
    }

    @FunctionalInterface
    public interface PistonPost
    {
        void onPistonPost(LevelAccessor level, BlockPos pos, Direction direction, boolean extending);
    }

    @FunctionalInterface
    public interface NoteBlockPlay
    {
        void onNoteBlockPlay(LevelAccessor level, BlockPos pos, BlockState state, CancelContext ctx);
    }

    @FunctionalInterface
    public interface FluidSource
    {
        void onFluidSource(LevelAccessor level, BlockPos pos, CancelContext ctx);
    }

    // ── Tier 8 ────────────────────────────────────────────────────────────────

    /**
     * Fired when a broken block is computing its drops. Modify the {@code drops} list to change results.
     * <p>
     * NeoForge: {@code BlockDropsEvent}<br>
     * Fabric: mixin on {@code Block#dropResources}
     */
    public static final PolyEvent<BlockDrops> BLOCK_DROPS = PolyEvent.create(
            handlers -> (level, pos, state, drops) ->
                    handlers.forEach(h -> h.onBlockDrops(level, pos, state, drops)));

    /**
     * Fired when an entity places multiple blocks at once (door, bed, etc.). Cancellable.
     * <p>
     * NeoForge: {@code BlockEvent.EntityMultiPlaceEvent}<br>
     * Fabric: no native equivalent
     */
    public static final PolyEvent<EntityMultiPlace> ENTITY_MULTI_PLACE = PolyEvent.create(handlers -> (level, entity, placedState, ctx) ->
    {
        for (var h : handlers)
        {
            h.onEntityMultiPlace(level, entity, placedState, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    @FunctionalInterface
    public interface BlockDrops
    {
        /** {@code drops} is a mutable list of item entities; add or remove as needed. */
        void onBlockDrops(ServerLevel level, BlockPos pos, BlockState state, List<ItemEntity> drops);
    }

    @FunctionalInterface
    public interface EntityMultiPlace
    {
        void onEntityMultiPlace(ServerLevel level, Entity entity, BlockState placedState, CancelContext ctx);
    }

    // ── Tier 12 ────────────────────────────────────────────────────────────────

    /**
     * Fired when a flowing fluid places a solid block (obsidian, cobblestone, etc.). Cancellable.
     * <p>
     * NeoForge: {@code BlockEvent.FluidPlaceBlockEvent}<br>
     * Fabric: mixin on {@code FlowingFluid#spreadTo}
     */
    public static final PolyEvent<FluidPlaceBlock> FLUID_PLACE_BLOCK = PolyEvent.create(handlers -> (level, pos, liquidPos, state, ctx) ->
    {
        for (var h : handlers)
        {
            h.onFluidPlaceBlock(level, pos, liquidPos, state, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    /**
     * Fired when a block notifies its neighbors of a state change. Not cancellable.
     * <p>
     * NeoForge: {@code BlockEvent.NeighborNotifyEvent}<br>
     * Fabric: mixin on {@code Level#updateNeighborsAt}
     */
    public static final PolyEvent<NeighborNotify> NEIGHBOR_NOTIFY = PolyEvent.create(
            handlers -> (level, pos, state, notifiedSides, forceRedstoneUpdate) ->
                    handlers.forEach(h -> h.onNeighborNotify(level, pos, state, notifiedSides, forceRedstoneUpdate)));

    /**
     * Fired when a block feature (mushroom, tree, azalea, etc.) grows in the world. Not cancellable.
     * <p>
     * NeoForge: {@code BlockGrowFeature}<br>
     * Fabric: mixin on {@code BonemealableBlock#performBonemeal}
     */
    public static final PolyEvent<BlockGrowFeature> BLOCK_GROW_FEATURE = PolyEvent.create(
            handlers -> (level, pos, feature) -> handlers.forEach(h -> h.onBlockGrowFeature(level, pos, feature)));

    /**
     * Fired when ground blocks are altered by tree/feature generation. Not cancellable.
     * <p>
     * NeoForge: {@code AlterGroundEvent}<br>
     * Fabric: mixin on {@code AlterGroundDecorator#place}
     */
    public static final PolyEvent<AlterGround> ALTER_GROUND = PolyEvent.create(
            handlers -> (level, pos, provider) -> handlers.forEach(h -> h.onAlterGround(level, pos, provider)));

    /**
     * Fired when a note block's note is changed by a player right-click. Cancellable.
     * <p>
     * NeoForge: {@code NoteBlockEvent.Change}<br>
     * Fabric: mixin on {@code NoteBlock#use}
     */
    public static final PolyEvent<NoteBlockChange> NOTE_BLOCK_CHANGE = PolyEvent.create(handlers -> (level, pos, oldInstrument, newInstrument, oldNote, newNote, ctx) ->
    {
        for (var h : handlers)
        {
            h.onNoteBlockChange(level, pos, oldInstrument, newInstrument, oldNote, newNote, ctx);
            if (ctx.isCancelled()) break;
        }
    });

    @FunctionalInterface
    public interface FluidPlaceBlock
    {
        void onFluidPlaceBlock(Level level, BlockPos pos, BlockPos liquidPos, BlockState state, CancelContext ctx);
    }

    @FunctionalInterface
    public interface NeighborNotify
    {
        void onNeighborNotify(Level level, BlockPos pos, BlockState state,
                              EnumSet<Direction> notifiedSides, boolean forceRedstoneUpdate);
    }

    @FunctionalInterface
    public interface BlockGrowFeature
    {
        void onBlockGrowFeature(ServerLevel level, BlockPos pos, Holder<ConfiguredFeature<?, ?>> feature);
    }

    @FunctionalInterface
    public interface AlterGround
    {
        void onAlterGround(ServerLevel level, BlockPos pos, BlockStateProvider provider);
    }

    @FunctionalInterface
    public interface NoteBlockChange
    {
        void onNoteBlockChange(Level level, BlockPos pos,
                               NoteBlockInstrument oldInstrument, NoteBlockInstrument newInstrument,
                               int oldNote, int newNote, CancelContext ctx);
    }
}
