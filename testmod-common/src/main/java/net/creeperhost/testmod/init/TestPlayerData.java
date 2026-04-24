package net.creeperhost.testmod.init;

import com.mojang.serialization.Codec;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataRegistry;
import net.creeperhost.polylib.player.serverdata.PlayerServerDataType;
import net.creeperhost.polylib.player.settings.BroadcastScope;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsRegistry;
import net.creeperhost.polylib.player.settings.PlayerClientSettingsType;
import net.creeperhost.testmod.TestModCommon;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * Testmod registrations for PlayerClientSettings and PlayerServerData.
 * Exercises the API added in feat/player-data (PR #105).
 *
 * NOTE: depends on feat/player-data PR being merged before this compiles standalone.
 */
public final class TestPlayerData
{
    /**
     * A simple boolean client setting — e.g. whether the player has opted into something.
     * Synced to all nearby players (TRACKING_RANGE) and preserved on death.
     */
    public static final PlayerClientSettingsType<Boolean> TEST_TOGGLE = PlayerClientSettingsRegistry.register(
            Identifier.fromNamespaceAndPath(TestModCommon.MOD_ID, "test_toggle"),
            StreamCodec.of(
                    (buf, val) -> buf.writeBoolean(val),
                    buf -> buf.readBoolean()
            ),
            () -> false,
            BroadcastScope.TRACKING_RANGE,
            true
    );

    /**
     * A simple integer server data value — e.g. a kill/use counter.
     * Synced to the owning player's client and lost on death.
     */
    public static final PlayerServerDataType<Integer> TEST_COUNTER = PlayerServerDataRegistry.register(
            Identifier.fromNamespaceAndPath(TestModCommon.MOD_ID, "test_counter"),
            Codec.INT,
            StreamCodec.of(
                    (buf, val) -> buf.writeInt(val),
                    buf -> buf.readInt()
            ),
            () -> 0,
            false
    );

    // TODO(feat/string-uuid-data-types): add a PlayerServerDataType<String> using StringData
    //   and a PlayerServerDataType<UUID> using UUIDData, then verify they persist across
    //   server restarts and sync correctly to the client via the testmod GUI/commands.

    public static void init()
    {
        // Static fields are initialised above; this method exists so TestModCommon can
        // trigger classloading at the right time (before the server starts).
        // No-op body is intentional.
    }
}
