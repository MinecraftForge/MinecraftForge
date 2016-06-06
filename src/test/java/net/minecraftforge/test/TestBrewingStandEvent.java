package net.minecraftforge.test;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Test mod showcasing example use of passing BlockPos to PotionBrewEvent
 * <p/>
 * Adds following features:
 * <br/>
 * - If brewing stand is placed directly on top of a diamond block, all brews are free
 * <br/>
 * - Brewing a Potion of Poison applies poison status effect to all entities within radius of 4 blocks
 * <br/>
 */
@Mod(modid = "TestBrwingStandEvent", name = "TestBrewingStandEvent", version = "0.0.0")
public class TestBrewingStandEvent
{

    public static final boolean ENABLED = true;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (!ENABLED) return;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPotionBrewEvent(PotionBrewEvent.Pre event)
    {
        // Check if block below brewing stand is a block of diamond
        if (event.getWorld().getBlockState(event.getPos().down()).getBlock() == Blocks.diamond_block)
        {
            // Cancel the event to override brewing behavior
            event.setCanceled(true);

            // Update outputs
            for (int index = 0; index < 3; index++)
            {
                ItemStack input = event.getItem(index);
                ItemStack output = BrewingRecipeRegistry.getOutput(input, event.getItem(3));

                if (output != null)
                {
                    event.setItem(index, output);
                }
            }

            event.getWorld().playAuxSFX(1035, event.getPos(), 0);

            // Don't touch the ingredient as we want this brew to complete for free.
        }
    }

    @SubscribeEvent
    public void onPotionBrewEvent(PotionBrewEvent.Post event)
    {
        // Return if none of the outputs are potions of poison
        if (!isPoison(event.getItem(0)) && !isPoison(event.getItem(1)) && !isPoison(event.getItem(2)))
        {
            return;
        }

        // Create bounding area (4 blocks on each axis) around the brewing stand
        AxisAlignedBB area = new AxisAlignedBB(event.getPos().add(-4, -4, -4), event.getPos().add(4, 4, 4));

        // Apply 10 seconds of poison to all nearby living entities
        List<EntityLivingBase> entities = event.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, area);

        for (EntityLivingBase entity : entities)
        {
            entity.addPotionEffect(new PotionEffect(MobEffects.poison, 200));
        }
    }

    private boolean isPoison(ItemStack item)
    {
        return item != null && item.getItem() == Items.potionitem
                && (PotionUtils.getPotionFromItem(item) == PotionTypes.poison
                || PotionUtils.getPotionFromItem(item) == PotionTypes.strong_poison
                || PotionUtils.getPotionFromItem(item) == PotionTypes.long_poison);
    }
}
