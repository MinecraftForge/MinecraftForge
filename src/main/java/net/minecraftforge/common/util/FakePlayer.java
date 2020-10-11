/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.common.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.stats.Stat;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

//Preliminary, simple Fake Player class
public class FakePlayer extends ServerPlayerEntity
{
    /**
     * UUID of the player responsible for the FakePlayers actions.
     */
    private UUID owner = null;

    public FakePlayer(ServerWorld world, GameProfile name)
    {
        super(world.getServer(), world, name, new PlayerInteractionManager(world));
    }

    @Override public Vector3d getPositionVec(){ return new Vector3d(0, 0, 0); }
    @Override public BlockPos func_233580_cy_(){ return BlockPos.ZERO; }
    @Override public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar){}
    @Override public void sendMessage(ITextComponent component, UUID p_145747_2_) {}
    @Override public void addStat(Stat par1StatBase, int par2){}
    //@Override public void openGui(Object mod, int modGuiId, World world, int x, int y, int z){}
    @Override public boolean isInvulnerableTo(DamageSource source){ return true; }
    @Override public boolean canAttackPlayer(PlayerEntity player){ return false; }
    @Override public void onDeath(DamageSource source){ return; }
    @Override public void tick(){ return; }
    @Override public void handleClientSettings(CClientSettingsPacket pkt){ return; }
    @Override @Nullable public MinecraftServer getServer() { return ServerLifecycleHooks.getCurrentServer(); }

    /**
     * Get the current owner of the fakeplayer.
     * @return the owners UUID.
     */
    @Nullable
    public UUID getOwner() { return owner; }

    /**
     * Set the owner of the fakeplayer.
     * @param owner the owners UUID.
     */
    public void setOwner(@Nonnull final UUID owner) { this.owner = owner; }

    @Override
    public void writeAdditional(@Nonnull final CompoundNBT compound)
    {
        super.writeAdditional(compound);
        if (owner != null)
            compound.putUniqueId("owner", owner);
    }

    @Override
    public void readAdditional(@Nonnull final CompoundNBT compound)
    {
        super.readAdditional(compound);
        if (compound.hasUniqueId("owner"))
            this.owner = compound.getUniqueId("owner");
    }
}
