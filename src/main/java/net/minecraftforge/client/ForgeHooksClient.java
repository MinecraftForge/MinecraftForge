/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.locale.Language;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlas;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.mojang.blaze3d.vertex.VertexFormatElement.Usage;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.player.Input;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TransformationHelper;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlclient.ExtendedServerListData;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.minecraftforge.fmllegacy.ForgeI18n;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.resource.VanillaResourceType;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL13;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.BOSSINFO;
import static net.minecraftforge.fml.VersionChecker.Status.BETA;
import static net.minecraftforge.fml.VersionChecker.Status.BETA_OUTDATED;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class ForgeHooksClient
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker CLIENTHOOKS = MarkerManager.getMarker("CLIENTHOOKS");

    //private static final ResourceLocation ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    /**
     * Contains the *extra* GUI layers.
     * The current top layer stays in Minecraft#currentScreen, and the rest serve as a background for it.
     */
    private static final Stack<Screen> guiLayers = new Stack<>();

    public static void clearGuiLayers(Minecraft minecraft)
    {
        while(guiLayers.size() > 0)
            popGuiLayerInternal(minecraft);
    }

    private static void popGuiLayerInternal(Minecraft minecraft)
    {
        if (minecraft.screen != null)
            minecraft.screen.removed();
        minecraft.screen = guiLayers.pop();
    }

    public static void pushGuiLayer(Minecraft minecraft, Screen screen)
    {
        if (minecraft.screen != null)
            guiLayers.push(minecraft.screen);
        minecraft.screen = Objects.requireNonNull(screen);
        screen.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        NarratorChatListener.INSTANCE.sayNow(screen.getNarrationMessage());
    }

    public static void popGuiLayer(Minecraft minecraft)
    {
        if (guiLayers.size() == 0)
        {
            minecraft.setScreen(null);
            return;
        }

        popGuiLayerInternal(minecraft);
        if (minecraft.screen != null)
            NarratorChatListener.INSTANCE.sayNow(minecraft.screen.getNarrationMessage());
    }

    public static float getGuiFarPlane()
    {
        // 1000 units for the overlay background,
        // and 2000 units for each layered Screen,

        return 1000.0F + 2000.0F * (1 + guiLayers.size());
    }

    public static String getArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlot slot, String type)
    {
        String result = armor.getItem().getArmorTexture(armor, entity, slot, type);
        return result != null ? result : _default;
    }

    //TODO: Rename to onDrawHighlight
    public static boolean onDrawBlockHighlight(LevelRenderer context, Camera info, HitResult target, float partialTicks, PoseStack matrix, MultiBufferSource buffers)
    {
        switch (target.getType()) {
            case BLOCK:
                if (!(target instanceof BlockHitResult)) return false;
                return MinecraftForge.EVENT_BUS.post(new DrawSelectionEvent.HighlightBlock(context, info, target, partialTicks, matrix, buffers));
            case ENTITY:
                if (!(target instanceof EntityHitResult)) return false;
                return MinecraftForge.EVENT_BUS.post(new DrawSelectionEvent.HighlightEntity(context, info, target, partialTicks, matrix, buffers));
            default:
                return MinecraftForge.EVENT_BUS.post(new DrawSelectionEvent(context, info, target, partialTicks, matrix, buffers));
        }
    }

    public static void dispatchRenderLast(LevelRenderer context, PoseStack mat, float partialTicks, Matrix4f projectionMatrix, long finishTimeNano)
    {
        MinecraftForge.EVENT_BUS.post(new RenderWorldLastEvent(context, mat, partialTicks, projectionMatrix, finishTimeNano));
    }

    public static boolean renderSpecificFirstPersonHand(InteractionHand hand, PoseStack mat, MultiBufferSource buffers, int light, float partialTicks, float interpPitch, float swingProgress, float equipProgress, ItemStack stack)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderHandEvent(hand, mat, buffers, light, partialTicks, interpPitch, swingProgress, equipProgress, stack));
    }

    public static void onTextureStitchedPre(TextureAtlas map, Set<ResourceLocation> resourceLocations)
    {
        StartupMessageManager.mcLoaderConsumer().ifPresent(c->c.accept("Atlas Stitching : "+map.location().toString()));
        ModLoader.get().postEvent(new TextureStitchEvent.Pre(map, resourceLocations));
//        ModelLoader.White.INSTANCE.register(map); // TODO Custom TAS
        Sheets.SIGN_MATERIALS.values().stream()
                .filter(rm -> rm.atlasLocation().equals(map.location()))
                .forEach(rm -> resourceLocations.add(rm.texture()));
    }

    public static void onTextureStitchedPost(TextureAtlas map)
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

    public static <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot slot, A _default)
    {
        A model = RenderProperties.get(itemStack).getArmorModel(entityLiving, itemStack, slot, _default);
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
    public static float getOffsetFOV(Player entity, float fov)
    {
        FOVUpdateEvent fovUpdateEvent = new FOVUpdateEvent(entity, fov);
        MinecraftForge.EVENT_BUS.post(fovUpdateEvent);
        return fovUpdateEvent.getNewfov();
    }

    public static double getFOVModifier(GameRenderer renderer, Camera info, double renderPartialTicks, double fov) {
        EntityViewRenderEvent.FOVModifier event = new EntityViewRenderEvent.FOVModifier(renderer, info, renderPartialTicks, fov);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getFOV();
    }

    /**
     * Initialization of Forge Renderers.
     */
    static
    {
        //FluidRegistry.renderIdFluid = RenderingRegistry.getNextAvailableRenderId();
        //RenderingRegistry.registerBlockHandler(RenderBlockFluid.instance);
    }

    public static void renderMainMenu(TitleScreen gui, PoseStack mStack, Font font, int width, int height, int alpha)
    {
        VersionChecker.Status status = ForgeVersion.getStatus();
        if (status == BETA || status == BETA_OUTDATED)
        {
            // render a warning at the top of the screen,
            Component line = new TranslatableComponent("forge.update.beta.1", ChatFormatting.RED, ChatFormatting.RESET).withStyle(ChatFormatting.RED);
            GuiComponent.drawCenteredString(mStack, font, line, width / 2, 4 + (0 * (font.lineHeight + 1)), 0xFFFFFF | alpha);
            line = new TranslatableComponent("forge.update.beta.2");
            GuiComponent.drawCenteredString(mStack, font, line, width / 2, 4 + (1 * (font.lineHeight + 1)), 0xFFFFFF | alpha);
        }

        String line = null;
        switch(status)
        {
            //case FAILED:        line = " Version check failed"; break;
            //case UP_TO_DATE:    line = "Forge up to date"}; break;
            //case AHEAD:         line = "Using non-recommended Forge build, issues may arise."}; break;
            case OUTDATED:
            case BETA_OUTDATED: line = I18n.get("forge.update.newversion", ForgeVersion.getTarget()); break;
            default: break;
        }

        forgeStatusLine = line;
    }

    public static String forgeStatusLine;
    public static SoundInstance playSound(SoundEngine manager, SoundInstance sound)
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

    public static void drawScreen(Screen screen, PoseStack mStack, int mouseX, int mouseY, float partialTicks)
    {
        mStack.pushPose();
        guiLayers.forEach(layer -> {
            // Prevent the background layers from thinking the mouse is over their controls and showing them as highlighted.
            drawScreenInternal(layer, mStack, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTicks);
            mStack.translate(0,0,2000);
        });
        drawScreenInternal(screen, mStack, mouseX, mouseY, partialTicks);
        mStack.popPose();
    }

    private static void drawScreenInternal(Screen screen, PoseStack mStack, int mouseX, int mouseY, float partialTicks)
    {
        if (!MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Pre(screen, mStack, mouseX, mouseY, partialTicks)))
            screen.render(mStack, mouseX, mouseY, partialTicks);
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.DrawScreenEvent.Post(screen, mStack, mouseX, mouseY, partialTicks));
    }

    public static float getFogDensity(FogMode type, Camera info, float partial, float density)
    {
        EntityViewRenderEvent.FogDensity event = new EntityViewRenderEvent.FogDensity(type, info, partial, density);
        if (MinecraftForge.EVENT_BUS.post(event)) return event.getDensity();
        return -1;
    }

    public static void onFogRender(FogMode type, Camera info, float partial, float distance)
    {
        MinecraftForge.EVENT_BUS.post(new EntityViewRenderEvent.RenderFogEvent(type, info, partial, distance));
    }

    public static EntityViewRenderEvent.CameraSetup onCameraSetup(GameRenderer renderer, Camera info, float partial)
    {
        EntityViewRenderEvent.CameraSetup event = new EntityViewRenderEvent.CameraSetup(renderer, info, partial, info.getYRot(), info.getXRot(), 0);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void onModelBake(ModelManager modelManager, Map<ResourceLocation, BakedModel> modelRegistry, ModelLoader modelLoader)
    {
        ModLoader.get().postEvent(new ModelBakeEvent(modelManager, modelRegistry, modelLoader));
        modelLoader.onPostBakeEvent(modelRegistry);
    }

    private static final Matrix4f flipX;
    private static final Matrix3f flipXNormal;
    static {
        flipX = Matrix4f.createScaleMatrix(-1,1,1);
        flipXNormal = new Matrix3f(flipX);
    }

    public static BakedModel handleCameraTransforms(PoseStack matrixStack, BakedModel model, ItemTransforms.TransformType cameraTransformType, boolean leftHandHackery)
    {
        PoseStack stack = new PoseStack();
        model = model.handlePerspective(cameraTransformType, stack);

        // If the stack is not empty, the code has added a matrix for us to use.
        if (!stack.clear())
        {
            // Apply the transformation to the real matrix stack, flipping for left hand
            Matrix4f tMat = stack.last().pose();
            Matrix3f nMat = stack.last().normal();
            if (leftHandHackery)
            {
                tMat.multiplyBackward(flipX);
                tMat.multiply(flipX);
                nMat.multiplyBackward(flipXNormal);
                nMat.mul(flipXNormal);
            }
            matrixStack.last().pose().multiply(tMat);
            matrixStack.last().normal().mul(nMat);
        }
        return model;
    }

    // moved and expanded from WorldVertexBufferUploader.draw

    public static void preDraw(Usage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer)
    {
        VertexFormatElement attr = format.getElements().get(element);
        int count = attr.getElementCount();
        int constant = attr.getType().getGlType();
        ((Buffer)buffer).position(format.getOffset(element));
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
                LOGGER.fatal("Unimplemented vanilla attribute upload: {}", attrType.getName());
        }
    }

    public static void postDraw(Usage attrType, VertexFormat format, int element, int stride, ByteBuffer buffer)
    {
        VertexFormatElement attr = format.getElements().get(element);
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
                LOGGER.fatal("Unimplemented vanilla attribute upload: {}", attrType.getName());
        }
    }

    public static int getColorIndex(VertexFormat fmt)
    {
        ImmutableList<VertexFormatElement> elements = fmt.getElements();
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
    public static TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter world, BlockPos pos, FluidState fluidStateIn)
    {
        ResourceLocation overlayTexture = fluidStateIn.getType().getAttributes().getOverlayTexture();
        return new TextureAtlasSprite[] {
                Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidStateIn.getType().getAttributes().getStillTexture(world, pos)),
                Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidStateIn.getType().getAttributes().getFlowingTexture(world, pos)),
                overlayTexture == null ? null : Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(overlayTexture),
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
        return new Material(TextureAtlas.LOCATION_BLOCKS, loc);
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
        v2.normalize();

        int x = ((byte) Math.round(v2.x() * 127)) & 0xFF;
        int y = ((byte) Math.round(v2.y() * 127)) & 0xFF;
        int z = ((byte) Math.round(v2.z() * 127)) & 0xFF;

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
                entityRenderer.loadEffect(shader);
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

    public static RenderGameOverlayEvent.BossInfo bossBarRenderPre(PoseStack mStack, Window res, LerpingBossEvent bossInfo, int x, int y, int increment)
    {
        RenderGameOverlayEvent.BossInfo evt = new RenderGameOverlayEvent.BossInfo(mStack, new RenderGameOverlayEvent(mStack, Animation.getPartialTickTime(), res),
                BOSSINFO, bossInfo, x, y, increment);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static void bossBarRenderPost(PoseStack mStack, Window res)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(mStack, new RenderGameOverlayEvent(mStack, Animation.getPartialTickTime(), res), BOSSINFO));
    }

    public static ScreenshotEvent onScreenshot(NativeImage image, File screenshotFile)
    {
        ScreenshotEvent event = new ScreenshotEvent(image, screenshotFile);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void onClientChangeGameMode(PlayerInfo info, GameType currentGameMode, GameType newGameMode)
    {
        if (currentGameMode != newGameMode)
        {
            ClientPlayerChangeGameModeEvent evt = new ClientPlayerChangeGameModeEvent(info, currentGameMode, newGameMode);
            MinecraftForge.EVENT_BUS.post(evt);
        }
    }

    @SuppressWarnings("deprecation")
    public static BakedModel handlePerspective(BakedModel model, ItemTransforms.TransformType type, PoseStack stack)
    {
        Transformation tr = TransformationHelper.toTransformation(model.getTransforms().getTransform(type));
        if(!tr.isIdentity()) {
            tr.push(stack);
        }
        return model;
    }

    public static void onInputUpdate(Player player, Input movementInput)
    {
        MinecraftForge.EVENT_BUS.post(new InputUpdateEvent(player, movementInput));
    }

    /**
     * @deprecated use {@link Minecraft#reloadResourcePacks()} instead
     */
    @Deprecated(since="1.17.1", forRemoval = true)
    public static void refreshResources(Minecraft mc, VanillaResourceType... types) {
        mc.reloadResourcePacks();
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

    public static boolean onGuiMouseScrollPre(MouseHandler mouseHelper, Screen guiScreen, double scrollDelta)
    {
        Window mainWindow = guiScreen.getMinecraft().getWindow();
        double mouseX = mouseHelper.xpos() * (double) mainWindow.getGuiScaledWidth() / (double) mainWindow.getScreenWidth();
        double mouseY = mouseHelper.ypos() * (double) mainWindow.getGuiScaledHeight() / (double) mainWindow.getScreenHeight();
        Event event = new GuiScreenEvent.MouseScrollEvent.Pre(guiScreen, mouseX, mouseY, scrollDelta);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onGuiMouseScrollPost(MouseHandler mouseHelper, Screen guiScreen, double scrollDelta)
    {
        Window mainWindow = guiScreen.getMinecraft().getWindow();
        double mouseX = mouseHelper.xpos() * (double) mainWindow.getGuiScaledWidth() / (double) mainWindow.getScreenWidth();
        double mouseY = mouseHelper.ypos() * (double) mainWindow.getGuiScaledHeight() / (double) mainWindow.getScreenHeight();
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

    public static void fireMouseInput(int button, int action, int mods)
    {
        MinecraftForge.EVENT_BUS.post(new InputEvent.MouseInputEvent(button, action, mods));
    }

    public static void fireKeyInput(int key, int scanCode, int action, int modifiers)
    {
        MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent(key, scanCode, action, modifiers));
    }

    public static boolean onMouseScroll(MouseHandler mouseHelper, double scrollDelta)
    {
        Event event = new InputEvent.MouseScrollEvent(scrollDelta, mouseHelper.isLeftPressed(), mouseHelper.isMiddlePressed(), mouseHelper.isRightPressed(), mouseHelper.xpos(), mouseHelper.ypos());
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onRawMouseClicked(int button, int action, int mods)
    {
        return MinecraftForge.EVENT_BUS.post(new InputEvent.RawMouseEvent(button, action, mods));
    }

    public static InputEvent.ClickInputEvent onClickInput(int button, KeyMapping keyBinding, InteractionHand hand)
    {
        InputEvent.ClickInputEvent event = new InputEvent.ClickInputEvent(button, keyBinding, hand);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void drawItemLayered(ItemRenderer renderer, BakedModel modelIn, ItemStack itemStackIn, PoseStack matrixStackIn,
                                       MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, boolean fabulous)
    {
        for(com.mojang.datafixers.util.Pair<BakedModel,RenderType> layerModel : modelIn.getLayerModels(itemStackIn, fabulous))
        {
            BakedModel layer = layerModel.getFirst();
            RenderType rendertype = layerModel.getSecond();
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(rendertype); // neded for compatibility with MultiLayerModels
            VertexConsumer ivertexbuilder;
            if (fabulous)
            {
                ivertexbuilder = ItemRenderer.getFoilBufferDirect(bufferIn, rendertype, true, itemStackIn.hasFoil());
            } else {
                ivertexbuilder = ItemRenderer.getFoilBuffer(bufferIn, rendertype, true, itemStackIn.hasFoil());
            }
            renderer.renderModelLists(layer, itemStackIn, combinedLightIn, combinedOverlayIn, matrixStackIn, ivertexbuilder);
        }
        net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
    }

    public static boolean isNameplateInRenderDistance(Entity entity, double squareDistance) {
        if (entity instanceof LivingEntity) {
            final AttributeInstance attribute = ((LivingEntity) entity).getAttribute(ForgeMod.NAMETAG_DISTANCE.get());
            if (attribute != null) {
                return !(squareDistance > (attribute.getValue() * attribute.getValue()));
            }
        }
        return !(squareDistance > 4096.0f);
    }

    public static void renderPistonMovedBlocks(BlockPos pos, BlockState state, PoseStack stack, MultiBufferSource buffer, Level world, boolean checkSides, int combinedOverlay, BlockRenderDispatcher blockRenderer) {
        RenderType.chunkBufferLayers().stream()
                .filter(t -> ItemBlockRenderTypes.canRenderInLayer(state, t))
                .forEach(rendertype ->
                {
                    setRenderLayer(rendertype);
                    VertexConsumer ivertexbuilder = buffer.getBuffer(rendertype == RenderType.translucent() ? RenderType.translucentMovingBlock() : rendertype);
                    blockRenderer.getModelRenderer().tesselateBlock(world, blockRenderer.getBlockModel(state), state, pos, stack, ivertexbuilder, checkSides, new Random(), state.getSeed(pos), combinedOverlay);
                });
        setRenderLayer(null);
    }

    public static void registerForgeWorldTypeScreens()
    {
        ForgeWorldTypeScreens.registerTypes();
    }

    public static WorldPreset.PresetEditor getBiomeGeneratorTypeScreenFactory(Optional<WorldPreset> generator, @Nullable WorldPreset.PresetEditor biomegeneratortypescreens$ifactory)
    {
        return ForgeWorldTypeScreens.getGeneratorScreenFactory(generator, biomegeneratortypescreens$ifactory);
    }

    public static boolean hasBiomeGeneratorSettingsOptionsScreen(Optional<WorldPreset> generator)
    {
        return getBiomeGeneratorTypeScreenFactory(generator, null) != null;
    }

    public static Optional<WorldPreset> getWorldTypeFromGenerator(WorldGenSettings dimensionGeneratorSettings)
    {
        return WorldPreset.of(dimensionGeneratorSettings);
    }

    public static Optional<WorldPreset> getDefaultWorldType()
    {
        return Optional.of(ForgeWorldTypeScreens.getDefaultGenerator());
    }

    public static boolean shouldRender(MobEffectInstance effectInstance)
    {
        return RenderProperties.getEffectRenderer(effectInstance).shouldRender(effectInstance);
    }

    @Nullable
    public static TextureAtlasSprite loadTextureAtlasSprite(
            TextureAtlas textureAtlas,
            ResourceManager resourceManager, TextureAtlasSprite.Info textureInfo,
            Resource resource,
            int atlasWidth, int atlasHeight,
            int spriteX, int spriteY, int mipmapLevel,
            NativeImage image
    )
    {
        ForgeTextureMetadata metadata = ForgeTextureMetadata.forResource(resource);
        return metadata.getLoader() == null ? null : metadata.getLoader().load(textureAtlas, resourceManager, textureInfo, resource, atlasWidth, atlasHeight, spriteX, spriteY, mipmapLevel, image);
    }


    private static final Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions = new HashMap<>();

    public static void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier)
    {
        layerDefinitions.put(layerLocation, supplier);
    }

    public static void loadLayerDefinitions(ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder) {
        layerDefinitions.forEach((k, v) -> builder.put(k, v.get()));
    }

    public static void processForgeListPingData(ServerStatus packet, ServerData target)
    {
        if (packet.getForgeData() != null) {
            final Map<String, String> mods = packet.getForgeData().getRemoteModData();
            final Map<ResourceLocation, Pair<String, Boolean>> remoteChannels = packet.getForgeData().getRemoteChannels();
            final int fmlver = packet.getForgeData().getFMLNetworkVersion();

            boolean fmlNetMatches = fmlver == FMLNetworkConstants.FMLNETVERSION;
            boolean channelsMatch = NetworkRegistry.checkListPingCompatibilityForClient(remoteChannels);
            AtomicBoolean result = new AtomicBoolean(true);
            final List<String> extraClientMods = new ArrayList<>();
            ModList.get().forEachModContainer((modid, mc) ->
                    mc.getCustomExtension(IExtensionPoint.DisplayTest.class).ifPresent(ext-> {
                        boolean foundModOnServer = ext.remoteVersionTest().test(mods.get(modid), true);
                        result.compareAndSet(true, foundModOnServer);
                        if (!foundModOnServer) {
                            extraClientMods.add(modid);
                        }
                    })
            );
            boolean modsMatch = result.get();

            final Map<String, String> extraServerMods = mods.entrySet().stream().
                    filter(e -> !Objects.equals(FMLNetworkConstants.IGNORESERVERONLY, e.getValue())).
                    filter(e -> !ModList.get().isLoaded(e.getKey())).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            LOGGER.debug(CLIENTHOOKS, "Received FML ping data from server at {}: FMLNETVER={}, mod list is compatible : {}, channel list is compatible: {}, extra server mods: {}", target.ip, fmlver, modsMatch, channelsMatch, extraServerMods);

            String extraReason = null;

            if (!extraServerMods.isEmpty()) {
                extraReason = "fml.menu.multiplayer.extraservermods";
                LOGGER.info(CLIENTHOOKS, ForgeI18n.parseMessage(extraReason) + ": {}", extraServerMods.entrySet().stream()
                        .map(e -> e.getKey() + "@" + e.getValue())
                        .collect(Collectors.joining(", ")));
            }
            if (!modsMatch) {
                extraReason = "fml.menu.multiplayer.modsincompatible";
                LOGGER.info(CLIENTHOOKS, "Client has mods that are missing on server: {}", extraClientMods);
            }
            if (!channelsMatch) {
                extraReason = "fml.menu.multiplayer.networkincompatible";
            }

            if (fmlver < FMLNetworkConstants.FMLNETVERSION) {
                extraReason = "fml.menu.multiplayer.serveroutdated";
            }
            if (fmlver > FMLNetworkConstants.FMLNETVERSION) {
                extraReason = "fml.menu.multiplayer.clientoutdated";
            }
            target.forgeData = new ExtendedServerListData("FML", extraServerMods.isEmpty() && fmlNetMatches && channelsMatch && modsMatch, mods.size(), extraReason, packet.getForgeData().isTruncated());
        } else {
            target.forgeData = new ExtendedServerListData("VANILLA", NetworkRegistry.canConnectToVanillaServer(),0, null);
        }

    }

    private static final ResourceLocation ICON_SHEET = new ResourceLocation(ForgeVersion.MOD_ID, "textures/gui/icons.png");
    public static void drawForgePingInfo(JoinMultiplayerScreen gui, ServerData target, PoseStack mStack, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
        int idx;
        String tooltip;
        if (target.forgeData == null)
            return;
        switch (target.forgeData.type) {
            case "FML":
                if (target.forgeData.isCompatible) {
                    idx = 0;
                    tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.compatible", target.forgeData.numberOfMods);
                } else {
                    idx = 16;
                    if(target.forgeData.extraReason != null) {
                        String extraReason = ForgeI18n.parseMessage(target.forgeData.extraReason);
                        tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible.extra", extraReason);
                    } else {
                        tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible");
                    }
                }
                if (target.forgeData.truncated)
                {
                    tooltip += "\n" + ForgeI18n.parseMessage("fml.menu.multiplayer.truncated");
                }
                break;
            case "VANILLA":
                if (target.forgeData.isCompatible) {
                    idx = 48;
                    tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla");
                } else {
                    idx = 80;
                    tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla.incompatible");
                }
                break;
            default:
                idx = 64;
                tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.unknown", target.forgeData.type);
        }

        RenderSystem.setShaderTexture(0, ICON_SHEET);
        GuiComponent.blit(mStack, x + width - 18, y + 10, 16, 16, 0, idx, 16, 16, 256, 256);

        if(relativeMouseX > width - 15 && relativeMouseX < width && relativeMouseY > 10 && relativeMouseY < 26) {
            //this is not the most proper way to do it,
            //but works best here and has the least maintenance overhead
            gui.setToolTip(Arrays.stream(tooltip.split("\n")).map(TextComponent::new).collect(Collectors.toList()));
        }
    }

    private static Connection getClientToServerNetworkManager()
    {
        return Minecraft.getInstance().getConnection()!=null ? Minecraft.getInstance().getConnection().getConnection() : null;
    }

    public static void handleClientWorldClosing(ClientLevel world)
    {
        Connection client = getClientToServerNetworkManager();
        // ONLY revert a non-local connection
        if (client != null && !client.isMemoryConnection())
        {
            GameData.revertToFrozen();
        }
    }

    public static void firePlayerLogin(MultiPlayerGameMode pc, LocalPlayer player, Connection networkManager) {
        MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggedInEvent(pc, player, networkManager));
    }

    public static void firePlayerLogout(MultiPlayerGameMode pc, LocalPlayer player) {
        MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggedOutEvent(pc, player, player != null ? player.connection != null ? player.connection.getConnection() : null : null));
    }

    public static void firePlayerRespawn(MultiPlayerGameMode pc, LocalPlayer oldPlayer, LocalPlayer newPlayer, Connection networkManager) {
        MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.RespawnEvent(pc, oldPlayer, newPlayer, networkManager));
    }


    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid="forge", bus= Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientEvents
    {
        @Nullable
        private static ShaderInstance rendertypeEntityTranslucentUnlitShader;

        public static ShaderInstance getEntityTranslucentUnlitShader()
        {
            return Objects.requireNonNull(rendertypeEntityTranslucentUnlitShader, "Attempted to call getEntityTranslucentUnlitShader before shaders have finished loading.");
        }

        @SubscribeEvent
        public static void registerShaders(RegisterShadersEvent event) throws IOException
        {
            event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation("forge","rendertype_entity_unlit_translucent"), DefaultVertexFormat.NEW_ENTITY), (p_172645_) -> {
                rendertypeEntityTranslucentUnlitShader = p_172645_;
            });
        }
    }

    public static Font getTooltipFont(@Nullable Font forcedFont, @Nonnull ItemStack stack, Font fallbackFont)
    {
        if (forcedFont != null)
        {
            return forcedFont;
        }
        Font stackFont = RenderProperties.get(stack).getFont(stack);
        return stackFont == null ? fallbackFont : stackFont;
    }

    public static RenderTooltipEvent.Pre preTooltipEvent(@Nonnull ItemStack stack, PoseStack matrixStack, int x, int y, int screenWidth, int screenHeight, @Nonnull List<ClientTooltipComponent> components, @Nullable Font forcedFont, @Nonnull Font fallbackFont)
    {
        var preEvent = new RenderTooltipEvent.Pre(stack, matrixStack, x, y, screenWidth, screenHeight, getTooltipFont(forcedFont, stack, fallbackFont), components);
        MinecraftForge.EVENT_BUS.post(preEvent);
        return preEvent;
    }

    public static RenderTooltipEvent.Color colorTooltipEvent(@Nonnull ItemStack stack, PoseStack matrixStack, int x, int y, @Nonnull Font font, @Nonnull List<ClientTooltipComponent> components)
    {
        var colorEvent = new RenderTooltipEvent.Color(stack, matrixStack, x, y, font, 0xf0100010, 0x505000FF, 0x5028007f, components);
        MinecraftForge.EVENT_BUS.post(colorEvent);
        return colorEvent;
    }

    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, int mouseX, int screenWidth, int screenHeight, @Nullable Font forcedFont, Font fallbackFont)
    {
        return gatherTooltipComponents(stack, textElements, Optional.empty(), mouseX, screenWidth, screenHeight, forcedFont, fallbackFont);
    }

    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, @Nullable Font forcedFont, Font fallbackFont)
    {
        Font font = getTooltipFont(forcedFont, stack, fallbackFont);
        List<Either<FormattedText, TooltipComponent>> elements = textElements.stream()
                .map((Function<FormattedText, Either<FormattedText, TooltipComponent>>) Either::left)
                .collect(Collectors.toCollection(ArrayList::new));
        itemComponent.ifPresent(c -> elements.add(Either.right(c)));

        var event = new RenderTooltipEvent.GatherComponents(stack, screenWidth, screenHeight, elements, -1);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) return List.of();

        // text wrapping
        int tooltipTextWidth = event.getTooltipElements().stream()
                .mapToInt(either -> either.map(font::width, component -> 0))
                .max()
                .orElse(0);

        boolean needsWrap = false;

        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth)
        {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) // if the tooltip doesn't fit on the screen
            {
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }

        if (event.getMaxWidth() > 0 && tooltipTextWidth > event.getMaxWidth())
        {
            tooltipTextWidth = event.getMaxWidth();
            needsWrap = true;
        }

        int tooltipTextWidthF = tooltipTextWidth;
        if (needsWrap)
        {
            return event.getTooltipElements().stream()
                    .flatMap(either -> either.map(
                            text -> font.split(text, tooltipTextWidthF).stream().map(ClientTooltipComponent::create),
                            component -> Stream.of(ClientTooltipComponent.create(component))
                    ))
                    .toList();
        }
        return event.getTooltipElements().stream()
                .map(either -> either.map(
                        text -> ClientTooltipComponent.create(text instanceof Component ? ((Component) text).getVisualOrderText() : Language.getInstance().getVisualOrder(text)),
                        ClientTooltipComponent::create
                ))
                .toList();
    }

}
