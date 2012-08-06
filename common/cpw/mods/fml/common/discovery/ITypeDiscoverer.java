package cpw.mods.fml.common.discovery;

import java.util.List;
import java.util.regex.Pattern;

import cpw.mods.fml.common.ModContainer;

public interface ITypeDiscoverer
{
    public static Pattern classFile = Pattern.compile("([^\\s$]+).class$");

    public List<ModContainer> discover(ModCandidate candidate, ASMDataTable table);
}
