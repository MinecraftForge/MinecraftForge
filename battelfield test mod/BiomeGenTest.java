package net.minecraft.src;

public class BiomeGenTest extends BiomeGenBase{

		protected BiomeGenTest(int i) {
			super(i);
			this.topBlock = (byte)Block.stone.blockID;
			this.theBiomeDecorator.cactiPerChunk = 200;
			this.theBiomeDecorator.reedsPerChunk = 200;
			this.theBiomeDecorator.flowersPerChunk = 200;
		}
		
}