package com.grim3212.assorted.treasuremob.client;

import com.grim3212.assorted.treasuremob.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * The client-only entry point: a second {@code @Mod} for the same mod id, constructed only on the
 * client.
 */
@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class AssortedTreasureMobNeoForgeClient {

    public AssortedTreasureMobNeoForgeClient(IEventBus modBus, ModContainer modContainer) {
        TreasureMobClient.init();
    }
}
