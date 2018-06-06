/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml;

import com.electronwill.nightconfig.core.conversion.Converter;

import java.net.MalformedURLException;
import java.net.URL;

public final class TomlConverters
{
    public static class StringToURL implements Converter<URL, String> {

        @Override
        public URL convertToField(String value)
        {
            try
            {
                return new URL(value);
            }
            catch (MalformedURLException e)
            {
                throw new RuntimeException("Invalid URL specified", e);
            }
        }

        @Override
        public String convertFromField(URL value)
        {
            return value.toString();
        }
    }
}
