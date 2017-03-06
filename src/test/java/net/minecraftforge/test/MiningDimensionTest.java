package net.minecraftforge.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import javax.annotation.Nullable;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.storage.AnvilSaveHandler;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.network.ForgeMessage.DimensionRegisterMessage;
import net.minecraftforge.event.world.DimensionPreloadEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = "miningdimensiontest",
        name = "MiningDimensionTest",
        version = "1.0.0",
        acceptableRemoteVersions = "*"/*, serverSideOnly = true*/)
public class MiningDimensionTest
{

    public static final String MOD_NAME = "MiningDimensionTest";
    public static final String MOD_ID = "miningdimensiontest";
    public static final String commandName = "gmining";
    public static String dimtypename = "miningdimensiontest";
    @Instance("miningdimensiontest")
    public static MiningDimensionTest inst;
    public static DimensionType dimtypetest;
    public static int dimtypetestid;
    public static boolean keepinv = true;
    public static boolean enableMapFeatures = false;// bug if true: Caused by: java.lang.NullPointerException
    // net.minecraft.world.gen.feature.WorldGenFossils.generate(WorldGenFossils.java:45)
    public static WorldType worldtype;
    public static String savefoldername;
    public static GameType gameType;
    public static int miningid;// this should be saved on a file or something
    static
    {
        dimtypetest = DimensionType.OVERWORLD;
        worldtype = WorldType.AMPLIFIED;
        savefoldername = "mining";
        gameType = GameType.SURVIVAL;
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        // dimtypetestid = getNextFreeDimentionTypeId();
        // dimtypetest = DimensionType.register(dimtypename, "_mine", dimtypetestid , WorldProviderTest.class, false); //does not work... DimensionType... getConstructor(new
        // Class[0]) NoSuchMethodException
        // System.out.println("MiningDimensionTest Registered dimension type "+ dimtypetest + " "+ dimtypetest.name() + " " + dimtypetest.getId());

    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new MiningDimensionTest.DimensionTestCommand());
        System.out.println("MiningDimensionTest Registered  command /mining <int>");
        // attempt to force load first mining dimension if any
        for (File file : new File(event.getServer().isSinglePlayer() ? "saves" : ".").listFiles())// ugly but good enough for testing
        {
            // System.out.println("Checking file " + file);
            if (file.isFile() || !file.getName().startsWith(savefoldername))
                continue;
            String sint = file.getName().replace(savefoldername, "");
            if (!sint.isEmpty() && isInteger(sint))
            {
                miningid = Integer.valueOf(sint);
                DimensionManager.registerDimension(miningid, dimtypetest);
                System.out.println("Found OFFLINE mining dimension " + miningid);
                break;
            }
        }
    }

    public static boolean isInteger(String string)
    {
        try
        {
            Integer.valueOf(string);
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    @EventHandler
    public void postinit(FMLPostInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDimensionPreload(DimensionPreloadEvent e)
    {
        System.out.println(MOD_NAME + " WORKS================ DimensionPreloadEvent id: " + e.getDimensionId());

        if (e.getDimensionId() == miningid)
        {
            if (DimensionManager.getWorld(e.getDimensionId()) == null)
            {
                System.out.println("Mining dimension detected... generating new dimension for the first time id:" + e.getDimensionId());

                WorldServer newdim = geterateNewMiningDimension(e.getDimensionId());
                newdim.init();
                System.out.println("DimensionPreloadEvent setting alternative");
                e.setAlternativeDimension(newdim);
                System.out.println("DimensionPreloadEvent end event");
            }
            else
            {
                System.out.println("Mining dimension detected... doing nothing... id:" + e.getDimensionId());
            }
        }

    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onServerConnectionFromClientEvent(FMLNetworkEvent.ServerConnectionFromClientEvent e)
    {
        if (miningid != 0)
        {
            FMLEmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DISPATCHER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(NetworkDispatcher.get(e.getManager()));
            channel.writeOutbound(new DimensionRegisterMessage(miningid, dimtypetest.toString()));
        }
    }

    private static int getNextFreeDimentionTypeId()
    {
        for (int i = 2; i < 1000; ++i)
        {
            try
            {
                DimensionType.getById(i);
            }
            catch (IllegalArgumentException var2)
            {
                return i;
            }
        }
        return -2;
    }

    private static MinecraftServer getServer()
    {
        return FMLCommonHandler.instance().getSidedDelegate().getServer();
    }

    public static final void teleportPlayer(EntityPlayerMP player, int dimId)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT))
            return;
        int dimNow = player.dimension;
        boolean adjust = true;
        if (DimensionManager.isDimensionRegistered(dimId))
        {
            final WorldServer destWorld = getServer().worldServerForDimension(dimId);

            if (destWorld == null)
            {
                player.addChatMessage(new TextComponentString("Ups.. something went wrong, world null for id: " + dimId + " but is registered!!"));
            }
            else
            {
                if (dimNow != dimId)
                {
                    if (!(player instanceof EntityPlayerMP))
                    {
                        if (destWorld != null)
                        {
                            getServer().getPlayerList().transferEntityToWorld(player, player.getEntityWorld().provider.getDimension(),
                                    getServer().worldServerForDimension(player.getEntityWorld().provider.getDimension()), destWorld);
                        }
                        return;
                    }
                    getServer().getPlayerList().transferPlayerToDimension(player, dimId, new Teleporter(destWorld) {
                        public void placeInPortal(Entity entityIn, float rotationYaw)
                        {
                        }
                    });
                }
                BlockPos pos = destWorld.getSpawnPoint();
                if (pos == null)
                {
                    destWorld.setInitialSpawnLocation();
                    pos = destWorld.getSpawnPoint();
                }
                BlockPos blockpos = findHighestBlock(destWorld, pos, 5, false);
                double x = (double) blockpos.getX();
                double y = (double) blockpos.getY();
                double z = (double) blockpos.getZ();
                if (adjust)
                {
                    x += 0.5D;
                    y += 0.5D;
                }
                if (player.connection != null)
                {
                    player.setPositionAndUpdate(x, y, z);
                }

            }
        }
        else
        {
            player.addChatMessage(new TextComponentString("Dimension id: " + dimId + " is not registered!!"));
        }
    }

    private static BlockPos findHighestBlock(World p_184308_0_, BlockPos p_184308_1_, int p_184308_2_, boolean p_184308_3_)
    {
        BlockPos blockpos = null;

        for (int i = -p_184308_2_; i <= p_184308_2_; ++i)
        {
            for (int j = -p_184308_2_; j <= p_184308_2_; ++j)
            {
                if (i != 0 || j != 0 || p_184308_3_)
                {
                    for (int k = 255; k > (blockpos == null ? 0 : blockpos.getY()); --k)
                    {
                        BlockPos blockpos1 = new BlockPos(p_184308_1_.getX() + i, k, p_184308_1_.getZ() + j);
                        IBlockState iblockstate = p_184308_0_.getBlockState(blockpos1);

                        if (iblockstate.isBlockNormalCube() && (p_184308_3_ || iblockstate.getBlock() != Blocks.BEDROCK))
                        {
                            blockpos = blockpos1;
                            break;
                        }
                    }
                }
            }
        }

        return blockpos == null ? p_184308_1_ : blockpos;
    }
    public static class DimensionTestCommand extends CommandBase
    {

        public String getCommandName()
        {
            return "mining";
        }

        public String getName()
        {
            return "mining";
        }

        public String getCommandUsage(ICommandSender sender)
        {
            return "/mining <int> - optional argument will just tp to dimension";
        }

        public String getUsage(ICommandSender sender)
        {
            return "/mining <int> - optional argument will just tp to dimension";
        }

        public boolean checkPermission(MinecraftServer server, ICommandSender sender)
        {
            return true;
        }

        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            if (args.length < 1)
            {
                if (miningid == 0)
                {
                    // creating new mining dimension
                    miningid = DimensionManager.getNextFreeDimId();
                    DimensionManager.registerDimension(miningid, dimtypetest);// MiningDimensionTest.dimtypetest);
                    sender.addChatMessage(
                            new TextComponentString(ChatFormatting.DARK_GREEN + "Generating new mining dimension id: " + ChatFormatting.YELLOW + miningid));
                    // FMLEmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE", Side.SERVER);
                    // channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
                    // channel.writeOutbound(new DimensionRegisterMessage(miningid,dimtypetest.toString()));
                    return;
                }
                // tp to mining dimension
                // WorldServer dim = DimensionManager.getWorld(miningid);
                sender.addChatMessage(new TextComponentString(ChatFormatting.DARK_GREEN + "Teleporting you to mining dimension with id: " + miningid));
                MiningDimensionTest.teleportPlayer(player, miningid);
                return;
            }
            else
            {
                if (args[0].equalsIgnoreCase("reset"))
                {
                    miningid = 0;
                    sender.addChatMessage(new TextComponentString(
                            ChatFormatting.DARK_GREEN + "Dimension mining id reset, next time when run '/mining' will generate new one"));
                    return;
                }
                if (args[0].equalsIgnoreCase("info"))
                {
                    int pdimid = player.getEntityWorld().provider.getDimension();
                    if (miningid == 0)
                    {
                        sender.addChatMessage(new TextComponentString(ChatFormatting.DARK_GREEN + "No mining dimension registered"));
                    }

                    WorldInfo info = player.getEntityWorld().getWorldInfo();
                    sender.addChatMessage(new TextComponentString(ChatFormatting.DARK_GREEN + "Dimension Name: " + ChatFormatting.YELLOW + info.getWorldName()
                            + ChatFormatting.DARK_GREEN + " id: " + ChatFormatting.YELLOW + pdimid));
                    sender.addChatMessage(new TextComponentString(ChatFormatting.DARK_GREEN + "GameType: " + ChatFormatting.YELLOW + info.getGameType()
                            + ChatFormatting.DARK_GREEN + ", Difficulty: " + ChatFormatting.YELLOW + info.getDifficulty()));
                    sender.addChatMessage(new TextComponentString(ChatFormatting.DARK_GREEN + "Seed: " + ChatFormatting.YELLOW + info.getSeed()));
                    TextComponentString rules = new TextComponentString("");
                    for (String rule : info.getGameRulesInstance().getRules())
                    {
                        rules.appendText(ChatFormatting.DARK_GREEN + rule + ": " + ChatFormatting.YELLOW + info.getGameRulesInstance().getString(rule)
                                + ChatFormatting.RESET + ",");
                    }
                    sender.addChatMessage(rules);
                    sender.addChatMessage(new TextComponentString(ChatFormatting.DARK_GREEN + "You are in dimension: " + ChatFormatting.YELLOW + pdimid
                            + ChatFormatting.DARK_GREEN + " pos: " + ChatFormatting.YELLOW + player.getPosition().getX() + "," + player.getPosition().getY()
                            + "," + player.getPosition().getZ()));
                    return;
                }
                int dimId = 0;
                if (args.length > 0)
                {
                    dimId = parseInt(args[0]);
                }
                if (!DimensionManager.isDimensionRegistered(dimId))
                {
                    sender.addChatMessage(new TextComponentString(ChatFormatting.DARK_GREEN + "No dimension registered with id: " + dimId));
                    return;
                }
                else
                {
                    WorldServer dim = DimensionManager.getWorld(dimId);
                    sender.addChatMessage(new TextComponentString(ChatFormatting.DARK_GREEN + "Teleporting you to dimension with id: " + dimId));
                    MiningDimensionTest.teleportPlayer(player, dimId);
                }

            }
        }
    }

    public WorldServerMultiTest geterateNewMiningDimension(int id)
    {
        MinecraftServer server = getServer();
        WorldServer overworld = DimensionManager.getWorld(0);
        long seedIn = (new Random()).nextLong();
        WorldSettings settings = new WorldSettings(seedIn, gameType, enableMapFeatures, false, worldtype).enableBonusChest();
        // make new from overworld file
        WorldInfo info = overworld.getSaveHandler().loadWorldInfo();
        info.populateFromWorldSettings(settings);
        info.getGameRulesInstance().setOrCreateGameRule("keepInventory", String.valueOf(keepinv));
        info.getGameRulesInstance().setOrCreateGameRule("doDaylightCycle", "false");
        info.getGameRulesInstance().setOrCreateGameRule("doMobSpawning", "false");
        info.setWorldName(savefoldername + id);
        ISaveHandler savehandler = server.getActiveAnvilConverter().getSaveLoader(info.getWorldName(), false);
        WorldServerMultiTest world = new WorldServerMultiTest(server, savehandler, info, id, overworld, server.theProfiler);
        world.provider.setDimension(id);
        world.initialize(settings);
        return world;
    }

    public class SaveHandlerTest2 extends AnvilSaveHandler
    {
        /** The directory in which to save world data. */
        private final File worldDirectory;
        private final File parentfolder;

        public SaveHandlerTest2(File parentfolder, String saveDirectoryNameIn, boolean structures, DataFixer dataFixerIn)
        {
            super(parentfolder, saveDirectoryNameIn, structures, dataFixerIn);
            this.parentfolder = parentfolder;
            this.worldDirectory = new File(parentfolder, saveDirectoryNameIn);
        }

        /**
         * Gets the File object corresponding to the base directory of this world.
         *//*
           * public File getWorldDirectory() { return parentfolder; }
           */
        /**
         * used to update level.dat from old format to MCRegion format
         */
        @Override
        public void saveWorldInfo(WorldInfo worldInformation)
        {
            this.saveWorldInfoWithPlayer(worldInformation, (NBTTagCompound) null);
        }

        /**
         * Loads and returns the world info
         */
        @Override
        public WorldInfo loadWorldInfo()
        {
            File file1 = new File(this.worldDirectory, "level.dat");

            if (file1.exists())
            {
                WorldInfo worldinfo = SaveFormatOld.loadAndFix(file1, this.dataFixer, this);

                if (worldinfo != null)
                {
                    return worldinfo;
                }
            }

            net.minecraftforge.fml.common.FMLCommonHandler.instance().confirmBackupLevelDatUse(this);
            file1 = new File(this.worldDirectory, "level.dat_old");
            return file1.exists() ? SaveFormatOld.loadAndFix(file1, this.dataFixer, this) : null;
        }

        /**
         * Saves the given World Info with the given NBTTagCompound as the Player.
         */
        @Override
        public void saveWorldInfoWithPlayer(WorldInfo worldInformation, @Nullable NBTTagCompound tagCompound)
        {
            NBTTagCompound nbttagcompound = worldInformation.cloneNBTCompound(tagCompound);
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.setTag("Data", nbttagcompound);

            net.minecraftforge.fml.common.FMLCommonHandler.instance().handleWorldDataSave(this, worldInformation, nbttagcompound1);

            try
            {
                File file1 = new File(this.worldDirectory, "level.dat_new");
                File file2 = new File(this.worldDirectory, "level.dat_old");
                File file3 = new File(this.worldDirectory, "level.dat");
                CompressedStreamTools.writeCompressed(nbttagcompound1, new FileOutputStream(file1));

                if (file2.exists())
                {
                    file2.delete();
                }

                file3.renameTo(file2);

                if (file3.exists())
                {
                    file3.delete();
                }

                file1.renameTo(file3);

                if (file1.exists())
                {
                    file1.delete();
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }

    }

    public class WorldProviderTest extends WorldProvider
    {
        /*
         * public WorldProviderTest() { // super(); }
         */
        @Override
        public DimensionType getDimensionType()
        {
            return DimensionType.getById(2);
        }

        /**
         * True if the player can respawn in this dimension (true = overworld, false = nether).
         */
        public boolean canRespawnHere()
        {
            return false;
        }

        /**
         * Will check if the x, z position specified is alright to be set as the map spawn point
         */
        public boolean canCoordinateBeSpawn(int x, int z)
        {
            return false;
        }
    }

    public class WorldServerMultiTest extends WorldServer
    {
        private final WorldServer delegate;
        private IBorderListener borderListener;
        private Teleporter teleporter;

        public WorldServerMultiTest(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, WorldServer delegate,
                                    Profiler profilerIn)
        {
            super(server, saveHandlerIn, info, dimensionId, profilerIn);
            this.delegate = delegate;
            this.borderListener = new IBorderListener() {
                public void onSizeChanged(WorldBorder border, double newSize)
                {
                    WorldServerMultiTest.this.getWorldBorder().setTransition(newSize);
                }

                public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time)
                {
                    WorldServerMultiTest.this.getWorldBorder().setTransition(oldSize, newSize, time);
                }

                public void onCenterChanged(WorldBorder border, double x, double z)
                {
                    WorldServerMultiTest.this.getWorldBorder().setCenter(x, z);
                }

                public void onWarningTimeChanged(WorldBorder border, int newTime)
                {
                    WorldServerMultiTest.this.getWorldBorder().setWarningTime(newTime);
                }

                public void onWarningDistanceChanged(WorldBorder border, int newDistance)
                {
                    WorldServerMultiTest.this.getWorldBorder().setWarningDistance(newDistance);
                }

                public void onDamageAmountChanged(WorldBorder border, double newAmount)
                {
                    WorldServerMultiTest.this.getWorldBorder().setDamageAmount(newAmount);
                }

                public void onDamageBufferChanged(WorldBorder border, double newSize)
                {
                    WorldServerMultiTest.this.getWorldBorder().setDamageBuffer(newSize);
                }
            };
            this.delegate.getWorldBorder().addListener(this.borderListener);
            this.teleporter = new Teleporter(this) {
                @Override
                public void placeInPortal(Entity entityIn, float rotationYaw)
                {
                }
            };
        }

        protected void saveLevel() throws MinecraftException
        {
            this.perWorldStorage.saveAllData();
            this.saveHandler.saveWorldInfo(this.worldInfo);
        }

        @Override
        public Teleporter getDefaultTeleporter()
        {
            return this.teleporter;
        }

        public World init()
        {
            this.mapStorage = this.delegate.getMapStorage();
            this.worldScoreboard = this.delegate.getScoreboard();
            this.lootTable = this.delegate.getLootTableManager();
            String s = VillageCollection.fileNameForProvider(this.provider);
            VillageCollection villagecollection = (VillageCollection) this.perWorldStorage.getOrLoadData(VillageCollection.class, s);

            if (villagecollection == null)
            {
                this.villageCollectionObj = new VillageCollection(this);
                this.perWorldStorage.setData(s, this.villageCollectionObj);
            }
            else
            {
                this.villageCollectionObj = villagecollection;
                this.villageCollectionObj.setWorldsForAll(this);
            }

            initCapabilities();
            return this;
        }

        public void flush()
        {
            super.flush();
            this.delegate.getWorldBorder().removeListener(this.borderListener);
        }

        public void saveAdditionalData()
        {
            this.provider.onWorldSave();
        }
    }

}