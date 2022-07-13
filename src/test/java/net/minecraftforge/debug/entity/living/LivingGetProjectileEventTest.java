/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity.living;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingGetProjectileEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("living_get_projectile_event_test")
public class LivingGetProjectileEventTest
{
    public static final boolean ENABLED = true;
    public static final Logger LOGGER = LogManager.getLogger();

    public LivingGetProjectileEventTest()
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.addListener(this::onLivingGetProjectile);
        }
    }

    public void onLivingGetProjectile(LivingGetProjectileEvent event)
    {
        LOGGER.info("{} about to fire {} with a {}", event.getEntity(), event.getProjectileItemStack(), event.getProjectileWeaponItemStack());

        // for this test, we're checking if the player has a spectral arrow itemstack in their offhand and if they're firing a normal arrow.
        // if they do, we're going to use that itemstack. if not, we will create a spectral arrow itemstack
        // this demonstrates the usage of specific itemstacks with this event. you can use specific itemstacks from the player's inventory in this similar style
        if (event.getEntity() instanceof Player player && event.getProjectileItemStack().getItem() == Items.ARROW)
        {
            ItemStack offhandItem = player.getOffhandItem();
            event.setProjectileItemStack(offhandItem.getItem() == Items.SPECTRAL_ARROW ? offhandItem : new ItemStack(Items.SPECTRAL_ARROW));
        }
    }
}
