package net.creeperhost.polylib.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.creeperhost.polylib.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.ConfigurationScreen.ConfigurationSectionScreen;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * NeoForge's native controls backed by a detached PolyLib edit session.
 * No config is registered with FML: JSON5 remains owned by {@link ConfigBuilder}.
 */
final class NeoForgeConfigScreen extends ConfigurationSectionScreen
{
    private final ConfigEditorSession session;

    NeoForgeConfigScreen(String modId, Screen parent, ConfigBuilder builder, ConfigData defaults)
    {
        this(modId, parent, builder.getConfigName(), new ConfigEditorSession(builder, defaults));
    }

    private NeoForgeConfigScreen(String modId, Screen parent, String title, ConfigEditorSession session)
    {
        super(context(modId, parent, session), Component.translatableWithFallback(modId + ".configuration.title", title));
        this.session = session;
    }

    private static Context context(String modId, Screen parent, ConfigEditorSession session)
    {
        ModConfigSpec.Builder metadata = new ModConfigSpec.Builder();
        for (int i = 0; i < session.entries().size(); i++) {
            ConfigEditorSession.Entry entry = session.entries().get(i);
            Object sample = entry.hasDefault() ? entry.defaultValue : entry.value;
            if (!entry.comment.isBlank()) metadata.comment(entry.comment);
            if (sample instanceof Enum<?> enumValue) {
                defineEnum(metadata, key(i), enumValue);
            } else {
                metadata.define(List.of(key(i)), () -> sample == null ? "" : sample,
                        value -> value != null && (sample == null || sample.getClass().isInstance(value)),
                        sample == null ? String.class : sample.getClass());
            }
        }
        ModConfigSpec spec = metadata.build();
        // Context is NeoForge's internal bridge to its supported widget extension hooks.
        // There are deliberately no ConfigValue entries: synthetic controls use our own storage.
        return new Context(modId, parent, null, spec, Set.of(), spec.getSpec().valueMap(), List.of(), (c, k, e) -> e);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void defineEnum(ModConfigSpec.Builder builder, String key, Enum value)
    {
        builder.defineEnum(key, value);
    }

    private static String key(int index) { return "entry" + index; }

    private ConfigEditorSession.Entry entry(String key)
    {
        return session.entries().get(Integer.parseInt(key.substring("entry".length())));
    }

    @Override
    protected String getTranslationKey(String key)
    {
        return context.modId() + ".configuration." + String.join(".", entry(key).path);
    }

    @Override
    protected MutableComponent getTranslationComponent(String key)
    {
        return Component.translatableWithFallback(getTranslationKey(key), entry(key).label());
    }

    @Override
    protected Collection<? extends Element> createSyntheticValues()
    {
        List<Element> result = new ArrayList<>();
        for (int i = 0; i < session.entries().size(); i++) result.add(createEntry(key(i), session.entries().get(i)));
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Element createEntry(String key, ConfigEditorSession.Entry entry)
    {
        var spec = getValueSpec(key);
        if (entry.editable()) {
            switch (entry.value) {
                case Boolean ignored: return createBooleanValue(key, spec, () -> (Boolean) entry.value, v -> entry.value = v);
                case String ignored: return createStringValue(key, spec::test, () -> (String) entry.value, v -> entry.value = v);
                case Byte ignored: return createNumberBox(key, spec, () -> (Byte) entry.value, v -> entry.value = v, null, Byte::decode, (byte) 0);
                case Short ignored: return createNumberBox(key, spec, () -> (Short) entry.value, v -> entry.value = v, null, Short::decode, (short) 0);
                case Integer ignored: return createIntegerValue(key, spec, () -> (Integer) entry.value, v -> entry.value = v);
                case Long ignored: return createLongValue(key, spec, () -> (Long) entry.value, v -> entry.value = v);
                case Float ignored: return createNumberBox(key, spec, () -> (Float) entry.value, v -> entry.value = v, Float::isFinite, Float::parseFloat, 0F);
                case Double ignored: return createNumberBox(key, spec, () -> (Double) entry.value, v -> entry.value = v, Double::isFinite, Double::parseDouble, 0D);
                case Enum ignored: return createEnumValue(key, spec, () -> (Enum) entry.value, v -> entry.value = v);
                default: break;
            }
        }
        StringWidget label = new StringWidget(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                Component.translatableWithFallback("polylib.configuration.read_only", "Edit in config file"), font);
        label.setTooltip(Tooltip.create(Component.literal(String.valueOf(entry.value))));
        return new Element(getTranslationComponent(key), getTooltipComponent(key, null), label, false);
    }

    @Override
    protected boolean isAnyNondefault()
    {
        return session.entries().stream().anyMatch(ConfigEditorSession.Entry::nonDefault);
    }

    @Override
    protected void createResetButton()
    {
        resetButton = Button.builder(ConfigurationScreen.RESET, button -> {
            var before = session.entries().stream().map(e -> e.value).toList();
            var defaults = session.entries().stream().map(e -> e.hasDefault() ? e.defaultValue : e.value).toList();
            undoManager.add(this::restore, defaults, this::restore, before);
            rebuild();
        }).tooltip(Tooltip.create(ConfigurationScreen.RESET_TOOLTIP)).width(Button.SMALL_WIDTH).build();
    }

    private void restore(List<Object> values)
    {
        for (int i = 0; i < values.size(); i++) session.entries().get(i).value = values.get(i);
        changed = true;
    }

    @Override
    public void onClose()
    {
        try {
            session.save();
        } catch (RuntimeException e) {
            Constants.LOG.warn("Could not save config from editor", e);
            minecraft.gui.setScreen(new ConfirmScreen(retry -> minecraft.gui.setScreen(retry ? this : lastScreen),
                    Component.translatableWithFallback("polylib.configuration.save_failed", "Could not save config"),
                    Component.literal(e.getMessage()),
                    Component.translatableWithFallback("polylib.configuration.back", "Back to editor"),
                    Component.translatableWithFallback("polylib.configuration.discard", "Discard changes")));
            return;
        }
        // The native close handler must not try saving an FML/TOML config.
        changed = false;
        super.onClose();
    }
}
