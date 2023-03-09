/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.registries.attachment;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

/**
 * Class used for attaching objects to registry entries, or querying attached objects. <br>
 * You may attach objects to registry entries using {@link AddBuiltInRegistryAttachmentsEvent} or using a datapack (which takes prioriy over the event),
 * and placing a tag-like json file at the following path:
 * <pre>
 *     {@code data/<attachmentType_namespace>/attachments/<registry_namespace>/<registry_path>/<attachmentType_path>.json}
 * </pre>
 * The json file has 2 parameters:
 * <ul>
 *     <li>{@code replace} - if this file should overwrite previous entries, including code-added entries</li>
 *     <li>{@code values} - a map of registry entry ID -> attached value</li>
 * </ul>
 * <br>
 * For example, in order to attach a {@link String} value using the {@code examplemod:example} {@link IRegistryAttachmentType} to the {@code minecraft:item} registry, we would
 * add a json file at the path
 * <pre>
 *     {@code data/examplemod/attachments/minecraft/item/example.json}
 * </pre>
 * with the contents
 * <pre>
 *     {@code
 *     {
 *         "replace": false,
 *         "values" : {
 *             "minecraft:carrot": "some string" // Attach the "some string" value to the "minecraft:carrot" item
 *         }
 *     }
 *     }
 * </pre>
 *
 * @param <R> the type of the objects to attach to
 * @param <A> the type of the attached objects
 * @see IWithRegistryAttachments#attachment(ResourceKey)
 */
public interface IRegistryAttachmentHolder<R, A>
{
    /**
     * Gets the value associated with the attachment type for the specified entry.
     *
     * @param entry the registry entry
     * @return the attached value, or if not present, the {@linkplain IRegistryAttachmentType#getDefaultValue() default value}
     */
    @Nullable
    A get(R entry);

    /**
     * Gets the value associated with the attachment type for the specified entry.
     *
     * @param entry the registry entry holder
     * @return the attached value, or if not present, the {@linkplain IRegistryAttachmentType#getDefaultValue() default value}
     */
    @Nullable
    A get(Holder<R> entry);

    /**
     * Attaches a value to a registry entry.
     *
     * @param entry the registry entry
     * @param value the value to attach
     */
    void attach(R entry, A value);

    /**
     * Attaches a value to a registry entry.
     *
     * @param entry the registry entry
     * @param value the value to attach
     */
    void attach(Holder<R> entry, A value);
}
