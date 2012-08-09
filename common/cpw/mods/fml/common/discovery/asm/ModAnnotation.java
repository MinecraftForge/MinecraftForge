package cpw.mods.fml.common.discovery.asm;

import java.util.ArrayList;
import java.util.Map;

import org.objectweb.asm.Type;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.discovery.asm.ASMModParser.AnnotationType;

public class ModAnnotation
{
    public class EnumHolder
    {

        private String desc;
        private String value;

        public EnumHolder(String desc, String value)
        {
            this.desc = desc;
            this.value = value;
        }

    }
    AnnotationType type;
    Type asmType;
    String member;
    Map<String,Object> values = Maps.newHashMap();
    private ArrayList<Object> arrayList;
    private Object array;
    private String arrayName;
    private ModAnnotation parent;
    public ModAnnotation(AnnotationType type, Type asmType, String member)
    {
        this.type = type;
        this.asmType = asmType;
        this.member = member;
    }
    
    public ModAnnotation(AnnotationType type, Type asmType, ModAnnotation parent)
    {
        this.type = type;
        this.asmType = asmType;
        this.parent = parent;
    }
    @Override
    public String toString()
    {
        return Objects.toStringHelper("Annotation")
                .add("type",type)
                .add("name",asmType.getClassName())
                .add("member",member)
                .add("values", values)
                .toString();
    }
    public AnnotationType getType()
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
        values.put(key, new EnumHolder(enumName, value));
    }
    
    public void endArray()
    {
        values.put(arrayName, arrayList);
        arrayList = null;
    }
    public ModAnnotation addChildAnnotation(String name, String desc)
    {
        return new ModAnnotation(AnnotationType.SUBTYPE, Type.getType(desc), this);
    }
}