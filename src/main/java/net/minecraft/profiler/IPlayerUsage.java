package net.minecraft.profiler;

public interface IPlayerUsage
{
    void addServerStatsToSnooper(PlayerUsageSnooper var1);

    void addServerTypeToSnooper(PlayerUsageSnooper var1);

    // JAVADOC METHOD $$ func_70002_Q
    boolean isSnooperEnabled();
}