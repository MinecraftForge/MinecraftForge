/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.debug.entity.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DispenseBoatBehavior;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.entity.item.BoatType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.util.logging.Logger;

import static net.minecraftforge.debug.entity.item.BoatEntityTest.MOD_ID;

@Mod(MOD_ID)
@Mod.EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD)
public class BoatEntityTest
{
    static final String MOD_ID = "boat_entity_test";
    private static final String BOAT_KEY = "test";
    private static final String ITEM_KEY = "test_boat";

    private static final Logger LOGGER = Logger.getLogger(MOD_ID);

    private static BoatType GREEN_BOAT_TYPE = new GreenBoatType();
    private static Item TEST_BOAT_ITEM = new BoatItem(GREEN_BOAT_TYPE, (new Item.Properties()).maxStackSize(1).group(ItemGroup.TRANSPORTATION));

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        TEST_BOAT_ITEM.setRegistryName(MOD_ID, ITEM_KEY);
        event.getRegistry().register(TEST_BOAT_ITEM);
        DispenserBlock.registerDispenseBehavior(TEST_BOAT_ITEM, new DispenseBoatBehavior(GREEN_BOAT_TYPE));
    }

    @SubscribeEvent
    public static void registerBoatTypes(RegistryEvent.Register<BoatType> event)
    {
        GREEN_BOAT_TYPE.setRegistryName(MOD_ID, BOAT_KEY);
        event.getRegistry().register(GREEN_BOAT_TYPE);
    }

    static class GreenBoatType extends BoatType {

        @Override
        public Item getBoatItem() {
            LOGGER.info("getBoatItem()");
            return BoatEntityTest.TEST_BOAT_ITEM;
        }

        @Override
        public IItemProvider[] getOnBreakItems() {
            LOGGER.info("getDropItems()");
            return new IItemProvider[]{Items.CACTUS};
        }

    }
}


