package net.minecraftforge.event.entity.player;

import static net.minecraftforge.event.Event.Result.ALLOW;
import static net.minecraftforge.event.Event.Result.DEFAULT;
import static net.minecraftforge.event.Event.Result.DENY;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerOpenContainerEvent extends PlayerEvent
{
    
    private Result interact = DEFAULT;

    public PlayerOpenContainerEvent(EntityPlayer player)
    {
        super(player);
    }
    
    public boolean canInteractWith()
    {
        return interact == ALLOW;
    }
    
    public void allowInteraction()
    {
        interact = ALLOW;
    }
    
    @Override
    public void setCanceled(boolean cancel)
    {
        super.setCanceled(cancel);
        interact = (cancel ? DENY : interact == DENY ? DEFAULT : interact);
    }
}
