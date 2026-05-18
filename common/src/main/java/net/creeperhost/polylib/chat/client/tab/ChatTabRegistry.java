package net.creeperhost.polylib.chat.client.tab;

import net.creeperhost.polylib.chat.ChatChannel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class ChatTabRegistry {
    /** Sentinel identifier used to track the Vanilla tab's position in order lists. */
    public static final Identifier VANILLA_TAB_ID = Identifier.fromNamespaceAndPath("polylib", "vanilla_tab");
    /** Sentinel identifier used to track the All tab's position in order lists. */
    public static final Identifier ALL_TAB_ID = Identifier.fromNamespaceAndPath("polylib", "all_tab");

    private static final ChatTabRegistry INSTANCE = new ChatTabRegistry();

    private final Map<Identifier, TabEntry> entries = new ConcurrentHashMap<>();
    private final Map<Identifier, NotificationTabEntry> notifEntries = new ConcurrentHashMap<>();
    private final Set<Identifier> floatingChannels = ConcurrentHashMap.newKeySet();
    private final List<Identifier> topOrder = new ArrayList<>();
    private final List<Identifier> sideLeftOrder = new ArrayList<>();
    private final List<Identifier> sideRightOrder = new ArrayList<>();
    private ChatTab activeTab = new VanillaTab();
    private boolean showVanillaTab = true;
    private boolean showAllTab = true;

    public record TabEntry(ChatChannel channel, TabPosition position) {}
    public record NotificationTabEntry(Identifier windowId, Component title, TabPosition position) {}

    private ChatTabRegistry() {
        // Vanilla and All tabs default to TOP, prepended to the order list
        topOrder.add(VANILLA_TAB_ID);
        topOrder.add(ALL_TAB_ID);
    }

    public static ChatTabRegistry get() { return INSTANCE; }

    // --- Registration ---

    public void registerTop(ChatChannel channel) {
        Identifier id = channel.getChannelId();
        entries.put(id, new TabEntry(channel, TabPosition.TOP));
        floatingChannels.remove(id);
        sideLeftOrder.remove(id);
        sideRightOrder.remove(id);
        if (!topOrder.contains(id)) topOrder.add(id);
    }

    public void registerSideLeft(ChatChannel channel) {
        Identifier id = channel.getChannelId();
        entries.put(id, new TabEntry(channel, TabPosition.SIDE_LEFT));
        floatingChannels.remove(id);
        topOrder.remove(id);
        sideRightOrder.remove(id);
        if (!sideLeftOrder.contains(id)) sideLeftOrder.add(id);
    }

    public void registerSideRight(ChatChannel channel) {
        Identifier id = channel.getChannelId();
        entries.put(id, new TabEntry(channel, TabPosition.SIDE_RIGHT));
        floatingChannels.remove(id);
        topOrder.remove(id);
        sideLeftOrder.remove(id);
        if (!sideRightOrder.contains(id)) sideRightOrder.add(id);
    }

    public void unregister(Identifier channelId) {
        entries.remove(channelId);
        notifEntries.remove(channelId);
        topOrder.remove(channelId);
        sideLeftOrder.remove(channelId);
        sideRightOrder.remove(channelId);
        if (activeTab instanceof ChannelTab ct && ct.channel().getChannelId().equals(channelId)) {
            activeTab = new VanillaTab();
        }
        if (activeTab instanceof NotificationTab nt && nt.windowId().equals(channelId)) {
            activeTab = new VanillaTab();
        }
    }

    // --- Notification Tab Registration ---

    public void registerNotificationTop(Identifier windowId, Component title) {
        notifEntries.put(windowId, new NotificationTabEntry(windowId, title, TabPosition.TOP));
        sideLeftOrder.remove(windowId);
        sideRightOrder.remove(windowId);
        if (!topOrder.contains(windowId)) topOrder.add(windowId);
    }

    public void registerNotificationSideLeft(Identifier windowId, Component title) {
        notifEntries.put(windowId, new NotificationTabEntry(windowId, title, TabPosition.SIDE_LEFT));
        topOrder.remove(windowId);
        sideRightOrder.remove(windowId);
        if (!sideLeftOrder.contains(windowId)) sideLeftOrder.add(windowId);
    }

    public void registerNotificationSideRight(Identifier windowId, Component title) {
        notifEntries.put(windowId, new NotificationTabEntry(windowId, title, TabPosition.SIDE_RIGHT));
        topOrder.remove(windowId);
        sideLeftOrder.remove(windowId);
        if (!sideRightOrder.contains(windowId)) sideRightOrder.add(windowId);
    }

    @org.jetbrains.annotations.Nullable
    public NotificationTabEntry getNotificationEntry(Identifier windowId) {
        return notifEntries.get(windowId);
    }

    // --- Active Tab ---

    public ChatTab getActiveTab() { return activeTab; }

    public void setActiveTab(ChatTab tab) { this.activeTab = tab; }

    // --- Built-in tab positions ---

    public TabPosition getVanillaTabPosition() {
        if (sideLeftOrder.contains(VANILLA_TAB_ID)) return TabPosition.SIDE_LEFT;
        if (sideRightOrder.contains(VANILLA_TAB_ID)) return TabPosition.SIDE_RIGHT;
        return TabPosition.TOP;
    }

    public void setVanillaTabPosition(TabPosition pos) {
        topOrder.remove(VANILLA_TAB_ID);
        sideLeftOrder.remove(VANILLA_TAB_ID);
        sideRightOrder.remove(VANILLA_TAB_ID);
        getOrderList(pos).add(0, VANILLA_TAB_ID);
    }

    public TabPosition getAllTabPosition() {
        if (sideLeftOrder.contains(ALL_TAB_ID)) return TabPosition.SIDE_LEFT;
        if (sideRightOrder.contains(ALL_TAB_ID)) return TabPosition.SIDE_RIGHT;
        return TabPosition.TOP;
    }

    public void setAllTabPosition(TabPosition pos) {
        topOrder.remove(ALL_TAB_ID);
        sideLeftOrder.remove(ALL_TAB_ID);
        sideRightOrder.remove(ALL_TAB_ID);
        getOrderList(pos).add(ALL_TAB_ID);
    }

    public boolean shouldShowVanillaTab() { return showVanillaTab && hasAnyChannel(); }
    public void setShowVanillaTab(boolean show) { this.showVanillaTab = show; }

    public boolean shouldShowAllTab() { return showAllTab && (entries.size() + floatingChannels.size()) >= 2; }
    public void setShowAllTab(boolean show) { this.showAllTab = show; }

    // --- Queries ---

    public boolean hasAnyChannel() { return !entries.isEmpty() || !floatingChannels.isEmpty(); }

    /** Track a channel as floating (torn off into a window). */
    public void markFloating(Identifier channelId) { floatingChannels.add(channelId); }

    /** Remove floating tracking (when window is closed/destroyed). */
    public void unmarkFloating(Identifier channelId) { floatingChannels.remove(channelId); }

    /** Returns a snapshot of all currently-floating channel IDs. */
    public Set<Identifier> getFloatingChannels() { return Collections.unmodifiableSet(floatingChannels); }

    public List<TabEntry> getTopEntries() {
        List<TabEntry> result = new ArrayList<>();
        for (Identifier id : topOrder) {
            TabEntry e = entries.get(id);
            if (e != null && e.position() == TabPosition.TOP) result.add(e);
        }
        return result;
    }

    public List<TabEntry> getSideLeftEntries() {
        List<TabEntry> result = new ArrayList<>();
        for (Identifier id : sideLeftOrder) {
            TabEntry e = entries.get(id);
            if (e != null && e.position() == TabPosition.SIDE_LEFT) result.add(e);
        }
        return result;
    }

    public List<TabEntry> getSideRightEntries() {
        List<TabEntry> result = new ArrayList<>();
        for (Identifier id : sideRightOrder) {
            TabEntry e = entries.get(id);
            if (e != null && e.position() == TabPosition.SIDE_RIGHT) result.add(e);
        }
        return result;
    }

    public boolean hasAnySideEntries() {
        return entries.values().stream()
            .anyMatch(e -> e.position() == TabPosition.SIDE_LEFT || e.position() == TabPosition.SIDE_RIGHT);
    }

    @Nullable
    public TabEntry getEntry(Identifier channelId) {
        return entries.get(channelId);
    }

    @Nullable
    public TabPosition getTabPosition(Identifier channelId) {
        TabEntry e = entries.get(channelId);
        return e != null ? e.position() : null;
    }

    public Collection<TabEntry> allEntries() {
        return Collections.unmodifiableCollection(entries.values());
    }

    // --- Reorder ---

    /** Swap two channel tabs (by channel ID) within the same bar. */
    public void swapOrder(Identifier a, Identifier b) {
        // Allow swapping sentinel IDs (vanilla/all) with channel IDs or each other
        TabPosition posA = getPositionOfId(a);
        TabPosition posB = getPositionOfId(b);
        if (posA == null || posB == null || posA != posB) return;
        List<Identifier> order = getOrderList(posA);
        int ia = order.indexOf(a);
        int ib = order.indexOf(b);
        if (ia >= 0 && ib >= 0) {
            Collections.swap(order, ia, ib);
        }
    }

    /** Returns which bar position a given ID (channel or sentinel) lives in. */
    @Nullable
    private TabPosition getPositionOfId(Identifier id) {
        if (id.equals(VANILLA_TAB_ID)) return getVanillaTabPosition();
        if (id.equals(ALL_TAB_ID)) return getAllTabPosition();
        TabEntry e = entries.get(id);
        return e != null ? e.position() : null;
    }

    public List<Identifier> getOrderList(TabPosition pos) {
        return switch (pos) {
            case TOP -> topOrder;
            case SIDE_LEFT -> sideLeftOrder;
            case SIDE_RIGHT -> sideRightOrder;
        };
    }

    // --- Tear-off callback ---

    public record TearOffEvent(ChatChannel channel, double x, double y, TabPosition sourcePosition) {}

    private Consumer<TearOffEvent> tearOffCallback;

    public void setTearOffCallback(Consumer<TearOffEvent> callback) {
        this.tearOffCallback = callback;
    }

    public boolean hasTearOffCallback() {
        return tearOffCallback != null;
    }

    void fireTearOffCallback(ChatChannel channel, double x, double y, TabPosition sourcePosition) {
        if (tearOffCallback != null) {
            tearOffCallback.accept(new TearOffEvent(channel, x, y, sourcePosition));
        }
    }
}
