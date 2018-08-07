/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = "wrnormal", name = "WRNormal", version = "1.0", acceptableRemoteVersions = "*")
public class VertexBufferNormalTest
{
    @Instance("wrnormal")
    public static VertexBufferNormalTest instance;

    @SidedProxy
    public static ServerProxy proxy;

    @EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        EntityRegistry.registerModEntity(new ResourceLocation("wrnormal", "scale_test"), EntityScaleTest.class, "scale_test", 0, instance, 60, 3, true);
        proxy.registerRenders();
    }

    public static class ServerProxy
    {
        public void registerRenders()
        {
        }
    }

    public static class ClientProxy extends ServerProxy
    {
        @Override
        public void registerRenders()
        {
            RenderingRegistry.registerEntityRenderingHandler(EntityScaleTest.class, new RenderScaleTestFactory());
        }
    }

    public static class RenderScaleTestFactory implements IRenderFactory<EntityScaleTest>
    {
        @Override
        public RenderScaleTest createRenderFor(RenderManager manager)
        {
            return new RenderScaleTest(manager);
        }
    }

    public static class EntityScaleTest extends EntityArmorStand
    {
        public EntityScaleTest(World world)
        {
            super(world);
        }
    }

    public static class RenderScaleTest extends RenderLivingBase<EntityScaleTest>
    {
        private static final ResourceLocation TEXTURE = new ResourceLocation("textures/blocks/stone.png");

        public RenderScaleTest(RenderManager manager)
        {
            super(manager, new ModelScaleTest(), 0);
        }

        @Override
        public void renderName(EntityScaleTest entity, double x, double y, double z)
        {
        }

        @Override
        protected ResourceLocation getEntityTexture(EntityScaleTest entity)
        {
            return TEXTURE;
        }
    }

    public static class ModelScaleTest extends ModelBase
    {
        private ModelRenderer component;

        public ModelScaleTest()
        {
            textureWidth = textureHeight = 16;
            component = new ModelRenderer(this);
            int size = 10;
            for (int x = 0; x < size; x++)
            {
                for (int z = 0; z < size; z++)
                {
                    component.addBox((x - size / 2) * 3, 0, (z - size / 2) * 3, 2, 2, 2, -(x + z * size) / (float) (size * size) * 0.5F);
                }
            }
        }

        @Override
        public void render(Entity entity, float p1, float p2, float p3, float p4, float p5, float scale)
        {
            component.render(scale);
        }
    }
}
