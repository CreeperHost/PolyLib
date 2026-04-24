package net.creeperhost.polylib.event.events.client;

import net.creeperhost.polylib.event.data.CancelContext;
import net.creeperhost.polylib.event.PolyEvent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Client-side entity and world-object rendering events.
 *
 * <p>All events in this class use MC render-state objects
 * ({@link LivingEntityRenderState}, {@link AvatarRenderState}, etc.) rather than
 * live entity references, reflecting the MC 26.1.2 extract-then-submit pipeline.
 */
public final class PolyEntityRenderEvents
{
    // ── T18-A: Living entity rendering ───────────────────────────────────────

    /**
     * Fired before a living entity is rendered.  Cancel to skip the render.
     * <p>
     * NeoForge: {@code RenderLivingEvent.Pre}<br>
     * Fabric: mixin on {@code LivingEntityRenderer#submit} at HEAD (cancellable)
     */
    public static final PolyEvent<RenderLivingPre> RENDER_LIVING_PRE = PolyEvent.create(
            handlers -> (renderState, poseStack, partialTick, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onRenderLivingPre(renderState, poseStack, partialTick, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    /**
     * Fired after a living entity has been rendered.
     * <p>
     * NeoForge: {@code RenderLivingEvent.Post}<br>
     * Fabric: mixin on {@code LivingEntityRenderer#submit} at RETURN
     */
    public static final PolyEvent<RenderLivingPost> RENDER_LIVING_POST = PolyEvent.create(
            handlers -> (renderState, poseStack, partialTick) ->
                    handlers.forEach(h -> h.onRenderLivingPost(renderState, poseStack, partialTick)));

    /**
     * Fired before a player avatar is rendered.  Cancel to skip the render.
     * Fires in addition to {@link #RENDER_LIVING_PRE} for player entities.
     * <p>
     * NeoForge: {@code RenderPlayerEvent.Pre}<br>
     * Fabric: mixin on {@code LivingEntityRenderer#submit} at HEAD (cancellable),
     *         checking {@code renderState instanceof AvatarRenderState}
     */
    public static final PolyEvent<RenderPlayerPre> RENDER_PLAYER_PRE = PolyEvent.create(
            handlers -> (renderState, poseStack, partialTick, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onRenderPlayerPre(renderState, poseStack, partialTick, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    /**
     * Fired after a player avatar has been rendered.
     * <p>
     * NeoForge: {@code RenderPlayerEvent.Post}<br>
     * Fabric: mixin on {@code LivingEntityRenderer#submit} at RETURN,
     *         checking {@code renderState instanceof AvatarRenderState}
     */
    public static final PolyEvent<RenderPlayerPost> RENDER_PLAYER_POST = PolyEvent.create(
            handlers -> (renderState, poseStack, partialTick) ->
                    handlers.forEach(h -> h.onRenderPlayerPost(renderState, poseStack, partialTick)));

    // ── T18-B: World-object rendering ─────────────────────────────────────────

    /**
     * Fired before an entity name tag is rendered.  Cancel to suppress the tag.
     * <p>
     * NeoForge: {@code RenderNameTagEvent.DoRender}<br>
     * Fabric: mixin on {@code EntityRenderer#submitNameDisplay} at HEAD (cancellable)
     */
    public static final PolyEvent<RenderNameTag> RENDER_NAME_TAG = PolyEvent.create(
            handlers -> (renderState, displayName, poseStack, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onRenderNameTag(renderState, displayName, poseStack, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    /**
     * Fired before the held-hand item/arms are rendered for the current frame.
     * Cancel to suppress the hand render entirely.
     * <p>
     * NeoForge: {@code RenderHandEvent}<br>
     * Fabric: mixin on {@code ItemInHandRenderer#renderArmWithItem} at HEAD (cancellable)
     */
    public static final PolyEvent<RenderHand> RENDER_HAND = PolyEvent.create(
            handlers -> (hand, itemStack, poseStack, partialTick, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onRenderHand(hand, itemStack, poseStack, partialTick, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    /**
     * Fired before a bare player arm is rendered in first-person view.
     * Cancel to suppress the arm.
     * <p>
     * NeoForge: {@code RenderArmEvent}<br>
     * Fabric: mixin on {@code ItemInHandRenderer#renderPlayerArm} at HEAD (cancellable)
     */
    public static final PolyEvent<RenderArm> RENDER_ARM = PolyEvent.create(
            handlers -> (arm, player, packedLight, poseStack, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onRenderArm(arm, player, packedLight, poseStack, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    /**
     * Fired before the item inside an item frame is rendered.  Cancel to suppress it.
     * <p>
     * NeoForge: {@code RenderItemInFrameEvent}<br>
     * Fabric: mixin on {@code ItemFrameRenderer#submit} at HEAD (cancellable)
     */
    public static final PolyEvent<RenderItemInFrame> RENDER_ITEM_IN_FRAME = PolyEvent.create(
            handlers -> (itemRenderState, frameRenderState, poseStack, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onRenderItemInFrame(itemRenderState, frameRenderState, poseStack, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    // ── T18-C: Block screen effects ───────────────────────────────────────────

    /**
     * Fired before a block-type screen overlay (fire, water, or block) is rendered.
     * Cancel to suppress the overlay.
     * <p>
     * NeoForge: {@code RenderBlockScreenEffectEvent}<br>
     * Fabric: no equivalent (NeoForge-primary)
     */
    public static final PolyEvent<RenderBlockScreenEffect> RENDER_BLOCK_SCREEN_EFFECT = PolyEvent.create(
            handlers -> (player, pos, state, overlayType, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onRenderBlockScreenEffect(player, pos, state, overlayType, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    // ── T18-E: Entity render features ─────────────────────────────────────────

    /**
     * Fired to determine whether the player's cape should be rendered.
     * Cancel to suppress the cape.
     * <p>
     * NeoForge: mixin {@code MixinCapeLayerNF} on {@code CapeLayer#submit} at HEAD (cancellable)<br>
     * Fabric: {@code LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER}
     */
    public static final PolyEvent<AllowCapeRender> ALLOW_CAPE_RENDER = PolyEvent.create(
            handlers -> (renderState, ctx) ->
            {
                for (var h : handlers)
                {
                    h.onAllowCapeRender(renderState, ctx);
                    if (ctx.isCancelled()) break;
                }
            });

    private PolyEntityRenderEvents() {}

    // ── SAM interfaces ────────────────────────────────────────────────────────

    @FunctionalInterface
    public interface RenderLivingPre
    {
        void onRenderLivingPre(LivingEntityRenderState renderState, PoseStack poseStack,
                               float partialTick, CancelContext ctx);
    }

    @FunctionalInterface
    public interface RenderLivingPost
    {
        void onRenderLivingPost(LivingEntityRenderState renderState, PoseStack poseStack,
                                float partialTick);
    }

    @FunctionalInterface
    public interface RenderPlayerPre
    {
        void onRenderPlayerPre(AvatarRenderState renderState, PoseStack poseStack,
                               float partialTick, CancelContext ctx);
    }

    @FunctionalInterface
    public interface RenderPlayerPost
    {
        void onRenderPlayerPost(AvatarRenderState renderState, PoseStack poseStack,
                                float partialTick);
    }

    @FunctionalInterface
    public interface RenderNameTag
    {
        void onRenderNameTag(EntityRenderState renderState, Component displayName,
                             PoseStack poseStack, CancelContext ctx);
    }

    @FunctionalInterface
    public interface RenderHand
    {
        void onRenderHand(InteractionHand hand, ItemStack itemStack, PoseStack poseStack,
                          float partialTick, CancelContext ctx);
    }

    @FunctionalInterface
    public interface RenderArm
    {
        void onRenderArm(HumanoidArm arm, AbstractClientPlayer player, int packedLight,
                         PoseStack poseStack, CancelContext ctx);
    }

    @FunctionalInterface
    public interface RenderItemInFrame
    {
        void onRenderItemInFrame(ItemStackRenderState itemRenderState,
                                 ItemFrameRenderState frameRenderState,
                                 PoseStack poseStack, CancelContext ctx);
    }

    @FunctionalInterface
    public interface RenderBlockScreenEffect
    {
        void onRenderBlockScreenEffect(Player player, BlockPos pos, BlockState state,
                                       ScreenOverlayType overlayType, CancelContext ctx);
    }

    @FunctionalInterface
    public interface AllowCapeRender
    {
        void onAllowCapeRender(AvatarRenderState renderState, CancelContext ctx);
    }

    /** Cross-loader representation of the block screen overlay type. */
    public enum ScreenOverlayType
    {
        FIRE, BLOCK, WATER
    }
}
