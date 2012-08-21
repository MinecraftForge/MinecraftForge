package cpw.mods.fml.common.modloader;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class ModLoaderGuiHelper implements IGuiHandler
{

    private BaseModProxy mod;
    private int id;
    private Container container;

    ModLoaderGuiHelper(BaseModProxy mod, int id)
    {
        this.mod = mod;
        this.id = id;
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

    public void injectContainer(Container container)
    {
        this.container = container;
    }

    public Object getMod()
    {
        return mod;
    }

}
