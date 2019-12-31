/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
import static net.minecraftforge.fml.VersionChecker.Status.BETA;
import static net.minecraftforge.fml.VersionChecker.Status.BETA_OUTDATED;
import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glMultMatrixf;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.*;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.async.ThreadNameCachingStrategy;
import org.apache.logging.log4j.core.impl.ReusableLogEventFactory;
import org.lwjgl.opengl.GL13;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.FogRenderer.FogType;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.Usage;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.pipeline.QuadGatheringTransformer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TransformationHelper;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.resource.ReloadRequirements;
import net.minecraftforge.resource.SelectiveReloadStateHandler;
import net.minecraftforge.resource.VanillaResourceType;
import net.minecraftforge.versions.forge.ForgeVersion;

public class ForgeHooksClient
{
    private static final Logger LOGGER = LogManager.getLogger();

    //private static final ResourceLocation ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    public static String getArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlotType slot, String type)
    {
        String result = armor.getItem().getArmorTexture(armor, entity, slot, type);
        return result != null ? result : _default;
    }

    public static boolean onDrawBlockHighlight(WorldRenderer context, ActiveRenderInfo info, RayTraceResult target, int subID, float partialTicks)
    {
        switch (target.getType()) {
            case BLOCK:
                if (!(target instanceof BlockRayTraceResult)) return false;
                return MinecraftForge.EVENT_BUS.post(new DrawHighlightEvent.HighlightBlock(context, info, target, subID, partialTicks));
            case ENTITY:
                if (!(target instanceof EntityRayTraceResult)) return false;
                return MinecraftForge.EVENT_BUS.post(new DrawHighlightEvent.HighlightEntity(context, info, target, subID, partialTicks));
        }
        return MinecraftForge.EVENT_BUS.post(new DrawHighlightEvent(context, info, target, subID, partialTicks));
    }

    public static void dispatchRenderLast(WorldRenderer context, MatrixStack mat, float partialTicks)
    {
        MinecraftForge.EVENT_BUS.post(new RenderWorldLastEvent(context, mat, partialTicks));
    }

    public static boolean renderFirstPersonHand(WorldRenderer context, MatrixStack mat, float partialTicks)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderHandEvent(context, mat, partialTicks));
    }

    public static boolean renderSpecificFirstPersonHand(Hand hand, MatrixStack mat, float partialTicks, float interpPitch, float swingProgress, float equipProgress, ItemStack stack)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderSpecificHandEvent(hand, mat, partialTicks, interpPitch, swingProgress, equipProgress, stack));
    }

    public static void onTextureStitchedPre(AtlasTexture map, Set<ResourceLocation> resourceLocations)
    {
        ModLoader.get().postEvent(new TextureStitchEvent.Pre(map, resourceLocations));
//        ModelLoader.White.INSTANCE.register(map); // TODO Custom TAS
    }

    public static void onTextureStitchedPost(AtlasTexture map)
    {
        ModLoader.get().postEvent(new TextureStitchEvent.Post(map));
    }

    public static void onBlockColorsInit(BlockColors blockColors)
    {
        ModLoader.get().postEvent(new ColorHandlerEvent.Block(blockColors));
    }

    public static void onItemColorsInit(ItemColors itemColors, BlockColors blockColors)
    {
        ModLoader.get().postEvent(new ColorHandlerEvent.Item(itemColors, blockColors));
    }

    static final ThreadLocal<RenderType> renderLayer = new ThreadLocal<RenderType>();

    public static void setRenderLayer(RenderType layer)
    {
        renderLayer.set(layer);
    }

    public static <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType slot, A _default)
    {
        A model = itemStack.getItem().getArmorModel(entityLiving, itemStack, slot, _default);
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
/* TODO mouse input
    public static boolean postMouseEvent()
    {
        return MinecraftForge.EVENT_BUS.post(new MouseEvent());
    }
*/
    public static float getOffsetFOV(PlayerEntity entity, float fov)
    {
        FOVUpdateEvent fovUpdateEvent = new FOVUpdateEvent(entity, fov);
        MinecraftForge.EVENT_BUS.post(fovUpdateEvent);
        return fovUpdateEvent.getNewfov();
    }

    public static double getFOVModifier(GameRenderer renderer, ActiveRenderInfo info, double renderPartialTicks, double fov) {
        EntityViewRenderEvent.FOVModifier event = new EntityViewRenderEvent.FOVModifier(renderer, info, renderPartialTicks, fov);
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

        GameSettings settings = Minecraft.getInstance().gameSettings;
        int[] ranges = { 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34 };
        int distance = 0;
        if (settings.fancyGraphics && ranges.length > 0)
        {
            distance = ranges[MathHelper.clamp(settings.renderDistanceChunks, 0, ranges.length-1)];
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
                Biome biome = world.func_225526_b_(pos.getX(), pos.getY(), pos.getZ());
                int colour = 0xFFFFFF; // TODO: biome.getSkyColorByTemp(biome.getTemperature(pos));
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

    public static void renderMainMenu(MainMenuScreen gui, FontRenderer font, int width, int height)
    {
        VersionChecker.Status status = ForgeVersion.getStatus();
        if (status == BETA || status == BETA_OUTDATED)
        {
            // render a warning at the top of the screen,
            String line = I18n.format("forge.update.beta.1", TextFormatting.RED, TextFormatting.RESET);
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

        forgeStatusLine = line;
    }

    public static String forgeStatusLine;
    public static ISound playSound(SoundEngine manager, ISound sound)
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

    public static void drawScreen(Screen screen, int mouseX, int mouseY, float partialTicks)
    {
        if (!MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Pre(screen, mouseX, mouseY, partialTicks)))
            screen.render(mouseX, mouseY, partialTicks);
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Post(screen, mouseX, mouseY, partialTicks));
    }

    public static float getFogDensity(FogType type, ActiveRenderInfo info, float partial, float density)
    {
        EntityViewRenderEvent.FogDensity event = new EntityViewRenderEvent.FogDensity(type, info, partial, density);
        if (MinecraftForge.EVENT_BUS.post(event)) return event.getDensity();
        return -1;
    }

    public static void onFogRender(FogType type, ActiveRenderInfo info, float partial, float distance)
    {
        MinecraftForge.EVENT_BUS.post(new EntityViewRenderEvent.RenderFogEvent(type, info, partial, distance));
    }

    public static EntityViewRenderEvent.CameraSetup onCameraSetup(GameRenderer renderer, ActiveRenderInfo info, float partial)
    {
        EntityViewRenderEvent.CameraSetup event = new EntityViewRenderEvent.CameraSetup(renderer, info, partial, info.getYaw(), info.getPitch(), 0);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void onModelBake(ModelManager modelManager, Map<ResourceLocation, IBakedModel> modelRegistry, ModelLoader modelLoader)
    {
        ModLoader.get().postEvent(new ModelBakeEvent(modelManager, modelRegistry, modelLoader));
        modelLoader.onPostBakeEvent(modelRegistry);
    }

    private static final net.minecraft.client.renderer.Matrix4f flipX;
    private static final net.minecraft.client.renderer.Matrix3f flipXNormal;
    static {
        flipX = Matrix4f.func_226593_a_(-1,1,1);
        flipXNormal = new net.minecraft.client.renderer.Matrix3f(flipX);
    }

    public static IBakedModel handleCameraTransforms(MatrixStack matrixStack, IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType, boolean leftHandHackery)
    {
        MatrixStack stack = new MatrixStack();
        model = model.handlePerspective(cameraTransformType, stack);

        // If the stack is not empty, the code has added a matrix for us to use.
        if (!stack.func_227867_d_())
        {
            // Apply the transformation to the real matrix stack, flipping for left hand
            net.minecraft.client.renderer.Matrix4f tMat = stack.func_227866_c_().func_227870_a_();
            net.minecraft.client.renderer.Matrix3f nMat = stack.func_227866_c_().func_227872_b_();
            if (leftHandHackery)
            {
                tMat.multiplyBackward(flipX);
                tMat.func_226595_a_(flipX);
                nMat.multiplyBackward(flipXNormal);
                nMat.func_226118_b_(flipXNormal);
            }
            matrixStack.func_227866_c_().func_227870_a_().func_226595_a_(tMat);
            matrixStack.func_227866_c_().func_227872_b_().func_226118_b_(nMat);
        }
        return model;
    }

    // moved and expanded from WorldVertexBufferUploader.draw

    public static void preDraw(Usage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer)
    {
        VertexFormatElement attr = format.func_227894_c_().get(element);
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
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0 + attr.getIndex());
                glTexCoordPointer(count, constant, stride, buffer);
                glEnableClientState(GL_TEXTURE_COORD_ARRAY);
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
                break;
            case PADDING:
                break;
            case GENERIC:
                glEnableVertexAttribArray(attr.getIndex());
                glVertexAttribPointer(attr.getIndex(), count, constant, false, stride, buffer);
                break;
            default:
                LOGGER.fatal("Unimplemented vanilla attribute upload: {}", attrType.getDisplayName());
        }
    }

    public static void postDraw(Usage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer)
    {
        VertexFormatElement attr = format.func_227894_c_().get(element);
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
                break;
            case UV:
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0 + attr.getIndex());
                glDisableClientState(GL_TEXTURE_COORD_ARRAY);
                GL13.glClientActiveTexture(GL13.GL_TEXTURE0);
                break;
            case PADDING:
                break;
            case GENERIC:
                glDisableVertexAttribArray(attr.getIndex());
                break;
            default:
                LOGGER.fatal("Unimplemented vanilla attribute upload: {}", attrType.getDisplayName());
        }
    }

    public static int getColorIndex(VertexFormat fmt)
    {
        ImmutableList<VertexFormatElement> elements = fmt.func_227894_c_();
        for(int i=0;i<elements.size();i++)
        {
            if (elements.get(i).getUsage() == Usage.COLOR)
                return i;
        }
        throw new IndexOutOfBoundsException("There is no COLOR element in the provided VertexFormat.");
    }

    // TODO: IVertexBuilder doesn't have a means to modify existing data.
    /*
    public static void putQuadColor(IVertexBuilder renderer, BakedQuad quad, float cr, float cg, float cb, float ca)
    {
        VertexFormat format = quad.getFormat();
        int size = format.getIntegerSize();
        int offset = format.getOffset(getColorIndex(format)) / 4; // assumes that color is aligned
        boolean hasColor = format.hasColor();
        for(int i = 0; i < 4; i++)
        {
            int vc = hasColor ? quad.getVertexData()[offset + size * i] : 0xFFFFFFFF;
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
    }*/

    @SuppressWarnings("deprecation")
    public static TextureAtlasSprite[] getFluidSprites(ILightReader world, BlockPos pos, IFluidState fluidStateIn)
    {
        return new TextureAtlasSprite[] {
                Minecraft.getInstance().func_228015_a_(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluidStateIn.getFluid().getAttributes().getStillTexture(world, pos)),
                Minecraft.getInstance().func_228015_a_(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(fluidStateIn.getFluid().getAttributes().getFlowingTexture(world, pos)),
        };
    }

    public static void gatherFluidTextures(Set<Material> textures)
    {
        ForgeRegistries.FLUIDS.getValues().stream()
                .flatMap(ForgeHooksClient::getFluidMaterials)
                .forEach(textures::add);
    }

    public static Stream<Material> getFluidMaterials(Fluid fluid)
    {
        return fluid.getAttributes().getTextures()
                .filter(Objects::nonNull)
                .map(ForgeHooksClient::getBlockMaterial);
    }

    @SuppressWarnings("deprecation")
    public static Material getBlockMaterial(ResourceLocation loc)
    {
        return new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, loc);
    }

    private static class LightGatheringTransformer extends QuadGatheringTransformer {

        private static final VertexFormat FORMAT = new VertexFormat(ImmutableList.of(DefaultVertexFormats.TEX_2F, DefaultVertexFormats.TEX_2S));

        int blockLight, skyLight;

        { setVertexFormat(FORMAT); }

        boolean hasLighting()
        {
            return dataLength[1] >= 2;
        }

        @Override
        protected void processQuad()
        {
            // Reset light data
            blockLight = 0;
            skyLight = 0;
            // Compute average light for all 4 vertices
            for (int i = 0; i < 4; i++)
            {
                blockLight += (int) ((quadData[1][i][0] * 0xFFFF) / 0x20);
                skyLight += (int) ((quadData[1][i][1] * 0xFFFF) / 0x20);
            }
            // Values must be multiplied by 16, divided by 4 for average => x4
            blockLight *= 4;
            skyLight *= 4;
        }

        // Dummy overrides

        @Override
        public void setQuadTint(int tint) {}

        @Override
        public void setQuadOrientation(Direction orientation) {}

        @Override
        public void setApplyDiffuseLighting(boolean diffuse) {}

        @Override
        public void setTexture(TextureAtlasSprite texture) {}
    }

    private static final LightGatheringTransformer lightGatherer = new LightGatheringTransformer();

    // TODO: Fix: Vanilla now batches rendering items, so our hack of forcing the GL lighting state does not work.
    public static void renderLitItem(ItemRenderer ri, MatrixStack mat, IVertexBuilder consumer, IBakedModel model, ItemStack stack)
    {
        List<BakedQuad> allquads = new ArrayList<>();
        Random random = new Random();
        long seed = 42L;

        for (Direction enumfacing : Direction.values())
        {
            random.setSeed(seed);
            allquads.addAll(model.getQuads(null, enumfacing, random, EmptyModelData.INSTANCE));
        }

        random.setSeed(seed);
        allquads.addAll(model.getQuads(null, null, random, EmptyModelData.INSTANCE));

        if (allquads.isEmpty()) return;

        // Current list of consecutive quads with the same lighting
        List<BakedQuad> segment = new ArrayList<>();

        // Lighting of the current segment
        int segmentBlockLight = 0;
        int segmentSkyLight = 0;
        // Diffuse lighting state
        boolean segmentShading = true;
        // State changed by the current segment
        boolean segmentLightingDirty = false;
        boolean segmentShadingDirty = false;
        // If the current segment contains lighting data
        boolean hasLighting = false;

        for (int i = 0; i < allquads.size(); i++)
        {
            BakedQuad q = allquads.get(i);

            // Lighting of the current quad
            int bl = 0;
            int sl = 0;

            if (q.getFormat().hasUV(1))
            {
                LightUtil.putBakedQuad(lightGatherer, q);
                if (lightGatherer.hasLighting())
                {
                    bl = lightGatherer.blockLight;
                    sl = lightGatherer.skyLight;
                }
            }

            boolean shade = q.shouldApplyDiffuseLighting();

            boolean lightingDirty = segmentBlockLight != bl || segmentSkyLight != sl;
            boolean shadeDirty = shade != segmentShading;

            // If lighting or color data has changed, draw the segment and flush it
            if (lightingDirty || shadeDirty)
            {
                if (i > 0) // Make sure this isn't the first quad being processed
                    drawSegment(ri, mat, consumer, stack, segment, segmentBlockLight, segmentSkyLight, segmentShading, segmentLightingDirty && (hasLighting || segment.size() < i), segmentShadingDirty);
                segmentBlockLight = bl;
                segmentSkyLight = sl;
                segmentShading = shade;
                segmentLightingDirty = lightingDirty;
                segmentShadingDirty = shadeDirty;
                hasLighting = segmentBlockLight > 0 || segmentSkyLight > 0 || !segmentShading;
            }

            segment.add(q);
        }

        drawSegment(ri, mat, consumer, stack, segment, segmentBlockLight, segmentSkyLight, segmentShading, segmentLightingDirty && (hasLighting || segment.size() < allquads.size()), segmentShadingDirty);

        // Clean up render state if necessary
        if (hasLighting)
        {
            RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, GlStateManager.lastBrightnessX, GlStateManager.lastBrightnessY);
            RenderSystem.enableLighting();
        }
    }

    private static void drawSegment(ItemRenderer ir, MatrixStack mat, IVertexBuilder cons, ItemStack stack, List<BakedQuad> segment, int bl, int sl, boolean shade, boolean updateLighting, boolean updateShading)
    {
        float lastBl = GlStateManager.lastBrightnessX;
        float lastSl = GlStateManager.lastBrightnessY;

        if (updateShading)
        {
            if (shade)
            {
                // (Re-)enable lighting for normal look with shading
                RenderSystem.enableLighting();
            }
            else
            {
                // Disable lighting to simulate a lack of diffuse lighting
                RenderSystem.disableLighting();
            }
        }

        if (updateLighting)
        {
            // Force lightmap coords to simulate synthetic lighting
            RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, Math.max(bl, lastBl), Math.max(sl, lastSl));
        }

        // TODO can we just use this light value ??
        ir.func_229112_a_(mat, cons, segment, stack, (bl << 16) | sl, OverlayTexture.field_229196_a_);

        // Preserve this as it represents the "world" lighting
        GlStateManager.lastBrightnessX = lastBl;
        GlStateManager.lastBrightnessY = lastSl;

        segment.clear();
    }

    /**
     * internal, relies on fixed format of FaceBakery
     */
    // TODO Do we need this?
    public static void fillNormal(int[] faceData, Direction facing)
    {
        Vector3f v1 = getVertexPos(faceData, 3);
        Vector3f t1 = getVertexPos(faceData, 1);
        Vector3f v2 = getVertexPos(faceData, 2);
        Vector3f t2 = getVertexPos(faceData, 0);
        v1.sub(t1);
        v2.sub(t2);
        v2.cross(v1);
        v2.func_229194_d_();

        int x = ((byte) Math.round(v2.getX() * 127)) & 0xFF;
        int y = ((byte) Math.round(v2.getY() * 127)) & 0xFF;
        int z = ((byte) Math.round(v2.getZ() * 127)) & 0xFF;

        int normal = x | (y << 0x08) | (z << 0x10);

        for(int i = 0; i < 4; i++)
        {
            faceData[i * 8 + 7] = normal;
        }
    }

    private static Vector3f getVertexPos(int[] data, int vertex)
    {
        int idx = vertex * 8;

        float x = Float.intBitsToFloat(data[idx]);
        float y = Float.intBitsToFloat(data[idx + 1]);
        float z = Float.intBitsToFloat(data[idx + 2]);

        return new Vector3f(x, y, z);
    }

    public static void loadEntityShader(Entity entity, GameRenderer entityRenderer)
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

    private static int slotMainHand = 0;

    public static boolean shouldCauseReequipAnimation(@Nonnull ItemStack from, @Nonnull ItemStack to, int slot)
    {
        boolean fromInvalid = from.isEmpty();
        boolean toInvalid   = to.isEmpty();

        if (fromInvalid && toInvalid) return false;
        if (fromInvalid || toInvalid) return true;

        boolean changed = false;
        if (slot != -1)
        {
            changed = slot != slotMainHand;
            slotMainHand = slot;
        }
        return from.getItem().shouldCauseReequipAnimation(from, to, changed);
    }

    public static RenderGameOverlayEvent.BossInfo bossBarRenderPre(MainWindow res, ClientBossInfo bossInfo, int x, int y, int increment)
    {
        RenderGameOverlayEvent.BossInfo evt = new RenderGameOverlayEvent.BossInfo(new RenderGameOverlayEvent(Animation.getPartialTickTime(), res),
                BOSSINFO, bossInfo, x, y, increment);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static void bossBarRenderPost(MainWindow res)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(new RenderGameOverlayEvent(Animation.getPartialTickTime(), res), BOSSINFO));
    }

    public static ScreenshotEvent onScreenshot(NativeImage image, File screenshotFile)
    {
        ScreenshotEvent event = new ScreenshotEvent(image, screenshotFile);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    @SuppressWarnings("deprecation")
    public static IBakedModel handlePerspective(IBakedModel model, ItemCameraTransforms.TransformType type, MatrixStack stack)
    {
        TransformationMatrix tr = TransformationHelper.toTransformation(model.getItemCameraTransforms().getTransform(type));
        if(!tr.isIdentity()) {
            tr.push(stack);
        }
        return model;
    }

    public static void onInputUpdate(PlayerEntity player, MovementInput movementInput)
    {
        MinecraftForge.EVENT_BUS.post(new InputUpdateEvent(player, movementInput));
    }

    public static void refreshResources(Minecraft mc, VanillaResourceType... types) {
        SelectiveReloadStateHandler.INSTANCE.beginReload(ReloadRequirements.include(types));
        mc.reloadResources();
        SelectiveReloadStateHandler.INSTANCE.endReload();
    }

    public static boolean onGuiMouseClickedPre(Screen guiScreen, double mouseX, double mouseY, int button)
    {
        Event event = new GuiScreenEvent.MouseClickedEvent.Pre(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseClickedPost(Screen guiScreen, double mouseX, double mouseY, int button)
    {
        Event event = new GuiScreenEvent.MouseClickedEvent.Post(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseReleasedPre(Screen guiScreen, double mouseX, double mouseY, int button)
    {
        Event event = new GuiScreenEvent.MouseReleasedEvent.Pre(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseReleasedPost(Screen guiScreen, double mouseX, double mouseY, int button)
    {
        Event event = new GuiScreenEvent.MouseReleasedEvent.Post(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseDragPre(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
    {
        Event event = new GuiScreenEvent.MouseDragEvent.Pre(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseDragPost(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
    {
        Event event = new GuiScreenEvent.MouseDragEvent.Post(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseScrollPre(MouseHelper mouseHelper, Screen guiScreen, double scrollDelta)
    {
        MainWindow mainWindow = guiScreen.getMinecraft().func_228018_at_();
        double mouseX = mouseHelper.getMouseX() * (double) mainWindow.getScaledWidth() / (double) mainWindow.getWidth();
        double mouseY = mouseHelper.getMouseY() * (double) mainWindow.getScaledHeight() / (double) mainWindow.getHeight();
        Event event = new GuiScreenEvent.MouseScrollEvent.Pre(guiScreen, mouseX, mouseY, scrollDelta);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseScrollPost(MouseHelper mouseHelper, Screen guiScreen, double scrollDelta)
    {
        MainWindow mainWindow = guiScreen.getMinecraft().func_228018_at_();
        double mouseX = mouseHelper.getMouseX() * (double) mainWindow.getScaledWidth() / (double) mainWindow.getWidth();
        double mouseY = mouseHelper.getMouseY() * (double) mainWindow.getScaledHeight() / (double) mainWindow.getHeight();
        Event event = new GuiScreenEvent.MouseScrollEvent.Post(guiScreen, mouseX, mouseY, scrollDelta);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiKeyPressedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardKeyPressedEvent.Pre(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiKeyPressedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardKeyPressedEvent.Post(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiKeyReleasedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardKeyReleasedEvent.Pre(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiKeyReleasedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardKeyReleasedEvent.Post(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiCharTypedPre(Screen guiScreen, char codePoint, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardCharTypedEvent.Pre(guiScreen, codePoint, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiCharTypedPost(Screen guiScreen, char codePoint, int modifiers)
    {
        Event event = new GuiScreenEvent.KeyboardCharTypedEvent.Post(guiScreen, codePoint, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onRecipesUpdated(RecipeManager mgr)
    {
        Event event = new RecipesUpdatedEvent(mgr);
        MinecraftForge.EVENT_BUS.post(event);
    }

    // Resets cached thread fields in ThreadNameCachingStrategy and ReusableLogEventFactory to be repopulated during their next access.
    // This serves a workaround for no built-in method of triggering this type of refresh as brought up by LOG4J2-2178.
    public static void invalidateLog4jThreadCache()
    {
        if (System.getProperty("java.version").compareTo("1.8.0_102") >= 0) return; // skip for later JDKs, because it's not CACHED see LOG4J2-2052
        try
        {
            Field nameField = ThreadNameCachingStrategy.class.getDeclaredField("THREADLOCAL_NAME");
            Field logEventField = ReusableLogEventFactory.class.getDeclaredField("mutableLogEventThreadLocal");
            nameField.setAccessible(true);
            logEventField.setAccessible(true);
            ((ThreadLocal<?>) nameField.get(null)).set(null);
            ((ThreadLocal<?>) logEventField.get(null)).set(null);
        }
        catch (ReflectiveOperationException | NoClassDefFoundError e)
        {
            LOGGER.error("Unable to invalidate log4j thread cache, thread fields in logs may be inaccurate", e);
        }
    }

    public static void fireMouseInput(int button, int action, int mods)
    {
        MinecraftForge.EVENT_BUS.post(new InputEvent.MouseInputEvent(button, action, mods));
    }

    public static void fireKeyInput(int key, int scanCode, int action, int modifiers)
    {
        MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent(key, scanCode, action, modifiers));
    }

    public static boolean onMouseScroll(MouseHelper mouseHelper, double scrollDelta)
    {
        Event event = new InputEvent.MouseScrollEvent(scrollDelta, mouseHelper.isLeftDown(), mouseHelper.isMiddleDown(), mouseHelper.isRightDown(), mouseHelper.getMouseX(), mouseHelper.getMouseY());
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onRawMouseClicked(int button, int action, int mods)
    {
        return MinecraftForge.EVENT_BUS.post(new InputEvent.RawMouseEvent(button, action, mods));
    }
}
