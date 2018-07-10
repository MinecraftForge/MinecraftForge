/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.relauncher;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

public final class FileListHelper {
    private enum CaseInsensitiveFileComparator implements Comparator<File>
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
    public static File[] sortFileList(File dir, @Nullable FilenameFilter filter)
    {
        File[] files = dir.listFiles(filter);
        return sortFileList(files);
    }
}
