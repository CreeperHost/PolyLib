package net.creeperhost.polylib.config;

import blue.endless.jankson.Comment;

/**
 * Created by brandon3055 on 12/09/2024
 */
public class PolyConfig extends ConfigData {

    @Comment ("Allows poly lib to run as a server side only support. Without this, non-vanilla clients without polylib may not be able to connect.")
    public boolean serverOnlySupport = true;

}
