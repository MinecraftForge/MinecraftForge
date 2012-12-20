package net.minecraftforge.event.container;

import static net.minecraftforge.event.Event.Result.DEFAULT;
import static net.minecraftforge.event.Event.Result.DENY;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event.Result;

@Cancelable
public class ContainerInteractEvent extends ContainerEvent
{
    protected World world;
    protected EntityPlayer entityplayer;
    
    public Result interact = DEFAULT;

    public ContainerInteractEvent(Container container, EntityPlayer entityplayer)
    {
        super(container);
        this.entityplayer = entityplayer;
    }
    
    @Override
    public void setCanceled(boolean cancel)
    {
        super.setCanceled(cancel);
        interact = (cancel ? DENY : interact == DENY ? DEFAULT : interact);
    }
}
