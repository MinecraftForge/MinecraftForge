package net.minecraftforge.fml.loading.progress;

import org.jetbrains.annotations.Nullable;

class NoVisualization implements Visualization
{
    @Override
    public int windowWidth()
    {
        return 0;
    }

    @Override
    public int windowHeight()
    {
        return 0;
    }

    @Override
    public Runnable start(@Nullable String mcVersion)
    {
        return () -> {};
    }
}
