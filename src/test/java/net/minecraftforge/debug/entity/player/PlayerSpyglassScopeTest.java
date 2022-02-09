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

package net.minecraftforge.debug.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.SpyglassCondition;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Tests the patch to Player to allow alternate triggers for the spyglass scoping effect.
 * While enabled, sneaking will cause the scoping effect.
 */
@Mod(PlayerSpyglassScopeTest.MODID)
public class PlayerSpyglassScopeTest
{

    static final String MODID = "player_spyglass_scope_test";
    static final boolean ENABLED = false;

    public PlayerSpyglassScopeTest()
    {
        if (ENABLED)
        {
            FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(SpyglassCondition.class, PlayerSpyglassScopeTest::registerSpyglassCondition);
        }
    }

    private static void registerSpyglassCondition(RegistryEvent.Register<SpyglassCondition> e)
    {
        e.getRegistry().register(new SpyglassCondition()
        {
            @Override
            public boolean test(Player player)
            {
                return player.isShiftKeyDown();
            }
        }.setRegistryName(MODID, "while_sneaking"));
    }
}
