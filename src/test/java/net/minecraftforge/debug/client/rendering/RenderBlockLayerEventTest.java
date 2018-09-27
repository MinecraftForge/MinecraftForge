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

package net.minecraftforge.debug.client.rendering;

import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.client.event.RenderBlockLayerEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
@Mod(modid = RenderBlockLayerEventTest.MODID, name = "RenderBlockLayerEventTest", version = "1.0", acceptableRemoteVersions = "*", clientSideOnly = true)
public class RenderBlockLayerEventTest
{

    static final String MODID = "render_block_layer_event_test";
    static final boolean ENABLED = true;

    public static void sad(final RenderBlockLayerEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        if (event.getBlockRenderLayer() == BlockRenderLayer.SOLID)
        {
            event.setCanceled(true);
        }
    }

}
