package com.grim3212.assorted.treasuremob;

import com.grim3212.assorted.treasuremob.client.TreasureMobClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedTreasureMobFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TreasureMobClient.init();
    }
}
