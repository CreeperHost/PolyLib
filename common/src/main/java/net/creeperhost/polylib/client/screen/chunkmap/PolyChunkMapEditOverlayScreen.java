package net.creeperhost.polylib.client.screen.chunkmap;

import net.creeperhost.polylib.PolyLibClient;
import net.creeperhost.polylib.chunkmap.client.PolyChunkMapConfig;
import net.creeperhost.polylib.chunkmap.client.PolyChunkMapOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PolyChunkMapEditOverlayScreen extends Screen {
    private final Screen parent;
    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public PolyChunkMapEditOverlayScreen(Screen parent) {
        super(Component.literal("Edit Overlay Position"));
        this.parent = parent;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(gfx, mouseX, mouseY, partialTicks);
        
        PolyChunkMapConfig config = PolyLibClient.chunkMapConfig;
        if (config == null) return;
        
        // Render the overlay preview
        PolyChunkMapOverlay.renderMinimap(gfx, this.minecraft, config.minimapAnchorX, config.minimapAnchorY);
        
        // Draw some instructions
        String line1 = "Click and drag the minimap to position it.";
        String line2 = "Press ESC to save and return.";
        int w1 = this.font.width(line1);
        int w2 = this.font.width(line2);
        
        gfx.text(this.font, line1, (this.width - w1) / 2, 20, 0xFFFFFFFF, true);
        gfx.text(this.font, line2, (this.width - w2) / 2, 35, 0xFFAAAAAA, true);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent mouseButtonEvent, boolean bl) {
        PolyChunkMapConfig config = PolyLibClient.chunkMapConfig;
        if (config != null && mouseButtonEvent.button() == 0) {
            int size = config.minimapSize;
            int x = config.minimapAnchorX;
            int y = config.minimapAnchorY;
            double mouseX = mouseButtonEvent.x();
            double mouseY = mouseButtonEvent.y();
            
            if (mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size) {
                isDragging = true;
                dragOffsetX = (int) mouseX - x;
                dragOffsetY = (int) mouseY - y;
                return true;
            }
        }
        return super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.button() == 0 && isDragging) {
            isDragging = false;
            if (PolyLibClient.chunkMapConfigBuilder != null) {
                PolyLibClient.chunkMapConfigBuilder.save();
            }
            return true;
        }
        return super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent mouseButtonEvent, double dragX, double dragY) {
        if (isDragging && PolyLibClient.chunkMapConfig != null) {
            PolyChunkMapConfig config = PolyLibClient.chunkMapConfig;
            double mouseX = mouseButtonEvent.x();
            double mouseY = mouseButtonEvent.y();
            config.minimapAnchorX = (int) mouseX - dragOffsetX;
            config.minimapAnchorY = (int) mouseY - dragOffsetY;
            
            // Constrain to screen bounds
            config.minimapAnchorX = Math.max(0, Math.min(this.width - config.minimapSize, config.minimapAnchorX));
            config.minimapAnchorY = Math.max(0, Math.min(this.height - config.minimapSize, config.minimapAnchorY));
            return true;
        }
        return super.mouseDragged(mouseButtonEvent, dragX, dragY);
    }

    @Override
    public void onClose() {
        if (PolyLibClient.chunkMapConfigBuilder != null) {
            PolyLibClient.chunkMapConfigBuilder.save();
        }
        this.minecraft.setScreen(parent);
    }
}
