/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.arguments.EntityOptions;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
        EntityOptions.register("health", this::healthArgument, parser -> true, new StringTextComponent("Selects entities based on their current health."));
        EntitySelectorManager.register("er", new ExampleCustomSelector());
    }

    /**
     * Example for a custom selector argument, checks for the health of the entity
     */
    private void healthArgument(EntitySelectorParser parser) throws CommandSyntaxException
    {
        MinMaxBounds.FloatBound bound = MinMaxBounds.FloatBound.fromReader(parser.getReader());
        parser.addFilter(entity -> entity instanceof LivingEntity && bound.test(((LivingEntity) entity).getHealth()));
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
            parser.setSorter(EntitySelectorParser.RANDOM);
            parser.setLimit(1);
            parser.setIncludeNonPlayers(true);
            parser.addFilter(Entity::isAlive);
            parser.setSuggestionHandler((builder, consumer) -> builder.suggest(String.valueOf('[')).buildFuture());
            if (parser.getReader().canRead() && parser.getReader().peek() == '[')
            {
                parser.getReader().skip();
                parser.setSuggestionHandler((builder, consumer) -> {
                    builder.suggest(String.valueOf(']'));
                    EntityOptions.suggestOptions(parser, builder);
                    return builder.buildFuture();
                });

                parser.parseArguments();
            }

            parser.updateFilter();
            return parser.build();
        }

        @Override
        public ITextComponent getSuggestionTooltip()
        {
            return new StringTextComponent("Example: Selects a random entity");
        }
    }
}
