package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.lang.reflect.Type;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;

public class ChatStyle
{
    private ChatStyle field_150249_a;
    private EnumChatFormatting field_150247_b;
    private Boolean field_150248_c;
    private Boolean field_150245_d;
    private Boolean field_150246_e;
    private Boolean field_150243_f;
    private Boolean field_150244_g;
    private ClickEvent field_150251_h;
    private HoverEvent field_150252_i;
    private static final ChatStyle field_150250_j = new ChatStyle()
    {
        private static final String __OBFID = "CL_00001267";
        public EnumChatFormatting func_150215_a()
        {
            return null;
        }
        public boolean func_150223_b()
        {
            return false;
        }
        public boolean func_150242_c()
        {
            return false;
        }
        public boolean func_150236_d()
        {
            return false;
        }
        public boolean func_150234_e()
        {
            return false;
        }
        public boolean func_150233_f()
        {
            return false;
        }
        public ClickEvent func_150235_h()
        {
            return null;
        }
        public HoverEvent func_150210_i()
        {
            return null;
        }
        public ChatStyle func_150238_a(EnumChatFormatting p_150238_1_)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle func_150227_a(Boolean p_150227_1_)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle func_150217_b(Boolean p_150217_1_)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle func_150225_c(Boolean p_150225_1_)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle func_150228_d(Boolean p_150228_1_)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle func_150237_e(Boolean p_150237_1_)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle func_150241_a(ClickEvent p_150241_1_)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle func_150209_a(HoverEvent p_150209_1_)
        {
            throw new UnsupportedOperationException();
        }
        public ChatStyle func_150221_a(ChatStyle p_150221_1_)
        {
            throw new UnsupportedOperationException();
        }
        public String toString()
        {
            return "Style.ROOT";
        }
        public ChatStyle func_150232_l()
        {
            return this;
        }
        public ChatStyle func_150206_m()
        {
            return this;
        }
        @SideOnly(Side.CLIENT)
        public String func_150218_j()
        {
            return "";
        }
    };
    private static final String __OBFID = "CL_00001266";

    public EnumChatFormatting func_150215_a()
    {
        return this.field_150247_b == null ? this.func_150224_n().func_150215_a() : this.field_150247_b;
    }

    public boolean func_150223_b()
    {
        return this.field_150248_c == null ? this.func_150224_n().func_150223_b() : this.field_150248_c.booleanValue();
    }

    public boolean func_150242_c()
    {
        return this.field_150245_d == null ? this.func_150224_n().func_150242_c() : this.field_150245_d.booleanValue();
    }

    public boolean func_150236_d()
    {
        return this.field_150243_f == null ? this.func_150224_n().func_150236_d() : this.field_150243_f.booleanValue();
    }

    public boolean func_150234_e()
    {
        return this.field_150246_e == null ? this.func_150224_n().func_150234_e() : this.field_150246_e.booleanValue();
    }

    public boolean func_150233_f()
    {
        return this.field_150244_g == null ? this.func_150224_n().func_150233_f() : this.field_150244_g.booleanValue();
    }

    public boolean func_150229_g()
    {
        return this.field_150248_c == null && this.field_150245_d == null && this.field_150243_f == null && this.field_150246_e == null && this.field_150244_g == null && this.field_150247_b == null && this.field_150251_h == null && this.field_150252_i == null;
    }

    public ClickEvent func_150235_h()
    {
        return this.field_150251_h == null ? this.func_150224_n().func_150235_h() : this.field_150251_h;
    }

    public HoverEvent func_150210_i()
    {
        return this.field_150252_i == null ? this.func_150224_n().func_150210_i() : this.field_150252_i;
    }

    public ChatStyle func_150238_a(EnumChatFormatting p_150238_1_)
    {
        this.field_150247_b = p_150238_1_;
        return this;
    }

    public ChatStyle func_150227_a(Boolean p_150227_1_)
    {
        this.field_150248_c = p_150227_1_;
        return this;
    }

    public ChatStyle func_150217_b(Boolean p_150217_1_)
    {
        this.field_150245_d = p_150217_1_;
        return this;
    }

    public ChatStyle func_150225_c(Boolean p_150225_1_)
    {
        this.field_150243_f = p_150225_1_;
        return this;
    }

    public ChatStyle func_150228_d(Boolean p_150228_1_)
    {
        this.field_150246_e = p_150228_1_;
        return this;
    }

    public ChatStyle func_150237_e(Boolean p_150237_1_)
    {
        this.field_150244_g = p_150237_1_;
        return this;
    }

    public ChatStyle func_150241_a(ClickEvent p_150241_1_)
    {
        this.field_150251_h = p_150241_1_;
        return this;
    }

    public ChatStyle func_150209_a(HoverEvent p_150209_1_)
    {
        this.field_150252_i = p_150209_1_;
        return this;
    }

    public ChatStyle func_150221_a(ChatStyle p_150221_1_)
    {
        this.field_150249_a = p_150221_1_;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public String func_150218_j()
    {
        if (this.func_150229_g())
        {
            return this.field_150249_a != null ? this.field_150249_a.func_150218_j() : "";
        }
        else
        {
            StringBuilder stringbuilder = new StringBuilder();

            if (this.func_150215_a() != null)
            {
                stringbuilder.append(this.func_150215_a());
            }

            if (this.func_150223_b())
            {
                stringbuilder.append(EnumChatFormatting.BOLD);
            }

            if (this.func_150242_c())
            {
                stringbuilder.append(EnumChatFormatting.ITALIC);
            }

            if (this.func_150234_e())
            {
                stringbuilder.append(EnumChatFormatting.UNDERLINE);
            }

            if (this.func_150233_f())
            {
                stringbuilder.append(EnumChatFormatting.OBFUSCATED);
            }

            if (this.func_150236_d())
            {
                stringbuilder.append(EnumChatFormatting.STRIKETHROUGH);
            }

            return stringbuilder.toString();
        }
    }

    private ChatStyle func_150224_n()
    {
        return this.field_150249_a == null ? field_150250_j : this.field_150249_a;
    }

    public String toString()
    {
        return "Style{hasParent=" + (this.field_150249_a != null) + ", color=" + this.field_150247_b + ", bold=" + this.field_150248_c + ", italic=" + this.field_150245_d + ", underlined=" + this.field_150246_e + ", obfuscated=" + this.field_150244_g + ", clickEvent=" + this.func_150235_h() + ", hoverEvent=" + this.func_150210_i() + '}';
    }

    public boolean equals(Object par1Obj)
    {
        if (this == par1Obj)
        {
            return true;
        }
        else if (!(par1Obj instanceof ChatStyle))
        {
            return false;
        }
        else
        {
            ChatStyle chatstyle = (ChatStyle)par1Obj;
            boolean flag;

            if (this.func_150223_b() == chatstyle.func_150223_b() && this.func_150215_a() == chatstyle.func_150215_a() && this.func_150242_c() == chatstyle.func_150242_c() && this.func_150233_f() == chatstyle.func_150233_f() && this.func_150236_d() == chatstyle.func_150236_d() && this.func_150234_e() == chatstyle.func_150234_e())
            {
                label56:
                {
                    if (this.func_150235_h() != null)
                    {
                        if (!this.func_150235_h().equals(chatstyle.func_150235_h()))
                        {
                            break label56;
                        }
                    }
                    else if (chatstyle.func_150235_h() != null)
                    {
                        break label56;
                    }

                    if (this.func_150210_i() != null)
                    {
                        if (!this.func_150210_i().equals(chatstyle.func_150210_i()))
                        {
                            break label56;
                        }
                    }
                    else if (chatstyle.func_150210_i() != null)
                    {
                        break label56;
                    }

                    flag = true;
                    return flag;
                }
            }

            flag = false;
            return flag;
        }
    }

    public int hashCode()
    {
        int i = this.field_150247_b.hashCode();
        i = 31 * i + this.field_150248_c.hashCode();
        i = 31 * i + this.field_150245_d.hashCode();
        i = 31 * i + this.field_150246_e.hashCode();
        i = 31 * i + this.field_150243_f.hashCode();
        i = 31 * i + this.field_150244_g.hashCode();
        i = 31 * i + this.field_150251_h.hashCode();
        i = 31 * i + this.field_150252_i.hashCode();
        return i;
    }

    public ChatStyle func_150232_l()
    {
        ChatStyle chatstyle = new ChatStyle();
        chatstyle.field_150248_c = this.field_150248_c;
        chatstyle.field_150245_d = this.field_150245_d;
        chatstyle.field_150243_f = this.field_150243_f;
        chatstyle.field_150246_e = this.field_150246_e;
        chatstyle.field_150244_g = this.field_150244_g;
        chatstyle.field_150247_b = this.field_150247_b;
        chatstyle.field_150251_h = this.field_150251_h;
        chatstyle.field_150252_i = this.field_150252_i;
        chatstyle.field_150249_a = this.field_150249_a;
        return chatstyle;
    }

    public ChatStyle func_150206_m()
    {
        ChatStyle chatstyle = new ChatStyle();
        chatstyle.func_150227_a(Boolean.valueOf(this.func_150223_b()));
        chatstyle.func_150217_b(Boolean.valueOf(this.func_150242_c()));
        chatstyle.func_150225_c(Boolean.valueOf(this.func_150236_d()));
        chatstyle.func_150228_d(Boolean.valueOf(this.func_150234_e()));
        chatstyle.func_150237_e(Boolean.valueOf(this.func_150233_f()));
        chatstyle.func_150238_a(this.func_150215_a());
        chatstyle.func_150241_a(this.func_150235_h());
        chatstyle.func_150209_a(this.func_150210_i());
        return chatstyle;
    }

    public static class Serializer implements JsonDeserializer, JsonSerializer
        {
            private static final String __OBFID = "CL_00001268";

            public ChatStyle deserialize(JsonElement p_150204_1_, Type p_150204_2_, JsonDeserializationContext p_150204_3_)
            {
                if (p_150204_1_.isJsonObject())
                {
                    ChatStyle chatstyle = new ChatStyle();
                    JsonObject jsonobject = p_150204_1_.getAsJsonObject();

                    if (jsonobject.has("bold"))
                    {
                        chatstyle.field_150248_c = Boolean.valueOf(jsonobject.get("bold").getAsBoolean());
                    }

                    if (jsonobject.has("italic"))
                    {
                        chatstyle.field_150245_d = Boolean.valueOf(jsonobject.get("italic").getAsBoolean());
                    }

                    if (jsonobject.has("underlined"))
                    {
                        chatstyle.field_150246_e = Boolean.valueOf(jsonobject.get("underlined").getAsBoolean());
                    }

                    if (jsonobject.has("strikethrough"))
                    {
                        chatstyle.field_150243_f = Boolean.valueOf(jsonobject.get("strikethrough").getAsBoolean());
                    }

                    if (jsonobject.has("obfuscated"))
                    {
                        chatstyle.field_150244_g = Boolean.valueOf(jsonobject.get("obfuscated").getAsBoolean());
                    }

                    if (jsonobject.has("color"))
                    {
                        chatstyle.field_150247_b = (EnumChatFormatting)p_150204_3_.deserialize(jsonobject.get("color"), EnumChatFormatting.class);
                    }

                    JsonObject jsonobject1;

                    if (jsonobject.has("clickEvent"))
                    {
                        jsonobject1 = jsonobject.getAsJsonObject("clickEvent");
                        ClickEvent.Action action = ClickEvent.Action.func_150672_a(jsonobject1.getAsJsonPrimitive("action").getAsString());
                        String s = jsonobject1.getAsJsonPrimitive("value").getAsString();

                        if (action != null && s != null && action.func_150674_a())
                        {
                            chatstyle.field_150251_h = new ClickEvent(action, s);
                        }
                    }

                    if (jsonobject.has("hoverEvent"))
                    {
                        jsonobject1 = jsonobject.getAsJsonObject("hoverEvent");
                        HoverEvent.Action action1 = HoverEvent.Action.func_150684_a(jsonobject1.getAsJsonPrimitive("action").getAsString());
                        IChatComponent ichatcomponent = (IChatComponent)p_150204_3_.deserialize(jsonobject1.get("value"), IChatComponent.class);

                        if (action1 != null && ichatcomponent != null && action1.func_150686_a())
                        {
                            chatstyle.field_150252_i = new HoverEvent(action1, ichatcomponent);
                        }
                    }

                    return chatstyle;
                }
                else
                {
                    return null;
                }
            }

            public JsonElement serialize(ChatStyle p_150203_1_, Type p_150203_2_, JsonSerializationContext p_150203_3_)
            {
                if (p_150203_1_.func_150229_g())
                {
                    return null;
                }
                else
                {
                    JsonObject jsonobject = new JsonObject();

                    if (p_150203_1_.field_150248_c != null)
                    {
                        jsonobject.addProperty("bold", p_150203_1_.field_150248_c);
                    }

                    if (p_150203_1_.field_150245_d != null)
                    {
                        jsonobject.addProperty("italic", p_150203_1_.field_150245_d);
                    }

                    if (p_150203_1_.field_150246_e != null)
                    {
                        jsonobject.addProperty("underlined", p_150203_1_.field_150246_e);
                    }

                    if (p_150203_1_.field_150243_f != null)
                    {
                        jsonobject.addProperty("strikethrough", p_150203_1_.field_150243_f);
                    }

                    if (p_150203_1_.field_150244_g != null)
                    {
                        jsonobject.addProperty("obfuscated", p_150203_1_.field_150244_g);
                    }

                    if (p_150203_1_.field_150247_b != null)
                    {
                        jsonobject.add("color", p_150203_3_.serialize(p_150203_1_.field_150247_b));
                    }

                    JsonObject jsonobject1;

                    if (p_150203_1_.field_150251_h != null)
                    {
                        jsonobject1 = new JsonObject();
                        jsonobject1.addProperty("action", p_150203_1_.field_150251_h.func_150669_a().func_150673_b());
                        jsonobject1.addProperty("value", p_150203_1_.field_150251_h.func_150668_b());
                        jsonobject.add("clickEvent", jsonobject1);
                    }

                    if (p_150203_1_.field_150252_i != null)
                    {
                        jsonobject1 = new JsonObject();
                        jsonobject1.addProperty("action", p_150203_1_.field_150252_i.func_150701_a().func_150685_b());
                        jsonobject1.add("value", p_150203_3_.serialize(p_150203_1_.field_150252_i.func_150702_b()));
                        jsonobject.add("hoverEvent", jsonobject1);
                    }

                    return jsonobject;
                }
            }

            public JsonElement serialize(Object par1Obj, Type par2Type, JsonSerializationContext par3JsonSerializationContext)
            {
                return this.serialize((ChatStyle)par1Obj, par2Type, par3JsonSerializationContext);
            }
        }
}