/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.attachment.IRegistryAttachmentType;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RegistryAttachmentProvider<R, A> implements DataProvider
{
    private final PackOutput output;
    private final ResourceKey<? extends Registry<R>> registry;
    private final ResourceKey<IRegistryAttachmentType<A>> attachmentType;
    private final Map<ResourceLocation, WithCondition<A>> attachments;
    private final boolean replace;

    public RegistryAttachmentProvider(PackOutput output, ResourceKey<? extends Registry<R>> registry, ResourceKey<IRegistryAttachmentType<A>> attachmentType, boolean replace, Map<ResourceLocation, WithCondition<A>> attachments)
    {
        this.output = output;
        this.registry = registry;
        this.attachmentType = attachmentType;
        this.attachments = attachments;
        this.replace = replace;
    }

    public static <R, A> RegistryAttachmentProvider<R, A> forRegistry(PackOutput output, ResourceKey<? extends Registry<R>> registryKey, ResourceKey<IRegistryAttachmentType<A>> attachmentType, boolean replace, Registry<R> registry, Consumer<Builder<R, A>> builderConsumer)
    {
        final Map<ResourceLocation, WithCondition<A>> attachments = new HashMap<>();
        builderConsumer.accept(new Builder<>()
        {
            @Override
            public Builder<R, A> add(R object, A attachment, ICondition... conditions)
            {
                return add(registry.wrapAsHolder(object), attachment, conditions);
            }

            @Override
            public Builder<R, A> add(Holder<R> object, A attachment, ICondition... conditions)
            {
                return add(object.unwrapKey().orElseThrow().location(), attachment, conditions);
            }

            @Override
            public Builder<R, A> add(ResourceLocation objectId, A attachment, ICondition... conditions)
            {
                attachments.put(objectId, new WithCondition<>(conditions, attachment));
                return this;
            }
        });
        return new RegistryAttachmentProvider<>(
                output, registryKey, attachmentType, replace, attachments
        );
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output)
    {
        final ResourceLocation regLocation = registry.location();
        final ResourceLocation attachLocation = attachmentType.location();
        final Path outputFile = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(String.join(
                "/", attachLocation.getNamespace(), "attachments", regLocation.getNamespace(), regLocation.getPath()
        ) + "/" + attachLocation.getPath() + ".json");

        final Codec<A> codec = (Codec<A>) ForgeRegistries.REGISTRY_ATTACHMENT_TYPES.get().getValue(attachLocation).getCodec();

        final JsonObject root = new JsonObject();
        root.addProperty("replace", replace);

        final JsonObject values = new JsonObject();
        attachments.forEach((resourceLocation, attachment) -> values.add(
                resourceLocation.toString(), attachment.encode(resourceLocation, codec)
        ));
        root.add("values", values);
        return DataProvider.saveStable(output, root, outputFile);
    }

    @Override
    public String getName()
    {
        return "%s registry attachments".formatted(attachmentType.location());
    }

    public interface Builder<R, A>
    {
        Builder<R, A> add(R object, A attachment, ICondition... conditions);

        Builder<R, A> add(Holder<R> object, A attachment, ICondition... conditions);

        Builder<R, A> add(ResourceLocation objectId, A attachment, ICondition... conditions);
    }

    public record WithCondition<T>(@Nullable ICondition[] conditions, T value)
    {
        public JsonElement encode(Object path, Codec<T> codec)
        {
            final JsonElement element = codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow(false, e ->
            {
            });
            if (conditions != null && conditions.length > 0) {
                if (element instanceof JsonObject obj) {
                    obj.add("forge:conditions", CraftingHelper.serialize(conditions));
                } else {
                    LOGGER.error("Attempted to apply conditions to a type that is not a JsonObject! - {}", path);
                }
            }
            return element;
        }
    }
}
