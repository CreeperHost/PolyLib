package net.creeperhost.testmod;

import com.mojang.serialization.Codec;
import net.creeperhost.polylib.platform.Services;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataRegistry;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataType;
import net.creeperhost.polylib.player.settings.BroadcastScope;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsRegistry;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsType;
import net.creeperhost.testmod.init.TestBlocks;
import net.creeperhost.testmod.init.TestCreativeTabs;
import net.creeperhost.testmod.init.TestItems;
import net.creeperhost.testmod.init.TestPlayerData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public class TestModCommon
{
    /** Demo: client-side "reduce screen shake" preference synced to nearby players. */
    public static final PlayerClientSettingsType<Boolean> REDUCE_SCREENSHAKE =
        PlayerClientSettingsRegistry.register(
            Identifier.fromNamespaceAndPath("testmod", "reduce_screenshake"),
            StreamCodec.<RegistryFriendlyByteBuf, Boolean>of(
                (buf, v) -> buf.writeBoolean(v),
                buf -> buf.readBoolean()
            ),
            () -> false,
            BroadcastScope.TRACKING_RANGE,
            /*copyOnDeath*/ true,
            "testmod.setting.reduce_screenshake",
            "Reduce Screen Shake"
        );

    /** Demo: server-authoritative ticks-played counter, synced to owner client. */
    public static final PlayerServerDataType<Integer> TICKS_PLAYED =
        PlayerServerDataRegistry.register(
            Identifier.fromNamespaceAndPath("testmod", "ticks_played"),
            Codec.INT,
            StreamCodec.<RegistryFriendlyByteBuf, Integer>of(
                (buf, v) -> buf.writeVarInt(v),
                buf -> buf.readVarInt()
            ),
            () -> 0,
            /*copyOnDeath*/ true
        );

    public static void init()
    {
        TestPlayerData.init();
        TestItems.init();
        TestBlocks.init();
        TestCreativeTabs.init();

        if (Services.PLATFORM.isClient()) {
            TestModClientCommon.init();
        }
    }
}
