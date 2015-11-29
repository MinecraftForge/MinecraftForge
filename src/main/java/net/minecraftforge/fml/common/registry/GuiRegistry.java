package net.minecraftforge.fml.common.registry;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.gui.GuiContainerPair;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.LinkedHashMap;


public class GuiRegistry {

    private final Object instance;
    public LinkedHashMap<String,GuiHandlerMetadata> datas=new LinkedHashMap<String, GuiHandlerMetadata>();

    /***
     * @param modInstance the instance of the creator mod
     */


    public GuiRegistry(Object modInstance){
        this.instance=modInstance;
        NetworkRegistry.INSTANCE.registerGuiHandler(modInstance, new GuiRegistryGuiHandler(modInstance));
    }

    public void registerElement(String id,GuiOperationHandler opHandler,Class<? extends Container> container,Class<? extends Gui> gui){

        datas.put(id, new GuiHandlerMetadata(opHandler, container, gui));


    }

    public void openGui(String key,EntityPlayer player,BlockPos pos,World world) throws Exception {
        int id=getPosOfElement(key,datas);
        if(id==-1)
            throw new Exception(String.format("The object with key '%s' not found",id));
        player.openGui(instance, id, world, pos.getX(), pos.getY(), pos.getZ());
    }



    private int getPosOfElement(Object key,LinkedHashMap map){
        for(int i=0;i<map.keySet().size();i++){
            if(map.keySet().toArray()[i].equals(key))
                return i;
        }
        return -1;
    }



    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
       String key= (String) datas.keySet().toArray()[id];

        return datas.get(key).handler.handle(instance,key,world.getTileEntity(new BlockPos(x,y,z)),player).container;
    }

    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return datas.get(datas.keySet().toArray()[id]).handler.handle(instance, ((String) datas.keySet().toArray()[id]), world.getTileEntity(new BlockPos(x,y,z)),player).gui;
    }

    public class GuiHandlerMetadata {
        public Class<? extends Gui> gui;
        public Class<? extends Container> container;
        private GuiOperationHandler handler;

        public GuiHandlerMetadata( GuiOperationHandler handler,Class<? extends Container> container,Class<? extends Gui> gui) {
            this.gui = gui;
            this.container = container;
            this.handler=handler;
        }



    }



    private class GuiRegistryGuiHandler implements IGuiHandler {


        private final Object modObject;

        public GuiRegistryGuiHandler(Object mod){
            this.modObject=mod;
        }


        @Override
        public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
            return FMLCommonHandler.instance().getGuiRegistry(modObject).getServerGuiElement(ID, player, world, x, y, z);
        }

        @Override
        public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
            return FMLCommonHandler.instance().getGuiRegistry(modObject).getClientGuiElement(ID, player, world, x, y, z);
        }

    }

    public interface GuiOperationHandler {
      GuiContainerPair handle(Object mod,String id,TileEntity tileEntity,EntityPlayer player);
    }
}
