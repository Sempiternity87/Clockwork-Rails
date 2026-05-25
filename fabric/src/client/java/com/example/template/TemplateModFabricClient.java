package com.example.template;

import net.fabricmc.api.ClientModInitializer;

public class TemplateModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TemplateModClient.onInitializeClient();
    }
}
