package net.minecraftforge.debug;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.EntityBoundingBoxChangeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = EntityBoundingBoxChangeEventTestMod.MOD_ID, name = "Entity Bounding Box Change Event Test Mod", version = "1.0")
@Mod.EventBusSubscriber
public class EntityBoundingBoxChangeEventTestMod {

    public static final String MOD_ID = "ebbcetm";

    @SubscribeEvent
    public static void onBoundingBoxChanged(EntityBoundingBoxChangeEvent e) {
        if (e.getEntity().getDataManager() != null && e.getEntity() instanceof EntityPlayer) {
            if (e.getEntity().isSneaking()) {
                e.newBoundingBox = new AxisAlignedBB(e.newBoundingBox.minX, e.newBoundingBox.minY, e.newBoundingBox.minZ, e.newBoundingBox.maxX, (e.newBoundingBox.minY+1.7999999523162842)-0.4, e.newBoundingBox.maxZ);
            }
        }
    }

}
