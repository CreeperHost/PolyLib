package net.creeperhost.polylib.development;

import dev.architectury.event.events.client.ClientGuiEvent;
import net.creeperhost.polylib.client.render.RenderUtils;
import net.creeperhost.polylib.helpers.LevelHelper;
import net.creeperhost.polylib.helpers.VectorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.concurrent.atomic.AtomicInteger;

public class FakeWaila
{
    public static void init()
    {
        ClientGuiEvent.RENDER_HUD.register((matrices, tickDelta) ->
        {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            if (!player.getMainHandItem().is(Items.STICK)) return;

            Level level = player.level;
            BlockHitResult blockHitResult = VectorHelper.getLookingAt(player, 5);
            Font font = Minecraft.getInstance().font;
            if (blockHitResult != null)
            {
                BlockState blockState = level.getBlockState(blockHitResult.getBlockPos());
                if (blockState != null && !LevelHelper.isAir(level, blockHitResult.getBlockPos()))
                {
                    matrices.pushPose();
                    boolean sneaking = player.isShiftKeyDown();
                    int startX = 10;
                    int startY = 10;
                    //TODO get max width and height before drawing
                    FormattedCharSequence title = blockState.getBlock().getName().getVisualOrderText();
                    ClientTooltipComponent s = ClientTooltipComponent.create(title);
                    int width = sneaking ? s.getWidth(font) + 180 : s.getWidth(font) + 80;
                    int height = sneaking ? 280 : 50;

                    RenderUtils.renderTooltipBox(matrices, startX, startY, width, height);

                    ItemStack stack = new ItemStack(blockState.getBlock());
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    itemRenderer.renderGuiItem(stack, startX, startY);
                    font.draw(matrices, blockState.getBlock().getName(), startX + 20, startY + 4, -1);
                    if (!sneaking)
                    {
                        font.draw(matrices, "Has BlockEntity: " + blockState.hasBlockEntity(), startX + 20, startY + 16,
                                -1);
                        int light = blockState.getLightEmission();
                        font.draw(matrices, "Light: " + light, startX + 20, startY + 28, -1);
                    } else
                    {
                        AtomicInteger tagY = new AtomicInteger(startY + 16);

                        blockState.getTags().forEach(tagKey ->
                        {
                            font.draw(matrices, tagKey.location().toString(), startX + 20, tagY.get(), -1);
                            tagY.addAndGet(12);
                        });
                    }
                    matrices.popPose();
                }
            }
        });
    }
}
