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

package net.minecraftforge.event.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This event is fired during {@link IWorldReader#getCollisionShapes(Entity, VoxelShape, VoxelShape, boolean)}
 * <br>
 * {@link #worldReaderBase} contains the {@link IWorldReaderBase} passed in the method.<br>
 * {@link #pos} contains the {@link BlockPos} passed in the method.<br>
 * {@link #entity} contains the entity passed in the {@link IWorldReader#getCollisionShapes(Entity, VoxelShape, VoxelShape, boolean)} method. <b>Can be null.</b>
 * {@link #shape} contains the {@link VoxelShape} that will be returned. The shape can be modified.<br>
 * <br>
 * This event is not {@link Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult} <br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 * <br>
 * This event is pooled. Do NOT keep any references to it.
 **/
public final class GetCollisionShapeEvent extends Event {

	private static final ThreadLocal<GetCollisionShapeEvent> EVENT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new GetCollisionShapeEvent(null, null, null));

	@Nonnull
	private IWorldReaderBase worldReaderBase;
	@Nonnull
	private BlockPos pos;
	@Nullable
	private Entity entity;

	@Nullable
	private VoxelShape shape;

	private GetCollisionShapeEvent(@Nonnull final IWorldReaderBase worldReaderBase, @Nonnull final BlockPos pos, @Nullable final Entity entity) {
		this.worldReaderBase = worldReaderBase;
		this.pos = pos;
		this.entity = entity;
	}

	/**
	 * Resets the event and then sets its fields to the new values
	 * @deprecated INTERNAL USE ONLY!
	 */
	@Deprecated
	public static GetCollisionShapeEvent getAndReset(final IWorldReaderBase worldReaderBase, final BlockPos pos, final Entity entityIn) {
		final GetCollisionShapeEvent event = EVENT_THREAD_LOCAL.get();

//      event.isCanceled = false; // Not needed because this event is not Cancelable
//      event.result = Result.DEFAULT; // Not needed because this event does not have a result
//		event.phase = null;

		event.worldReaderBase = worldReaderBase;
		event.pos = pos;
		event.entity = entityIn;
		event.shape = null;

		return event;
	}

	@Nullable
	public Entity getEntity() {
		return entity;
	}

	@Nonnull
	public IWorldReaderBase getWorldReaderBase() {
		return worldReaderBase;
	}

	@Nonnull
	public BlockPos getPos() {
		return pos;
	}

	/**
	 * @deprecated Use combineShape or clearShape instead
	 */
	@Nullable
	@Deprecated
	public VoxelShape getShape() {
		return shape;
	}

	@Nonnull
	public VoxelShape combineShape(@Nonnull final VoxelShape other, @Nonnull IBooleanFunction function) {
		if (this.shape == null) {
			this.shape = VoxelShapes.empty();
		}
		VoxelShapes.combine(this.shape, other, function);
		return this.shape;
	}

	@Nonnull
	public VoxelShape clearShape() {
		this.shape = VoxelShapes.empty();
		return this.shape;
	}

}
