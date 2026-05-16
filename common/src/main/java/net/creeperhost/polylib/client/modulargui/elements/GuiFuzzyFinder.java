package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;

/**
 * A search-based context menu overlay (Fuzzy Finder) for visual scripting.
 * Allows users to type to filter a list of available nodes or options.
 */
public class GuiFuzzyFinder<T> extends GuiElement<GuiFuzzyFinder<T>> {

    private String searchText = "";
    private final List<FuzzyOption<T>> allOptions = new ArrayList<>();
    private final List<FuzzyOption<T>> filteredOptions = new ArrayList<>();
    private int selectedIndex = 0;
    
    private Consumer<T> onSelect;
    private Runnable onCancel;

    public record FuzzyOption<T>(String label, String description, T value) {}

    public GuiFuzzyFinder(GuiParent<?> parent) {
        super(parent);
    }

    public GuiFuzzyFinder<T> setOptions(List<FuzzyOption<T>> options) {
        this.allOptions.clear();
        this.allOptions.addAll(options);
        updateFilter();
        return this;
    }

    public GuiFuzzyFinder<T> onSelect(Consumer<T> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    public GuiFuzzyFinder<T> onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public void updateFilter() {
        filteredOptions.clear();
        String query = searchText.toLowerCase();
        for (FuzzyOption<T> opt : allOptions) {
            if (opt.label().toLowerCase().contains(query) || 
                (opt.description() != null && opt.description().toLowerCase().contains(query))) {
                filteredOptions.add(opt);
            }
        }
        selectedIndex = Math.max(0, Math.min(selectedIndex, filteredOptions.size() - 1));
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        // Standard text input
        if (characterEvent.isAllowedChatCharacter()) {
            searchText += characterEvent.codepointAsString();
            updateFilter();
            return true;
        }
        return super.charTyped(characterEvent);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        // 259 = KEY_BACKSPACE
        if (keyCode == 259 && !searchText.isEmpty()) {
            searchText = searchText.substring(0, Math.max(0, searchText.length() - 1));
            updateFilter();
            return true;
        }
        // 257 = KEY_ENTER, 335 = KEY_NUMPADENTER
        if ((keyCode == 257 || keyCode == 335) && !filteredOptions.isEmpty()) {
            if (onSelect != null) {
                onSelect.accept(filteredOptions.get(selectedIndex).value());
            }
            return true;
        }
        // 265 = KEY_UP
        if (keyCode == 265) {
            selectedIndex = Math.max(0, selectedIndex - 1);
            return true;
        }
        // 264 = KEY_DOWN
        if (keyCode == 264) {
            selectedIndex = Math.min(filteredOptions.size() - 1, selectedIndex + 1);
            return true;
        }
        // 256 = KEY_ESCAPE
        if (keyCode == 256) {
            if (onCancel != null) onCancel.run();
            return true;
        }
        return super.keyPressed(event);
    }

    public void render(GuiRender render, double mouseX, double mouseY, float partialTicks) {
        // Render search box and filtered list...
        // This is a stub that PolyLib will implement natively via its render pipelines.
        double x = xMin();
        double y = yMin();
        double w = xSize();
        
        // Search Box Background
        render.fill(x, y, x + w, y + 20, 0xFF222222);
        // List Background
        render.fill(x, y + 20, x + w, yMax(), 0xFF111111);
    }
}
