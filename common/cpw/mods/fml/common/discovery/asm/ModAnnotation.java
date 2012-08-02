package cpw.mods.fml.common.discovery.asm;

import java.util.Map;

import org.objectweb.asm.Type;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.discovery.asm.ASMModParser.AnnotationType;

public class ModAnnotation
{
    AnnotationType type;
    Type asmType;
    String member;
    Map<String,Object> values = Maps.newHashMap();
    public ModAnnotation(AnnotationType type, Type asmType, String member)
    {
        this.type = type;
        this.asmType = asmType;
        this.member = member;
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
}