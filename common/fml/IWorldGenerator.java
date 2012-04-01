package fml;

import java.util.Random;

public interface IWorldGenerator {
  public void generate(Random random, int chunkX, int chunkZ, Object...additionalData);
}
