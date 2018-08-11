package net.minecraftforge.debug.entity;


import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
@Mod(modid = "entityentryfactorytest", name = "Entity Entry Factory Test", version = "1.0.0", acceptableRemoteVersions = "*")
public class EntityEntryFactoryTest {

	public static final String ID = "entityentryfactorytest";

	private static Logger logger;

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}

	private static enum EntityTypes {
		
		TEST_ONE(0), TEST_TWO(1), TEST_THREE(2);
		
		private final int id;
		
		EntityTypes(int idIn){
			this.id = idIn;
		}
		
		public final int getId() {
			return id;
		}
		
	}

	@SubscribeEvent
	public static final void onRegisterEntitiesEvent(
			final RegistryEvent.Register<EntityEntry> event) {

		int entityId = 0;

		for (final EntityTypes type : EntityTypes.values()) {

			final Class<? extends Entity> clazz = EntityFactoryTestEntity.class;
			final ResourceLocation registryName = new ResourceLocation(EntityEntryFactoryTest.ID, type.name().toLowerCase() + "_slug");
			EntityEntryBuilder<Entity> builder = EntityEntryBuilder.create();
			builder = builder.entity(clazz);
			builder = builder.factory(worldIn -> new EntityFactoryTestEntity(worldIn, type));
			builder = builder.id(registryName, entityId++);
			builder = builder.name(registryName.getResourcePath());
			builder = builder.tracker(64, 5, true);

			event.getRegistry().register(builder.build());

		}

	}

	@SubscribeEvent
	public static final void onRegisterModelsEvent(final ModelRegistryEvent event) {

		RenderingRegistry.registerEntityRenderingHandler(EntityFactoryTestEntity.class, renderManager -> new RenderEntityFactoryTestEntity(renderManager));

	}


	private static class EntityFactoryTestEntity extends Entity {

		private EntityTypes type;

		public EntityFactoryTestEntity(World worldIn) {
			this(worldIn, EntityTypes.TEST_ONE);
			throw new IllegalArgumentException();
		}

		public EntityFactoryTestEntity(World worldIn, EntityTypes typeIn) {
			super(worldIn);
			this.setSize(0.5f, 0.5f);
			this.type = typeIn;
		}

		@Override
		protected void entityInit() {
			// TODO Auto-generated method stub

		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			// TODO Auto-generated method stub

		}

		public EntityTypes getType() {
			return type;
		}

	}
	
	private static class RenderEntityFactoryTestEntity extends Render<EntityFactoryTestEntity> {

		protected RenderEntityFactoryTestEntity(RenderManager renderManager) {
			super(renderManager);
		}

		@Override
		public void doRender(EntityFactoryTestEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {

			GlStateManager.pushMatrix();
			try {
				GlStateManager.translate(x, y, z);

				ItemStack stack = new ItemStack(Blocks.REDSTONE_BLOCK);

				switch (entity.getType()) {
				case TEST_ONE:
					stack = new ItemStack(Blocks.IRON_ORE);
					break;
				case TEST_TWO:
					stack = new ItemStack(Items.IRON_PICKAXE);
					break;
				case TEST_THREE:
					stack = new ItemStack(Blocks.IRON_BLOCK);
					break;
				default:
					break;

				}

				IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, entity.getEntityWorld(), null);
				model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.NONE, false);

				bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);

				this.renderLivingLabel(entity, "NAME: " + entity.getDisplayName().getFormattedText(), x, y, z, 64);

				this.renderLivingLabel(entity, "TYPE: " + entity.getType().name(), x, y - 0.5, z, 64);

				this.renderLivingLabel(entity, "KEY: " + EntityList.getKey(entity), x, y - 1, z, 64);

//				getEntityString()

			} catch (Exception e) {
				// TODO: handle exception
			}
			GlStateManager.popMatrix();

			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}

		@Override
		protected ResourceLocation getEntityTexture(EntityFactoryTestEntity entity) {
			return TextureMap.LOCATION_BLOCKS_TEXTURE;
		}

	}

}
