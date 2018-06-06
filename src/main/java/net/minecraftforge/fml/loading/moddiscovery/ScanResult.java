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

package net.minecraftforge.fml.loading.moddiscovery;


import com.google.common.collect.Multimap;
import net.minecraftforge.fml.loading.FMLJavaModLanguageProvider;
import net.minecraftforge.fml.loading.IModLanguageProvider;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScanResult {
    private final ModFile file;
    private final List<ScanResult.AnnotationData> annotations = new ArrayList<>();
    private final List<ScanResult.ClassData> classes = new ArrayList<>();
    private Map<String,? extends IModLanguageProvider.IModLanguageLoader> modTargets;

    public ScanResult(final ModFile file) {
        this.file = file;
    }

    public ModFile getFile() {
        return file;
    }

    public static boolean interestingAnnotations(final ModAnnotation annotation) {
        return true;
    }

    public List<ScanResult.ClassData> getClasses() {
        return classes;
    }

    public List<ScanResult.AnnotationData> getAnnotations() {
        return annotations;
    }

    public void addLanguageLoader(Map<String,? extends IModLanguageProvider.IModLanguageLoader> modTargetMap)
    {
        modTargets = modTargetMap;
    }

    public Map<String, ? extends IModLanguageProvider.IModLanguageLoader> getTargets()
    {
        return modTargets;
    }


    public static class ClassData {
        private final Type clazz;
        private final Type parent;
        private final Set<Type> interfaces;

        public ClassData(final Type clazz, final Type parent, final Set<Type> interfaces) {
            this.clazz = clazz;
            this.parent = parent;
            this.interfaces = interfaces;
        }

    }
    public static class AnnotationData {
        private final Type annotationType;
        private final Type clazz;
        private final String memberName;
        private final Map<String,Object> annotationData;


        public AnnotationData(final Type annotationType, final Type clazz, final String memberName, final Map<String, Object> annotationData) {
            this.annotationType = annotationType;
            this.clazz = clazz;
            this.memberName = memberName;
            this.annotationData = annotationData;
        }

        public Type getAnnotationType() {
            return annotationType;
        }

        public Type getClassType() {
            return clazz;
        }

        public String getMemberName() {
            return memberName;
        }

        public Map<String, Object> getAnnotationData() {
            return annotationData;
        }
        public static ScanResult.AnnotationData fromModAnnotation(final Type clazz, final ModAnnotation annotation) {
            return new AnnotationData(annotation.asmType, clazz, annotation.member, annotation.values);
        }

    }
}
