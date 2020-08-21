/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import net.minecraftforge.fml.loading.EarlyLoadingException;
import net.minecraftforge.forgespi.language.IModFileInfo;

public class InvalidModFileException extends RuntimeException
{
    private final IModFileInfo modFileInfo;
    private final String i18nKey;

    public InvalidModFileException(String message, IModFileInfo modFileInfo)
    {
        super(String.format("%s (%s)", message, ((ModFileInfo)modFileInfo).getFile().getFileName()));
        this.modFileInfo = modFileInfo;
        this.i18nKey = null;
    }

    public InvalidModFileException(String message, String i18nKey, IModFileInfo modFileInfo)
    {
        super(String.format("%s (%s)", message, ((ModFileInfo)modFileInfo).getFile().getFileName()));
        this.modFileInfo = modFileInfo;
        this.i18nKey = i18nKey;
    }

    public EarlyLoadingException.ExceptionData makeExceptionData() {
        if (this.i18nKey == null)
            return new EarlyLoadingException.ExceptionData("fml.modloading.generalerror", this.getMessage());
        else
            return new EarlyLoadingException.ExceptionData(i18nKey, ((ModFileInfo)modFileInfo).getFile().getFileName());
    }
}
