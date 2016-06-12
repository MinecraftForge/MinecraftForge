package net.minecraftforge.fmp.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
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
import net.minecraft.item.EnumDyeColor;
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
import net.minecraftforge.common.util.RayTraceUtils;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fmp.ForgeMultipartModContainer;
import net.minecraftforge.fmp.capabilities.CapabilityMicroblockContainerTile;
import net.minecraftforge.fmp.client.multipart.AdvancedParticleManager;
import net.minecraftforge.fmp.client.multipart.MultipartStateMapper;
import net.minecraftforge.fmp.microblock.IMicroblockContainerTile;
import net.minecraftforge.fmp.microblock.MicroblockContainer;
import net.minecraftforge.fmp.multipart.IMultipart;
import net.minecraftforge.fmp.multipart.Multipart;
import net.minecraftforge.fmp.multipart.MultipartContainer;
import net.minecraftforge.fmp.multipart.MultipartRegistry;
import net.minecraftforge.fmp.multipart.PartState;

/**
 * An {@link Block} that allows your block to act as a microblock container.<br/>
 * Extend this class if you want your block to be able to have microblocks, but not any other kinds of multiparts.
 * Otherwise, extend {@link Multipart} or make a custom implementation of {@link IMultipart}.<br/>
 * All the overriden methods have a "default" counterpart that allows you to handle interactions with your block if the
 * player isn't interacting with a multipart.<br/>
 * Extend {@link TileCoverable} or implement {@link IMicroblockContainerTile} and return it in
 * {@link BlockCoverable#createNewTileEntity(World, int)} if you want a custom tile entity for your block.
 */
public class BlockCoverable extends Block implements ITileEntityProvider
{

    public BlockCoverable(Material material)
    {
        super(material);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileCoverable();
    }

    protected IMicroblockContainerTile getMicroblockTile(IBlockAccess world, BlockPos pos)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null)
        {
            return null;
        }
        return getMicroblockTile(tile);
    }

    protected IMicroblockContainerTile getMicroblockTile(TileEntity tile)
    {
        if (!tile.hasCapability(CapabilityMicroblockContainerTile.MICROBLOCK_CONTAINER_CAPABILITY, null))
        {
            return null;
        }
        return tile.getCapability(CapabilityMicroblockContainerTile.MICROBLOCK_CONTAINER_CAPABILITY, null);
    }

    @Override
    public final RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        RayTraceResult tileRaytrace = null;
        if (tile != null)
        {
            tileRaytrace = tile.getMicroblockContainer().getPartContainer().collisionRayTrace(start, end);
        }
        RayTraceResult defaultRaytrace = collisionRayTraceDefault(state, world, pos, start, end);
        if (tileRaytrace == null || (defaultRaytrace != null
                && defaultRaytrace.hitVec.squareDistanceTo(start) < tileRaytrace.hitVec.squareDistanceTo(start)))
        {
            return defaultRaytrace;
        }
        return tileRaytrace;
    }

    public RayTraceResult collisionRayTraceDefault(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        return super.collisionRayTrace(state, world, pos, start, end);
    }

    @Override
    public final void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
            List<AxisAlignedBB> collidingBoxes, Entity collidingEntity)
    {
        addCollisionBoxToListDefault(state, world, pos, entityBox, collidingBoxes, collidingEntity);
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        List<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
        AxisAlignedBB box = entityBox.offset(-pos.getX(), -pos.getY(), -pos.getZ());
        tile.getMicroblockContainer().getPartContainer().addCollisionBoxes(box, list, collidingEntity);
        for (AxisAlignedBB aabb : list)
        {
            collidingBoxes.add(aabb.offset(pos));
        }
    }

    public void addCollisionBoxToListDefault(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
            List<AxisAlignedBB> collidingBoxes, Entity entityIn)
    {
        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn);
    }

    @Override
    public final int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        return Math.max(tile != null ? tile.getMicroblockContainer().getPartContainer().getLightValue() : 0,
                getLightValueDefault(state, world, pos));
    }

    public int getLightValueDefault(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return super.getLightValue(state, world, pos);
    }

    @Override
    public final ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        if (target.partHit != null)
        {
            IMicroblockContainerTile tile = getMicroblockTile(world, pos);
            return tile != null ? tile.getMicroblockContainer().getPartContainer().getPickBlock(player, target) : null;
        }
        return getPickBlockDefault(state, target, world, pos, player);
    }

    public ItemStack getPickBlockDefault(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return super.getPickBlock(state, target, world, pos, player);
    }

    private IMicroblockContainerTile brokenTile = null;
    private boolean harvestingWrapper = false;

    @Override
    public final void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack)
    {
        brokenTile = getMicroblockTile(tile);
        harvestBlockDefault(world, player, pos, state, tile, stack);
        brokenTile = null;
    }

    public void harvestBlockDefault(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tile, ItemStack stack)
    {
        super.harvestBlock(worldIn, player, pos, state, tile, stack);
    }

    @Override
    public final List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        if (harvestingWrapper)
        {
            return getDropsDefault(world, pos, state, fortune);
        }
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile == null && (tile = brokenTile) == null)
        {
            return getDropsDefault(world, pos, state, fortune);
        }
        List<ItemStack> drops = new ArrayList<ItemStack>();
        drops.addAll(getDropsDefault(world, pos, state, fortune));
        drops.addAll(tile.getMicroblockContainer().getPartContainer().getDrops());
        return drops;
    }

    public List<ItemStack> getDropsDefault(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        return super.getDrops(world, pos, state, fortune);
    }

    @Override
    public final boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        RayTraceResult hit = reTraceAll(world, pos, player);
        if (hit.partHit != null)
        {
            IMicroblockContainerTile tile = getMicroblockTile(world, pos);
            if (tile != null)
            {
                tile.getMicroblockContainer().getPartContainer().harvest(player, (RayTraceResult) hit);
            }
            return false;
        }
        else
        {
            IMicroblockContainerTile tile = getMicroblockTile(world, pos);
            MultipartContainer container = tile != null ? tile.getMicroblockContainer().getPartContainer() : null;
            if (container.getParts().isEmpty())
            {
                return removedByPlayerDefault(state, world, pos, player, willHarvest);
            }
            else
            {
                if (!removedByPlayerDefault(state, world, pos, player, willHarvest))
                {
                    return false;
                }
                if (!world.setBlockState(pos, ForgeMultipartModContainer.multipart.getDefaultState(), 0))
                {
                    return false;
                }
                world.removeTileEntity(pos);
                world.setTileEntity(pos, new TileMultipartContainer(container));
                world.notifyBlockUpdate(pos, state, ForgeMultipartModContainer.multipart.getDefaultState(), 0);
                harvestingWrapper = true;
                if (!player.capabilities.isCreativeMode)
                {
                    harvestBlock(world, player, pos, world.getBlockState(pos), world.getTileEntity(pos), player.getActiveItemStack());
                }
                harvestingWrapper = false;
                return false;
            }
        }
    }

    public boolean removedByPlayerDefault(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public final float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos)
    {
        RayTraceResult hit = reTraceAll(world, pos, player);
        if (hit.partHit != null)
        {
            IMicroblockContainerTile tile = getMicroblockTile(world, pos);
            if (tile != null)
            {
                return tile.getMicroblockContainer().getPartContainer().getHardness(player, hit);
            }
            return 0F;
        }
        else
        {
            return getPlayerRelativeBlockHardnessDefault(state, player, world, pos);
        }
    }

    public float getPlayerRelativeBlockHardnessDefault(IBlockState state, EntityPlayer player, World world, BlockPos pos)
    {
        return super.getPlayerRelativeBlockHardness(state, player, world, pos);
    }

    @Override
    public final boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
            ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        RayTraceResult hit = reTraceAll(world, pos, player);
        if (hit.partHit != null)
        {
            IMicroblockContainerTile tile = getMicroblockTile(world, pos);
            if (tile != null)
            {
                return tile.getMicroblockContainer().getPartContainer().onActivated(player, hand, heldItem, hit);
            }
            return false;
        }
        else
        {
            return onBlockActivatedDefault(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
        }
    }

    public boolean onBlockActivatedDefault(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
            ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public final void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
    {
        RayTraceResult hit = reTraceAll(world, pos, player);
        if (hit.partHit != null)
        {
            IMicroblockContainerTile tile = getMicroblockTile(world, pos);
            if (tile != null)
            {
                tile.getMicroblockContainer().getPartContainer().onClicked(player, (RayTraceResult) hit);
            }
        }
        else
        {
            onBlockClickedDefault(world, pos, player);
        }
    }

    public void onBlockClickedDefault(World world, BlockPos pos, EntityPlayer player)
    {
        super.onBlockClicked(world, pos, player);
    }

    @Override
    public final void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile != null)
        {
            tile.getMicroblockContainer().getPartContainer().onNeighborBlockChange(neighborBlock);
        }
        onNeighborBlockChangeDefault(world, pos, state, neighborBlock);
    }

    public void onNeighborBlockChangeDefault(World world, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        super.neighborChanged(state, world, pos, neighborBlock);
    }

    @Override
    public final void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile != null)
        {
            tile.getMicroblockContainer().getPartContainer().onNeighborTileChange(EnumFacing
                    .getFacingFromVector(neighbor.getX() - pos.getX(), neighbor.getY() - pos.getY(), neighbor.getZ() - pos.getZ()));
        }
        onNeighborChangeDefault(world, pos, neighbor);
    }

    public void onNeighborChangeDefault(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        super.onNeighborChange(world, pos, neighbor);
    }

    @Override
    public final void onEntityWalk(World world, BlockPos pos, Entity entity)
    {
        onEntityWalkDefault(world, pos, entity);
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile != null)
        {
            tile.getMicroblockContainer().getPartContainer().onEntityStanding(entity);
        }
    }

    public void onEntityWalkDefault(World worldIn, BlockPos pos, Entity entityIn)
    {
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
    public final void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
    {
        onEntityCollidedWithBlockDefault(world, pos, state, entity);
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile != null)
        {
            tile.getMicroblockContainer().getPartContainer().onEntityCollided(entity);
        }
    }

    public void onEntityCollidedWithBlockDefault(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
    }

    @Override
    public final Boolean isAABBInsideMaterial(World world, BlockPos pos, AxisAlignedBB aabb, Material material)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        Boolean def = isAABBInsideMaterialDefault(world, pos, aabb, material);
        if (tile != null)
        {
            Boolean is = tile.getMicroblockContainer().getPartContainer().isAABBInsideMaterial(aabb, material);
            if ((def != null && def == true) || (is != null && is == true))
            {
                return true;
            }
            if ((def != null && def == false) || (is != null && is == false))
            {
                return false;
            }
            return null;
        }
        return def;
    }

    public Boolean isAABBInsideMaterialDefault(World world, BlockPos pos, AxisAlignedBB boundingBox, Material materialIn)
    {
        return super.isAABBInsideMaterial(world, pos, boundingBox, materialIn);
    }

    @Override
    public final Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos pos, IBlockState state, Entity entity, double yToTest,
            Material material, boolean testingHead)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        Boolean def = isEntityInsideMaterialDefault(world, pos, state, entity, yToTest, material, testingHead);
        if (tile != null)
        {
            Boolean is = tile.getMicroblockContainer().getPartContainer().isEntityInsideMaterial(entity, yToTest, material, testingHead);
            if ((def != null && def == true) || (is != null && is == true))
            {
                return true;
            }
            if ((def != null && def == false) || (is != null && is == false))
            {
                return false;
            }
            return null;
        }
        return def;
    }

    public Boolean isEntityInsideMaterialDefault(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity,
            double yToTest, Material materialIn, boolean testingHead)
    {
        return super.isEntityInsideMaterial(world, blockpos, iblockstate, entity, yToTest, materialIn, testingHead);
    }

    @Override
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }

    @Override
    public final boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if (side == null)
        {
            return false;
        }
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile != null){ 
            MicroblockContainer container = tile.getMicroblockContainer();
            return container.getPartContainer().canConnectRedstone(side) || canConnectRedstoneDefault(state, world, pos, side, container);
        }
        return false;
    }

    public boolean canConnectRedstoneDefault(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side,
            MicroblockContainer partContainer)
    {
        return super.canConnectRedstone(state, world, pos, side);
    }

    @Override
    public final int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if (side == null)
        {
            return 0;
        }
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if(tile != null)
        {
            MicroblockContainer container = tile.getMicroblockContainer();
            return Math.max(container.getPartContainer().getWeakSignal(side), getWeakPowerDefault(state, world, pos, side, container));
        }
        return 0;
    }

    public int getWeakPowerDefault(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side, MicroblockContainer partContainer)
    {
        return super.getWeakPower(state, world, pos, side);
    }

    @Override
    public final int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if (side == null)
        {
            return 0;
        }
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if(tile != null)
        {
            MicroblockContainer container = tile.getMicroblockContainer();
            return Math.max(container.getPartContainer().getStrongSignal(side), getStrongPowerDefault(state, world, pos, side, container));
        }
        return 0;
    }

    public int getStrongPowerDefault(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side,
            MicroblockContainer partContainer)
    {
        return super.getStrongPower(state, world, pos, side);
    }

    @Override
    public final boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile != null)
        {
            return tile.getMicroblockContainer().getPartContainer().isSideSolid(side) || isSideSolidDefault(state, world, pos, side);
        }
        return false;
    }

    public boolean isSideSolidDefault(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return super.isSideSolid(state, world, pos, side);
    }

    @Override
    public final boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile != null)
        {
            return tile.getMicroblockContainer().getPartContainer().canPlaceTorchOnTop() || canPlaceTorchOnTopDefault(state, world, pos);
        }
        return false;
    }

    public boolean canPlaceTorchOnTopDefault(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return super.canPlaceTorchOnTop(state, world, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile != null)
        {
            tile.getMicroblockContainer().getPartContainer().randomDisplayTick(rand);
        }
        randomDisplayTickDefault(state, world, pos, rand);
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTickDefault(IBlockState state, World world, BlockPos pos, Random rand)
    {
        super.randomDisplayTick(state, world, pos, rand);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final boolean addDestroyEffects(World world, BlockPos pos, ParticleManager particleManager)
    {
        RayTraceResult hit = reTrace(world, pos, FMLClientHandler.instance().getClientPlayerEntity());
        if (hit != null)
        {
            if (hit.partHit.addDestroyEffects(AdvancedParticleManager.getInstance(particleManager)))
            {
                return true;
            }

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
                    return true;
                }
            }
            return true;
        }
        return addDestroyEffectsDefault(world, pos, particleManager);
    }

    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffectsDefault(World world, BlockPos pos, ParticleManager particleManager)
    {
        return super.addDestroyEffects(world, pos, particleManager);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager particleManager)
    {
        RayTraceResult hit = target.partHit != null ? (RayTraceResult) target : null;
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
                    return true;
                }
            }
            return true;
        }
        return addHitEffectsDefault(state, world, target, particleManager);
    }

    @SideOnly(Side.CLIENT)
    public boolean addHitEffectsDefault(IBlockState state, World world, RayTraceResult target, ParticleManager particleManager)
    {
        return super.addHitEffects(state, world, target, particleManager);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate,
            EntityLivingBase entity, int numberOfParticles)
    {
        return true;
    }

    private RayTraceResult reTrace(World world, BlockPos pos, EntityPlayer player)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile == null)
        {
            return null;
        }
        Vec3d start = RayTraceUtils.getStart(player);
        Vec3d end = RayTraceUtils.getEnd(player);
        RayTraceResult result = tile.getMicroblockContainer().getPartContainer().collisionRayTrace(start, end);
        return result;
    }

    private RayTraceResult reTraceBlock(World world, BlockPos pos, EntityPlayer player)
    {
        Vec3d start = RayTraceUtils.getStart(player);
        Vec3d end = RayTraceUtils.getEnd(player);
        return collisionRayTraceDefault(world.getBlockState(pos), world, pos, start, end);
    }

    private RayTraceResult reTraceAll(World world, BlockPos pos, EntityPlayer player)
    {
        Vec3d start = RayTraceUtils.getStart(player);
        RayTraceResult RayTraceResult = reTrace(world, pos, player);
        RayTraceResult blockMOP = reTraceBlock(world, pos, player);
        if (RayTraceResult == null && blockMOP == null)
        {
            return null;
        }
        if (RayTraceResult == null && blockMOP != null)
        {
            return blockMOP;
        }
        if (RayTraceResult != null && blockMOP == null)
        {
            return RayTraceResult;
        }
        if (RayTraceResult.hitVec.squareDistanceTo(start) <= blockMOP.hitVec.squareDistanceTo(start))
        {
            return RayTraceResult;
        }
        return blockMOP;
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

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        List<PartState> states;
        if (tile != null)
        {
            states = tile.getMicroblockContainer().getPartContainer().getExtendedStates(world, pos);
        }
        else
        {
            states = new ArrayList<PartState>();
        }
        return ((IExtendedBlockState) state).withProperty(BlockMultipartContainer.PROPERTY_MULTIPART_CONTAINER, states);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer.Builder(this).add(BlockMultipartContainer.PROPERTY_MULTIPART_CONTAINER).build();
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return true;
    }

    public boolean canRenderInLayerDefault(IBlockState state, BlockRenderLayer layer)
    {
        return canRenderInLayerDefault(layer);
    }

    @Deprecated
    public boolean canRenderInLayerDefault(BlockRenderLayer layer)
    {
        return super.canRenderInLayer(layer);
    }

    @Override
    public boolean canRotateBlockAround(World world, BlockPos pos, EnumFacing axis)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile != null)
        {
            for (IMultipart part : tile.getMicroblockContainer().getParts())
            {
                if (!part.canRotatePartAround(axis))
                {
                    return false;
                }
            }
        }
        return canRotateBlockAroundDefault(world, pos, axis);
    }

    public boolean canRotateBlockAroundDefault(World world, BlockPos pos, EnumFacing axis)
    {
        return super.canRotateBlockAround(world, pos, axis);
    }

    @Override
    public void rotateBlockAround(World world, BlockPos pos, EnumFacing axis)
    {
        IMicroblockContainerTile tile = getMicroblockTile(world, pos);
        if (tile != null)
        {
            for (IMultipart part : tile.getMicroblockContainer().getParts())
            {
                part.rotatePartAround(axis);
            }
        }
        rotateBlockAroundDefault(world, pos, axis);
    }

    public void rotateBlockAroundDefault(World world, BlockPos pos, EnumFacing axis)
    {
        super.rotateBlockAround(world, pos, axis);
    }

    @Override
    public boolean canPlayerRotate(World world, BlockPos pos, EnumFacing axis, EntityPlayer player, RayTraceResult hit)
    {
        if (hit != null && hit.partHit != null)
        {
            return hit.partHit.canPlayerRotate(axis, player, hit);
        }
        else
        {
            return canPlayerRotateDefault(world, pos, axis, player, hit);
        }
    }

    public boolean canPlayerRotateDefault(World world, BlockPos pos, EnumFacing axis, EntityPlayer player, RayTraceResult hit)
    {
        return super.canPlayerRotate(world, pos, axis, player, hit);
    }

    @Override
    public void rotateBlock(World world, BlockPos pos, EnumFacing axis, EntityPlayer player, RayTraceResult hit)
    {
        if (hit != null && hit.partHit != null)
        {
            hit.partHit.rotatePart(axis, player, hit);
        }
        else
        {
            rotateBlockDefault(world, pos, axis, player, hit);
        }
    }

    public void rotateBlockDefault(World world, BlockPos pos, EnumFacing axis, EntityPlayer player, RayTraceResult hit)
    {
        super.rotateBlock(world, pos, axis, player, hit);
    }

    @Override
    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color, EntityPlayer player, RayTraceResult hit)
    {
        if (hit != null && hit.partHit != null)
        {
            return hit.partHit.recolorPart(side, color, player, hit);
        }
        else
        {
            return recolorBlockDefault(world, pos, side, color, player, hit);
        }
    }

    public boolean recolorBlockDefault(World world, BlockPos pos, EnumFacing side, EnumDyeColor color, EntityPlayer player,
            RayTraceResult hit)
    {
        return super.recolorBlock(world, pos, side, color, player, hit);
    }

}
