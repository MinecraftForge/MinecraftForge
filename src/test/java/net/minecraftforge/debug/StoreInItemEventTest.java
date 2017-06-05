package net.minecraftforge.debug;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.village.Village;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.StoreInItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Mod(modid = "storeinitemtestmod", name = "StroreInItemEvent Test Mod", version = "0.0.0", acceptableRemoteVersions = "*")
public class StoreInItemEventTest
{
    public static final boolean ENABLE = true;
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt)
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }
    @SubscribeEvent
    public void onStoreInItem(StoreInItemEvent event)
    {
        if (event.getInput().getItem()==Items.STICK)
        {
            boolean deny = true;
            for (int i = 0;i<event.getContents().size();i++) {
                if (event.getContents().get(i).getItem()==Items.BLAZE_ROD)
                {
                    deny = false;
                }
            }
            if (deny)
            {
                event.setResult(Event.Result.DENY);
            }
        }
    }
}
