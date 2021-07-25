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
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormatElement.Usage;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.render.EntityViewRenderEvent;
import net.minecraftforge.client.event.render.RenderGameOverlayEvent;
import net.minecraftforge.client.event.render.RenderHandEvent;
import net.minecraftforge.client.event.render.RenderLevelLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.TransformationHelper;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.resource.VanillaResourceType;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.async.ThreadNameCachingStrategy;
import org.apache.logging.log4j.core.impl.ReusableLogEventFactory;
import org.lwjgl.opengl.GL13;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Stream;

import static net.minecraftforge.client.event.render.RenderGameOverlayEvent.ElementType.BOSS_EVENT;
import static net.minecraftforge.fml.VersionChecker.Status.BETA;
import static net.minecraftforge.fml.VersionChecker.Status.BETA_OUTDATED;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ForgeHooksClient
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static String getArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlot slot, String type)
    {
        String result = armor.getItem().getArmorTexture(armor, entity, slot, type);
        return result != null ? result : _default;
    }

    public static boolean onDrawHighlight(LevelRenderer levelRenderer, Camera camera, HitResult hitResult, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
    {
        return switch (hitResult.getType()) {
            case BLOCK -> hitResult instanceof BlockHitResult blockHitResult
                    && MinecraftForge.EVENT_BUS.post(new DrawSelectionEvent.HighlightBlock(levelRenderer, camera, blockHitResult, partialTick, poseStack, bufferSource));
            case ENTITY -> hitResult instanceof EntityHitResult entityHitResult
                    && MinecraftForge.EVENT_BUS.post(new DrawSelectionEvent.HighlightEntity(levelRenderer, camera, entityHitResult, partialTick, poseStack, bufferSource));
            default -> MinecraftForge.EVENT_BUS.post(new DrawSelectionEvent(levelRenderer, camera, hitResult, partialTick, poseStack, bufferSource));
        };
    }

    public static void onRenderLast(LevelRenderer levelRenderer, PoseStack poseStack, float partialTick, Matrix4f projectionMatrix, long finishTimeNano)
    {
        MinecraftForge.EVENT_BUS.post(new RenderLevelLastEvent(levelRenderer, poseStack, partialTick, projectionMatrix, finishTimeNano));
    }

    public static boolean renderSpecificFirstPersonHand(InteractionHand hand, PoseStack poseStack, MultiBufferSource bufferSource, int light, float partialTick, float lerpPitch, float swingProgress, float equipProgress, ItemStack stack)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderHandEvent(hand, poseStack, bufferSource, light, partialTick, lerpPitch, swingProgress, equipProgress, stack));
    }

    public static void onTextureStitchPre(TextureAtlas atlas, Set<ResourceLocation> resourceLocations)
    {
        StartupMessageManager.mcLoaderConsumer().ifPresent(c -> c.accept("Atlas Stitching : " + atlas.location()));
        ModLoader.get().postEvent(new TextureStitchEvent.Pre(atlas, resourceLocations));
//        ModelLoader.White.INSTANCE.register(map); // TODO Custom TAS
        Sheets.SIGN_MATERIALS.values().stream()
                .filter(rm -> rm.atlasLocation().equals(atlas.location()))
                .forEach(rm -> resourceLocations.add(rm.texture()));
    }

    public static void onTextureStitchedPost(TextureAtlas atlas)
    {
        ModLoader.get().postEvent(new TextureStitchEvent.Post(atlas));
    }

    public static void onBlockColors(BlockColors blockColors)
    {
        ModLoader.get().postEvent(new ColorHandlerEvent.Block(blockColors));
    }

    public static void onItemColors(ItemColors itemColors, BlockColors blockColors)
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

        String name = complex.substring(idx + 1);
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
    public static float getFOVModifier(Player entity, float fov)
    {
        FOVModifierEvent fovModifierEvent = new FOVModifierEvent(entity, fov);
        MinecraftForge.EVENT_BUS.post(fovModifierEvent);
        return fovModifierEvent.getNewFOV();
    }

    public static double getFieldOfView(GameRenderer renderer, Camera info, double renderPartialTicks, double fov) {
        EntityViewRenderEvent.FieldOfView event = new EntityViewRenderEvent.FieldOfView(renderer, info, renderPartialTicks, fov);
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

    public static String forgeStatusLine;
    public static void renderMainMenu(TitleScreen screen, PoseStack poseStack, Font font, int width, int height, int alpha)
    {
        VersionChecker.Status status = ForgeVersion.getStatus();
        if (status == BETA || status == BETA_OUTDATED)
        {
            // render a warning at the top of the screen,
            Component line = new TranslatableComponent("forge.update.beta.1", ChatFormatting.RED, ChatFormatting.RESET).withStyle(ChatFormatting.RED);
            GuiComponent.drawCenteredString(poseStack, font, line, width / 2, 4 + (0 * (font.lineHeight + 1)), 0xFFFFFF | alpha);
            line = new TranslatableComponent("forge.update.beta.2");
            GuiComponent.drawCenteredString(poseStack, font, line, width / 2, 4 + (1 * (font.lineHeight + 1)), 0xFFFFFF | alpha);
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

    public static SoundInstance onPlaySound(SoundEngine manager, SoundInstance sound)
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

    public static void drawScreen(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (!MinecraftForge.EVENT_BUS.post(new ScreenEvent.DrawScreenEvent.Pre(screen, poseStack, mouseX, mouseY, partialTick)))
            screen.render(poseStack, mouseX, mouseY, partialTick);
        MinecraftForge.EVENT_BUS.post(new ScreenEvent.DrawScreenEvent.Post(screen, poseStack, mouseX, mouseY, partialTick));
    }

    public static float getFogDensity(FogMode mode, Camera camera, float partial, float density)
    {
        EntityViewRenderEvent.FogDensity event = new EntityViewRenderEvent.FogDensity(mode, camera, partial, density);
        if (MinecraftForge.EVENT_BUS.post(event)) return event.getDensity();
        return -1;
    }

    public static void onFogRender(FogMode mode, Camera camera, float partial, float distance)
    {
        MinecraftForge.EVENT_BUS.post(new EntityViewRenderEvent.RenderFogEvent(mode, camera, partial, distance));
    }

    public static EntityViewRenderEvent.CameraSetup onCameraSetup(GameRenderer renderer, Camera camera, float partialTick)
    {
        EntityViewRenderEvent.CameraSetup event = new EntityViewRenderEvent.CameraSetup(renderer, camera, partialTick, camera.getYRot(), camera.getXRot(), 0);
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

    public static BakedModel handleCameraTransforms(PoseStack poseStack, BakedModel model, ItemTransforms.TransformType transformType, boolean leftHandHackery)
    {
        PoseStack stack = new PoseStack();
        model = model.handlePerspective(transformType, stack);

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
            poseStack.last().pose().multiply(tMat);
            poseStack.last().normal().mul(nMat);
        }
        return model;
    }

    // moved and expanded from WorldVertexBufferUploader.draw

    public static void preDraw(Usage usage, VertexFormat format, int element, int stride, ByteBuffer buffer)
    {
        VertexFormatElement attr = format.getElements().get(element);
        int count = attr.getElementCount();
        int constant = attr.getType().getGlType();
        ((Buffer)buffer).position(format.getOffset(element));
        switch(usage)
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
                LOGGER.fatal("Unimplemented vanilla attribute upload: {}", usage.getName());
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

    public static int getColorIndex(VertexFormat format)
    {
        ImmutableList<VertexFormatElement> elements = format.getElements();
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
    public static TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter level, BlockPos pos, FluidState fluidState)
    {
        ResourceLocation overlayTexture = fluidState.getType().getAttributes().getOverlayTexture();
        return new TextureAtlasSprite[] {
                Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidState.getType().getAttributes().getStillTexture(level, pos)),
                Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(fluidState.getType().getAttributes().getFlowingTexture(level, pos)),
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

    public static RenderGameOverlayEvent.BossEvent onBossEventRenderPre(PoseStack poseStack, Window window, LerpingBossEvent bossEvent, int x, int y, int increment)
    {
        RenderGameOverlayEvent.BossEvent evt = new RenderGameOverlayEvent.BossEvent(poseStack, new RenderGameOverlayEvent(poseStack, Animation.getPartialTickTime(), window),
                BOSS_EVENT, bossEvent, x, y, increment);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static void onBossEventRenderPost(PoseStack poseStack, Window window)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(poseStack, new RenderGameOverlayEvent(poseStack, Animation.getPartialTickTime(), window), BOSS_EVENT));
    }

    public static ScreenshotEvent onScreenshot(NativeImage image, File screenshotFile)
    {
        ScreenshotEvent event = new ScreenshotEvent(image, screenshotFile);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void onClientChangeGameMode(PlayerInfo info, GameType currentGameType, GameType newGameType)
    {
        if (currentGameType != newGameType)
        {
            ClientPlayerChangeGameTypeEvent evt = new ClientPlayerChangeGameTypeEvent(info, currentGameType, newGameType);
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
        MinecraftForge.EVENT_BUS.post(new PlayerInputUpdateEvent(player, movementInput));
    }

    public static void refreshResources(Minecraft mc, VanillaResourceType... types) {
        mc.reloadResourcePacks();
    }

    public static boolean onMouseClickedPre(Screen screen, double mouseX, double mouseY, int button)
    {
        Event event = new ScreenEvent.MouseClickedEvent.Pre(screen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onMouseClickedPost(Screen screen, double mouseX, double mouseY, int button)
    {
        Event event = new ScreenEvent.MouseClickedEvent.Post(screen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onMouseReleasedPre(Screen screen, double mouseX, double mouseY, int button)
    {
        Event event = new ScreenEvent.MouseReleasedEvent.Pre(screen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onMouseReleasedPost(Screen screen, double mouseX, double mouseY, int button)
    {
        Event event = new ScreenEvent.MouseReleasedEvent.Post(screen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onMouseDragPre(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
    {
        Event event = new ScreenEvent.MouseDragEvent.Pre(screen, mouseX, mouseY, mouseButton, dragX, dragY);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onMouseDragPost(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
    {
        Event event = new ScreenEvent.MouseDragEvent.Post(screen, mouseX, mouseY, mouseButton, dragX, dragY);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onMouseScrollPre(MouseHandler handler, Screen screen, double scrollDelta)
    {
        Window mainWindow = screen.getMinecraft().getWindow();
        double mouseX = handler.xpos() * (double) mainWindow.getGuiScaledWidth() / (double) mainWindow.getScreenWidth();
        double mouseY = handler.ypos() * (double) mainWindow.getGuiScaledHeight() / (double) mainWindow.getScreenHeight();
        Event event = new ScreenEvent.MouseScrollEvent.Pre(screen, mouseX, mouseY, scrollDelta);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onMouseScrollPost(MouseHandler handler, Screen screen, double scrollDelta)
    {
        Window mainWindow = screen.getMinecraft().getWindow();
        double mouseX = handler.xpos() * (double) mainWindow.getGuiScaledWidth() / (double) mainWindow.getScreenWidth();
        double mouseY = handler.ypos() * (double) mainWindow.getGuiScaledHeight() / (double) mainWindow.getScreenHeight();
        Event event = new ScreenEvent.MouseScrollEvent.Post(screen, mouseX, mouseY, scrollDelta);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onKeyPressedPre(Screen screen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new ScreenEvent.KeyboardKeyPressedEvent.Pre(screen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onKeyPressedPost(Screen screen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new ScreenEvent.KeyboardKeyPressedEvent.Post(screen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onKeyReleasedPre(Screen screen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new ScreenEvent.KeyboardKeyReleasedEvent.Pre(screen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onKeyReleasedPost(Screen screen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new ScreenEvent.KeyboardKeyReleasedEvent.Post(screen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onCharTypedPre(Screen screen, char codePoint, int modifiers)
    {
        Event event = new ScreenEvent.KeyboardCharTypedEvent.Pre(screen, codePoint, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onCharTypedPost(Screen screen, char codePoint, int modifiers)
    {
        Event event = new ScreenEvent.KeyboardCharTypedEvent.Post(screen, codePoint, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onRecipesUpdated(RecipeManager manager)
    {
        Event event = new RecipesUpdatedEvent(manager);
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

    public static void onMouseInput(int button, int action, int mods)
    {
        MinecraftForge.EVENT_BUS.post(new InputEvent.MouseInputEvent(button, action, mods));
    }

    public static void onKeyInput(int key, int scanCode, int action, int modifiers)
    {
        MinecraftForge.EVENT_BUS.post(new InputEvent.KeyInputEvent(key, scanCode, action, modifiers));
    }

    public static boolean onMouseScroll(MouseHandler handler, double scrollDelta)
    {
        Event event = new InputEvent.MouseScrollEvent(scrollDelta, handler.isLeftPressed(), handler.isMiddlePressed(), handler.isRightPressed(), handler.xpos(), handler.ypos());
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onRawMouseInput(int button, int action, int mods)
    {
        return MinecraftForge.EVENT_BUS.post(new InputEvent.RawMouseEvent(button, action, mods));
    }

    public static InputEvent.ClickInputEvent onClickInput(int button, KeyMapping keyBinding, InteractionHand hand)
    {
        InputEvent.ClickInputEvent event = new InputEvent.ClickInputEvent(button, keyBinding, hand);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void drawItemLayered(ItemRenderer renderer, BakedModel model, ItemStack itemStack, PoseStack poseStack,
                                       MultiBufferSource bufferSource, int light, int overlay, boolean fabulous)
    {
        for(com.mojang.datafixers.util.Pair<BakedModel,RenderType> layerModel : model.getLayerModels(itemStack, fabulous))
        {
            BakedModel layer = layerModel.getFirst();
            RenderType rendertype = layerModel.getSecond();
            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(rendertype); // neded for compatibility with MultiLayerModels
            VertexConsumer ivertexbuilder;
            if (fabulous)
            {
                ivertexbuilder = ItemRenderer.getFoilBufferDirect(bufferSource, rendertype, true, itemStack.hasFoil());
            } else {
                ivertexbuilder = ItemRenderer.getFoilBuffer(bufferSource, rendertype, true, itemStack.hasFoil());
            }
            renderer.renderModelLists(layer, itemStack, light, overlay, poseStack, ivertexbuilder);
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

    public static void renderPistonMovedBlocks(BlockPos pos, BlockState state, PoseStack stack, MultiBufferSource bufferSource, Level level, boolean checkSides, int overlay, BlockRenderDispatcher blockRenderer) {
        RenderType.chunkBufferLayers().stream()
                .filter(t -> ItemBlockRenderTypes.canRenderInLayer(state, t))
                .forEach(rendertype ->
                {
                    setRenderLayer(rendertype);
                    VertexConsumer ivertexbuilder = bufferSource.getBuffer(rendertype == RenderType.translucent() ? RenderType.translucentMovingBlock() : rendertype);
                    blockRenderer.getModelRenderer().tesselateBlock(level, blockRenderer.getBlockModel(state), state, pos, stack, ivertexbuilder, checkSides, new Random(), state.getSeed(pos), overlay);
                });
        setRenderLayer(null);
    }

    public static void registerForgeWorldTypeScreens()
    {
        ForgeWorldTypeScreens.registerTypes();
    }

    public static WorldPreset.PresetEditor getBiomeGeneratorTypeScreenFactory(Optional<WorldPreset> generator, @Nullable WorldPreset.PresetEditor presetEditor)
    {
        return ForgeWorldTypeScreens.getGeneratorScreenFactory(generator, presetEditor);
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
}
