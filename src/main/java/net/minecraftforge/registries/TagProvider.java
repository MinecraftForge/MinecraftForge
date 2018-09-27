package net.minecraftforge.registries;

import com.google.common.collect.*;
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
import net.minecraftforge.common.tags.Tags.Blocks;
import net.minecraftforge.common.tags.Tags.Items;
import net.minecraftforge.common.tags.UnmodifiableTagWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class TagProvider<T extends IForgeRegistryEntry<T>> implements IResourceManagerReloadListener/*implements INBTSerializable<NBTTagCompound>*/ {  //doesn't implement NBTSerializable, as it isn't really meant to be saved to Disc
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String TAG_FOLDER = "tags/";
    public static final String FORGE_TAG_SUBFOLDER = "forge/";
    private ForgeRegistry<T> reg;
    private ForgeTagCollection<T> tags;
    private ResourceLocation regName;
    private BiFunction<ResourceLocation, IForgeRegistry<T>, ? extends Tag<T>> wrapperFactory;

    TagProvider(@Nullable ForgeRegistry<T> reg, @Nullable BiFunction<ResourceLocation, IForgeRegistry<T>, ? extends Tag<T>> wrapperFactory, ResourceLocation regName)
    {
        this.regName = Objects.requireNonNull(regName);
        setReg(reg);
        this.wrapperFactory = wrapperFactory;
        if (this.wrapperFactory == null)
        {
            LOGGER.warn("No Wrapper Factory specified for this TagProvider. Using default ForgeTagWrapper implementation!");
            this.wrapperFactory = UnmodifiableTagWrapper::new;
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
            tags = new ForgeTagCollection<>(reg::containsKey, reg::getValue, regName.toString(), regName);
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
        return reg.getRegistrySuperType().equals(Blocks.class) || reg.getRegistrySuperType().equals(Items.class);
    }

    void onReadVanillaPacket(PacketBuffer buf)
    {
        clear();
        int i = buf.readVarInt();

        for (int j = 0; j < i; ++j)
        {
            ResourceLocation resourcelocation = buf.readResourceLocation();
            int k = buf.readVarInt();
            List<T> list = Lists.newArrayList();

            for (int l = 0; l < k; ++l)
            {
                list.add(this.reg.getValue(buf.readVarInt()));
            }

            tags.register(Tag.Builder.<T>create().addAll(list).build(resourcelocation));
        }
    }

    void onWriteVanillaPacket(PacketBuffer buf)
    {
        buf.writeVarInt(tags.getTagMap().size());

        for (Entry<ResourceLocation, Tag<T>> entry : this.tags.getTagMap().entrySet())
        {
            buf.writeResourceLocation(entry.getKey());
            buf.writeVarInt(((Tag) entry.getValue()).getAllElements().size());

            for (T t : (entry.getValue()).getAllElements())
            {
                buf.writeVarInt(this.reg.getID(t));
            }
        }
    }

    private ResourceLocation getRegName()
    {
        return regName;
    }

    @Nonnull
    public NBTTagCompound serializeNBT()
    { //this should never be used to try to persist tags
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
        clear();
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
        regName = new ResourceLocation(compound.getString("name"));
    }

    private Tag.Builder<T> deserializeTagEntries(@Nonnull INBTBase nbt)
    {
        NBTTagList nbtTagList = (NBTTagList) nbt;
        Tag.Builder<T> builder = Tag.Builder.create();
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
     * @param id The Tag id to retrieve
     * @return
     */
    public List<Tag<T>> getOwningTags(ResourceLocation id)
    {
        return getOwningTags(reg.getValue(id));
    }

    public List<Tag<T>> getOwningTags(T thing)
    {
        return tags.getOwningTagObjects(thing);
    }

    public List<Tag<T>> getMatchingTags(Predicate<Tag<T>> predicate)
    {
        return tags.getMatchingTags(predicate);
    }

    public boolean matchesTag(Predicate<Tag<T>> predicate)
    {
        return tags.tagMatch(predicate);
    }

    public Collection<ResourceLocation> getOwningTagIDs(ResourceLocation id)
    {
        return getOwningTagIDs(reg.getValue(id));
    }

    public Collection<ResourceLocation> getOwningTagIDs(T thing)
    {
        return tags.getOwningTags(thing);
    }

    @Nonnull
    public Tag<T> getOrCreate(ResourceLocation location)
    {
        return tags.getOrCreate(location);
    }

    public Tag<T> createWrapper(ResourceLocation location)
    {
        return wrapperFactory.apply(location, reg);
    }

}
