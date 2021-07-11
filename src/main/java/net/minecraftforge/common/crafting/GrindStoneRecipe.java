package net.minecraftforge.common.crafting;

import com.google.gson.JsonObject;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GrindStoneRecipe implements IRecipe<IInventory>
{

	private ResourceLocation id;
	private Ingredient ingredient;
	private Ingredient ingredient1;
	private ItemStack result;
	

	public GrindStoneRecipe(ResourceLocation id,Ingredient ingredient, Ingredient ingredient1, ItemStack result) 
	{
		this.id = id;
		this.ingredient = ingredient;
		this.ingredient1 = ingredient1;
		this.result = result;
	}
	
	@Override
	public boolean matches(IInventory p_77569_1_, World p_77569_2_) 
	{
		return this.ingredient.test(p_77569_1_.getItem(0)) && this.ingredient1.test(p_77569_1_.getItem(1));
	}

	@Override
	public ItemStack assemble(IInventory p_77572_1_) 
	{
		return this.result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) 
	{
		return true;
	}

	@Override
	public ItemStack getResultItem() 
	{
		return this.result;
	}

	@Override
	public ResourceLocation getId() 
	{
		return this.id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() 
	{
		return IRecipeSerializer.GRINDSTONE;
	}

	@Override
	public IRecipeType<?> getType() 
	{
		return IRecipeType.GRINDING;
	}
	
	public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GrindStoneRecipe> 
	{

		@Override
		public GrindStoneRecipe fromJson(ResourceLocation p_199425_1_, JsonObject p_199425_2_) 
		{
			Ingredient ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "base"));
	        Ingredient ingredient1 = Ingredient.fromJson(JSONUtils.getAsJsonObject(p_199425_2_, "addition"));
	        ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(p_199425_2_, "result"));
			return new GrindStoneRecipe(p_199425_1_, ingredient, ingredient1, result);
		}

		@Override
		public GrindStoneRecipe fromNetwork(ResourceLocation p_199426_1_, PacketBuffer p_199426_2_) 
		{
			Ingredient ingredient = Ingredient.fromNetwork(p_199426_2_);
	        Ingredient ingredient1 = Ingredient.fromNetwork(p_199426_2_);
	        ItemStack result = p_199426_2_.readItem();
			return new GrindStoneRecipe(p_199426_1_, ingredient, ingredient1, result);
		}

		@Override
		public void toNetwork(PacketBuffer p_199427_1_, GrindStoneRecipe p_199427_2_) 
		{
			p_199427_2_.ingredient.toNetwork(p_199427_1_);
	        p_199427_2_.ingredient1.toNetwork(p_199427_1_);
	        p_199427_1_.writeItem(p_199427_2_.result);
		}
		
	}

	
	

}
