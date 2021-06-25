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

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.server.permission.context.IContext;
import net.minecraftforge.server.permission.context.PlayerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class PermissionAPI
{
   private static final Logger LOGGER = LogManager.getLogger();

   private static IPermissionHandler permissionHandler = new DefaultPermissionHandler();

   /**
    * <strong>Only use this before {@link net.minecraftforge.fml.event.server.FMLServerStartingEvent} fired!</strong>
    * <p>
    * The last PermissionHandler to be registered wins.
    */
   public static void setPermissionHandler(IPermissionHandler handler)
   {
      Preconditions.checkNotNull(handler, "Permission handler can't be null!");
      LOGGER.warn("Replacing {} with {}", permissionHandler.getClass().getName(), handler.getClass().getName());
      permissionHandler = handler;
   }

   public static IPermissionHandler getPermissionHandler()
   {
      return permissionHandler;
   }

   /**
    * <strong>Only use this after {@link net.minecraftforge.fml.event.server.FMLServerStartingEvent} fired!</strong>
    * Notifies a PermissionHandler about the existence of a node, this is not required, but recommended.
    *
    * @param node PermissionNode to be registered
    */
   public static void registerNode(PermissionNode<?> node)
   {
      Preconditions.checkNotNull(node, "Permission node can't be null!");
      permissionHandler.registerNode(node);
   }

   /**
    * <strong>Only use this after {@link net.minecraftforge.fml.event.server.FMLServerStartingEvent} fired!</strong>
    * Requests a PermissionHandler to "forget" about a node, meant for usage with dynamic PermissionNodes
    *
    * @param node PermissionNode to be unregistered
    */
   public static void unregisterNode(PermissionNode<?> node)
   {
      Preconditions.checkNotNull(node, "Permission node can't be null!");
      permissionHandler.unregisterNode(node);
   }

   /**
    * @param profile GameProfile of the player who is requesting permission. The player doesn't have to be online
    * @param node    Permission node
    * @param context Context for this permission. Highly recommended to not be null. See {@link IContext}
    * @return permission value depending on the PermissionNode type, uses the default value if not existent.
    */
   public static <T> T getPermission(GameProfile profile, PermissionNode<T> node, @Nullable IContext context)
   {
      Preconditions.checkNotNull(profile, "GameProfile can't be null!");
      Preconditions.checkNotNull(node, "Permission node can't be null!");
      return permissionHandler.getPermission(profile, node, context);
   }

   /**
    * Shortcut method using EntityPlayer and creating PlayerContext
    *
    * @see PermissionAPI#getPermission(GameProfile, PermissionNode, IContext)
    */
   public static <T> T getPermission(PlayerEntity player, PermissionNode<T> node)
   {
      Preconditions.checkNotNull(player, "Player can't be null!");
      return getPermission(player.getGameProfile(), node, new PlayerContext(player));
   }
}
