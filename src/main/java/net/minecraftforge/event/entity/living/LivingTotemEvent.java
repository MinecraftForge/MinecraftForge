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

package net.minecraftforge.event.entity.living;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * LivingTotemEvent is fired when a player is about to die.
 * <br>
 * This event is fired whenever a player is about to die.
 * <br>
 * This event isn't {@link Cancelable}
 * <br>
 * This event has a result. {@link HasResult}
 * <br>
 * If you pass Result.Allow, you need to do any resulting handling such as setting effects and such.
 * Result.Allow does only handle Life-Setting and Entity State handling by default.
 * This is done to allow mod-authors to create their own "set of effects" for their own totems.
 * <br>
 *  Result.Allow = Protects the player, setting their health to the value set in getHealth or 1F.
 *  Result.Default = Runs the default Totem of Undying checks and balances.
 *  Result.Deny = Do not save the player regardless of them holding the Totem.
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}
 */
@Event.HasResult
public class LivingTotemEvent extends LivingEvent
{
    /**
     * If the event returns Result.Allow, the value of health will be used to decide the health of the player is set to when "protected".
     * By default, the player's health will be set to 1.
     */
    private float health;
    private final DamageSource source;

    public LivingTotemEvent(EntityLivingBase livingBase, DamageSource source)
    {
        super(livingBase);
        this.source = source;
    }

    public DamageSource getSource()
    {
        return source;
    }

    public float getHealth()
    {
        return health;
    }

    public void setHealth(int health)
    {
        this.health = health;
    }
}
