package net.creeperhost.testmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.creeperhost.polylib.client.render.GhostBlockRenderer;
import net.creeperhost.polylib.helpers.VectorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class PlacementRenderer
{
    private static MultiBufferSource.BufferSource buffer = null;

    public static void render(PoseStack poseStack)
    {
        final Minecraft mc = Minecraft.getInstance();
        if(mc.player == null) return;
        if(mc.player.getMainHandItem().isEmpty()) return;
        Item item = mc.player.getMainHandItem().getItem();
        if(mc.level == null) return;

        if(Block.byItem(item) != Blocks.AIR)
        {
            Block verticalSlabBlock = Block.byItem(item);
            BlockHitResult blockHitResult = VectorHelper.getLookingAt(mc.player, 10);

            EntityRenderDispatcher erd = mc.getEntityRenderDispatcher();
            double renderPosX = erd.camera.getPosition().x();
            double renderPosY = erd.camera.getPosition().y();
            double renderPosZ = erd.camera.getPosition().z();

            poseStack.pushPose();
            poseStack.translate(-renderPosX, -renderPosY, -renderPosZ);

            if(buffer == null) buffer = GhostBlockRenderer.initBuffers(mc.renderBuffers().bufferSource());

            BlockPos checkPos = null;
            if (mc.hitResult instanceof BlockHitResult blockRes)
            {
                checkPos = blockRes.getBlockPos().relative(blockRes.getDirection());
            }
            if(checkPos == null) return;

            BlockState blockState = verticalSlabBlock.getStateForPlacement(new BlockPlaceContext(mc.player, InteractionHand.MAIN_HAND, mc.player.getMainHandItem(), blockHitResult));
            if(blockState == null) return;

            if(verticalSlabBlock.canSurvive(blockState, mc.level, checkPos))
            {
                GhostBlockRenderer.renderBlock(blockState, checkPos, poseStack, buffer);
            }

            buffer.endBatch();
            poseStack.popPose();
        }
    }
}
