package net.creeperhost.polylib.client.screen.chunkmap;

import net.creeperhost.polylib.PolyLibClient;
import net.creeperhost.polylib.chunkmap.client.PolyChunkMapConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PolyChunkMapConfigScreen extends Screen {

    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public PolyChunkMapConfigScreen() {
        super(Component.translatable("screen.polylib.chunkmap.config"));
    }

    @Override
    protected void init() {
        super.init();

        PolyChunkMapConfig config = PolyLibClient.chunkMapConfig;
        if (config == null) return;

        int btnWidth = 150;
        int btnHeight = 20;

        addRenderableWidget(Button.builder(
                Component.literal("Minimap: " + config.minimapDisplayMode.name()),
                btn -> {
                    config.minimapDisplayMode = nextMode(config.minimapDisplayMode);
                    btn.setMessage(Component.literal("Minimap: " + config.minimapDisplayMode.name()));
                }).bounds(width / 2 - btnWidth / 2, height / 2 - 30, btnWidth, btnHeight).build());

        addRenderableWidget(Button.builder(
                Component.literal("Render Mode: " + config.renderMode.name()),
                btn -> {
                    config.renderMode = config.renderMode == PolyChunkMapConfig.RenderMode.STATUS ? PolyChunkMapConfig.RenderMode.TICKETS : PolyChunkMapConfig.RenderMode.STATUS;
                    btn.setMessage(Component.literal("Render Mode: " + config.renderMode.name()));
                }).bounds(width / 2 - btnWidth / 2, height / 2, btnWidth, btnHeight).build());

        addRenderableWidget(Button.builder(
                Component.literal(String.format("Minimap Zoom: %.1fx", config.minimapZoom)),
                btn -> {
                    config.minimapZoom = nextZoom(config.minimapZoom);
                    btn.setMessage(Component.literal(String.format("Minimap Zoom: %.1fx", config.minimapZoom)));
                }).bounds(width / 2 - btnWidth / 2, height / 2 + 30, btnWidth, btnHeight).build());

        addRenderableWidget(Button.builder(
                Component.literal("Minimap Size: " + config.minimapSize),
                btn -> {
                    config.minimapSize = config.minimapSize >= 200 ? 50 : config.minimapSize + 25;
                    btn.setMessage(Component.literal("Minimap Size: " + config.minimapSize));
                }).bounds(width / 2 - btnWidth / 2, height / 2 + 60, btnWidth, btnHeight).build());

        addRenderableWidget(Button.builder(
                Component.literal("Retention Ticks: " + config.retentionTicks),
                btn -> {
                    config.retentionTicks = config.retentionTicks >= 200 ? 0 : config.retentionTicks + 20;
                    btn.setMessage(Component.literal("Retention Ticks: " + config.retentionTicks));
                }).bounds(width / 2 - btnWidth / 2, height / 2 + 90, btnWidth, btnHeight).build());
    }

    private double nextZoom(double current) {
        if (current <= 0.5) return 1.0;
        if (current <= 1.0) return 2.0;
        if (current <= 2.0) return 4.0;
        return 0.5;
    }

    private PolyChunkMapConfig.MinimapDisplayMode nextMode(PolyChunkMapConfig.MinimapDisplayMode current) {
        return switch (current) {
            case ALWAYS -> PolyChunkMapConfig.MinimapDisplayMode.F3_ONLY;
            case F3_ONLY -> PolyChunkMapConfig.MinimapDisplayMode.OFF;
            case OFF -> PolyChunkMapConfig.MinimapDisplayMode.ALWAYS;
        };
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(gfx, mouseX, mouseY, partialTick);

        PolyChunkMapConfig config = PolyLibClient.chunkMapConfig;
        if (config == null) return;

        // Draw title
        gfx.text(font, title, width / 2 - font.width(title) / 2, 20, 0xFFFFFF, true);
        String sub = "Drag the minimap box to reposition it!";
        gfx.text(font, sub, width / 2 - font.width(sub) / 2, 40, 0xAAAAAA, true);

        // Draw Draggable Box for Minimap preview
        int x = config.minimapAnchorX;
        int y = config.minimapAnchorY;
        int size = config.minimapSize;

        gfx.fill(x, y, x + size, y + size, 0xAA000000); // BG
        int borderCol = isDragging ? 0xFF00FF00 : 0xFFFFFFFF;
        
        gfx.fill(x - 1, y - 1, x + size + 1, y, borderCol);
        gfx.fill(x - 1, y + size, x + size + 1, y + size + 1, borderCol);
        gfx.fill(x - 1, y, x, y + size, borderCol);
        gfx.fill(x + size, y, x + size + 1, y + size, borderCol);
        String label = "Minimap";
        gfx.text(font, label, x + size / 2 - font.width(label) / 2, y + size / 2 - 4, 0xFFFFFF, true);
    }

    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean bl) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        PolyChunkMapConfig config = PolyLibClient.chunkMapConfig;
        if (config != null && button == 0) {
            int x = config.minimapAnchorX;
            int y = config.minimapAnchorY;
            int size = config.minimapSize;
            
            if (mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size) {
                isDragging = true;
                dragOffsetX = (int) mouseX - x;
                dragOffsetY = (int) mouseY - y;
                return true;
            }
        }
        return super.mouseClicked(event, bl);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (isDragging) {
            PolyChunkMapConfig config = PolyLibClient.chunkMapConfig;
            if (config != null) {
                config.minimapAnchorX = (int) mouseX - dragOffsetX;
                config.minimapAnchorY = (int) mouseY - dragOffsetY;
            }
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
        if (event.button() == 0 && isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        if (PolyLibClient.chunkMapConfigBuilder != null) {
            PolyLibClient.chunkMapConfigBuilder.save();
        }
        super.onClose();
    }
}
