package net.minecraftforge.test;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

@Mod(modid = "criticalhiteventtest", name = "CriticalHitEventTest", version = "0.0.0")
public class CriticalHitEventTest
{
    public static final boolean ENABLE = false;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLE)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCriticalHit(CriticalHitEvent event)
    {
        ItemStack itemstack = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
        
        if(event.getDamageModifier()>1F)
        {
            System.out.println("By default this hit will be critical.");
        }
        else
        {
            System.out.println("By default this hit won't be critical.");
        }
    
        if (itemstack.getItem() instanceof ItemSword)
        {
            event.setResult(Result.ALLOW); //Every hit is Critical
            System.out.println("This hit will be critical.");
        }
        else if (!itemstack.isEmpty())
        {
            event.setResult(Result.DENY);//No hit will be Critical
            System.out.println("This hit wont be critical.");
        }
        else
        {
            event.setResult(Result.DEFAULT); //Vanilla Hits
        }       
            
        System.out.println(event.getTarget() + " got hit by " + event.getEntityPlayer() + " with a damagemodifier of " + event.getDamageModifier() );
        event.setDamageModifier(2.0F);
        System.out.println("The damagemodifier is changed to " + event.getDamageModifier());
    }
}
