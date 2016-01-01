package net.minecraftforge.client.model.animation;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.animation.ModelBlockAnimation.MBJointInfo;
import net.minecraftforge.common.property.IUnlistedProperty;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;

public class Animation
{
    public static final IUnlistedProperty<IModelState> AnimationProperty = new IUnlistedProperty<IModelState>()
    {
        public String getName() { return "forge_animation"; }
        public boolean isValid(IModelState state) { return true; }
        public Class<IModelState> getType() { return IModelState.class; }
        public String valueToString(IModelState state) { return state.toString(); }
    };

    public static float getWorldTime(World world)
    {
        return getWorldTime(world, 0);
    }

    public static float getWorldTime(World world, float tickProgress)
    {
        return (world.getTotalWorldTime() + tickProgress) / 20;
    }

    private static final AnimationStateMachine missing = new AnimationStateMachine(
        ImmutableMap.of("missingno", (IClip)Clips.IdentityClip.instance),
        ImmutableList.of("missingno"),
        ImmutableTable.<IClip, IClip, IClipProvider>of(),
        "missingno");

    /**
     * Entry point for loading animation state machines.
     */
    public static AnimationStateMachine load(ResourceLocation location, ImmutableMap<String, IParameter> customParameters)
    {
        // hardcoded test case for now, JSON later

        if(location.equals(new ResourceLocation("forgedebugmodelanimation", "afsm/block/chest")))
        {
            IClip b3d = Clips.getModelClipNode(new ResourceLocation("forgedebugmodelloaderregistry", "block/chest.b3d"), "main");
            IClip closed = new Clips.TimeClip(b3d, new Parameters.ConstParameter(0));
            IClip open = new Clips.TimeClip(b3d, new Parameters.ConstParameter(10));
            IClipProvider c2o = Clips.slerpFactory(closed, open, Parameters.NoopParameter.instance, 1);
            IClipProvider o2c = Clips.slerpFactory(open, closed, Parameters.NoopParameter.instance, 1);

            ImmutableTable.Builder<IClip, IClip, IClipProvider> builder = ImmutableTable.builder();
            builder.put(closed, open, c2o);
            builder.put(open, closed, o2c);

            return new AnimationStateMachine(
                ImmutableMap.of("closed", closed, "open", open),
                ImmutableList.of("closed", "open"),
                builder.build(),
                "closed"
            );
        }
        else if(location.equals(new ResourceLocation("forgedebugmodelanimation", "afsm/block/engine")))
        {
            final IParameter worldToCycle = customParameters.getOrDefault("worldToCycle", Parameters.NoopParameter.instance);
            final IParameter roundCycle = customParameters.getOrDefault("roundCycle", Parameters.NoopParameter.instance);

            final IClip default_ = Clips.getModelClipNode(new ResourceLocation("forgedebugmodelanimation", "block/engine_ring"), "default");
            IClip movingTmp = Clips.getModelClipNode(new ResourceLocation("forgedebugmodelanimation", "block/engine_ring"), "moving");
            final IClip moving = new Clips.TimeClip(movingTmp, worldToCycle);

            IClipProvider d2m = Clips.createClipLength(default_, roundCycle);
            IClipProvider m2d = Clips.createClipLength(moving, roundCycle);

            ImmutableTable.Builder<IClip, IClip, IClipProvider> builder = ImmutableTable.builder();
            builder.put(default_, moving, d2m);
            builder.put(moving, default_, m2d);

            return new AnimationStateMachine(
                ImmutableMap.of("default", default_, "moving", moving),
                ImmutableList.of("default", "moving"),
                builder.build(),
                "moving"
            );
        }
        else return missing;
    }

    /**
     * Entry point for loading animation tracks associated with vanilla json models.
     */
    public static ModelBlockAnimation loadVanillaAnimation(ResourceLocation armatureLocation)
    {
        // TODO json

        ImmutableMap<String, MBJointInfo> joints = ImmutableMap.of();
        ImmutableMap<String, Optional<ModelBlockAnimation.MBClip>> clips = ImmutableMap.of();

        if(armatureLocation.getResourcePath().endsWith("engine_ring"))
        {
            ModelBlockAnimation.MBJointInfo ring = new ModelBlockAnimation.MBJointInfo("ring", ImmutableMap.of(
                0, new float[]{ 1 }
            ));
            ModelBlockAnimation.MBJointInfo chamber = new ModelBlockAnimation.MBJointInfo("chamber", ImmutableMap.of(
                1, new float[]{ 1 }
            ));
            ModelBlockAnimation.MBJointInfo trunk = new ModelBlockAnimation.MBJointInfo("trunk", ImmutableMap.of(
                2, new float[]{ 1 }
            ));
            joints = ImmutableMap.of(
                "ring", ring,
                "chamber", chamber,
                "trunk", trunk
            );
            ModelBlockAnimation.MBClip moving = new ModelBlockAnimation.MBClip(ImmutableList.of(
                new ModelBlockAnimation.MBJointClip(ring, true, ImmutableList.of(
                    new ModelBlockAnimation.MBVariableClip(
                        ModelBlockAnimation.Parameter.Variable.Y,
                        ModelBlockAnimation.Parameter.Type.Uniform,
                        ModelBlockAnimation.Parameter.Interpolation.Linear,
                        new float[]{ .00f, .08f, .25f, .42f, .50f, .42f, .25f, .08f }
                    ),
                    new ModelBlockAnimation.MBVariableClip(
                        ModelBlockAnimation.Parameter.Variable.YR,
                        ModelBlockAnimation.Parameter.Type.Uniform,
                        ModelBlockAnimation.Parameter.Interpolation.Nearest,
                        new float[]{ 1 }
                    ),
                    new ModelBlockAnimation.MBVariableClip(
                        ModelBlockAnimation.Parameter.Variable.AR,
                        ModelBlockAnimation.Parameter.Type.Uniform,
                        ModelBlockAnimation.Parameter.Interpolation.Linear,
                        new float[]{ 0, 120, 240, 0, 120, 240, 0, 120, 240, 0, 120, 240 }
                    )
                )),
                /*new ModelBlockAnimation.MBJointClip(chamber, true, ImmutableList.of(
                    new ModelBlockAnimation.MBVariableClip(
                        ModelBlockAnimation.Parameter.Variable.Y,
                        ModelBlockAnimation.Parameter.Type.Uniform,
                        ModelBlockAnimation.Parameter.Interpolation.Nearest,
                        new float[]{ .0f, .5f }
                    )
                )),
                new ModelBlockAnimation.MBJointClip(trunk, true, ImmutableList.of(
                    new ModelBlockAnimation.MBVariableClip(
                        ModelBlockAnimation.Parameter.Variable.Y,
                        ModelBlockAnimation.Parameter.Type.Uniform,
                        ModelBlockAnimation.Parameter.Interpolation.Nearest,
                        new float[]{ .0f, -.5f }
                    )
                )),*/
                new ModelBlockAnimation.MBJointClip(chamber, true, ImmutableList.of(
                    new ModelBlockAnimation.MBVariableClip(
                        ModelBlockAnimation.Parameter.Variable.SCALE,
                        ModelBlockAnimation.Parameter.Type.Uniform,
                        ModelBlockAnimation.Parameter.Interpolation.Nearest,
                        new float[]{ 0, 1 }
                    )
                )),
                new ModelBlockAnimation.MBJointClip(trunk, true, ImmutableList.of(
                    new ModelBlockAnimation.MBVariableClip(
                        ModelBlockAnimation.Parameter.Variable.SCALE,
                        ModelBlockAnimation.Parameter.Type.Uniform,
                        ModelBlockAnimation.Parameter.Interpolation.Nearest,
                        new float[]{ 1, 0 }
                    )
                ))
            ));

            clips = ImmutableMap.<String, Optional<ModelBlockAnimation.MBClip>>of(
                "default", Optional.<ModelBlockAnimation.MBClip>absent(),
                "moving", Optional.<ModelBlockAnimation.MBClip>of(moving)
            );
        }
        ModelBlockAnimation mba = new ModelBlockAnimation(
            new ResourceLocation("forgedebugmodelanimation", "engine"),
            joints,
            clips
        );

        return mba;
    }
}
