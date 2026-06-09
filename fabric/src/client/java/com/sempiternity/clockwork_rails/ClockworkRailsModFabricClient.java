package com.sempiternity.clockwork_rails;

import net.fabricmc.api.ClientModInitializer;

public class ClockworkRailsModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClockworkRailsModClient.onInitializeClient();
    }
}
