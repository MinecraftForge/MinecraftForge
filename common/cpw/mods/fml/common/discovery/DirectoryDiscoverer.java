package cpw.mods.fml.common.discovery;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import cpw.mods.fml.common.ModContainer;

public class DirectoryDiscoverer implements ITypeDiscoverer
{
    private class ClassFilter implements FileFilter
    {
        @Override
        public boolean accept(File file)
        {
            return (file.isFile() && modClass.matcher(file.getName()).find()) || file.isDirectory();
        }
    }

    @Override
    public List<ModContainer> discover(ModCandidate candidate)
    {
        if (path.length() == 0)
        {
            dirRoot = modDir;

        }
        boolean foundAModClass = false;
        File[] content = modDir.listFiles(new ClassFilter());

        // Always sort our content
        Arrays.sort(content);
        for (File file : content)
        {
            if (file.isDirectory())
            {
                log.finest(String.format("Recursing into package %s", path + file.getName()));
                attemptDirLoad(file, path + file.getName() + ".", sourceType, dirRoot);
                continue;
            }
            Matcher fname = modClass.matcher(file.getName());
            if (!fname.find())
            {
                continue;
            }
            String clazzName = path + fname.group(2);
            log.fine(String.format("Candidate mod %s found in directory %s", clazzName, dirRoot.getName()));
            candidates.add(new ModCandidate(sourceType, dirRoot, dirRoot, clazzName, sourceType));
            foundAModClass = true;
        }
        // TODO Auto-generated method stub
        return null;
    }

}
