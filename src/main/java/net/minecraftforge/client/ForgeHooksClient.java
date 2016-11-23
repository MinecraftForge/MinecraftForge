/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.client;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.BOSSINFO;
import static net.minecraftforge.common.ForgeVersion.Status.BETA;
import static net.minecraftforge.common.ForgeVersion.Status.BETA_OUTDATED;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfoLerping;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.ITransformation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiJava8Error;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Java8VersionException;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.BufferUtils;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class ForgeHooksClient
{
    //private static final ResourceLocation ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    static TextureManager engine()
    {
        return FMLClientHandler.instance().getClient().renderEngine;
    }

    public static String getArmorTexture(Entity entity, ItemStack armor, String _default, EntityEquipmentSlot slot, String type)
    {
        String result = armor.getItem().getArmorTexture(armor, entity, slot, type);
        return result != null ? result : _default;
    }

    //Optifine Helper Functions u.u, these are here specifically for Optifine
    //Note: When using Optifine, these methods are invoked using reflection, which
    //incurs a major performance penalty.
    public static void orientBedCamera(IBlockAccess world, BlockPos pos, IBlockState state, Entity entity)
    {
        Block block = state.getBlock();

        if (block != null && block.isBed(state, world, pos, entity))
        {
            glRotatef((float)(block.getBedDirection(state, world, pos).getHorizontalIndex() * 90), 0.0F, 1.0F, 0.0F);
        }
    }

    public static boolean onDrawBlockHighlight(RenderGlobal context, EntityPlayer player, RayTraceResult target, int subID, float partialTicks)
    {
        return MinecraftForge.EVENT_BUS.post(new DrawBlockHighlightEvent(context, player, target, subID, partialTicks));
    }

    public static void dispatchRenderLast(RenderGlobal context, float partialTicks)
    {
        MinecraftForge.EVENT_BUS.post(new RenderWorldLastEvent(context, partialTicks));
    }

    public static boolean renderFirstPersonHand(RenderGlobal context, float partialTicks, int renderPass)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderHandEvent(context, partialTicks, renderPass));
    }

    public static boolean renderSpecificFirstPersonHand(EnumHand hand, float partialTicks, float interpPitch, float swingProgress, float equipProgress, ItemStack stack)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderSpecificHandEvent(hand, partialTicks, interpPitch, swingProgress, equipProgress, stack));
    }

    public static void onTextureStitchedPre(TextureMap map)
    {
        MinecraftForge.EVENT_BUS.post(new TextureStitchEvent.Pre(map));
        ModelLoader.White.INSTANCE.register(map);
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

    static final ThreadLocal<BlockRenderLayer> renderLayer = new ThreadLocal<BlockRenderLayer>();

    public static void setRenderLayer(BlockRenderLayer layer)
    {
        renderLayer.set(layer);
    }

    public static ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped _default)
    {
        ModelBiped model = itemStack.getItem().getArmorModel(entityLiving, itemStack, slot, _default);
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
        return fovUpdateEvent.getNewfov();
    }

    public static float getFOVModifier(EntityRenderer renderer, Entity entity, IBlockState state, double renderPartialTicks, float fov) {
        EntityViewRenderEvent.FOVModifier event = new EntityViewRenderEvent.FOVModifier(renderer, entity, state, renderPartialTicks, fov);
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
                Biome biome = world.getBiome(pos);
                int colour = biome.getSkyColorByTemp(biome.getFloatTemperature(pos));
                r += (colour & 0xFF0000) >> 16;
                g += (colour & 0x00FF00) >> 8;
                b += colour & 0x0000FF;
                divider++;
            }
        }

        int multiplier = (r / divider & 255) << 16 | (g / divider & 255) << 8 | b / divider & 255;

        skyX = center.getX();
        skyZ = center.getZ();
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

    private static int updatescrollcounter = 0;
    public static String renderMainMenu(GuiMainMenu gui, FontRenderer font, int width, int height, String splashText)
    {
        Status status = ForgeVersion.getStatus();
        if (status == BETA || status == BETA_OUTDATED)
        {
            // render a warning at the top of the screen,
            String line = I18n.format("forge.update.beta.1", TextFormatting.RED, TextFormatting.RESET);
            gui.drawString(font, line, (width - font.getStringWidth(line)) / 2, 4 + (0 * (font.FONT_HEIGHT + 1)), -1);
            line = I18n.format("forge.update.beta.2");
            gui.drawString(font, line, (width - font.getStringWidth(line)) / 2, 4 + (1 * (font.FONT_HEIGHT + 1)), -1);
        }

        if (!Loader.instance().java8)
        {
            String line = I18n.format("fml.messages.java8warning.1", TextFormatting.RED, TextFormatting.RESET);
            gui.drawString(font, line, (width - font.getStringWidth(line)) / 2, 4 + (8 * (font.FONT_HEIGHT + 1)), -1);
            line = I18n.format("fml.messages.java8warning.2");
            gui.drawString(font, line, (width - font.getStringWidth(line)) / 2, 4 + (9 * (font.FONT_HEIGHT + 1)), -1);
            splashText = updatescrollcounter < 50 ? "UPDATE!" : "JAVA!";
            updatescrollcounter+=1;
            updatescrollcounter%=100;
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

        return splashText;
    }

    public static void mainMenuMouseClick(int mouseX, int mouseY, int mouseButton, FontRenderer font, int width)
    {
        if (!Loader.instance().java8)
        {
            if (mouseY >= (4 + (8 * 10)) && mouseY < (4 + (10 * 10)))
            {
                int w = font.getStringWidth(I18n.format("fml.messages.java8warning.1", TextFormatting.RED, TextFormatting.RESET));
                w = Math.max(w, font.getStringWidth(I18n.format("fml.messages.java8warning.2")));
                if (mouseX >= ((width - w) / 2) && mouseX <= ((width + w) / 2))
                {
                    FMLClientHandler.instance().showGuiScreen(new GuiJava8Error(new Java8VersionException(Collections.<ModContainer>emptyList())));
                }
            }
        }
    }

    public static ISound playSound(SoundManager manager, ISound sound)
    {
        PlaySoundEvent e = new PlaySoundEvent(manager, sound);
        MinecraftForge.EVENT_BUS.post(e);
        return e.getResultSound();
    }

    //static RenderBlocks VertexBufferRB;
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

    public static float getFogDensity(EntityRenderer renderer, Entity entity, IBlockState state, float partial, float density)
    {
        EntityViewRenderEvent.FogDensity event = new EntityViewRenderEvent.FogDensity(renderer, entity, state, partial, density);
        if (MinecraftForge.EVENT_BUS.post(event)) return event.getDensity();
        return -1;
    }

    public static void onFogRender(EntityRenderer renderer, Entity entity, IBlockState state, float partial, int mode, float distance)
    {
        MinecraftForge.EVENT_BUS.post(new EntityViewRenderEvent.RenderFogEvent(renderer, entity, state, partial, mode, distance));
    }

    public static void onModelBake(ModelManager modelManager, IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, ModelLoader modelLoader)
    {
        MinecraftForge.EVENT_BUS.post(new ModelBakeEvent(modelManager, modelRegistry, modelLoader));
        modelLoader.onPostBakeEvent(modelRegistry);
    }

    @SuppressWarnings("deprecation")
    public static Matrix4f getMatrix(net.minecraft.client.renderer.block.model.ItemTransformVec3f transform)
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

    private static final Matrix4f flipX;
    static {
        flipX = new Matrix4f();
        flipX.setIdentity();
        flipX.m00 = -1;
    }

    @SuppressWarnings("deprecation")
    public static IBakedModel handleCameraTransforms(IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType, boolean leftHandHackery)
    {
        if(model instanceof IPerspectiveAwareModel)
        {
            Pair<? extends IBakedModel, Matrix4f> pair = ((IPerspectiveAwareModel)model).handlePerspective(cameraTransformType);

            if(pair.getRight() != null)
            {
                Matrix4f matrix = new Matrix4f(pair.getRight());
                if(leftHandHackery)
                {
                    matrix.mul(flipX, matrix);
                    matrix.mul(matrix, flipX);
                }
                multiplyCurrentGlMatrix(matrix);
            }
            return pair.getLeft();
        }
        else
        {
            //if(leftHandHackery) GlStateManager.scale(-1, 1, 1);
            ItemCameraTransforms.applyTransformSide(model.getItemCameraTransforms().getTransform(cameraTransformType), leftHandHackery);
            //if(leftHandHackery) GlStateManager.scale(-1, 1, 1);
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
        buffer.position(format.getOffset(element));
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

    public static void putQuadColor(VertexBuffer renderer, BakedQuad quad, int color)
    {
        float cb = color & 0xFF;
        float cg = (color >>> 8) & 0xFF;
        float cr = (color >>> 16) & 0xFF;
        float ca = (color >>> 24) & 0xFF;
        VertexFormat format = quad.getFormat();
        int size = format.getIntegerSize();
        int offset = format.getColorOffset() / 4; // assumes that color is aligned
        for(int i = 0; i < 4; i++)
        {
            int vc = quad.getVertexData()[offset + size * i];
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

    @SuppressWarnings("deprecation")
    public static Optional<TRSRTransformation> applyTransform(net.minecraft.client.renderer.block.model.ItemTransformVec3f transform, Optional<? extends IModelPart> part)
    {
        if(part.isPresent()) return Optional.absent();
        return Optional.of(TRSRTransformation.blockCenterToCorner(new TRSRTransformation(transform)));
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

    public static IBakedModel getDamageModel(IBakedModel ibakedmodel, TextureAtlasSprite texture, IBlockState state, IBlockAccess world, BlockPos pos)
    {
        state = state.getBlock().getExtendedState(state, world, pos);
        return (new SimpleBakedModel.Builder(state, ibakedmodel, texture, pos)).makeBakedModel();
    }

    private static int slotMainHand = 0;
    // FIXME
    public static boolean shouldCauseReequipAnimation(ItemStack from, ItemStack to, int slot)
    {
        if (from == null && to != null) return true;
        if (from == null && to == null) return false;
        if (from != null && to == null) return true;
        boolean changed = false;
        if (slot != -1)
        {
            changed = slot != slotMainHand;
            slotMainHand = slot;
        }
        return from.getItem().shouldCauseReequipAnimation(from, to, changed);
    }

    public static boolean shouldCauseBlockBreakReset(@Nonnull ItemStack from, @Nonnull ItemStack to)
    {
        return from.getItem().shouldCauseBlockBreakReset(from, to);
    }

    public static BlockFaceUV applyUVLock(BlockFaceUV blockFaceUV, EnumFacing originalSide, ITransformation rotation)
    {
        TRSRTransformation global = new TRSRTransformation(rotation.getMatrix());
        Matrix4f uv = global.getUVLockTransform(originalSide).getMatrix();
        Vector4f vec = new Vector4f(0, 0, 0, 1);
        vec.x = blockFaceUV.getVertexU(blockFaceUV.getVertexRotatedRev(0)) / 16;
        vec.y = blockFaceUV.getVertexV(blockFaceUV.getVertexRotatedRev(0)) / 16;
        uv.transform(vec);
        float uMin = 16 * vec.x; // / vec.w;
        float vMin = 16 * vec.y; // / vec.w;
        vec.x = blockFaceUV.getVertexU(blockFaceUV.getVertexRotatedRev(2)) / 16;
        vec.y = blockFaceUV.getVertexV(blockFaceUV.getVertexRotatedRev(2)) / 16;
        vec.z = 0;
        vec.w = 1;
        uv.transform(vec);
        float uMax = 16 * vec.x; // / vec.w;
        float vMax = 16 * vec.y; // / vec.w;
        if(uMin > uMax)
        {
            float t = uMin;
            uMin = uMax;
            uMax = t;
        }
        if(vMin > vMax)
        {
            float t = vMin;
            vMin = vMax;
            vMax = t;
        }
        float a = (float)Math.toRadians(blockFaceUV.rotation);
        Vector3f rv = new Vector3f(MathHelper.cos(a), MathHelper.sin(a), 0);
        Matrix3f rot = new Matrix3f();
        uv.getRotationScale(rot);
        rot.transform(rv);
        int angle = MathHelper.normalizeAngle(-(int)Math.round(Math.toDegrees(Math.atan2(rv.y, rv.x)) / 90) * 90, 360);
        return new BlockFaceUV(new float[]{ uMin, vMin, uMax, vMax }, angle);
    }

    public static RenderGameOverlayEvent.BossInfo bossBarRenderPre(ScaledResolution res, BossInfoLerping bossInfo, int x, int y, int increment)
    {
        RenderGameOverlayEvent.BossInfo evt = new RenderGameOverlayEvent.BossInfo(new RenderGameOverlayEvent(Animation.getPartialTickTime(), res),
                BOSSINFO, bossInfo, x, y, increment);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static void bossBarRenderPost(ScaledResolution res)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(new RenderGameOverlayEvent(Animation.getPartialTickTime(), res), BOSSINFO));
    }

    public static ScreenshotEvent onScreenshot(BufferedImage image, File screenshotFile)
    {
        ScreenshotEvent event = new ScreenshotEvent(image, screenshotFile);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

}
