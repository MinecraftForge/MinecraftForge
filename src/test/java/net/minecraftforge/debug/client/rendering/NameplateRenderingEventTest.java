/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("nameplate_render_test")
@Mod.EventBusSubscriber
public class NameplateRenderingEventTest
{

    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onNameplateRender(RenderNameplateEvent event)
    {

        if(!ENABLED)
        {
            return;
        }

        if(event.getEntity() instanceof CowEntity)
        {
            event.setContent(TextFormatting.RED + "Evil Cow");
            event.setResult(Event.Result.ALLOW);
        }

        if(event.getEntity() instanceof PlayerEntity)
        {
            event.setContent(TextFormatting.GOLD + "" + (event.getEntity()).getDisplayName().getString());
        }
    }
}
