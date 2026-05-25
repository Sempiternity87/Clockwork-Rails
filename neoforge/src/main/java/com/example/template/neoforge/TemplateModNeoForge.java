package com.example.template.neoforge;

import com.example.template.TemplateMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(TemplateMod.MOD_ID)
public class TemplateModNeoForge {
    public TemplateModNeoForge(IEventBus modEventBus) {
        TemplateMod.onInitialize();
    }
}
