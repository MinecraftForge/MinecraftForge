package net.minecraftforge.fmp.multipart;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * A general use occlusion helper, with methods to check part-part occlusion, part-AABB occlusion, as well as slot
 * occlusion.
 *
 * @see IMultipart
 * @see Multipart
 * @see INormallyOccludingPart
 * @see NormallyOccludingPart
 */
public class OcclusionHelper
{
    
    private static final NormallyOccludingPart NOP = new NormallyOccludingPart();

    /**
     * Performs the default occlusion test between two {@link IMultipart}s.
     */
    public static boolean defaultOcclusionTest(IMultipart part1, IMultipart part2)
    {
        if (part1 instanceof INormallyOccludingPart && part2 instanceof INormallyOccludingPart)
        {
            List<AxisAlignedBB> boxes1 = new ArrayList<AxisAlignedBB>();
            List<AxisAlignedBB> boxes2 = new ArrayList<AxisAlignedBB>();
            ((INormallyOccludingPart) part1).addOcclusionBoxes(boxes1);
            ((INormallyOccludingPart) part2).addOcclusionBoxes(boxes2);

            for (AxisAlignedBB a : boxes1)
            {
                for (AxisAlignedBB b : boxes2)
                {
                    if (a.intersectsWith(b))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Performs an occlusion test between the two specified parts.
     *
     * @param part
     *            The part to test.
     * @param other
     *            The part to test against.
     * @return True if they don't occupy the same space, false if they do.
     */
    public static boolean occlusionTest(IMultipart part, IMultipart other)
    {
        return part.occlusionTest(other) && other.occlusionTest(part);
    }

    /**
     * Performs an occlusion test between a part and the specified collection of parts.
     *
     * @param part
     *            The part to test.
     * @param others
     *            The parts to test against.
     * @return True if they don't occupy the same space, false if they do.
     */
    public static boolean occlusionTest(IMultipart part, IMultipart... others)
    {
        return occlusionTest(part, (Predicate<IMultipart>) null, others);
    }

    /**
     * Performs an occlusion test between a part and the specified collection of parts.
     *
     * @param part
     *            The part to test.
     * @param others
     *            The parts to test against.
     * @return True if they don't occupy the same space, false if they do.
     */
    public static boolean occlusionTest(IMultipart part, Iterable<? extends IMultipart> others)
    {
        return occlusionTest(part, null, others);
    }

    /**
     * Performs an occlusion test between a part and the specified multipart container.
     *
     * @param part
     *            The part to test.
     * @param container
     *            The multipart container to test against.
     * @return True if they don't occupy the same space, false if they do.
     */
    public static boolean occlusionTest(IMultipart part, IMultipartContainer container)
    {
        return container.occlusionTest(part);
    }

    /**
     * Performs an occlusion test between a part and the specified collection of parts, ignoring some of them.
     *
     * @param part
     *            The part to test.
     * @param ignored
     *            The parts to ignore.
     * @param others
     *            The parts to test against.
     * @return True if they don't occupy the same space, false if they do.
     */
    public static boolean occlusionTest(IMultipart part, Predicate<IMultipart> ignored, IMultipart... others)
    {
        for (IMultipart other : others)
        {
            if ((ignored == null || !ignored.apply(other)) && !occlusionTest(part, other))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Performs an occlusion test between a part and the specified collection of parts, ignoring some of them.
     *
     * @param part
     *            The part to test.
     * @param ignored
     *            The parts to ignore.
     * @param others
     *            The parts to test against.
     * @return True if they don't occupy the same space, false if they do.
     */
    public static boolean occlusionTest(IMultipart part, Predicate<IMultipart> ignored, Iterable<? extends IMultipart> others)
    {
        for (IMultipart other : others)
        {
            if ((ignored == null || !ignored.apply(other)) && !occlusionTest(part, other))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Performs an occlusion test between a part and the specified multipart container, ignoring some parts.
     *
     * @param part
     *            The part to test.
     * @param ignored
     *            The parts to ignore.
     * @param container
     *            The multipart container to test against.
     * @return True if they don't occupy the same space, false if they do.
     */
    public static boolean occlusionTest(IMultipart part, Predicate<IMultipart> ignored, IMultipartContainer container)
    {
        List<IMultipart> ignoredParts = new ArrayList<IMultipart>();
        for (IMultipart p : container.getParts())
        {
            if (ignored.apply(p))
            {
                ignoredParts.add(p);
            }
        }
        return container.occlusionTest(part, ignoredParts.toArray(new IMultipart[ignoredParts.size()]));
    }

    /**
     * Performs an occlusion test between a slot and the specificed collection of parts.
     *
     * @param slot
     *            The slot to test.
     * @param parts
     *            The parts to test against.
     * @return True if the slot isn't occupied, false if it is.
     */
    public static boolean slotOcclusionTest(PartSlot slot, IMultipart... parts)
    {
        return slotOcclusionTest(slot, null, parts);
    }

    /**
     * Performs an occlusion test between a slot and the specificed collection of parts.
     *
     * @param slot
     *            The slot to test.
     * @param parts
     *            The parts to test against.
     * @return True if the slot isn't occupied, false if it is.
     */
    public static boolean slotOcclusionTest(PartSlot slot, Iterable<? extends IMultipart> parts)
    {
        return slotOcclusionTest(slot, null, parts);
    }

    /**
     * Performs an occlusion test between a slot and the specificed multipart container.
     *
     * @param slot
     *            The slot to test.
     * @param container
     *            The multipart container to test against.
     * @return True if the slot isn't occupied, false if it is.
     */
    public static boolean slotOcclusionTest(PartSlot slot, IMultipartContainer container)
    {
        return slotOcclusionTest(slot, null, container.getParts());
    }

    /**
     * Performs an occlusion test between a slot and the specificed collection of parts, ignoring some parts.
     *
     * @param slot
     *            The slot to test.
     * @param ignored
     *            The parts to ignore.
     * @param parts
     *            The parts to test against.
     * @return True if the slot isn't occupied, false if it is.
     */
    public static boolean slotOcclusionTest(PartSlot slot, Predicate<IMultipart> ignored, IMultipart... parts)
    {
        for (IMultipart part : parts)
        {
            if ((ignored == null || !ignored.apply(part)) && ((part instanceof ISlottedPart &&
                    ((ISlottedPart) part).getSlotMask().contains(slot)) || part instanceof ISlotOccludingPart
                    && ((ISlotOccludingPart) part).getOccludedSlots().contains(slot)))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Performs an occlusion test between a slot and the specificed collection of parts, ignoring some parts.
     *
     * @param slot
     *            The slot to test.
     * @param ignored
     *            The parts to ignore.
     * @param parts
     *            The parts to test against.
     * @return True if the slot isn't occupied, false if it is.
     */
    public static boolean slotOcclusionTest(PartSlot slot, Predicate<IMultipart> ignored, Iterable<? extends IMultipart> parts)
    {
        for (IMultipart part : parts)
        {
            if ((ignored == null || !ignored.apply(part)) && ((part instanceof ISlottedPart &&
                    ((ISlottedPart) part).getSlotMask().contains(slot)) || part instanceof ISlotOccludingPart
                    && ((ISlotOccludingPart) part).getOccludedSlots().contains(slot)))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Performs an occlusion test between a slot and the specificed multipart container, ignoring some parts.
     *
     * @param slot
     *            The slot to test.
     * @param ignored
     *            The parts to ignore.
     * @param container
     *            The multipart container to test against.
     * @return True if the slot isn't occupied, false if it is.
     */
    public static boolean slotOcclusionTest(PartSlot slot, Predicate<IMultipart> ignored, IMultipartContainer container)
    {
        return slotOcclusionTest(slot, ignored, container.getParts());
    }

    /**
     * Creates a {@link Predicate} that returns true for the specified parts and false for any others.
     *
     * @param parts
     *            The parts to be ignored.
     * @return A {@link Predicate} that only returns true for the specified parts.
     */
    public static Predicate<IMultipart> ignore(final IMultipart... parts)
    {
        return new Predicate<IMultipart>()
        {
            
            @Override
            public boolean apply(IMultipart input)
            {
                for (IMultipart p : parts)
                {
                    if (input == p)
                    {
                        return true;
                    }
                }
                return false;
            }

        };
    }

    /**
     * Creates a {@link Predicate} that returns true for the specified parts and false for any others.
     *
     * @param parts
     *            The parts to be ignored.
     * @return A {@link Predicate} that only returns true for the specified parts.
     */
    public static Predicate<IMultipart> ignore(final Iterable<IMultipart> parts)
    {
        return new Predicate<IMultipart>()
        {
            
            @Override
            public boolean apply(IMultipart input)
            {
                for (IMultipart p : parts)
                {
                    if (input == p)
                    {
                        return true;
                    }
                }
                return false;
            }

        };
    }

    /**
     * Returns a {@link NormallyOccludingPart} that occludes the specified boxes. <b>The output of this method is not
     * meant to be cached</b> , since every time the method is called the boxes that the object occupies will change. If
     * you need to cache the result, create your own instance of {@link NormallyOccludingPart} by using the constructor.
     *
     * @param boxes
     *            The boxes to be occluded.
     * @return An instance of {@link NormallyOccludingPart} that occludes the specified boxes.
     */
    public static NormallyOccludingPart boxes(AxisAlignedBB... boxes)
    {
        return NOP.setBoxes(boxes);
    }

    /**
     * Returns a {@link NormallyOccludingPart} that occludes the specified boxes. <b>The output of this method is not
     * meant to be cached</b> , since every time the method is called the boxes that the object occupies will change. If
     * you need to cache the result, create your own instance of {@link NormallyOccludingPart} by using the constructor.
     *
     * @param boxes
     *            The boxes to be occluded.
     * @return An instance of {@link NormallyOccludingPart} that occludes the specified boxes.
     */
    public static NormallyOccludingPart boxes(Iterable<AxisAlignedBB> boxes)
    {
        return NOP.setBoxes(boxes);
    }

    /**
     * An {@link INormallyOccludingPart} that occludes a certain set of {@link AxisAlignedBB}s.
     *
     * @see IMultipart
     * @see Multipart
     * @see IMultipartContainer
     */
    public static class NormallyOccludingPart extends Multipart implements INormallyOccludingPart
    {
        
        private AxisAlignedBB[] boxArray;
        private Iterable<AxisAlignedBB> boxIterable;

        public NormallyOccludingPart(Iterable<AxisAlignedBB> boxes)
        {
            this.boxIterable = boxes;
        }

        public NormallyOccludingPart(AxisAlignedBB... boxes)
        {
            this.boxArray = boxes;
        }

        @Override
        public ResourceLocation getType()
        {
            return null;
        }

        @Override
        public void addOcclusionBoxes(List<AxisAlignedBB> list)
        {
            if (boxArray != null)
            {
                for (AxisAlignedBB bb : boxArray)
                {
                    list.add(bb);
                }
            }
            if (boxIterable != null)
            {
                for (AxisAlignedBB bb : boxIterable)
                {
                    list.add(bb);
                }
            }
        }

        public NormallyOccludingPart setBoxes(Iterable<AxisAlignedBB> boxes)
        {
            this.boxIterable = boxes;
            this.boxArray = null;
            return this;
        }

        public NormallyOccludingPart setBoxes(AxisAlignedBB... boxes)
        {
            this.boxIterable = null;
            this.boxArray = boxes;
            return this;
        }

    }

}
