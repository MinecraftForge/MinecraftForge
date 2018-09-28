package net.minecraftforge.registries;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.ITagEntry;
import net.minecraft.tags.Tag.ListEntry;
import net.minecraft.tags.Tag.TagEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.tags.ForgeTagCollection;
import net.minecraftforge.common.tags.ImmutableTag;
import net.minecraftforge.common.tags.UnmodifiableTagWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * @param <T> The Type of RegistryEntry this TagProvider is responsible for
 * @implNote All Methods except {@link #getOrCreate(ResourceLocation)} return TagWrappers (created by this TagProviders wrapper Factory).
 * This means you can safely cache all tags you request, but modification might not be possible.
 */
public class TagProvider<T extends IForgeRegistryEntry<T>> implements IResourceManagerReloadListener/*implements INBTSerializable<NBTTagCompound>*/ {  //doesn't implement NBTSerializable, as it isn't really meant to be saved to Disc
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String TAG_FOLDER = "tags/";
    public static final String FORGE_TAG_SUBFOLDER = "forge/";
    private ForgeRegistry<T> reg;
    private ForgeTagCollection<T> tags;
    private ResourceLocation regName;
    private final BiFunction<ResourceLocation, IForgeRegistry<T>, ? extends Tag<T>> wrapperFactory;

    TagProvider(@Nullable ForgeRegistry<T> reg, @Nullable BiFunction<ResourceLocation, IForgeRegistry<T>, ? extends Tag<T>> wrapperFactory, ResourceLocation regName)
    {
        this.regName = Objects.requireNonNull(regName);
        setReg(reg);
        if (wrapperFactory == null)
        {
            LOGGER.warn("No Wrapper Factory specified for this TagProvider. Using default ForgeTagWrapper implementation!");
            this.wrapperFactory = UnmodifiableTagWrapper::new;
        } else
        {
            this.wrapperFactory = wrapperFactory;
        }
    }

    TagProvider(ResourceLocation regName, BiFunction<ResourceLocation, IForgeRegistry<T>, ? extends Tag<T>> wrapperFactory)
    {
        this(null, wrapperFactory, regName);
    }

    BiFunction<ResourceLocation, IForgeRegistry<T>, ? extends Tag<T>> getWrapperFactory()
    {
        return wrapperFactory;
    }

    void setReg(ForgeRegistry<T> reg)
    {
        this.reg = reg;
        if (reg != null)
            tags = new ForgeTagCollection<>(wrapperFactory, regName.toString(), regName);
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager)
    {
        if (isVanillaHandled()) return;
        clear();
        onReload(resourceManager);
    }

    void onReload(IResourceManager manager)
    {
        tags.reload(manager);
    }

    boolean isVanillaHandled()
    {
        return reg.getRegistrySuperType().equals(Block.class) || reg.getRegistrySuperType().equals(Item.class);
    }

    void onReadVanillaPacket(PacketBuffer buf)
    {
        LOGGER.debug("Receiving {} Vanilla TagData",regName);
        tags.read(buf);
    }

    void onWriteVanillaPacket(PacketBuffer buf)
    {
        LOGGER.debug("Preparing {} Vanilla TagData",regName);
        tags.write(buf);
    }

    private ResourceLocation getRegName()
    {
        return regName;
    }

    @Nonnull
    public NBTTagCompound serializeNBT()
    { //this should never be used to try to persist tags
        LOGGER.debug("Preparing {} Forge TagData",regName);
        NBTTagCompound resCompound = new NBTTagCompound();
        NBTTagList nbtTagList = new NBTTagList();
        for (Map.Entry<ResourceLocation, Tag<T>> tag : tags.getTagMap().entrySet())
        {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("K", tag.getKey().toString());
            compound.setTag("V", serializeTagEntries(tag.getValue().getEntries()));
        }
        resCompound.setTag("data", nbtTagList);
        resCompound.setString("name", regName.toString());
        return resCompound;
    }

    private NBTTagList serializeTagEntries(Iterable<ITagEntry<T>> c)
    {
        NBTTagList entries = new NBTTagList();
        for (ITagEntry<? extends T> tagEntry : c)
        {
            NBTTagCompound entryCompound = new NBTTagCompound();
            if (tagEntry instanceof Tag.TagEntry)
            {
                entryCompound.setBoolean("tag", true);
                entryCompound.setString("V", ((TagEntry<? extends T>) tagEntry).getSerializedId().toString());
            } else if (tagEntry instanceof Tag.ListEntry)
            {
                entryCompound.setBoolean("tag", false);
                entryCompound.setTag("V", serializeTagListEntry((ListEntry<? extends T>) tagEntry));
            } else
            {
                LOGGER.error("Cannot serialize ITagEntry because it's class is unkown {}. It will not be synced!", tagEntry.getClass().getName());
            }
            entries.add(entryCompound);
        }
        return entries;
    }

    private NBTTagList serializeTagListEntry(Tag.ListEntry<? extends T> entry)
    {
        NBTTagList items = new NBTTagList();
        for (T thing : entry.getTaggedItems())
        {
            if (reg.containsValue(thing))
            {
                items.add(new NBTTagInt(reg.getID(thing)));
            } else
            {
                LOGGER.warn("Could not serialize tagged Item with name {}, because it is not registered.", thing.getRegistryName() != null ? thing.getRegistryName() : "unknown");
            }
        }
        return items;
    }

    public void deserializeNBT(@Nonnull NBTTagCompound compound)
    { //this should never be used to try to persist tags
        LOGGER.debug("Receiving {} Forge TagData",regName);
        clear();
        regName = new ResourceLocation(compound.getString("name"));
        NBTTagList list = (NBTTagList) compound.getTag("data");
        for (INBTBase nbtPair : list)
        {
            NBTTagCompound entryCompound = (NBTTagCompound) nbtPair;
            if (!entryCompound.hasKey("K") || !entryCompound.hasKey("V"))
            {
                LOGGER.warn("Tried to deserialize incomplete NBT-Data. Found K={}, V={}. Both are required.", entryCompound.hasKey("K"), entryCompound.hasKey("V"));
                continue;
            }
            //like Vanilla, we assume that only completely resolved tags are synced
            tags.register(deserializeTagEntries(entryCompound.getTag("V")).build(new ResourceLocation(entryCompound.getString("K"))));
        }
        tags.updateOwningTags();
    }

    private Tag.Builder<T> deserializeTagEntries(@Nonnull INBTBase nbt)
    {
        NBTTagList nbtTagList = (NBTTagList) nbt;
        Tag.Builder<T> builder = ImmutableTag.builder();
        for (INBTBase tagEntry : nbtTagList)
        {
            NBTTagCompound entryCompound = (NBTTagCompound) tagEntry;
            if (!entryCompound.hasKey("tag") || !entryCompound.hasKey("V"))
            {
                LOGGER.warn("Expected {} to have 'tag' and 'V' keys. Skipping, whilst deserializing {} tags.", tagEntry.getClass().getName(), getRegName());
                continue;
            }
            if (entryCompound.getBoolean("tag"))
            {
                builder.add(new ResourceLocation(entryCompound.getString("V")));
            } else
            {
                builder.addAll(deserializeTagListEntry(entryCompound));
            }
        }
        return builder;
    }

    private List<T> deserializeTagListEntry(NBTTagCompound entryCompound)
    {
        INBTBase valTag = entryCompound.getTag("V");
        NBTTagList list = (NBTTagList) valTag;
        List<T> res = new ArrayList<>(list.size());
        for (INBTBase nbt : list)
        {
            assert nbt instanceof NBTTagInt;
            NBTTagInt tagInt = (NBTTagInt) nbt;
            res.add(reg.getValue(tagInt.getId()));
        }
        return res;
    }

    void clear()
    {
        tags.clear();
    }

    @Nonnull
    NetworkTagCollection<T> asTagCollection()
    {
        return tags;
    }

    /**
     * @param id The Tag id for which to retrieve Tags for
     * @return All Tags on the object represented by id, sorted by their ID'S
     */
    public SortedSet<Tag<T>> getOwningTags(ResourceLocation id)
    {
        return tags.getOwningTagObjects(id);
    }

    /**
     * @param thing The thing for which to retrieve Tags for
     * @return All Tags on thing, sorted by their ID'S
     */
    public SortedSet<Tag<T>> getOwningTags(T thing)
    {
        if (thing.getRegistryName() == null) return Collections.emptySortedSet();
        return getOwningTags(thing.getRegistryName());
    }

    /**
     * @param predicate Predicate to test
     * @return All Tags matching the given Predicate in undefined ordering
     */
    public List<Tag<T>> getMatchingTags(Predicate<Tag<T>> predicate)
    {
        return tags.getMatchingTags(predicate);
    }

    /**
     * @param predicate Predicate to test
     * @return Whether there is any Tag which matches the given Predicate or not
     */
    public boolean matchesTag(Predicate<Tag<T>> predicate)
    {
        return tags.tagMatch(predicate);
    }

    /**
     * @param id        The item to test
     * @param predicate the Predicate to use
     * @return All tags on the item matching the Predicate, sorted by their ID'S
     */
    public SortedSet<Tag<T>> getMatchingTagsOnItem(ResourceLocation id, Predicate<Tag<T>> predicate)
    {
        return tags.getMatchingTagsForItem(id, predicate);
    }

    /**
     * @param id        The item to test
     * @param predicate the Predicate to use
     * @return whether any tag on the Item represented by id matches the Predicate or not
     */
    public boolean matchesTagOnItem(ResourceLocation id, Predicate<Tag<T>> predicate)
    {
        return tags.tagMatchForItem(id, predicate);
    }

    /**
     * @param id The id of an RegistryEntry for which to request TagID's for
     * @return The id's of all Tags which contain this RegistryEntry, sorted by their ID'S
     * @implNote because the Tags have to be mapped to ResourceLocations first, slightly less performant than {@link #getOwningTags(ResourceLocation)} (more of a vanilla compat...)
     */
    public SortedSet<ResourceLocation> getOwningTagIDs(ResourceLocation id)
    {
        return tags.getOwningTags(id);
    }

    /**
     * @param thing An RegistryEntry for which to request TagID's for
     * @return The id's of all Tags which contain this RegistryEntry, sorted by their ID'S
     * @implNote because the Tags have to be mapped to ResourceLocations first, slightly less performant than {@link #getOwningTags(IForgeRegistryEntry)} (more of a vanilla compat...)
     */
    public SortedSet<ResourceLocation> getOwningTagIDs(T thing)
    {
        return tags.getOwningTags(thing);
    }

    /**
     * @param location The location for which to retrieve the Tag
     * @return the underlying tag
     * @implNote Notice, that this is the only method which does not return a Wrapper Tag, which could safely be cached!
     */
    @Nonnull
    public Tag<T> getOrCreate(ResourceLocation location)
    {
        return tags.getOrCreate(location);
    }

    /**
     * @param location The tag to retrieve
     * @return A lazily initialized TagWrapper
     */
    public Tag<T> createWrapper(ResourceLocation location)
    {
        return wrapperFactory.apply(location, reg);
    }

}
