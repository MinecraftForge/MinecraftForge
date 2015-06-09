package net.minecraftforge.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.common.util.ItemCondition;
import net.minecraftforge.common.util.NBTEvidence;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.SensitiveOreDict;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.google.common.primitives.Ints;
import com.sun.javafx.css.converters.ShapeConverter;

@Mod(modid = NewOreSystemDebug.MODID, version = NewOreSystemDebug.VERSION)
public class NewOreSystemDebug
{
    public static final String MODID = "ForgeOreSystemDebugMod";
    public static final String VERSION = "1.0";

    public static Item itemIngot;;
    public static ItemCondition condIron;
    public static ItemCondition condGold;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        itemIngot = new ItemIngot();
        condIron  = new ItemCondition.Builder(itemIngot).addCondition("metal", NBTEvidence.STRING, "iron").build();
        condGold = new ItemCondition.Builder(itemIngot).addCondition("metal", NBTEvidence.STRING, "gold").build();
        
        GameRegistry.registerItem(itemIngot, "newIngot");
        GameRegistry.addRecipe(new ShapedOreRecipe(Items.shears, "X ", " X", 'X', "ingotGold"));
        SensitiveOreDict.registerSensitiveOre("ingotIron", condIron);
        SensitiveOreDict.registerSensitiveOre("ingotGold", condGold);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
    
    public static class ItemIngot extends Item
    {
        private ItemIngot()
        {
            this.setUnlocalizedName("newIngot");
            this.setCreativeTab(new CreativeTabs("newore") {
                @Override public Item getTabIconItem() {return Items.iron_ingot;}
            });
            
        }
        
        @Override
        public String getItemStackDisplayName(ItemStack itemStack)
        {
            return condIron.check(itemStack) ? "new_ingot_iron" : condGold.check(itemStack) ? "new_ingot_gold" : "none";
        }

        @Override
        public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
        {
            subItems.add(condIron.createStack());
            subItems.add(condGold.createStack());
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced)
        {
            super.addInformation(stack, playerIn, tooltip, advanced);
            tooltip.addAll(SensitiveOreDict.getNames(stack));
        }
        
        
    }
}
