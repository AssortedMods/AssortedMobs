package com.grim3212.assorted.icepixie;

import com.grim3212.assorted.icepixie.client.IcePixieClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedIcePixieFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        IcePixieClient.init();
    }
}
