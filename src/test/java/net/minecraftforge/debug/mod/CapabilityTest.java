/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.mod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Mod(modid = "forge.testcapmod", name = "Forge TestCapMod", version = "1.0", acceptableRemoteVersions = "*")
public class CapabilityTest
{
    // A Holder/Marker for if this capability is installed.
    // MUST be Static final doesn't matter but is suggested to prevent
    // you from overriding it elsewhere.
    // As Annotations and generic's are erased/unload at runtime this
    // does NOT create a hard dependency on the class.
    @CapabilityInject(IExampleCapability.class)
    private static final Capability<IExampleCapability> TEST_CAP = null;

    static final boolean ENABLED = false;
    private static Logger logger;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt)
    {
        logger = evt.getModLog();
        // If you are a API, provide register your capability ASAP.
        // You MUST supply a default save handler and a default implementation
        // If you are a CONSUMER of the capability DO NOT register it. Only APIs should.
        CapabilityManager.INSTANCE.register(IExampleCapability.class, new Storage(), DefaultImpl::new);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.LeftClickBlock event)
    {
        if (!ENABLED || event.getItemStack().getItem() != Items.STICK)
        {
            return;
        }

        // This is just a example of how to interact with the TE, note the strong type binding that getCapability has
        TileEntity te = event.getWorld().getTileEntity(event.getPos());
        if (te != null && te.hasCapability(TEST_CAP, event.getFace()))
        {
            event.setCanceled(true);
            IExampleCapability inv = te.getCapability(TEST_CAP, event.getFace());
            logger.info("Hi I'm a {}", inv.getOwnerType());
        }
        if (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.DIRT && event.getItemStack().hasCapability(TEST_CAP, null))
        {
            IExampleCapability cap = event.getItemStack().getCapability(TEST_CAP, null);
            event.getEntityPlayer().sendMessage(new TextComponentString((cap.getVal() ? TextFormatting.GREEN : TextFormatting.RED) + "" + TextFormatting.ITALIC + "TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST"));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onInteractItem(PlayerInteractEvent.RightClickItem event)
    {
        if (!ENABLED || !event.getEntityPlayer().isSneaking())
        {
            return;
        }

        if (event.getItemStack().hasCapability(TEST_CAP, null))
        {
            IExampleCapability cap = event.getItemStack().getCapability(TEST_CAP, null);
            cap.toggleVal();
            logger.info("Test value is now: {}", cap.getVal() ? "TRUE" : "FALSE");
        }
    }

    // Example of having this annotation on a method, this will be called when the capability is present.
    // You could do something like register event handlers to attach these capabilities to objects, or
    // setup your factory, who knows. Just figured i'd give you the power.
    @CapabilityInject(IExampleCapability.class)
    private static void capRegistered(Capability<IExampleCapability> cap)
    {
        logger.info("IExampleCapability was registered wheeeeee!");
    }

    // Having the Provider implement the cap is not recommended as this creates a hard dep on the cap interface.
    // And does not allow for sidedness.
    // But as this is a example and we do not care about that here we go.
    class Provider<V> implements ICapabilitySerializable<NBTTagCompound>, IExampleCapability
    {
        private V obj;
        private boolean value;

        Provider(V obj)
        {
            this.obj = obj;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return TEST_CAP != null && capability == TEST_CAP;
        }

        @Override
        @Nullable
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            if (TEST_CAP != null && capability == TEST_CAP)
            {
                return TEST_CAP.cast(this);
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("CapTestVal", this.value);
            return tag;
        }

        @Override
        public void deserializeNBT(NBTTagCompound tag)
        {
            this.value = tag.getBoolean("CapTestVal");
        }

        @Override
        public String getOwnerType()
        {
            return obj.getClass().getName();
        }

        @Override
        public boolean getVal()
        {
            return this.value;
        }

        @Override
        public void toggleVal()
        {
            this.value = !this.value;
        }
    }

    // An example of how to attach a capability to an arbitrary Tile entity.
    // Note: Doing this IS NOT recommended for normal implementations.
    // If you control the TE it is HIGHLY recommend that you implement a fast
    // version of the has/getCapability functions yourself. So you have control
    // over everything yours being called first.
    @SubscribeEvent
    public void onTELoad(AttachCapabilitiesEvent<TileEntity> event)
    {
        //Attach it! The resource location MUST be unique it's recommended that you tag it with your modid and what the cap is.
        event.addCapability(new ResourceLocation("forge.testcapmod:dummy_cap"), new Provider<TileEntity>(event.getObject()));
    }

    @SubscribeEvent
    public void onItemLoad(AttachCapabilitiesEvent<ItemStack> event)
    {
        if (event.getObject().getItem() == Items.STICK)
        {
            event.addCapability(new ResourceLocation("forge.testcapmod:dummy_cap"), new Provider<ItemStack>(event.getObject()));
        }
    }

    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    public void attachEvent(AttachCapabilitiesEvent event) //Test Raw type gets everything still.
    {
        System.currentTimeMillis();
    }

    @SubscribeEvent
    public void attachTileEntity(AttachCapabilitiesEvent<TileEntity> event)
    {
        if (!(event.getObject() instanceof TileEntity))
        {
            throw new IllegalArgumentException("Generic event handler failed! Expected Tile Entity got " + event.getObject());
        }
    }

    @SubscribeEvent
    public void attachVillage(AttachCapabilitiesEvent<Village> event)
    {
        System.out.println(event.getObject());
        event.addCapability(new ResourceLocation("forge.testcapmod:dummy_cap"), new Provider(event.getObject()));
    }

    //Detects if a player has entered the radius of a village
    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event)
    {
        if (ENABLED && !event.world.isRemote)
        {
            List<EntityPlayer> players = event.world.playerEntities;
            int i = 0;
            for (Village village : event.world.villageCollection.getVillageList())
            {
                if (village.hasCapability(TEST_CAP, null))
                {
                    boolean playerInRadius = false;
                    for (EntityPlayer player : players)
                    {
                        if (new Vec3d(player.posX, 0, player.posZ).squareDistanceTo(new Vec3d(village.getCenter().getX(), 0, village.getCenter().getZ())) <= village.getVillageRadius() * village.getVillageRadius())
                        {
                            playerInRadius = playerInRadius || true;
                        }
                    }
                    //If the test cap is true but no players are in radius
                    if (village.getCapability(TEST_CAP, null).getVal() && !playerInRadius)
                    {
                        village.getCapability(TEST_CAP, null).toggleVal();
                        for (EntityPlayer player : players)
                            player.sendMessage(new TextComponentString(TextFormatting.RED + "All Players Have Exited Village " + i));
                    }
                    //If the test cap is false but there are players in radius
                    else if (!village.getCapability(TEST_CAP, null).getVal() && playerInRadius)
                    {
                        village.getCapability(TEST_CAP, null).toggleVal();
                        for (EntityPlayer player : players)
                            player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Some Players Have Entered Village " + i));
                    }
                }
                i++;
            }
        }
    }
    // Capabilities SHOULD be interfaces, NOT concrete classes, this allows for
    // the most flexibility for the implementors.
    public interface IExampleCapability
    {
        String getOwnerType();

        boolean getVal();

        void toggleVal();
    }

    // Storage implementations are required, tho there is some flexibility here.
    // If you are the API provider you can also say that in order to use the default storage
    // the consumer MUST use the default impl, to allow you to access innards.
    // This is just a contract you will have to stipulate in the documentation of your cap.
    public static class Storage implements IStorage<IExampleCapability>
    {
        @Override
        public NBTBase writeNBT(Capability<IExampleCapability> capability, IExampleCapability instance, EnumFacing side)
        {
            return null;
        }

        @Override
        public void readNBT(Capability<IExampleCapability> capability, IExampleCapability instance, EnumFacing side, NBTBase nbt)
        {
        }
    }

    // You MUST also supply a default implementation.
    // This is to make life easier on consumers.
    public static class DefaultImpl implements IExampleCapability
    {
        @Override
        public String getOwnerType()
        {
            return "Default Implementation!";
        }

        public boolean getVal()
        {
            return false;
        }

        @Override
        public void toggleVal()
        {
        }
    }
}
