package net.minecraftforge.items;

import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.CustomItemDecorator;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

import javax.annotation.Nullable;
import java.util.*;

public class CustomItemDecorationHandler implements ICustomItemDecoration
{
    private Map<String, CustomItemDecorator> customItemDecoratorCache = new HashMap<>();
    private static final String CUSTOM_DECORATIONS = "CapabilityCustomItemDecorators";

    @Override
    public CompoundTag serializeNBT()
    {
        ListTag nbtTagList = new ListTag();
        for(int i = 0; i < customItemDecoratorCache.size(); i++)
        {
            CompoundTag tag = new CompoundTag();
            tag.putString("key", customItemDecoratorCache.get(i).key);
            nbtTagList.add(i, tag);
        }
        CompoundTag decorators = new CompoundTag();
        decorators.put(CUSTOM_DECORATIONS, nbtTagList);
        return decorators;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        ListTag nbtTagList = nbt.getList(CUSTOM_DECORATIONS, Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < nbtTagList.size(); i++)
        {
            CompoundTag tag = nbtTagList.getCompound(i);
        }
    }

    /**
     * Adds the required NBT for a Decoration to the Items NBT.
     * Should be called server side, since the data should persist.
     * It is potentially required to synchronize the Item with the client afterwards.
     * @param key The Decoration
     * @param stack
     */
    @Override
    public void addDecoration(ResourceLocation key, ItemStack stack)
    {
        /*
        Writing to the ItemStack NBT directly is an ugly solution, but since this is also supposed to
        work with vanilla items, where getShareTag isn't implemented, it's either this,
        or asking modders to send a custom packet every time they add a new decoration.
        */
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains(CUSTOM_DECORATIONS))
        {
            CompoundTag newTag = new CompoundTag();
            newTag.putString("key", key.toString());
            tag.getList(CUSTOM_DECORATIONS, Constants.NBT.TAG_COMPOUND).add(newTag);
        }
        else
        {
            ListTag nbtTagList = new ListTag();
            CompoundTag newTag = new CompoundTag();
            newTag.putString("key", key.toString());
            tag.put(CUSTOM_DECORATIONS, nbtTagList);
            tag.getList(CUSTOM_DECORATIONS, Constants.NBT.TAG_COMPOUND).add(newTag);
        }
    }

    @Override
    public void removeDecoration(ResourceLocation key, ItemStack stack)
    {
        /*
        Writing to the ItemStack NBT directly is an ugly solution, but since this is also supposed to
        work with vanilla items, where getShareTag isn't implemented, it's either this,
        or asking modders to send a custom packet every time they add a new decoration.
        */
        if(stack.hasTag() && stack.getTag().contains(CUSTOM_DECORATIONS))
        {
            ListTag nbtTagList = stack.getTag().getList(CUSTOM_DECORATIONS, Constants.NBT.TAG_COMPOUND);
            int size = nbtTagList.size();
            for(int i = 0; i < size; i++)
            {
                if(nbtTagList.getCompound(i).getString("key").equals(key.toString()))
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
        if(stack.hasTag() && stack.getTag().contains(CUSTOM_DECORATIONS))
        {
            ListTag nbtTagList = stack.getTag().getList(CUSTOM_DECORATIONS, Constants.NBT.TAG_COMPOUND);
            int size = nbtTagList.size();
            for(int i = 0; i < size; i++)
            {
                String key = nbtTagList.getCompound(i).getString("key");
                if (!customItemDecoratorCache.containsKey(key)) {
                    customItemDecoratorCache.put(key, ClientRegistry.getCustomDecorationsRenderer(key));
                }
                customItemDecoratorCache.get(key).render(font, stack, xOffset, yOffset, itemCountLabel);
            }
        }
    }
}
