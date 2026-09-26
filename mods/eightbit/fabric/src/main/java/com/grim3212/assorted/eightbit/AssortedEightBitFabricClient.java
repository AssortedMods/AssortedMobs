package com.grim3212.assorted.eightbit;

import com.grim3212.assorted.eightbit.client.EightBitClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedEightBitFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EightBitClient.init();
    }
}
