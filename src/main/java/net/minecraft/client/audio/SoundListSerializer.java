package net.minecraft.client.audio;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

@SideOnly(Side.CLIENT)
public class SoundListSerializer implements JsonDeserializer
{
    private static final String __OBFID = "CL_00001124";

    public SoundList deserialize(JsonElement p_148578_1_, Type p_148578_2_, JsonDeserializationContext p_148578_3_)
    {
        JsonObject jsonobject = JsonUtils.func_151210_l(p_148578_1_, "entry");
        SoundList soundlist = new SoundList();
        soundlist.func_148572_a(JsonUtils.func_151209_a(jsonobject, "replace", false));
        SoundCategory soundcategory = SoundCategory.func_147154_a(JsonUtils.func_151219_a(jsonobject, "category", SoundCategory.MASTER.func_147155_a()));
        soundlist.func_148571_a(soundcategory);
        Validate.notNull(soundcategory, "Invalid category", new Object[0]);

        if (jsonobject.has("sounds"))
        {
            JsonArray jsonarray = JsonUtils.func_151214_t(jsonobject, "sounds");

            for (int i = 0; i < jsonarray.size(); ++i)
            {
                JsonElement jsonelement1 = jsonarray.get(i);
                SoundList.SoundEntry soundentry = new SoundList.SoundEntry();

                if (JsonUtils.func_151211_a(jsonelement1))
                {
                    soundentry.func_148561_a(JsonUtils.func_151206_a(jsonelement1, "sound"));
                }
                else
                {
                    JsonObject jsonobject1 = JsonUtils.func_151210_l(jsonelement1, "sound");
                    soundentry.func_148561_a(JsonUtils.func_151200_h(jsonobject1, "name"));

                    if (jsonobject1.has("type"))
                    {
                        SoundList.SoundEntry.Type type1 = SoundList.SoundEntry.Type.func_148580_a(JsonUtils.func_151200_h(jsonobject1, "type"));
                        Validate.notNull(type1, "Invalid type", new Object[0]);
                        soundentry.func_148562_a(type1);
                    }

                    float f;

                    if (jsonobject1.has("volume"))
                    {
                        f = JsonUtils.func_151217_k(jsonobject1, "volume");
                        Validate.isTrue(f > 0.0F, "Invalid volume", new Object[0]);
                        soundentry.func_148553_a(f);
                    }

                    if (jsonobject1.has("pitch"))
                    {
                        f = JsonUtils.func_151217_k(jsonobject1, "pitch");
                        Validate.isTrue(f > 0.0F, "Invalid pitch", new Object[0]);
                        soundentry.func_148559_b(f);
                    }

                    if (jsonobject1.has("weight"))
                    {
                        int j = JsonUtils.func_151203_m(jsonobject1, "weight");
                        Validate.isTrue(j > 0, "Invalid weight", new Object[0]);
                        soundentry.func_148554_a(j);
                    }

                    if (jsonobject1.has("stream"))
                    {
                        soundentry.func_148557_a(JsonUtils.func_151212_i(jsonobject1, "stream"));
                    }
                }

                soundlist.func_148570_a().add(soundentry);
            }
        }

        return soundlist;
    }
}