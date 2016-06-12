package net.minecraftforge.fmp.microblock;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Interface that represents an {@link IMicroblock} material. For advanced per-microblock functionality, you'll want to
 * implement {@link IDelegatedMicroMaterial}.<br/>
 * For a default implementation of most of these methods and some helpers, you can use {@link BlockMicroMaterial}
 * directly.
 *
 * @see IMicroblock
 * @see BlockMicroMaterial
 */
public interface IMicroMaterial
{
    
    /**
     * Gets a unique identifier for this micro material. Used for saving/loading.
     */
    public ResourceLocation getType();

    /**
     * Gets the localized name of this micro material.
     */
    public String getLocalizedName();

    /**
     * Checks whether or not this material is solid.
     */
    public boolean isSolid();

    /**
     * Gets the amount of light emitted by a microblock made of this material.
     */
    public int getLightValue();

    /**
     * Gets the hardness of a microblock made of this material.
     */
    public float getHardness();

    /**
     * Gets the saw strength required to cut this material.
     */
    public int getSawStrength();

    /**
     * Gets the item that will be cut to make a microblock out of this material.
     */
    public ItemStack getItem();

    /**
     * Gets the placement sound of this material.
     */
    public SoundType getSound();

    /**
     * Checks whether or not this material can be rendered in the specified layer.
     */
    public boolean canRenderInLayer(BlockRenderLayer layer, IBlockState state);

    /**
     * Gets the "default" of this material. Used for item rendering.
     */
    public IBlockState getDefaultMaterialState();

    /**
     * Gets the in-world state of this material at the specified position.
     */
    public IBlockState getMaterialState(IBlockAccess world, BlockPos pos, IMicroblock microblock);

    /**
     * Interface that represents an {@link IMicroblock} material with method delegation. If you don't need this kind of
     * advanced functionality, implement {@link IMicroMaterial} instead.
     *
     * @see IMicroblock
     * @see IMicroMaterial
     * @see MicroblockDelegate
     */
    public static interface IDelegatedMicroMaterial extends IMicroMaterial
    {
        
        /**
         * Gets a delegate for the specified microblock. This delegate allows you to add special interaction with
         * microblocks made out of this micro material to handle things like clicking, redstone I/O, etc.
         *
         * @see MicroblockDelegate
         */
        public MicroblockDelegate provideDelegate(IMicroblock microblock, boolean isRemote);

    }

}
