/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.entity.living;

import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

//@Mod(modid = "entitymobgriefingeventtest", name = "EntityMobGriefingEventTest", version = "1.0", acceptableRemoteVersions = "*")
//@Mod.EventBusSubscriber
public class MobGriefingEventTest
{
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onMobGriefing(EntityMobGriefingEvent event)
    {
        if (ENABLED)
        {
            String customName = event.getEntity().getCustomNameTag();

            try
            {
                Result result = Result.valueOf(customName);
                event.setResult(result);
            }
            catch (IllegalArgumentException iae)
            {
                // Thrown if the name tag did not match a result value, can be ignored and DEFAULT will still be used.
            }
        }
    }
}
*/
