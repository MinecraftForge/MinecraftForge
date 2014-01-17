package cpw.mods.fml.common.network.internal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import org.apache.logging.log4j.Level;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

import com.google.common.base.Throwables;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.internal.FMLMessage.EntityAdjustMessage;
import cpw.mods.fml.common.network.internal.FMLMessage.EntityMessage;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;

public class EntitySpawnHandler extends SimpleChannelInboundHandler<FMLMessage.EntityMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, EntityMessage msg) throws Exception
    {
        if (msg.getClass().equals(FMLMessage.EntitySpawnMessage.class))
        {
            spawnEntity((FMLMessage.EntitySpawnMessage)msg);
        }
        else if (msg.getClass().equals(FMLMessage.EntityAdjustMessage.class))
        {
            adjustEntity((FMLMessage.EntityAdjustMessage)msg);
        }
    }

    private void adjustEntity(EntityAdjustMessage msg)
    {
        Entity ent = FMLClientHandler.instance().getWorldClient().func_73045_a(msg.entityId);
        if (ent != null)
        {
            ent.field_70118_ct = msg.serverX;
            ent.field_70117_cu = msg.serverY;
            ent.field_70116_cv = msg.serverZ;
        }
        else
        {
            FMLLog.fine("Attempted to adjust the position of entity %d which is not present on the client", msg.entityId);
        }

    }

    private void spawnEntity(FMLMessage.EntitySpawnMessage spawnMsg)
    {
        System.out.println("Spawning entity on client");
        ModContainer mc = Loader.instance().getIndexedModList().get(spawnMsg.modId);
        EntityRegistration er = EntityRegistry.instance().lookupModSpawn(mc, spawnMsg.modEntityTypeId);
        WorldClient wc = FMLClientHandler.instance().getWorldClient();
        Class<? extends Entity> cls = er.getEntityClass();
        try
        {
            Entity entity;
            if (er.hasCustomSpawning())
            {
                entity = er.doCustomSpawning(spawnMsg);
            } else
            {
                entity = (Entity) (cls.getConstructor(World.class).newInstance(wc));

                int offset = spawnMsg.entityId - entity.func_145782_y();
                entity.func_145769_d(spawnMsg.entityId);
                entity.func_70012_b(spawnMsg.scaledX, spawnMsg.scaledY, spawnMsg.scaledZ, spawnMsg.scaledYaw, spawnMsg.scaledPitch);
                if (entity instanceof EntityLiving)
                {
                    ((EntityLiving) entity).field_70759_as = spawnMsg.scaledHeadYaw;
                }

                Entity parts[] = entity.func_70021_al();
                if (parts != null)
                {
                    for (int j = 0; j < parts.length; j++)
                    {
                        parts[j].func_145769_d(parts[j].func_145782_y() + offset);
                    }
                }
            }

            entity.field_70118_ct = spawnMsg.rawX;
            entity.field_70117_cu = spawnMsg.rawY;
            entity.field_70116_cv = spawnMsg.rawZ;

            EntityClientPlayerMP clientPlayer = FMLClientHandler.instance().getClientPlayerEntity();
            if (entity instanceof IThrowableEntity)
            {
                Entity thrower = clientPlayer.func_145782_y() == spawnMsg.throwerId ? clientPlayer : wc.func_73045_a(spawnMsg.throwerId);
                ((IThrowableEntity) entity).setThrower(thrower);
            }

            if (spawnMsg.dataWatcherList != null)
            {
                entity.func_70096_w().func_75687_a((List<?>) spawnMsg.dataWatcherList);
            }

            if (spawnMsg.throwerId > 0)
            {
                entity.func_70016_h(spawnMsg.speedScaledX, spawnMsg.speedScaledY, spawnMsg.speedScaledZ);
            }

            if (entity instanceof IEntityAdditionalSpawnData)
            {
                ((IEntityAdditionalSpawnData) entity).readSpawnData(spawnMsg.dataStream);
            }
            wc.func_73027_a(spawnMsg.entityId, entity);
            System.out.println("Entity spawned on client");
        } catch (Exception e)
        {
            FMLLog.log(Level.ERROR, e, "A severe problem occurred during the spawning of an entity");
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "EntitySpawnHandler exception");
        super.exceptionCaught(ctx, cause);
    }
}
