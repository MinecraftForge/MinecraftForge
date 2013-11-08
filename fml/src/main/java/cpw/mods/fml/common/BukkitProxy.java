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

package cpw.mods.fml.common;

/**
 * A marker interface for retrieving a proxy to a bukkit plugin.
 * Fields associated with {@link BukkitPluginRef} annotations should
 * declare this type and cast down if the target is available (not null)
 * @author cpw
 *
 */
public interface BukkitProxy
{
}
