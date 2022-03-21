/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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

@Mod("forge_datafix_test")
public class ForgeDataFixTest
{
    public static final  boolean ENABLE = true;
    private static final Logger  LOGGER = LogUtils.getLogger();

    public ForgeDataFixTest()
    {
        if (ENABLE) {
            registerEventHandler();
        }
    }

    private void registerEventHandler() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(
          this::onDataFixSchemaTypeConfiguration
        );
        FMLJavaModLoadingContext.get().getModEventBus().addListener(
          this::onDataFixerRegistration
        );
    }

    private void onDataFixSchemaTypeConfiguration(ConfigureDataFixSchemaEvent configureDataFixSchemaEvent) {
        LOGGER.info("Initializing DFU schema: " + configureDataFixSchemaEvent.getVersion());

        //Register something to all the schemas.
        configureDataFixSchemaEvent.registerSimpleBlockEntity(new ResourceLocation("forge_datafix_test:simple_block_entity"));
    }

    private void onDataFixerRegistration(RegisterFixesEvent registerFixesEvent) {
        LOGGER.info("Registering DFU Fixer " + registerFixesEvent.getVersion());

        registerFixesEvent.addFixer(schema -> new DataFix(schema, false) {
            @Override
            protected TypeRewriteRule makeRule() {
                LOGGER.warn("Creating test rewrite rule for schema version: " + schema.getVersionKey());
                return TypeRewriteRule.nop();
            }
        });
    }
}
