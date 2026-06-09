package com.sempiternity.clockwork_rails;

import net.fabricmc.api.ClientModInitializer;

public class ClockworkRailModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClockworkRailModClient.onInitializeClient();
    }
}
