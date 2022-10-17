package net.minecraftforge.data.event;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.data.ValidationPredicate;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.IModBusEvent;
import org.slf4j.Logger;

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

    public void or(ValidationPredicate predicate)
    {
        add(Strategy.OR, predicate);
    }

    public enum Strategy
    {
        AND, OR
    }
}
