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
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.texturepacks.ITexturePack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureLoadEvent;
import net.minecraftforge.common.IArmorTextureProvider;
import net.minecraftforge.common.MinecraftForge;
import static net.minecraftforge.client.IItemRenderer.ItemRenderType.*;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.*;

public class ForgeHooksClient
{
    private static class TesKey implements Comparable<TesKey>
    {
        public final int texture, subid;
        public TesKey(int textureID, int subID)
        {
            texture = textureID;
            subid = subID;
        }

        public int compareTo(TesKey key)
        {
            if (subid == key.subid)
            {
                return texture - key.texture;
            }
            return subid - key.subid;
        }

        public boolean equals(Object obj)
        {
            return compareTo((TesKey)obj) == 0;
        }

        public int hashCode()
        {
            return texture + 31 * subid;
        }
    }

    public static HashMap<TesKey, Tessellator> tessellators = new HashMap<TesKey, Tessellator>();
    public static HashMap<String, Integer> textures = new HashMap<String, Integer>();
    public static TreeSet<TesKey> renderTextures = new TreeSet<TesKey>();
    public static Tessellator defaultTessellator = null;
    public static boolean inWorld = false;
    public static HashMap<TesKey, IRenderContextHandler> renderHandlers = new HashMap<TesKey, IRenderContextHandler>();
    public static IRenderContextHandler unbindContext = null;

    protected static void registerRenderContextHandler(String texture, int subID, IRenderContextHandler handler)
    {
        Integer texID = textures.get(texture);
        if (texID == null)
        {
            texID = engine().getTexture(texture);
            textures.put(texture, texID);
        }
        renderHandlers.put(new TesKey(texID, subID), handler);
    }

    static RenderEngine engine()
    {
        return FMLClientHandler.instance().getClient().renderEngine;
    }
    public static void bindTexture(String texture, int subID)
    {
        Integer texID = textures.get(texture);
        if (texID == null)
        {
            texID = engine().getTexture(texture);
            textures.put(texture, texID);
        }
        if (!inWorld)
        {
            if (unbindContext != null)
            {
                unbindContext.afterRenderContext();
                unbindContext = null;
            }
            if (Tessellator.instance.isDrawing)
            {
                int mode = Tessellator.instance.drawMode;
                Tessellator.instance.draw();
                Tessellator.instance.startDrawing(mode);
            }
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
            unbindContext = renderHandlers.get(new TesKey(texID, subID));
            if (unbindContext != null)
            {
                unbindContext.beforeRenderContext();
            }
            return;
        }
        bindTessellator(texID, subID);
    }

    public static void unbindTexture()
    {
        if (inWorld)
        {
            Tessellator.instance = defaultTessellator;
        }
        else
        {
            if (Tessellator.instance.isDrawing)
            {
                int mode = Tessellator.instance.drawMode;
                Tessellator.instance.draw();
                if (unbindContext != null)
                {
                    unbindContext.afterRenderContext();
                    unbindContext = null;
                }
                Tessellator.instance.startDrawing(mode);
            }
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, engine().getTexture("/terrain.png"));
            return;
        }
    }

    protected static void bindTessellator(int texture, int subID)
    {
        TesKey key = new TesKey(texture, subID);
        Tessellator tess = tessellators.get(key);

        if (tess == null)
        {
            tess = new Tessellator();
            tess.textureID = texture;
            tessellators.put(key, tess);
        }

        if (inWorld && !renderTextures.contains(key))
        {
            renderTextures.add(key);
            tess.startDrawingQuads();
            tess.setTranslation(defaultTessellator.xOffset, defaultTessellator.yOffset, defaultTessellator.zOffset);
        }

        Tessellator.instance = tess;
    }

    static int renderPass = -1;
    public static void beforeRenderPass(int pass)
    {
        renderPass = pass;
        defaultTessellator = Tessellator.instance;
        Tessellator.renderingWorldRenderer = true;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, engine().getTexture("/terrain.png"));
        renderTextures.clear();
        inWorld = true;
    }

    public static void afterRenderPass(int pass)
    {
        renderPass = -1;
        inWorld = false;
        for (TesKey info : renderTextures)
        {
            IRenderContextHandler handler = renderHandlers.get(info);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, info.texture);
            Tessellator tess = tessellators.get(info);
            if (handler == null)
            {
                tess.draw();
            }
            else
            {
                Tessellator.instance = tess;
                handler.beforeRenderContext();
                tess.draw();
                handler.afterRenderContext();
            }
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, engine().getTexture("/terrain.png"));
        Tessellator.renderingWorldRenderer = false;
        Tessellator.instance = defaultTessellator;
    }

    public static void beforeBlockRender(Block block, RenderBlocks render)
    {
        if (!block.isDefaultTexture && render.overrideBlockTexture == -1)
        {
            bindTexture(block.getTextureFile(), 0);
        }
    }

    public static void afterBlockRender(Block block, RenderBlocks render)
    {
        if (!block.isDefaultTexture && render.overrideBlockTexture == -1)
        {
            unbindTexture();
        }
    }

    public static String getArmorTexture(ItemStack armor, String _default)
    {
        if (armor.getItem() instanceof IArmorTextureProvider)
        {
            return ((IArmorTextureProvider)armor.getItem()).getArmorTextureFile(armor);
        }
        return _default;
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

        if (item.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[item.itemID].getRenderType())))
        {
            engine.bindTexture(engine.getTexture(item.getItem().getTextureFile()));
            int renderType = Block.blocksList[item.itemID].getRenderType();
            float scale = (renderType == 1 || renderType == 19 || renderType == 12 || renderType == 2 ? 0.5F : 0.25F);

            if (RenderItem.field_82407_g)
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
                engine.bindTexture(engine.getTexture(item.getItem().getTextureFile()));
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

        engine.bindTexture(engine.getTexture(Item.itemsList[item.itemID].getTextureFile()));
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

    public static void renderEquippedItem(IItemRenderer customRenderer, RenderBlocks renderBlocks, EntityLiving entity, ItemStack item)
    {
        if (customRenderer.shouldUseRenderHelper(EQUIPPED, item, EQUIPPED_BLOCK))
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            customRenderer.renderItem(EQUIPPED, item, renderBlocks, entity);
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
            customRenderer.renderItem(EQUIPPED, item, renderBlocks, entity);
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

    public static void setRenderPass(int pass)
    {
        renderPass = pass;
    }
}
