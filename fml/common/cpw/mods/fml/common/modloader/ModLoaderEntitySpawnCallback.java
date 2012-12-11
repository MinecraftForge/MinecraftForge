package cpw.mods.fml.common.modloader;

import java.util.concurrent.Callable;

import net.minecraft.entity.Entity;

import com.google.common.base.Function;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

public class ModLoaderEntitySpawnCallback implements Function<EntitySpawnPacket, Entity>
{

    private BaseModProxy mod;
    private EntityRegistration registration;
    private boolean isAnimal;

    public ModLoaderEntitySpawnCallback(BaseModProxy mod, EntityRegistration er)
    {
        this.mod = mod;
        this.registration = er;
    }

    @Override
    public Entity apply(EntitySpawnPacket input)
    {
        return ModLoaderHelper.sidedHelper.spawnEntity(mod, input, registration);
    }
}
