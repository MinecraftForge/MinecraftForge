package cpw.mods.fml.relauncher;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

public final class FileListHelper {
    private static enum CaseInsensitiveFileComparator implements Comparator<File>
    {
        INSTANCE;
        @Override
        public int compare(File o1, File o2)
        {
            return o1 != null && o2 != null ? o1.getName().compareToIgnoreCase(o2.getName()) : o1 == null ? -1 : 1;
        }

    }
    public static File[] sortFileList(File[] files)
    {
        Arrays.sort(files, CaseInsensitiveFileComparator.INSTANCE);
        return files;
    }
    public static File[] sortFileList(File dir, FilenameFilter filter)
    {
        File[] files = dir.listFiles(filter);
        return sortFileList(files);
    }
}