/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;

/**
 * Internal implementation for optionally logging warnings about legacy tags along with suggestions for direct
 * replacements, in order to help modders migrate to the new tags.
 * @see ForgeConfig.Common#migrationHelperMode
 */
@ApiStatus.Internal
public final class TagConventionMigrationHelper {
    private TagConventionMigrationHelper() {}

    private static final ArrayList<TagKey<?>> FOUND_LEGACY_TAGS = new ArrayList<>();

    static void init() {
        MinecraftForge.EVENT_BUS.addListener(TagConventionMigrationHelper::onServerStarting);
    }

    public static void onServerStarting(ServerStartingEvent event) {
        // note: this check has to be done here because the config is not loaded until the server is starting
        var mode = ForgeConfig.COMMON.migrationHelperMode.get();
        boolean shouldRun = switch (mode) {
            case OFF -> false;
            case ONLY_IN_DEV_ENV -> !FMLLoader.isProduction();
            case ALWAYS -> true;
        };

        if (shouldRun)
            run(event.getServer().registryAccess());
    }

    private static void run(RegistryAccess.Frozen registryAccess) {
        registryAccess.registries().forEach(registryEntry -> {
            if (registryEntry.key().location().getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
                FOUND_LEGACY_TAGS.addAll(
                        registryEntry.value().getTagNames().filter(TagConventionMappings.MAPPINGS::containsKey).toList()
                );
            }
        });

        if (FOUND_LEGACY_TAGS.isEmpty())
            return;

        var stringBuilder = new StringBuilder("""
            ~~~~~~~~ Tag migration helper ~~~~~~~~
            Warning for mod devs: Found known legacy tags that have direct common convention equivalents - consider migrating these to improve compatibility with other mods.
            Note: This feature isn't fully comprehensive - see Forge's net.minecraftforge.common.Tags class for a full list of tags.
            You can disable this message by setting "migrationHelperMode" to "OFF" in Forge's common config.
            
            Here are some suggestions for replacements:""".stripIndent());

        for (var legacyTag : FOUND_LEGACY_TAGS) {
            var replacement = TagConventionMappings.MAPPINGS.get(legacyTag);
            stringBuilder.append("\n- ").append(legacyTag).append(" -> ").append(replacement);
        }
        stringBuilder.append("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        LogManager.getLogger().warn(stringBuilder);
        FOUND_LEGACY_TAGS.clear();
        FOUND_LEGACY_TAGS.trimToSize();
    }

}
