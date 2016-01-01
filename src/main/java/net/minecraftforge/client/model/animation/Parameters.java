package net.minecraftforge.client.model.animation;

import com.google.common.base.Objects;

/**
 * Various implementations of IParameter.
 */
public class Parameters
{
    public static enum NoopParameter implements IParameter
    {
        instance;

        public float apply(float input)
        {
            return input;
        }
    }

    public static final class ConstParameter implements IParameter
    {
        private final float output;

        public ConstParameter(float output)
        {
            this.output = output;
        }

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
            ConstParameter other = (ConstParameter) obj;
            return output == other.output;
        }
    }

    public static final class LinearParameter implements IParameter
    {
        private final float weight;
        private final float offset;

        public LinearParameter(float weight, float offset)
        {
            super();
            this.weight = weight;
            this.offset = offset;
        }

        public float apply(float input)
        {
            return input * weight + offset;
        }

        @Override
        public int hashCode()
        {
            return Objects.hashCode(weight, offset);
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
            LinearParameter other = (LinearParameter) obj;
            return weight == other.weight && offset == other.offset;
        }
    }
}
