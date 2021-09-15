package net.minecraftforge.items;

import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ItemDecorator;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class ItemDecoratorHandler implements IItemDecoratorHandler
{
    private Map<String, ItemDecorator> customItemDecoratorCache = new HashMap<>();
    private static final String DECORATORS = "CapabilityItemDecoratorHandler";
   
    @Override
    public CompoundTag serializeNBT()
    {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
    }
  
    /**
     * Adds the required NBT for an ItemDecorator to the Items NBT.
     * Should be called server side, since the data should persist.
     * It is potentially required to synchronize the Item with the client afterwards.
     * @param decorator The ItemDecorator
     * @param stack The ItemStack with the Capability
     */
    @Override
    public void addDecorator(ItemDecorator decorator, ItemStack stack)
    {
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains(DECORATORS))
        {
            ListTag nbtTagList = stack.getTag().getList(DECORATORS, Constants.NBT.TAG_COMPOUND);
            int size = nbtTagList.size();
            for(int i = 0; i < size; i++)
            {
                if(nbtTagList.getCompound(i).getString("key").equals(decorator.getRegistryName().toString()))
                {
                   return;
                }
            }
            CompoundTag newTag = new CompoundTag();
            newTag.putString("key", decorator.getRegistryName().toString());
            tag.getList(DECORATORS, Constants.NBT.TAG_COMPOUND).add(newTag);
        }
        else
        {
            ListTag nbtTagList = new ListTag();
            CompoundTag newTag = new CompoundTag();
            newTag.putString("key", decorator.getRegistryName().toString());
            tag.put(DECORATORS, nbtTagList);
            tag.getList(DECORATORS, Constants.NBT.TAG_COMPOUND).add(newTag);
        }
    }
    
    /**
     * Removes the required NBT for a ItemDecorator from the Items NBT.
     * Should be called server side, since the data should persist.
     * It is potentially required to synchronize the Item with the client afterwards.
     * @param decorator The ItemDecorator
     * @param stack The ItemStack with the Capability
     */
    @Override
    public void removeDecorator(ItemDecorator decorator, ItemStack stack)
    {
        if(stack.hasTag() && stack.getTag().contains(DECORATORS))
        {
            ListTag nbtTagList = stack.getTag().getList(DECORATORS, Constants.NBT.TAG_COMPOUND);
            int size = nbtTagList.size();
            for(int i = 0; i < size; i++)
            {
                if(nbtTagList.getCompound(i).getString("key").equals(decorator.getRegistryName().toString()))
                {
                    nbtTagList.remove(i);
                    break;
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(Font font, ItemStack stack, int xOffset, int yOffset, @Nullable String itemCountLabel)
    {
        if(stack.hasTag() && stack.getTag().contains(DECORATORS))
        {
            ListTag nbtTagList = stack.getTag().getList(DECORATORS, Constants.NBT.TAG_COMPOUND);
            int size = nbtTagList.size();
            for(int i = 0; i < size; i++)
            {
                String key = nbtTagList.getCompound(i).getString("key");
                
                if (!customItemDecoratorCache.containsKey(key)) {
                    ResourceLocation location = new ResourceLocation(key);
                    customItemDecoratorCache.put(key, ForgeRegistries.ITEM_DECORATORS.getValue(location));
                }
                if(customItemDecoratorCache.get(key) != null) {
                    customItemDecoratorCache.get(key).render(font, stack, xOffset, yOffset, itemCountLabel);
                }
            }
        }
    }
}
