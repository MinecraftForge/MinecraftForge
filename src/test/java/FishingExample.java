
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("fishing_example")
public class FishingExample
{
    public FishingExample()
    {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(FishingExample::handleImpact);
    }

    public static void handleImpact(ProjectileImpactEvent.FishingBobber event)
    {
        FishingBobberEntity bob = event.getFishingBobber();
        RayTraceResult trace = event.getRayTraceResult();
        if (trace.getType() == RayTraceResult.Type.ENTITY)
        {
            Vector3d vector3d = bob.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale(0.6);
            if (vector3d.lengthSqr() > 0.0D)
            {
                Entity entity = ((EntityRayTraceResult) trace).getEntity();
                if (entity instanceof LivingEntity)
                {
                    ((LivingEntity) entity).push(vector3d.x, 0.3D, vector3d.z);
                }
            }
        }
    }
}