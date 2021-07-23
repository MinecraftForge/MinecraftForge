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

package net.minecraftforge.debug.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("fishing_bobber_event_test")
public class FishingBobberEventTest
{
    public static final boolean ENABLE = true;

    public FishingBobberEventTest()
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(this);
            MinecraftForge.EVENT_BUS.addListener(FishingBobberEventTest::handleImpact);
        }
    }

    public static void handleImpact(ProjectileImpactEvent<FishingHook> event)
    {
        FishingHook fishingHook = event.getProjectile();
        HitResult trace = event.getHitResult();
        if (trace.getType() == HitResult.Type.ENTITY)
        {
            Vec3 vector3d = fishingHook.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(0.6);
            if (vector3d.lengthSqr() > 0.0D)
            {
                Entity entity = ((EntityHitResult) trace).getEntity();
                if (entity instanceof LivingEntity)
                {
                    entity.push(vector3d.x, 0.3D, vector3d.z);
                }
            }
        }
    }
}