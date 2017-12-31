package net.minecraftforge.debug;


import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraftforge.event.ItemTransferEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "itemtransfereventtest", name = "ItemTransferEventTest", version = "1.0")
@Mod.EventBusSubscriber
public class ItemTransferEventTest
{

    public static Boolean DEBUG = true;

    @SubscribeEvent
    public static void onItemTransferPost(ItemTransferEvent.POST event)
    {
        if (!DEBUG) return;
        if (event.getFromTileEntity() != null && event.getFromTileEntity() instanceof TileEntityHopper)
        {
            if (!(event.getTargetTileEntity() instanceof TileEntityHopper))
            {
                TileEntityHopper hopper = ((TileEntityHopper)event.getFromTileEntity());
                //set the TransferCooldown to 20
                hopper.setTransferCooldown(20);
            }
        }
    }

    @SubscribeEvent
    public static void onItemTransferPre(ItemTransferEvent.PRE event)
    {
        if (event.getTargetTileEntity() instanceof TileEntityLockable)
        {
            //we dont want to steal out of a locked chest
            if (((TileEntityLockable) event.getTargetTileEntity()).isLocked() && event.getFlow() == ItemTransferEvent.flow.EXTRACT)
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (!DEBUG) return;
        World world = event.getEntityPlayer().world;
        BlockPos pos = event.getPos();
        TileEntity tileEntity = world.getTileEntity(pos);
        if (world.isRemote){
            return;
        }

        if (event.getEntityPlayer().getHeldItem(event.getHand()).getItem() == Item.getItemFromBlock(Blocks.GLASS)){
            if (tileEntity != null && tileEntity instanceof TileEntityLockable){
                event.setCanceled(true);
                ((TileEntityLockable)tileEntity).setLockCode(new LockCode("hello"));
                event.getEntityPlayer().sendMessage(new TextComponentString("the code is: hello"));
            }
        }
    }
}
