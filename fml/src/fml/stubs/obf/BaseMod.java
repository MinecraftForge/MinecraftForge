package fml.stubs.obf;

public interface BaseMod {
	 int	addFuel(int id, int metadata);
	 void	addRenderer(java.util.Map<java.lang.Class<? extends nn>,um> renderers); 
	 boolean	dispenseEntity(xd world, double x, double y, double z, int xVel, int zVel, aan item); 
	 void	generateNether(xd world, java.util.Random random, int chunkX, int chunkZ); 
	 void	generateSurface(xd world, java.util.Random random, int chunkX, int chunkZ); 
	 java.lang.String	getName(); 
	 java.lang.String	getPriorities(); 
	 abstract  java.lang.String	getVersion(); 
	 void	keyboardEvent(afu event); 
	 abstract  void	load(); 
	 void	modsLoaded(); 
	 void	onItemPickup(yw player, aan item) 
	 boolean	onTickInGame(float tick, net.minecraft.client.Minecraft game); 
	 boolean	onTickInGUI(float tick, net.minecraft.client.Minecraft game, vp gui); 
	 void	receiveChatPacket(java.lang.String text); 
	 void	receiveCustomPacket(ee packet); 
	 void	registerAnimation(net.minecraft.client.Minecraft game); 
	 void	renderInvBlock(vl renderer, pb block, int metadata, int modelID); 
	 boolean	renderWorldBlock(vl renderer, ali world, int x, int y, int z, pb block, int modelID); 
	 void	takenFromCrafting(yw player, aan item, io matrix); 
	 void	takenFromFurnace(yw player, aan item);
	 java.lang.String	toString();    
}
