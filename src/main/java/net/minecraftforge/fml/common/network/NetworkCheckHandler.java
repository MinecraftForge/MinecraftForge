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

package net.minecraftforge.fml.common.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraftforge.fml.relauncher.Side;

/**
 * A method annotated with this will be called when a remote network connection is offered.
 * The method should have two parameters, of types Map<String,String> and {@link Side}. It should return a boolean
 * true indicating that the remote party is acceptable, or false if not.
 *
 * <p>
 * When the method is invoked, the map will contain String keys and values listing all mods and their versions present.
 * The side represents the side of the remote party. So if you're on the server, it'll be CLIENT, and vice versa.
 *
 * <p>
 * This method will be invoked both when querying the status of the remote server, and when connecting to the remote server.
 *
 * <p>
 * <strong>NOTE: the server will not be setup at any point when this method is called. Do not try and interact with the server
 * or the client in any way, except to accept or reject the list of mods.</strong>
 *
 * @author cpw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NetworkCheckHandler
{

}
