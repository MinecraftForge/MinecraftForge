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

package net.minecraftforge.client.event;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * SetupLivingBipedAnimEvent is fired if rendering a PlayerModel.
 * <br>
 * This event is {@link Cancelable}. <br>
 * If the event is canceled, does all Vanilla animations stop.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/

public class SetupLivingBipedAnimEvent extends Event
{
    private LivingEntity entity;
    private BipedModel model;
    private float limbSwing;
    private float limbSwingAmount;
    private float ageInTicks;
    private float netHeadYaw;
    private float headPitch;

    public SetupLivingBipedAnimEvent(LivingEntity entity, BipedModel model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.entity = entity;
        this.model = model;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.ageInTicks = ageInTicks;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
    }

    public LivingEntity getEntity()
    {
        return entity;
    }

    public BipedModel getModel()
    {
        return model;
    }

    public float getLimbSwing()
    {
        return limbSwing;
    }

    public float getLimbSwingAmount()
    {
        return limbSwingAmount;
    }

    public float getAgeInTicks()
    {
        return ageInTicks;
    }

    public float getNetHeadYaw()
    {
        return netHeadYaw;
    }

    public float getHeadPitch() {
        return headPitch;
    }

    @Cancelable
    public static class Pre extends SetupLivingBipedAnimEvent
    {
        public Pre(LivingEntity entity, BipedModel model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
        {
            super(entity, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public static class Post extends SetupLivingBipedAnimEvent
    {
        public Post(LivingEntity entity, BipedModel model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
        {
            super(entity, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }
}
