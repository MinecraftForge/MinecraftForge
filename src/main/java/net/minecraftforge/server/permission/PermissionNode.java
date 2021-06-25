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
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class PermissionNode<T>
{
   private final String identifier;
   private final PermissionType<T> type;
   private final Function<GameProfile, T> defaultValueProvider;
   private final String description;

   public PermissionNode(ResourceLocation identifier, PermissionType<T> type, Function<GameProfile, T> defaultValueProvider)
   {
      this(identifier.getNamespace() + "." + identifier.getPath(), type, defaultValueProvider, "");
   }

   public PermissionNode(ResourceLocation identifier, PermissionType<T> type, Function<GameProfile, T> defaultValueProvider, String description)
   {
      this(identifier.getNamespace() + "." + identifier.getPath(), type, defaultValueProvider, description);
   }

   public PermissionNode(String identifier, PermissionType<T> type, Function<GameProfile, T> defaultValueProvider, String description)
   {
      this.identifier = identifier;
      this.type = type;
      this.defaultValueProvider = defaultValueProvider;
      this.description = description;
   }

   public String getIdentifier()
   {
      return identifier;
   }

   public PermissionType<T> getType()
   {
      return type;
   }

   public T getDefaultValue(GameProfile profile)
   {
      return defaultValueProvider.apply(profile);
   }

   public String getDescription()
   {
      return this.description;
   }

   /**
    * Shortcut method
    *
    * @see PermissionAPI#registerNode(PermissionNode)
    */
   public void registerPermission()
   {
      PermissionAPI.registerNode(this);
   }

   /**
    * Shortcut method
    *
    * @see PermissionAPI#unregisterNode(PermissionNode)
    */
   public void unregisterPermission()
   {
      PermissionAPI.unregisterNode(this);
   }
}
