package net.minecraftforge.debug.entity.living;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageSourceEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("living_damage_source_change_test")
public class LivingDamageSourceChangeTest
{
    public LivingDamageSourceChangeTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onLivingDamageSource);
    }
    
    public void onLivingDamageSource(LivingDamageSourceEvent event)
    {
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity)
        {
            ItemStack mainhand = ((LivingEntity) attacker).getMainHandItem();
            CompoundTag tag = mainhand.getOrCreateTag();
            if (tag.contains("test_setSource"))
            {
                event.setSource(new TestDamageSource(source));
            }
            else if (tag.contains("test_noDamage") && event.isCancelable())
            {
                event.setCanceled(true);
            }
        }
    }
    
    public static class TestDamageSource extends DamageSource
    {
        private final DamageSource wrapped;
        public TestDamageSource(DamageSource wrapped)
        {
            super("test_source");
            this.wrapped = wrapped;
        }
        
        @Override
        public Component getLocalizedDeathMessage(LivingEntity p_19343_)
        {
            return this.wrapped.getLocalizedDeathMessage(p_19343_).copy().append(new TextComponent(" - and now for something completely different."));
        }
    }

}
