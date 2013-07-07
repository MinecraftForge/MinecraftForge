package org.bukkit.craftbukkit.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftMetaItem.ItemMetaKey.Specific;
import org.bukkit.craftbukkit.inventory.CraftMetaItem.ItemMetaKey.Specific.To;
import org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.inventory.meta.FireworkMeta;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap.Builder;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaFirework extends CraftMetaItem implements FireworkMeta {
    /*
       "Fireworks", "Explosion", "Explosions", "Flight", "Type", "Trail", "Flicker", "Colors", "FadeColors";

        Fireworks
        - Compound: Fireworks
        -- Byte: Flight
        -- List: Explosions
        --- Compound: Explosion
        ---- IntArray: Colors
        ---- Byte: Type
        ---- Boolean: Trail
        ---- Boolean: Flicker
        ---- IntArray: FadeColors
     */

    @Specific(To.NBT)
    static final ItemMetaKey FIREWORKS = new ItemMetaKey("Fireworks");
    static final ItemMetaKey FLIGHT = new ItemMetaKey("Flight", "power");
    static final ItemMetaKey EXPLOSIONS = new ItemMetaKey("Explosions", "firework-effects");
    @Specific(To.NBT)
    static final ItemMetaKey EXPLOSION_COLORS = new ItemMetaKey("Colors");
    @Specific(To.NBT)
    static final ItemMetaKey EXPLOSION_TYPE = new ItemMetaKey("Type");
    @Specific(To.NBT)
    static final ItemMetaKey EXPLOSION_TRAIL = new ItemMetaKey("Trail");
    @Specific(To.NBT)
    static final ItemMetaKey EXPLOSION_FLICKER = new ItemMetaKey("Flicker");
    @Specific(To.NBT)
    static final ItemMetaKey EXPLOSION_FADE = new ItemMetaKey("FadeColors");

    private List<FireworkEffect> effects;
    private int power;

    CraftMetaFirework(CraftMetaItem meta) {
        super(meta);

        if (!(meta instanceof CraftMetaFirework)) {
            return;
        }

        CraftMetaFirework that = (CraftMetaFirework) meta;

        this.power = that.power;

        if (that.hasEffects()) {
            this.effects = new ArrayList<FireworkEffect>(that.effects);
        }
    }

    CraftMetaFirework(net.minecraft.nbt.NBTTagCompound tag) {
        super(tag);

        if (!tag.hasKey(FIREWORKS.NBT)) {
            return;
        }

        net.minecraft.nbt.NBTTagCompound fireworks = tag.getCompoundTag(FIREWORKS.NBT);

        power = 0xff & fireworks.getByte(FLIGHT.NBT);

        if (!fireworks.hasKey(EXPLOSIONS.NBT)) {
            return;
        }

        net.minecraft.nbt.NBTTagList fireworkEffects = fireworks.getTagList(EXPLOSIONS.NBT);
        List<FireworkEffect> effects = this.effects = new ArrayList<FireworkEffect>(fireworkEffects.tagCount());

        for (int i = 0; i < fireworkEffects.tagCount(); i++) {
            effects.add(getEffect((net.minecraft.nbt.NBTTagCompound) fireworkEffects.tagAt(i)));
        }
    }

    static FireworkEffect getEffect(net.minecraft.nbt.NBTTagCompound explosion) {
        FireworkEffect.Builder effect = FireworkEffect.builder()
                .flicker(explosion.getBoolean(EXPLOSION_FLICKER.NBT))
                .trail(explosion.getBoolean(EXPLOSION_TRAIL.NBT))
                .with(getEffectType(0xff & explosion.getByte(EXPLOSION_TYPE.NBT)));

        for (int color : explosion.getIntArray(EXPLOSION_COLORS.NBT)) {
            effect.withColor(Color.fromRGB(color));
        }

        for (int color : explosion.getIntArray(EXPLOSION_FADE.NBT)) {
            effect.withFade(Color.fromRGB(color));
        }

        return effect.build();
    }

    static net.minecraft.nbt.NBTTagCompound getExplosion(FireworkEffect effect) {
        net.minecraft.nbt.NBTTagCompound explosion = new net.minecraft.nbt.NBTTagCompound();

        if (effect.hasFlicker()) {
            explosion.setBoolean(EXPLOSION_FLICKER.NBT, true);
        }

        if (effect.hasTrail()) {
            explosion.setBoolean(EXPLOSION_TRAIL.NBT, true);
        }

        addColors(explosion, EXPLOSION_COLORS, effect.getColors());
        addColors(explosion, EXPLOSION_FADE, effect.getFadeColors());

        explosion.setByte(EXPLOSION_TYPE.NBT, (byte) getNBT(effect.getType()));

        return explosion;
    }

    static int getNBT(Type type) {
        switch (type) {
            case BALL:
                return 0;
            case BALL_LARGE:
                return 1;
            case STAR:
                return 2;
            case CREEPER:
                return 3;
            case BURST:
                return 4;
            default:
                throw new IllegalStateException(type.toString()); // Spigot
        }
    }

    static Type getEffectType(int nbt) {
        switch (nbt) {
            case 0:
                return Type.BALL;
            case 1:
                return Type.BALL_LARGE;
            case 2:
                return Type.STAR;
            case 3:
                return Type.CREEPER;
            case 4:
                return Type.BURST;
            default:
                throw new IllegalStateException(Integer.toString(nbt)); // Spigot
        }
    }

    CraftMetaFirework(Map<String, Object> map) {
        super(map);

        Integer power = SerializableMeta.getObject(Integer.class, map, FLIGHT.BUKKIT, true);
        if (power != null) {
            setPower(power);
        }

        Iterable<?> effects = SerializableMeta.getObject(Iterable.class, map, EXPLOSIONS.BUKKIT, true);
        safelyAddEffects(effects);
    }

    public boolean hasEffects() {
        return !(effects == null || effects.isEmpty());
    }

    void safelyAddEffects(Iterable<?> collection) {
        if (collection == null || (collection instanceof Collection && ((Collection<?>) collection).isEmpty())) {
            return;
        }

        List<FireworkEffect> effects = this.effects;
        if (effects == null) {
            effects = this.effects = new ArrayList<FireworkEffect>();
        }

        for (Object obj : collection) {
            if (obj instanceof FireworkEffect) {
                effects.add((FireworkEffect) obj);
            } else {
                throw new IllegalArgumentException(obj + " in " + collection + " is not a FireworkEffect");
            }
        }
    }

    @Override
    void applyToItem(net.minecraft.nbt.NBTTagCompound itemTag) {
        super.applyToItem(itemTag);
        if (isFireworkEmpty()) {
            return;
        }

        net.minecraft.nbt.NBTTagCompound fireworks = itemTag.getCompoundTag(FIREWORKS.NBT);
        itemTag.setCompoundTag(FIREWORKS.NBT, fireworks);

        if (hasEffects()) {
            net.minecraft.nbt.NBTTagList effects = new net.minecraft.nbt.NBTTagList(EXPLOSIONS.NBT);
            for (FireworkEffect effect : this.effects) {
                effects.appendTag(getExplosion(effect));
            }

            if (effects.tagCount() > 0) {
                fireworks.setTag(EXPLOSIONS.NBT, effects);
            }
        }

        if (hasPower()) {
            fireworks.setByte(FLIGHT.NBT, (byte) power);
        }
    }

    static void addColors(net.minecraft.nbt.NBTTagCompound compound, ItemMetaKey key, List<Color> colors) {
        if (colors.isEmpty()) {
            return;
        }

        final int[] colorArray = new int[colors.size()];
        int i = 0;
        for (Color color : colors) {
            colorArray[i++] = color.asRGB();
        }

        compound.setIntArray(key.NBT, colorArray);
    }

    @Override
    boolean applicableTo(Material type) {
        switch(type) {
            case FIREWORK:
                return true;
            default:
                return false;
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && isFireworkEmpty();
    }

    boolean isFireworkEmpty() {
        return  !(hasEffects() || hasPower());
    }

    boolean hasPower() {
        return power != 0;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }

        if (meta instanceof CraftMetaFirework) {
            CraftMetaFirework that = (CraftMetaFirework) meta;

            return (hasPower() ? that.hasPower() && this.power == that.power : !that.hasPower())
                    && (hasEffects() ? that.hasEffects() && this.effects.equals(that.effects) : !that.hasEffects());
        }

        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaFirework || isFireworkEmpty());
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        if (hasPower()) {
            hash = 61 * hash + power;
        }
        if (hasEffects()) {
            hash = 61 * hash + 13 * effects.hashCode();
        }
        return hash != original ? CraftMetaFirework.class.hashCode() ^ hash : hash;
    }

    @Override
    Builder<String, Object> serialize(Builder<String, Object> builder) {
        super.serialize(builder);

        if (hasEffects()) {
            builder.put(EXPLOSIONS.BUKKIT, ImmutableList.copyOf(effects));
        }

        if (hasPower()) {
            builder.put(FLIGHT.BUKKIT, power);
        }

        return builder;
    }

    @Override
    public CraftMetaFirework clone() {
        CraftMetaFirework meta = (CraftMetaFirework) super.clone();

        if (this.effects != null) {
            meta.effects = new ArrayList<FireworkEffect>(this.effects);
        }

        return meta;
    }

    public void addEffect(FireworkEffect effect) {
        Validate.notNull(effect, "Effect cannot be null");
        if (this.effects == null) {
            this.effects = new ArrayList<FireworkEffect>();
        }
        this.effects.add(effect);
    }

    public void addEffects(FireworkEffect...effects) {
        Validate.notNull(effects, "Effects cannot be null");
        if (effects.length == 0) {
            return;
        }

        List<FireworkEffect> list = this.effects;
        if (list == null) {
            list = this.effects = new ArrayList<FireworkEffect>();
        }

        for (FireworkEffect effect : effects) {
            Validate.notNull(effect, "Effect cannot be null");
            list.add(effect);
        }
    }

    public void addEffects(Iterable<FireworkEffect> effects) {
        Validate.notNull(effects, "Effects cannot be null");
        safelyAddEffects(effects);
    }

    public List<FireworkEffect> getEffects() {
        return this.effects == null ? ImmutableList.<FireworkEffect>of() : ImmutableList.copyOf(this.effects);
    }

    public int getEffectsSize() {
        return this.effects == null ? 0 : this.effects.size();
    }

    public void removeEffect(int index) {
        if (this.effects == null) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: 0");
        } else {
            this.effects.remove(index);
        }
    }

    public void clearEffects() {
        this.effects = null;
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        Validate.isTrue(power >= 0, "Power cannot be less than zero: ", power);
        Validate.isTrue(power < 0x80, "Power cannot be more than 127: ", power);
        this.power = power;
    }
}
