/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.test.simplenet;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.api.distmarker.Dist;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SimpleNetTester
{

    @Test
    public void test()
    {
        SimpleNetworkWrapper simpleChannel = NetworkRegistry.INSTANCE.newSimpleChannel("TEST");
        simpleChannel.registerMessage(SimpleNetHandler1.class, SimpleNetTestMessage1.class, 1, Side.SERVER);
        simpleChannel.registerMessage(SimpleNetHandler2.class, SimpleNetTestMessage2.class, 2, Side.CLIENT);
        assertTrue("Hello", true);
    }

}
