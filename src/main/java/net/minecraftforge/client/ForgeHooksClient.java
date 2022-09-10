/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.FileUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.locale.Language;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MessageSigner;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerChangeGameTypeEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderBlockScreenEffectEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.ToastAddEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.fml.VersionChecker.Status.BETA;
import static net.minecraftforge.fml.VersionChecker.Status.BETA_OUTDATED;

@ApiStatus.Internal
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

    public static void resizeGuiLayers(Minecraft minecraft, int width, int height) {
        guiLayers.forEach(screen -> screen.resize(minecraft, width, height));
    }

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
        minecraft.getNarrator().sayNow(screen.getNarrationMessage());
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
            minecraft.getNarrator().sayNow(minecraft.screen.getNarrationMessage());
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

    public static boolean onDrawHighlight(LevelRenderer context, Camera camera, HitResult target, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource)
    {
        switch (target.getType()) {
            case BLOCK:
                if (!(target instanceof BlockHitResult blockTarget)) return false;
                return MinecraftForge.EVENT_BUS.post(new RenderHighlightEvent.Block(context, camera, blockTarget, partialTick, poseStack, bufferSource));
            case ENTITY:
                if (!(target instanceof EntityHitResult entityTarget)) return false;
                return MinecraftForge.EVENT_BUS.post(new RenderHighlightEvent.Entity(context, camera, entityTarget, partialTick, poseStack, bufferSource));
            default:
                return false; // NO-OP - This doesn't even get called for anything other than blocks and entities
        }
    }

    @Deprecated(forRemoval = true, since = "1.19")
    public static void dispatchRenderLast(LevelRenderer context, PoseStack poseStack, float partialTick, Matrix4f projectionMatrix, long finishTimeNano)
    {
        MinecraftForge.EVENT_BUS.post(new RenderLevelLastEvent(context, poseStack, partialTick, projectionMatrix, finishTimeNano));
    }

    public static void dispatchRenderStage(RenderLevelStageEvent.Stage stage, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, Camera camera, Frustum frustum)
    {
        var mc = Minecraft.getInstance();
        var profiler = mc.getProfiler();
        profiler.push(stage.toString());
        MinecraftForge.EVENT_BUS.post(new RenderLevelStageEvent(stage, levelRenderer, poseStack, projectionMatrix, renderTick, mc.getPartialTick(), camera, frustum));
        profiler.pop();
    }

    public static void dispatchRenderStage(RenderType renderType, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, Camera camera, Frustum frustum)
    {
        RenderLevelStageEvent.Stage stage = RenderLevelStageEvent.Stage.fromRenderType(renderType);
        if (stage != null)
            dispatchRenderStage(stage, levelRenderer, poseStack, projectionMatrix, renderTick, camera, frustum);
    }

    public static boolean renderSpecificFirstPersonHand(InteractionHand hand, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick, float interpPitch, float swingProgress, float equipProgress, ItemStack stack)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderHandEvent(hand, poseStack, bufferSource, packedLight, partialTick, interpPitch, swingProgress, equipProgress, stack));
    }

    public static boolean renderSpecificFirstPersonArm(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, AbstractClientPlayer player, HumanoidArm arm)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderArmEvent(poseStack, multiBufferSource, packedLight, player, arm));
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
        ModLoader.get().postEvent(new RegisterColorHandlersEvent.Block(blockColors));
    }

    public static void onItemColorsInit(ItemColors itemColors, BlockColors blockColors)
    {
        ModLoader.get().postEvent(new RegisterColorHandlersEvent.Item(itemColors, blockColors));
    }

    public static Model getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> _default)
    {
        return IClientItemExtensions.of(itemStack).getGenericArmorModel(entityLiving, itemStack, slot, _default);
    }

    /** Copies humanoid model properties from the original model to another, used for armor models */
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> void copyModelProperties(HumanoidModel<T> original, HumanoidModel<?> replacement)
    {
        // this function does not make use of the <T> generic, so the unchecked cast should be safe
        original.copyPropertiesTo((HumanoidModel<T>)replacement);
        replacement.head.visible = original.head.visible;
        replacement.hat.visible = original.hat.visible;
        replacement.body.visible = original.body.visible;
        replacement.rightArm.visible = original.rightArm.visible;
        replacement.leftArm.visible = original.leftArm.visible;
        replacement.rightLeg.visible = original.rightLeg.visible;
        replacement.leftLeg.visible = original.leftLeg.visible;
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

    public static float getFieldOfViewModifier(Player entity, float fovModifier)
    {
        ComputeFovModifierEvent fovModifierEvent = new ComputeFovModifierEvent(entity, fovModifier);
        MinecraftForge.EVENT_BUS.post(fovModifierEvent);
        return fovModifierEvent.getNewFovModifier();
    }

    public static double getFieldOfView(GameRenderer renderer, Camera camera, double partialTick, double fov, boolean usedConfiguredFov)
    {
        ViewportEvent.ComputeFov event = new ViewportEvent.ComputeFov(renderer, camera, partialTick, fov, usedConfiguredFov);
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

    public static void renderMainMenu(TitleScreen gui, PoseStack poseStack, Font font, int width, int height, int alpha)
    {
        VersionChecker.Status status = ForgeVersion.getStatus();
        if (status == BETA || status == BETA_OUTDATED)
        {
            // render a warning at the top of the screen,
            Component line = Component.translatable("forge.update.beta.1", ChatFormatting.RED, ChatFormatting.RESET).withStyle(ChatFormatting.RED);
            GuiComponent.drawCenteredString(poseStack, font, line, width / 2, 4 + (0 * (font.lineHeight + 1)), 0xFFFFFF | alpha);
            line = Component.translatable("forge.update.beta.2");
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

    public static String forgeStatusLine;
    @Nullable
    public static SoundInstance playSound(SoundEngine manager, SoundInstance sound)
    {
        PlaySoundEvent e = new PlaySoundEvent(manager, sound);
        MinecraftForge.EVENT_BUS.post(e);
        return e.getSound();
    }

    public static void drawScreen(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        poseStack.pushPose();
        guiLayers.forEach(layer -> {
            // Prevent the background layers from thinking the mouse is over their controls and showing them as highlighted.
            drawScreenInternal(layer, poseStack, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTick);
            poseStack.translate(0,0,2000);
        });
        drawScreenInternal(screen, poseStack, mouseX, mouseY, partialTick);
        poseStack.popPose();
    }

    private static void drawScreenInternal(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        if (!MinecraftForge.EVENT_BUS.post(new ScreenEvent.Render.Pre(screen, poseStack, mouseX, mouseY, partialTick)))
            screen.render(poseStack, mouseX, mouseY, partialTick);
        MinecraftForge.EVENT_BUS.post(new ScreenEvent.Render.Post(screen, poseStack, mouseX, mouseY, partialTick));
    }

    public static Vector3f getFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, float fogRed, float fogGreen, float fogBlue)
    {
        // Modify fog color depending on the fluid
        FluidState state = level.getFluidState(camera.getBlockPosition());
        Vector3f fluidFogColor = new Vector3f(fogRed, fogGreen, fogBlue);
        if (camera.getPosition().y < (double)((float)camera.getBlockPosition().getY() + state.getHeight(level, camera.getBlockPosition())))
            fluidFogColor = IClientFluidTypeExtensions.of(state).modifyFogColor(camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor);

        ViewportEvent.ComputeFogColor event = new ViewportEvent.ComputeFogColor(camera, partialTick, fluidFogColor.x(), fluidFogColor.y(), fluidFogColor.z());
        MinecraftForge.EVENT_BUS.post(event);

        fluidFogColor.set(event.getRed(), event.getGreen(), event.getBlue());
        return fluidFogColor;
    }

    public static void onFogRender(FogRenderer.FogMode mode, FogType type, Camera camera, float partialTick, float renderDistance, float nearDistance, float farDistance, FogShape shape)
    {
        // Modify fog rendering depending on the fluid
        FluidState state = camera.getEntity().level.getFluidState(camera.getBlockPosition());
        if (camera.getPosition().y < (double)((float)camera.getBlockPosition().getY() + state.getHeight(camera.getEntity().level, camera.getBlockPosition())))
            IClientFluidTypeExtensions.of(state).modifyFogRender(camera, mode, renderDistance, partialTick, nearDistance, farDistance, shape);

        ViewportEvent.RenderFog event = new ViewportEvent.RenderFog(mode, type, camera, partialTick, nearDistance, farDistance, shape);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            RenderSystem.setShaderFogStart(event.getNearPlaneDistance());
            RenderSystem.setShaderFogEnd(event.getFarPlaneDistance());
            RenderSystem.setShaderFogShape(event.getFogShape());
        }
    }

    public static ViewportEvent.ComputeCameraAngles onCameraSetup(GameRenderer renderer, Camera camera, float partial)
    {
        ViewportEvent.ComputeCameraAngles event = new ViewportEvent.ComputeCameraAngles(renderer, camera, partial, camera.getYRot(), camera.getXRot(), 0);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void onModelBake(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery)
    {
        ModLoader.get().postEvent(new ModelEvent.BakingCompleted(modelManager, models, modelBakery));
    }

    public static BakedModel handleCameraTransforms(PoseStack poseStack, BakedModel model, ItemTransforms.TransformType cameraTransformType, boolean applyLeftHandTransform)
    {
        model = model.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
        return model;
    }

    @SuppressWarnings("deprecation")
    public static TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter level, BlockPos pos, FluidState fluidStateIn)
    {
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStateIn);
        ResourceLocation overlayTexture = props.getOverlayTexture(fluidStateIn, level, pos);
        return new TextureAtlasSprite[] {
                Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(props.getStillTexture(fluidStateIn, level, pos)),
                Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(props.getFlowingTexture(fluidStateIn, level, pos)),
                overlayTexture == null ? null : Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(overlayTexture),
        };
    }

    public static void gatherFluidTextures(Set<Material> textures)
    {
        if (!ModLoader.isLoadingStateValid()) return;

        ForgeRegistries.FLUIDS.getValues().stream()
                .flatMap(ForgeHooksClient::getFluidMaterials)
                .forEach(textures::add);
    }

    public static Stream<Material> getFluidMaterials(Fluid fluid)
    {
        return IClientFluidTypeExtensions.of(fluid).getTextures()
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

    public static boolean calculateFaceWithoutAO(BlockAndTintGetter getter, BlockState state, BlockPos pos, BakedQuad quad, boolean isFaceCubic, float[] brightness, int[] lightmap)
    {
        if (quad.hasAmbientOcclusion())
            return false;

        BlockPos lightmapPos = isFaceCubic ? pos.relative(quad.getDirection()) : pos;

        brightness[0] = brightness[1] = brightness[2] = brightness[3] = getter.getShade(quad.getDirection(), quad.isShade());
        lightmap[0] = lightmap[1] = lightmap[2] = lightmap[3] = LevelRenderer.getLightColor(getter, state, lightmapPos);
        return true;
    }

    public static void loadEntityShader(Entity entity, GameRenderer entityRenderer)
    {
        if (entity != null)
        {
            ResourceLocation shader = EntitySpectatorShaderManager.get(entity.getType());
            if (shader != null)
            {
                entityRenderer.loadEffect(shader);
            }
        }
    }

    private static int slotMainHand = 0;

    public static boolean shouldCauseReequipAnimation(@NotNull ItemStack from, @NotNull ItemStack to, int slot)
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

    public static CustomizeGuiOverlayEvent.BossEventProgress onCustomizeBossEventProgress(PoseStack poseStack, Window window, LerpingBossEvent bossInfo, int x, int y, int increment)
    {
        CustomizeGuiOverlayEvent.BossEventProgress evt = new CustomizeGuiOverlayEvent.BossEventProgress(window, poseStack,
                Minecraft.getInstance().getPartialTick(), bossInfo, x, y, increment);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt;
    }

    public static ScreenshotEvent onScreenshot(NativeImage image, File screenshotFile)
    {
        ScreenshotEvent event = new ScreenshotEvent(image, screenshotFile);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static void onClientChangeGameType(PlayerInfo info, GameType currentGameMode, GameType newGameMode)
    {
        if (currentGameMode != newGameMode)
        {
            ClientPlayerChangeGameTypeEvent evt = new ClientPlayerChangeGameTypeEvent(info, currentGameMode, newGameMode);
            MinecraftForge.EVENT_BUS.post(evt);
        }
    }

    public static void onMovementInputUpdate(Player player, Input movementInput)
    {
        MinecraftForge.EVENT_BUS.post(new MovementInputUpdateEvent(player, movementInput));
    }

    public static boolean onScreenMouseClickedPre(Screen guiScreen, double mouseX, double mouseY, int button)
    {
        Event event = new ScreenEvent.MouseButtonPressed.Pre(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onScreenMouseClickedPost(Screen guiScreen, double mouseX, double mouseY, int button, boolean handled)
    {
        Event event = new ScreenEvent.MouseButtonPressed.Post(guiScreen, mouseX, mouseY, button, handled);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() == Event.Result.DEFAULT ? handled : event.getResult() == Event.Result.ALLOW;
    }

    public static boolean onScreenMouseReleasedPre(Screen guiScreen, double mouseX, double mouseY, int button)
    {
        Event event = new ScreenEvent.MouseButtonReleased.Pre(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onScreenMouseReleasedPost(Screen guiScreen, double mouseX, double mouseY, int button, boolean handled)
    {
        Event event = new ScreenEvent.MouseButtonReleased.Post(guiScreen, mouseX, mouseY, button, handled);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() == Event.Result.DEFAULT ? handled : event.getResult() == Event.Result.ALLOW;
    }

    public static boolean onScreenMouseDragPre(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
    {
        Event event = new ScreenEvent.MouseDragged.Pre(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onScreenMouseDragPost(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY)
    {
        Event event = new ScreenEvent.MouseDragged.Post(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onScreenMouseScrollPre(MouseHandler mouseHelper, Screen guiScreen, double scrollDelta)
    {
        Window mainWindow = guiScreen.getMinecraft().getWindow();
        double mouseX = mouseHelper.xpos() * (double) mainWindow.getGuiScaledWidth() / (double) mainWindow.getScreenWidth();
        double mouseY = mouseHelper.ypos() * (double) mainWindow.getGuiScaledHeight() / (double) mainWindow.getScreenHeight();
        Event event = new ScreenEvent.MouseScrolled.Pre(guiScreen, mouseX, mouseY, scrollDelta);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onScreenMouseScrollPost(MouseHandler mouseHelper, Screen guiScreen, double scrollDelta)
    {
        Window mainWindow = guiScreen.getMinecraft().getWindow();
        double mouseX = mouseHelper.xpos() * (double) mainWindow.getGuiScaledWidth() / (double) mainWindow.getScreenWidth();
        double mouseY = mouseHelper.ypos() * (double) mainWindow.getGuiScaledHeight() / (double) mainWindow.getScreenHeight();
        Event event = new ScreenEvent.MouseScrolled.Post(guiScreen, mouseX, mouseY, scrollDelta);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onScreenKeyPressedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new ScreenEvent.KeyPressed.Pre(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onScreenKeyPressedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new ScreenEvent.KeyPressed.Post(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onScreenKeyReleasedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new ScreenEvent.KeyReleased.Pre(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onScreenKeyReleasedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers)
    {
        Event event = new ScreenEvent.KeyReleased.Post(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onScreenCharTypedPre(Screen guiScreen, char codePoint, int modifiers)
    {
        Event event = new ScreenEvent.CharacterTyped.Pre(guiScreen, codePoint, modifiers);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onScreenCharTypedPost(Screen guiScreen, char codePoint, int modifiers)
    {
        Event event = new ScreenEvent.CharacterTyped.Post(guiScreen, codePoint, modifiers);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onRecipesUpdated(RecipeManager mgr)
    {
        Event event = new RecipesUpdatedEvent(mgr);
        MinecraftForge.EVENT_BUS.post(event);
    }

    public static boolean onMouseButtonPre(int button, int action, int mods)
    {
        return MinecraftForge.EVENT_BUS.post(new InputEvent.MouseButton.Pre(button, action, mods));
    }

    public static void onMouseButtonPost(int button, int action, int mods)
    {
        MinecraftForge.EVENT_BUS.post(new InputEvent.MouseButton.Post(button, action, mods));
    }

    public static boolean onMouseScroll(MouseHandler mouseHelper, double scrollDelta)
    {
        Event event = new InputEvent.MouseScrollingEvent(scrollDelta, mouseHelper.isLeftPressed(), mouseHelper.isMiddlePressed(), mouseHelper.isRightPressed(), mouseHelper.xpos(), mouseHelper.ypos());
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static void onKeyInput(int key, int scanCode, int action, int modifiers)
    {
        MinecraftForge.EVENT_BUS.post(new InputEvent.Key(key, scanCode, action, modifiers));
    }

    public static InputEvent.InteractionKeyMappingTriggered onClickInput(int button, KeyMapping keyBinding, InteractionHand hand)
    {
        InputEvent.InteractionKeyMappingTriggered event = new InputEvent.InteractionKeyMappingTriggered(button, keyBinding, hand);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
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

    public static void renderPistonMovedBlocks(BlockPos pos, BlockState state, PoseStack stack, MultiBufferSource bufferSource, Level level, boolean checkSides, int packedOverlay, BlockRenderDispatcher blockRenderer) {
        var model = blockRenderer.getBlockModel(state);
        for (var renderType : model.getRenderTypes(state, RandomSource.create(state.getSeed(pos)), ModelData.EMPTY))
        {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypeHelper.getMovingBlockRenderType(renderType));
            blockRenderer.getModelRenderer().tesselateBlock(level, model, state, pos, stack, vertexConsumer, checkSides, RandomSource.create(), state.getSeed(pos), packedOverlay, ModelData.EMPTY, renderType);
        }
    }

    public static boolean shouldRenderEffect(MobEffectInstance effectInstance)
    {
        return IClientMobEffectExtensions.of(effectInstance).isVisibleInInventory(effectInstance);
    }

    @Nullable
    public static TextureAtlasSprite loadTextureAtlasSprite(
            TextureAtlas textureAtlas,
            ResourceManager resourceManager, TextureAtlasSprite.Info textureInfo,
            Resource resource,
            int atlasWidth, int atlasHeight,
            int spriteX, int spriteY, int mipmapLevel,
            NativeImage image
    ) throws IOException
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

            boolean fmlNetMatches = fmlver == NetworkConstants.FMLNETVERSION;
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
                    filter(e -> !Objects.equals(NetworkConstants.IGNORESERVERONLY, e.getValue())).
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

            if (fmlver < NetworkConstants.FMLNETVERSION) {
                extraReason = "fml.menu.multiplayer.serveroutdated";
            }
            if (fmlver > NetworkConstants.FMLNETVERSION) {
                extraReason = "fml.menu.multiplayer.clientoutdated";
            }
            target.forgeData = new ExtendedServerListData("FML", extraServerMods.isEmpty() && fmlNetMatches && channelsMatch && modsMatch, mods.size(), extraReason, packet.getForgeData().isTruncated());
        } else {
            target.forgeData = new ExtendedServerListData("VANILLA", NetworkRegistry.canConnectToVanillaServer(),0, null);
        }

    }

    private static final ResourceLocation ICON_SHEET = new ResourceLocation(ForgeVersion.MOD_ID, "textures/gui/icons.png");
    public static void drawForgePingInfo(JoinMultiplayerScreen gui, ServerData target, PoseStack poseStack, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
        int idx;
        String tooltip;
        if (target.forgeData == null)
            return;
        switch (target.forgeData.type()) {
            case "FML":
                if (target.forgeData.isCompatible()) {
                    idx = 0;
                    tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.compatible", target.forgeData.numberOfMods());
                } else {
                    idx = 16;
                    if(target.forgeData.extraReason() != null) {
                        String extraReason = ForgeI18n.parseMessage(target.forgeData.extraReason());
                        tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible.extra", extraReason);
                    } else {
                        tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible");
                    }
                }
                if (target.forgeData.truncated())
                {
                    tooltip += "\n" + ForgeI18n.parseMessage("fml.menu.multiplayer.truncated");
                }
                break;
            case "VANILLA":
                if (target.forgeData.isCompatible()) {
                    idx = 48;
                    tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla");
                } else {
                    idx = 80;
                    tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla.incompatible");
                }
                break;
            default:
                idx = 64;
                tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.unknown", target.forgeData.type());
        }

        RenderSystem.setShaderTexture(0, ICON_SHEET);
        GuiComponent.blit(poseStack, x + width - 18, y + 10, 16, 16, 0, idx, 16, 16, 256, 256);

        if(relativeMouseX > width - 15 && relativeMouseX < width && relativeMouseY > 10 && relativeMouseY < 26) {
            //this is not the most proper way to do it,
            //but works best here and has the least maintenance overhead
            gui.setToolTip(Arrays.stream(tooltip.split("\n")).map(Component::literal).collect(Collectors.toList()));
        }
    }

    private static Connection getClientConnection()
    {
        return Minecraft.getInstance().getConnection()!=null ? Minecraft.getInstance().getConnection().getConnection() : null;
    }

    public static void handleClientLevelClosing(ClientLevel level)
    {
        Connection client = getClientConnection();
        // ONLY revert a non-local connection
        if (client != null && !client.isMemoryConnection())
        {
            GameData.revertToFrozen();
        }
    }

    public static void firePlayerLogin(MultiPlayerGameMode pc, LocalPlayer player, Connection networkManager) {
        MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggingIn(pc, player, networkManager));
    }

    public static void firePlayerLogout(@Nullable MultiPlayerGameMode pc, @Nullable LocalPlayer player) {
        MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.LoggingOut(pc, player, player != null ? player.connection != null ? player.connection.getConnection() : null : null));
    }

    public static void firePlayerRespawn(MultiPlayerGameMode pc, LocalPlayer oldPlayer, LocalPlayer newPlayer, Connection networkManager) {
        MinecraftForge.EVENT_BUS.post(new ClientPlayerNetworkEvent.Clone(pc, oldPlayer, newPlayer, networkManager));
    }

    public static void onRegisterParticleProviders(ParticleEngine particleEngine) {
        ModLoader.get().postEvent(new RegisterParticleProvidersEvent(particleEngine));
    }

    public static void onRegisterKeyMappings(Options options) {
        ModLoader.get().postEvent(new RegisterKeyMappingsEvent(options));
    }

    public static void onRegisterAdditionalModels(Set<ResourceLocation> additionalModels) {
        ModLoader.get().postEvent(new ModelEvent.RegisterAdditional(additionalModels));
    }

    @Nullable
    public static Component onClientChat(ChatType.Bound boundChatType, Component message, PlayerChatMessage playerChatMessage, MessageSigner messageSigner)
    {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(boundChatType, message, playerChatMessage, messageSigner);
        return MinecraftForge.EVENT_BUS.post(event) ? null : event.getMessage();
    }

    @NotNull
    public static String onClientSendMessage(String message)
    {
        ClientChatEvent event = new ClientChatEvent(message);
        return MinecraftForge.EVENT_BUS.post(event) ? "" : event.getMessage();
    }

    /**
     * Mimics the behavior of {@link net.minecraft.client.renderer.ItemBlockRenderTypes#getRenderType(BlockState, boolean)}
     * for the input {@link RenderType}.
     */
    @NotNull
    public static RenderType getEntityRenderType(RenderType chunkRenderType, boolean cull)
    {
        return RenderTypeHelper.getEntityRenderType(chunkRenderType, cull);
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

    public static Font getTooltipFont(@Nullable Font forcedFont, @NotNull ItemStack stack, Font fallbackFont)
    {
        if (forcedFont != null)
        {
            return forcedFont;
        }
        Font stackFont = IClientItemExtensions.of(stack).getFont(stack, IClientItemExtensions.FontContext.TOOLTIP);
        return stackFont == null ? fallbackFont : stackFont;
    }

    public static RenderTooltipEvent.Pre onRenderTooltipPre(@NotNull ItemStack stack, PoseStack poseStack, int x, int y, int screenWidth, int screenHeight, @NotNull List<ClientTooltipComponent> components, @Nullable Font forcedFont, @NotNull Font fallbackFont)
    {
        var preEvent = new RenderTooltipEvent.Pre(stack, poseStack, x, y, screenWidth, screenHeight, getTooltipFont(forcedFont, stack, fallbackFont), components);
        MinecraftForge.EVENT_BUS.post(preEvent);
        return preEvent;
    }

    public static RenderTooltipEvent.Color onRenderTooltipColor(@NotNull ItemStack stack, PoseStack poseStack, int x, int y, @NotNull Font font, @NotNull List<ClientTooltipComponent> components)
    {
        var colorEvent = new RenderTooltipEvent.Color(stack, poseStack, x, y, font, 0xf0100010, 0x505000FF, 0x5028007f, components);
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
        itemComponent.ifPresent(c -> elements.add(1, Either.right(c)));

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

    public static Comparator<ParticleRenderType> makeParticleRenderTypeComparator(List<ParticleRenderType> renderOrder)
    {
        Comparator<ParticleRenderType> vanillaComparator = Comparator.comparingInt(renderOrder::indexOf);
        return (typeOne, typeTwo) ->
        {
            boolean vanillaOne = renderOrder.contains(typeOne);
            boolean vanillaTwo = renderOrder.contains(typeTwo);

            if (vanillaOne && vanillaTwo)
            {
                return vanillaComparator.compare(typeOne, typeTwo);
            }
            if (!vanillaOne && !vanillaTwo)
            {
                return Integer.compare(System.identityHashCode(typeOne), System.identityHashCode(typeTwo));
            }
            return vanillaOne ? -1 : 1;
        };
    }

    public static ScreenEvent.RenderInventoryMobEffects onScreenPotionSize(Screen screen, int availableSpace, boolean compact, int horizontalOffset)
    {
        final ScreenEvent.RenderInventoryMobEffects event = new ScreenEvent.RenderInventoryMobEffects(screen, availableSpace, compact, horizontalOffset);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public static boolean onToastAdd(Toast toast)
    {
        return MinecraftForge.EVENT_BUS.post(new ToastAddEvent(toast));
    }

    public static boolean isBlockInSolidLayer(BlockState state)
    {
        var model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        return model.getRenderTypes(state, RandomSource.create(), ModelData.EMPTY).contains(RenderType.solid());
    }

    public static void createWorldConfirmationScreen(Runnable doConfirmedWorldLoad)
    {
        Component title = Component.translatable("selectWorld.backupQuestion.experimental");
        Component msg = Component.translatable("selectWorld.backupWarning.experimental")
                .append("\n\n")
                .append(Component.translatable("forge.selectWorld.backupWarning.experimental.additional"));

        Screen screen = new ConfirmScreen(confirmed ->
        {
            if (confirmed)
            {
                doConfirmedWorldLoad.run();
            }
            else
            {
                Minecraft.getInstance().setScreen(null);
            }
        }, title, msg, CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL);

        Minecraft.getInstance().setScreen(screen);
    }

    public static boolean renderFireOverlay(Player player, PoseStack mat)
    {
        return renderBlockOverlay(player, mat, RenderBlockScreenEffectEvent.OverlayType.FIRE, Blocks.FIRE.defaultBlockState(), player.blockPosition());
    }

    public static boolean renderWaterOverlay(Player player, PoseStack mat)
    {
        return renderBlockOverlay(player, mat, RenderBlockScreenEffectEvent.OverlayType.WATER, Blocks.WATER.defaultBlockState(), player.blockPosition());
    }

    public static boolean renderBlockOverlay(Player player, PoseStack mat, RenderBlockScreenEffectEvent.OverlayType type, BlockState block, BlockPos pos)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderBlockScreenEffectEvent(player, mat, type, block, pos));
    }

    public static int getMaxMipmapLevel(int width, int height)
    {
        return Math.min(
                Mth.log2(Math.max(1, width)),
                Mth.log2(Math.max(1, height))
        );
    }

    public static ResourceLocation getShaderImportLocation(String basePath, boolean isRelative, String importPath)
    {
        final var loc = new ResourceLocation(importPath);
        final var normalised = FileUtil.normalizeResourcePath(
            (isRelative ? basePath : "shaders/include/") + loc.getPath());
        return new ResourceLocation(loc.getNamespace(), normalised);
    }
}
