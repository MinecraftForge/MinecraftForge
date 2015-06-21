package net.minecraftforge.event.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired when a crafting-type inventory searches for the first
 * valid recipe that matches the inputs. It is not recommended that you use this
 * event just to check if a specific item is being made, as removing all recipes
 * that output that specific item from the relevant CraftingManager or
 * FuranceRecipes instance is quicker.
 * 
 * <br>
 * This event is {@link Cancelable}. <br>
 * If the event is canceled, the current input will be considered invalid, and
 * no crafting can occur. This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 * 
 * @author AlexIIL
 */
@Cancelable
public class FindMatchingRecipeEvent extends Event {
    /**
     * This shows the type of crafting that is occurring. It is recommended that
     * if you have a category that doesn't fit into these that you use OTHER.
     */
    /*
     * OR maybe that this is just a String with constants expressed here? that
     * would allow for many more possibilities, but I don't know if thats
     * actually helpful, unless an ore-dictionary type naming convention was
     * here, and even then, what would you do with that?
     */
    public static enum EType {
        CRAFT, SMELT, OTHER;
    }

    public final World world;
    public final ItemStack[] input;
    public final ItemStack output;
    public final EType type;
    private ItemStack newOutput;

    /**
     * The input can be null, and it will be initialized to an empty array. Type
     * will be initialized to EType.OTHER if it is null.
     */
    private FindMatchingRecipeEvent(World world, ItemStack[] in, ItemStack out, EType type)
    {
        input = in == null ? new ItemStack[0] : in;
        output = out;
        newOutput = out;
        this.world = world;
        this.type = type == null ? EType.OTHER : type;
        // Is this really wanted? or just throw an NPE early?
    }

    /**
     * This sets the output that the recipe will give. NOTE that if this is for
     * an already existing recipe, the items will be used up
     */
    public void setOutput(ItemStack stack)
    {
        newOutput = stack;
    }

    /**
     * This gets the new output that the recipe will give. If none has been set,
     * then it will return whatever was output by the recipe
     */
    public ItemStack getOutput()
    {
        return newOutput;
    }

    /**
     * This is fired whenever a block tries to craft with items. More
     * specifically, this is when a block, or multiple blocks but this as the
     * center, try to craft something
     */
    public static class Block extends FindMatchingRecipeEvent {
        public final BlockPos pos;

        public Block(World world, BlockPos pos, ItemStack[] in, ItemStack out, EType type)
        {
            super(world, in, out, type);
            this.pos = pos;
        }
    }

    /**
     * This is fired whenever an entity tries to craft items. While this is
     * never called from vanilla classes, this is intended to allow modded
     * non-player entities to fire this event, and give the entity.
     */
    public static class Entity extends FindMatchingRecipeEvent {
        public final net.minecraft.entity.Entity entity;

        public Entity(World world, net.minecraft.entity.Entity entity, ItemStack[] in, ItemStack out, EType type)
        {
            super(world, in, out, type);
            this.entity = entity;
        }
    }

    /**
     * This is fired whenever a player tires to craft items. More specifically,
     * this is whenever a player, and only one player tries to craft something
     */
    public static class Player extends Entity {
        public final EntityPlayer player;

        public Player(World world, EntityPlayer player, ItemStack[] in, ItemStack out, EType type)
        {
            super(world, player, in, out, type);
            this.player = player;
        }
    }
}
