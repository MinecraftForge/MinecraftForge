/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml;

import net.minecraftforge.api.Side;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public final class SidedExecutor
{
    private SidedExecutor() {}

    /**
     * Run the callable in the supplier only on the specified {@link Side}
     *
     * @param side The side to run on
     * @param toRun A supplier of the callable to run (Supplier wrapper to ensure classloading only on the appropriate side)
     * @param <T> The return type from the callable
     * @return The callable's result
     */
    public static <T> T runOn(Side side, Supplier<Callable<T>> toRun) {
        if (side == Side.CLIENT) {
            try
            {
                return toRun.get().call();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
