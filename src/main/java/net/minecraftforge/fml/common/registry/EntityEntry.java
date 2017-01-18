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
package net.minecraftforge.fml.common.registry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.google.common.base.Throwables;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry.Impl;

public class EntityEntry extends Impl<EntityEntry>
{
    private Class<? extends Entity> cls;
    private String name;
    private EntityEggInfo egg;
    private Constructor<?> ctr;

    public EntityEntry(Class<? extends Entity> cls, String name)
    {
        this.cls = cls;
        this.name = name;
        init();
    }

    //Protected method, to make this optional, in case people subclass this to have a better factory.
    protected void init()
    {
        try
        {
            this.ctr = this.cls.getConstructor(World.class);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException("Invalid class " + this.cls + " no constructor taking " + World.class.getName());
        }
        catch (SecurityException e)
        {
            Throwables.propagate(e);
        }
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
        try
        {
            return (Entity)this.ctr.newInstance(world);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
