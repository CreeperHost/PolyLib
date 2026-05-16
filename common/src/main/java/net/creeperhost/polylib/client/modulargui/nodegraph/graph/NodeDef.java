package net.creeperhost.polylib.client.modulargui.nodegraph.graph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

import java.util.UUID;

/**
 * Mutable description of one node in a {@link NodeGraph}.
 *
 * <p>Each node has a permanent {@link #id} (UUID), a {@link #typeId} that maps to
 * a registered {@link INodeType}, a canvas position ({@link #x}, {@link #y}), and
 * a type-specific settings blob ({@link #settings}).</p>
 *
 * <p>Instances are owned by a {@link NodeGraph}.  Do not share them across graphs.</p>
 */
public final class NodeDef {

    private final UUID id;
    private final Identifier typeId;
    private int x;
    private int y;
    private boolean collapsed;
    private CompoundTag settings;

    public NodeDef(UUID id, Identifier typeId, int x, int y, boolean collapsed, CompoundTag settings) {
        this.id       = id;
        this.typeId   = typeId;
        this.x        = x;
        this.y        = y;
        this.collapsed = collapsed;
        this.settings = settings != null ? settings : new CompoundTag();
    }

    public NodeDef(UUID id, Identifier typeId, int x, int y, CompoundTag settings) {
        this(id, typeId, x, y, false, settings);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public UUID id()             { return id; }
    public Identifier typeId()   { return typeId; }
    public int x()               { return x; }
    public int y()               { return y; }
    public boolean isCollapsed() { return collapsed; }
    public CompoundTag settings(){ return settings; }

    // ── Mutators ──────────────────────────────────────────────────────────────

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public void setSettings(CompoundTag settings) {
        this.settings = settings != null ? settings : new CompoundTag();
    }

    // ── Serialization ─────────────────────────────────────────────────────────

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id",   id.toString());
        tag.putString("type", typeId.toString());
        tag.putInt("x", x);
        tag.putInt("y", y);
        if (collapsed) {
            tag.putBoolean("collapsed", true);
        }
        if (!settings.isEmpty()) {
            tag.put("settings", settings.copy());
        }
        return tag;
    }

    public static NodeDef fromNbt(CompoundTag tag) {
        try {
            UUID id       = UUID.fromString(tag.getString("id").orElse(""));
            Identifier rl = Identifier.parse(tag.getString("type").orElse("polylib:unknown"));
            int x         = tag.getInt("x").orElse(0);
            int y         = tag.getInt("y").orElse(0);
            boolean col   = tag.getBoolean("collapsed").orElse(false);
            net.minecraft.nbt.Tag settingsTag = tag.get("settings");
            CompoundTag settings = (settingsTag instanceof CompoundTag ct) ? ct : new CompoundTag();
            return new NodeDef(id, rl, x, y, col, settings);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "NodeDef{id=" + id + ", type=" + typeId + ", x=" + x + ", y=" + y + "}";
    }
}
