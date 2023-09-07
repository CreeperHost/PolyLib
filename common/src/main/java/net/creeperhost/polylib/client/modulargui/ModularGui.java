package net.creeperhost.polylib.client.modulargui;

import net.creeperhost.polylib.client.modulargui.elements.GuiElement;
import net.creeperhost.polylib.client.modulargui.lib.DynamicTextures;
import net.creeperhost.polylib.client.modulargui.lib.GuiBuilder;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

/**
 * The modular gui system is built around "Gui Elements" but those elements need to be rendered by a base parent element. That's what this class is.
 * This class is essentially just a container for the root gui element.
 * <p>
 * Implementation: TODO...
 * <p>
 * Created by brandon3055 on 18/08/2023
 * @see GuiBuilder
 */
public class ModularGui implements GuiParent<ModularGui> {

    private final GuiBuilder builder;
    private final GuiElement<?> root;

    private boolean guiBuilt = false;
    private boolean pauseScreen = false;
    private boolean closeOnEscape = true;

    private Font font;
    private Minecraft mc;
    private int screenWidth;
    private int screenHeight;

    private Component guiTitle = Component.empty();
    private GuiElement<?> focused;

    /**
     * @param builder The gui builder that will be used to construct this modular gui when the screen is initialized.
     */
    public ModularGui(GuiBuilder builder) {
        this.builder = builder;
        if (builder instanceof DynamicTextures textures) textures.makeTextures(DynamicTextures.DynamicTexture::guiTexturePath);
        Minecraft mc = Minecraft.getInstance();
        updateScreenData(mc, mc.font, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        this.root = builder.createRootElement(this);
        this.root.initElement(this);
    }

    //=== Modular Gui Setup ===//

    public void setGuiTitle(@NotNull Component guiTitle) {
        this.guiTitle = guiTitle;
    }

    @NotNull
    public Component getGuiTitle() {
        return guiTitle;
    }

    /**
     * @param pauseScreen Should a single-player game pause while this screen is open?
     */
    public void setPauseScreen(boolean pauseScreen) {
        this.pauseScreen = pauseScreen;
    }

    public boolean isPauseScreen() {
        return pauseScreen;
    }

    public void setCloseOnEscape(boolean closeOnEscape) {
        this.closeOnEscape = closeOnEscape;
    }

    public boolean closeOnEscape() {
        return closeOnEscape;
    }

    /**
     * @return the root element.
     */
    public GuiElement<?> getRoot() {
        return root;
    }

    /**
     * Sets up this gui to render like any other standards gui with the specified width and height.
     * Meaning, the root element (usually the gui background image) will be centered on the screen, and will have the specified width and height.
     * <p>
     *
     * @param guiWidth  Gui Width
     * @param guiHeight Gui Height
     * @see #initFullscreenGui()
     */
    public ModularGui initStandardGui(int guiWidth, int guiHeight) {
        root.constrain(GeoParam.WIDTH, Constraint.literal(guiWidth));
        root.constrain(GeoParam.HEIGHT, Constraint.literal(guiHeight));
        root.constrain(GeoParam.LEFT, Constraint.midPoint(get(GeoParam.LEFT), get(GeoParam.RIGHT), guiWidth / -2D));
        root.constrain(GeoParam.TOP, Constraint.midPoint(get(GeoParam.TOP), get(GeoParam.BOTTOM), guiHeight / -2D));
        return this;
    }

    /**
     * Sets up this gui to render as a full screen gui.
     * Meaning the root element's geometry will match that of the underlying minecraft screen.
     * <p>
     * @see #initStandardGui(int, int)
     */
    public ModularGui initFullscreenGui() {
        root.constrain(GeoParam.WIDTH, Constraint.match(get(GeoParam.WIDTH)));
        root.constrain(GeoParam.HEIGHT, Constraint.match(get(GeoParam.HEIGHT)));
        root.constrain(GeoParam.TOP, Constraint.match(get(GeoParam.TOP)));
        root.constrain(GeoParam.LEFT, Constraint.match(get(GeoParam.LEFT)));
        return this;
    }

    //=== Modular Gui Passthrough Methods ===//

    /**
     * Primary render method for ModularGui. The screen implementing ModularGui must call this in its render method.
     *
     * @param buffers BufferSource can be retried from {@link net.minecraft.client.gui.GuiGraphics}
     * @return true if an overlay such as a tooltip is currently being drawn.
     */
    public boolean render(MultiBufferSource.BufferSource buffers, float partialTicks) {
        root.clearGeometryCache();
        GuiRender render = new GuiRender(mc, buffers);
        double mouseX = computeMouseX();
        double mouseY = computeMouseY();
        root.render(render, mouseX, mouseY, partialTicks);

        //Ensure overlay is rendered at a depth of ether 400 or total element depth + 100 (whichever is greater)
        double depth = root.getCombinedElementDepth();
        if (depth <= 300) {
            render.pose().translate(0, 0, 400 - depth);
        } else {
            render.pose().translate(0, 0, 100);
        }

        return root.renderOverlay(render, mouseX, mouseY, partialTicks, false);
    }

    /**
     * Primary update / tick method. Must be called from the tick method of the implementing screen.
     */
    public void tick() {
        root.tick(computeMouseX(), computeMouseY());
    }

    /**
     * Pass through for the mouseMoved event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param mouseX new mouse X position
     * @param mouseY new mouse Y position
     */
    public void mouseMoved(double mouseX, double mouseY) {
        root.mouseMoved(mouseX, mouseY);
    }

    /**
     * Pass through for the mouseClicked event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse Button
     * @return true if this event has been consumed.
     */
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return root.mouseClicked(mouseX, mouseY, button, false);
    }

    /**
     * Pass through for the mouseReleased event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse Button
     * @return true if this event has been consumed.
     */
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return root.mouseReleased(mouseX, mouseY, button, false);
    }

    /**
     * Pass through for the keyPressed event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param key       the keyboard key that was pressed.
     * @param scancode  the system-specific scancode of the key
     * @param modifiers bitfield describing which modifier keys were held down.
     * @return true if this event has been consumed.
     */
    public boolean keyPressed(int key, int scancode, int modifiers) {
        return root.keyPressed(key, scancode, modifiers, false);
    }

    /**
     * Pass through for the keyReleased event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param key       the keyboard key that was released.
     * @param scancode  the system-specific scancode of the key
     * @param modifiers bitfield describing which modifier keys were held down.
     * @return true if this event has been consumed.
     */
    public boolean keyReleased(int key, int scancode, int modifiers) {
        return root.keyReleased(key, scancode, modifiers, false);
    }

    /**
     * Pass through for the charTyped event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param character The character typed.
     * @param modifiers bitfield describing which modifier keys were held down.
     * @return true if this event has been consumed.
     */
    public boolean charTyped(char character, int modifiers) {
        return root.charTyped(character, modifiers, false);
    }

    /**
     * Pass through for the mouseScrolled event. Any screen implementing {@link ModularGui} must pass through this event.
     *
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param scroll Scroll direction and amount
     * @return true if this event has been consumed.
     */
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        return root.mouseScrolled(mouseX, mouseY, scroll, false);
    }

    //=== Basic Minecraft Stuff ===//

    protected void updateScreenData(Minecraft mc, Font font, int screenWidth, int screenHeight) {
        this.mc = mc;
        this.font = font;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    @Override
    public void onScreenInit(Minecraft mc, Font font, int screenWidth, int screenHeight) {
        updateScreenData(mc, font, screenWidth, screenHeight);
        root.onScreenInit(mc, font, screenWidth, screenHeight);

        if (!guiBuilt) {
            builder.buildGui(this);
            guiBuilt = true;
        }
    }

    @Override
    public Minecraft mc() {
        return mc;
    }

    @Override
    public Font font() {
        return font;
    }

    @Override
    public int scaledScreenWidth() {
        return screenWidth;
    }

    @Override
    public int scaledScreenHeight() {
        return screenHeight;
    }

    //=== Child Elements ===//

    @Override
    public List<GuiElement<?>> getChildren() {
        throw new UnsupportedOperationException("Child elements must be managed via the root gui element not the modular gui itself.");
    }

    @Override
    public void addChild(GuiElement<?> child) {
        if (root == null) return; //Root installation calls parent.addChild
        throw new UnsupportedOperationException("Child elements must be managed via the root gui element not the modular gui itself.");
    }

    @Override
    public ModularGui addChild(Consumer<ModularGui> createChild) {
        throw new UnsupportedOperationException("Child elements must be managed via the root gui element not the modular gui itself.");
    }

    @Override
    public void adoptChild(GuiElement<?> child) {
        throw new UnsupportedOperationException("Child elements must be managed via the root gui element not the modular gui itself.");
    }

    @Override
    public void removeChild(GuiElement<?> child) {
        throw new UnsupportedOperationException("Child elements must be managed via the root gui element not the modular gui itself.");
    }

    //=== Geometry ===//
    //The geometry of the base ModularGui class should always match the underlying minecraft screen.

    @Override
    public double xMin() {
        return 0;
    }

    @Override
    public double xMax() {
        return screenWidth;
    }

    @Override
    public double xSize() {
        return screenWidth;
    }

    @Override
    public double yMin() {
        return 0;
    }

    @Override
    public double yMax() {
        return screenHeight;
    }

    @Override
    public double ySize() {
        return screenHeight;
    }

    //=== Other ===//

//    @Override
//    public void setFocused(@Nullable GuiElement<?> element) {
//        focused = element;
//    }
//
//    @Nullable
//    @Override
//    public GuiElement<?> getFocused() {
//        return focused;
//    }

    public double computeMouseX() {
        return mc.mouseHandler.xpos() * (double) mc.getWindow().getGuiScaledWidth() / (double) mc.getWindow().getScreenWidth();
    }

    public double computeMouseY() {
        return mc.mouseHandler.ypos() * (double) mc.getWindow().getGuiScaledHeight() / (double) mc.getWindow().getScreenHeight();
    }
}
