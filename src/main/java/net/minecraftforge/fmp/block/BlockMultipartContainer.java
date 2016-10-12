package net.minecraftforge.fmp.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.util.RayTraceUtils;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fmp.client.multipart.AdvancedParticleManager;
import net.minecraftforge.fmp.client.multipart.MultipartStateMapper;
import net.minecraftforge.fmp.multipart.IMultipart;
import net.minecraftforge.fmp.multipart.Multipart;
import net.minecraftforge.fmp.multipart.MultipartRegistry;
import net.minecraftforge.fmp.multipart.PartState;

/**
 * A final class that extends {@link BlockContainer} and represents a block which can contain any kind of multipart.
 * <br/>
 * You do NOT need to extend this class for your multiparts to work. I repeat, you do NOT. You need to either extend
 * {@link Multipart} or implement {@link IMultipart}. If you only need microblock support, look into
 * {@link BlockCoverable}.
 */
public final class BlockMultipartContainer extends Block implements ITileEntityProvider
{

    public BlockMultipartContainer()
    {
        super(Material.GROUND);
        setDefaultState(getDefaultState().withProperty(PROPERTY_TICKING, true));
    }

    private TileMultipartContainer getMultipartTile(IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileMultipartContainer ? (TileMultipartContainer) tile : null;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return meta == 1 ? new TileMultipartContainer() : new TileMultipartContainer.Ticking();
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        if (state == null || world == null || pos == null || start == null || end == null)
        {
            return null;
        }
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            return tile.getPartContainer().collisionRayTrace(start, end);
        }
        return null;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
            List<AxisAlignedBB> collidingBoxes, Entity collidingEntity)
    {
        TileMultipartContainer tile = getMultipartTile(worldIn, pos);
        if (tile != null)
        {
            List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
            AxisAlignedBB box = entityBox.offset(-pos.getX(), -pos.getY(), -pos.getZ());
            tile.getPartContainer().addCollisionBoxes(box, list, collidingEntity);
            for (AxisAlignedBB aabb : list)
            {
                collidingBoxes.add(aabb.offset(pos));
            }
        }
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            return tile.getPartContainer().getLightValue();
        }
        return 0;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        if (target instanceof RayTraceResult)
        {
            TileMultipartContainer tile = getMultipartTile(world, pos);
            if (tile != null)
            {
                return tile.getPartContainer().getPickBlock(player, (RayTraceResult) target);
            }
        }
        return null;
    }

    private TileMultipartContainer brokenTile = null;

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack)
    {
        brokenTile = te instanceof TileMultipartContainer ? (TileMultipartContainer) te : null;
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        brokenTile = null;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile == null)
        {
            tile = brokenTile;
        }
        if (tile != null)
        {
            return tile.getPartContainer().getDrops();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            RayTraceResult hit = reTrace(world, pos, player);
            if (hit != null)
            {
                return tile.getPartContainer().harvest(player, hit) ? super.removedByPlayer(state, world, pos, player, willHarvest) : false;
            }
        }
        return false;
    }

    @Override
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            RayTraceResult hit = reTrace(world, pos, player);
            if (hit != null)
            {
                return tile.getPartContainer().getHardness(player, hit);
            }
        }
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem,
            EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            return tile.getPartContainer().onActivated(player, hand, heldItem, reTrace(world, pos, player));
        }
        return false;
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            tile.getPartContainer().onClicked(player, reTrace(world, pos, player));
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock)
    {
        TileMultipartContainer tile = getMultipartTile(worldIn, pos);
        if (tile != null)
        {
            tile.getPartContainer().onNeighborBlockChange(neighborBlock);
        }
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            tile.getPartContainer().onNeighborTileChange(
                EnumFacing.getFacingFromVector(neighbor.getX() - pos.getX(), neighbor.getY() - pos.getY(), neighbor.getZ() - pos.getZ()));
        }
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            tile.getPartContainer().onEntityStanding(entity);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            tile.getPartContainer().onEntityCollided(entity);
        }
    }

    @Override
    public Boolean isAABBInsideMaterial(World world, BlockPos pos, AxisAlignedBB aabb, Material material)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            return tile.getPartContainer().isAABBInsideMaterial(aabb, material);
        }
        return null;
    }

    @Override
    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos pos, IBlockState state, Entity entity, double yToTest,
            Material material, boolean testingHead)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            return tile.getPartContainer().isEntityInsideMaterial(entity, yToTest, material, testingHead);
        }
        return null;
    }

    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if (side == null)
        {
            return false;
        }
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            return tile.getPartContainer().canConnectRedstone(side.getOpposite());
        }
        return false;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if (side == null)
        {
            return 0;
        }
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            return tile.getPartContainer().getWeakSignal(side.getOpposite());
        }
        return 0;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if (side == null)
        {
            return 0;
        }
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            return tile.getPartContainer().getStrongSignal(side.getOpposite());
        }
        return 0;
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            return tile.getPartContainer().isSideSolid(side);
        }
        return false;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            return tile.getPartContainer().canPlaceTorchOnTop();
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            tile.getPartContainer().randomDisplayTick(rand);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager particleManager)
    {
        RayTraceResult hit = reTrace(world, pos, FMLClientHandler.instance().getClientPlayerEntity());
        if (hit != null)
        {
            if (hit.partHit.addDestroyEffects(AdvancedParticleManager.getInstance(particleManager)))
                return true;

            ResourceLocation path = hit.partHit.getModelPath();
            IBlockState state = hit.partHit.getExtendedState(MultipartRegistry.getDefaultState(hit.partHit).getBaseState());
            IBakedModel model = path == null ? null
                    : Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getModel(
                            new ModelResourceLocation(path, MultipartStateMapper.instance.getPropertyString(state.getProperties())));
            if (model != null)
            {
                TextureAtlasSprite icon = model.getParticleTexture();
                if (icon != null)
                {
                    AdvancedParticleManager.getInstance(particleManager).addBlockDestroyEffects(pos, icon);
                }
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager particleManager)
    {
        RayTraceResult hit = target instanceof RayTraceResult ? (RayTraceResult) target : null;
        if (hit != null)
        {
            if (hit.partHit.addHitEffects(hit, AdvancedParticleManager.getInstance(particleManager)))
            {
                return true;
            }

            ResourceLocation path = hit.partHit.getModelPath();
            IBlockState partState = hit.partHit.getExtendedState(MultipartRegistry.getDefaultState(hit.partHit).getBaseState());
            IBakedModel model = path == null ? null
                    : Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getModel(
                            new ModelResourceLocation(path, MultipartStateMapper.instance.getPropertyString(partState.getProperties())));
            if (model != null)
            {
                TextureAtlasSprite icon = model.getParticleTexture();
                if (icon != null)
                {
                    AdvancedParticleManager.getInstance(particleManager).addBlockHitEffects(target.getBlockPos(), hit, hit.boxHit, icon);
                }
            }
        }
        return true;
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate,
            EntityLivingBase entity, int numberOfParticles)
    {
        return true;
    }

    private RayTraceResult reTrace(World world, BlockPos pos, EntityPlayer player)
    {
        Vec3d start = RayTraceUtils.getStart(player);
        Vec3d end = RayTraceUtils.getEnd(player);
        return getMultipartTile(world, pos).getPartContainer().collisionRayTrace(start, end);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    public static final IProperty<Boolean> PROPERTY_TICKING = PropertyBool.create("ticking");
    public static final IUnlistedProperty<List<PartState>> PROPERTY_MULTIPART_CONTAINER = new PropertyMultipartStates(
            "multipart_container");

    @Override
    public IExtendedBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        List<PartState> states;
        if (tile != null)
        {
            states = tile.getPartContainer().getExtendedStates(world, pos);
        }
        else
        {
            states = new ArrayList<PartState>();
        }
        return ((IExtendedBlockState) state).withProperty(PROPERTY_MULTIPART_CONTAINER, states);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PROPERTY_TICKING) ? 0 : 1;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PROPERTY_TICKING, meta == 0);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder(this).add(PROPERTY_TICKING).add(PROPERTY_MULTIPART_CONTAINER).build();
    }

    @Override
    public boolean canRenderInLayer(BlockRenderLayer layer)
    {
        return true;
    }

    @Override
    public boolean canRotateBlockAround(World world, BlockPos pos, EnumFacing axis)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            for (IMultipart part : tile.getPartContainer().getParts())
            {
                if (!part.canRotatePartAround(axis))
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void rotateBlockAround(World world, BlockPos pos, EnumFacing axis)
    {
        TileMultipartContainer tile = getMultipartTile(world, pos);
        if (tile != null)
        {
            for (IMultipart part : tile.getPartContainer().getParts())
            {
                part.rotatePartAround(axis);
            }
        }
    }

    @Override
    public boolean canPlayerRotate(World world, BlockPos pos, EnumFacing axis, EntityPlayer player, RayTraceResult hit)
    {
        if (hit != null && hit.partHit != null)
        {
            return hit.partHit.canPlayerRotate(axis, player, hit);
        }
        return false;
    }

    @Override
    public void rotateBlock(World world, BlockPos pos, EnumFacing axis, EntityPlayer player, RayTraceResult hit)
    {
        if (hit != null && hit.partHit != null)
        {
            hit.partHit.rotatePart(axis, player, hit);
        }
    }

    private static class PropertyMultipartStates implements IUnlistedProperty<List<PartState>>
    {

        private String name;

        public PropertyMultipartStates(String name)
        {
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public boolean isValid(List<PartState> value)
        {
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<List<PartState>> getType()
        {
            return (Class<List<PartState>>) (Class<?>) List.class;
        }

        @Override
        public String valueToString(List<PartState> value)
        {
            return "";
        }

    }

}
