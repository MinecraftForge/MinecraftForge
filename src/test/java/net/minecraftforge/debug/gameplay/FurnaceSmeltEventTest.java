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

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceSmeltEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = FurnaceSmeltEventTest.MODID, name = "Furnace Smelt Event Test", version = "1.0", acceptableRemoteVersions = "*")
public class FurnaceSmeltEventTest {

    public static final String MODID = "furnacesmelteventtest";

    private static final boolean ENABLED = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (ENABLED) {
            MinecraftForge.EVENT_BUS.register(FurnaceSmeltEventTest.class);
        }
    }

    @SubscribeEvent
    public static void onFurnaceSmelt(FurnaceSmeltEvent event) {
        ItemStack inputStack = event.getInputStack();
        //If the input item is a potion item, set the output item to be a lingering potion with the same effects as the input potion
        if(inputStack.getItem() == Items.POTIONITEM) {
            ItemStack out = new ItemStack(Items.LINGERING_POTION);
            PotionUtils.addPotionToItemStack(out, PotionUtils.getPotionFromItem(inputStack));
            PotionUtils.appendEffects(out, PotionUtils.getEffectsFromStack(inputStack));
            event.setOutputStack(out);
            event.setCanceled(true);
        }
    }

}
