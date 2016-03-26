package net.minecraftforge.test;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = "wrnormal", version = "1.0")
public class WRNormalMod
{
    @Instance("wrnormal")
    public static WRNormalMod instance;

    @SidedProxy
    public static ServerProxy proxy;

    @EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        EntityRegistry.registerModEntity(EntityScaleTest.class, "ScaleTest", 0, instance, 60, 3, true);
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

    public static class RenderScaleTest extends RendererLivingEntity<EntityScaleTest>
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
