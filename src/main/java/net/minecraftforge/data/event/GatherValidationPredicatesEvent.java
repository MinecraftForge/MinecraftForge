/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.data.event;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.data.ValidationPredicate;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * This event is fired before {@link GatherDataEvent} in order to collect {@linkplain ValidationPredicate validation predicates}
 * which will be used to determine if the {@link net.minecraftforge.common.data.ExistingFileHelper} should actually
 * check for the existence of a resource. <br>
 * <strong>Important:</strong> your mod's predicates will only be appended to the list if your mod's generators are enabled.
 * <br>
 * This event is fired on the {@linkplain FMLJavaModLoadingContext#getModEventBus() mod-specific event bus}. <br>
 * <br><br>
 * Example usage for ignoring all model validations for mod {@code examplemod}:
 * <pre>
 * {@code
 *   // Use andNot, as we want to disable validation if the check passes
 *   event.andNot((validationType, requestedPath, packType) ->
 *         // Check if the requested resource is a client resource
 *         packType == PackType.CLIENT_RESOURCES
 *         // Check if the validated resource is a model
 *         && Objects.equals(validationType, ValidationPredicate.MODELS_VALIDATION_TYPE)
 *         // Check if the requested resource is from the `examplemod` namespace
 *         && requestedPath.getNamespace().equals("examplemod"));
 * }
 * </pre>
 * Example usage for disabling model validation:
 * <pre>
 * {@code
 *   event.andNot(ValidationPredicate.enableType(ValidationPredicate.MODELS_VALIDATION_TYPE));
 * }
 * </pre>
 */
public class GatherValidationPredicatesEvent extends Event implements IModBusEvent
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private final ModContainer container;
    private final GatherDataEvent.DataGeneratorConfig config;

    public GatherValidationPredicatesEvent(final ModContainer container, final GatherDataEvent.DataGeneratorConfig config)
    {
        this.container = container;
        this.config = config;
    }

    public ValidationPredicate getValidationPredicate() { return this.config.validationPredicate; }

    public void add(Strategy strategy, ValidationPredicate predicate)
    {
        if (!config.getMods().contains(container.getModId()))
        {
            LOGGER.debug("Skipping validation predicate {} as mod does not have datagen enabled.", predicate);
            return;
        }

        this.config.validationPredicate = switch (strategy)
        {
            case AND -> getValidationPredicate().and(predicate);
            case OR -> getValidationPredicate().or(predicate);
        };
    }

    public void and(ValidationPredicate predicate)
    {
        add(Strategy.AND, predicate);
    }

    public void andNot(ValidationPredicate predicate)
    {
        and(predicate.not());
    }

    public void or(ValidationPredicate predicate)
    {
        add(Strategy.OR, predicate);
    }

    public enum Strategy
    {
        AND, OR
    }
}
