package com.sempiternity.clockwork_rails.neoforge;

import com.sempiternity.clockwork_rails.ClockworkRailsMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ClockworkRailsMod.MOD_ID)
public class ClockworkRailsModNeoForge {
    public ClockworkRailsModNeoForge(IEventBus modEventBus) {
        ClockworkRailsMod.onInitialize();
    }
}
