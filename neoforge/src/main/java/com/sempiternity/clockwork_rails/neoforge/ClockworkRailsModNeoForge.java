package com.sempiternity.clockwork_rails.neoforge;

import com.sempiternity.clockwork_rails.ClockworkRailMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ClockworkRailMod.MOD_ID)
public class ClockworkRailModNeoForge {
    public ClockworkRailModNeoForge(IEventBus modEventBus) {
        ClockworkRailMod.onInitialize();
    }
}
