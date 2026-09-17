package net.creeperhost.polylib.config;

import blue.endless.jankson.Comment;
import blue.endless.jankson.annotation.SerializedName;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** A detached edit session. Only changed leaves are written back to the original objects. */
final class ConfigEditorSession
{
    private final ConfigBuilder builder;
    private final ConfigData original;
    private final List<Entry> entries = new ArrayList<>();

    ConfigEditorSession(ConfigBuilder builder, ConfigData defaults)
    {
        this.builder = builder;
        this.original = Objects.requireNonNull(builder.getConfigData(), "Config must be loaded before opening its editor");
        if (defaults.getClass() != original.getClass()) {
            throw new IllegalArgumentException("Editor defaults must have the same class as the loaded config");
        }
        visit(original, defaults, List.of(), "", () -> true,
                Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    List<Entry> entries()
    {
        return Collections.unmodifiableList(entries);
    }

    void save()
    {
        if (entries.stream().noneMatch(Entry::changed)) return;
        if (builder.getConfigData() != original) {
            throw new IllegalStateException("Config was reloaded while editing; reopen its editor");
        }
        // Check every destination before applying anything (nested objects can also be replaced).
        for (Entry entry : entries) {
            if (entry.changed() && (!entry.attached.get() || !Objects.equals(entry.read.get(), entry.initial))) {
                throw new IllegalStateException("Config entry changed while editing: " + entry.label());
            }
        }
        List<Entry> applied = new ArrayList<>();
        try {
            for (Entry entry : entries) {
                if (entry.changed()) {
                    entry.write.accept(entry.value);
                    applied.add(entry);
                }
            }
            builder.saveOrThrow();
        } catch (IOException | RuntimeException e) {
            for (Entry entry : applied) entry.write.accept(entry.initial);
            throw new IllegalStateException("Could not save " + builder.getConfigName(), e);
        }
        for (Entry entry : entries) entry.initial = entry.value;
    }

    private void visit(Object owner, Object defaults, List<String> path, String comment,
                       Supplier<Boolean> attached, Set<Object> ancestors)
    {
        if (!ancestors.add(owner)) {
            readOnly(path, owner, comment + " (cyclic reference)");
            return;
        }
        try {
            if (owner instanceof Map<?, ?> map) {
                if (map.isEmpty()) readOnly(path, owner, comment);
                for (var item : map.entrySet()) {
                    if (!(item.getKey() instanceof String key)) {
                        readOnly(path, owner, comment);
                        return;
                    }
                    Object value = item.getValue();
                    Object defaultValue = defaults instanceof Map<?, ?> defaultMap ? defaultMap.get(key) : null;
                    List<String> childPath = append(path, key);
                    // Arbitrary Map implementations may be immutable. Display them without attempting writes.
                    boolean mutable = map.getClass() == java.util.HashMap.class || map.getClass() == LinkedHashMap.class
                            || map.getClass() == java.util.TreeMap.class;
                    @SuppressWarnings("unchecked") Map<String, Object> target = (Map<String, Object>) map;
                    add(childPath, value, defaultValue, comment, mutable,
                            () -> target.get(key), v -> target.put(key, v),
                            () -> attached.get() && target.containsKey(key), ancestors);
                }
                return;
            }

            Map<String, Field> fields = new LinkedHashMap<>();
            for (Field field : owner.getClass().getFields()) fields.put(field.getName(), field);
            for (Field field : owner.getClass().getDeclaredFields()) fields.putIfAbsent(field.getName(), field);
            if (fields.isEmpty()) readOnly(path, owner, comment);
            for (Field field : fields.values()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic()) continue;
                SerializedName name = field.getAnnotation(SerializedName.class);
                List<String> childPath = append(path, name == null ? field.getName() : name.value());
                Comment annotation = field.getAnnotation(Comment.class);
                String description = annotation == null ? "" : annotation.value();
                if (!field.trySetAccessible()) {
                    readOnly(childPath, "Inaccessible field", description);
                    continue;
                }
                Object value = read(field, owner);
                Object defaultValue = field.getDeclaringClass().isInstance(defaults) ? read(field, defaults) : null;
                add(childPath, value, defaultValue, description, !Modifier.isFinal(modifiers),
                        () -> read(field, owner), v -> write(field, owner, v), attached, ancestors);
            }
        } finally {
            ancestors.remove(owner);
        }
    }

    private void add(List<String> path, Object value, Object defaultValue, String comment, boolean writable,
                     Supplier<Object> read, Consumer<Object> write, Supplier<Boolean> attached, Set<Object> ancestors)
    {
        if (writable && scalar(value)) {
            entries.add(new Entry(path, value, defaultValue, comment, read, write, attached));
        } else if (value != null && (value instanceof Map<?, ?> || !value.getClass().getPackageName().startsWith("java."))
                && !value.getClass().isArray() && !(value instanceof Enum<?>)) {
            visit(value, defaultValue, path, comment, () -> attached.get() && read.get() == value, ancestors);
        } else {
            readOnly(path, value, comment);
        }
    }

    private void readOnly(List<String> path, Object value, String comment)
    {
        entries.add(new Entry(path, value, null, comment, () -> value, null, () -> true));
    }

    private static boolean scalar(Object value)
    {
        return value instanceof Boolean || value instanceof String || value instanceof Byte || value instanceof Short
                || value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double
                || value instanceof Enum<?>;
    }

    private static List<String> append(List<String> path, String key)
    {
        List<String> result = new ArrayList<>(path);
        result.add(key);
        return List.copyOf(result);
    }

    private static Object read(Field field, Object owner)
    {
        try {
            return field.get(owner);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot read config field " + field, e);
        }
    }

    private static void write(Field field, Object owner, Object value)
    {
        try {
            field.set(owner, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot write config field " + field, e);
        }
    }

    static final class Entry
    {
        final List<String> path;
        final Object defaultValue;
        final String comment;
        final Supplier<Object> read;
        final Consumer<Object> write;
        final Supplier<Boolean> attached;
        Object initial;
        Object value;

        Entry(List<String> path, Object value, Object defaultValue, String comment,
              Supplier<Object> read, Consumer<Object> write, Supplier<Boolean> attached)
        {
            this.path = path;
            this.initial = this.value = value;
            this.defaultValue = defaultValue;
            this.comment = comment;
            this.read = read;
            this.write = write;
            this.attached = attached;
        }

        String label() { return String.join(" / ", path); }
        boolean editable() { return write != null; }
        boolean changed() { return editable() && !Objects.equals(value, initial); }
        boolean hasDefault() {
            if (!editable() || defaultValue == null) return false;
            if (value instanceof Enum<?> current && defaultValue instanceof Enum<?> fallback) {
                return current.getDeclaringClass() == fallback.getDeclaringClass();
            }
            return defaultValue.getClass() == value.getClass();
        }
        boolean nonDefault() { return hasDefault() && !Objects.equals(value, defaultValue); }
    }
}
