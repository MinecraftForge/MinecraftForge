package net.minecraftforge.debug;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.DispenseFluidContainer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import net.minecraftforge.fluids.capability.wrappers.BlockWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

// TODO: Auto-generated Javadoc
@Mod( modid = FluidAdditionalFieldsTest.MODID, 
      name = FluidAdditionalFieldsTest.MODNAME, 
      version = FluidAdditionalFieldsTest.MODVERSION,
      acceptedMinecraftVersions = "[1.12]")
@Mod.EventBusSubscriber
public class FluidAdditionalFieldsTest
{
	// use this to enable / disable the test mod
	public static final boolean ENABLED = false;
	
    public static final String MODID = "fluidadditionalfields";
    public static final String MODNAME = "Fluid Additional Fields Test";
    public static final String MODVERSION = "1.0.0";
    public static final String MODDESCRIPTION = "Testing addition of fields like color and flammability";
    public static final String MODAUTHOR = "jabelar";
    public static final String MODCREDITS = "";
    public static final String MODURL = "www.jabelarminecraft.blogspot.com";
    public static final String MODLOGO = "";
 
	// use a named channel to identify packets related to this mod
    public static final String NETWORK_CHANNEL_NAME = MODID;
	public static FMLEventChannel channel;

	// networking
	public static SimpleNetworkWrapper network;
    
    // instantiate creative tabs
	public static final CustomCreativeTab CREATIVE_TAB = new CustomCreativeTab();
    
    static 
    {
    	if (ENABLED)
    	{
    		FluidRegistry.enableUniversalBucket();
    	}
    }

    // instantiate the mod
    @Instance(MODID)
    public static FluidAdditionalFieldsTest instance;
        
    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide="net.minecraftforge.debug.FluidAdditionalFieldsTest$ClientProxy", serverSide="net.minecraftforge.debug.FluidAdditionalFieldsTest$CommonProxy")
    public static CommonProxy proxy;
               
    /**
     * Fml life cycle event.
     *
     * @param event the event
     */
    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the GameRegistry."
    public void fmlLifeCycleEvent(FMLPreInitializationEvent event) 
    {   	
    	if (!ENABLED) { return; }
    	
        // DEBUG
        System.out.println("preInit()"+event.getModMetadata().name);
                
        // hard-code mod information so don't need mcmod.info file
        event.getModMetadata().autogenerated = false ; // stops it from complaining about missing mcmod.info
        event.getModMetadata().credits = TextFormatting.BLUE+MODCREDITS;
        event.getModMetadata().authorList.add(TextFormatting.RED+MODAUTHOR);
        event.getModMetadata().description = TextFormatting.YELLOW+MODDESCRIPTION;
        event.getModMetadata().url = MODURL;
        event.getModMetadata().logoFile = MODLOGO;
        
        proxy.fmlLifeCycleEvent(event);
    }

	/**
	 * Fml life cycle event.
	 *
	 * @param event the event
	 */
	@Mod.EventHandler
    // Do your mod setup. Build whatever data structures you care about. Register recipes."
    // Register network handlers
    public void fmlLifeCycleEvent(FMLInitializationEvent event) 
    {
    	if (!ENABLED) { return; }
    	    	
        // DEBUG
        System.out.println("init()");
        
        proxy.fmlLifeCycleEvent(event);
    }

	/**
	 * Fml life cycle.
	 *
	 * @param event the event
	 */
	@Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void fmlLifeCycle(FMLPostInitializationEvent event) 
	{
    	if (!ENABLED) { return; }
    	
        // DEBUG
        System.out.println("postInit()");
        
        proxy.fmlLifeCycleEvent(event);
    }

	/**
	 * Fml life cycle.
	 *
	 * @param event the event
	 */
	@Mod.EventHandler
	public void fmlLifeCycle(FMLServerAboutToStartEvent event)
	{
    	if (!ENABLED) { return; }
    	
        // DEBUG
        System.out.println("Server about to start");
        
		proxy.fmlLifeCycleEvent(event);
	}

	/**
	 * Fml life cycle.
	 *
	 * @param event the event
	 */
	@Mod.EventHandler
	// register server commands
	// refer to tutorial at http://www.minecraftforge.net/wiki/Server_Command#Mod_Implementation
	public void fmlLifeCycle(FMLServerStartingEvent event)
	{
    	if (!ENABLED) { return; }
    	
        // DEBUG
        System.out.println("Server starting");
        
		proxy.fmlLifeCycleEvent(event);
	}

	/**
	 * Fml life cycle.
	 *
	 * @param event the event
	 */
	@Mod.EventHandler
	public void fmlLifeCycle(FMLServerStartedEvent event)
	{
    	if (!ENABLED) { return; }
    	
        // DEBUG
        System.out.println("Server started");
        
		proxy.fmlLifeCycleEvent(event);
	}

	/**
	 * Fml life cycle.
	 *
	 * @param event the event
	 */
	@Mod.EventHandler
	public void fmlLifeCycle(FMLServerStoppingEvent event)
	{
    	if (!ENABLED) { return; }
    	
        // DEBUG
        System.out.println("Server stopping");
        
		proxy.fmlLifeCycleEvent(event);
	}

	/**
	 * Fml life cycle.
	 *
	 * @param event the event
	 */
	@Mod.EventHandler
	public void fmlLifeCycle(FMLServerStoppedEvent event)
	{
    	if (!ENABLED) { return; }
    	
        // DEBUG
        System.out.println("Server stopped");
        
		proxy.fmlLifeCycleEvent(event);
	}
    
	public static class CommonProxy 
	{
	    /**
	     * Fml life cycle event for Pre-Initialization. Historically (before registry 
	     * events) this was where blocks, items, etc. were registered. There are still things
	     * like entities and networking which should still be registered here.
	     *
	     * @param event the event
	     */
	    public void fmlLifeCycleEvent(FMLPreInitializationEvent event)
	    {     	
	        // register stuff
	        registerFluids();
	    }
	
	    /**
	     * Fml life cycle event for Initialization. This phase is good for registering event listeners,
	     * for registering things that depend on things in pre-init from other mods (like recipes, advancements
	     * and such.)
	     *
	     * @param event the event
	     */
	    public void fmlLifeCycleEvent(FMLInitializationEvent event)
	    {

	    }
	    
	
	    /**
	     * Fml life cycle event Post Initialization. This phase is useful
	     * For doing inter-mod stuff like checking which mods are loaded
	     * or if you want a complete view of things across mods like having 
	     * a list of all registered items to aid random item generation.
	     *
	     * @param event the event
	     */
	    public void fmlLifeCycleEvent(FMLPostInitializationEvent event)
	    {
	        // can do some inter-mod stuff here
	    }
	
	    /**
	     * Fml life cycle event for Server About To Start.
	     *
	     * @param event the event
	     */
	    public void fmlLifeCycleEvent(FMLServerAboutToStartEvent event) 
	    {
	        // TODO Auto-generated method stub        
	    }
	
	    /**
	     * Fml life cycle event for Server Started.
	     *
	     * @param event the event
	     */
	    public void fmlLifeCycleEvent(FMLServerStartedEvent event) 
	    {
	        // TODO Auto-generated method stub
	    }
	
	    /**
	     * Fml life cycle event for Server Stopping.
	     *
	     * @param event the event
	     */
	    public void fmlLifeCycleEvent(FMLServerStoppingEvent event) 
	    {
	        // TODO Auto-generated method stub  
	    }
	
	    /**
	     * Fml life cycle event for Server Stopped.
	     *
	     * @param event the event
	     */
	    public void fmlLifeCycleEvent(FMLServerStoppedEvent event) 
	    {
	        // TODO Auto-generated method stub       
	    }
	
	    /**
	     * Fml life cycle event.
	     *
	     * @param event the event
	     */
	    public void fmlLifeCycleEvent(FMLServerStartingEvent event) 
	    {
	        // register server commands
	    }
	    
	    /**
	     *  
	     * Registers fluids.
	     */
	    public void registerFluids()
	    {
	    	// DEBUG
	    	System.out.println("Registering fluids");
	        FluidRegistry.registerFluid(ModFluids.SLIME);
	//        FluidRegistry.addBucketForFluid(ModFluids.SLIME);
	    }
	    
	    	    
	    /**
	     * handles the acceleration of an object whilst in a material.
	     *
	     * @param entityIn the entity in
	     * @param materialIn the material in
	     * @return true, if successful
	     */
		public boolean handleMaterialAcceleration(Entity entityIn, Material materialIn)
	    {
	    	World parWorld = entityIn.world;
	    	AxisAlignedBB bb = entityIn.getEntityBoundingBox().grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.001D);
	    	
	        int j2 = MathHelper.floor(bb.minX);
	        int k2 = MathHelper.ceil(bb.maxX);
	        int l2 = MathHelper.floor(bb.minY);
	        int i3 = MathHelper.ceil(bb.maxY);
	        int j3 = MathHelper.floor(bb.minZ);
	        int k3 = MathHelper.ceil(bb.maxZ);
	
	        boolean flag = false;
	        Vec3d vec3d = Vec3d.ZERO;
	        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();
	
	        for (int l3 = j2; l3 < k2; ++l3)
	        {
	            for (int i4 = l2; i4 < i3; ++i4)
	            {
	                for (int j4 = j3; j4 < k3; ++j4)
	                {
	                    blockpos$pooledmutableblockpos.setPos(l3, i4, j4);
	                    IBlockState iblockstate1 = parWorld.getBlockState(blockpos$pooledmutableblockpos);
	                    Block block = iblockstate1.getBlock();
	
	                    Boolean result = block.isEntityInsideMaterial(parWorld, blockpos$pooledmutableblockpos, iblockstate1, entityIn, i3, materialIn, false);
	                    if (result != null && result == true)
	                    {
	                        // Forge: When requested call blocks modifyAcceleration method, and more importantly cause this method to return true, which results in an entity being "inWater"
	                        flag = true;
	                        vec3d = block.modifyAcceleration(parWorld, blockpos$pooledmutableblockpos, entityIn, vec3d);
	                  	  	                  	  
	                        continue;
	                    }
	                    else if (result != null && result == false) continue;
	
	                    if (iblockstate1.getMaterial() == materialIn)
	                    {	                  	  
	                        double d0 = i4 + 1 - BlockLiquid.getLiquidHeightPercent(iblockstate1.getValue(BlockLiquid.LEVEL).intValue());
	
	                        if (i3 >= d0)
	                        {
	                      	  flag = true;
	                      	  vec3d = block.modifyAcceleration(parWorld, blockpos$pooledmutableblockpos, entityIn, vec3d);
	                      	  
	                         }
	                    }
	                }
	            }
	        }
	
	        blockpos$pooledmutableblockpos.release();
	
	        if (vec3d.lengthVector() > 0.0D && entityIn.isPushedByWater())
	        {
	      	  
	      	  /*
	      	   * Although applied to all entities, EntityPlayer doesn't really take
	      	   * affect, so the fluid motion control is handled in the client-side
	      	   * PlayerTickEvent
	      	   */
	            vec3d = vec3d.normalize();
	            double d1 = 0.014D;
	            entityIn.motionX += vec3d.x * d1;
	            entityIn.motionY += vec3d.y * d1;
	            entityIn.motionZ += vec3d.z * d1;
	        }
	        else
	        {
	        }
	    	
	        entityIn.fallDistance = 0.0F;
	
	        return flag;
	    }
	}
	
	public static class ClientProxy extends CommonProxy 
	{
	    /* (non-Javadoc)
	     * @see com.blogspot.jabelarminecraft.examplemod.proxy.CommonProxy#fmlLifeCycleEvent(net.minecraftforge.fml.common.event.FMLPreInitializationEvent)
	     */
	    @Override
	    public void fmlLifeCycleEvent(FMLPreInitializationEvent event)
	    {
	        // DEBUG
	        System.out.println("on Client side");
	                
	        // do common stuff
	        super.fmlLifeCycleEvent(event);
	    }
	    
	    /* (non-Javadoc)
	     * @see com.blogspot.jabelarminecraft.examplemod.proxy.CommonProxy#fmlLifeCycleEvent(net.minecraftforge.fml.common.event.FMLInitializationEvent)
	     */
	    @Override
	    public void fmlLifeCycleEvent(FMLInitializationEvent event)
	    {
	        // DEBUG
	        System.out.println("on Client side");

	        super.fmlLifeCycleEvent(event);
	    }
	    
	    /* (non-Javadoc)
	     * @see com.blogspot.jabelarminecraft.examplemod.proxy.CommonProxy#fmlLifeCycleEvent(net.minecraftforge.fml.common.event.FMLPostInitializationEvent)
	     */
	    @Override
	    public void fmlLifeCycleEvent(FMLPostInitializationEvent event)
	    {
	        // DEBUG
	        System.out.println("on Client side");

	        super.fmlLifeCycleEvent(event);
	}


	    /**
	     * Registers the entity renderers.
	     */
	    public void registerEntityRenderers() 
	    {
	        // the float parameter passed to the Render class is the shadow size for the entity
	      
//	        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
	        // RenderingRegistry.registerEntityRenderingHandler(EntityGoldenGoose.class, new RenderGoldenGoose(renderManager, new ModelGoldenGoose(), 0.5F)); // 0.5F is shadow size 
//	    	RenderingRegistry.registerEntityRenderingHandler(EntityPigTest.class, new RenderPig(renderManager));
	    }
	    	      
	    /**
	     * handles the acceleration of an object whilst in a material.
	     *
	     * @param entityIn the entity in
	     * @param materialIn the material in
	     * @return true, if successful
	     */
	    @Override
		public boolean handleMaterialAcceleration(Entity entityIn, Material materialIn)
	    {
	    	World parWorld = entityIn.world;
	    	AxisAlignedBB bb = entityIn.getEntityBoundingBox().grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.001D);
	    	
	        int j2 = MathHelper.floor(bb.minX);
	        int k2 = MathHelper.ceil(bb.maxX);
	        int l2 = MathHelper.floor(bb.minY);
	        int i3 = MathHelper.ceil(bb.maxY);
	        int j3 = MathHelper.floor(bb.minZ);
	        int k3 = MathHelper.ceil(bb.maxZ);

	        boolean flag = false;
	        Vec3d vec3d = Vec3d.ZERO;
	        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

	        for (int l3 = j2; l3 < k2; ++l3)
	        {
	            for (int i4 = l2; i4 < i3; ++i4)
	            {
	                for (int j4 = j3; j4 < k3; ++j4)
	                {
	                    blockpos$pooledmutableblockpos.setPos(l3, i4, j4);
	                    IBlockState iblockstate1 = parWorld.getBlockState(blockpos$pooledmutableblockpos);
	                    Block block = iblockstate1.getBlock();

	                    Boolean result = block.isEntityInsideMaterial(parWorld, blockpos$pooledmutableblockpos, iblockstate1, entityIn, i3, materialIn, false);
	                    if (result != null && result == true)
	                    {
	                        // Forge: When requested call blocks modifyAcceleration method, and more importantly cause this method to return true, which results in an entity being "inWater"
	                        flag = true;
	                        vec3d = block.modifyAcceleration(parWorld, blockpos$pooledmutableblockpos, entityIn, vec3d);
	                  	  
//	                        // DEBUG
//	                  	  System.out.println("Entity is inside material = "+materialIn+" and motion add vector = "+vec3d);
	                  	  
	                        continue;
	                    }
	                    else if (result != null && result == false) continue;

	                    if (iblockstate1.getMaterial() == materialIn)
	                    {
//	                  	  // DEBUG
//	                  	  System.out.println("blockstate material matches material in");
	                  	  
	                        double d0 = i4 + 1 - BlockLiquid.getLiquidHeightPercent(iblockstate1.getValue(BlockLiquid.LEVEL).intValue());

	                        if (i3 >= d0)
	                        {
	                      	  flag = true;
	                      	  vec3d = block.modifyAcceleration(parWorld, blockpos$pooledmutableblockpos, entityIn, vec3d);
	                      	  
//	                            // DEBUG
//	                      	  System.out.println("deep enough to push entity and motion add = "+vec3d);                 
	                         }
	                    }
	                }
	            }
	        }

	        blockpos$pooledmutableblockpos.release();

	        if (vec3d.lengthVector() > 0.0D && entityIn.isPushedByWater())
	        {
//	      	  // DEBUG
//	      	  System.out.println("motion vector is non-zero");
	      	  
	      	  /*
	      	   * Although applied to all entities, EntityPlayer doesn't really take
	      	   * affect, so the fluid motion control is handled in the client-side
	      	   * PlayerTickEvent
	      	   */
	            vec3d = vec3d.normalize();
	            double d1 = 0.014D;
	            entityIn.motionX += vec3d.x * d1;
	            entityIn.motionY += vec3d.y * d1;
	            entityIn.motionZ += vec3d.z * d1;
	        }
	        else
	        {
//	          	  // DEBUG
//	          	  System.out.println("motion vector is zero");
	        }
	    	
	        entityIn.fallDistance = 0.0F;

	        return flag;
	    }
	}
	
	public static class ModBlockFluidClassic extends BlockFluidClassic
	{
		/**
		 * Instantiates a new mod block fluid classic.
		 *
		 * @param parFluid the par fluid
		 * @param parMaterial the par material
		 */
		public ModBlockFluidClassic(Fluid parFluid, Material parMaterial) 
		{
			super(parFluid, parMaterial);
		}
		
	    /* (non-Javadoc)
	     * @see net.minecraftforge.fluids.BlockFluidBase#modifyAcceleration(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraft.entity.Entity, net.minecraft.util.math.Vec3d)
	     */
	    @Override
		public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion)
	    {
	    	if (worldIn.getBlockState(pos).getMaterial() instanceof MaterialLiquid)
	    	{
	    		Vec3d flowAdder = getFlow(worldIn, pos, worldIn.getBlockState(pos));
	    		
				return motion.add(flowAdder);
	    	}
	    	else
	    	{   		
	    		return motion;
	    	}
	    }

	    /**
	     * Gets the flow.
	     *
	     * @param worldIn the world in
	     * @param pos the pos
	     * @param state the state
	     * @return the flow
	     */
	    protected Vec3d getFlow(IBlockAccess worldIn, BlockPos pos, IBlockState state)
	    {
	        double d0 = 0.0D;
	        double d1 = 0.0D;
	        double d2 = 0.0D;
	        int i = this.getRenderedDepth(state);
	        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

	        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
	        {
	            blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing);
	            int j = this.getRenderedDepth(worldIn.getBlockState(blockpos$pooledmutableblockpos));

	            if (j < 0)
	            {
	                if (!worldIn.getBlockState(blockpos$pooledmutableblockpos).getMaterial().blocksMovement())
	                {
	                    j = this.getRenderedDepth(worldIn.getBlockState(blockpos$pooledmutableblockpos.down()));

	                    if (j >= 0)
	                    {
	                        int k = j - (i - 8);
	                        d0 += enumfacing.getFrontOffsetX() * k;
	                        d1 += enumfacing.getFrontOffsetY() * k;
	                        d2 += enumfacing.getFrontOffsetZ() * k;
	                    }
	                }
	            }
	            else if (j >= 0)
	            {
	                int l = j - i;
	                d0 += enumfacing.getFrontOffsetX() * l;
	                d1 += enumfacing.getFrontOffsetY() * l;
	                d2 += enumfacing.getFrontOffsetZ() * l;
	            }
	        }

	        Vec3d vec3d = new Vec3d(d0, d1, d2);

	        if (state.getValue(LEVEL).intValue() >= 8)
	        {
//	        	// DEBUG
//	        	System.out.println("fluid level greater than zero");
	        	
	            for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL)
	            {
	                blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing1);

	                if (this.causesDownwardCurrent(worldIn, blockpos$pooledmutableblockpos, enumfacing1) || this.causesDownwardCurrent(worldIn, blockpos$pooledmutableblockpos.up(), enumfacing1))
	                {
//	                	// DEBUG
//	                	System.out.println("Causes downward current");
	                	
	                    vec3d = vec3d.normalize().addVector(0.0D, -6.0D, 0.0D);
	                    break;
	                }
	            }
	        }

	        blockpos$pooledmutableblockpos.release();
	        return vec3d.normalize();
	    }

	    /**
	     * Gets the depth.
	     *
	     * @param state the state
	     * @return the depth
	     */
	    protected int getDepth(IBlockState state)
	    {
	        return state.getMaterial() == this.blockMaterial ? state.getValue(LEVEL).intValue() : -1;
	    }

	    /**
	     * Gets the rendered depth.
	     *
	     * @param state the state
	     * @return the rendered depth
	     */
	    protected int getRenderedDepth(IBlockState state)
	    {
	        int i = this.getDepth(state);
	        return i >= 8 ? 0 : i;
	    }

	    /**
	     * Checks if an additional {@code -6} vertical drag should be applied to the entity. See {#link
	     * net.minecraft.block.BlockLiquid#getFlow()}
	     */
	    private boolean causesDownwardCurrent(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	    {
	        IBlockState iblockstate = worldIn.getBlockState(pos);
	        Block block = iblockstate.getBlock();
	        Material material = iblockstate.getMaterial();

	        if (material == this.blockMaterial)
	        {
	            return false;
	        }
	        else if (side == EnumFacing.UP)
	        {
	            return true;
	        }
	        else if (material == Material.ICE)
	        {
	            return false;
	        }
	        else
	        {
	            boolean flag = isExceptBlockForAttachWithPiston(block) || block instanceof BlockStairs;
	            return !flag && iblockstate.getBlockFaceShape(worldIn, pos, side) == BlockFaceShape.SOLID;
	        }
	    }
	    
	    /* (non-Javadoc)
	     * @see net.minecraftforge.fluids.BlockFluidClassic#place(net.minecraft.world.World, net.minecraft.util.math.BlockPos, net.minecraftforge.fluids.FluidStack, boolean)
	     */
	    /* IFluidBlock */
	    @Override
	    public int place(World world, BlockPos pos, @Nonnull FluidStack fluidStack, boolean doPlace)
	    {

	        if (doPlace)
	        {
	            FluidUtil.destroyBlockOnFluidPlacement(world, pos);
	            world.setBlockState(pos, this.getDefaultState(), 11);
	        }
	        return fluidStack.amount;
	    }

	    @Override
		@SideOnly (Side.CLIENT)
	    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)
	    {
	    	int color = getFluid().getColor(world, pos);
	    	return new Vec3d(color>>16&0xFF, color>>8&0xFF, color&0xFF);
//	    	return new Vec3d(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue());
	    }
	    
	}

	public static class ModBlocks 
	{
		/*
		 * fluid blocks
		 * Make sure you set registry name here
		 */
		public static final BlockFluidBase SLIME_BLOCK = (BlockFluidBase) Utilities.setBlockName(new ModBlockFluidClassic(ModFluids.SLIME, ModMaterials.SLIME), "slime");
		
		public static final Set<Block> SET_BLOCKS = ImmutableSet.of(
				SLIME_BLOCK
				);
		public static final Set<ItemBlock> SET_ITEM_BLOCKS = ImmutableSet.of(
				new ItemBlock(SLIME_BLOCK)
				);

		/**
		 * Initialize this mod's {@link Block}s with any post-registration data.
		 */
		private static void initialize() 
		{
		}

		@Mod.EventBusSubscriber(modid = FluidAdditionalFieldsTest.MODID)
		public static class RegistrationHandler 
		{

			/**
			 * Register this mod's {@link Block}s.
			 *
			 * @param event The event
			 */
			@SubscribeEvent
			public static void onEvent(final RegistryEvent.Register<Block> event) 
			{
		    	if (!ENABLED) { return; }
		    	
				final IForgeRegistry<Block> registry = event.getRegistry();

				for (final Block block : SET_BLOCKS) {
					registry.register(block);
					// DEBUG
					System.out.println("Registering block: "+block.getRegistryName());
				}
				
				initialize();
			}

			/**
			 * Register this mod's {@link ItemBlock}s.
			 *
			 * @param event The event
			 */
			@SubscribeEvent
			public static void registerItemBlocks(final RegistryEvent.Register<Item> event) 
			{
		    	if (!ENABLED) { return; }
		    	
				final IForgeRegistry<Item> registry = event.getRegistry();

				for (final ItemBlock item : SET_ITEM_BLOCKS) {
					final Block block = item.getBlock();
					final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
					registry.register(item.setRegistryName(registryName));
					// DEBUG
					System.out.println("Registering Item Block for "+registryName);			}
			}		
			
			/**
			 * On model event.
			 *
			 * @param event the event
			 */
			@SubscribeEvent
			@SideOnly(Side.CLIENT)
			public static void onModelEvent(final ModelRegistryEvent event) 
			{
		    	if (!ENABLED) { return; }
		    	
				//DEBUG
				System.out.println("Registering block models");
				
				registerBlockModels();
				registerItemBlockModels();
			}
		}	
		
	    /**
	     * Register block models.
	     */
		@SideOnly(Side.CLIENT)
	    public static void registerBlockModels()
	    {
			for (final Block block : SET_BLOCKS) {
				registerBlockModel(block);
				// DEBUG
				System.out.println("Registering block model for"
						+ ": "+block.getRegistryName());
			}        
	    }
	    
	    /**
	     * Register block model.
	     *
	     * @param parBlock the par block
	     */
		@SideOnly(Side.CLIENT)
	    public static void registerBlockModel(Block parBlock)
	    {
	    	registerBlockModel(parBlock, 0);
	    }
	    
	    /**
	     * Register block model.
	     *
	     * @param parBlock the par block
	     * @param parMetaData the par meta data
	     */
		@SideOnly(Side.CLIENT)
	    public static void registerBlockModel(Block parBlock, int parMetaData)
	    {
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(parBlock), parMetaData, new ModelResourceLocation(FluidAdditionalFieldsTest.MODID + ":" + parBlock.getUnlocalizedName().substring(5), "inventory"));
	    }

		
	    /**
	     * Register block models.
	     */
		@SideOnly(Side.CLIENT)
	    public static void registerItemBlockModels()
	    {
			for (final ItemBlock block : SET_ITEM_BLOCKS) {
				registerItemBlockModel(block);
				// DEBUG
				System.out.println("Registering item block model for"
						+ ": "+block.getRegistryName());
			}        
	    }
	    
	    /**
	     * Register block model.
	     *
	     * @param parBlock the par block
	     */
		@SideOnly(Side.CLIENT)
	    public static void registerItemBlockModel(ItemBlock parBlock)
	    {
	    	registerItemBlockModel(parBlock, 0);
	    }
	    
	    /**
	     * Register block model.
	     *
	     * @param parBlock the par block
	     * @param parMetaData the par meta data
	     */
		@SideOnly(Side.CLIENT)
	    public static void registerItemBlockModel(ItemBlock parBlock, int parMetaData)
	    {
	        ModelLoader.setCustomModelResourceLocation(parBlock, parMetaData, new ModelResourceLocation(FluidAdditionalFieldsTest.MODID + ":" + parBlock.getUnlocalizedName().substring(5), "inventory"));
	    }
	}
	
	public static class ModFluids 
	{
		/*
		 * fluids
		 */
		public static final Fluid SLIME = new Fluid(
				"slime", 
				new ResourceLocation(FluidAdditionalFieldsTest.MODID,"slime_still"), 
				new ResourceLocation(FluidAdditionalFieldsTest.MODID, "slime_flow")
				)
				.setDensity(1100)
				.setGaseous(false)
				.setLuminosity(9)
				.setViscosity(25000)
				.setTemperature(300)
				.setColor(0xFF00FF00)
				.setEmptySound(SoundEvents.ITEM_BUCKET_EMPTY)
				.setFillSound(SoundEvents.ITEM_BUCKET_FILL)
				;
	} 
	
	public static class FluidHandlerSlimeBag extends FluidHandlerItemStack
	{
		// Always copy this to use it for an assignment
		protected static final FluidStack EMPTY = new FluidStack(ModFluids.SLIME, 0); 
		
		/**
		 * Instantiates a new, empty fluid handler slime bag.
		 *
		 * @param parContainerStack the container stack
		 * @param parCapacity the capacity
		 */
		public FluidHandlerSlimeBag(ItemStack parContainerStack, int parCapacity) 
		{
			super(parContainerStack, parCapacity);
			
			// if container was constructed by loading from NBT, should already
			// have fluid information in tags
			if (getFluidStack() == null)
			{
				setContainerToEmpty(); // start empty
			}
			
//			// DEBUG
//			System.out.println("Constructing FluidHandlerSlimeBag with FluidStack = "+getFluid()+" capacity = "+capacity+" and container = "+container);
		}
		
	    /* (non-Javadoc)
	     * @see net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack#setContainerToEmpty()
	     */
	    @Override
		protected void setContainerToEmpty()
	    {
	    	setFluidStack(EMPTY.copy()); // some code looks at level, some looks at lack of handler (tag)
	        container.getTagCompound().removeTag(FLUID_NBT_KEY);
	    }

	    /* (non-Javadoc)
	     * @see net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack#canFillFluidType(net.minecraftforge.fluids.FluidStack)
	     */
	    @Override
		public boolean canFillFluidType(FluidStack fluid)
	    {
	        return (fluid.getFluid() == ModFluids.SLIME);
	    }
	    
	    /**
	     * Gets the fluid stack.
	     *
	     * @return the fluid stack
	     */
	    // rename getFluid() method since it is confusing as it returns a fluid stack
	    public FluidStack getFluidStack()
	    {
	    	return getFluid();
	    }
	    
	    /**
	     * Sets the fluid stack.
	     *
	     * @param parFluidStack the new fluid stack
	     */
	    // rename setFluid() method since it is confusing as it take a fluid stack
	    public void setFluidStack(FluidStack parFluidStack)
	    {
	    	setFluid(parFluidStack);
	    }
	}

	public static class ModMaterials 
	{
		public static final Material SLIME = new MaterialLiquid(MapColor.LIME);
	}

	public static class CustomCreativeTab extends CreativeTabs 
	{

		/**
		 * Instantiates a new custom creative tab.
		 */
		public CustomCreativeTab() 
		{
			super(FluidAdditionalFieldsTest.MODID);
		}

		/* (non-Javadoc)
		 * @see net.minecraft.creativetab.CreativeTabs#getTabIconItem()
		 */
		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack getTabIconItem() 
		{
			return new ItemStack(Items.BANNER);
		}

		/* (non-Javadoc)
		 * @see net.minecraft.creativetab.CreativeTabs#displayAllRelevantItems(net.minecraft.util.NonNullList)
		 */
		@SideOnly(Side.CLIENT)
		@Override
		public void displayAllRelevantItems(final NonNullList<ItemStack> items) 
		{
			super.displayAllRelevantItems(items);
		}
	}
	
	public static class ModItems 
	{
		// instantiate fluid container items
		public final static ItemSlimeBag SLIME_BAG = new ItemSlimeBag();
		
		// only put items with standard models here
		public static final Set<Item> SET_ITEMS_STANDARD = ImmutableSet.of(
				);
		
		// only put items with custom models here
		public static final Set<Item> SET_ITEMS_CUSTOM = ImmutableSet.of(
				SLIME_BAG
				);


		/**
		 * Initialize this mod's {@link Item}s with any post-registration data.
		 */
		private static void initialize() 
		{
			// You can put furnace smelting recipes here
		}

		@Mod.EventBusSubscriber(modid = FluidAdditionalFieldsTest.MODID)
		public static class RegistrationHandler 
		{
			/**
			 * Register this mod's {@link Item}s.
			 *
			 * @param event The event
			 */
			@SubscribeEvent
			public static void onEvent(final RegistryEvent.Register<Item> event) 
			{
		    	if (!ENABLED) { return; }
		    	
				final IForgeRegistry<Item> registry = event.getRegistry();

		        System.out.println("Registering items");

				for (final Item item : SET_ITEMS_STANDARD) 
				{
					registry.register(item);
					// DEBUG
					System.out.println("Registering item: "+item.getRegistryName());
				}

				for (final Item item : SET_ITEMS_CUSTOM) 
				{
					registry.register(item);
					// DEBUG
					System.out.println("Registering item: "+item.getRegistryName());
				}

				initialize();
			}
			
			/**
			 * ModelRegistryEvent handler.
			 *
			 * @param event the event
			 */
			@SubscribeEvent
			@SideOnly(Side.CLIENT)
			public static void onModelEvent(final ModelRegistryEvent event) 
			{
		    	if (!ENABLED) { return; }
		    	
				//DEBUG
				System.out.println("Registering item models");
				
				// register standard model items
				for (final Item item : SET_ITEMS_STANDARD) 
				{
					registerItemModel(item);
					
					// DEBUG
					System.out.println("Registering item model for: "+item.getRegistryName());
				}
				
				// register custom model items
				ModelLoaderRegistry.registerLoader(ModelSlimeBag.CustomModelLoader.INSTANCE);
				ModelLoader.setCustomMeshDefinition(SLIME_BAG, stack -> ModelSlimeBag.LOCATION);
		        ModelBakery.registerItemVariants(SLIME_BAG, ModelSlimeBag.LOCATION);
				// DEBUG
				System.out.println("Registering item model for: "+SLIME_BAG.getRegistryName());
			}
			
			/**
			 * ModelBakeEvent handler.
			 *
			 * @param event the event
			 */
			@SubscribeEvent
			@SideOnly(Side.CLIENT)
			public static void onModelEvent(final ModelBakeEvent event) 
			{
		    	if (!ENABLED) { return; }
		    	
				//DEBUG
				System.out.println("Models have been baked");
			}
		}
	    
	    /**
	     * Register item model.
	     *
	     * @param parItem the par item
	     */
		@SideOnly(Side.CLIENT)
	    public static void registerItemModel(Item parItem)
	    {
	    	registerItemModel(parItem, 0);
	    }
	    
	    /**
	     * Register item model.
	     *
	     * @param parItem the par item
	     * @param parMetaData the par meta data
	     */
		@SideOnly(Side.CLIENT)
	    public static void registerItemModel(Item parItem, int parMetaData)
	    {
	        ModelLoader.setCustomModelResourceLocation(parItem, parMetaData, new ModelResourceLocation(FluidAdditionalFieldsTest.MODID + ":" + parItem.getUnlocalizedName().substring(5), "inventory"));
	    }	    
	}
	
	@SideOnly(Side.CLIENT)
	public static final class ModelSlimeBag implements IModel
	{
	    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation(FluidAdditionalFieldsTest.MODID, "slime_bag"), "inventory");

	    // minimal Z offset to prevent depth-fighting
	    private static final float NORTH_Z_FLUID = 7.498f / 16f;
	    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	    public static final ModelSlimeBag MODEL = new ModelSlimeBag();

	    @Nullable
	    private final ResourceLocation emptyLocation = new ResourceLocation(FluidAdditionalFieldsTest.MODID, "slime_bag");
	    @Nullable
	    private final ResourceLocation filledLocation = new ResourceLocation(FluidAdditionalFieldsTest.MODID, "slime_bag_filled");
	    @Nullable
	    private final Fluid fluid;

	    /**
	     * Instantiates a new model slime bag.
	     */
	    public ModelSlimeBag()
	    {
	    	this(null);
	    }
	    
	    /**
	     * Instantiates a new model slime bag.
	     *
	     * @param parFluid the par fluid
	     */
	    public ModelSlimeBag(Fluid parFluid)
	    {
	    	fluid = parFluid;
	    }

	    /* (non-Javadoc)
	     * @see net.minecraftforge.client.model.IModel#getTextures()
	     */
	    @Override
	    public Collection<ResourceLocation> getTextures()
	    {
	        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
	        if (emptyLocation != null)
	            builder.add(emptyLocation);
	        if (filledLocation != null)
	            builder.add(filledLocation);

	        return builder.build();
	    }


		/* (non-Javadoc)
		 * @see net.minecraftforge.client.model.IModel#getDependencies()
		 */
		@Override
		public Collection<ResourceLocation> getDependencies() {
			return ImmutableList.of();
		}

		/* (non-Javadoc)
	     * @see net.minecraftforge.client.model.IModel#bake(net.minecraftforge.common.model.IModelState, net.minecraft.client.renderer.vertex.VertexFormat, java.util.function.Function)
	     */
	    @Override
	    public IBakedModel bake(IModelState state, VertexFormat format,
	                                    Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	    {
	    	// DEBUG
	    	System.out.println("baking");
	    	
	        ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);

	        TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
	        TextureAtlasSprite fluidSprite = null;
	        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

	        if(fluid != null) 
	        {
	        	// DEBUG
	        	System.out.println("Getting fluid sprite");
	        	
	            fluidSprite = bakedTextureGetter.apply(fluid.getStill());
	        }

	        if (emptyLocation != null)
	        {
	        	// DEBUG
	        	System.out.println("Buiding empty model");
	        	
	            IBakedModel model = (new ItemLayerModel(ImmutableList.of(emptyLocation))).bake(state, format, bakedTextureGetter);
	            builder.addAll(model.getQuads(null, null, 0));
	        }
	        if (filledLocation != null && fluidSprite != null)
	        {
	        	// DEBUG
	        	System.out.println("Building filled model");
	        	
	            TextureAtlasSprite filledTexture = bakedTextureGetter.apply(filledLocation);
	            // build liquid layer (inside)
	            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, filledTexture, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor()));
	            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, filledTexture, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor()));
	        }

	        return new Baked(this, builder.build(), fluidSprite, format, Maps.immutableEnumMap(transformMap), Maps.<String, IBakedModel>newHashMap());
	    }

		/* (non-Javadoc)
		 * @see net.minecraftforge.client.model.IModel#getDefaultState()
		 */
		@Override
		public IModelState getDefaultState() {
			return TRSRTransformation.identity();
		}

	    /**
	     * Sets the liquid in the model.
	     * fluid - Name of the fluid in the FluidRegistry
	     * If the fluid can't be found, water is used
	     *
	     * @param customData the custom data
	     * @return the model slime bag
	     */
	    @Override
	    public ModelSlimeBag process(ImmutableMap<String, String> customData)
	    {
	    	// DEBUG
	    	System.out.println("process method with custom data = ");
	    	
	        String fluidName = customData.get("fluid");
	        Fluid fluid = FluidRegistry.getFluid(fluidName);

	        if (fluid == null) fluid = this.fluid;

	        // create new model with correct liquid
	        return new ModelSlimeBag(fluid);
	    }

	    /**
	     * Allows to use different textures for the model.
	     *
	     * @param textures the textures
	     * @return the model slime bag
	     */
	    @Override
	    public ModelSlimeBag retexture(ImmutableMap<String, String> textures)
	    {
	    	// don't allow retexturing
	    	// DEBUG
	    	System.out.println("Retexturing");

	        return new ModelSlimeBag(fluid);
	    }

	    public enum CustomModelLoader implements ICustomModelLoader
	    {
	        INSTANCE;

	        /* (non-Javadoc)
	         * @see net.minecraftforge.client.model.ICustomModelLoader#accepts(net.minecraft.util.ResourceLocation)
	         */
	        @Override
	        public boolean accepts(ResourceLocation modelLocation)
	        {
	            return modelLocation.getResourceDomain().equals(FluidAdditionalFieldsTest.MODID) && modelLocation.getResourcePath().contains("slime_bag");
	        }

	        /* (non-Javadoc)
	         * @see net.minecraftforge.client.model.ICustomModelLoader#loadModel(net.minecraft.util.ResourceLocation)
	         */
	        @Override
	        public IModel loadModel(ResourceLocation modelLocation)
	        {
	        	// DEBUG
	        	System.out.println("Loading model");
	        	
	            return MODEL;
	        }

	        /* (non-Javadoc)
	         * @see net.minecraft.client.resources.IResourceManagerReloadListener#onResourceManagerReload(net.minecraft.client.resources.IResourceManager)
	         */
	        @Override
	        public void onResourceManagerReload(IResourceManager resourceManager)
	        {
	            // no need to clear cache since we create a new model instance
	        }
	    }

	    private static final class BakedOverrideHandler extends ItemOverrideList
	    {
	        public static final BakedOverrideHandler INSTANCE = new BakedOverrideHandler();
	        
	        private BakedOverrideHandler()
	        {
	            super(ImmutableList.<ItemOverride>of());
	            
	            // DEBUG
	            System.out.println("Constructing BakedOverrideHandler");
	        }

	        @Override
	        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
	        {
				FluidStack fluidStack = null;
				if (stack.hasTagCompound() && stack.getTagCompound().hasKey(FluidHandlerItemStack.FLUID_NBT_KEY)) 
				{
					fluidStack = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag(FluidHandlerItemStack.FLUID_NBT_KEY));
				}
	 
	            if (fluidStack == null || fluidStack.amount <= 0)
	            {
//	            	// DEBUG
//	            	System.out.println("fluid stack is null, returning original model");
	            	
	                // empty bucket
	                return originalModel;
	            }
	            
//	            // DEBUG
//	            System.out.println("Fluid stack was not null and fluid amount = "+fluidStack.amount);

	            Baked model = (Baked)originalModel;

	            Fluid fluid = fluidStack.getFluid();
	            String name = fluid.getName();

	            if (!model.cache.containsKey(name))
	            {
	            	// DEBUG
	            	System.out.println("The model cache does not have key for fluid name");
					IModel parent = model.parent.process(ImmutableMap.of("fluid", name));
					Function<ResourceLocation, TextureAtlasSprite> textureGetter;
					textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
						@Override
						public TextureAtlasSprite apply(ResourceLocation location) {
							return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
						}
					};
					IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format,
							textureGetter);
					model.cache.put(name, bakedModel);
					return bakedModel;
	            }
	            
//	            // DEBUG
//	            System.out.println("The model cache already has key so returning the model");

	            return model.cache.get(name);
	        }
	    }

	    // the filled bucket is based on the empty bucket
	    private static final class Baked implements IBakedModel
	    {

	        private final ModelSlimeBag parent;
	        // FIXME: guava cache?
	        private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
	        private final ImmutableMap<TransformType, TRSRTransformation> transforms;
	        private final ImmutableList<BakedQuad> quads;
	        private final TextureAtlasSprite particle;
	        private final VertexFormat format;

	        public Baked(ModelSlimeBag parent,
	                              ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms,
	                              Map<String, IBakedModel> cache)
	        {
	            this.quads = quads;
	            this.particle = particle;
	            this.format = format;
	            this.parent = parent;
	            this.transforms = transforms;
	            this.cache = cache;
	            
	            // DEBUG
	            System.out.println("Constructing Baked");
	        }

	        @Override
	        public ItemOverrideList getOverrides()
	        {
	            return BakedOverrideHandler.INSTANCE;
	        }

	        @Override
	        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
	        {
	            return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType);
	        }

	        @Override
	        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
	        {
	            if(side == null) return quads;
	            return ImmutableList.of();
	        }

	        @Override
			public boolean isAmbientOcclusion() { return true;  }
	        @Override
			public boolean isGui3d() { return false; }
	        @Override
			public boolean isBuiltInRenderer() { return false; }
	        @Override
			public TextureAtlasSprite getParticleTexture() { return particle; }
	    }
	}

	@SuppressWarnings("deprecation")
	public static class ItemSlimeBag extends Item
	{
		private final int CAPACITY = Fluid.BUCKET_VOLUME;
		private final ItemStack EMPTY_STACK = new ItemStack(this);
		
		/**
		 * Instantiates a new item slime bag.
		 */
		public ItemSlimeBag() 
		{
			Utilities.setItemName(this, "slime_bag");
			setCreativeTab(FluidAdditionalFieldsTest.CREATIVE_TAB);
			setMaxStackSize(1);
	        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DispenseFluidContainer.getInstance());
	     
	        // DEBUG
			System.out.println("Constructing ItemSlimeBag");
		}
		
	    /* (non-Javadoc)
	     * @see net.minecraft.item.Item#initCapabilities(net.minecraft.item.ItemStack, net.minecraft.nbt.NBTTagCompound)
	     */
	    @Override
	    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
	    {
//	    	// DEBUG
//	    	System.out.println("initCapabilities for ItemSlimeBag with NBT = "+stack.getTagCompound()+" and Cap NBT = "+nbt);
	    	
	        return new FluidHandlerSlimeBag(stack, CAPACITY);
	    }

		/* (non-Javadoc)
		 * @see net.minecraft.item.Item#getSubItems(net.minecraft.creativetab.CreativeTabs, net.minecraft.util.NonNullList)
		 */
		@Override
		public void getSubItems(@Nullable final CreativeTabs tab, final NonNullList<ItemStack> subItems) 
		{
			if (!this.isInCreativeTab(tab)) return;

			subItems.add(EMPTY_STACK);

			final FluidStack fluidStack = new FluidStack(ModFluids.SLIME, CAPACITY);
			final ItemStack stack = new ItemStack(this);
			final IFluidHandlerItem fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			if (fluidHandler != null)
			{
				final int fluidFillAmount = fluidHandler.fill(fluidStack, true);
				if (fluidFillAmount == fluidStack.amount) 
				{
					final ItemStack filledStack = fluidHandler.getContainer();
					subItems.add(filledStack);	
					
					// DEBUG
					System.out.println("Filled bag and adding as sub-item = "+filledStack+" with amount = "+FluidUtil.getFluidContained(filledStack).amount);
				}
				else
				{
					// DEBUG
					System.out.println("Failed to add filled sub-item because amounts didn't match, fillAmount = "+fluidFillAmount);
				}
			}
			else
			{
				// DEBUG
				System.out.println("Failed to add filled sub-item because fluid handler was null");
			}
		}

		/* (non-Javadoc)
		 * @see net.minecraft.item.Item#getItemStackDisplayName(net.minecraft.item.ItemStack)
		 */
		@Override
		public String getItemStackDisplayName(final ItemStack stack) 
		{
			String unlocalizedName = this.getUnlocalizedNameInefficiently(stack);
	        IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(stack);
			FluidStack fluidStack = fluidHandler.getTankProperties()[0].getContents();

			// If the bucket is empty, translate the unlocalised name directly
			if (fluidStack == null || fluidStack.amount <= 0) 
			{
				unlocalizedName += ".name";
			}
			else
			{
				unlocalizedName += ".filled.name";
			}

			return I18n.translateToLocal(unlocalizedName).trim();
		}
		
		/**
		 * Empty.
		 *
		 * @param stack the stack
		 * @return the item stack
		 */
		public static ItemStack empty(ItemStack stack)
		{
			if (stack.getItem() instanceof ItemSlimeBag)
			{
				FluidStack fluidStack = FluidUtil.getFluidContained(stack);
				if (fluidStack != null)
				{
					fluidStack.amount = 0;
				}
			}
			
			return stack;	
		}


	    /**
	     * Called when the equipped item is right clicked.
	     *
	     * @param parWorld the par world
	     * @param parPlayer the par player
	     * @param parHand the par hand
	     * @return the action result
	     */
	    @Override
	    @Nonnull
	    public ActionResult<ItemStack> onItemRightClick(@Nonnull World parWorld, @Nonnull EntityPlayer parPlayer, @Nonnull EnumHand parHand)
	    {
	    	// DEBUG
	    	System.out.println("onItemRightClick for ItemSlimeBag for hand = "+parHand);
	    
	        ItemStack itemStack = parPlayer.getHeldItem(parHand);

	        // clicked on a block?
	        RayTraceResult mop = rayTrace(parWorld, parPlayer, true);

	        if(mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK)
	        {
	            return ActionResult.newResult(EnumActionResult.PASS, itemStack);
	        }

	        // DEBUG
	        System.out.println("Slime bag used");
	        
	        BlockPos clickPos = mop.getBlockPos();
	                
	        FluidStack fluidStack = getFluidStack(itemStack);
	            	
	        // can we place block there?
	        if (parWorld.isBlockModifiable(parPlayer, clickPos))
	        {
	            // the block adjacent to the side we clicked on
	            BlockPos targetPos = clickPos.offset(mop.sideHit);

	            // can the player place there?
	            if (parPlayer.canPlayerEdit(targetPos, mop.sideHit, itemStack))
	            {
	                if (fluidStack == null || fluidStack.amount <= 0)
	                {
	                	// DEBUG
	                	System.out.println("Fluid stack in item is empty so try to fill");
	                	
	                	return tryFillAlt(parWorld, parPlayer, mop, itemStack);
	                }
	                else
	                {     
	                    // DEBUG
	                    System.out.println("Fluid stack is not empty so try to place");

	                	return tryPlaceAlt(parWorld, parPlayer, targetPos, itemStack);               	
	                }
	            }
	            else // player cannot edit
	            {
	    	        // DEBUG
	    	        System.out.println("Failed to place fluid because player cannot edit");
	    	
	    	        // couldn't place liquid there
	    	        return ActionResult.newResult(EnumActionResult.FAIL, itemStack);
	            }
	         }
	        else // cannot place blocks at that location
	        {
		        // DEBUG
		        System.out.println("Failed to place fluid because location not modifiable");
		
		        // couldn't place liquid there
		        return ActionResult.newResult(EnumActionResult.FAIL, itemStack);
	        }
	    }
	    
	    /**
	     * Try place alt.
	     *
	     * @param parWorld the par world
	     * @param parPlayer the par player
	     * @param parPos the par pos
	     * @param parStack the par stack
	     * @return the action result
	     */
	    public ActionResult<ItemStack> tryPlaceAlt(World parWorld, EntityPlayer parPlayer, BlockPos parPos, ItemStack parStack)
	    {
	    	ActionResult<ItemStack> resultPass = new ActionResult<ItemStack>(EnumActionResult.PASS, parStack);
	    	ActionResult<ItemStack> resultFail = new ActionResult<ItemStack>(EnumActionResult.FAIL, parStack);
	    	
	        if (parWorld == null || parPos == null) // not valid location to attempt to place
	        {
	        	// DEBUG
	        	System.out.println("not valid location to place at");
	        	
	            return resultFail;
	        }
	        else // valid location to attempt to place
	        {
	        	// DEBUG
	        	System.out.println("Valid location to place at");
	        	
		        IFluidHandlerItem containerFluidHandler = FluidUtil.getFluidHandler(parStack);
		        
		        if (containerFluidHandler == null) // itemstack not really a fluid handler
		        {
		        	// DEBUG
		        	System.out.println("Item stack not really a fluid handling container");
		        	
		        	return resultFail;
		        }
		        else // itemstack is a valid fluid handler
		        {
		        	// DEBUG
		        	System.out.println("Item stack has a fluid handler");
		        	
			    	FluidStack containerFluidStack = getFluidStack(parStack);
			    	
			    	if (containerFluidStack == null || containerFluidStack.amount <= 0) // not actual fluid stack in container
			    	{
			    		// DEBUG
			    		System.out.println("No actual fluid in container");
			    		
			    		return resultFail;
			    	}
			    	else // there is actual fluid stack in container
			    	{
			    		// DEBUG
			    		System.out.println("There is a fluid stack in the container");
			    		
			            Fluid fluid = containerFluidStack.getFluid();
			            if (fluid == null ) // no fluid associated with fluid stack
			            {
			            	// DEBUG
			            	System.out.println("Malformed fluid stack has null fluid");
			            	
			                return resultFail;
			            }
			            else // fluid associated with fluid stack
			            {
			            	if (!fluid.canBePlacedInWorld()) // fluid cannot be placed in world
			            	{
			            		// DEBUG
			            		System.out.println("Fluid type doesn't allow placement in world");
			            		
			            		return resultFail;
			            	}
			            	else // fluid can be placed in world
			            	{
			            		// DEBUG
			            		System.out.println("Fluid type allows placement in world");
			            		
			                    // check that we can place the fluid at the destination
			                    IBlockState destBlockState = parWorld.getBlockState(parPos);
			                    if (!parWorld.isAirBlock(parPos) && destBlockState.getMaterial().isSolid() && !destBlockState.getBlock().isReplaceable(parWorld, parPos))
			                    {
			                    	// DEBUG
			                    	System.out.println("Location is not replaceable");
			                    	
			                        return resultFail; // Non-air, solid, unreplacable block. We can't put fluid here.
			                    }
			                    else // location is placeable
			                    {
			                    	// DEBUG
			                    	System.out.println("Location is replaceable");
			                    	
			                        if (parWorld.provider.doesWaterVaporize() && fluid.doesVaporize(containerFluidStack))
			                        {
			                                fluid.vaporize(parPlayer, parWorld, parPos, containerFluidStack);
			                                return ActionResult.newResult(EnumActionResult.SUCCESS, parStack);
			                        }
			                        else // fluid does not vaporize
			                        {
			                            // This fluid handler places the fluid block when filled
			                            Block blockToPlace = fluid.getBlock();
			                            IFluidHandler blockFluidHandler;
			                            if (blockToPlace instanceof IFluidBlock)
			                            {
			                                blockFluidHandler = new FluidBlockWrapper((IFluidBlock) blockToPlace, parWorld, parPos);
			                            }
			                            else if (blockToPlace instanceof BlockLiquid)
			                            {
			                                blockFluidHandler = new BlockLiquidWrapper((BlockLiquid) blockToPlace, parWorld, parPos);
			                            }
			                            else
			                            {
			                                blockFluidHandler = new BlockWrapper(blockToPlace, parWorld, parPos);
			                            }
			                            
			                            // actually transfer fluid
			                            int blockCapacity = blockFluidHandler.getTankProperties()[0].getCapacity();
			                            int amountInContainer = containerFluidStack.amount;
			                            
			                            FluidStack blockFluidStack = blockFluidHandler.getTankProperties()[0].getContents();
			                            if (blockFluidStack == null)
			                            {
			                            	// DEBUG
			                            	System.out.println("Block fluid stack is null");
			                            	
			                            	return resultFail;
			                            }
			                            else // non-null fluid stack
			                            {
				                            // DEBUG
				                            System.out.println("Before transferring fluids amount in container = "+amountInContainer+" and block capacity = "+blockCapacity);
				                            
				                            // transfer amounts and handle cases of differences between amounts and capacities
				                            if (amountInContainer > blockCapacity) // more than enough fluid to fill block
				                            {
				                            	containerFluidStack.amount -= blockCapacity;
				                            	blockFluidStack.amount = blockCapacity;
				                            }
				                            else // all fluid in container can fit within block
				                            {
				                            	blockFluidStack.amount = amountInContainer;
				                            	containerFluidStack.amount = 0;
				                            }
				                            
				                            // DEBUG
				                            System.out.println("After transferring amount in container = "+containerFluidStack.amount+" and amount in block = "+blockFluidStack.amount);
				                            
			                                SoundEvent soundevent = fluid.getEmptySound(containerFluidStack);
			                                parWorld.playSound(parPlayer, parPos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
		
			                    	        if (!parPlayer.capabilities.isCreativeMode)
			                    	        {
			                    	            // DEBUG
			                    	            System.out.println("Not in creative so draining container");
			                    	
			                    	            // success!
			                    	            parPlayer.addStat(StatList.getObjectUseStats(this));
			                    	
			                    	            // clamp value to non-negative
			                    	            if (containerFluidStack.amount <= 0)
			                    	            {
			                    	            	containerFluidStack.amount = 0; 
			                    	            	
			                    	            	// DEBUG
			                    	            	System.out.println("fully drained the container so returning empty container");
			                    	            }
		                    	            	
				    							// update tag data
		                    	            	updateFluidNBT(parStack, containerFluidStack);
				    					        
				    					        // send packet to update player
		                    	            	sendUpdatePacketToClient(parPlayer);
				    							
				    							// DEBUG
				    							System.out.println("After transfer block fluid stack = "+blockFluidStack.getFluid()+" "+blockFluidStack.amount+" and container fluid stack now = "+containerFluidStack.getFluid()+" "+containerFluidStack.amount);
				            					                    	        	
				                    	        parWorld.setBlockState(parPos, blockToPlace.getDefaultState());

			                    	        	// DEBUG
			                    	        	System.out.println("Placing fluid was a success");
			                    	        	
				                                return ActionResult.newResult(EnumActionResult.SUCCESS, containerFluidHandler.getContainer());
			                    	        }
			                    	        else // in creative mode so don't use up stack
			                    	        {
			                    	        	// restore amount
			                    	        	containerFluidStack.amount = amountInContainer;
			                    	        	
										        parWorld.setBlockState(parPos, blockToPlace.getDefaultState());
										
										    	// DEBUG
										    	System.out.println("Placing fluid was a success");
										    	
			                    	        	return resultPass; // not really sure why fail, but consistent with universal bucket
			                    	        }
			                            }
			                        }
			                    }
			            	}
			            }
			    	}
		        }
	        }
	    }
	   
	    private void sendUpdatePacketToClient(EntityPlayer parPlayer) 
	    {
	    	
			if (parPlayer instanceof EntityPlayerMP)
			{
				// DEBUG
				System.out.println("Sending player inventory update");
		        ((EntityPlayerMP)parPlayer).connection.sendPacket(new SPacketHeldItemChange(parPlayer.inventory.currentItem));
			}
			else
			{
				// do nothing
			}		
		}

		private void updateFluidNBT(ItemStack parItemStack, FluidStack parFluidStack) 
	    {
	        if (!parItemStack.hasTagCompound())
	        {
	            parItemStack.setTagCompound(new NBTTagCompound());
	        }
	        NBTTagCompound fluidTag = new NBTTagCompound();
	        parFluidStack.writeToNBT(fluidTag);
	        parItemStack.getTagCompound().setTag(FluidHandlerItemStack.FLUID_NBT_KEY, fluidTag);		
			
			// DEBUG
			System.out.println("Wrote fluid tag to container item stack = "+fluidTag);
		}

		/**
		 * Gets the matching fluid stack.
		 *
		 * @param sourceHandler the source handler
		 * @param parFluid the par fluid
		 * @return the matching fluid stack
		 */
		@Nullable
	    public FluidStack getMatchingFluidStack(IFluidHandler sourceHandler, Fluid parFluid)
	    {
	    	// Theoretically a tank may contain mulitple fluid stacks
	    	// grab first one that matches fluid type
	    	IFluidTankProperties[] tankProperties = sourceHandler.getTankProperties();
	    	FluidStack result = null;
	    	for (int i=0; i < tankProperties.length; i++)
	    	{
	    		if (tankProperties[i].getContents().getFluid() == parFluid)
	    		{
	    			result = tankProperties[i].getContents();
	    		}
	    		else
	    		{
	    			// do nothing
	    		}
	    	}
	    	
	    	return result;
	    }
	    
	    /**
	     * Gets the fluid stack.
	     *
	     * @param container the container
	     * @return the fluid stack
	     */
	    @Nullable
		public FluidStack getFluidStack(final ItemStack container) 
	    {
			if (container.hasTagCompound() && container.getTagCompound().hasKey(FluidHandlerItemStack.FLUID_NBT_KEY)) {
				return FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag(FluidHandlerItemStack.FLUID_NBT_KEY));
			}
			return null;
		}
	    
	    /**
	     * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
	     *
	     * @return the share tag
	     */
	    @Override
		public boolean getShareTag()
	    {
	        return true;
	    }
	    
	    /* (non-Javadoc)
	     * @see net.minecraft.item.Item#getNBTShareTag(net.minecraft.item.ItemStack)
	     */
	    @Override
		public NBTTagCompound getNBTShareTag(ItemStack stack)
	    {
	    	// DEBUG
	    	System.out.println("tag compound = "+stack.getTagCompound());
	    	return super.getNBTShareTag(stack);
	    }
	    
	    /**
	     * Try fill alt.
	     *
	     * @param parWorld the par world
	     * @param parPlayer the par player
	     * @param mop the mop
	     * @param parContainerStack the par container stack
	     * @return the action result
	     */
	    public ActionResult<ItemStack> tryFillAlt(World parWorld, EntityPlayer parPlayer, RayTraceResult mop, ItemStack parContainerStack)
	    {
	    	ActionResult<ItemStack> resultPass = new ActionResult<ItemStack>(EnumActionResult.PASS, parContainerStack);
	    	ActionResult<ItemStack> resultFail = new ActionResult<ItemStack>(EnumActionResult.FAIL, parContainerStack);
	    	BlockPos blockPos = mop.getBlockPos();
	    	
	    	if (parWorld == null || blockPos == null || parContainerStack.isEmpty())
	    	{
	    		// DEBUG
	    		System.out.println("invalid parameters (null or empty");
	    		
	    		return resultPass;
	    	}
	    	else // parameters are valid
	    	{
	    		// that there is fluid or liquid block at the position
		        Block block = parWorld.getBlockState(blockPos).getBlock();
		        if (block instanceof IFluidBlock || block instanceof BlockLiquid)
		        {
		            IFluidHandler sourceFluidHandler = FluidUtil.getFluidHandler(parWorld, blockPos, mop.sideHit);
		            if (sourceFluidHandler != null) // valid fluid block
		            {
			        	// DEBUG
			        	System.out.println("Found valid fluid block");
			        	
		            	IFluidHandlerItem containerFluidHandler = FluidUtil.getFluidHandler(parContainerStack);
		            	if (containerFluidHandler != null) // valid fluid item
		            	{
		            		// DEBUG
		            		System.out.println("With valid fluid item");
		            		
		            		FluidStack containerFluidStack = getFluidStack(parContainerStack);
		            		// handlw case where fluid stack is null by replacing it with amount zero
		            		if (containerFluidStack == null)
		            		{
		            			containerFluidStack = new FluidStack(ModFluids.SLIME, 0);
		            		}
		            		int amountRoomInContainer = CAPACITY - containerFluidStack.amount;
		            		if (amountRoomInContainer <= 0)
		            		{
		            			// DEBUG
		            			System.out.println("No room in container, already full");
		            			
		            			return resultPass;
		            		}
		            		else // room in container
		            		{
		            			// DEBUG
		            			System.out.println("There is room in the container");
		            			
		            			FluidStack sourceFluidStack = getMatchingFluidStack(sourceFluidHandler, ModFluids.SLIME);
		            			if (sourceFluidStack != null)
		            			{
		            				int amountInSource = sourceFluidStack.amount;
		            				if (amountInSource <= 0) // not enough fluid in source
		            				{
		            					// DEBUG
		            					System.out.println("Not enough fluid in source");
		            					
		            					return resultPass;

		            				}
		            				else // some fluid in source
		            				{
		            					// DEBUG
		            					System.out.println("There is some fluid in source");
		    							System.out.println("Before transfer source fluid stack = "+sourceFluidStack.getFluid()+" "+sourceFluidStack.amount+" and container fluid stack now = "+containerFluidStack.getFluid()+" "+containerFluidStack.amount);
		            					
		    							// check whether enough to fill container with some to spare
		    							if (sourceFluidStack.amount > amountRoomInContainer) // some to spare	
	    								{	
		    								containerFluidStack.amount = CAPACITY;
		    								sourceFluidStack.amount -= amountRoomInContainer;
		    							}
		    							else // no extra in source after filling containder
		    							{
		    								containerFluidStack.amount = sourceFluidStack.amount;
		    								sourceFluidStack.amount = 0; // used all source amount
		    								parWorld.setBlockToAir(blockPos);
		    							}
		            					
		    	                        SoundEvent soundevent = containerFluidStack.getFluid().getFillSound(containerFluidStack);
		    							parPlayer.playSound(soundevent, 1f, 1f);
		    							
		    							// update tag data
	                	            	updateFluidNBT(parContainerStack, containerFluidStack);
		    					        
		    					        // send packet to update player
	                	            	sendUpdatePacketToClient(parPlayer);
		    							
		    							// DEBUG
		    							System.out.println("After transfer source fluid stack = "+sourceFluidStack.getFluid()+" "+sourceFluidStack.amount+" and container fluid stack now = "+containerFluidStack.getFluid()+" "+containerFluidStack.amount);
		            					
		            					return ActionResult.newResult(EnumActionResult.SUCCESS, containerFluidHandler.getContainer());
		            				}
		            			}
		            			else // could not find fluid in block that matches itemstack
		            			{
		            				// DEBUG
		            				System.out.println("No matching fluid in block");
		            				
		            				return resultPass;
		            			}
		            		}
		            	}
		            	else // not a proper fluid item
		            	{
			            	// DEBUG
			            	System.out.println("Malformed fluid item at position "+parContainerStack);
			            	
			            	return resultFail;
		            	}
		            }
		            else // not a proper fluid block
		            {
		            	// DEBUG
		            	System.out.println("Malformed fluid block at position = "+blockPos);
		            	
		            	return resultFail;
		            }
		        }
		        else
		        {
		        	// DEBUG
		        	System.out.println("Not a fluid block in that location");
		        	
		        	return resultPass;
		        }
	    	}
	    }
	 }
	
	public static Field activeItemStackUseCount = ReflectionHelper.findField(EntityLivingBase.class, "activeItemStackUseCount", "field_184628_bn");
	public static Field handInventory = ReflectionHelper.findField(EntityLivingBase.class, "handInventory", "field_184630_bs");
	public static Field armorArray = ReflectionHelper.findField(EntityLivingBase.class, "armorArray", "field_184631_bt");
	public static Field ticksElytraFlying = ReflectionHelper.findField(EntityLivingBase.class, "ticksElytraFlying", "field_184629_bo");
	public static Field rideCooldown = ReflectionHelper.findField(Entity.class, "rideCooldown", "field_184245_j");
	public static Field portalCounter = ReflectionHelper.findField(Entity.class, "portalCounter", "field_82153_h");
	public static Field inPortal = ReflectionHelper.findField(Entity.class, "inPortal", "field_71087_bX");
	public static Field fire = ReflectionHelper.findField(Entity.class, "fire", "field_190534_ay");
	public static Field prevBlockpos = ReflectionHelper.findField(EntityLivingBase.class, "prevBlockpos", "field_184620_bC");
	public static Field firstUpdate = ReflectionHelper.findField(Entity.class, "firstUpdate", "field_70148_d");
	public static Field attackingPlayer = ReflectionHelper.findField(EntityLivingBase.class, "attackingPlayer", "field_70717_bb");
	public static Field recentlyHit = ReflectionHelper.findField(EntityLivingBase.class, "recentlyHit", "field_70718_bc");
	
	public static Method setFlag = ReflectionHelper.findMethod(Entity.class, "setFlag", "func_70052_a", Integer.TYPE, Boolean.TYPE); 
	public static Method getFlag = ReflectionHelper.findMethod(Entity.class, "getFlag", "func_70083_f", Integer.TYPE); 
	public static Method decrementTimeUntilPortal = ReflectionHelper.findMethod(Entity.class, "decrementTimeUntilPortal", "func_184173_H", new Class[] {});
	public static Method updatePotionEffects = ReflectionHelper.findMethod(EntityLivingBase.class, "updatePotionEffects", "func_70679_bo", new Class[] {});
	public static Method onDeathUpdate = ReflectionHelper.findMethod(EntityLivingBase.class, "onDeathUpdate", "func_70609_aI", new Class[] {});
	
    /**
     * In order to allow custom fluids to cause reduction of air supply
     * and drowning damage, need to copy almost entirety of vanilla functions
     * and modify the hard-coded WATER to accept other fluids.
     *
     * @param event the event
     * @throws IllegalArgumentException the illegal argument exception
     * @throws IllegalAccessException the illegal access exception
     */
    @SuppressWarnings("unchecked")
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public static void onEvent(LivingUpdateEvent event) throws IllegalArgumentException, IllegalAccessException
    {
    	if (!ENABLED) { return; }
    	
    	event.setCanceled(true);
    	
    	EntityLivingBase theEntity = event.getEntityLiving();
     	
        // super.onUpdate() expanded
        if (!theEntity.world.isRemote)
        {
            try {
				setFlag.invoke(theEntity, 6, theEntity.isGlowing());
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
        }

        // theEntity.onEntityUpdate();
        // onEntityUpdate() expanded
        theEntity.prevSwingProgress = theEntity.swingProgress;
        // super.onEntityUpdate();
        // super onEntityUpdage() expanded
        theEntity.world.profiler.startSection("entityBaseTick");

        if (theEntity.isRiding() && theEntity.getRidingEntity().isDead)
        {
            theEntity.dismountRidingEntity();
        }

        if (rideCooldown.getInt(theEntity) > 0)
        {
        	rideCooldown.setInt(theEntity, rideCooldown.getInt(theEntity) - 1);
        }

        theEntity.prevDistanceWalkedModified = theEntity.distanceWalkedModified;
        theEntity.prevPosX = theEntity.posX;
        theEntity.prevPosY = theEntity.posY;
        theEntity.prevPosZ = theEntity.posZ;
        theEntity.prevRotationPitch = theEntity.rotationPitch;
        theEntity.prevRotationYaw = theEntity.rotationYaw;

        if (!theEntity.world.isRemote && theEntity.world instanceof WorldServer)
        {
            theEntity.world.profiler.startSection("portal");

            if (inPortal.getBoolean(theEntity))
            {
                MinecraftServer minecraftserver = theEntity.world.getMinecraftServer();

                if (minecraftserver.getAllowNether())
                {
                    if (!theEntity.isRiding())
                    {
                        int i = theEntity.getMaxInPortalTime();

                        portalCounter.setInt(theEntity, portalCounter.getInt(theEntity) + 1);
                        if (portalCounter.getInt(theEntity) >= i)
                        {
                            portalCounter.set(theEntity, i);
                            theEntity.timeUntilPortal = theEntity.getPortalCooldown();
                            int j;

                            if (theEntity.world.provider.getDimensionType().getId() == -1)
                            {
                                j = 0;
                            }
                            else
                            {
                                j = -1;
                            }

                            theEntity.changeDimension(j);
                        }
                    }

                    inPortal.set(theEntity, false);
                }
            }
            else
            {
                if (portalCounter.getInt(theEntity) > 0)
                {
                    portalCounter.setInt(theEntity, portalCounter.getModifiers() - 4);
                }

                if (portalCounter.getInt(theEntity) < 0)
                {
                    portalCounter.setInt(theEntity, 0);
                }
            }

            try {
				decrementTimeUntilPortal.invoke(theEntity, new Object[] {});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
            theEntity.world.profiler.endSection();
        }

        theEntity.spawnRunningParticles();
        theEntity.handleWaterMovement();

        if (theEntity.world.isRemote)
        {
            theEntity.extinguish();
        }
        else if (fire.getInt(theEntity) > 0)
        {
            if (theEntity.isImmuneToFire())
            {
                fire.setInt(theEntity, fire.getInt(theEntity) - 4);

                if (fire.getInt(theEntity) < 0)
                {
                    theEntity.extinguish();
                }
            }
            else
            {
                if (fire.getInt(theEntity) % 20 == 0)
                {
                    theEntity.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
                }

                theEntity.setFire(fire.getInt(theEntity));
            }
        }

        if (theEntity.isInLava())
        {
        	// setOnFireFromLava expanded
            // theEntity.setOnFireFromLava();
            if (!theEntity.isImmuneToFire())
            {
                theEntity.attackEntityFrom(DamageSource.LAVA, 4.0F);
                theEntity.setFire(15);
            }

            theEntity.fallDistance *= 0.5F;
        }

        if (theEntity.posY < -64.0D)
        {
        	// onDeathUpdate expanded
            theEntity.attackEntityFrom(DamageSource.OUT_OF_WORLD, 4.0F);
        }

        if (!theEntity.world.isRemote)
        {
            try {
				setFlag.invoke(theEntity, 0, fire.getInt(theEntity) > 0);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
        }

        firstUpdate.setBoolean(theEntity, false);
        theEntity.world.profiler.endSection();
        theEntity.world.profiler.startSection("livingEntityBaseTick");
        boolean flag = theEntity instanceof EntityPlayer;

        if (theEntity.isEntityAlive())
        {
            if (theEntity.isEntityInsideOpaqueBlock())
            {
                theEntity.attackEntityFrom(DamageSource.IN_WALL, 1.0F);
            }
            else if (flag && !theEntity.world.getWorldBorder().contains(theEntity.getEntityBoundingBox()))
            {
                double d0 = theEntity.world.getWorldBorder().getClosestDistance(theEntity) + theEntity.world.getWorldBorder().getDamageBuffer();

                if (d0 < 0.0D)
                {
                    double d1 = theEntity.world.getWorldBorder().getDamageAmount();

                    if (d1 > 0.0D)
                    {
                        theEntity.attackEntityFrom(DamageSource.IN_WALL, Math.max(1, MathHelper.floor(-d0 * d1)));
                    }
                }
            }
        }

        if (theEntity.isImmuneToFire() || theEntity.world.isRemote)
        {
            theEntity.extinguish();
        }

        boolean flag1 = flag && ((EntityPlayer)theEntity).capabilities.disableDamage;

        if (theEntity.isEntityAlive())
        {
        	/*
        	 * Modified this so that custom fluids can suffocate
        	 */
            if (!theEntity.isInsideOfMaterial(Material.WATER) && !theEntity.isInsideOfMaterial(ModMaterials.SLIME))
            {
                theEntity.setAir(300);
            }
            else
            {
                if (!theEntity.canBreatheUnderwater() && !theEntity.isPotionActive(MobEffects.WATER_BREATHING) && !flag1)
                {	                	
                	// decreaseAirSupply() expanded
                    theEntity.setAir(EnchantmentHelper.getRespirationModifier(theEntity) > 0 && theEntity.getRNG().nextInt(EnchantmentHelper.getRespirationModifier(theEntity) + 1) > 0 ? theEntity.getAir() : theEntity.getAir() - 1);

                    if (theEntity.getAir() == -20)
                    {
                        theEntity.setAir(0);

                        for (int i = 0; i < 8; ++i)
                        {
                            float f2 = theEntity.getRNG().nextFloat() - theEntity.getRNG().nextFloat();
                            float f = theEntity.getRNG().nextFloat() - theEntity.getRNG().nextFloat();
                            float f1 = theEntity.getRNG().nextFloat() - theEntity.getRNG().nextFloat();
                            theEntity.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, theEntity.posX + f2, theEntity.posY + f, theEntity.posZ + f1, theEntity.motionX, theEntity.motionY, theEntity.motionZ);
                        }

                        theEntity.attackEntityFrom(DamageSource.DROWN, 2.0F);
                    }
                }

                if (!theEntity.world.isRemote && theEntity.isRiding() && theEntity.getRidingEntity() != null && theEntity.getRidingEntity().shouldDismountInWater(theEntity))
                {
                    theEntity.dismountRidingEntity();
                }
            }

            if (!theEntity.world.isRemote)
            {
                BlockPos blockpos = new BlockPos(theEntity);

                if (!Objects.equal(prevBlockpos.get(theEntity), blockpos))
                {
                    prevBlockpos.set(theEntity, blockpos);
                    // theEntity.frostWalk(blockpos);
                    // frostWalk() expanded
                    int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FROST_WALKER, theEntity);

                    if (i > 0)
                    {
                        EnchantmentFrostWalker.freezeNearby(theEntity, theEntity.world, blockpos, i);
                    }
                }
            }
        }

        if (theEntity.isEntityAlive() && theEntity.isWet())
        {
            theEntity.extinguish();
        }

        theEntity.prevCameraPitch = theEntity.cameraPitch;

        if (theEntity.hurtTime > 0)
        {
            --theEntity.hurtTime;
        }

        if (theEntity.hurtResistantTime > 0 && !(theEntity instanceof EntityPlayerMP))
        {
            --theEntity.hurtResistantTime;
        }

        if (theEntity.getHealth() <= 0.0F)
        {
            try {
				onDeathUpdate.invoke(theEntity, new Object[] {});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
        }

        if (recentlyHit.getInt(theEntity) > 0)
        {
            recentlyHit.setInt(theEntity, recentlyHit.getInt(theEntity) - 1);;
        }
        else
        {
            attackingPlayer.set(theEntity, null);
        }

        if (theEntity.getLastAttackedEntity() != null && !theEntity.getLastAttackedEntity().isEntityAlive())
        {
            attackingPlayer.set(theEntity, null);
        }

        if (theEntity.getRevengeTarget() != null)
        {
            if (!theEntity.getRevengeTarget().isEntityAlive())
            {
                theEntity.setRevengeTarget((EntityLivingBase)null);
            }
            else if (theEntity.ticksExisted - theEntity.getRevengeTimer() > 100)
            {
                theEntity.setRevengeTarget((EntityLivingBase)null);
            }
        }

        try {
			updatePotionEffects.invoke(theEntity, new Object[] {});
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
        // theEntity.prevMovedDistance = theEntity.movedDistance;
        theEntity.prevRenderYawOffset = theEntity.renderYawOffset;
        theEntity.prevRotationYawHead = theEntity.rotationYawHead;
        theEntity.prevRotationYaw = theEntity.rotationYaw;
        theEntity.prevRotationPitch = theEntity.rotationPitch;
        theEntity.world.profiler.endSection();

        // updateActiveHand() method expanded
        if (theEntity.isHandActive())
        {
            ItemStack itemstack = theEntity.getHeldItem(theEntity.getActiveHand());

            if (itemstack == theEntity.getActiveItemStack())
            {
                if (!theEntity.getActiveItemStack().isEmpty())
                {

                    try {
						activeItemStackUseCount.setInt(theEntity, net.minecraftforge.event.ForgeEventFactory.onItemUseTick(theEntity, theEntity.getActiveItemStack(), activeItemStackUseCount.getInt(theEntity)));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
                    try {
						if (activeItemStackUseCount.getInt(theEntity) > 0)
						    theEntity.getActiveItemStack().getItem().onUsingTick(theEntity.getActiveItemStack(), theEntity, activeItemStackUseCount.getInt(theEntity));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
                }

                if (theEntity.getItemInUseCount() <= 25 && theEntity.getItemInUseCount() % 4 == 0)
                {
                    // theEntity.updateItemUse(theEntity.getActiveItemStack(), 5);
                    // updateItemUse() expanded
                    ItemStack stack = theEntity.getActiveItemStack();
                    int eatingParticleCount = 5;
                    if (!stack.isEmpty() && theEntity.isHandActive())
                    {
                        if (stack.getItemUseAction() == EnumAction.DRINK)
                        {
                            theEntity.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5F, theEntity.world.rand.nextFloat() * 0.1F + 0.9F);
                        }

                        if (stack.getItemUseAction() == EnumAction.EAT)
                        {
                            for (int i = 0; i < eatingParticleCount; ++i)
                            {
                                Vec3d vec3d = new Vec3d((theEntity.world.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                                vec3d = vec3d.rotatePitch(-theEntity.rotationPitch * 0.017453292F);
                                vec3d = vec3d.rotateYaw(-theEntity.rotationYaw * 0.017453292F);
                                double d0 = (-theEntity.world.rand.nextFloat()) * 0.6D - 0.3D;
                                Vec3d vec3d1 = new Vec3d((theEntity.world.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
                                vec3d1 = vec3d1.rotatePitch(-theEntity.rotationPitch * 0.017453292F);
                                vec3d1 = vec3d1.rotateYaw(-theEntity.rotationYaw * 0.017453292F);
                                vec3d1 = vec3d1.addVector(theEntity.posX, theEntity.posY + theEntity.getEyeHeight(), theEntity.posZ);

                                if (stack.getHasSubtypes())
                                {
                                    theEntity.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z, Item.getIdFromItem(stack.getItem()), stack.getMetadata());
                                }
                                else
                                {
                                    theEntity.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z, Item.getIdFromItem(stack.getItem()));
                                }
                            }

                            theEntity.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5F + 0.5F * theEntity.world.rand.nextInt(2), (theEntity.world.rand.nextFloat() - theEntity.world.rand.nextFloat()) * 0.2F + 1.0F);
                        }
                    }                 
                }
                try {
					activeItemStackUseCount.setInt(theEntity, activeItemStackUseCount.getInt(theEntity) - 1);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
                try {
					if (activeItemStackUseCount.getInt(theEntity) - 1 <= 0 && !theEntity.world.isRemote)
					{
					    // theEntity.onItemUseFinish();
						// onITemUseFinish() expanded
					    if (!theEntity.getActiveItemStack().isEmpty() && theEntity.isHandActive())
					    {
					    	{
					    		// theEntity.updateItemUse(theEntity.getActiveItemStack(), 16);
					    		// updateItemUse expanded
					            ItemStack stack = theEntity.getActiveItemStack();
					            int eatingParticleCount = 16;
					            if (!stack.isEmpty() && theEntity.isHandActive())
					            {
					                if (stack.getItemUseAction() == EnumAction.DRINK)
					                {
					                    theEntity.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5F, theEntity.world.rand.nextFloat() * 0.1F + 0.9F);
					                }

					                if (stack.getItemUseAction() == EnumAction.EAT)
					                {
					                    for (int i = 0; i < eatingParticleCount; ++i)
					                    {
					                        Vec3d vec3d = new Vec3d((theEntity.world.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
					                        vec3d = vec3d.rotatePitch(-theEntity.rotationPitch * 0.017453292F);
					                        vec3d = vec3d.rotateYaw(-theEntity.rotationYaw * 0.017453292F);
					                        double d0 = (-theEntity.world.rand.nextFloat()) * 0.6D - 0.3D;
					                        Vec3d vec3d1 = new Vec3d((theEntity.world.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
					                        vec3d1 = vec3d1.rotatePitch(-theEntity.rotationPitch * 0.017453292F);
					                        vec3d1 = vec3d1.rotateYaw(-theEntity.rotationYaw * 0.017453292F);
					                        vec3d1 = vec3d1.addVector(theEntity.posX, theEntity.posY + theEntity.getEyeHeight(), theEntity.posZ);

					                        if (stack.getHasSubtypes())
					                        {
					                            theEntity.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z, Item.getIdFromItem(stack.getItem()), stack.getMetadata());
					                        }
					                        else
					                        {
					                            theEntity.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z, Item.getIdFromItem(stack.getItem()));
					                        }
					                    }

					                    theEntity.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5F + 0.5F * theEntity.world.rand.nextInt(2), (theEntity.world.rand.nextFloat() - theEntity.world.rand.nextFloat()) * 0.2F + 1.0F);
					                }
					            }                 

					    	}
					    	ItemStack itemstack2 = theEntity.getActiveItemStack().onItemUseFinish(theEntity.world, theEntity);
					        itemstack2 = net.minecraftforge.event.ForgeEventFactory.onItemUseFinish(theEntity, theEntity.getActiveItemStack(), theEntity.getItemInUseCount(), itemstack2);
					        theEntity.setHeldItem(theEntity.getActiveHand(), itemstack2);
					        theEntity.resetActiveHand();
					    }

					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
            }
            else
            {
                theEntity.resetActiveHand();
            }
        }


        if (!theEntity.world.isRemote)
        {
            int i = theEntity.getArrowCountInEntity();

            if (i > 0)
            {
                if (theEntity.arrowHitTimer <= 0)
                {
                    theEntity.arrowHitTimer = 20 * (30 - i);
                }

                --theEntity.arrowHitTimer;

                if (theEntity.arrowHitTimer <= 0)
                {
                    theEntity.setArrowCountInEntity(i - 1);
                }
            }

            for (EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values())
            {
                ItemStack itemstack = ItemStack.EMPTY;

                switch (entityequipmentslot.getSlotType())
                {
                    case HAND:
					try {
						itemstack = ((NonNullList<ItemStack>)handInventory.get(theEntity)).get(entityequipmentslot.getIndex());
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
                        break;
                    case ARMOR:
					try {
						itemstack = ((NonNullList<ItemStack>)armorArray.get(theEntity)).get(entityequipmentslot.getIndex());
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
                        break;
                    default:
                        continue;
                }

                ItemStack itemstack1 = theEntity.getItemStackFromSlot(entityequipmentslot);

                if (!ItemStack.areItemStacksEqual(itemstack, itemstack1))
                {
                    if (!ItemStack.areItemStacksEqualUsingNBTShareTag(itemstack1, itemstack))
                    ((WorldServer)theEntity.world).getEntityTracker().sendToTracking(theEntity, new SPacketEntityEquipment(theEntity.getEntityId(), entityequipmentslot, itemstack1));
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent(theEntity, entityequipmentslot, itemstack, itemstack1));

                    if (!itemstack.isEmpty())
                    {
                        theEntity.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers(entityequipmentslot));
                    }

                    if (!itemstack1.isEmpty())
                    {
                        theEntity.getAttributeMap().applyAttributeModifiers(itemstack1.getAttributeModifiers(entityequipmentslot));
                    }

                    switch (entityequipmentslot.getSlotType())
                    {
                        case HAND:
                            ((NonNullList<ItemStack>)handInventory.get(theEntity)).set(entityequipmentslot.getIndex(), itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1.copy());
                            break;
                        case ARMOR:
                            ((NonNullList<ItemStack>)armorArray.get(theEntity)).set(entityequipmentslot.getIndex(), itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1.copy());
                    }
                }
            }

            if (theEntity.ticksExisted % 20 == 0)
            {
                theEntity.getCombatTracker().reset();
            }

            if (!theEntity.isGlowing())
            {
                try {
					if (((boolean)getFlag.invoke(theEntity, 6)) != theEntity.isPotionActive(MobEffects.GLOWING))
					{
					    setFlag.invoke(theEntity, 6, theEntity.isPotionActive(MobEffects.GLOWING));
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
            }
        }

        theEntity.onLivingUpdate();
        double d0 = theEntity.posX - theEntity.prevPosX;
        double d1 = theEntity.posZ - theEntity.prevPosZ;
        float f3 = (float)(d0 * d0 + d1 * d1);
        float f4 = theEntity.renderYawOffset;
        float f5 = 0.0F;
        if (f3 > 0.0025000002F)
        {
            f5 = (float)Math.sqrt(f3) * 3.0F;
            float f1 = (float)MathHelper.atan2(d1, d0) * (180F / (float)Math.PI) - 90.0F;
            float f2 = MathHelper.abs(MathHelper.wrapDegrees(theEntity.rotationYaw) - f1);

            if (95.0F < f2 && f2 < 265.0F)
            {
                f4 = f1 - 180.0F;
            }
            else
            {
                f4 = f1;
            }
        }

        if (theEntity.swingProgress > 0.0F)
        {
            f4 = theEntity.rotationYaw;
        }

        if (!theEntity.onGround)
        {
        }

        // theEntity.onGroundSpeedFactor += (f - theEntity.onGroundSpeedFactor) * 0.3F;
        theEntity.world.profiler.startSection("headTurn");
        // f5 = theEntity.updateDistance(f4, f5);
        // updateDistance expanded
        float p_110146_1_ = f4;
        float p_110146_2_ = f5;
        theEntity.renderYawOffset += MathHelper.wrapDegrees(p_110146_1_ - theEntity.renderYawOffset) * 0.3F;
        float f1 = MathHelper.wrapDegrees(theEntity.rotationYaw - theEntity.renderYawOffset);
        boolean flagx = f1 < -90.0F || f1 >= 90.0F;

        if (f1 < -75.0F)
        {
            f1 = -75.0F;
        }

        if (f1 >= 75.0F)
        {
            f1 = 75.0F;
        }

        theEntity.renderYawOffset = theEntity.rotationYaw - f1;

        if (f1 * f1 > 2500.0F)
        {
            theEntity.renderYawOffset += f1 * 0.2F;
        }

        if (flagx)
        {
            p_110146_2_ *= -1.0F;
        }

        f5 = p_110146_2_;

        theEntity.world.profiler.endSection();
        theEntity.world.profiler.startSection("rangeChecks");

        while (theEntity.rotationYaw - theEntity.prevRotationYaw < -180.0F)
        {
            theEntity.prevRotationYaw -= 360.0F;
        }

        while (theEntity.rotationYaw - theEntity.prevRotationYaw >= 180.0F)
        {
            theEntity.prevRotationYaw += 360.0F;
        }

        while (theEntity.renderYawOffset - theEntity.prevRenderYawOffset < -180.0F)
        {
            theEntity.prevRenderYawOffset -= 360.0F;
        }

        while (theEntity.renderYawOffset - theEntity.prevRenderYawOffset >= 180.0F)
        {
            theEntity.prevRenderYawOffset += 360.0F;
        }

        while (theEntity.rotationPitch - theEntity.prevRotationPitch < -180.0F)
        {
            theEntity.prevRotationPitch -= 360.0F;
        }

        while (theEntity.rotationPitch - theEntity.prevRotationPitch >= 180.0F)
        {
            theEntity.prevRotationPitch += 360.0F;
        }

        while (theEntity.rotationYawHead - theEntity.prevRotationYawHead < -180.0F)
        {
            theEntity.prevRotationYawHead -= 360.0F;
        }

        while (theEntity.rotationYawHead - theEntity.prevRotationYawHead >= 180.0F)
        {
            theEntity.prevRotationYawHead += 360.0F;
        }

        theEntity.world.profiler.endSection();
        // theEntity.movedDistance += f5;

        if (theEntity.isElytraFlying())
        {
        	try {
				ticksElytraFlying.setInt(theEntity, ticksElytraFlying.getInt(theEntity) + 1);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
        }
        else
        {
            try {
				ticksElytraFlying.setInt(theEntity, 0);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
        }
    }

    /**
	 * Use fog density to create the effect of being under custom fluid, similar
	 * to how being under water does it.
	 *
	 * @param event the event
	 */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public static void onEvent(FogDensity event)
    {
    	if (!ENABLED) { return; }
    	
		// EntityPlayer thePlayer = Minecraft.getMinecraft().player;
		
		if (event.getEntity().isInsideOfMaterial(ModMaterials.SLIME))
		{
			event.setDensity(0.5F);
		}	
		else
		{
			event.setDensity(0.0001F);
		}
		
		event.setCanceled(true); // must cancel event for event handler to take effect
    }
    
    /**
     * Render the air indicator when submerged in a liquid.
     *
     * @param event the event
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.HIGHEST, receiveCanceled=true)
    public static void onEvent(RenderGameOverlayEvent event)
    {
    	if (!ENABLED) { return; }
    	
    	Minecraft mc = Minecraft.getMinecraft();
    	GuiIngame ingameGUI = mc.ingameGUI;
        ScaledResolution scaledRes = event.getResolution();
     	
       if (mc.getRenderViewEntity() instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)mc.getRenderViewEntity();

            int airIndicatorX = scaledRes.getScaledWidth() / 2 + 91;
            int airIndicatorBottom = scaledRes.getScaledHeight() - 39;
            int airIndicatorTop = airIndicatorBottom - 10;

            if (entityplayer.isInsideOfMaterial(Material.WATER) || entityplayer.isInsideOfMaterial(ModMaterials.SLIME))
            {
                int airAmount = mc.player.getAir();
                int airLostPercent = MathHelper.ceil((airAmount - 2) * 10.0D / 300.0D);
                int airLeftPercent = MathHelper.ceil(airAmount * 10.0D / 300.0D) - airLostPercent;

                for (int airUnitIndex = 0; airUnitIndex < airLostPercent + airLeftPercent; ++airUnitIndex)
                {
                    if (airUnitIndex < airLostPercent)
                    {
                        ingameGUI.drawTexturedModalRect(airIndicatorX - airUnitIndex * 8 - 9, airIndicatorTop, 16, 18, 9, 9);
                    }
                    else
                    {
                        ingameGUI.drawTexturedModalRect(airIndicatorX - airUnitIndex * 8 - 9, airIndicatorTop, 25, 18, 9, 9);
                    }
                }
            }
        }
    }        

            
    /**
	 * On event.
	 *
	 * @param event the event
	 */
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public static void onEvent(PlayerTickEvent event)
    {       
    	if (!ENABLED) { return; }
    	
		// do client side stuff
        if (event.phase == TickEvent.Phase.START && event.player.world.isRemote) // only proceed if START phase otherwise, will execute twice per tick
        {
            EntityPlayer thePlayer = event.player;
  	        FluidAdditionalFieldsTest.proxy.handleMaterialAcceleration(thePlayer, ModBlocks.SLIME_BLOCK.getDefaultState().getMaterial());           
        }
        else if (event.phase == TickEvent.Phase.START && !event.player.world.isRemote)
        {
        	// do server side stuff
        }
    }
	
	/**
	 * On event.
	 *
	 * @param event the event
	*/
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public static void onEvent(WorldTickEvent event)
	{
    	if (!ENABLED) { return; }
    	
		if (event.phase == TickEvent.Phase.END) // only proceed if START phase otherwise, will execute twice per tick
		{
			return;
		}   
  
		List<Entity> entityList = event.world.loadedEntityList;
		Iterator<Entity> iterator = entityList.iterator();
  
		while(iterator.hasNext())
		{
			Entity theEntity = iterator.next();
	  
			/* 
			 * Update all motion of all entities except players that may be inside your fluid
			 */
			FluidAdditionalFieldsTest.proxy.handleMaterialAcceleration(theEntity, ModBlocks.SLIME_BLOCK.getDefaultState().getMaterial());
		}
	}
  
	/**
	* Update air supply.
	*
	* @param parEntity the par entity
	* @param parMaterial the par material
	*/
	 public static void updateAirSupply(Entity parEntity, Material parMaterial)
	{
		 if (!(parEntity instanceof EntityLivingBase))
		 {
			 return;
		 }
	  
		 EntityLivingBase theEntityLiving = (EntityLivingBase) parEntity;
		 boolean immuneToDamage = false;
      
		 if (theEntityLiving instanceof EntityPlayer)
		 {
			 immuneToDamage = ((EntityPlayer)theEntityLiving).capabilities.disableDamage;
		 }

		 if (theEntityLiving.isEntityAlive())
		 {
			 if (!theEntityLiving.isInsideOfMaterial(parMaterial))
			 {
				 theEntityLiving.setAir(300);
			 }
			 else
			 {
	              if (!theEntityLiving.canBreatheUnderwater() && (parMaterial instanceof MaterialLiquid) && !theEntityLiving.isPotionActive(MobEffects.WATER_BREATHING) && !immuneToDamage)
	              {
	                  int respirationModifier = EnchantmentHelper.getRespirationModifier(theEntityLiving);
	                  int air = theEntityLiving.getAir();
	                  Random rand = theEntityLiving.world.rand;
	                  if (!(respirationModifier > 0 && rand.nextInt(respirationModifier + 1) > 0))
	                  {             	  
	                	  theEntityLiving.setAir(air - 1);
	                	  
	                	  // DEBUG
	                	  System.out.println("Reducing air to "+theEntityLiving.getAir());
	                  }
	                  if (theEntityLiving.getAir() == -20)
	                  {
	                	  // DEBUG
	                	  System.out.println("Entity getting damaged due to drowning");
	                	  
	                      theEntityLiving.setAir(0);
	
	                      for (int i = 0; i < 8; ++i)
	                      {
	                          float f2 = rand.nextFloat() - rand.nextFloat();
	                          float f = rand.nextFloat() - rand.nextFloat();
	                          float f1 = rand.nextFloat() - rand.nextFloat();
	                          theEntityLiving.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, theEntityLiving.posX + f2, theEntityLiving.posY + f, theEntityLiving.posZ + f1, theEntityLiving.motionX, theEntityLiving.motionY, theEntityLiving.motionZ);
	                      }
	
	                      theEntityLiving.attackEntityFrom(DamageSource.DROWN, 2.0F);
	                  }
	              }

	              if (!theEntityLiving.world.isRemote && theEntityLiving.isRiding() && theEntityLiving.getRidingEntity() != null && theEntityLiving.getRidingEntity().shouldDismountInWater(theEntityLiving))
	              {
	            	  theEntityLiving.dismountRidingEntity();
	              }
			 }
		 }
	}
	
	public static class Utilities 
	{ 
	    // Need to call this on item instance prior to registering the item
	    /**
	     * Sets the item name.
	     *
	     * @param parItem the par item
	     * @param parItemName the par item name
	     * @return the item
	     */
	    // chainable
	    public static Item setItemName(Item parItem, String parItemName) {
	        parItem.setRegistryName(parItemName);
	        parItem.setUnlocalizedName(parItemName);
	        return parItem;
	       } 
	    
	    // Need to call this on block instance prior to registering the block
	    /**
	     * Sets the block name.
	     *
	     * @param parBlock the par block
	     * @param parBlockName the par block name
	     * @return the block
	     */
	    // chainable
	    public static Block setBlockName(Block parBlock, String parBlockName) 
	    {
	        parBlock.setRegistryName(parBlockName);
	        parBlock.setUnlocalizedName(parBlockName);
	        return parBlock;
	    }   
	}
}

