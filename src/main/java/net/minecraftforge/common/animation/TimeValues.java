/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.animation;

import java.io.IOException;
import java.util.regex.Pattern;

import net.minecraft.util.IStringSerializable;

import java.util.function.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.annotation.Nullable;

/**
 * Various implementations of ITimeValue.
 */
public final class TimeValues
{
    public static enum IdentityValue implements ITimeValue, IStringSerializable
    {
        INSTANCE;

        @Override
        public float apply(float input)
        {
            return input;
        }

        @Override
        public String getString()
        {
            return "identity";
        }
    }

    public static final class ConstValue implements ITimeValue
    {
        private final float output;

        public ConstValue(float output)
        {
            this.output = output;
        }

        @Override
        public float apply(float input)
        {
            return output;
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(output);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ConstValue other = (ConstValue) obj;
            return output == other.output;
        }
    }

    /**
     * Simple value holder.
     */
    public static final class VariableValue implements ITimeValue
    {
        private float output;

        public VariableValue(float initialValue)
        {
            this.output = initialValue;
        }

        public void setValue(float newValue)
        {
            this.output = newValue;
        }

        @Override
        public float apply(float input)
        {
            return output;
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(output);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            VariableValue other = (VariableValue) obj;
            return output == other.output;
        }
    }

    public static final class SimpleExprValue implements ITimeValue
    {
        private static final Pattern opsPattern = Pattern.compile("[+\\-*/mMrRfF]+");

        private final String operators;
        private final ImmutableList<ITimeValue> args;

        public SimpleExprValue(String operators, ImmutableList<ITimeValue> args)
        {
            this.operators = operators;
            this.args = args;
        }

        @Override
        public float apply(float input)
        {
            float ret = input;
            for(int i = 0; i < operators.length(); i++)
            {
                float arg = args.get(i).apply(input);
                switch(operators.charAt(i))
                {
                    case '+': ret += arg; break;
                    case '-': ret -= arg; break;
                    case '*': ret *= arg; break;
                    case '/': ret /= arg; break;
                    case 'm': ret = Math.min(ret, arg); break;
                    case 'M': ret = Math.max(ret, arg); break;
                    case 'r': ret = (float)Math.floor(ret / arg) * arg; break;
                    case 'R': ret = (float)Math.ceil(ret / arg) * arg; break;
                    case 'f': ret -= Math.floor(ret / arg) * arg; break;
                    case 'F': ret = (float)Math.ceil(ret / arg) * arg - ret; break;
                }
            }
            return ret;
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(operators, args);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            SimpleExprValue other = (SimpleExprValue) obj;
            return Objects.equal(operators, other.operators) && Objects.equal(args, other.args);
        }
    }

    public static final class CompositionValue implements ITimeValue
    {
        private final ITimeValue g;
        private final ITimeValue f;

        public CompositionValue(ITimeValue g, ITimeValue f)
        {
            super();
            this.g = g;
            this.f = f;
        }

        @Override
        public float apply(float input)
        {
            return g.apply(f.apply(input));
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(g, f);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CompositionValue other = (CompositionValue) obj;
            return Objects.equal(g, other.g) && Objects.equal(f, other.f);
        }
    }

    public static final class ParameterValue implements ITimeValue, IStringSerializable
    {
        private final String parameterName;
        private final Function<String, ITimeValue> valueResolver;
        private ITimeValue parameter;

        public ParameterValue(String parameterName, Function<String, ITimeValue> valueResolver)
        {
            this.parameterName = parameterName;
            this.valueResolver = valueResolver;
        }

        @Override
        public String getString()
        {
            return parameterName;
        }

        private void resolve()
        {
            if(parameter == null)
            {
                if(valueResolver != null)
                {
                    parameter = valueResolver.apply(parameterName);
                }
                if(parameter == null)
                {
                    throw new IllegalArgumentException("Couldn't resolve parameter value " + parameterName);
                }
            }
        }

        @Override
        public float apply(float input)
        {
            resolve();
            return parameter.apply(input);
        }

        @Override
        public int hashCode()
        {
            resolve();
            return parameter.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ParameterValue other = (ParameterValue) obj;
            resolve();
            other.resolve();
            return Objects.equal(parameter, other.parameter);
        }
    }

    public static enum CommonTimeValueTypeAdapterFactory implements TypeAdapterFactory
    {
        INSTANCE;

        private final ThreadLocal<Function<String, ITimeValue>> valueResolver = new ThreadLocal<Function<String, ITimeValue>>();

        public void setValueResolver(@Nullable Function<String, ITimeValue> valueResolver)
        {
            this.valueResolver.set(valueResolver);
        }

        @Override
        @SuppressWarnings("unchecked")
        @Nullable
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
        {
            if(type.getRawType() != ITimeValue.class)
            {
                return null;
            }

            return (TypeAdapter<T>)new TypeAdapter<ITimeValue>()
            {
                @Override
                public void write(JsonWriter out, ITimeValue parameter) throws IOException
                {
                    if(parameter instanceof ConstValue)
                    {
                        out.value(((ConstValue)parameter).output);
                    }
                    else if(parameter instanceof SimpleExprValue)
                    {
                        SimpleExprValue p = (SimpleExprValue)parameter;
                        out.beginArray();
                        out.value(p.operators);
                        for(ITimeValue v : p.args)
                        {
                            write(out, v);
                        }
                        out.endArray();
                    }
                    else if(parameter instanceof CompositionValue)
                    {
                        CompositionValue p = (CompositionValue)parameter;
                        out.beginArray();
                        out.value("compose");
                        write(out, p.g);
                        write(out, p.f);
                        out.endArray();
                    }
                    else if(parameter instanceof IStringSerializable)
                    {
                        out.value("#" + ((IStringSerializable)parameter).getString());
                    }
                }

                @Override
                public ITimeValue read(JsonReader in) throws IOException
                {
                    switch(in.peek())
                    {
                    case NUMBER:
                        return new ConstValue((float)in.nextDouble());
                    case BEGIN_ARRAY:
                        in.beginArray();
                        String type = in.nextString();
                        ITimeValue p;
                        if(SimpleExprValue.opsPattern.matcher(type).matches())
                        {
                            ImmutableList.Builder<ITimeValue> builder = ImmutableList.builder();
                            while(in.hasNext())
                            {
                                builder.add(read(in));
                            }
                            p = new SimpleExprValue(type, builder.build());
                        }
                        else if("compose".equals(type))
                        {
                            p = new CompositionValue(read(in), read(in));
                        }
                        else
                        {
                            throw new IOException("Unknown TimeValue type \"" + type + "\"");
                        }
                        in.endArray();
                        return p;
                    case STRING:
                        String string = in.nextString();
                        if(string.equals("#identity"))
                        {
                            return IdentityValue.INSTANCE;
                        }
                        if(!string.startsWith("#"))
                        {
                            throw new IOException("Expected TimeValue reference, got \"" + string + "\"");
                        }
                        // User Parameter TimeValue
                        return new ParameterValue(string.substring(1), valueResolver.get());
                    default:
                        throw new IOException("Expected TimeValue, got " + in.peek());
                    }
                }
            };
        }
    }
}
