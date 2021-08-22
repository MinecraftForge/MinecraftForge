package net.minecraftforge.fml.loading.targets;

import net.minecraftforge.api.distmarker.Dist;

import java.util.concurrent.Callable;

public class ForgeGametestUserdevLaunchHandler extends ForgeUserdevLaunchHandler {
    @Override public String name() { return "forgegametestuserdev"; }
    @Override public Dist getDist() { return Dist.DEDICATED_SERVER; }

    @Override
    public Callable<Void> launchService(String[] arguments, ModuleLayer layer) {
        return () -> {
            var args = preLaunch(arguments, layer);

            Class.forName(layer.findModule("forge").orElseThrow(), "net.minecraftforge.gametest.GameTestMain").getMethod("main", String[].class).invoke(null, (Object)args);
            return null;
        };
    }
}