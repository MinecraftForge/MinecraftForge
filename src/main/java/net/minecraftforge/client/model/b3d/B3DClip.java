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

package net.minecraftforge.client.model.b3d;

import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.b3d.B3DLoader.NodeJoint;
import net.minecraftforge.client.model.b3d.B3DModel.Key;
import net.minecraftforge.client.model.b3d.B3DModel.Node;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.model.TransformationHelper;
import net.minecraftforge.common.model.animation.IClip;
import net.minecraftforge.common.model.animation.IJoint;
import net.minecraftforge.common.model.animation.IJointClip;
import net.minecraftforge.common.model.animation.JointClips;

import com.google.common.collect.ImmutableSet;

// FIXME: is this fast enough?
public enum B3DClip implements IClip
{
    INSTANCE;

    @Override
    public IJointClip apply(final IJoint joint)
    {
        if(!(joint instanceof NodeJoint))
        {
            return JointClips.IdentityJointClip.INSTANCE;
        }
        return new NodeClip(((NodeJoint)joint).getNode());
    }

    @Override
    public Iterable<Event> pastEvents(float lastPollTime, float time)
    {
        return ImmutableSet.of();
    }

    protected static class NodeClip implements IJointClip
    {
        private final Node<?> node;

        public NodeClip(Node<?> node)
        {
            this.node = node;
        }

        @Override
        public TransformationMatrix apply(float time)
        {
            TransformationMatrix ret = TransformationMatrix.identity();
            if(node.getAnimation() == null)
            {
                return ret.compose(new TransformationMatrix(node.getPos(), node.getRot(), node.getScale(), null));
            }
            int start = Math.max(1, (int)Math.round(Math.floor(time)));
            int end = Math.min(start + 1, (int)Math.round(Math.ceil(time)));
            float progress = time - (float)Math.floor(time);
            Key keyStart = node.getAnimation().getKeys().get(start, node);
            Key keyEnd = node.getAnimation().getKeys().get(end, node);
            TransformationMatrix startTr = keyStart == null ? null : new TransformationMatrix(keyStart.getPos(), keyStart.getRot(),keyStart.getScale(), null);
            TransformationMatrix endTr = keyEnd == null ? null : new TransformationMatrix(keyEnd.getPos(), keyEnd.getRot(),keyEnd.getScale(), null);
            if(keyStart == null)
            {
                if(keyEnd == null)
                {
                    ret = ret.compose(new TransformationMatrix(node.getPos(), node.getRot(), node.getScale(), null));
                }
                // TODO animated TRSRTransformation for speed?
                else
                {
                    ret = ret.compose(endTr);
                }
            }
            else if(progress < 1e-5 || keyEnd == null)
            {
                ret = ret.compose(startTr);
            }
            else
            {
                ret = ret.compose(TransformationHelper.slerp(startTr, endTr, progress));
            }
            return ret;
        }
    }
}
