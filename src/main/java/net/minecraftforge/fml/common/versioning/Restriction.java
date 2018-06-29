/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fml.common.versioning;

import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;

/**
 * Describes a restriction in versioning.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class Restriction
{
    private final ArtifactVersion lowerBound;

    private final boolean lowerBoundInclusive;

    private final ArtifactVersion upperBound;

    private final boolean upperBoundInclusive;

    public static final Restriction EVERYTHING = new Restriction( null, false, null, false );

    public Restriction( @Nullable ArtifactVersion lowerBound, boolean lowerBoundInclusive, @Nullable ArtifactVersion upperBound,
                       boolean upperBoundInclusive )
    {
        this.lowerBound = lowerBound;
        this.lowerBoundInclusive = lowerBoundInclusive;
        this.upperBound = upperBound;
        this.upperBoundInclusive = upperBoundInclusive;
    }

    @Nullable
    public ArtifactVersion getLowerBound()
    {
        return lowerBound;
    }

    public boolean isLowerBoundInclusive()
    {
        return lowerBoundInclusive;
    }

    @Nullable
    public ArtifactVersion getUpperBound()
    {
        return upperBound;
    }

    public boolean isUpperBoundInclusive()
    {
        return upperBoundInclusive;
    }

    public boolean containsVersion( ArtifactVersion version )
    {
        if ( lowerBound != null )
        {
            int comparison = lowerBound.compareTo( version );

            if ( ( comparison == 0 ) && !lowerBoundInclusive )
            {
                return false;
            }
            if ( comparison > 0 )
            {
                return false;
            }
        }
        if ( upperBound != null )
        {
            int comparison = upperBound.compareTo( version );

            if ( ( comparison == 0 ) && !upperBoundInclusive )
            {
                return false;
            }
            if ( comparison < 0 )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = 13;

        if ( lowerBound == null )
        {
            result += 1;
        }
        else
        {
            result += lowerBound.hashCode();
        }

        result *= lowerBoundInclusive ? 1 : 2;

        if ( upperBound == null )
        {
            result -= 3;
        }
        else
        {
            result -= upperBound.hashCode();
        }

        result *= upperBoundInclusive ? 2 : 3;

        return result;
    }

    @Override
    public boolean equals( Object other )
    {
        if ( this == other )
        {
            return true;
        }

        if ( !( other instanceof Restriction ) )
        {
            return false;
        }

        Restriction restriction = (Restriction) other;
        if ( lowerBound != null )
        {
            if ( !lowerBound.equals( restriction.lowerBound ) )
            {
                return false;
            }
        }
        else if ( restriction.lowerBound != null )
        {
            return false;
        }

        if ( lowerBoundInclusive != restriction.lowerBoundInclusive )
        {
            return false;
        }

        if ( upperBound != null )
        {
            if ( !upperBound.equals( restriction.upperBound ) )
            {
                return false;
            }
        }
        else if ( restriction.upperBound != null )
        {
            return false;
        }

        if ( upperBoundInclusive != restriction.upperBoundInclusive )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        buf.append( isLowerBoundInclusive() ? "[" : "(" );
        if ( getLowerBound() != null )
        {
            buf.append( getLowerBound().toString() );
        }
        buf.append( "," );
        if ( getUpperBound() != null )
        {
            buf.append( getUpperBound().toString() );
        }
        buf.append( isUpperBoundInclusive() ? "]" : ")" );

        return buf.toString();
    }

    public String toStringFriendly()
    {
        if ( getLowerBound() == null && getUpperBound() == null )
        {
            return I18n.translateToLocal("fml.messages.version.restriction.any");
        }
        else if ( getLowerBound() != null && getUpperBound() != null )
        {
            if ( getLowerBound().getVersionString().equals(getUpperBound().getVersionString()) )
            {
                return getLowerBound().getVersionString();
            }
            else
            {
                if (isLowerBoundInclusive() && isUpperBoundInclusive())
                {
                    return I18n.translateToLocalFormatted("fml.messages.version.restriction.bounded.inclusive", getLowerBound(), getUpperBound());
                }
                else if (isLowerBoundInclusive())
                {
                    return I18n.translateToLocalFormatted("fml.messages.version.restriction.bounded.upperexclusive", getLowerBound(), getUpperBound());
                }
                else if (isUpperBoundInclusive())
                {
                    return I18n.translateToLocalFormatted("fml.messages.version.restriction.bounded.lowerexclusive", getLowerBound(), getUpperBound());
                }
                else
                {
                    return I18n.translateToLocalFormatted("fml.messages.version.restriction.bounded.exclusive", getLowerBound(), getUpperBound());
                }
            }
        }
        else if ( getLowerBound() != null )
        {
            if ( isLowerBoundInclusive() )
            {
                return I18n.translateToLocalFormatted("fml.messages.version.restriction.lower.inclusive", getLowerBound());
            }
            else
            {
                return I18n.translateToLocalFormatted("fml.messages.version.restriction.lower.exclusive", getLowerBound());
            }
        }
        else
        {
            if ( isUpperBoundInclusive() )
            {
                return I18n.translateToLocalFormatted("fml.messages.version.restriction.upper.inclusive", getUpperBound());
            }
            else
            {
                return I18n.translateToLocalFormatted("fml.messages.version.restriction.upper.exclusive", getUpperBound());
            }
        }
    }
}
