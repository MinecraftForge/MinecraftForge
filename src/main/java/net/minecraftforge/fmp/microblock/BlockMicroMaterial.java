package net.minecraftforge.fmp.microblock;

import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * A simple implementation of {@link IMicroMaterial} that's defined based on an {@link IBlockState}.
 *
 * @see IMicroMaterial
 * @see MicroblockRegistry
 */
public class BlockMicroMaterial implements IMicroMaterial
{
    
    private static final Joiner COMMA_JOINER = Joiner.on(',');
    @SuppressWarnings("rawtypes")
    private static final Function<Entry<IProperty<?>, Comparable<?>>, String> MAP_ENTRY_TO_STRING = new Function<Entry<IProperty<?>, Comparable<?>>, String>()
    {
        @SuppressWarnings("unchecked")
        @Override
        public String apply(Entry<IProperty<?>, Comparable<?>> entry)
        {
            if (entry == null)
            {
                return "<NULL>";
            }
            else
            {
                IProperty iproperty = entry.getKey();
                return iproperty.getName() + "=" + iproperty.getName(entry.getValue());
            }
        }
    };

    private final IBlockState blockState;
    private final float hardness;
    private final ResourceLocation type;

    public BlockMicroMaterial(IBlockState blockState)
    {
        this(blockState, ((Float) ReflectionHelper.getPrivateValue(Block.class, blockState.getBlock(), "blockHardness", "field_149782_v"))
                .floatValue());
    }

    public BlockMicroMaterial(IBlockState blockState, float hardness)
    {
        this.blockState = blockState;
        this.hardness = hardness;
        this.type = genType();
    }

    private BlockMicroMaterial(BlockMicroMaterial material)
    {
        this.blockState = material.blockState;
        this.hardness = material.hardness;
        this.type = material.type;
    }

    private final ResourceLocation genType()
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(Block.REGISTRY.getNameForObject(blockState.getBlock()));
        if (!blockState.getProperties().isEmpty())
        {
            stringbuilder.append("[");
            COMMA_JOINER.appendTo(stringbuilder, Iterables.transform(blockState.getProperties().entrySet(), MAP_ENTRY_TO_STRING));
            stringbuilder.append("]");
        }
        return new ResourceLocation(stringbuilder.toString());
    }

    @Override
    public ResourceLocation getType()
    {
        return type;
    }

    @Override
    public String getLocalizedName()
    {
        return getItem().getDisplayName();
    }

    @Override
    public boolean isSolid()
    {
        return blockState.isFullCube();
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightValue()
    {
        return blockState.getLightValue();
    }

    @Override
    public float getHardness()
    {
        return hardness;
    }

    @Override
    public int getSawStrength()
    {
        return blockState.getBlock().getHarvestLevel(blockState);
    }

    @Override
    public ItemStack getItem()
    {
        return new ItemStack(blockState.getBlock(), 1, blockState.getBlock().getMetaFromState(blockState));
    }

    @Override
    public SoundType getSound()
    {
        return blockState.getBlock().getSoundType();
    }

    @Override
    public boolean canRenderInLayer(BlockRenderLayer layer)
    {
        return blockState.getBlock().canRenderInLayer(getDefaultMaterialState(), layer);
    }

    @Override
    public IBlockState getDefaultMaterialState()
    {
        return blockState;
    }

    @Override
    public IBlockState getMaterialState(IBlockAccess world, BlockPos pos, IMicroblock microblock)
    {
        return blockState;
    }

    public DelegatedBlockMicroMaterial withDelegate(Function<Tuple<IMicroblock, Boolean>, MicroblockDelegate> delegateFactory)
    {
        return new DelegatedBlockMicroMaterial(this, delegateFactory);
    }

    public static class DelegatedBlockMicroMaterial extends BlockMicroMaterial implements IDelegatedMicroMaterial
    {
        
        private final Function<Tuple<IMicroblock, Boolean>, MicroblockDelegate> delegateFactory;

        private DelegatedBlockMicroMaterial(BlockMicroMaterial material,
                Function<Tuple<IMicroblock, Boolean>, MicroblockDelegate> delegateFactory)
        {
            super(material);
            this.delegateFactory = delegateFactory;
        }

        @Override
        public MicroblockDelegate provideDelegate(IMicroblock microblock, boolean isRemote)
        {
            return delegateFactory.apply(new Tuple<IMicroblock, Boolean>(microblock, isRemote));
        }
    }

}
