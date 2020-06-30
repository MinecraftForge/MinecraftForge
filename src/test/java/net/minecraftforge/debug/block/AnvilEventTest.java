package net.minecraftforge.debug.block;

import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Hand;
import net.minecraftforge.event.anvil.AnvilBreakEvent;
import net.minecraftforge.event.anvil.AnvilDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod("anvil_event_test")
@EventBusSubscriber
public class AnvilEventTest
{
    @SubscribeEvent
    public static void onAnvilDamage(AnvilDamageEvent event)
    {
        if (event.isCanceled()) return;
        if (event.getCurrentState().getBlock().equals(Blocks.ANVIL))
        {
            System.out.println("Cancelled Anvil Damage");
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAnvilDamageFalling(AnvilDamageEvent.Falling event)
    {
        if (event.getGroundState() != null && event.getGroundState().isIn(BlockTags.WOOL))
        {
            System.out.println("Cancelled Anvil Damage from Falling");
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onAnvilDamageContainer(AnvilDamageEvent.Container event)
    {
        if (event.getPlayer().getHeldItem(Hand.MAIN_HAND).isItemEqual(new ItemStack(Items.STICK)))
            System.out.println("Cancelled Anvil Damage from Container");
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onAnvilBreak(AnvilBreakEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity)
        {
            event.getWorld().setBlockState(event.getPos(), Blocks.DIAMOND_BLOCK.getDefaultState());
        }

        if (event.getEntity() instanceof FallingBlockEntity)
        {
            FallingBlockEntity entity = (FallingBlockEntity) event.getEntity();
            entity.shouldDropItem = false;
            event.getWorld().setBlockState(event.getPos(), Blocks.GOLD_BLOCK.getDefaultState());
        }
    }
}