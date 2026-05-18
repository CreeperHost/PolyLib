package net.creeperhost.polylib.chat.client;

import net.creeperhost.polylib.chat.ChatChannel;
import net.creeperhost.polylib.chat.ChatMember;
import net.creeperhost.polylib.chat.RichChatMessage;
import net.creeperhost.polylib.chat.client.PulseEffect;
import net.creeperhost.polylib.chat.client.ChatNotifications;
import net.creeperhost.polylib.chat.layout.ChatWindowLayout;
import net.creeperhost.polylib.chat.layout.SnapCorner;
import net.creeperhost.polylib.chat.client.tab.ChatTabInjection;
import net.creeperhost.polylib.chat.client.tab.ChatTabRegistry;
import net.creeperhost.polylib.chat.client.tab.ChatTabBarSide;
import net.creeperhost.polylib.chat.client.tab.ChatTabBarTop;
import net.creeperhost.polylib.chat.client.tab.TabPosition;
import net.creeperhost.polylib.client.modulargui.elements.GuiButton;
import net.creeperhost.polylib.client.modulargui.elements.GuiRectangle;
import net.creeperhost.polylib.client.modulargui.elements.GuiScrolling;
import net.creeperhost.polylib.client.modulargui.elements.GuiText;
import net.creeperhost.polylib.client.modulargui.elements.GuiTextField;
import net.creeperhost.polylib.client.modulargui.elements.GuiTexture;
import net.creeperhost.polylib.client.modulargui.elements.GuiWindow;
import net.creeperhost.polylib.client.modulargui.lib.Constraints;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint;
import net.creeperhost.polylib.client.modulargui.lib.geometry.Rectangle;
import net.creeperhost.polylib.client.modulargui.sprite.Material;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.elements.GuiElement;
import net.creeperhost.polylib.client.modulargui.lib.ForegroundRender;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * The actual chat UI that renders a ChatChannel.
 */
public class FloatingChatWindow extends GuiWindow implements ForegroundRender {

    private static final int CORNER_SNAP_THRESHOLD = 40;
    private static final int DOCK_THRESHOLD = 10;

    /**
     * Persists minimized state across chat close/reopen (keyed by channel ID path).
     * Cleared when a window is permanently closed (docked or X'd).
     */
    private static final java.util.Map<String, Boolean> MINIMIZED_STATE = new java.util.HashMap<>();

    /**
     * Persists window position (xMin, yMin, width, height) across chat close/reopen.
     * Cleared when a window is permanently closed.
     */
    public static final java.util.Map<String, double[]> SAVED_POSITIONS = new java.util.HashMap<>();

    private final ChatChannel channel;
    
    private final GuiRectangle sidebar;
    private final GuiScrolling messageArea;
    private final GuiTextField inputField;

    // Plan B: window manager reference, minimize, snap
    private ChatWindowManager windowManager;
    private final GuiButton minimizeButton;
    private boolean minimized = false;
    private double restoreHeight = 150;
    private SnapCorner currentSnapCorner = null;
    private TabPosition sourceTabPosition = TabPosition.TOP;
    private DockTarget pendingDockTarget = null;

    // Plan C: mention pulse
    private final PulseEffect pulseEffect = new PulseEffect();
    private boolean isPulsing = false;
    private static final int NORMAL_HEADER_COLOR = 0xFF333333;
    private ChatNotifications.PulseListener pulseListener;

    /** Set while any floating window is being dragged; used to show empty side bars as drop targets. */
    public static boolean anyWindowDragging = false;

    public FloatingChatWindow(GuiParent<?> parent, ChatChannel channel) {
        super(parent);
        this.channel = channel;
        
        setTitle(channel.getName());

        // Wire close button — re-docks to original tab position
        getCloseButton().setOpaque(true);
        getCloseButton().onClick(() -> {
            ChatTabRegistry registry = ChatTabRegistry.get();
            registry.unmarkFloating(channel.getChannelId());
            switch (sourceTabPosition) {
                case TOP -> registry.registerTop(channel);
                case SIDE_LEFT -> registry.registerSideLeft(channel);
                case SIDE_RIGHT -> registry.registerSideRight(channel);
            }
            dispose();
            if (windowManager != null) {
                windowManager.removeWindow(this);
            }
        });

        // Add background fill to close button so it's visible (brightens on hover)
        GuiRectangle closeBg = new GuiRectangle(getCloseButton());
        Constraints.bind(closeBg, getCloseButton());
        closeBg.fill(() -> getCloseButton().isMouseOver() ? 0xFFCC4444 : 0xFF993333);

        // Minimize button — left of close button
        this.minimizeButton = new GuiButton(getHeaderBar());
        this.minimizeButton.setOpaque(true);
        this.minimizeButton.constrain(GeoParam.RIGHT, Constraint.relative(getCloseButton().get(GeoParam.LEFT), -2))
                           .constrain(GeoParam.TOP, Constraint.relative(getHeaderBar().get(GeoParam.TOP), 2))
                           .constrain(GeoParam.WIDTH, Constraint.literal(10))
                           .constrain(GeoParam.HEIGHT, Constraint.literal(10));
        // Add background fill to minimize button (brightens on hover)
        GuiRectangle minBg = new GuiRectangle(this.minimizeButton);
        Constraints.bind(minBg, this.minimizeButton);
        minBg.fill(() -> this.minimizeButton.isMouseOver() ? 0xFF777777 : 0xFF555555);
        GuiText minLabel = new GuiText(this.minimizeButton, Component.literal("_"));
        minLabel.constrain(GeoParam.LEFT, Constraint.match(this.minimizeButton.get(GeoParam.LEFT)))
                .constrain(GeoParam.TOP, Constraint.match(this.minimizeButton.get(GeoParam.TOP)))
                .constrain(GeoParam.RIGHT, Constraint.match(this.minimizeButton.get(GeoParam.RIGHT)))
                .constrain(GeoParam.BOTTOM, Constraint.match(this.minimizeButton.get(GeoParam.BOTTOM)));
        this.minimizeButton.setLabel(minLabel);
        this.minimizeButton.onClick(this::toggleMinimize);

        // Setup Sidebar
        this.sidebar = new GuiRectangle(getBackground());
        this.sidebar.constrain(GeoParam.RIGHT, Constraint.match(getBackground().get(GeoParam.RIGHT)))
                    .constrain(GeoParam.TOP, Constraint.match(getHeaderBar().get(GeoParam.BOTTOM)))
                    .constrain(GeoParam.BOTTOM, Constraint.match(getBackground().get(GeoParam.BOTTOM)))
                    .constrain(GeoParam.WIDTH, Constraint.literal(50))
                    .fill(0x55000000);

        // Setup Message Area
        this.messageArea = new GuiScrolling(getBackground());
        this.messageArea.constrain(GeoParam.LEFT, Constraint.match(getBackground().get(GeoParam.LEFT)))
                        .constrain(GeoParam.RIGHT, Constraint.match(sidebar.get(GeoParam.LEFT)))
                        .constrain(GeoParam.TOP, Constraint.match(getHeaderBar().get(GeoParam.BOTTOM)))
                        .constrain(GeoParam.BOTTOM, Constraint.relative(getBackground().get(GeoParam.BOTTOM), channel.canReply() ? -20 : 0));

        // Setup Input Field (only if channel supports replies)
        if (channel.canReply()) {
            this.inputField = new GuiTextField(getBackground());
            this.inputField.constrain(GeoParam.LEFT, Constraint.match(getBackground().get(GeoParam.LEFT)))
                           .constrain(GeoParam.RIGHT, Constraint.match(getBackground().get(GeoParam.RIGHT)))
                           .constrain(GeoParam.BOTTOM, Constraint.match(getBackground().get(GeoParam.BOTTOM)))
                           .constrain(GeoParam.HEIGHT, Constraint.literal(20));
                           
            this.inputField.setEnterPressed(() -> {
                String text = this.inputField.getValue();
                if (!text.isEmpty()) {
                    this.channel.submitMessage(text);
                    this.inputField.setValue("");
                }
            });
        } else {
            this.inputField = null;
        }

        // Hook up listeners
        this.channel.addMessageListener(this::onNewMessage);

        // Plan C: register pulse observer — observe ChatNotifications state changes
        this.pulseListener = (channelId, pulsing) -> {
            if (!channelId.equals(channel.getChannelId())) return;
            // If we're already the focused front window, dismiss the pulse immediately
            if (pulsing && isFocusedWindow()) {
                ChatNotifications.clearPulse(channelId);
                return;
            }
            isPulsing = pulsing;
            if (pulsing) {
                pulseEffect.reset();
                getHeaderBar().fill(() -> pulseEffect.getColor());
            } else {
                getHeaderBar().fill(NORMAL_HEADER_COLOR);
            }
        };
        ChatNotifications.addPulseListener(pulseListener);

        // Initial render
        refreshMembers();
        refreshMessages();
    }

    // --- Accessors ---

    public ChatChannel getChannel() { return channel; }

    public FloatingChatWindow setWindowManager(ChatWindowManager manager) {
        this.windowManager = manager;
        return this;
    }

    public boolean isMinimized() { return minimized; }

    /** Called after constraints are set to restore minimized state from previous session. */
    public void applyRestoredState() {
        if (Boolean.TRUE.equals(MINIMIZED_STATE.get(channel.getChannelId().toString())) && !minimized) {
            toggleMinimize();
        }
    }

    public SnapCorner getCurrentSnapCorner() { return currentSnapCorner; }

    public FloatingChatWindow setSourceTabPosition(TabPosition pos) {
        this.sourceTabPosition = pos;
        return this;
    }

    public TabPosition getSourceTabPosition() { return sourceTabPosition; }

    // --- Dock zone visual overlay (ForegroundRender) ---

    @Override
    public void renderInFront(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        if (!isDraggingPosition()) return;

        int sw = scaledScreenWidth();
        int sh = scaledScreenHeight();

        // Zone bounds: TOP strip at bottom, side strips on edges
        double topZoneY = sh - 28;   // where the top-bar sits (bottom=sh-14, height=14)
        double topZoneH = 14;
        double sideZoneTop = 0;
        double sideZoneBottom = topZoneY;
        double sideZoneH = sideZoneBottom - sideZoneTop;
        double leftZoneW = 16;
        double rightZoneX = sw - 18;
        double rightZoneW = 16;

        int dimColor    = 0x2200FFFF;  // very translucent cyan hint
        int activeColor = 0x8844FFAA;  // semi-opaque green highlight

        // Draw TOP zone
        render.rect(0, topZoneY, sw, topZoneH,
            pendingDockTarget == DockTarget.TOP ? activeColor : dimColor);

        // Draw SIDE_LEFT zone
        render.rect(0, sideZoneTop, leftZoneW, sideZoneH,
            pendingDockTarget == DockTarget.SIDE_LEFT ? activeColor : dimColor);

        // Draw SIDE_RIGHT zone
        render.rect(rightZoneX, sideZoneTop, rightZoneW, sideZoneH,
            pendingDockTarget == DockTarget.SIDE_RIGHT ? activeColor : dimColor);
    }

    // --- Cursor fix + pulse tick ---

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);
        if (getCloseButton().isMouseOver() || minimizeButton.isMouseOver()) {
            getModularGui().setCursor(null);
        }
        if (isPulsing) {
            pulseEffect.tick();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        // Clear pulse when user clicks this window
        ChatNotifications.clearPulse(channel.getChannelId());
        return result;
    }

    /**
     * Call when permanently closing this window (not just docking) to release listeners.
     */
    public void dispose() {
        MINIMIZED_STATE.remove(channel.getChannelId().toString());
        SAVED_POSITIONS.remove(channel.getChannelId().toString());
        ChatNotifications.removePulseListener(pulseListener);
        channel.removeMessageListener(this::onNewMessage);
    }

    // --- Minimize ---

    /** Height of the header bar (title + buttons); must match GuiWindow's headerBar HEIGHT constraint. */
    private static final int HEADER_HEIGHT = 14;

    private void toggleMinimize() {
        minimized = !minimized;
        MINIMIZED_STATE.put(channel.getChannelId().toString(), minimized);
        if (minimized) {
            restoreHeight = yMax - yMin; // raw fields — reliable here since not mid-constraint-solve
            sidebar.setEnabled(false);
            messageArea.setEnabled(false);
            if (inputField != null) inputField.setEnabled(false);
            // Write the raw field that drives contentElement, AND update the outer HEIGHT constraint
            // so that resetBounds() (called on onScreenInit) also reads back the correct value.
            yMax = yMin + HEADER_HEIGHT;
            super.constrain(GeoParam.HEIGHT, Constraint.literal(HEADER_HEIGHT)); // bypass resetBounds override
            setMinSize(Rectangle.create(0, 0, 50, HEADER_HEIGHT));
        } else {
            sidebar.setEnabled(true);
            messageArea.setEnabled(true);
            if (inputField != null) inputField.setEnabled(true);
            yMax = yMin + restoreHeight;
            super.constrain(GeoParam.HEIGHT, Constraint.literal(restoreHeight));
            setMinSize(Rectangle.create(0, 0, 50, 50));
        }
        ((GuiText) minimizeButton.getLabel()).setText(
            Component.literal(minimized ? "\u25A1" : "_")
        );
    }

    // --- Corner Snap ---

    @Override
    protected void onFinishMove(double mouseX, double mouseY) {
        super.onFinishMove(mouseX, mouseY);
        anyWindowDragging = false;
        pendingDockTarget = null;

        // Check for dock targets first (dock takes priority over corner snap)
        DockTarget target = detectDockTarget(mouseX, mouseY);
        if (target != null) {
            dockToTabBar(target);
            return;
        }

        // Corner snap detection
        double centerX = (xMin + xMax) / 2.0;
        double centerY = (yMin + yMax) / 2.0;
        int sw = scaledScreenWidth();
        int sh = scaledScreenHeight();

        SnapCorner corner = SnapCorner.detect(centerX, centerY, sw, sh, CORNER_SNAP_THRESHOLD);
        if (corner != null) {
            snapToCorner(corner);
        } else {
            currentSnapCorner = null;
        }
        savePosition(); // save after snap is applied
    }

    @Override
    protected void onManipulated(double mouseX, double mouseY) {
        super.onManipulated(mouseX, mouseY);
        anyWindowDragging = true;
        pendingDockTarget = detectDockTarget(mouseX, mouseY);
        savePosition(); // capture resize updates
    }

    private void savePosition() {
        SAVED_POSITIONS.put(channel.getChannelId().toString(),
            new double[]{xMin, yMin, xMax - xMin, minimized ? restoreHeight : yMax - yMin});
    }

    private void snapToCorner(SnapCorner corner) {
        currentSnapCorner = corner;
        int w = (int)(xMax - xMin);
        int h = (int)(yMax - yMin);
        int sw = scaledScreenWidth();
        int sh = scaledScreenHeight();
        int[] pos = corner.computePosition(w, h, sw, sh);
        xMin = pos[0];
        yMin = pos[1];
        xMax = xMin + w;
        yMax = yMin + h;
    }

    // --- Screen Resize Re-anchor ---

    @Override
    public void onScreenInit(Minecraft mc, Font font, int screenWidth, int screenHeight) {
        super.onScreenInit(mc, font, screenWidth, screenHeight);
        // Re-anchor corner-snapped windows after screen resize
        if (currentSnapCorner != null) {
            snapToCorner(currentSnapCorner);
        }
        // Restore minimized state now that constraints are fully resolved and yMin/yMax are valid
        boolean shouldBeMinimized = Boolean.TRUE.equals(MINIMIZED_STATE.get(channel.getChannelId().toString()));
        if (shouldBeMinimized && !minimized) {
            toggleMinimize();
        }
    }

    // --- Drag-to-Dock ---

    private enum DockTarget { TOP, SIDE_LEFT, SIDE_RIGHT }

    private DockTarget detectDockTarget(double mouseX, double mouseY) {
        // Check against actual tab bar elements
        ChatTabBarTop topBar = ChatTabInjection.getTopBar();
        ChatTabBarSide leftBar = ChatTabInjection.getSideBarLeft();
        ChatTabBarSide rightBar = ChatTabInjection.getSideBarRight();

        // Near top bar
        if (topBar != null) {
            double barTop = topBar.yMin();
            double barBottom = topBar.yMax();
            if (this.yMax > barTop - DOCK_THRESHOLD && this.yMax < barBottom + DOCK_THRESHOLD
                    && this.xMax >= topBar.xMin() && this.xMin <= topBar.xMax()) {
                return DockTarget.TOP;
            }
        }

        // Near left sidebar
        if (leftBar != null) {
            double lxMin = leftBar.xMin();
            double lxMax = leftBar.xMax();
            double lyMin = leftBar.yMin();
            double lyMax = leftBar.yMax();
            if (this.xMin >= lxMin - DOCK_THRESHOLD && this.xMin <= lxMax + DOCK_THRESHOLD
                    && this.yMax >= lyMin && this.yMin <= lyMax) {
                return DockTarget.SIDE_LEFT;
            }
        }

        // Near right sidebar
        if (rightBar != null) {
            double rxMin = rightBar.xMin();
            double rxMax = rightBar.xMax();
            double ryMin = rightBar.yMin();
            double ryMax = rightBar.yMax();
            if (this.xMax >= rxMin - DOCK_THRESHOLD && this.xMax <= rxMax + DOCK_THRESHOLD
                    && this.yMax >= ryMin && this.yMin <= ryMax) {
                return DockTarget.SIDE_RIGHT;
            }
        }

        // Fallback screen-edge detection (when bars don't exist)
        int sw = scaledScreenWidth();
        int sh = scaledScreenHeight();
        // Near bottom of screen → TOP
        if (this.yMax > sh - 14 - DOCK_THRESHOLD) {
            return DockTarget.TOP;
        }
        // Near left edge → SIDE_LEFT
        if (this.xMin < 20) {
            return DockTarget.SIDE_LEFT;
        }
        // Near right edge → SIDE_RIGHT
        if (this.xMax > sw - 20) {
            return DockTarget.SIDE_RIGHT;
        }
        return null;
    }

    private void dockToTabBar(DockTarget target) {
        ChatTabRegistry registry = ChatTabRegistry.get();
        registry.unmarkFloating(channel.getChannelId());
        switch (target) {
            case TOP -> registry.registerTop(channel);
            case SIDE_LEFT -> registry.registerSideLeft(channel);
            case SIDE_RIGHT -> registry.registerSideRight(channel);
        }
        dispose();
        if (windowManager != null) {
            windowManager.removeWindow(this);
        }
    }

    private boolean isFocusedWindow() {
        if (inputField != null && inputField.isFocused()) return true;
        return windowManager != null && windowManager.isFrontWindow(this);
    }

    // --- Persistence ---

    public void saveToLayout(ChatWindowLayout layout) {
        layout.setX((int) xMin);
        layout.setY((int) yMin);
        layout.setWidth((int)(xMax - xMin));
        layout.setHeight((int)(yMax - yMin));
        layout.setMinimized(minimized);
        layout.setSnapCorner(currentSnapCorner);
        layout.setDisplayMode(ChatWindowLayout.DisplayMode.FLOATING);
    }

    public void loadFromLayout(ChatWindowLayout layout) {
        xMin = layout.getX();
        yMin = layout.getY();
        xMax = xMin + layout.getWidth();
        yMax = yMin + layout.getHeight();
        restoreHeight = layout.getHeight();

        if (layout.getSnapCorner() != null) {
            snapToCorner(layout.getSnapCorner());
        }
        if (layout.isMinimized() && !minimized) {
            toggleMinimize();
        }
    }

    // --- Message/Member Rendering ---

    private void onNewMessage(RichChatMessage message) {
        refreshMessages();
    }

    private void refreshMembers() {
        new java.util.ArrayList<>(sidebar.getChildren()).forEach(sidebar::removeChild);
        
        GuiText headerText = new GuiText(sidebar);
        headerText.setText(Component.literal("Members"));
        headerText.constrain(GeoParam.LEFT, Constraint.relative(sidebar.get(GeoParam.LEFT), 2))
                .constrain(GeoParam.TOP, Constraint.relative(sidebar.get(GeoParam.TOP), 2));
        
        GuiElement<?> lastElement = headerText;
        
        for (ChatMember member : channel.getMembers()) {
            GuiText nameText = new GuiText(sidebar);
            nameText.setText(member.displayName());
            nameText.constrain(GeoParam.LEFT, Constraint.relative(sidebar.get(GeoParam.LEFT), 2))
                    .constrain(GeoParam.TOP, Constraint.relative(lastElement.get(GeoParam.BOTTOM), 2))
                    .constrain(GeoParam.RIGHT, Constraint.relative(sidebar.get(GeoParam.RIGHT), -2))
                    .setWrap(true).setAlignment(net.creeperhost.polylib.client.modulargui.lib.geometry.Align.MIN).autoHeight();
            
            lastElement = nameText;
        }
    }

    private void refreshMessages() {
        new java.util.ArrayList<>(messageArea.getContentElement().getChildren()).forEach(messageArea.getContentElement()::removeChild);
        
        messageArea.getContentElement().constrain(GeoParam.WIDTH, Constraint.match(messageArea.get(GeoParam.WIDTH)));
        
        GuiElement<?> lastElement = null;
        for (RichChatMessage msg : channel.getMessages()) {
            GuiElement<?> container = new GuiElement<>(messageArea.getContentElement());
            container.constrain(GeoParam.LEFT, Constraint.match(messageArea.getContentElement().get(GeoParam.LEFT)));
            container.constrain(GeoParam.RIGHT, Constraint.match(messageArea.getContentElement().get(GeoParam.RIGHT)));
            
            if (lastElement == null) {
                container.constrain(GeoParam.TOP, Constraint.match(messageArea.getContentElement().get(GeoParam.TOP)));
            } else {
                container.constrain(GeoParam.TOP, Constraint.match(lastElement.get(GeoParam.BOTTOM)));
            }

            double currentX = 2;
            
            if (msg.senderIcon() != null) {
                GuiTexture icon = new GuiTexture(container, Material.fromRawTexture(msg.senderIcon()));
                icon.constrain(GeoParam.LEFT, Constraint.relative(container.get(GeoParam.LEFT), currentX))
                    .constrain(GeoParam.TOP, Constraint.relative(container.get(GeoParam.TOP), 2))
                    .constrain(GeoParam.WIDTH, Constraint.literal(8))
                    .constrain(GeoParam.HEIGHT, Constraint.literal(8));
                currentX += 10;
            }

            GuiText msgText = new GuiText(container);
            msgText.setText(Component.literal("<")
                .append(msg.senderName())
                .append("> ")
                .append(msg.content()));
                
            msgText.constrain(GeoParam.LEFT, Constraint.relative(container.get(GeoParam.LEFT), currentX))
                   .constrain(GeoParam.TOP, Constraint.match(container.get(GeoParam.TOP)))
                   .constrain(GeoParam.RIGHT, Constraint.relative(container.get(GeoParam.RIGHT), -2))
                   .setWrap(true).setAlignment(net.creeperhost.polylib.client.modulargui.lib.geometry.Align.MIN).autoHeight();
            
            container.constrain(GeoParam.HEIGHT, Constraint.relative(msgText.get(GeoParam.HEIGHT), 2));
            
            lastElement = container;
        }
    }
}
