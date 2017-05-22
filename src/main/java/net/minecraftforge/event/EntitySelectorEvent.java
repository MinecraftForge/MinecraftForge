/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.event;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.IEntitySelectorFactory;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.Map;

/**
 * Is not fired anymore.
 * Replaced by a factory, which has to be registered with {@link net.minecraftforge.fml.common.registry.GameRegistry#registerEntitySelector(IEntitySelectorFactory, String...)}
 * TODO remove in 1.12
 */
@Deprecated
public class EntitySelectorEvent extends Event
{

    private final Map<String, String> map;
    private final String mainSelector;
    private final ICommandSender sender;
    private final Vec3d position;
    private final List<Predicate<Entity>> selectors;

    public EntitySelectorEvent(Map<String, String> map, String mainSelector, ICommandSender sender, Vec3d position)
    {
        this.map = map;
        this.mainSelector = mainSelector;
        this.sender = sender;
        this.position = position;
        selectors = Lists.newArrayList();
    }

    /**
     * Add your custom selector.
     * It is recommend to use "modid:name", if checking for own arguments, to avoid inter-mod interference (e.g.  "@a[Forge:timeplayed=100]").
     *
     * @param selector Your custom predicate
     */
    public void addPredicate(Predicate<Entity> selector)
    {
        if (selector == null)
        {
            throw new NullPointerException("Attempted to add null predicate as entity selector");
        }
        selectors.add(selector);
    }

    /**
     * @return The main selector used (e.g. 'a' for all players or 'e' for all entities)
     */
    public String getMainSelector()
    {
        return mainSelector;
    }

    /**
     * Example: "@a[test=true]" would result in a map with "test"=>"true"
     *
     * @return The argument map. Maps all given argument names with its value.
     */
    public Map<String, String> getArgumentMap()
    {
        return map;
    }

    /**
     * See {@link EntitySelector#getPosFromArguments(Map, Vec3d)}
     *
     * @return A position either specified in the selector arguments or by the players position.
     */
    public Vec3d getPosition()
    {
        return position;
    }

    /**
     * @return The sender of the command.
     */
    public ICommandSender getSender()
    {
        return sender;
    }

    /**
     * @return The list of added custom selectors
     */
    List<Predicate<Entity>> getSelectors()
    {
        return selectors;
    }

}
