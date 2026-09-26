package com.grim3212.assorted.seacreatures;

import com.grim3212.assorted.seacreatures.client.SeaCreaturesClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedSeaCreaturesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SeaCreaturesClient.init();
    }
}
