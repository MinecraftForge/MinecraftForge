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

import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ObjectHolder("shield_test")
@EventBusSubscriber
@Mod(modid = "shield_test", name = "Shield Test", version = "0.0.0", acceptableRemoteVersions = "*")
public class ShieldTest
{
    public static final ItemShield DIAMOND_SHIELD = null;
    public static final ItemSword HEAVY_DIAMOND_SWORD = null;
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        class ItemCustomShield extends ItemShield
        {
            
            @Override
            public String getItemStackDisplayName(ItemStack stack)
            {
                return I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
            }
            
            @Override
            public boolean isShield(ItemStack stack, EntityLivingBase entity)
            {
                return true;
            }
        }
        class ItemHeavyDiamondSword extends ItemSword
        {
            public ItemHeavyDiamondSword()
            {
                super(ToolMaterial.DIAMOND);
            }
            
            @Override
            public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot)
            {
                Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
                if(slot == EntityEquipmentSlot.MAINHAND)
                {
                    multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 12.0F, 0));
                    multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -5D, 0));
                }
                return multimap;
            }
            
            @Override
            public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker)
            {
                return shield.getItem() != DIAMOND_SHIELD;
            }
        }
        event.getRegistry().register(new ItemCustomShield().setMaxDamage(2098).setUnlocalizedName("diamond_shield").setRegistryName("diamond_shield"));
        event.getRegistry().register(new ItemHeavyDiamondSword().setUnlocalizedName("heavy_diamond_sword").setRegistryName("heavy_diamond_sword"));
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(DIAMOND_SHIELD, 0, new ModelResourceLocation("shield_test:diamond_shield", "inventory"));
        ModelLoader.setCustomModelResourceLocation(HEAVY_DIAMOND_SWORD, 0, new ModelResourceLocation("minecraft:diamond_sword", "inventory"));
    }
}