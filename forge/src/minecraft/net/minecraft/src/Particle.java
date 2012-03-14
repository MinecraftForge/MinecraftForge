package net.minecraft.src;

import java.util.Random;

public class Particle
{
    private static Random rand = new Random();
    public double posX;
    public double posY;
    public double prevPosX;
    public double prevPosY;
    public double velocityX;
    public double velocityY;
    public double accelScale;
    public boolean isDead;
    public int timeTick;
    public int timeLimit;
    public double tintRed;
    public double tintGreen;
    public double tintBlue;
    public double tintAlpha;
    public double prevTintRed;
    public double prevTintGreen;
    public double prevTintBlue;
    public double prevTintAlpha;

    public void update(GuiParticle par1GuiParticle)
    {
        this.posX += this.velocityX;
        this.posY += this.velocityY;
        this.velocityX *= this.accelScale;
        this.velocityY *= this.accelScale;
        this.velocityY += 0.1D;

        if (++this.timeTick > this.timeLimit)
        {
            this.setDead();
        }

        this.tintAlpha = 2.0D - (double)this.timeTick / (double)this.timeLimit * 2.0D;

        if (this.tintAlpha > 1.0D)
        {
            this.tintAlpha = 1.0D;
        }

        this.tintAlpha *= this.tintAlpha;
        this.tintAlpha *= 0.5D;
    }

    public void preUpdate()
    {
        this.prevTintRed = this.tintRed;
        this.prevTintGreen = this.tintGreen;
        this.prevTintBlue = this.tintBlue;
        this.prevTintAlpha = this.tintAlpha;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
    }

    public void setDead()
    {
        this.isDead = true;
    }
}
