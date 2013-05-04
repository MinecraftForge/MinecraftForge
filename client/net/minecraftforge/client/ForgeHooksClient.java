package net.minecraftforge.client;

import java.util.HashMap;
import java.util.Random;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.texturepacks.ITexturePack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureLoadEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraftforge.common.MinecraftForge;
import static net.minecraftforge.client.IItemRenderer.ItemRenderType.*;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.*;

public class ForgeHooksClient
{
    static RenderEngine engine()
    {
        return FMLClientHandler.instance().getClient().renderEngine;
    }

    @Deprecated //Deprecated in 1.5.1, move to the more detailed one below.
    @SuppressWarnings("deprecation")
    public static String getArmorTexture(ItemStack armor, String _default)
    {
        String result = null;
        if (armor.getItem() instanceof IArmorTextureProvider)
        {
            result = ((IArmorTextureProvider)armor.getItem()).getArmorTextureFile(armor);
        }
        return result != null ? result : _default;
    }

    public static String getArmorTexture(Entity entity, ItemStack armor, String _default, int slot, int layer)
    {
        String result = armor.getItem().getArmorTexture(armor, entity, slot, layer);
        return result != null ? result : _default;
    }

    public static boolean renderEntityItem(EntityItem entity, ItemStack item, float bobing, float rotation, Random random, RenderEngine engine, RenderBlocks renderBlocks)
    {
        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, ENTITY);
        if (customRenderer == null)
        {
            return false;
        }

        if (customRenderer.shouldUseRenderHelper(ENTITY, item, ENTITY_ROTATION))
        {
            GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
        }
        if (!customRenderer.shouldUseRenderHelper(ENTITY, item, ENTITY_BOBBING))
        {
            GL11.glTranslatef(0.0F, -bobing, 0.0F);
        }
        boolean is3D = customRenderer.shouldUseRenderHelper(ENTITY, item, BLOCK_3D);

        engine.bindTexture(item.getItemSpriteNumber() == 0 ? "/terrain.png" : "/gui/items.png");
        if (is3D || (item.itemID < Block.blocksList.length && RenderBlocks.renderItemIn3d(Block.blocksList[item.itemID].getRenderType())))
        {
            int renderType = (item.itemID < Block.blocksList.length ? Block.blocksList[item.itemID].getRenderType() : 1);
            float scale = (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2 ? 0.5F : 0.25F);

            if (RenderItem.renderInFrame)
            {
                GL11.glScalef(1.25F, 1.25F, 1.25F);
                GL11.glTranslatef(0.0F, 0.05F, 0.0F);
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            GL11.glScalef(scale, scale, scale);
            
            int size = item.stackSize;
            int count = (size > 20 ? 4 : (size > 5 ? 3 : (size > 1 ? 2 : 1)));

            for(int j = 0; j < count; j++)
            {
                GL11.glPushMatrix();
                if (j > 0)
                {
                    GL11.glTranslatef(
                        ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / 0.5F,
                        ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / 0.5F,
                        ((random.nextFloat() * 2.0F - 1.0F) * 0.2F) / 0.5F);
                }
                customRenderer.renderItem(ENTITY, item, renderBlocks, entity);
                GL11.glPopMatrix();
            }
        }
        else
        {
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            customRenderer.renderItem(ENTITY, item, renderBlocks, entity);
        }
        return true;
    }

    public static boolean renderInventoryItem(RenderBlocks renderBlocks, RenderEngine engine, ItemStack item, boolean inColor, float zLevel, float x, float y)
    {
        IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(item, INVENTORY);
        if (customRenderer == null)
        {
            return false;
        }

        engine.bindTexture(item.getItemSpriteNumber() == 0 ? "/terrain.png" : "/gui/items.png");
        if (customRenderer.shouldUseRenderHelper(INVENTORY, item, INVENTORY_BLOCK))
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(x - 2, y + 3, -3.0F + zLevel);
            GL11.glScalef(10F, 10F, 10F);
            GL11.glTranslatef(1.0F, 0.5F, 1.0F);
            GL11.glScalef(1.0F, 1.0F, -1F);
            GL11.glRotatef(210F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F);

            if(inColor)
            {
                int color = Item.itemsList[item.itemID].getColorFromItemStack(item, 0);
                float r = (float)(color >> 16 & 0xff) / 255F;
                float g = (float)(color >> 8 & 0xff) / 255F;
                float b = (float)(color & 0xff) / 255F;
                GL11.glColor4f(r, g, b, 1.0F);
            }

            GL11.glRotatef(-90F, 0.0F, 1.0F, 0.0F);
            renderBlocks.useInventoryTint = inColor;
            customRenderer.renderItem(INVENTORY, item, renderBlocks);
            renderBlocks.useInventoryTint = true;
            GL11.glPopMatrix();
        }
        else
        {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, -3.0F + zLevel);

            if (inColor)
            {
                int color = Item.itemsList[item.itemID].getColorFromItemStack(item, 0);
                float r = (float)(color >> 16 & 255) / 255.0F;
                float g = (float)(color >> 8 & 255) / 255.0F;
                float b = (float)(color & 255) / 255.0F;
                GL11.glColor4f(r, g, b, 1.0F);
            }

            customRenderer.renderItem(INVENTORY, item, renderBlocks);
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        return true;
    }
    
    @Deprecated
    public static void renderEquippedItem(IItemRenderer customRenderer, RenderBlocks renderBlocks, EntityLiving entity, ItemStack item)
    {
        renderEquippedItem(ItemRenderType.EQUIPPED, customRenderer, renderBlocks, entity, item);
    }

    public static void renderEquippedItem(ItemRenderType type, IItemRenderer customRenderer, RenderBlocks renderBlocks, EntityLiving entity, ItemStack item)
    {
        if (customRenderer.shouldUseRenderHelper(type, item, EQUIPPED_BLOCK))
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            customRenderer.renderItem(type, item, renderBlocks, entity);
            GL11.glPopMatrix();
        }
        else
        {
            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glTranslatef(0.0F, -0.3F, 0.0F);
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
            customRenderer.renderItem(type, item, renderBlocks, entity);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }

    //Optifine Helper Functions u.u, these are here specifically for Optifine
    //Note: When using Optfine, these methods are invoked using reflection, which
    //incurs a major performance penalty.
    public static void orientBedCamera(Minecraft mc, EntityLiving entity)
    {
        int x = MathHelper.floor_double(entity.posX);
        int y = MathHelper.floor_double(entity.posY);
        int z = MathHelper.floor_double(entity.posZ);
        Block block = Block.blocksList[mc.theWorld.getBlockId(x, y, z)];

        if (block != null && block.isBed(mc.theWorld, x, y, z, entity))
        {
            int var12 = block.getBedDirection(mc.theWorld, x, y, z);
            GL11.glRotatef((float)(var12 * 90), 0.0F, 1.0F, 0.0F);
        }
    }

    public static boolean onDrawBlockHighlight(RenderGlobal context, EntityPlayer player, MovingObjectPosition target, int subID, ItemStack currentItem, float partialTicks)
    {
        return MinecraftForge.EVENT_BUS.post(new DrawBlockHighlightEvent(context, player, target, subID, currentItem, partialTicks));
    }

    public static void dispatchRenderLast(RenderGlobal context, float partialTicks)
    {
        MinecraftForge.EVENT_BUS.post(new RenderWorldLastEvent(context, partialTicks));
    }

    public static void onTextureLoad(String texture, ITexturePack pack)
    {
        MinecraftForge.EVENT_BUS.post(new TextureLoadEvent(texture, pack));
    }

    public static void onTextureStitchedPre(TextureMap map)
    {
        MinecraftForge.EVENT_BUS.post(new TextureStitchEvent.Pre(map));
    }

    public static void onTextureStitchedPost(TextureMap map)
    {
        MinecraftForge.EVENT_BUS.post(new TextureStitchEvent.Post(map));
    }

    /**
     * This is added for Optifine's convenience. And to explode if a ModMaker is developing.
     * @param texture
     */
    public static void onTextureLoadPre(String texture)
    {
        if (Tessellator.renderingWorldRenderer)
        {
            String msg = String.format("Warning: Texture %s not preloaded, will cause render glitches!", texture);
            System.out.println(msg);
            if (Tessellator.class.getPackage() != null)
            {
                if (Tessellator.class.getPackage().getName().startsWith("net.minecraft."))
                {
                    Minecraft mc = FMLClientHandler.instance().getClient();
                    if (mc.ingameGUI != null)
                    {
                        mc.ingameGUI.getChatGUI().printChatMessage(msg);
                    }
                }
            }
        }
    }

    static int renderPass = -1;
    public static void setRenderPass(int pass)
    {
        renderPass = pass;
    }

    public static ModelBiped getArmorModel(EntityLiving entityLiving, ItemStack itemStack, int slotID, ModelBiped _default)
    {
        ModelBiped modelbiped = itemStack.getItem().getArmorModel(entityLiving, itemStack, slotID);
        return modelbiped == null ? _default : modelbiped;
    }
}
