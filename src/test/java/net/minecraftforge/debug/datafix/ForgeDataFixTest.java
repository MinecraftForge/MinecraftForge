/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.datafix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.datafix.ConfigureDataFixSchemaEvent;
import net.minecraftforge.event.datafix.RegisterFixesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * This test class checks if the forge wrapped DFU instance sets up properly during startup.
 *
 * In the startup log there should be a set of messages first indicating that the DFU schemas are initialized in ascending order,
 * then there should be a set of messages which indicates that the DFU custom fixes are registered properly and also initialized
 * in the proper ascending order.
 */
@Mod("forge_datafix_test")
public class ForgeDataFixTest
{
    public static final  boolean ENABLED = true;
    private static final Logger  LOGGER = LogUtils.getLogger();

    public ForgeDataFixTest()
    {
        if (ENABLED)
        {
            registerEventHandler();
        }
    }

    private void registerEventHandler()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(
          this::onDataFixSchemaTypeConfiguration
        );
        FMLJavaModLoadingContext.get().getModEventBus().addListener(
          this::onDataFixerRegistration
        );
    }

    private void onDataFixSchemaTypeConfiguration(ConfigureDataFixSchemaEvent configureDataFixSchemaEvent)
    {
        LOGGER.info("Initializing DFU schema: " + configureDataFixSchemaEvent.getVersion());

        //Register something to all the schemas.
        configureDataFixSchemaEvent.registerSimpleBlockEntity(new ResourceLocation("forge_datafix_test:simple_block_entity"));
    }

    private void onDataFixerRegistration(RegisterFixesEvent registerFixesEvent)
    {
        LOGGER.info("Registering DFU Fixer " + registerFixesEvent.getVersion());

        registerFixesEvent.addFixer(schema -> new DataFix(schema, false)
        {
            @Override
            protected TypeRewriteRule makeRule() {
                LOGGER.warn("Creating test rewrite rule for schema version: " + schema.getVersionKey());
                return TypeRewriteRule.nop();
            }
        });
    }
}
