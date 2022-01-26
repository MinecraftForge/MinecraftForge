package net.minecraftforge.fml.client.loading;

public interface IStartupMessageRenderer
{
    void render(float width, float height, float textSize, boolean isDarkMode);

    void startup();

    void shutdown();
}
