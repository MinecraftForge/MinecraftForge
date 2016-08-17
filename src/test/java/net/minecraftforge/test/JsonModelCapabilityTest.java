package net.minecraftforge.test;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientAttachCapabilitiesEvent;
import net.minecraftforge.client.event.VanillaModelWrapperEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid="jsonmodelcapabilitytest", name="JsonModelCapabilityTest", version="0.0.0")
public class JsonModelCapabilityTest
{

    public static boolean tblock;
    
    @CapabilityInject(ComplexTransforms.class)
    private static final Capability<ComplexTransforms> complexTransforms = null;

    @SidedProxy
    static ServerProxy proxy;
    
    private Block testBlock;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        GameRegistry.register(testBlock = new Block(Material.IRON).setRegistryName("jsonmodelcapabilitytest", "testblock").setCreativeTab(CreativeTabs.DECORATIONS));
        GameRegistry.register(new ItemBlock(testBlock).setRegistryName("jsonmodelcapabilitytest", "testblock"));
        MinecraftForge.EVENT_BUS.register(this);
        proxy.preInit(event);
    }

    public static class ServerProxy {
        
        public void preInit(FMLPreInitializationEvent event)
        {
           
        }
        
    }

    public static final class ClientProxy extends ServerProxy
    {
        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            CapabilityManager.INSTANCE.register(ComplexTransforms.class, new ComplexTransforms.Storage(), ComplexTransforms.class);
        }
    }

    @SubscribeEvent
    public void attachCaps(ClientAttachCapabilitiesEvent.BlockPart event){
        event.addCapability(new ResourceLocation("modelcapstest:ctransform"), new ICapabilitySerializable<NBTBase>() {

            ComplexTransforms instance = complexTransforms.getDefaultInstance();

            @Override
            public boolean hasCapability(Capability<?> capability, EnumFacing facing)
            {
                return capability == complexTransforms;
            }

            @Override
            public <T> T getCapability(Capability<T> capability, EnumFacing facing)
            {
                return capability == complexTransforms ? complexTransforms.<T>cast(instance) : null;
            }

            @Override
            public NBTBase serializeNBT()
            {
                return complexTransforms.writeNBT(instance, null);
            }

            @Override
            public void deserializeNBT(NBTBase nbt)
            {
                complexTransforms.readNBT(instance, null, nbt);
            }

        });
    }

    @SubscribeEvent
    public void transform(VanillaModelWrapperEvent.Bake.BlockPart.Pre event){
        if(event.getPart().hasCapability(complexTransforms, null)){
            ComplexTransforms trans = event.getPart().capabilities.getCapability(complexTransforms, null);
            TRSRTransformation compTrans = new TRSRTransformation(TRSRTransformation.toVecmath(new Matrix4f().translate(trans.translation).rotate((float) Math.toRadians(trans.rotation.x), new Vector3f(1, 0, 0)).rotate((float) Math.toRadians(trans.rotation.y), new Vector3f(0, 1, 0)).rotate((float) Math.toRadians(trans.rotation.z), new Vector3f(0, 0, 1)).scale(trans.scale)));
            event.setTransformation(TRSRTransformation.blockCenterToCorner(TRSRTransformation.blockCornerToCenter(event.getTransformation()).compose(compTrans)));
        }
    }

    public static class ComplexTransforms
    {

        private Vector3f translation = new Vector3f(0, 0, 0);
        private Vector3f rotation = new Vector3f(0, 0, 0);
        private Vector3f scale = new Vector3f(1, 1, 1);

        public ComplexTransforms()
        {

        }

        static class Storage implements IStorage<ComplexTransforms> {

            @Override
            public NBTTagCompound writeNBT(Capability<ComplexTransforms> capability, ComplexTransforms instance, EnumFacing side)
            {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setTag("translation", toNBT(instance.translation));
                nbt.setTag("rotation", toNBT(instance.rotation));
                nbt.setTag("scale", toNBT(instance.scale));
                return nbt;
            }

            @Override
            public void readNBT(Capability<ComplexTransforms> capability, ComplexTransforms instance, EnumFacing side, NBTBase base)
            {
                NBTTagCompound nbt = (NBTTagCompound) base;
                if(nbt.hasKey("translation")) instance.translation = fromNBT(nbt.getCompoundTag("translation"));
                if(nbt.hasKey("rotation")) instance.rotation = fromNBT(nbt.getCompoundTag("rotation"));
                if(nbt.hasKey("scale")) instance.scale = fromNBT(nbt.getCompoundTag("scale"));
            }

            public Vector3f fromNBT(NBTTagCompound nbt){
                return new Vector3f(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"));
            }

            public NBTTagCompound toNBT(Vector3f vec){
                if(vec == null) return null;
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setFloat("x", vec.x);
                nbt.setFloat("y", vec.y);
                nbt.setFloat("z", vec.z);
                return nbt;
            }

        }

    }

}
