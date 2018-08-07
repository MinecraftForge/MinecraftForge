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

package net.minecraftforge.debug.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "resources_error_test", name = "ResourceErrorTest", clientSideOnly = true)
@Mod.EventBusSubscriber
public class ResourcesErrorTest
{

    private static final boolean ENABLE = false;

    @SubscribeEvent
    public static void init(ModelRegistryEvent evt)
    {
        if (ENABLE)
            ModelLoader.setCustomModelResourceLocation(Items.DIAMOND, 1, new ModelResourceLocation("resources_error_test:missing#var0"));
    }

    @SubscribeEvent
    public static void init(TextureStitchEvent.Pre evt)
    {
        if (ENABLE)
            evt.getMap().registerSprite(new ResourceLocation("resources_error_test:missingno"));
    }
}
