package net.minecraftforge.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IItemRenderer {

	/**
	 * Determines whether the custom renderer should be used for this render type.
	 * @param stack The ItemStack being rendered.
	 * @param renderType The render type being checked.
	 * @return true if the item renderer should be used, false otherwise.
	 */
	boolean shouldRender(ItemStack stack, ItemRenderType renderType);
	
	/**
	 * Renders the item. All transformation and draw calls should go in here.
	 * @param stack The ItemStack being rendered.
	 * @param entity The entity currently associated with the item.
	 * @param renderType The current render type.
	 */
	void render(ItemStack stack, Entity entity, ItemRenderType renderType);
	
	default void bindTexture(ResourceLocation texture) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
	}
	
	public enum ItemRenderType {
		EQUIPPED, // equipped by an entity or player in third person
		EQUIPPED_FIRST_PERSON, // equipped by a player in first person
		INVENTORY, // displayed in the hotbar or inventory gui
		ENTITY // dropped on the ground as an item
	}
	
}