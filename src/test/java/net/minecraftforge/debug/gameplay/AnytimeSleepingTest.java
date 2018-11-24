/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.gameplay;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = AnytimeSleepingTest.MODID, name = "Anytime Sleeping Test", version = "0.0", acceptableRemoteVersions = "*")
public class AnytimeSleepingTest
{
    public static final String MODID = "anytimesleepingtest";
    @GameRegistry.ObjectHolder(ItemSleepCharm.NAME)
    public static final Item SLEEP_CHARM = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCheckSleepTime(SleepingTimeCheckEvent evt)
    {
        EntityPlayer player = evt.getEntityPlayer();
        if (player.getHeldItemMainhand().getItem() instanceof ItemSleepCharm)
        {
            evt.setResult(Event.Result.ALLOW);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> evt)
        {
            evt.getRegistry().register(new ItemSleepCharm());
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent evt)
        {
            ModelLoader.setCustomModelResourceLocation(SLEEP_CHARM, 0, new ModelResourceLocation("minecraft:totem", "inventory"));
        }
    }

    public static class ItemSleepCharm extends Item
    {
        static final String NAME = "sleep_charm";

        private ItemSleepCharm()
        {
            this.setCreativeTab(CreativeTabs.MISC);
            this.setUnlocalizedName(MODID + ":" + NAME);
            this.setRegistryName(new ResourceLocation(MODID, NAME));
        }
    }
}
