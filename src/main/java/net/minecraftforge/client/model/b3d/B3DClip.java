package net.minecraftforge.client.model.b3d;

import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.animation.Event;
import net.minecraftforge.client.model.animation.IClip;
import net.minecraftforge.client.model.animation.IJoint;
import net.minecraftforge.client.model.animation.IJointClip;
import net.minecraftforge.client.model.animation.JointClips;
import net.minecraftforge.client.model.b3d.B3DLoader.NodeJoint;
import net.minecraftforge.client.model.b3d.B3DModel.Key;
import net.minecraftforge.client.model.b3d.B3DModel.Node;

import com.google.common.collect.ImmutableSet;

// FIXME: is this fast enough?
public enum B3DClip implements IClip
{
    instance;

    public IJointClip apply(final IJoint joint)
    {
        if(!(joint instanceof NodeJoint))
        {
            return JointClips.IdentityJointClip.instance;
        }
        return new NodeClip(((NodeJoint)joint).getNode());
    }

    public Iterable<Event> pastEvents(float lastPollTime, float time)
    {
        return ImmutableSet.<Event>of();
    }

    protected static class NodeClip implements IJointClip
    {
        private final Node<?> node;

        public NodeClip(Node<?> node)
        {
            this.node = node;
        }

        public TRSRTransformation apply(float time)
        {
            TRSRTransformation ret = TRSRTransformation.identity();
            if(node.getAnimation() == null)
            {
                return ret.compose(new TRSRTransformation(node.getPos(), node.getRot(), node.getScale(), null));
            }
            int start = Math.max(1, (int)Math.round(Math.floor(time)));
            int end = Math.min(start + 1, (int)Math.round(Math.ceil(time)));
            float progress = time - (float)Math.floor(time);
            Key keyStart = node.getAnimation().getKeys().get(start, node);
            Key keyEnd = node.getAnimation().getKeys().get(end, node);
            TRSRTransformation startTr = keyStart == null ? null : new TRSRTransformation(keyStart.getPos(), keyStart.getRot(),keyStart.getScale(), null);
            TRSRTransformation endTr = keyEnd == null ? null : new TRSRTransformation(keyEnd.getPos(), keyEnd.getRot(),keyEnd.getScale(), null);
            if(keyStart == null)
            {
                if(keyEnd == null)
                {
                    ret = ret.compose(new TRSRTransformation(node.getPos(), node.getRot(), node.getScale(), null));
                }
                // TODO animated TRSR for speed?
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
                ret = ret.compose(startTr.slerp(endTr, progress));
            }
            return ret;
        }
    }
}
