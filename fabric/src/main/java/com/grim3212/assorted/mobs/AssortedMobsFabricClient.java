package com.grim3212.assorted.mobs;

import com.grim3212.assorted.mobs.client.MobsClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedMobsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MobsClient.init();
    }
}
