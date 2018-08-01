package net.minecraftforge.test.client;

import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IItemRenderer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid="itemrenderertest", name="Item Renderer Test", version="1.0")
/**
 * A mod to test the IItemRenderer class and custom item rendering system.
 * @author Romejanic
 */
public class ItemRendererTest {

	public Item testItem;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.testItem = new Item().setCreativeTab(CreativeTabs.MISC).setRegistryName("test").setUnlocalizedName("test");
		MinecraftForge.EVENT_BUS.register(this);
		
		if(event.getSide() == Side.CLIENT) {
			MinecraftForgeClient.registerItemRenderer(this.testItem, new IItemRenderer() {

				private ModelPig pig;
				private ResourceLocation tex;
				
				@Override
				public boolean shouldRender(ItemStack stack, ItemRenderType renderType) {
					switch(renderType) {
					case EQUIPPED_FIRST_PERSON:
					case EQUIPPED:
					case ENTITY:
					case INVENTORY:
						return true;
					default:
						return false;
					}
				}

				@Override
				public void render(ItemStack stack, Entity entity, ItemRenderType renderType) {
					if(this.pig == null) {
						this.pig = new ModelPig();
					}
					if(this.tex == null) {
						this.tex = new ResourceLocation("minecraft:textures/entity/pig/pig.png");
					}
					bindTexture(this.tex);
					
					GlStateManager.pushMatrix();
					GlStateManager.scale(0.7f, 0.7f, 0.7f);
					GlStateManager.rotate(180f, 0f, 0f, 1f);
					GlStateManager.translate(0f, -1.3f, 0f);
					this.pig.render(entity, 0f, 0f, 0f, 0f, 0f, 0.0625f);
					GlStateManager.popMatrix();
				}
				
			});
		}
	}
	
	@SubscribeEvent
	public void register(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(this.testItem);
	}

	@SubscribeEvent
	public void modelBake(ModelBakeEvent event) {
		ModelLoader.setCustomModelResourceLocation(this.testItem, 0, new ModelResourceLocation("itemrenderertest:test", "inventory"));
	}
	
}
