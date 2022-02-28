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

package net.minecraftforge.debug.client.rendering;

import com.google.common.collect.ImmutableMap;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.StandaloneModelConfiguration;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.renderable.BakedRenderable;
import net.minecraftforge.client.model.renderable.IRenderable;
import net.minecraftforge.client.model.renderable.MultipartTransforms;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Map;

@Mod(RenderableTest.MODID)
public class RenderableTest
{
    public static final String MODID = "renderable_test";

    public static final boolean ENABLED = false;

    public RenderableTest()
    {
        if (ENABLED)
        {
            if (FMLEnvironment.dist == Dist.CLIENT)
            {
                Client.init();
            }
        }
    }

    private static class Client
    {
        private static ResourceLocation MODEL_LOC = new ResourceLocation("minecraft:block/diamond_block");

        private static IRenderable<MultipartTransforms> renderable;
        private static IRenderable<IModelData> bakedRenderable;

        public static void init()
        {
            var bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener(Client::registerModels);
            bus.addListener(Client::registerReloadListeners);
            MinecraftForge.EVENT_BUS.addListener(Client::renderLast);
        }

        private static void registerModels(ModelRegistryEvent t)
        {
            ForgeModelBakery.addSpecialModel(MODEL_LOC);
        }

        public static void registerReloadListeners(RegisterClientReloadListenersEvent event)
        {
            event.registerReloadListener(new SimplePreparableReloadListener<OBJModel>()
            {
                @Override
                protected OBJModel prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller)
                {
                    var settings = new OBJModel.ModelSettings(
                            new ResourceLocation("new_model_loader_test:models/item/sugar_glider.obj"),
                            false,
                            true,
                            true,
                            false,
                            null
                    );
                    return OBJLoader.INSTANCE.loadModel(settings);
                }

                @Override
                protected void apply(OBJModel model, ResourceManager resourceManager, ProfilerFiller profilerFiller)
                {
                    var config = StandaloneModelConfiguration.create(Map.of(
                            "#qr", new ResourceLocation("minecraft:block/quartz_block_top")
                    ));
                    renderable = model.bakeRenderable(config);
                }
            });
        }

        private static double time;

        public static void renderLast(RenderLevelLastEvent event)
        {
            if (bakedRenderable == null)
            {
                bakedRenderable = BakedRenderable.of(MODEL_LOC);
            }

            var poseStack = event.getPoseStack();
            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

            time += Minecraft.getInstance().getDeltaFrameTime();

            var map = ImmutableMap.<String, Matrix4f>builder();

            var left = new Matrix4f();
            left.setIdentity();
            left.multiply(Quaternion.fromYXZ(0, 0, (float)Math.sin(time * 0.05) * 0.1f));
            map.put("object_1", left);

            var right = new Matrix4f();
            right.setIdentity();
            right.multiply(Quaternion.fromYXZ(0, 0, -(float)Math.sin(time * 0.05) * 0.1f));
            map.put("object_9", right);

            var transforms = MultipartTransforms.of(map.build());

            renderable.render(poseStack, bufferSource, RenderType::entitySolid, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, event.getPartialTick(), transforms);

            poseStack.pushPose();
            poseStack.translate(0,0.5f,0);
            bakedRenderable.render(poseStack, bufferSource, RenderType::entitySolid, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, event.getPartialTick(), EmptyModelData.INSTANCE);
            poseStack.popPose();

            bufferSource.endBatch();
        }
    }
}
