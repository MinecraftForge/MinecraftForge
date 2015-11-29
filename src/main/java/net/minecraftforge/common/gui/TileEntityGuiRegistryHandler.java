package net.minecraftforge.common.gui;


import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GuiRegistry;

import java.lang.reflect.InvocationTargetException;

import static net.minecraftforge.fml.common.registry.GuiRegistry.GuiOperationHandler;
import static net.minecraftforge.fml.common.registry.GuiRegistry.getGuiRegistry;

public class TileEntityGuiRegistryHandler implements GuiOperationHandler{

    public static final TileEntityGuiRegistryHandler instance= new TileEntityGuiRegistryHandler();

    @Override
    public GuiContainerPair handle(Object mod, String id, TileEntity tileEntity, EntityPlayer player) {
        GuiRegistry.GuiHandlerMetadata gconpair= getGuiRegistry().getGuiRegistry(mod).datas.get(id);
        try {
            Container container=null;
            Gui gui=null;
            container= (Container) gconpair.container.getConstructor(new Class[]{TileEntity.class, EntityPlayer.class}).newInstance(tileEntity,player);
            gui= (Gui) gconpair.gui.getConstructor(new Class[]{Container.class}).newInstance(container);
            GuiContainerPair pair;
            pair = new GuiContainerPair(container,gui);
            return pair;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();

            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static abstract class TileEntityContainer extends Container{
             public TileEntityContainer(TileEntity tile, EntityPlayer player) {
             }
     }

    public static abstract class TileEntityGui extends GuiContainer{
                public TileEntityGui(Container container) {
                        super(container);
                }
    }


}
