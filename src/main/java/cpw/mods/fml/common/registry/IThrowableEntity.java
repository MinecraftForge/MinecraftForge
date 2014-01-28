/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.common.registry;

import net.minecraft.entity.Entity;

/**
 * This interface should be implemented by an Entity that can be 'thrown', like snowballs.
 * This was created to mimic ModLoaderMP's 'owner' functionality.
 */
public interface IThrowableEntity
{
    /**
     * Gets the entity that threw/created this entity.
     * @return The owner instance, Null if none.
     */
    public Entity getThrower();

    /**
     * Sets the entity that threw/created this entity.
     * @param entity The new thrower/creator.
     */
    public void setThrower(Entity entity);
}