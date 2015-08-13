package net.minecraftforge.client.model;

import java.util.Map;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * IModelState that can change depending on the perspective.
 */
public interface IPerspectiveState extends IModelState
{
    /**
     * @return the additional state that needs to be applied for each part when in given perspective type.
     */
    public IModelState forPerspective(TransformType type);

    public static class Impl implements IPerspectiveState
    {
        private final IModelState parent;
        private final ImmutableMap<TransformType, IModelState> states;

        public Impl(IModelState parent, ImmutableMap<TransformType, IModelState> states)
        {
            this.parent = parent;
            this.states = states;
        }

        public Impl(IModelState parent, ItemCameraTransforms transforms)
        {
            this(parent, getMap(transforms));
        }

        private static ImmutableMap<TransformType, IModelState> getMap(ItemCameraTransforms transforms)
        {
            Map<TransformType, IModelState> map = Maps.newHashMap();
            map.put(TransformType.NONE, TRSRTransformation.identity());
            map.put(TransformType.THIRD_PERSON, transforms.thirdPerson);
            map.put(TransformType.FIRST_PERSON, transforms.firstPerson);
            map.put(TransformType.GUI, transforms.gui);
            map.put(TransformType.HEAD, transforms.head);
            return Maps.immutableEnumMap(map);
        }

        public TRSRTransformation apply(IModelPart part)
        {
            return parent.apply(part);
        }

        public IModelState forPerspective(TransformType type)
        {
            IModelState state = states.get(type);
            if(state == null) state = TRSRTransformation.identity();
            return state;
        }

        @Override
        public String toString()
        {
            return Objects.toStringHelper(this.getClass()).add("parent", parent).add("states", states).toString();
        }
    }
}
