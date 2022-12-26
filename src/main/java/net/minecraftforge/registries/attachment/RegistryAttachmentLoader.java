/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.attachment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@ApiStatus.Internal
@MethodsReturnNonnullByDefault
public record RegistryAttachmentLoader(RegistryAccess registryAccess) implements PreparableReloadListener
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PATH = "attachments";

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, ProfilerFiller p_10640_, ProfilerFiller p_10641_, Executor loadExecutor, Executor applyExecutor)
    {
        return this.load(manager, loadExecutor).thenCompose(barrier::wait)
                .thenCompose(values -> CompletableFuture.runAsync(() -> apply(values), applyExecutor));
    }

    private void apply(Map<ResourceKey<? extends Registry<?>>, AttachmentValues<?, ?>> values)
    {
        registryAccess.registries().forEach(entry ->
        {
            final var attachmentsView = entry.value().attachments();
            entry.value().setDuringAttachmentLoading(true);
            attachmentsView.forEach((type, holder) ->
            {
                final var holderInternal = ((IRegistryAttachmentHolderInternal<?, ?>) holder);
                holderInternal.unfreeze();
                holderInternal.replaceWith(new HashMap<>());
            });
            MinecraftForge.EVENT_BUS.post(new AddBuiltInRegistryAttachmentsEvent(entry.value()));

            final AttachmentValues<?, ?> val = values.get(entry.key());
            if (val != null) val.fillUnsafe(entry.value());

            attachmentsView.forEach((type, holder) -> ((IRegistryAttachmentHolderInternal<?, ?>) holder).freeze());
            entry.value().setDuringAttachmentLoading(false);
        });
    }

    private CompletableFuture<Map<ResourceKey<? extends Registry<?>>, AttachmentValues<?, ?>>> load(ResourceManager manager, Executor executor)
    {
        return CompletableFuture.supplyAsync(() -> load(manager, registryAccess), executor);
    }

    private static Map<ResourceKey<? extends Registry<?>>, AttachmentValues<?, ?>> load(ResourceManager manager, RegistryAccess access)
    {
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, access);

        final Map<ResourceKey<? extends Registry<?>>, AttachmentValues<?, ?>> values = new HashMap<>();
        FileToIdConverter filetoidconverter = FileToIdConverter.json(PATH);
        for (Map.Entry<ResourceLocation, List<Resource>> entry : filetoidconverter.listMatchingResourceStacks(manager).entrySet())
        {
            ResourceLocation key = entry.getKey();
            ResourceLocation id = filetoidconverter.fileToId(key);

            final ResourceLocation attachmentId;
            final ResourceLocation registryId;
            {
                final String[] split = id.getPath().split("/");
                if (split.length < 2) continue;
                if (split.length == 2)
                {
                    registryId = new ResourceLocation(split[0]);
                    attachmentId = new ResourceLocation(id.getNamespace(), split[1]);
                } else
                {
                    registryId = new ResourceLocation(split[0], split[1]);
                    attachmentId = new ResourceLocation(id.getNamespace(), String.join("/", Arrays.copyOfRange(split, 2, split.length)));
                }
            }

            final IRegistryAttachmentType<?> attachmentType = ForgeRegistries.REGISTRY_ATTACHMENT_TYPES.get().getValue(attachmentId);
            if (attachmentType == null) continue;
            final var attachments = readAttachments(ops, ResourceKey.createRegistryKey(registryId), attachmentId, attachmentType, entry.getValue());
            values.put(attachments.registryKey(), attachments);
        }

        return values;
    }

    private static <R, A> AttachmentValues<R, A> readAttachments(RegistryOps<JsonElement> ops, ResourceKey<? extends Registry<R>> registryKey, ResourceLocation attachmentId, IRegistryAttachmentType<A> attachment, List<Resource> resources)
    {
        final List<Entry<R, A>> entries = new ArrayList<>();
        for (Resource resource : resources)
        {
            try
            {
                try (Reader reader = resource.openAsReader())
                {
                    JsonElement jsonelement = JsonParser.parseReader(reader);
                    if (!(jsonelement instanceof JsonObject object)) continue;

                    final boolean replace = GsonHelper.getAsBoolean(object, "replace", false);
                    final Map<ResourceLocation, A> attachments = new HashMap<>();

                    final JsonObject valuesJson = GsonHelper.getAsJsonObject(object, "values");
                    for (final String key : valuesJson.keySet())
                    {
                        final JsonElement vjson = valuesJson.get(key);
                        if (!ICondition.shouldRegisterEntry(vjson)) continue;
                        final A attach = attachment.getCodec().decode(ops, vjson).getOrThrow(false, e ->
                        {
                        }).getFirst();
                        attachments.put(new ResourceLocation(key), attach);
                    }

                    entries.add(new Entry<>(replace, attachments));
                }
            } catch (Exception exception)
            {
                LOGGER.error("Could not read attachments of type {} for registry {}", attachmentId, registryKey, exception);
            }
        }

        return new AttachmentValues(
                registryKey, ResourceKey.create(ForgeRegistries.Keys.REGISTRY_ATTACHMENT_TYPES, attachmentId), entries
        );
    }

    public record Entry<R, A>(boolean replace, Map<ResourceLocation, A> values)
    {
    }

    public record AttachmentValues<R, A>(ResourceKey<? extends Registry<R>> registryKey,
                                         ResourceKey<IRegistryAttachmentType<A>> type, List<Entry<R, A>> entries)
    {
        @SuppressWarnings("unchecked")
        public void fillUnsafe(Registry<?> registry)
        {
            this.fill((Registry<R>) registry);
        }

        public void fill(Registry<R> registry)
        {
            final IRegistryAttachmentHolderInternal<R, A> holder = (IRegistryAttachmentHolderInternal<R, A>) registry.attachment(type);
            if (holder == null) return;

            for (final Entry<R, A> entry : entries)
            {
                if (entry.replace)
                {
                    final Map<Holder<R>, A> map = new HashMap<>(entry.values.size(), 1);
                    entry.values.forEach((resourceLocation, a) -> map.put(registry.getHolderOrThrow(ResourceKey.create(registryKey, resourceLocation)), a));
                    holder.replaceWith(map);
                } else
                {
                    entry.values.forEach((resourceLocation, a) -> holder.attach(registry.getHolderOrThrow(ResourceKey.create(registryKey, resourceLocation)), a));
                }
            }
        }
    }
}
