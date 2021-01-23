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

package net.minecraftforge.fml.loading.moddiscovery;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Map;

import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ModAnnotation
{
    public static ModFileScanData.AnnotationData fromModAnnotation(final Type clazz, final ModAnnotation annotation) {
        return new ModFileScanData.AnnotationData(annotation.asmType, annotation.type, clazz, annotation.member, annotation.values);
    }

    public static class EnumHolder
    {
        private final String desc;
        private final String value;

        public EnumHolder(String desc, String value)
        {
            this.desc = desc;
            this.value = value;
        }

        public String getDesc()
        {
            return desc;
        }

        public String getValue()
        {
            return value;
        }
    }
    private final ElementType type;
    private final Type asmType;
    private final String member;
    private final Map<String,Object> values = Maps.newHashMap();

    private ArrayList<Object> arrayList;
    private String arrayName;
    public ModAnnotation(ElementType type, Type asmType, String member)
    {
        this.type = type;
        this.asmType = asmType;
        this.member = member;
    }

    public ModAnnotation(Type asmType, ModAnnotation parent)
    {
        this.type = parent.type;
        this.asmType = asmType;
        this.member = parent.member;
    }
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper("Annotation")
                .add("type",type)
                .add("name",asmType.getClassName())
                .add("member",member)
                .add("values", values)
                .toString();
    }

    public ElementType getType()
    {
        return type;
    }
    public Type getASMType()
    {
        return asmType;
    }
    public String getMember()
    {
        return member;
    }
    public Map<String, Object> getValues()
    {
        return values;
    }
    public void addArray(String name)
    {
        this.arrayList = Lists.newArrayList();
        this.arrayName = name;
    }
    public void addProperty(String key, Object value)
    {
        if (this.arrayList != null)
        {
            arrayList.add(value);
        }
        else
        {
            values.put(key, value);
        }
    }

    public void addEnumProperty(String key, String enumName, String value)
    {
        addProperty(key, new EnumHolder(enumName, value));
    }

    public void endArray()
    {
        values.put(arrayName, arrayList);
        arrayList = null;
    }
    public ModAnnotation addChildAnnotation(String name, String desc)
    {
        ModAnnotation child = new ModAnnotation(Type.getType(desc), this);
        addProperty(name, child.getValues());
        return child;
    }
}
