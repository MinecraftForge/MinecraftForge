package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.SimpleTicket;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(modid = FarmlandWaterTest.ID, name = "Farmland Water Test", version = "1.0.0", acceptableRemoteVersions = "*")
public class FarmlandWaterTest
{
    //This mod has two test cases: A block in the creates a 4x4x4 watered region when activated, and a 8x8x8 region will be watered for 30s when shooting an arrow
    private static final boolean ENABLED = true;
    private static Logger logger;
    private static Block testBlock;
    static final String ID = "farmlandwatertest";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            logger = event.getModLog();
            MinecraftForge.EVENT_BUS.register(FarmlandWaterTest.class);
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        testBlock = new TestBlock();
        event.getRegistry().register(testBlock.setRegistryName(new ResourceLocation(ID, "test_block")).setCreativeTab(CreativeTabs.MISC).setUnlocalizedName("Farmland Water Test Block"));
        GameRegistry.registerTileEntity(TestTileEntity.class, new ResourceLocation(ID, "test_te"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(testBlock).setRegistryName(new ResourceLocation(ID, "test_block")));
    }

    //Sets a region of 16x16x16 watered at the pos where an arrow hit
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent.Arrow event)
    {
        World world = event.getEntity().world;
        if (!world.isRemote && event.getArrow().shootingEntity instanceof EntityPlayer)
        {
            BlockPos pos = new BlockPos(event.getRayTraceResult().hitVec);
            AxisAlignedBB aabb = new AxisAlignedBB(pos).grow(8D);
            //600 ticks = 30 seconds
            FarmlandWaterManager.addWateredRegion(world, aabb, 600);
            logger.info("Watering " + aabb + " for 30 seconds");
        }
    }

    public static class TestBlock extends Block
    {

        public TestBlock()
        {
            super(Material.ROCK);
        }

        @Override
        public boolean hasTileEntity(IBlockState state)
        {
            return true;
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(World world, IBlockState state)
        {
            return new TestTileEntity();
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
        {
            if (world.isRemote)
                return true;
            TestTileEntity tileEntity = (TestTileEntity) world.getTileEntity(pos);
            if (tileEntity == null)
            {
                return false;
            }
            tileEntity.isActive = !tileEntity.isActive;
            if (!tileEntity.isActive && tileEntity.farmlandTicket != null)
            {
                tileEntity.farmlandTicket.invalidate();
            }
            player.sendStatusMessage(new TextComponentString("Changed block powered state to " + tileEntity.isActive), true);
            logger.info("Changed block powered state at {} to {}", pos, tileEntity.isActive);
            return true;
        }
    }

    public static class TestTileEntity extends TileEntity implements ITickable
    {
        private SimpleTicket<AxisAlignedBB> farmlandTicket;
        private boolean isActive = false;

        @Override
        public void update()
        {
            if (!world.isRemote && isActive)
            {
                if (farmlandTicket == null)
                {
                    farmlandTicket = FarmlandWaterManager.addWateredRegion(world, new AxisAlignedBB(getPos()).grow(4D));
                }
                farmlandTicket.validate();
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound)
        {
            compound = super.writeToNBT(compound);
            compound.setBoolean("active", isActive);
            return compound;
        }

        @Override
        public void readFromNBT(NBTTagCompound compound)
        {
            super.readFromNBT(compound);
            isActive = compound.getBoolean("active");
        }
    }
}
