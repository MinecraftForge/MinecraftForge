package net.minecraftforge.event.world;

import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.eventbus.api.*;

/**
 *  RaidEvent is the base class of all Raid events.
 */
@Cancelable
public class RaidEvent extends Event
{
   private final Raid raid;
   
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
    * If this event is cancelled, that tick gets skipped.
    */
   @Cancelable
   public static class Tick extends RaidEvent
   {
	  public Tick(Raid raid) 
	  {
		super(raid);
	  }
   }
   
   /**
    * This event is fired when a {@link Raid} is stopped.
    */
   public static class Stop extends RaidEvent
   {
	  public Stop(Raid raid)
	  {
		super(raid);
	  }
   }
   
   /**
    * This event is fired when a {@link Raid} spawns a raider.
    */
   public static class RaidersSpawn extends RaidEvent
   {  
	  private final AbstractRaiderEntity raider;
	  
	  public RaidersSpawn(Raid raid, AbstractRaiderEntity raider)
	  {
		super(raid);
		this.raider = raider;
	  }
	  
	  public AbstractRaiderEntity getRaider()
	  {
		  return raider;
	  }
   }
   
   /**
    * This event is fired when a {@link Raid} is started.
    * This event is {@link Cancelable}.
    */
   @Cancelable
   public static class Start extends RaidEvent
   {
	  public Start(Raid raid) 
	  {
		super(raid);
	  }
   }
}
