package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IWorldAccess
{
    void func_147586_a(int var1, int var2, int var3);

    void func_147588_b(int var1, int var2, int var3);

    void func_147585_a(int var1, int var2, int var3, int var4, int var5, int var6);

    // JAVADOC METHOD $$ func_72704_a
    void playSound(String var1, double var2, double var4, double var6, float var8, float var9);

    // JAVADOC METHOD $$ func_85102_a
    void playSoundToNearExcept(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10);

    // JAVADOC METHOD $$ func_72708_a
    void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12);

    // JAVADOC METHOD $$ func_72703_a
    void onEntityCreate(Entity var1);

    // JAVADOC METHOD $$ func_72709_b
    void onEntityDestroy(Entity var1);

    // JAVADOC METHOD $$ func_72702_a
    void playRecord(String var1, int var2, int var3, int var4);

    void broadcastSound(int var1, int var2, int var3, int var4, int var5);

    // JAVADOC METHOD $$ func_72706_a
    void playAuxSFX(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6);

    void func_147587_b(int var1, int var2, int var3, int var4, int var5);

    void func_147584_b();
}