package net.minecraftforge.test;

import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
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
    
    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public void pre(FMLPreInitializationEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(DIAMOND_SHIELD, 0, new ModelResourceLocation("shield_test:diamond_shield", "inventory"));
        ModelLoader.setCustomModelResourceLocation(HEAVY_DIAMOND_SWORD, 0, new ModelResourceLocation("minecraft:diamond_sword", "inventory"));
    }
    
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
    
    private static class TileEntityCustomShield extends TileEntity
    {
        
    }
}
