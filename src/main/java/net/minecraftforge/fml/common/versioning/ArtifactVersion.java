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

package net.minecraftforge.fml.common.versioning;

//I beleive this is a unique class except that it uses the same name so that ComparibleVersion can stay the same.
//Best reference I could find: https://github.com/apache/maven/blob/3501485ed2280e30ba74eb9f0e1c6422b68a3228/maven-artifact/src/main/java/org/apache/maven/artifact/versioning/ArtifactVersion.java
//This entire package *should* be removed and updated to normal maven-artifact library in 1.13.

public interface ArtifactVersion
    extends Comparable<ArtifactVersion>
{
    String getVersionString();

    boolean containsVersion(ArtifactVersion source);

    String getRangeString();
}
