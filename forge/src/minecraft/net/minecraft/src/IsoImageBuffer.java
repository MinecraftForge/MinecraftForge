package net.minecraft.src;

import java.awt.image.BufferedImage;

public class IsoImageBuffer
{
    public BufferedImage image;
    public World level;
    public int x;
    public int y;
    public boolean rendered = false;
    public boolean noContent = false;
    public int lastVisible = 0;
    public boolean addedToRenderQueue = false;

    public IsoImageBuffer(World par1World, int par2, int par3)
    {
        this.level = par1World;
        this.init(par2, par3);
    }

    public void init(int par1, int par2)
    {
        this.rendered = false;
        this.x = par1;
        this.y = par2;
        this.lastVisible = 0;
        this.addedToRenderQueue = false;
    }

    public void init(World par1World, int par2, int par3)
    {
        this.level = par1World;
        this.init(par2, par3);
    }
}
