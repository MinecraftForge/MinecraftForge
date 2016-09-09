package net.minecraftforge.ingredients.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.ingredients.IIngredientSource;
import net.minecraftforge.ingredients.Ingredient;
import net.minecraftforge.ingredients.IngredientSource;
import net.minecraftforge.ingredients.IngredientStack;

import java.util.concurrent.Callable;

public class CapabilityIngredientHandler
{
    @CapabilityInject(IIngredientHandler.class)
    public static Capability<IIngredientHandler> INGREDIENT_HANDLER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IIngredientHandler.class, new Capability.IStorage<IIngredientHandler>()
        {
            @Override
            public NBTBase writeNBT(Capability<IIngredientHandler> capability, IIngredientHandler instance, EnumFacing side)
            {
                if(!(instance instanceof IIngredientSource))
                    throw new RuntimeException("IIngredientHandler does not implement IIngredientSource");
                NBTTagCompound nbt = new NBTTagCompound();
                IIngredientSource source = (IIngredientSource) instance;
                IngredientStack stack = source.getIngredient();
                if(stack != null)
                {
                    stack.writeToNBT(nbt);
                }
                else
                {
                    nbt.setString("Empty", "");
                }
                nbt.setInteger("Capacity", source.getCapacity());
                return nbt;
            }

            @Override
            public void readNBT(Capability<IIngredientHandler> capability, IIngredientHandler instance, EnumFacing side, NBTBase nbt)
            {
                if(!(instance instanceof IIngredientSource))
                    throw new RuntimeException("IIngredientHandler is not an instance of IngredientSource");
                NBTTagCompound tags = (NBTTagCompound) nbt;
                IngredientSource source = (IngredientSource) instance;
                source.setCapacity(tags.getInteger("Capacity"));
                source.readFromNBT(tags);
            }
        }, new Callable<IIngredientHandler>()
        {

            @Override
            public IIngredientHandler call() throws Exception {
                return new IngredientSource(Ingredient.FULL_BLOCK_VOLUME);
            }
        });
    }

}
