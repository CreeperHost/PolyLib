package net.creeperhost.polylib.config;

import blue.endless.jankson.Comment;

/**
 * Created by brandon3055 on 12/09/2024
 */
public class PolyConfig extends ConfigData {

    @Comment ("Allows poly lib to run as a server side only support. Without this, non-vanilla clients without polylib may not be able to connect.")
    public boolean serverOnlySupport = true;

    @Comment ("When true, all registered accessibility preferences treat the player's value as authoritative, ignoring server policy.")
    public boolean radicalAccessibility = false;

    @Comment ("Controls whether the keyboard shortcut for each registered config panel is enabled. Keys are mod IDs.")
    public java.util.Map<String, Boolean> configPanelKeybinds = new java.util.HashMap<>();

    @Comment ("Force enables the Chunk Map feature even if no dependent mod requests it.")
    public boolean forceEnableChunkMap = false;

}
