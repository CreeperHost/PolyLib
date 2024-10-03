package net.creeperhost.polylib.config;

import blue.endless.jankson.Comment;

/**
 * Created by brandon3055 on 12/09/2024
 */
public class PolyConfig extends ConfigData {

    //TODO, Later, Once mods have implemented the PolyLib.initPolyItemData() call, this can be changed to true by default.
    @Comment ("Allows poly lib to run as a server side only support. Without this, non-vanilla clients without polylib may not be able to connect.")
    public boolean serverOnlySupport = false;

}
