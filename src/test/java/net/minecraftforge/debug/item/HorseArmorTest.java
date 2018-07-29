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

package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
@Mod(modid = HorseArmorTest.MODID, name = "HorseArmorTest", version = "1.0", acceptableRemoteVersions = "*")
public class HorseArmorTest 
{
    public static final String MODID = "horse_armor_test";
    public static final boolean ENABLED = true;
    
    public static HorseArmorType testArmorType;
    @ObjectHolder("test_armor")
    public static final ItemTestHorseArmor TEST_ARMOR = null;
    
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if(ENABLED)
            testArmorType = EnumHelper.addHorseArmor("test", MODID + ":textures/entity/horse/armor/test.png", 15);
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        if(ENABLED)
            event.getRegistry().register(new ItemTestHorseArmor().setRegistryName(MODID, "test_armor").setUnlocalizedName(MODID + ".testArmor"));
    }

    @EventBusSubscriber(modid = MODID, value = Side.CLIENT)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) 
        {
            if(ENABLED) 
                ModelLoader.setCustomModelResourceLocation(TEST_ARMOR, 0, new ModelResourceLocation(TEST_ARMOR.getRegistryName(), "inventory"));
        }
    }

    private static class ItemTestHorseArmor extends Item
    {   
        @Override
        public HorseArmorType getHorseArmorType(ItemStack stack) 
        {
            return testArmorType;
        }
        
        @Override
        public String getHorseArmorTexture(EntityLiving wearer, ItemStack stack) 
        {
            return stack.isItemEnchanted() ? HorseArmorType.IRON.getTextureName() : super.getHorseArmorTexture(wearer, stack);
        }
        
        @Override
        public void onHorseArmorTick(World world, EntityLiving wearer, ItemStack itemStack) 
        {
            if(wearer.ticksExisted % 15 == 0)
                wearer.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 20, 1));
        }
    }
}
