package net.minecraft.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class JsonUtils
{
    private static final String __OBFID = "CL_00001484";

    @SideOnly(Side.CLIENT)
    public static boolean func_151205_a(JsonObject p_151205_0_, String p_151205_1_)
    {
        return !func_151201_f(p_151205_0_, p_151205_1_) ? false : p_151205_0_.getAsJsonPrimitive(p_151205_1_).isString();
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_151211_a(JsonElement p_151211_0_)
    {
        return !p_151211_0_.isJsonPrimitive() ? false : p_151211_0_.getAsJsonPrimitive().isString();
    }

    public static boolean func_151202_d(JsonObject p_151202_0_, String p_151202_1_)
    {
        return !func_151204_g(p_151202_0_, p_151202_1_) ? false : p_151202_0_.get(p_151202_1_).isJsonArray();
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_151201_f(JsonObject p_151201_0_, String p_151201_1_)
    {
        return !func_151204_g(p_151201_0_, p_151201_1_) ? false : p_151201_0_.get(p_151201_1_).isJsonPrimitive();
    }

    public static boolean func_151204_g(JsonObject p_151204_0_, String p_151204_1_)
    {
        return p_151204_0_ == null ? false : p_151204_0_.get(p_151204_1_) != null;
    }

    public static String func_151206_a(JsonElement p_151206_0_, String p_151206_1_)
    {
        if (p_151206_0_.isJsonPrimitive())
        {
            return p_151206_0_.getAsString();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151206_1_ + " to be a string, was " + func_151222_d(p_151206_0_));
        }
    }

    public static String func_151200_h(JsonObject p_151200_0_, String p_151200_1_)
    {
        if (p_151200_0_.has(p_151200_1_))
        {
            return func_151206_a(p_151200_0_.get(p_151200_1_), p_151200_1_);
        }
        else
        {
            throw new JsonSyntaxException("Missing " + p_151200_1_ + ", expected to find a string");
        }
    }

    @SideOnly(Side.CLIENT)
    public static String func_151219_a(JsonObject p_151219_0_, String p_151219_1_, String p_151219_2_)
    {
        return p_151219_0_.has(p_151219_1_) ? func_151206_a(p_151219_0_.get(p_151219_1_), p_151219_1_) : p_151219_2_;
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_151216_b(JsonElement p_151216_0_, String p_151216_1_)
    {
        if (p_151216_0_.isJsonPrimitive())
        {
            return p_151216_0_.getAsBoolean();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151216_1_ + " to be a Boolean, was " + func_151222_d(p_151216_0_));
        }
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_151212_i(JsonObject p_151212_0_, String p_151212_1_)
    {
        if (p_151212_0_.has(p_151212_1_))
        {
            return func_151216_b(p_151212_0_.get(p_151212_1_), p_151212_1_);
        }
        else
        {
            throw new JsonSyntaxException("Missing " + p_151212_1_ + ", expected to find a Boolean");
        }
    }

    @SideOnly(Side.CLIENT)
    public static boolean func_151209_a(JsonObject p_151209_0_, String p_151209_1_, boolean p_151209_2_)
    {
        return p_151209_0_.has(p_151209_1_) ? func_151216_b(p_151209_0_.get(p_151209_1_), p_151209_1_) : p_151209_2_;
    }

    @SideOnly(Side.CLIENT)
    public static float func_151220_d(JsonElement p_151220_0_, String p_151220_1_)
    {
        if (p_151220_0_.isJsonPrimitive() && p_151220_0_.getAsJsonPrimitive().isNumber())
        {
            return p_151220_0_.getAsFloat();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151220_1_ + " to be a Float, was " + func_151222_d(p_151220_0_));
        }
    }

    @SideOnly(Side.CLIENT)
    public static float func_151217_k(JsonObject p_151217_0_, String p_151217_1_)
    {
        if (p_151217_0_.has(p_151217_1_))
        {
            return func_151220_d(p_151217_0_.get(p_151217_1_), p_151217_1_);
        }
        else
        {
            throw new JsonSyntaxException("Missing " + p_151217_1_ + ", expected to find a Float");
        }
    }

    @SideOnly(Side.CLIENT)
    public static float func_151221_a(JsonObject p_151221_0_, String p_151221_1_, float p_151221_2_)
    {
        return p_151221_0_.has(p_151221_1_) ? func_151220_d(p_151221_0_.get(p_151221_1_), p_151221_1_) : p_151221_2_;
    }

    public static int func_151215_f(JsonElement p_151215_0_, String p_151215_1_)
    {
        if (p_151215_0_.isJsonPrimitive() && p_151215_0_.getAsJsonPrimitive().isNumber())
        {
            return p_151215_0_.getAsInt();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151215_1_ + " to be a Int, was " + func_151222_d(p_151215_0_));
        }
    }

    public static int func_151203_m(JsonObject p_151203_0_, String p_151203_1_)
    {
        if (p_151203_0_.has(p_151203_1_))
        {
            return func_151215_f(p_151203_0_.get(p_151203_1_), p_151203_1_);
        }
        else
        {
            throw new JsonSyntaxException("Missing " + p_151203_1_ + ", expected to find a Int");
        }
    }

    @SideOnly(Side.CLIENT)
    public static int func_151208_a(JsonObject p_151208_0_, String p_151208_1_, int p_151208_2_)
    {
        return p_151208_0_.has(p_151208_1_) ? func_151215_f(p_151208_0_.get(p_151208_1_), p_151208_1_) : p_151208_2_;
    }

    public static JsonObject func_151210_l(JsonElement p_151210_0_, String p_151210_1_)
    {
        if (p_151210_0_.isJsonObject())
        {
            return p_151210_0_.getAsJsonObject();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151210_1_ + " to be a JsonObject, was " + func_151222_d(p_151210_0_));
        }
    }

    @SideOnly(Side.CLIENT)
    public static JsonObject func_151218_a(JsonObject p_151218_0_, String p_151218_1_, JsonObject p_151218_2_)
    {
        return p_151218_0_.has(p_151218_1_) ? func_151210_l(p_151218_0_.get(p_151218_1_), p_151218_1_) : p_151218_2_;
    }

    public static JsonArray func_151207_m(JsonElement p_151207_0_, String p_151207_1_)
    {
        if (p_151207_0_.isJsonArray())
        {
            return p_151207_0_.getAsJsonArray();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151207_1_ + " to be a JsonArray, was " + func_151222_d(p_151207_0_));
        }
    }

    public static JsonArray func_151214_t(JsonObject p_151214_0_, String p_151214_1_)
    {
        if (p_151214_0_.has(p_151214_1_))
        {
            return func_151207_m(p_151214_0_.get(p_151214_1_), p_151214_1_);
        }
        else
        {
            throw new JsonSyntaxException("Missing " + p_151214_1_ + ", expected to find a JsonArray");
        }
    }

    @SideOnly(Side.CLIENT)
    public static JsonArray func_151213_a(JsonObject p_151213_0_, String p_151213_1_, JsonArray p_151213_2_)
    {
        return p_151213_0_.has(p_151213_1_) ? func_151207_m(p_151213_0_.get(p_151213_1_), p_151213_1_) : p_151213_2_;
    }

    public static String func_151222_d(JsonElement p_151222_0_)
    {
        String s = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf(p_151222_0_), "...", 10);

        if (p_151222_0_ == null)
        {
            return "null (missing)";
        }
        else if (p_151222_0_.isJsonNull())
        {
            return "null (json)";
        }
        else if (p_151222_0_.isJsonArray())
        {
            return "an array (" + s + ")";
        }
        else if (p_151222_0_.isJsonObject())
        {
            return "an object (" + s + ")";
        }
        else
        {
            if (p_151222_0_.isJsonPrimitive())
            {
                JsonPrimitive jsonprimitive = p_151222_0_.getAsJsonPrimitive();

                if (jsonprimitive.isNumber())
                {
                    return "a number (" + s + ")";
                }

                if (jsonprimitive.isBoolean())
                {
                    return "a boolean (" + s + ")";
                }
            }

            return s;
        }
    }
}