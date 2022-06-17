/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.command.EntitySelectorManager;
import net.minecraftforge.common.command.IEntitySelectorType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("entity_selector_test")
public class EntitySelectorTest
{
    public EntitySelectorTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    public void setup(FMLCommonSetupEvent event)
    {
        EntitySelectorOptions.register("health", this::healthArgument, parser -> true, Component.literal("Selects entities based on their current health."));
        EntitySelectorManager.register("er", new ExampleCustomSelector());
    }

    /**
     * Example for a custom selector argument, checks for the health of the entity
     */
    private void healthArgument(EntitySelectorParser parser) throws CommandSyntaxException
    {
        MinMaxBounds.Doubles bound = MinMaxBounds.Doubles.fromReader(parser.getReader());
        parser.addPredicate(entity -> entity instanceof LivingEntity && bound.matches(((LivingEntity) entity).getHealth()));
    }

    /**
     * Example for a custom selector type, works like @r but for entities.
     * Basically does exactly what @e[sorter=random, limit=1, ...] does.
     */
    private class ExampleCustomSelector implements IEntitySelectorType
    {
        @Override
        public EntitySelector build(EntitySelectorParser parser) throws CommandSyntaxException
        {
            parser.setOrder(EntitySelectorParser.ORDER_RANDOM);
            parser.setMaxResults(1);
            parser.setIncludesEntities(true);
            parser.addPredicate(Entity::isAlive);
            parser.setSuggestions((builder, consumer) -> builder.suggest(String.valueOf('[')).buildFuture());
            if (parser.getReader().canRead() && parser.getReader().peek() == '[')
            {
                parser.getReader().skip();
                parser.setSuggestions((builder, consumer) -> {
                    builder.suggest(String.valueOf(']'));
                    EntitySelectorOptions.suggestNames(parser, builder);
                    return builder.buildFuture();
                });

                parser.parseOptions();
            }

            parser.finalizePredicates();
            return parser.getSelector();
        }

        @Override
        public Component getSuggestionTooltip()
        {
            return Component.literal("Example: Selects a random entity");
        }
    }
}
