package net.minecraftforge.config.boot;

import net.minecraftforge.common.ForgeConfigSpec;

public class ForgeBootConfig {

    public final ForgeConfigSpec.BooleanValue cachePackAccess;
    public final ForgeConfigSpec.BooleanValue indexVanillaPackCachesOnThread;
    public final ForgeConfigSpec.BooleanValue indexModPackCachesOnThread;

    public ForgeBootConfig(ForgeConfigSpec.Builder builder) {
        builder.push("resources");
        builder.push("caching");

        cachePackAccess = builder
                .comment("Set this to true to cache resource listings in resource and data packs")
                .translation("forge.configgui.cachePackAccess")
                .worldRestart()
                .define("cachePackAccess", true);

        indexVanillaPackCachesOnThread = builder
                .comment("Set this to true to index vanilla resource and data packs on thread")
                .translation("forge.configgui.indexVanillaPackCachesOnThread")
                .worldRestart()
                .define("indexVanillaPackCachesOnThread", false);

        indexModPackCachesOnThread = builder
                .comment("Set this to true to index mod resource and data packs on thread")
                .translation("forge.configgui.indexModPackCachesOnThread")
                .worldRestart()
                .define("indexModPackCachesOnThread", false);

        builder.pop();
        builder.pop();
    }
}
