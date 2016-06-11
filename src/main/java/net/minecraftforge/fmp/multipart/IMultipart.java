package net.minecraftforge.fmp.multipart;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.util.RayTraceUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fmp.client.multipart.AdvancedParticleManager;
import net.minecraftforge.fmp.client.multipart.FastMSR;
import net.minecraftforge.fmp.client.multipart.MultipartSpecialRenderer;
import net.minecraftforge.fmp.client.multipart.MultipartStateMapper;
import net.minecraftforge.fmp.multipart.IPartFactory.IAdvancedPartFactory;

/**
 * Interface that represents a multipart. This has all the methods required for a multipart to work properly.<br/>
 * For a default implementation of most of these methods and some helpers, you can extend {@link Multipart} directly.
 *
 * @see Multipart
 */
public interface IMultipart extends ICapabilityProvider
{
    
    /**
     * Gets the world this part is in.
     */
    public World getWorld();

    /**
     * Gets the location of this part in the world.
     */
    public BlockPos getPos();

    /**
     * Gets the {@link IMultipartContainer} that contains this part.
     *
     * @see IMultipartContainer
     */
    public IMultipartContainer getContainer();

    /**
     * Sets the {@link IMultipartContainer} that contains this part.
     *
     * @see IMultipartContainer
     */
    public void setContainer(IMultipartContainer container);

    /**
     * Returns the identifier for this type of part. This will be passed into the {@link IPartFactory} or
     * {@link IAdvancedPartFactory} to create the part in the client, or when loaded from NBT.
     */
    public ResourceLocation getType();

    /**
     * Gets the path to the model used by this part. If the part requires a more advanced model loading system, you'll
     * need to register your own {@link MultipartStateMapper}.
     *
     * @see ResourceLocation
     * @see MultipartStateMapper
     */
    public ResourceLocation getModelPath();

    /**
     * Gets the path to the model used by this part depending on the blockstate. <b>For models whose paths don't match
     * the registry ID, you must override {@link IMultipart#getModelPath()} as well so it gets loaded.</b>
     *
     * @see ResourceLocation
     * @see ModelResourceLocation
     * @see IMultipart#getModelPath()
     */
    public ModelResourceLocation getModelPath(IBlockState state);

    /**
     * Ray traces through the part's collision from start vector to end vector returning a ray trace hit.
     *
     * @return The closest hit to the start vector, if any.
     * @see RayTraceUtils
     */
    public RayTraceResult collisionRayTrace(Vec3d start, Vec3d end);

    /**
     * Adds this part's collision boxes to the list for the specified entity if they intersect the mask.
     *
     * @param mask
     *            A mask that represents the bounds of the block. All boxes added to the list must intersect this mask.
     * @param list
     *            The list of collision boxes.
     * @param collidingEntity
     *            The entity that's colliding with this part. May be null.
     */
    public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity);

    /**
     * Performs an occlusion test against the specified part. Returning true means that they aren't occluding each
     * other, returning false means otherwise.
     *
     * @see OcclusionHelper
     */
    public boolean occlusionTest(IMultipart part);

    /**
     * Gets the amount of light emitted by this part (from 0 to 15).
     */
    public int getLightValue();

    /**
     * Gets the block that will be obtained by creative players when they middle-click this part.
     */
    public ItemStack getPickPart(EntityPlayer player, RayTraceResult hit);

    /**
     * Gets the items dropped by this part when broken.
     * @see IMultipart#harvest(EntityPlayer, RayTraceResult)
     */
    public List<ItemStack> getDrops();

    /**
     * Harvests this part, removing it from the container it's in.
     *
     * @see IMultipart#getDrops()
     */
    public void harvest(EntityPlayer player, RayTraceResult hit);

    /**
     * Gets the strength of a player against this part. Not to be confused with {@link Multipart#getHardness(RayTraceResult)}.
     * This also takes potion effects and tools into account.
     */
    public float getStrength(EntityPlayer player, RayTraceResult hit);

    /**
     * Called when a part in the same block block changes.
     */
    public void onPartChanged(IMultipart part);

    /**
     * Called when a neighbor block changes.
     */
    public void onNeighborBlockChange(Block block);

    /**
     * Called when a neighbor tile changes.
     */
    public void onNeighborTileChange(EnumFacing facing);

    /**
     * Called when this part is added to the container.
     */
    public void onAdded();

    /**
     * Called right before this part is removed from the container.
     */
    public void onRemoved();

    /**
     * Called when the chunk this part is on is loaded.
     */
    public void onLoaded();

    /**
     * Called when the chunk this part is on is unloaded.
     */
    public void onUnloaded();

    /**
     * Called when the block equivalent of this part has been converted.
     */
    public void onConverted(TileEntity tile);
    
    /**
     * Checks if this part can be rotated around the specified axis.
     */
    public boolean canRotatePartAround(EnumFacing axis);
    
    /**
     * Rotates this part around the specified axis.
     */
    public void rotatePartAround(EnumFacing axis);

    /**
     * Checks if this part can be rotated by a player around the specified axis.
     */
    public boolean canPlayerRotate(EnumFacing axis, EntityPlayer player, RayTraceResult hit);

    /**
     * Rotates this part around the specified axis.
     */
    public void rotatePart(EnumFacing axis, EntityPlayer player, RayTraceResult hit);

    /**
     * Recolors this part.
     */
    public boolean recolorPart(EnumFacing side, EnumDyeColor color, EntityPlayer player, RayTraceResult hit);

    /**
     * Called when a player right-clicks this part. Return true to play the right-click animation.
     */
    public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, RayTraceResult hit);

    /**
     * Called when a player left-clicks this part.
     */
    public void onClicked(EntityPlayer player, RayTraceResult hit);

    /**
     * Writes this part's NBT data to a tag so it can be saved.
     */
    public NBTTagCompound writeToNBT(NBTTagCompound tag);

    /**
     * Loads this part's data from the saved NBT tag.
     */
    public void readFromNBT(NBTTagCompound tag);

    /**
     * Writes this part's data to a packet buffer for it to be sent to the client. This is called when the client gets
     * close enough to the container that it gets synced and also and when {@link IMultipart#sendUpdatePacket()} is
     * called.
     */
    public void writeUpdatePacket(PacketBuffer buf);

    /**
     * Reads this part's data from a packet sent form the server.
     */
    public void readUpdatePacket(PacketBuffer buf);

    /**
     * Writes this part's data to a packet and sends it to the client.
     */
    public void sendUpdatePacket();

    /**
     * Checks whether or not this part can be rendered in the specified world layer.
     */
    public boolean canRenderInLayer(BlockRenderLayer layer);

    /**
     * Creates a {@link BlockState} for this part with the required properties.
     *
     * @see BlockStateContainer
     * @see ExtendedBlockState
     * @see IMultipart#getActualState(IBlockState)
     * @see IMultipart#getExtendedState(IBlockState)
     */
    public BlockStateContainer createBlockState();

    /**
     * Gets the actual state of this part. <b>ONLY USED FOR RENDERING, THIS IS NOT WHERE YOU STORE DATA.</b>
     *
     * @see IMultipart#createBlockState()
     * @see IMultipart#getExtendedState(IBlockState)
     */
    public IBlockState getActualState(IBlockState state);

    /**
     * Gets the extended state of this part. <b>ONLY USED FOR RENDERING, THIS IS NOT WHERE YOU STORE DATA.</b>
     *
     * @see IMultipart#createBlockState()
     * @see IMultipart#getActualState(IBlockState)
     */
    public IBlockState getExtendedState(IBlockState state);

    /**
     * Gets the bounding box used to render this part dynamically. By default, all multipart containers have a bounding
     * box that goes from (0, 0, 0) to (1, 1, 1) so that breaking animations can be rendered, but this can be expanded
     * here.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox();

    /**
     * Called randomly every few client ticks to do things like spawning particles.
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random rand);

    /**
     * Adds the particle effects in the event of this part being broken.
     */
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(AdvancedParticleManager particleManager);

    /**
     * Adds the particle effects while this part is hit.
     */
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(RayTraceResult hit, AdvancedParticleManager particleManager);

    /**
     * Called when an entity is standing on this block.
     *
     * @see IMultipart#onEntityCollided(Entity)
     */
    public void onEntityStanding(Entity entity);

    /**
     * Called when an entity is standing in this block.
     *
     * @see IMultipart#onEntityStanding(Entity)
     */
    public void onEntityCollided(Entity entity);

    /**
     * Checks if the specified bounding box contains the material.
     *
     * @see IMultipart#isEntityInsideMaterial(Entity, double, Material, boolean)
     */
    public Boolean isAABBInsideMaterial(AxisAlignedBB aabb, Material material);

    /**
     * Checks if an entity is inside the specified material.
     *
     * @see IMultipart#isAABBInsideMaterial(AxisAlignedBB, Material)
     */
    public Boolean isEntityInsideMaterial(Entity entity, double yToTest, Material material, boolean testingHead);
    
    /**
     * Draws this part's highlight.
     */
    @SideOnly(Side.CLIENT)
    public boolean drawHighlight(RayTraceResult hit, EntityPlayer player, float partialTicks);
    
    /**
     * Checks whether or not this part currently needs a {@link FastMSR} or it should just use a
     * {@link MultipartSpecialRenderer}.
     */
    public boolean hasFastRenderer();
    
    /**
     * Gets the tint provider for this multipart.
     */
    public IBlockColor getTint();

}
