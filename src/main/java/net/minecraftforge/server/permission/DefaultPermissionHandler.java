/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.server.permission;

import com.mojang.authlib.GameProfile;
import net.minecraftforge.server.permission.context.IContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DefaultPermissionHandler implements IPermissionHandler
{
   Set<PermissionNode<?>> registeredNodes = new HashSet<>();

   @Override
   public void registerNode(PermissionNode<?> node)
   {
      registeredNodes.add(node);
   }

   @Override
   public void unregisterNode(PermissionNode<?> node)
   {
      registeredNodes.remove(node);
   }

   @Override
   public Collection<PermissionNode<?>> getRegisteredNodes()
   {
      return Collections.unmodifiableSet(registeredNodes);
   }

   @Override
   public <T> T getPermission(GameProfile profile, PermissionNode<T> node, IContext context)
   {
      return node.getDefaultValue(profile);
   }
}
