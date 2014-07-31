package net.minecraftforge.client.event;

import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent.Specials;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

public class RenderEntityEvent extends Event
{
   public final Entity entity;
   public final RenderEntity renderer;
   public final double x;
   public final double y;
   public final double z;
   
   public RenderEntityEvent(Entity entity, RenderEntity renderer, double x, double y, double z)
   {
       this.entity = entity;
       this.renderer = renderer;
       this.x = x;
       this.y = x;
       this.z = z;
   }
   
   @Cancelable
   public static class Pre extends RenderEntityEvent
   {
       public Pre(Entity entity, RenderEntity renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
   }
   
   public static class Post extends RenderEntityEvent
   {
       public Post(Entity entity, RenderEntity renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
   }
}
