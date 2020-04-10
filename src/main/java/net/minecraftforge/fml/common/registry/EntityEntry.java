/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml.common.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

import java.util.function.Function;

public class EntityEntry extends Impl<EntityEntry>
{
    private Class<? extends Entity> cls;
    private String name;
    private EntityEggInfo egg;
    Function<World, ? extends Entity> factory;

    public EntityEntry(Class<? extends Entity> cls, String name)
    {
        this.cls = cls;
        this.name = name;
        init();
    }

    //Protected method, to make this optional, in case people subclass this to have a better factory.
    protected void init()
    {
        this.factory = new EntityEntryBuilder.ConstructorFactory<Entity>(this.cls) {
            @Override
            protected String describeEntity() {
                return String.valueOf(EntityEntry.this.getRegistryName());
            }
        };
    }

    public Class<? extends Entity> getEntityClass(){ return this.cls; }
    public String getName(){ return this.name; }
    public EntityEggInfo getEgg(){ return this.egg; }

    public void setEgg(EntityEggInfo egg)
    {
        this.egg = egg;
        if (this.getRegistryName() != null)
            EntityList.ENTITY_EGGS.put(this.getRegistryName(), egg);
    }

    public Entity newInstance(World world)
    {
        return this.factory.apply(world);
    }
}
