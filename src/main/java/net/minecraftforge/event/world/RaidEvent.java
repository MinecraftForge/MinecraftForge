package net.minecraftforge.event.world;

import net.minecraft.world.raid.Raid;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 *  RaidEvent along with its subevents gets fired whenever a {@link Raid} occurs.
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
   
   /**
    * This event is fired for every {@link Raid} tick.
    * This event is {@link Cancelable}.
    */
   @Cancelable
   public static class RaidTickEvent extends RaidEvent
   {
	  public RaidTickEvent(Raid raid) 
	  {
		super(raid);
	  }
   }
   
   /**
    * This event is fired when a {@link Raid} is stopped.
    */
   public static class RaidStopEvent extends RaidEvent
   {
	  public RaidStopEvent(Raid raid)
	  {
		super(raid);
	  }
   }
   
   /**
    * This event is fired when a {@link Raid} spawns a raider.
    */
   public static class RaidSpawnRaidersEvent extends RaidEvent
   {
	  public RaidSpawnRaidersEvent(Raid raid) 
	  {
		super(raid);
	  }
   }
   
   /**
    * This event is fired when a {@link Raid} is started.
    */
   public static class RaidStart extends RaidEvent
   {
	  public RaidStart(Raid raid) 
	  {
		super(raid);
	  }
   }
}
