package net.creeperhost.polylib.client.modulargui.lib;

import net.creeperhost.polylib.client.modulargui.elements.GuiElement;

/**
 * Implemented by elements that have a separate child element to which content should be added.
 * e.g. scroll element, manipulable element etc...
 * Created by brandon3055 on 13/11/2023
 */
public interface ContentElement<T extends GuiElement<?>> {

    T getContentElement();
}
