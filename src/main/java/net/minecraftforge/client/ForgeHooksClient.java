package net.minecraftforge.client;

import static net.minecraftforge.common.ForgeVersion.Status.BETA;
import static net.minecraftforge.common.ForgeVersion.Status.BETA_OUTDATED;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Map;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.BufferUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

@SuppressWarnings("deprecation")
public class ForgeHooksClient
{
    //private static final ResourceLocation ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    static TextureManager engine()
    {
        return FMLClientHandler.instance().getClient().renderEngine;
    }

    public static String getArmorTexture(Entity entity, ItemStack armor, String _default, int slot, String type)
    {
        String result = armor.getItem().getArmorTexture(armor, entity, slot, type);
        return result != null ? result : _default;
    }

    //Optifine Helper Functions u.u, these are here specifically for Optifine
    //Note: When using Optfine, these methods are invoked using reflection, which
    //incurs a major performance penalty.
    public static void orientBedCamera(IBlockAccess world, BlockPos pos, IBlockState state, Entity entity)
    {
        Block block = state.getBlock();

        if (block != null && block.isBed(world, pos, entity))
        {
            glRotatef((float)(block.getBedDirection(world, pos).getHorizontalIndex() * 90), 0.0F, 1.0F, 0.0F);
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

    public static boolean renderFirstPersonHand(RenderGlobal context, float partialTicks, int renderPass)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderHandEvent(context, partialTicks, renderPass));
    }

    public static void onTextureStitchedPre(TextureMap map)
    {
        MinecraftForge.EVENT_BUS.post(new TextureStitchEvent.Pre(map));
        ModelLoader.White.instance.register(map);
    }

    public static void onTextureStitchedPost(TextureMap map)
    {
        MinecraftForge.EVENT_BUS.post(new TextureStitchEvent.Post(map));
    }

    static int renderPass = -1;
    public static void setRenderPass(int pass)
    {
        renderPass = pass;
    }

    static final ThreadLocal<EnumWorldBlockLayer> renderLayer = new ThreadLocal<EnumWorldBlockLayer>()
    {
        protected EnumWorldBlockLayer initialValue()
        {
            return EnumWorldBlockLayer.SOLID;
        }
    };

    public static void setRenderLayer(EnumWorldBlockLayer layer)
    {
        renderLayer.set(layer);
    }

    public static ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int slotID, ModelBiped _default)
    {
        ModelBiped model = itemStack.getItem().getArmorModel(entityLiving, itemStack, slotID, _default);
        return model == null ? _default : model;
    }

    //This properly moves the domain, if provided, to the front of the string before concatenating
    public static String fixDomain(String base, String complex)
    {
        int idx = complex.indexOf(':');
        if (idx == -1)
        {
            return base + complex;
        }

        String name = complex.substring(idx + 1, complex.length());
        if (idx > 1)
        {
            String domain = complex.substring(0, idx);
            return domain + ':' + base + name;
        }
        else
        {
            return base + name;
        }
    }

    public static boolean postMouseEvent()
    {
        return MinecraftForge.EVENT_BUS.post(new MouseEvent());
    }

    public static float getOffsetFOV(EntityPlayer entity, float fov)
    {
        FOVUpdateEvent fovUpdateEvent = new FOVUpdateEvent(entity, fov);
        MinecraftForge.EVENT_BUS.post(fovUpdateEvent);
        return fovUpdateEvent.newfov;
    }
    
    public static float getFOVModifier(EntityRenderer renderer, Entity entity, Block block, double renderPartialTicks, float fov) {
        EntityViewRenderEvent.FOVModifier event = new EntityViewRenderEvent.FOVModifier(renderer, entity, block, renderPartialTicks, fov);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getFOV();
    }

    private static int skyX, skyZ;

    private static boolean skyInit;
    private static int skyRGBMultiplier;

    public static int getSkyBlendColour(World world, BlockPos center)
    {
        if (center.getX() == skyX && center.getZ() == skyZ && skyInit)
        {
            return skyRGBMultiplier;
        }
        skyInit = true;

        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        int[] ranges = ForgeModContainer.blendRanges;
        int distance = 0;
        if (settings.fancyGraphics && settings.renderDistanceChunks >= 0 && settings.renderDistanceChunks < ranges.length)
        {
            distance = ranges[settings.renderDistanceChunks];
        }

        int r = 0;
        int g = 0;
        int b = 0;

        int divider = 0;
        for (int x = -distance; x <= distance; ++x)
        {
            for (int z = -distance; z <= distance; ++z)
            {
                BlockPos pos = center.add(x, 0, z);
                BiomeGenBase biome = world.getBiomeGenForCoords(pos);
                int colour = biome.getSkyColorByTemp(biome.getFloatTemperature(pos));
                r += (colour & 0xFF0000) >> 16;
                g += (colour & 0x00FF00) >> 8;
                b += colour & 0x0000FF;
                divider++;
            }
        }

        int multiplier = (r / divider & 255) << 16 | (g / divider & 255) << 8 | b / divider & 255;

        skyX = center.getX();
        skyZ = center.getY();
        skyRGBMultiplier = multiplier;
        return skyRGBMultiplier;
    }
    /**
     * Initialization of Forge Renderers.
     */
    static
    {
        //FluidRegistry.renderIdFluid = RenderingRegistry.getNextAvailableRenderId();
        //RenderingRegistry.registerBlockHandler(RenderBlockFluid.instance);
    }

    public static void renderMainMenu(GuiMainMenu gui, FontRenderer font, int width, int height)
    {
        Status status = ForgeVersion.getStatus();
        if (status == BETA || status == BETA_OUTDATED)
        {
            // render a warning at the top of the screen,
            String line = I18n.format("forge.update.beta.1", EnumChatFormatting.RED, EnumChatFormatting.RESET);
            gui.drawString(font, line, (width - font.getStringWidth(line)) / 2, 4 + (0 * (font.FONT_HEIGHT + 1)), -1);
            line = I18n.format("forge.update.beta.2");
            gui.drawString(font, line, (width - font.getStringWidth(line)) / 2, 4 + (1 * (font.FONT_HEIGHT + 1)), -1);
        }

        String line = null;
        switch(status)
        {
            //case FAILED:        line = " Version check failed"; break;
            //case UP_TO_DATE:    line = "Forge up to date"}; break;
            //case AHEAD:         line = "Using non-recommended Forge build, issues may arise."}; break;
            case OUTDATED:
            case BETA_OUTDATED: line = I18n.format("forge.update.newversion", ForgeVersion.getTarget()); break;
            default: break;
        }

        if (line != null)
        {
            // if we have a line, render it in the bottom right, above Mojang's copyright line
            gui.drawString(font, line, width - font.getStringWidth(line) - 2, height - (2 * (font.FONT_HEIGHT + 1)), -1);
        }
    }

    public static ISound playSound(SoundManager manager, ISound sound)
    {
        SoundEventAccessorComposite accessor = manager.sndHandler.getSound(sound.getSoundLocation());
        PlaySoundEvent e = new PlaySoundEvent(manager, sound, (accessor == null ? null : accessor.getSoundCategory()));
        MinecraftForge.EVENT_BUS.post(e);
        return e.result;
    }

    //static RenderBlocks worldRendererRB;
    static int worldRenderPass;

    public static int getWorldRenderPass()
    {
        return worldRenderPass;
    }

    public static void drawScreen(GuiScreen screen, int mouseX, int mouseY, float partialTicks)
    {
        if (!MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Pre(screen, mouseX, mouseY, partialTicks)))
            screen.drawScreen(mouseX, mouseY, partialTicks);
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Post(screen, mouseX, mouseY, partialTicks));
    }

    public static float getFogDensity(EntityRenderer renderer, Entity entity, Block block, float partial, float density)
    {
        EntityViewRenderEvent.FogDensity event = new EntityViewRenderEvent.FogDensity(renderer, entity, block, partial, density);
        if (MinecraftForge.EVENT_BUS.post(event)) return event.density;
        return -1;
    }

    public static void onFogRender(EntityRenderer renderer, Entity entity, Block block, float partial, int mode, float distance)
    {
        MinecraftForge.EVENT_BUS.post(new EntityViewRenderEvent.RenderFogEvent(renderer, entity, block, partial, mode, distance));
    }

    /*
    public static void setWorldRendererRB(RenderBlocks renderBlocks)
    {
        worldRendererRB = renderBlocks;
    }

    public static void onPreRenderWorld(WorldRenderer worldRenderer, int pass)
    {
        if(worldRendererRB != null)
        {
            worldRenderPass = pass;
            MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.Pre(worldRenderer, (ChunkCache)worldRendererRB.blockAccess, worldRendererRB, pass));
        }
    }

    public static void onPostRenderWorld(WorldRenderer worldRenderer, int pass)
    {
        if(worldRendererRB != null)
        {
            MinecraftForge.EVENT_BUS.post(new RenderWorldEvent.Post(worldRenderer, (ChunkCache)worldRendererRB.blockAccess, worldRendererRB, pass));
            worldRenderPass = -1;
        }
    }
    */

    public static void onModelBake(ModelManager modelManager, IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, ModelBakery modelBakery)
    {
        ModelLoader loader = (ModelLoader)modelBakery;
        MinecraftForge.EVENT_BUS.post(new ModelBakeEvent(modelManager, modelRegistry, loader));
        loader.onPostBakeEvent(modelRegistry);
    }

	public static Matrix4f getMatrix(ItemTransformVec3f transform)
    {
        javax.vecmath.Matrix4f m = new javax.vecmath.Matrix4f(), t = new javax.vecmath.Matrix4f();
        m.setIdentity();
        m.setTranslation(TRSRTransformation.toVecmath(transform.translation));
        t.setIdentity();
        t.rotY(transform.rotation.y);
        m.mul(t);
        t.setIdentity();
        t.rotX(transform.rotation.x);
        m.mul(t);
        t.setIdentity();
        t.rotZ(transform.rotation.z);
        m.mul(t);
        t.setIdentity();
        t.m00 = transform.scale.x;
        t.m11 = transform.scale.y;
        t.m22 = transform.scale.z;
        m.mul(t);
        return m;
    }

	public static IBakedModel handleCameraTransforms(IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType)
    {
        if(model instanceof IPerspectiveAwareModel)
        {
            Pair<? extends IFlexibleBakedModel, Matrix4f> pair = ((IPerspectiveAwareModel)model).handlePerspective(cameraTransformType);

            if(pair.getRight() != null) multiplyCurrentGlMatrix(pair.getRight());
            return pair.getLeft();
        }
        else
        {
            model.getItemCameraTransforms().applyTransform(cameraTransformType);
        }
        return model;
    }

    private static final FloatBuffer matrixBuf = BufferUtils.createFloatBuffer(16);

    public static void multiplyCurrentGlMatrix(Matrix4f matrix)
    {
        matrixBuf.clear();
        float[] t = new float[4];
        for(int i = 0; i < 4; i++)
        {
            matrix.getColumn(i, t);
            matrixBuf.put(t);
        }
        matrixBuf.flip();
        glMultMatrix(matrixBuf);
    }

    // moved and expanded from WorldVertexBufferUploader.draw

    public static void preDraw(EnumUsage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer)
    {
        VertexFormatElement attr = format.getElement(element);
        int count = attr.getElementCount();
        int constant = attr.getType().getGlConstant();
        buffer.position(format.func_181720_d(element));
        switch(attrType)
        {
            case POSITION:
                glVertexPointer(count, constant, stride, buffer);
                glEnableClientState(GL_VERTEX_ARRAY);
                break;
            case NORMAL:
                if(count != 3)
                {
                    throw new IllegalArgumentException("Normal attribute should have the size 3: " + attr);
                }
                glNormalPointer(constant, stride, buffer);
                glEnableClientState(GL_NORMAL_ARRAY);
                break;
            case COLOR:
                glColorPointer(count, constant, stride, buffer);
                glEnableClientState(GL_COLOR_ARRAY);
                break;
            case UV:
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + attr.getIndex());
                glTexCoordPointer(count, constant, stride, buffer);
                glEnableClientState(GL_TEXTURE_COORD_ARRAY);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                break;
            case PADDING:
                break;
            case GENERIC:
                glEnableVertexAttribArray(attr.getIndex());
                glVertexAttribPointer(attr.getIndex(), count, constant, false, stride, buffer);
            default:
                FMLLog.severe("Unimplemented vanilla attribute upload: %s", attrType.getDisplayName());
        }
    }

    public static void postDraw(EnumUsage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer)
    {
        VertexFormatElement attr = format.getElement(element);
        switch(attrType)
        {
            case POSITION:
                glDisableClientState(GL_VERTEX_ARRAY);
                break;
            case NORMAL:
                glDisableClientState(GL_NORMAL_ARRAY);
                break;
            case COLOR:
                glDisableClientState(GL_COLOR_ARRAY);
                // is this really needed?
                GlStateManager.resetColor();
                break;
            case UV:
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + attr.getIndex());
                glDisableClientState(GL_TEXTURE_COORD_ARRAY);
                OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                break;
            case PADDING:
                break;
            case GENERIC:
                glDisableVertexAttribArray(attr.getIndex());
            default:
                FMLLog.severe("Unimplemented vanilla attribute upload: %s", attrType.getDisplayName());
        }
    }

    public static void transform(org.lwjgl.util.vector.Vector3f vec, Matrix4f m)
    {
        Vector4f tmp = new Vector4f(vec.x, vec.y, vec.z, 1f);
        m.transform(tmp);
        if(Math.abs(tmp.w - 1f) > 1e-5) tmp.scale(1f / tmp.w);
        vec.set(tmp.x, tmp.y, tmp.z);
    }

    public static Matrix4f getMatrix(ModelRotation modelRotation)
    {
        Matrix4f ret = new Matrix4f(TRSRTransformation.toVecmath(modelRotation.getMatrix4d())), tmp = new Matrix4f();
        tmp.setIdentity();
        tmp.m03 = tmp.m13 = tmp.m23 = .5f;
        ret.mul(tmp, ret);
        tmp.invert();
        //tmp.m03 = tmp.m13 = tmp.m23 = -.5f;
        ret.mul(tmp);
        return ret;
    }

    public static void putQuadColor(WorldRenderer renderer, BakedQuad quad, int color)
    {
        float cr = color & 0xFF;
        float cg = (color >>> 8) & 0xFF;
        float cb = (color >>> 16) & 0xFF;
        float ca = (color >>> 24) & 0xFF;
        for(int i = 0; i < 4; i++)
        {
            int vc = quad.getVertexData()[3 + 7 * i];
            float vcr = vc & 0xFF;
            float vcg = (vc >>> 8) & 0xFF;
            float vcb = (vc >>> 16) & 0xFF;
            float vca = (vc >>> 24) & 0xFF;
            int ncr = Math.min(0xFF, (int)(cr * vcr / 0xFF));
            int ncg = Math.min(0xFF, (int)(cg * vcg / 0xFF));
            int ncb = Math.min(0xFF, (int)(cb * vcb / 0xFF));
            int nca = Math.min(0xFF, (int)(ca * vca / 0xFF));
            renderer.putColorRGBA(renderer.getColorIndex(4 - i), ncr, ncg, ncb, nca);
        }
    }

    private static Map<Pair<Item, Integer>, Class<? extends TileEntity>> tileItemMap = Maps.newHashMap();

    public static void renderTileItem(Item item, int metadata)
    {
        Class<? extends TileEntity> tileClass = tileItemMap.get(Pair.of(item, metadata));
        if (tileClass != null)
        {
            TileEntitySpecialRenderer<?> r = TileEntityRendererDispatcher.instance.getSpecialRendererByClass(tileClass);
            if (r != null)
            {
                r.renderTileEntityAt(null, 0, 0, 0, 0, -1);
            }
        }
    }

    /**
     * @deprecated Will be removed as soon as possible, hopefully 1.9.
     */
    @Deprecated
    public static void registerTESRItemStack(Item item, int metadata, Class<? extends TileEntity> TileClass)
    {
        tileItemMap.put(Pair.of(item, metadata), TileClass);
    }

    /**
     * internal, relies on fixed format of FaceBakery
     */
    public static void fillNormal(int[] faceData, EnumFacing facing)
    {
        Vector3f v1 = new Vector3f(faceData[3 * 7 + 0], faceData[3 * 7 + 1], faceData[3 * 7 + 2]);
        Vector3f t = new Vector3f(faceData[1 * 7 + 0], faceData[1 * 7 + 1], faceData[1 * 7 + 2]);
        Vector3f v2 = new Vector3f(faceData[2 * 7 + 0], faceData[2 * 7 + 1], faceData[2 * 7 + 2]);
        v1.sub(t);
        t.set(faceData[0 * 7 + 0], faceData[0 * 7 + 1], faceData[0 * 7 + 2]);
        v2.sub(t);
        v1.cross(v2, v1);
        v1.normalize();

        int x = ((byte)(v1.x * 127)) & 0xFF;
        int y = ((byte)(v1.y * 127)) & 0xFF;
        int z = ((byte)(v1.z * 127)) & 0xFF;
        for(int i = 0; i < 4; i++)
        {
            faceData[i * 7 + 6] = x | (y << 0x08) | (z << 0x10);
        }
    }

    public static Optional<TRSRTransformation> applyTransform(ItemTransformVec3f transform, Optional<? extends IModelPart> part)
    {
        if(part.isPresent()) return Optional.absent();
        return Optional.of(new TRSRTransformation(transform));
    }

    public static Optional<TRSRTransformation> applyTransform(Matrix4f matrix, Optional<? extends IModelPart> part)
    {
        if(part.isPresent()) return Optional.absent();
        return Optional.of(new TRSRTransformation(matrix));
    }

    public static void loadEntityShader(Entity entity, EntityRenderer entityRenderer)
    {
        if (entity != null)
        {
            ResourceLocation shader = ClientRegistry.getEntityShader(entity.getClass());
            if (shader != null)
            {
                entityRenderer.loadShader(shader);
            }
        }
    }
}
