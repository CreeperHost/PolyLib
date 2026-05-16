package net.creeperhost.polylib.client.modulargui.elements;

import net.creeperhost.polylib.client.modulargui.lib.GuiRender;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GeoParam;
import net.creeperhost.polylib.client.modulargui.lib.geometry.GuiParent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.creeperhost.polylib.client.modulargui.lib.geometry.Constraint.*;

public class GuiFuzzySearch<T> extends GuiElement<GuiFuzzySearch<T>> {

    private final GuiTextField searchField;
    private final GuiList<T> resultList;
    private final List<T> allItems;
    private final Function<T, String> stringExtractor;
    private final Consumer<T> onSelected;

    public GuiFuzzySearch(GuiParent<?> parent, List<T> items, Function<T, String> stringExtractor, Consumer<T> onSelected) {
        super(parent);
        this.allItems = items;
        this.stringExtractor = stringExtractor;
        this.onSelected = onSelected;

        // Background
        new net.creeperhost.polylib.client.modulargui.elements.GuiRectangle(this)
                .fill(0xFF161616).border(0xFF333333)
                .constrain(GeoParam.LEFT, match(get(GeoParam.LEFT)))
                .constrain(GeoParam.RIGHT, match(get(GeoParam.RIGHT)))
                .constrain(GeoParam.TOP, match(get(GeoParam.TOP)))
                .constrain(GeoParam.BOTTOM, match(get(GeoParam.BOTTOM)));

        searchField = new GuiTextField(this)
                .constrain(GeoParam.TOP, relative(get(GeoParam.TOP), 4))
                .constrain(GeoParam.LEFT, relative(get(GeoParam.LEFT), 4))
                .constrain(GeoParam.RIGHT, relative(get(GeoParam.RIGHT), -4))
                .constrain(GeoParam.HEIGHT, literal(14));

        resultList = new GuiList<T>(this)
                .setDisplayBuilder((list, item) -> {
                    String name = stringExtractor.apply(item);
                    return GuiButton.vanillaAnimated(list, () -> Component.literal(name), () -> onSelected.accept(item))
                            .constrain(GeoParam.HEIGHT, literal(16));
                })
                .setFilter(this::matchesQuery)
                .constrain(GeoParam.TOP, relative(searchField.get(GeoParam.BOTTOM), 4))
                .constrain(GeoParam.LEFT, relative(get(GeoParam.LEFT), 4))
                .constrain(GeoParam.RIGHT, relative(get(GeoParam.RIGHT), -4))
                .constrain(GeoParam.BOTTOM, relative(get(GeoParam.BOTTOM), -4));

        resultList.addHiddenScrollBar();
        allItems.forEach(resultList::add);

        searchField.setTextState(net.creeperhost.polylib.client.modulargui.lib.TextState.simpleState("", s -> resultList.markDirty()));
    }

    private boolean matchesQuery(T item) {
        String query = searchField.getTextState().getText().trim();
        if (query.isEmpty()) return true;

        String name = stringExtractor.apply(item);
        
        // Exact substring
        if (name.toLowerCase().contains(query.toLowerCase())) return true;

        // CamelCase acronym check
        StringBuilder acronym = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c)) {
                acronym.append(c);
            }
        }
        if (acronym.toString().toLowerCase().startsWith(query.toLowerCase())) return true;

        return false;
    }

    @Override
    public void tick(double mouseX, double mouseY) {
        super.tick(mouseX, mouseY);
        // Focus the text field automatically when opened
        if (!searchField.isFocused() && this.isEnabled()) {
            searchField.setFocus(true);
        }
    }
}
