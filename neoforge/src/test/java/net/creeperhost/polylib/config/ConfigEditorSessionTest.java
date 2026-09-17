package net.creeperhost.polylib.config;

import blue.endless.jankson.Comment;
import blue.endless.jankson.annotation.SerializedName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigEditorSessionTest
{
    @TempDir Path directory;

    public enum Mode { FIRST, SECOND }

    public static class Nested {
        public int count = 3;
    }

    public static class Example extends ConfigData {
        @Comment("Whether the feature is enabled") public boolean enabled = true;
        public String text = "default";
        public Mode mode = Mode.FIRST;
        public byte small = 1;
        public short medium = 2;
        public long large = 3;
        public float fraction = 1.5F;
        public double precise = 2.5;
        @SerializedName("renamed") private int internal = 7;
        public Nested nested = new Nested();
        public Map<String, Boolean> flags = new LinkedHashMap<>(Map.of("mod.id", true));
        public List<String> list = new ArrayList<>();
        public String nullable = null;
        public final int constant = 99;
        public transient int ignored = 42;
        public static int shared = 42;
    }

    private ConfigBuilder builder(Example data) {
        return new ConfigBuilder("example", directory.resolve("example.json5"), data);
    }

    private ConfigEditorSession.Entry entry(ConfigEditorSession session, String... path) {
        return session.entries().stream().filter(e -> e.path.equals(List.of(path))).findFirst().orElseThrow();
    }

    @Test void editsAreDetachedAndSavePreservesObjectIdentityAndJson5() throws Exception {
        Example data = new Example();
        data.list.addAll(List.of("keep", "these"));
        var nested = data.nested;
        var flags = data.flags;
        var list = data.list;
        ConfigBuilder builder = builder(data);
        var session = new ConfigEditorSession(builder, new Example());
        entry(session, "enabled").value = false;
        entry(session, "nested", "count").value = 12;
        entry(session, "flags", "mod.id").value = false;
        entry(session, "text").value = "Hello, 世界";
        entry(session, "mode").value = Mode.SECOND;
        entry(session, "renamed").value = 8;
        assertTrue(data.enabled);
        assertEquals(3, data.nested.count);
        session.save();
        assertSame(data, builder.getConfigData());
        assertSame(nested, data.nested);
        assertSame(flags, data.flags);
        assertSame(list, data.list);
        assertFalse(data.enabled);
        assertFalse(data.flags.get("mod.id"));
        assertEquals(12, nested.count);
        ConfigBuilder reloaded = builder(new Example());
        Example persisted = (Example) reloaded.getConfigData();
        assertEquals("Hello, 世界", persisted.text);
        assertEquals(Mode.SECOND, persisted.mode);
        assertEquals(8, persisted.internal);
        assertEquals(list, persisted.list);
        assertTrue(Files.readString(builder.getConfigPath()).contains("Whether the feature is enabled"));
        try (var paths = Files.list(directory)) {
            assertEquals(List.of("example.json5"), paths.map(p -> p.getFileName().toString()).toList());
        }
    }

    @Test void defaultsComeFromDefaultsObjectAndUnsupportedEntriesAreReadOnly() {
        Example data = new Example();
        data.nested.count = 30;
        var session = new ConfigEditorSession(builder(data), new Example());
        assertEquals(3, entry(session, "nested", "count").defaultValue);
        assertTrue(entry(session, "nested", "count").nonDefault());
        assertFalse(entry(session, "list").editable());
        assertFalse(entry(session, "nullable").editable());
        assertFalse(entry(session, "constant").editable());
        assertTrue(session.entries().stream().noneMatch(e -> e.path.contains("ignored") || e.path.contains("shared")));
    }

    @Test void unrelatedChangesDuringEditingArePreserved() {
        Example data = new Example();
        var session = new ConfigEditorSession(builder(data), new Example());
        entry(session, "enabled").value = false;
        data.text = "changed elsewhere";
        session.save();
        assertEquals("changed elsewhere", data.text);
        assertFalse(data.enabled);
    }

    @Test void reloadRejectsStaleEdits() {
        Example data = new Example();
        ConfigBuilder builder = builder(data);
        var session = new ConfigEditorSession(builder, new Example());
        entry(session, "enabled").value = false;
        builder.load();
        assertThrows(IllegalStateException.class, session::save);
        assertTrue(((Example) builder.getConfigData()).enabled);
    }

    @Test void replacedNestedObjectsRejectAllEditsBeforeMutation() {
        Example data = new Example();
        var session = new ConfigEditorSession(builder(data), new Example());
        entry(session, "enabled").value = false;
        entry(session, "nested", "count").value = 12;
        data.nested = new Nested();
        assertThrows(IllegalStateException.class, session::save);
        assertTrue(data.enabled);
        assertEquals(3, data.nested.count);
    }

    @Test void changesToTheSameLeafRejectEdits() {
        Example data = new Example();
        var session = new ConfigEditorSession(builder(data), new Example());
        entry(session, "nested", "count").value = 12;
        data.nested.count = 15;
        assertThrows(IllegalStateException.class, session::save);
        assertEquals(15, data.nested.count);
    }

    @Test void failedSaveRollsBackObjectsAndKeepsPendingEdits() throws Exception {
        Example data = new Example();
        ConfigBuilder builder = builder(data);
        var session = new ConfigEditorSession(builder, new Example());
        entry(session, "enabled").value = false;
        Files.delete(builder.getConfigPath());
        Files.createDirectory(builder.getConfigPath());
        Files.writeString(builder.getConfigPath().resolve("blocker"), "keep");
        assertThrows(IllegalStateException.class, session::save);
        assertTrue(data.enabled);
        assertTrue(entry(session, "enabled").changed());
    }

    @Test void unchangedOrUndoneSessionDoesNotWriteFile() throws Exception {
        Example data = new Example();
        ConfigBuilder builder = builder(data);
        var session = new ConfigEditorSession(builder, new Example());
        String original = Files.readString(builder.getConfigPath());
        Files.writeString(builder.getConfigPath(), original + "\n// external comment\n");
        entry(session, "enabled").value = false;
        entry(session, "enabled").value = true;
        session.save();
        assertTrue(Files.readString(builder.getConfigPath()).endsWith("// external comment\n"));
    }

    @Test void immutableMapValuesStayReadOnlyAndMissingDefaultsAreNotInvented() {
        Example data = new Example();
        data.flags = Map.of("custom", true);
        var session = new ConfigEditorSession(builder(data), new Example());
        assertFalse(entry(session, "flags", "custom").editable());
        data.flags = new LinkedHashMap<>(data.flags);
        ConfigBuilder mutableBuilder = new ConfigBuilder("mutable", directory.resolve("mutable.json5"), data);
        session = new ConfigEditorSession(mutableBuilder, new Example());
        assertTrue(entry(session, "flags", "custom").editable());
        assertFalse(entry(session, "flags", "custom").hasDefault());
    }
}
