package net.minecraftforge.test;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.BakeBlockPartFaceEvent;
import net.minecraftforge.client.event.ClientAttachCapabilitiesEvent;
import net.minecraftforge.client.event.VanillaModelWrapperEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
@Mod(modid="jsonmodelcapabilitytest", name="JsonModelCapabilityTest", version="0.0.0")
public class JsonModelCapabilityTest
{

    public static boolean tblock;

    @CapabilityInject(Include.class)
    private static final Capability<Include> include = null;

    @CapabilityInject(UVLightmap.class)
    private static final Capability<UVLightmap> uvlightmap = null;

    @SidedProxy
    static ServerProxy proxy;

    private static Block testBlock;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        testBlock = new Block(Material.IRON){

            public boolean isOpaqueCube(IBlockState state) {
                return false;
            }

            public BlockRenderLayer getBlockLayer(){
                return BlockRenderLayer.CUTOUT;
            }

        }.setRegistryName("jsonmodelcapabilitytest", "testblock").setCreativeTab(CreativeTabs.DECORATIONS);
        MinecraftForge.EVENT_BUS.register(this);
        proxy.preInit(event);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event){
        event.getRegistry().register(testBlock);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(new ItemBlock(testBlock).setRegistryName("jsonmodelcapabilitytest", "testblock"));
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
            CapabilityManager.INSTANCE.register(Include.class, new Include.Storage(), Include.class);
            CapabilityManager.INSTANCE.register(UVLightmap.class, new UVLightmap.Storage(), UVLightmap.class);
        }
    }

    @SubscribeEvent
    public void attachCapsModel(ClientAttachCapabilitiesEvent.ModelBlock event){
        event.addCapability(new ResourceLocation("test:include"), new ICapabilitySerializable<NBTBase>() {

            Include instance = include.getDefaultInstance();

            @Override
            public boolean hasCapability(Capability<?> capability, EnumFacing facing)
            {
                return capability == include;
            }

            @Override
            public <T> T getCapability(Capability<T> capability, EnumFacing facing)
            {
                return capability == include ? include.<T>cast(instance) : null;
            }

            @Override
            public NBTBase serializeNBT()
            {
                return include.writeNBT(instance, null);
            }

            @Override
            public void deserializeNBT(NBTBase nbt)
            {
                include.readNBT(instance, null, nbt);
            }

        });
    }

    @SubscribeEvent
    public void attachCapsBlockPartFace(ClientAttachCapabilitiesEvent.BlockPartFace event){
        event.addCapability(new ResourceLocation("test:uvlightmap"), new ICapabilitySerializable<NBTBase>() {

            UVLightmap instance = uvlightmap.getDefaultInstance();

            @Override
            public boolean hasCapability(Capability<?> capability, EnumFacing facing)
            {
                return capability == uvlightmap;
            }

            @Override
            public <T> T getCapability(Capability<T> capability, EnumFacing facing)
            {
                return capability == uvlightmap ? uvlightmap.<T>cast(instance) : null;
            }

            @Override
            public NBTBase serializeNBT()
            {
                return uvlightmap.writeNBT(instance, null);
            }

            @Override
            public void deserializeNBT(NBTBase nbt)
            {
                uvlightmap.readNBT(instance, null, nbt);
            }

        });
    }

    @SubscribeEvent
    public void uvlightmap(BakeBlockPartFaceEvent event){
        if(event.getFace().hasCapability(uvlightmap, null)){
            final UVLightmap uvlightmap = event.getFace().getCapability(JsonModelCapabilityTest.uvlightmap, null);
            if(uvlightmap.block != -1 || uvlightmap.sky != -1){
                BakedQuad quad = event.getQuad();
                VertexFormat format = new VertexFormat(quad.getFormat());
                if(!format.getElements().contains( DefaultVertexFormats.TEX_2S )){
                    format.addElement(DefaultVertexFormats.TEX_2S);
                }
                UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder( format );
                VertexLighterFlat trans = new VertexLighterFlat( Minecraft.getMinecraft().getBlockColors() ){

                    @Override
                    protected void updateLightmap( float[] normal, float[] lightmap, float x, float y, float z )
                    {
                        lightmap[0] = uvlightmap.block;
                        lightmap[1] = uvlightmap.sky;
                    }

                };
                trans.setParent( builder );
                quad.pipe( trans );
                builder.setQuadOrientation( quad.getFace() );
                event.setQuad(builder.build());
            }
        }
    }

    @SubscribeEvent
    public void include(VanillaModelWrapperEvent.Bake.Model.Post event){
        if(event.getModel().hasCapability(include, null)){
            Include trans = event.getModel().getCapability(include, null);
            for(Pair<String, ComplexTransforms> include : trans.include){
                try {
                    TRSRTransformation compTrans = new TRSRTransformation(TRSRTransformation.toVecmath(include.getRight().getMat()));
                    IModel model = ModelLoaderRegistry.getModel(new ResourceLocation(include.getLeft()));
                    IBakedModel baked = model.bake(TRSRTransformation.blockCenterToCorner(TRSRTransformation.blockCornerToCenter(event.getBaseState()).compose(compTrans)), event.getFormat(), event.getBakedTextureGetter());
                    for(BakedQuad quad : baked.getQuads(null, null, 0)){
                        event.getBuilder().addGeneralQuad(quad);
                    }
                    for(EnumFacing face : EnumFacing.values()){
                        for(BakedQuad quad : baked.getQuads(null, face, 0)){
                            event.getBuilder().addFaceQuad(face, quad);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class UVLightmap {

        float block = -1;
        float sky = -1;

        static class Storage implements IStorage<UVLightmap> {

            @Override
            public NBTBase writeNBT(Capability<UVLightmap> capability, UVLightmap instance, EnumFacing side)
            {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setFloat("block", instance.block);
                nbt.setFloat("sky", instance.sky);
                return nbt;
            }

            @Override
            public void readNBT(Capability<UVLightmap> capability, UVLightmap instance, EnumFacing side, NBTBase b)
            {
                NBTTagCompound nbt = (NBTTagCompound) b;
                instance.block = nbt.getFloat("block");
                instance.sky = nbt.getFloat("sky");
            }

        }

    }

    public static class Include {

        List<Pair<String, ComplexTransforms>> include = new ArrayList<Pair<String,ComplexTransforms>>();

        static class Storage implements IStorage<Include> {

            @Override
            public NBTBase writeNBT(Capability<Include> capability, Include instance, EnumFacing side)
            {
                NBTTagList list = new NBTTagList();
                for(Pair<String, ComplexTransforms> pair : instance.include){
                    NBTTagCompound tag = new NBTTagCompound();
                    tag.setString("model", pair.getLeft());
                    tag.setTag("transform", pair.getRight().writeToNBT());
                    list.appendTag(tag);
                }
                return list;
            }

            @Override
            public void readNBT(Capability<Include> capability, Include instance, EnumFacing side, NBTBase nbt)
            {
                NBTTagList list = (NBTTagList) nbt;
                for(int i = 0; i < list.tagCount(); i++){
                    instance.include.add(new ImmutablePair<String, JsonModelCapabilityTest.ComplexTransforms>(list.getCompoundTagAt(i).getString("model"), new ComplexTransforms().readFromNBT(list.getCompoundTagAt(i).getTagList("transform", 10))));
                }
            }

        }

    }

    public static class ComplexTransforms
    {

        List<Transform> transforms = new ArrayList<Transform>();

        public Matrix4f getMat(){
            Matrix4f mat = new Matrix4f();
            for(Transform transform : transforms) mat = Matrix4f.mul(mat, transform.getMat(), null);
            return mat;
        }

        public NBTTagList writeToNBT()
        {
            NBTTagList list = new NBTTagList();
            for(Transform transform : transforms){
                list.appendTag(transform.writeToNBT());
            }
            return list;
        }

        public ComplexTransforms readFromNBT(NBTTagList list)
        {
            for(int i = 0; i < list.tagCount(); i++){
                transforms.add(new Transform().readFromNBT(list.getCompoundTagAt(i)));
            }
            return this;
        }

        static class Transform {

            private Vector3f translation = null;
            private Vector3f rotation = null;
            private Vector3f scale = null;

            public Matrix4f getMat(){
                Matrix4f mat = new Matrix4f();
                if(translation != null) mat.translate(translation);
                if(rotation != null) mat.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0)).rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0)).rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
                if(scale != null) mat.scale(scale);
                return mat;
            }

            public NBTTagCompound writeToNBT()
            {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setTag("translation", toNBT(translation));
                nbt.setTag("rotation", toNBT(rotation));
                nbt.setTag("scale", toNBT(scale));
                return nbt;
            }

            public Transform readFromNBT(NBTTagCompound nbt)
            {
                if(nbt.hasKey("translation")) translation = fromNBT(nbt.getCompoundTag("translation"));
                if(nbt.hasKey("rotation")) rotation = fromNBT(nbt.getCompoundTag("rotation"));
                if(nbt.hasKey("scale")) scale = fromNBT(nbt.getCompoundTag("scale"));
                return this;
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
