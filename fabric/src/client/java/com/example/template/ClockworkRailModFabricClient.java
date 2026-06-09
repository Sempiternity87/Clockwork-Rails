package com.sempiternity.clockwork_rail;

import net.fabricmc.api.ClientModInitializer;

public class ClockworkRailModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClockworkRailModClient.onInitializeClient();
    }
}
