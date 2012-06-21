/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.Item;
import net.minecraft.src.ModLoader;
import net.minecraft.src.Packet100OpenWindow;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.SoundPoolEntry;
import net.minecraft.src.Tessellator;
import net.minecraft.src.RenderGlobal;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.ItemStack;
import net.minecraft.src.WorldClient;

import org.lwjgl.opengl.GL11;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import net.minecraft.src.*;
import net.minecraft.src.forge.packets.ForgePacket;

import org.lwjgl.opengl.GL12;

import static net.minecraft.src.forge.IItemRenderer.ItemRenderType.*;
import static net.minecraft.src.forge.IItemRenderer.ItemRendererHelper.*;

public class ForgeHooksClient
{
    private static Field textureID = null;
    private static boolean textureIDChecked = false;
    public static boolean enable4096 = false; //If the server has told us that 4096 is enabled.
    
    public static boolean onBlockHighlight(RenderGlobal render, EntityPlayer player, MovingObjectPosition target, int i, ItemStack itemstack, float partialTicks)
    {
        for (IHighlightHandler handler : highlightHandlers)
        {
            if (handler.onBlockHighlight(render, player, target, i, itemstack, partialTicks))
            {
                return true;
            }
        }
        return false;
    }

    public static void onRenderWorldLast(RenderGlobal render, float partialTicks)
    {
        for (IRenderWorldLastHandler handler : renderWorldLastHandlers)
        {
            handler.onRenderWorldLast(render, partialTicks);
        }
    }

    public static LinkedList<IHighlightHandler> highlightHandlers = new LinkedList<IHighlightHandler>();
    public static LinkedList<IRenderWorldLastHandler> renderWorldLastHandlers = new LinkedList<IRenderWorldLastHandler>();
    
    public static void onTextureLoad(String textureName, int textureID)
    {
        for (ITextureLoadHandler handler: textureLoadHandlers)
        {
            handler.onTextureLoad(textureName, textureID);
        }
    }
    public static LinkedList<ITextureLoadHandler> textureLoadHandlers = new LinkedList<ITextureLoadHandler>();

    public static boolean canRenderInPass(Block block, int pass)
    {
        if (block instanceof IMultipassRender)
        {
            return ((IMultipassRender)block).canRenderInPass(pass);
        }
        if (pass == block.getRenderBlockPass())
        {
            return true;
        }
        return false;
    }

    private static class TesKey implements Comparable<TesKey>
    {
        public TesKey(int textureID, int subID)
        {
            tex = textureID;
            sub = subID;
        }
        public int compareTo(TesKey key)
        {
            if (sub == key.sub)
            {
                return tex - key.tex;
            }
            return sub - key.sub;
        }
        public boolean equals(Object obj)
        {
            return compareTo((TesKey)obj) == 0;
        }
        public int hashCode()
        {
            int c1 = Integer.valueOf(tex).hashCode();
            int c2 = Integer.valueOf(sub).hashCode();
            return c1 + 31 * c2;
        }
        public int tex, sub;
    }

    public static HashMap<TesKey, Tessellator> tessellators = new HashMap<TesKey, Tessellator>();
    public static HashMap<String, Integer> textures = new HashMap<String, Integer>();
    public static boolean inWorld = false;
    public static TreeSet<TesKey> renderTextures = new TreeSet<TesKey>();
    public static Tessellator defaultTessellator = null;
    public static HashMap<TesKey, IRenderContextHandler> renderHandlers = new HashMap<TesKey, IRenderContextHandler>();

    protected static void registerRenderContextHandler(String texture, int subID, IRenderContextHandler handler)
    {
        Integer texID = textures.get(texture);
        if (texID == null)
        {
            texID = ModLoader.getMinecraftInstance().renderEngine.getTexture(texture);
            textures.put(texture, texID);
        }
        renderHandlers.put(new TesKey(texID, subID), handler);
    }

    protected static void bindTessellator(int texture, int subID)
    {
        TesKey key = new TesKey(texture, subID);
        Tessellator tess = tessellators.get(key);
        if (tess == null)
        {
            tess = new Tessellator();
            //Hack around for waiting for Optifine to implement the feature he requested.
            //Should make it not cause while we wait for him to update.
            if (!textureIDChecked && textureID == null)
            {
                textureIDChecked = true;
                try 
                {
                    textureID = Tessellator.class.getField("textureID");
                }
                catch (NoSuchFieldException ex){}
            }
            if (textureID != null)
            {
                tess.textureID = texture;
            }
            //End Hack
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

    public static IRenderContextHandler unbindContext = null;
    protected static void bindTexture(String texture, int subID)
    {
        Integer texID = textures.get(texture);
        if (texID == null)
        {
            texID = ModLoader.getMinecraftInstance().renderEngine.getTexture(texture);
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

    protected static void unbindTexture()
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
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture("/terrain.png"));
            return;
        }
    }

    static int renderPass = -1;
    public static void beforeRenderPass(int pass)
    {
        renderPass = pass;
        defaultTessellator = Tessellator.instance;
        Tessellator.renderingWorldRenderer = true;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture("/terrain.png"));
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
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, info.tex);
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
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture("/terrain.png"));
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

    public static void overrideTexture(Object obj)
    {
        if (obj instanceof ITextureProvider)
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ModLoader.getMinecraftInstance().renderEngine.getTexture(((ITextureProvider)obj).getTextureFile()));
        }
    }

    public static String getTexture(String def, Object obj)
    {
        if (obj instanceof ITextureProvider)
        {
            return ((ITextureProvider)obj).getTextureFile();
        }
        else
        {
            return def;
        }
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

            GL11.glScalef(scale, scale, scale);
            int size = entity.item.stackSize;
            int count = (size > 20 ? 4 : (size > 5 ? 3 : (size > 1 ? 2 : 1)));
            
            for(int j = 0; j < size; j++)
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
                int color = Item.itemsList[item.itemID].getColorFromDamage(item.getItemDamage(), 0);
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
                int color = Item.itemsList[item.itemID].getColorFromDamage(item.getItemDamage(), 0);
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
    
    /**
     * Trys to get the class for the specified name, will also try the 
     * net.minecraft.src package in case we are in MCP
     * Returns null if not found.
     * 
     * @param name The class name
     * @return The Class, or null if not found
     */
    private static Class getClass(String name)
    {
        try 
        {
            return Class.forName(name);
        }
        catch (Exception e)
        {
            try
            {
                return Class.forName("net.minecraft.src." + name);
            }
            catch (Exception e2)
            {
                return null;
            }
        }
    }

    public static LinkedList<ISoundHandler> soundHandlers = new LinkedList<ISoundHandler>();
    public static LinkedList<ISoundHandler> soundHandlers2 = new LinkedList<ISoundHandler>();
    public static void onSetupAudio(SoundManager soundManager) 
    {
        for (ISoundHandler handler : soundHandlers)
        {
            handler.onSetupAudio(soundManager);
        }
    }

    public static void onLoadSoundSettings(SoundManager soundManager) 
    {
        for (ISoundHandler handler : soundHandlers)
        {
            handler.onLoadSoundSettings(soundManager);
        }
    }

    public static SoundPoolEntry onPlayBackgroundMusic(SoundManager soundManager, SoundPoolEntry entry) 
    {
        for (ISoundHandler handler : soundHandlers)
        {
            entry = handler.onPlayBackgroundMusic(soundManager, entry);
            if (entry == null)
            {
                return null;
            }
        }
        return entry;
    }

    public static SoundPoolEntry onPlayStreaming(SoundManager soundManager, SoundPoolEntry entry, String soundName, float x, float y, float z)
    {
        for (ISoundHandler handler : soundHandlers)
        {
            entry = handler.onPlayStreaming(soundManager, entry, soundName, x, y, z);
            if (entry == null)
            {
                return null;
            }
        }
        return entry;
    }

    public static SoundPoolEntry onPlaySound(SoundManager soundManager, SoundPoolEntry entry, String soundName, float x, float y, float z, float volume, float pitch)
    {
        for (ISoundHandler handler : soundHandlers)
        {
            entry = handler.onPlaySound(soundManager, entry, soundName, x, y, z, volume, pitch);
            if (entry == null)
            {
                return null;
            }
        }
        return entry;
    }

    public static SoundPoolEntry onPlaySoundEffect(SoundManager soundManager, SoundPoolEntry entry, String soundName, float volume, float pitch) 
    {
        for (ISoundHandler handler : soundHandlers)
        {
            entry = handler.onPlaySoundEffect(soundManager, entry, soundName, volume, pitch);
            if (entry == null)
            {
                return null;
            }
        }
        return entry;
    }

    public static String onPlaySoundAtEntity(Entity entity, String soundName, float volume, float pitch)
    {
        MinecraftForgeClient.checkMinecraftVersion("Minecraft Minecraft 1.2.5", "Interface check in onPlaySoundAtEntity, remove it Mods should be updated");
        for (ISoundHandler handler : soundHandlers2)
        {
            soundName = handler.onPlaySoundAtEntity(entity, soundName,volume, pitch);
            if (soundName == null)
            {
                return null;
            }
        }
        return soundName;
    }
    
    public static void onLogin(Packet1Login login, NetClientHandler net, NetworkManager netManager)
    {
        ForgeHooks.onLogin(netManager, login);
            
        String[] channels = MessageManager.getInstance().getRegisteredChannels(netManager);
        StringBuilder tmp = new StringBuilder();
        tmp.append("Forge");
        for(String channel : channels)
        {
            tmp.append("\0");
            tmp.append(channel);
        }
        Packet250CustomPayload pkt = new Packet250CustomPayload(); 
        pkt.channel = "REGISTER";
        try 
        {
            pkt.data = tmp.toString().getBytes("UTF8");
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        }
        pkt.length = pkt.data.length;
        net.addToSendQueue(pkt);
    }

    /**
     * We use some of the unused fields in Packet 001 Login to identify the user as having Forge installed.
     * This allows modded clients to connect to Vanilla server without crashing.
     * It also allows unmodded clients to connect to Forge server without crashing. 
     * Its a bit of a dirty hack, but it doesn't interrupt the login flow, and its unused data.
     * The C->S serverMode is set to the hash code of the string "Forge", this should provide a fairly unique 
     * identifier so we are certain it is not random, and it is Forge installed.
     * The C->S dimension is set to the current Forge build number, in case we need to do any quick version checks.
     */
    public static Packet onSendLogin(Packet1Login pkt)
    {
        enable4096 = false; //Disable 4096 packet modification untill the server says yes.
        pkt.serverMode    = ForgePacket.FORGE_ID;
        pkt.field_48170_e = ForgeHooks.buildVersion;
        return pkt;
    }
    
    public static void onCustomPayload(Packet250CustomPayload pkt, NetworkManager net)
    {
        MessageManager inst = MessageManager.getInstance();
        if (pkt.channel.equals("REGISTER")) 
        {
            try 
            {
                String channels = new String(pkt.data, "UTF8");
                for (String channel : channels.split("\0")) 
                {
                    inst.addActiveChannel(net, channel);
                }
            } 
            catch (UnsupportedEncodingException ex) 
            {
                ModLoader.throwException("ForgeHooksClient.onCustomPayload", ex);
            }
        } 
        else if (pkt.channel.equals("UNREGISTER")) 
        {
            try 
            {
                String channels = new String(pkt.data, "UTF8");
                for (String channel : channels.split("\0")) 
                {
                    inst.removeActiveChannel(net, channel);
                }
            }
            catch (UnsupportedEncodingException ex) 
            {
                ModLoader.throwException("ForgeHooksClient.onCustomPayload", ex);
            }
        } 
        else 
        {
            inst.dispatchIncomingMessage(net, pkt.channel, pkt.data);
        }
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
                if (Tessellator.class.getPackage().equals("net.minecraft.src"))
                {
                    Minecraft mc = ModLoader.getMinecraftInstance();
                    if (mc.ingameGUI != null)
                    {
                        mc.ingameGUI.addChatMessage(msg);
                    }
                }
            }
        }
    }
}
