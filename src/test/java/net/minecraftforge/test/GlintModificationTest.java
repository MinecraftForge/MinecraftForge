package net.minecraftforge.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.OverlayGlintEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid="glintTest", clientSideOnly = true)
public class GlintModificationTest {
	
	@EventHandler
	public void pre(FMLPreInitializationEvent e){
		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.registerItem(new ItemArmor(ArmorMaterial.LEATHER, 0, 0){
	@Override
	public int getItemGlintColor(ItemStack i){
		return 0xFFFF0000;
	}
	
	@Override
	public int getArmorGlintColor(ItemStack i, EntityLivingBase e, int slot){
		return 0xFF00FF00;
	}
	
	@Override
	public boolean isValidArmor(ItemStack i, int slot, Entity e){
		return slot == 0;
	}
	
	@Override
	public boolean hasEffect(ItemStack i){
		return true;
	}
	
}.setUnlocalizedName("glint_test").setRegistryName("glint_test"));
	}
	
	static final int red = 0xff << 24 | 0xFF0096;
	
	@SubscribeEvent
	public void doTheItems(OverlayGlintEvent.StackOverlayEvent e){
		if(e.theStack == null)
			return;
		if(e.theStack.getItem() instanceof ItemPotion)
			e.glintValue = (255 << 24) | (((ItemPotion)e.theStack.getItem()).getColorFromDamage(e.theStack.getItemDamage()));
		else if(e.theStack.getItem() instanceof ItemEnchantedBook && e.theStack.hasTagCompound()){
			e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void doTheArmors(OverlayGlintEvent.ArmorOverlayEvent e){
		if(e.armorSlot == 1)
			e.glintValue = 0xFFcccc00;
	}
	
	
	
}
