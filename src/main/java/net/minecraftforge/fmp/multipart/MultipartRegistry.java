package net.minecraftforge.fmp.multipart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fmp.ForgeMultipartModContainer;
import net.minecraftforge.fmp.multipart.IPartFactory.IAdvancedPartFactory;

/**
 * The multipart registry. This is where you need to register your multiparts as well as your block->multipart and
 * multipart->block converters.
 *
 * @see IMultipart
 * @see IPartFactory
 * @see IAdvancedPartFactory
 * @see IPartConverter
 * @see IReversePartConverter
 */
public class MultipartRegistry
{
    
    private static Map<BlockStateContainer, ResourceLocation> stateLocations = new HashMap<BlockStateContainer, ResourceLocation>();
    private static Map<ResourceLocation, BlockStateContainer> defaultStates = new HashMap<ResourceLocation, BlockStateContainer>();
    
    private static BiMap<ResourceLocation, Class<? extends IMultipart>> partClasses = HashBiMap.create();
    
    private static Map<Block, IPartConverter> converters = new HashMap<Block, IPartConverter>();
    private static List<IReversePartConverter> reverseConverters = new ArrayList<IReversePartConverter>();
    
    /**
     * Links a set of parts to an {@link IPartFactory} that can produce them.
     */
    public static void registerPartFactory(IPartFactory factory, ResourceLocation... names)
    {
        registerPartFactory(factory == null ? null : new AdvancedPartFactory(factory), names);
    }
    
    /**
     * Links a set of parts to an {@link IAdvancedPartFactory} that can produce them.
     */
    public static void registerPartFactory(IAdvancedPartFactory factory, ResourceLocation... names)
    {
        Preconditions.checkNotNull(factory, "Attempted to register a null multipart factory!");
        Preconditions.checkArgument(names.length != 0, "Attempted to register a multipart factory without any provided parts!");
        
        for (ResourceLocation name : names)
        {
            ForgeRegistries.MULTIPARTS.register(new MultipartRegistryEntry(factory).setRegistryName(name));
            IMultipart multipart = factory.createPart(name, new NBTTagCompound());
            BlockStateContainer state = multipart.createBlockState();
            defaultStates.put(name, state);
            stateLocations.put(state, multipart.getModelPath());
            ForgeMultipartModContainer.registerMultipartBlock();
        }
    }
    
    /**
     * Registers a part along with an identifier. A default part factory is automatically created.
     */
    public static void registerPart(Class<? extends IMultipart> clazz, ResourceLocation name)
    {
        Preconditions.checkNotNull(clazz, "Attempted to register a null multipart class!");
        Preconditions.checkNotNull(name, "Attempted to register a multipart with a null identifier!");
        Preconditions.checkArgument(!partClasses.containsKey(name),
                "Attempted to register a multipart with an identifier that's already in use!");
        Preconditions.checkArgument(!partClasses.containsValue(clazz),
                "Attempted to register a multipart with a class that's already in use!");
        
        partClasses.put(name, clazz);
        registerPartFactory(new SimplePartFactory(clazz), name);
    }
    
    /**
     * Registers an {@link IPartConverter}.
     */
    public static void registerPartConverter(IPartConverter converter)
    {
        for (Block block : converter.getConvertableBlocks())
        {
            converters.put(block, converter);
        }
    }
    
    /**
     * Registers an {@link IReversePartConverter}.
     */
    public static void registerReversePartConverter(IReversePartConverter converter)
    {
        reverseConverters.add(converter);
    }
    
    /**
     * Gets the type of a multipart.<br/>
     * Only for internal use. This will not return the type of custom multiparts!
     */
    public static ResourceLocation getPartType(IMultipart part)
    {
        return partClasses.inverse().get(part.getClass());
    }
    
    /**
     * Gets the {@link BlockState} that represents a specific part.
     */
    public static BlockStateContainer getDefaultState(IMultipart part)
    {
        return defaultStates.get(part.getType());
    }
    
    /**
     * Gets the {@link BlockStateContainer} that represents a specific part type.
     */
    public static BlockStateContainer getDefaultState(ResourceLocation partType)
    {
        return defaultStates.get(partType);
    }
    
    /**
     * Gets the location of a part's {@link BlockStateContainer}.
     */
    public static ResourceLocation getStateLocation(BlockStateContainer state)
    {
        return stateLocations.get(state);
    }
    
    /**
     * Creates a new part from NBT.
     */
    public static IMultipart createPart(ResourceLocation partType, NBTTagCompound tag)
    {
        IAdvancedPartFactory factory = ForgeRegistries.MULTIPARTS.getValue(partType);
        return factory == null ? null : factory.createPart(partType, tag);
    }
    
    /**
     * Creates a new part from an update packet.
     */
    public static IMultipart createPart(ResourceLocation partType, PacketBuffer buf)
    {
        IAdvancedPartFactory factory = ForgeRegistries.MULTIPARTS.getValue(partType);
        return factory == null ? null : factory.createPart(partType, buf);
    }
    
    /**
     * Gets the set of registered part types.
     */
    public static Set<ResourceLocation> getRegisteredParts()
    {
        return ForgeRegistries.MULTIPARTS.getKeys();
    }
    
    /**
     * Checks whether or not any parts have been registered.
     */
    public static boolean hasRegisteredParts()
    {
        return !getRegisteredParts().isEmpty();
    }
    
    /**
     * Converts the block at the specified location into a collection of multiparts. Doesn't actually replace the block.
     */
    public static Collection<? extends IMultipart> convert(IBlockAccess world, BlockPos pos, boolean simulated)
    {
        IPartConverter converter = converters.get(world.getBlockState(pos).getBlock());
        if (converter != null)
        {
            return converter.convertBlock(world, pos, simulated);
        }
        return null;
    }
    
    /**
     * Converts a multipart container back into a block. Actually replaces the block.
     */
    public static boolean convertToBlock(IMultipartContainer container)
    {
        for (IReversePartConverter converter : reverseConverters)
        {
            if (converter.convertToBlock(container))
            {
                return true;
            }
        }
        return false;
    }
    
    private static class SimplePartFactory implements IPartFactory
    {
        private final Class<? extends IMultipart> partClass;
        
        public SimplePartFactory(Class<? extends IMultipart> partClass)
        {
            this.partClass = partClass;
        }
        
        @Override
        public IMultipart createPart(ResourceLocation type, boolean client)
        {
            try
            {
                return partClass.newInstance();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    private static class AdvancedPartFactory implements IAdvancedPartFactory
    {
        
        private final IPartFactory simpleFactory;
        
        public AdvancedPartFactory(IPartFactory simpleFactory)
        {
            this.simpleFactory = simpleFactory;
        }
        
        @Override
        public IMultipart createPart(ResourceLocation type, PacketBuffer buf)
        {
            try
            {
                IMultipart part = simpleFactory.createPart(type, true);
                part.readUpdatePacket(buf);
                return part;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public IMultipart createPart(ResourceLocation type, NBTTagCompound tag)
        {
            try
            {
                IMultipart part = simpleFactory.createPart(type, false);
                part.readFromNBT(tag);
                return part;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    public static final class MultipartRegistryEntry implements IForgeRegistryEntry<MultipartRegistryEntry>, IAdvancedPartFactory
    {
        private ResourceLocation name;
        private final IAdvancedPartFactory factory;
        
        public MultipartRegistryEntry(IAdvancedPartFactory factory)
        {
            this.factory = factory;
        }
        
        @Override
        public IMultipart createPart(ResourceLocation type, NBTTagCompound tag)
        {
            return factory.createPart(type, tag);
        }
        
        @Override
        public IMultipart createPart(ResourceLocation type, PacketBuffer buf)
        {
            return factory.createPart(type, buf);
        }
        
        @Override
        public MultipartRegistryEntry setRegistryName(ResourceLocation name)
        {
            this.name = name;
            return this;
        }
        
        @Override
        public ResourceLocation getRegistryName()
        {
            return name;
        }
        
        @Override
        public Class<? super MultipartRegistryEntry> getRegistryType()
        {
            return MultipartRegistryEntry.class;
        }
        
    }
    
}
