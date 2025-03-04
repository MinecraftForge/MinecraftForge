/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ProjectileImpactEventLegacyCancelTest.MOD_ID)
public class ProjectileImpactEventLegacyCancelTest {
    static final String MOD_ID = "projectile_impact_event_legacy_cancel";

    private static final boolean ENABLED = false;
    private static final Logger LOGGER = LogManager.getLogger();

    public ProjectileImpactEventLegacyCancelTest() {
        if (ENABLED) {
            LOGGER.warn("ProjectileImpactEvent Legacy Cancel test mod active! This will cancel all AbstractArrow impacts!");
            MinecraftForge.EVENT_BUS.addListener(this::onProjectileImpact);
        }
    }

    @SuppressWarnings("removal")
    private void onProjectileImpact(ProjectileImpactEvent event) {
        event.setCanceled(event.getProjectile() instanceof AbstractArrow);
    }
}
