package net.minecraftforge.debug;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.debug.ModelBakeEventDebug.BakeEventHandler;
import net.minecraftforge.debug.ModelBakeEventDebug.CommonProxy;
import net.minecraftforge.debug.ModelBakeEventDebug.CustomModelBlock;
import net.minecraftforge.debug.ModelBakeEventDebug.CustomTileEntity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = GlStateManagerFixTest.MODID, version = GlStateManagerFixTest.VERSION)
public class GlStateManagerFixTest {
    public static final String MODID = "GlStateManagerFixTest";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) 
    {
        MinecraftForge.EVENT_BUS.register(new RenderLast()); 
    }

    public static class RenderLast
    {
        static boolean doneOnce = false;
        @SubscribeEvent
        public void renderLast(RenderWorldLastEvent event) 
        {
            if (doneOnce) return;
            doneOnce = true;
            GlStateManager.enableLighting(); 
            System.out.println("GlStateManager.enableLighting();");
            System.out.println("  Lighting bit:" + GL11.glIsEnabled(GL11.GL_LIGHTING));
            
            GlStateManager.pushAttrib(); 
            System.out.println("GlStateManager.pushAttrib();");
            System.out.println("  Lighting bit:" + GL11.glIsEnabled(GL11.GL_LIGHTING));
            
            GlStateManager.disableLighting(); 
            System.out.println("GlStateManager.disableLighting();");
            System.out.println("  Lighting bit:" + GL11.glIsEnabled(GL11.GL_LIGHTING));
            GlStateManager.popAttrib(); 
            System.out.println("GlStateManager.popAttrib();");
            System.out.println("  Lighting bit:" + GL11.glIsEnabled(GL11.GL_LIGHTING));
            
            GlStateManager.disableLighting(); 
            System.out.println("GlStateManager.disableLighting();");
            System.out.println("  Lighting bit:" + GL11.glIsEnabled(GL11.GL_LIGHTING));
            System.out.println(GL11.glIsEnabled(GL11.GL_LIGHTING) ? "failure" : "success");
            
        }    
    }   
}

