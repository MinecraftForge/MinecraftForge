package net.minecraftforge.event.world;

import net.minecraft.world.raid.Raid;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
/*
 * This event is called right before a raid starts.
 */
@Cancelable
public class RaidEvent extends Event
{
   public final Raid raid;
   
   public RaidEvent(Raid raid)
   {
	  this.raid = raid;
   }
   
   public Raid getRaid()
   {
	   return raid;
   }
}
