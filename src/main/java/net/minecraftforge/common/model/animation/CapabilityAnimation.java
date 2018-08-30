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

package net.minecraftforge.common.model.animation;

import java.util.concurrent.Callable;

import net.minecraft.nbt.INBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityAnimation
{
    @CapabilityInject(IAnimationStateMachine.class)
    public static Capability<IAnimationStateMachine> ANIMATION_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IAnimationStateMachine.class, new Capability.IStorage<IAnimationStateMachine>()
        {
            @Override
            public INBTBase writeNBT(Capability<IAnimationStateMachine> capability, IAnimationStateMachine instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IAnimationStateMachine> capability, IAnimationStateMachine instance, EnumFacing side, INBTBase nbt) {}
        }, AnimationStateMachine::getMissing);
    }

    public static class DefaultItemAnimationCapabilityProvider implements ICapabilityProvider
    {
        private final IAnimationStateMachine asm;

        public DefaultItemAnimationCapabilityProvider(IAnimationStateMachine asm)
        {
            this.asm = asm;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == ANIMATION_CAPABILITY;
        }

        @Override
        @Nullable
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            if(capability == ANIMATION_CAPABILITY)
            {
                return ANIMATION_CAPABILITY.cast(asm);
            }
            return null;
        }
    }
}
