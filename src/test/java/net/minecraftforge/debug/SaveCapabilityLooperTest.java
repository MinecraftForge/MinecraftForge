package net.minecraftforge.debug;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Simple mod to test save capability, by adding loops on world time.
 * This time-looping concept could be much more complicated;
 *  It's just here as a oversimplified placeholder for the test.
 */
@Mod(modid = SaveCapabilityLooperTest.MODID, name = "WorldTime Looper Test", version = "0.0.0", acceptableRemoteVersions = "*")
public class SaveCapabilityLooperTest
{
    public static final String MODID = "worldtimeloopertest";

    @CapabilityInject(IWorldTimeLoop.class)
    public static final Capability<IWorldTimeLoop> LOOP_CAP = null;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        CapabilityManager.INSTANCE.register(IWorldTimeLoop.class, new LooperStorage(), DefaultTimeLoop.class);
        MinecraftForge.EVENT_BUS.register(new NormalEventHandler());
    }

    @EventHandler
    public void init(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandSetLoop());
    }

    /*@SideOnly(Side.SERVER)
    @EventHandler
    public void init(FMLServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        if(server instanceof DedicatedServer) {
            int loopTime = ((DedicatedServer) server).getIntProperty("worldlooplength", 0);
        }
    }*/

    public static class NormalEventHandler
    {
        @SubscribeEvent
        public void attatchTimer(AttachCapabilitiesEvent<WorldInfo> event)
        {
            if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
                event.addCapability(new ResourceLocation(MODID, "looper"), new TimeLooperProvider());
        }

        @SubscribeEvent
        public void onTick(TickEvent.ServerTickEvent event)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            WorldInfo globalSettings = server.getEntityWorld().getWorldInfo();

            if (!globalSettings.hasCapability(LOOP_CAP, null))
            {
                return;
            }

            IWorldTimeLoop loop = globalSettings.getCapability(LOOP_CAP, null);

            if (event.phase == TickEvent.Phase.END)
            {
                if(loop.getTimeLoop() <= 0)
                    return;
                if(globalSettings.getWorldTime() >= loop.getTimeLoop())
                    globalSettings.setWorldTime(globalSettings.getWorldTime() % loop.getTimeLoop());
            }
        }
    }

    public static class CommandSetLoop extends CommandBase {
        @Override
        public String getName()
        {
            return "timeloop";
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "timeloop <loopLength|0(disabled)>";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            WorldInfo info = server.getEntityWorld().getWorldInfo();
            if(args.length < 1)
                throw new WrongUsageException("time loop length is needed", new Object[0]);
            long time = this.parseLong(args[0]);
            time = Math.max(0, time);
            if(info.hasCapability(LOOP_CAP, null))
                info.getCapability(LOOP_CAP, null).setTimeLoop(time);
            notifyCommandListener(sender, this, "Time loop length set to %s", new Object[] {time});
        }
    }

    public interface IWorldTimeLoop
    {
        public void setTimeLoop(long loopLength);
        public long getTimeLoop();
    }

    public static class LooperStorage implements IStorage<IWorldTimeLoop>
    {
        @Override
        public NBTBase writeNBT(Capability<IWorldTimeLoop> capability, IWorldTimeLoop instance, EnumFacing side)
        {
            return new NBTTagLong(instance.getTimeLoop());
        }

        @Override
        public void readNBT(Capability<IWorldTimeLoop> capability, IWorldTimeLoop instance, EnumFacing side, NBTBase nbt)
        {
            NBTTagLong data = (NBTTagLong) nbt;
            instance.setTimeLoop(data.getLong());
        }
    }

    public static class DefaultTimeLoop implements IWorldTimeLoop
    {
        private long loopLength = 0L;

        @Override
        public void setTimeLoop(long loopLength)
        {
           this.loopLength = loopLength;
        }

        @Override
        public long getTimeLoop()
        {
            return this.loopLength;
        }
    }

    public static class TimeLooperProvider implements ICapabilitySerializable<NBTTagLong>
    {
        private IWorldTimeLoop looper = LOOP_CAP.getDefaultInstance();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == LOOP_CAP;
        }

        @Override
        @Nullable
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return capability == LOOP_CAP ? LOOP_CAP.<T>cast(this.looper) : null;
        }

        @Override
        public NBTTagLong serializeNBT()
        {
            return (NBTTagLong) LOOP_CAP.writeNBT(this.looper, null);
        }

        @Override
        public void deserializeNBT(NBTTagLong nbt)
        {
            LOOP_CAP.readNBT(this.looper, null, nbt);
        }
    }
}
