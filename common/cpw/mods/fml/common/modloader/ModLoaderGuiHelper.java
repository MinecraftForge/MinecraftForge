package cpw.mods.fml.common.modloader;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.network.IGuiHandler;

public class ModLoaderGuiHelper implements IGuiHandler
{

    private BaseModProxy mod;
    private Set<Integer> ids;
    private Container container;
    private int currentID;

    ModLoaderGuiHelper(BaseModProxy mod)
    {
        this.mod = mod;
        this.ids = Sets.newHashSet();
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return container;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return ModLoaderHelper.getClientSideGui(mod, player, ID, x, y, z);
    }

    public void injectContainerAndID(Container container, int ID)
    {
        this.container = container;
        this.currentID = ID;
    }

    public Object getMod()
    {
        return mod;
    }

    public void associateId(int additionalID)
    {
        this.ids.add(additionalID);
    }

}
