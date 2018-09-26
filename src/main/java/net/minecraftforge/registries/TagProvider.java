package net.minecraftforge.registries;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Builder;
import net.minecraft.tags.Tag.ITagEntry;
import net.minecraft.tags.Tag.ListEntry;
import net.minecraft.tags.Tag.TagEntry;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.tags.Tags.Blocks;
import net.minecraftforge.common.tags.Tags.Items;
import net.minecraftforge.common.tags.UnmodifiableTagWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagProvider<T extends IForgeRegistryEntry<T>> implements IResourceManagerReloadListener/*implements INBTSerializable<NBTTagCompound>*/ {  //doesn't implement NBTSerializable, as it isn't really meant to be saved to Disc
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String TAG_FOLDER = "tags/";
    public static final String FORGE_TAG_SUBFOLDER = "forge/";
    private ForgeRegistry<T> reg;
    private ForgeTagCollection<T> tags;
    private ResourceLocation regName;
    private BiFunction<ResourceLocation,IForgeRegistry<T>,? extends Tag<T>> wrapperFactory;
    TagProvider(@Nullable ForgeRegistry<T> reg, @Nullable BiFunction<ResourceLocation,IForgeRegistry<T>,? extends Tag<T>> wrapperFactory, ResourceLocation regName)
    {
        this.regName = Objects.requireNonNull(regName);
        setReg(reg);
        this.wrapperFactory = wrapperFactory;
        if (this.wrapperFactory == null) {
            LOGGER.warn("No Wrapper Factory specified for this TagProvider. Using default ForgeTagWrapper implementation!");
            this.wrapperFactory = UnmodifiableTagWrapper::new;
        }
    }

    TagProvider(ResourceLocation regName, BiFunction<ResourceLocation,IForgeRegistry<T>,? extends Tag<T>> wrapperFactory)
    {
        this(null, wrapperFactory,regName);
    }

    BiFunction<ResourceLocation, IForgeRegistry<T>, ? extends Tag<T>> getWrapperFactory() {
        return wrapperFactory;
    }

    void setReg(ForgeRegistry<T> reg)
    {
        this.reg = reg;
        if (reg!=null)
            tags = new ForgeTagCollection<>(reg::containsKey,reg::getValue,regName.toString(),regName);
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

    boolean isVanillaHandled() {
        return reg.getRegistrySuperType().equals(Blocks.class) || reg.getRegistrySuperType().equals(Items.class);
    }

    void onReadVanillaPacket(PacketBuffer buf)
    {
        clear();
        int i = buf.readVarInt();

        for(int j = 0; j < i; ++j) {
            ResourceLocation resourcelocation = buf.readResourceLocation();
            int k = buf.readVarInt();
            List<T> list = Lists.newArrayList();

            for(int l = 0; l < k; ++l) {
                list.add(this.reg.getValue(buf.readVarInt()));
            }

            tags.register(Tag.Builder.<T>create().addAll(list).build(resourcelocation));
        }
    }

    void onWriteVanillaPacket(PacketBuffer buf)
    {
        buf.writeVarInt(tags.getTagMap().size());

        for(Entry<ResourceLocation, Tag<T>> entry : this.tags.getTagMap().entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeVarInt(((Tag)entry.getValue()).getAllElements().size());

            for(T t : (entry.getValue()).getAllElements()) {
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
        for (Map.Entry<ResourceLocation,Tag<T>> tag: tags.getTagMap().entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("K",tag.getKey().toString());
            compound.setTag("V",serializeTagEntries(tag.getValue().getEntries()));
        }
        resCompound.setTag("data",nbtTagList);
        resCompound.setString("name",regName.toString());
        return resCompound;
    }

    private NBTTagList serializeTagEntries(Iterable<ITagEntry<T>> c)
    {
        NBTTagList entries = new NBTTagList();
        for (ITagEntry<? extends T> tagEntry : c) {
            NBTTagCompound entryCompound = new NBTTagCompound();
            if (tagEntry instanceof Tag.TagEntry) {
                entryCompound.setBoolean("tag", true);
                entryCompound.setString("V", ((TagEntry<? extends T>) tagEntry).getSerializedId().toString());
            } else if (tagEntry instanceof Tag.ListEntry) {
                entryCompound.setBoolean("tag", false);
                entryCompound.setTag("V", serializeTagListEntry((ListEntry<? extends T>) tagEntry));
            } else {
                LOGGER.error("Cannot serialize ITagEntry because it's class is unkown {}. It will not be synced!", tagEntry.getClass().getName());
            }
            entries.add(entryCompound);
        }
        return entries;
    }

    private NBTTagList serializeTagListEntry(Tag.ListEntry<? extends T> entry)
    {
        NBTTagList items = new NBTTagList();
        for (T thing: entry.getTaggedItems()) {
            if (reg.containsValue(thing)) {
                items.add(new NBTTagInt(reg.getID(thing)));
            }
            else {
                LOGGER.warn("Could not serialize tagged Item with name {}, because it is not registered.",thing.getRegistryName()!=null?thing.getRegistryName():"unknown");
            }
        }
        return items;
    }

    public void deserializeNBT(@Nonnull NBTTagCompound compound)
    { //this should never be used to try to persist tags
        clear();
        NBTTagList list = (NBTTagList) compound.getTag("data");
        for (INBTBase nbtPair: list) {
            assert nbtPair instanceof NBTTagCompound;
            NBTTagCompound entryCompound = (NBTTagCompound) nbtPair;
            if (!entryCompound.hasKey("K") || !entryCompound.hasKey("V")) {
                LOGGER.warn("Tried to deserialize incomplete NBT-Data. Found K={}, V={}. Both are required.",entryCompound.hasKey("K"),entryCompound.hasKey("V"));
                continue;
            }
            //like Vanilla, we assume that only completely resolved tags are synced
            tags.register(deserializeTagEntries(entryCompound.getTag("V")).build(new ResourceLocation(entryCompound.getString("K"))));
        }
        regName = new ResourceLocation(compound.getString("name"));
    }

    private Tag.Builder<T> deserializeTagEntries(@Nonnull INBTBase nbt) {
        assert nbt instanceof NBTTagList;
        NBTTagList nbtTagList = (NBTTagList) nbt;
        Tag.Builder<T> builder = Tag.Builder.create();
        for (INBTBase tagEntry: nbtTagList) {
            assert tagEntry instanceof NBTTagCompound;
            NBTTagCompound entryCompound = (NBTTagCompound) tagEntry;
            if (!entryCompound.hasKey("tag") || !entryCompound.hasKey("V")) {
                LOGGER.warn("Expected {} to have 'tag' and 'V' keys. Skipping, whilst deserializing {} tags.",tagEntry.getClass().getName(),getRegName());
                continue;
            }
            if (entryCompound.getBoolean("tag")) {
                builder.add(new ResourceLocation(entryCompound.getString("V")));
            }
            else {
                builder.addAll(deserializeTagListEntry(entryCompound));
            }
        }
        return builder;
    }

    private List<T> deserializeTagListEntry(NBTTagCompound entryCompound) {
        INBTBase valTag = entryCompound.getTag("V");
        assert valTag instanceof NBTTagList;
        NBTTagList list = (NBTTagList) valTag;
        List<T> res = new ArrayList<>(list.size());
        for (INBTBase nbt: list) {
            assert nbt instanceof NBTTagInt;
            NBTTagInt tagInt = (NBTTagInt)nbt;
            res.add(reg.getValue(tagInt.getId()));
        }
        return res;
    }

    void clear() {
        tags.clear();
    }

    @Nonnull
    NetworkTagCollection<T> asTagCollection() {
        return tags;
    }

    /**
     *
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
    public Tag<T> getOrCreate(ResourceLocation location) {
        return tags.getOrCreate(location);
    }

    public Tag<T> createWrapper(ResourceLocation location) {
        return wrapperFactory.apply(location,reg);
    }

    private static class ForgeTagCollection<T extends IForgeRegistryEntry<T>> extends NetworkTagCollection<T> {
        private static final Logger LOGGER = LogManager.getLogger();
        private static final Gson GSON = new Gson();
        private static final int JSON_EXTENSION_LENGTH = ".json".length();

        private final ResourceLocation regName;
        private ForgeTagCollection(Predicate<ResourceLocation> isValueKnownPredicateIn, Function<ResourceLocation, T> resourceLocationToItemIn, String resourceLocationPrefixIn, boolean preserveOrderIn, ResourceLocation registryId)
        {
            super(isValueKnownPredicateIn, resourceLocationToItemIn, resourceLocationPrefixIn, preserveOrderIn, registryId.toString());
            regName = registryId;
        }

        private ForgeTagCollection(Predicate<ResourceLocation> isValueKnownPredicateIn, Function<ResourceLocation, T> resourceLocationToItemIn, String resourceLocationPrefixIn, ResourceLocation registryId)
        {
            this(isValueKnownPredicateIn, resourceLocationToItemIn, resourceLocationPrefixIn, false, registryId);
        }

        @Override
        public void reload(IResourceManager resourceManager)
        {
            Map<ResourceLocation, Builder<T>> map = deserializeTags(getLoadingParameters(resourceManager),resourceManager);
            registerUnbuildTags(map);
        }

        @Override
        public void write(PacketBuffer buf)
        {
            throw new AssertionError("Network write access to the ForgeTagCollection should be routed through the TagProvider! Failed to write to PacketBuffer!");
        }

        @Override
        public void read(PacketBuffer buf)
        {
            throw new AssertionError("Network write access to the ForgeTagCollection should be routed through the TagProvider! Failed to read from PacketBuffer!");
        }

        public List<Tag<T>> getOwningTagObjects(final T thing)
        {
            //with possibly large amounts of forge tags, consider a reverse Mapping of T->List<Tag<T>>? (Multimap)
            //especially if they might be used to control gameplay related stuff?
            return getMatchingTags(tag -> tag.contains(thing));
        }

        public List<Tag<T>> getMatchingTags(Predicate<Tag<T>> predicate) {
            return getTagMap().values().stream().filter(predicate).collect(Collectors.toList());
        }

        public boolean tagMatch(Predicate<Tag<T>> tagPredicate) {
            return getTagMap().values().stream().anyMatch(tagPredicate);
        }

        private ImmutableList<String> getLocationPrefixes()
        {
            String regPath = regName.getNamespace()+'/'+regName.getPath();
            return regName.getNamespace().equals("minecraft") ? ImmutableList.of(TAG_FOLDER+regName.getPath(),TAG_FOLDER+regPath,TAG_FOLDER+FORGE_TAG_SUBFOLDER+regPath):ImmutableList.of(TAG_FOLDER+FORGE_TAG_SUBFOLDER+regPath);
        }

        private Stream<TagLoadingParameter> getLoadingParameters(IResourceManager resourceManager)
        {
            ImmutableList<String> prefixes = getLocationPrefixes();
            Stream<TagLoadingParameter> resources = Stream.of();
            for (String s:prefixes) {
                resources = Stream.concat(resources,resourceManager.getAllResourceLocations(s,s1 -> s1.endsWith(".json")).stream().map(location -> new TagLoadingParameter(s,location)));
            }
            return resources;
        }

        private Map<ResourceLocation, Tag.Builder<T>> deserializeTags(Stream<TagLoadingParameter> stream,IResourceManager resourceManager)
        {
            Map<ResourceLocation, Tag.Builder<T>> map = Maps.newHashMap();
            stream.forEach(tagLoadingParameter ->
            {
                try {
                    for(IResource iresource : resourceManager.getAllResources(tagLoadingParameter.getLocation())) {
                        try {
                            JsonObject jsonobject = (JsonObject) JsonUtils.fromJson(GSON, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                            if (jsonobject == null) {
                                LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", this.regName, tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), iresource.getPackName());
                            } else {
                                Tag.Builder<T> builder = map.getOrDefault(tagLoadingParameter.getTagId(), Tag.Builder.create());
                                builder.deserialize(this.isValueKnownPredicate, this.resourceLocationToItem, jsonobject);
                                map.put(tagLoadingParameter.getTagId(), builder);
                            }
                        } catch (RuntimeException | IOException ioexception) {
                            LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.regName, tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), iresource.getPackName(), ioexception);
                        } finally {
                            IOUtils.closeQuietly((Closeable)iresource);
                        }
                    }
                } catch (IOException ioexception1) {
                    LOGGER.error("Couldn't read {} tag list {} from {}", this.regName, tagLoadingParameter.getTagId(), tagLoadingParameter.getLocation(), ioexception1);
                }
            });
            return map;
        }

        private void registerUnbuildTags(Map<ResourceLocation, Tag.Builder<T>> map)
        {
            while(!map.isEmpty()) {
                boolean flag = false;
                Iterator<Entry<ResourceLocation, Builder<T>>> iterator = map.entrySet().iterator();

                while(iterator.hasNext()) {
                    Entry<ResourceLocation, Tag.Builder<T>> entry1 = iterator.next();
                    if ((entry1.getValue()).resolve(this::get)) {
                        flag = true;
                        this.register((entry1.getValue()).build(entry1.getKey()));
                        iterator.remove();
                    }
                }

                if (!flag) {
                    for(Entry<ResourceLocation, Tag.Builder<T>> entry2 : map.entrySet()) {
                        LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", this.regName, entry2.getKey());
                    }
                    break;
                }
            }
            //I have no idea why vanilla does this - it's the only reason preserveOrder is protected
            //if everything is well, the tag-list is empty, if not then some tag 'either references another tag that doesn't exist, or ultimately references itself'
            for(Entry<ResourceLocation, Tag.Builder<T>> entry : map.entrySet()) {  //so why register tags that are invalid?!?
                this.register((entry.getValue()).ordered(this.preserveOrder).build(entry.getKey()));
            }
        }

        private static class TagLoadingParameter { //value class, so that we don't need to use Triples...
            private final String prefix;
            private final ResourceLocation location;
            private final ResourceLocation tagId;

            public TagLoadingParameter(String prefix, ResourceLocation location) {
                this.prefix = prefix;
                this.location = location;
                String path = location.getPath();
                //this creates a ResourceLocation which is stripped of the prefixPath
                tagId = new ResourceLocation(location.getNamespace(), path.substring(prefix.length() + 1, path.length() - JSON_EXTENSION_LENGTH));
            }

            public String getPrefix() {
                return prefix;
            }

            public ResourceLocation getLocation() {
                return location;
            }

            public ResourceLocation getTagId() {
                return tagId;
            }
        }
    }
}
