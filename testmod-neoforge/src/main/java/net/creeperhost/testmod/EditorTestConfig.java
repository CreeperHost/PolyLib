package net.creeperhost.testmod;

import blue.endless.jankson.Comment;
import net.creeperhost.polylib.config.ConfigData;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.TranslatableEnum;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;

/** Manual regression fixture for the native NeoForge JSON5 editor. */
public class EditorTestConfig extends ConfigData
{
    @Comment("Toggle this, close the editor, then reopen to verify persistence.")
    public boolean enabled = true;
    public int count = 10;
    public long largeCount = 10000000000L;
    public double scale = 1.5;
    public String greeting = "Hello from JSON5";
    public Mode mode = Mode.FIRST;
    public Nested nested = new Nested();
    public Map<String, Boolean> flags = new LinkedHashMap<>(Map.of("example.mod", true));
    @Comment("Lists remain visible but read-only in the first editor implementation.")
    public List<String> names = new ArrayList<>();

    public enum Mode implements TranslatableEnum {
        FIRST, SECOND, THIRD;

        @Override
        public Component getTranslatedName() {
            return Component.translatable("testmod.configuration.mode." + name().toLowerCase(Locale.ROOT));
        }
    }

    public static class Nested {
        public float opacity = 0.75F;
        public int offset = -5;
    }
}
