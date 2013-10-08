package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.Event;

public class EventBlockDig extends Event {

	public final int x, y, z, face, status;
	public final EntityPlayerMP player;
	
	public EventBlockDig(int x, int y, int z, int face, int status, EntityPlayerMP player) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.face = face;
		this.status = status;
		this.player = player;
	}

	@Override
	public String toString(){
		return "{ x:" + x + ", y:" + y + ", z:" + z + ", face:" + face + ", status:" + status + ", player:" + player.toString() + "}";		
	}
}
