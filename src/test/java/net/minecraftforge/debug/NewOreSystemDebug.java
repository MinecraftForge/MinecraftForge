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



    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ItemCondition cond = new ItemCondition.Builder(Items.stick).addCondition("aint", NBTEvidence.INT, 1).build();
        
        GameRegistry.addRecipe(cond.createStack(), "XXX", 'X', Items.stick);
        
//        SensitiveOreDict.registerSensitiveOre("int1", cond);
        
        for (ItemCondition condition : SensitiveOreDict.getOres("ingotGold"))
        {
            System.out.println(condition);
        }
    }
    
}
