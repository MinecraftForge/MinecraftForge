/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.loading.moddiscovery;

import net.minecraftforge.forgespi.language.ModFileScanData;

import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApiStatus.Internal
final class ModClassVisitor extends ClassVisitor {
    private static final Set<String> ANNOTATION_BLACKLIST;

    private static final Type OBJECT_TYPE = Type.getObjectType("java/lang/Object");
    private static final Type RECORD_TYPE = Type.getObjectType("java/lang/Record");

    static {
        var customBlacklist = System.getProperty("forge.annotationScanningBlacklist");
        if (customBlacklist != null) {
            ANNOTATION_BLACKLIST = Set.of(customBlacklist.split(","));
        } else {
            ANNOTATION_BLACKLIST = Set.of(
                    "Ljava/lang/Deprecated;",
                    "Ljava/lang/FunctionalInterface;",
                    "Ljava/lang/SafeVarargs;",

                    "Ljava/lang/annotation/Retention;",
                    "Ljava/lang/annotation/Target;",

                    "Ljavax/annotation/Nullable;",

                    "Lorg/jspecify/nullness/NonNull;",
                    "Lorg/jspecify/nullness/Nullable;",
                    "Lorg/jspecify/annotations/NullMarked;",
                    "Lorg/jspecify/annotations/NullUnmarked;",

                    "Lorg/jetbrains/annotations/ApiStatus$Internal;",
                    "Lorg/jetbrains/annotations/Contract;",
                    "Lorg/jetbrains/annotations/Nullable;",
                    "Lorg/jetbrains/annotations/NotNull;",
                    "Lorg/jetbrains/annotations/VisibleForTesting;",

                    "Lcom/google/common/annotations/VisibleForTesting;",

                    "Lnet/minecraftforge/api/distmarker/OnlyIn", // should not be used by mods
                    // @Mod.EventBusSubscriber doesn't use scan data for finding @SubscribeEvent annotated methods
                    "Lnet/minecraftforge/eventbus/api/listener/SubscribeEvent;"
            );
        }
    }

    private Type asmType;
    private Type asmSuperType;
    private Set<Type> interfaces;
    private final LinkedList<ModAnnotation> annotations = new LinkedList<>();

    ModClassVisitor() {
        super(Opcodes.ASM9);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.asmType = Type.getObjectType(name);
        this.asmSuperType = superName != null && !superName.isEmpty() ? switch (superName) {
            case "java/lang/Object" -> OBJECT_TYPE;
            case "java/lang/Record" -> RECORD_TYPE;
            default -> Type.getObjectType(superName);
        } : null;
        this.interfaces = Stream.of(interfaces).map(Type::getObjectType).collect(Collectors.toSet());
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String annotationName, final boolean runtimeVisible) {
        if (ANNOTATION_BLACKLIST.contains(annotationName)) return null;
        var ann = new ModAnnotation(ElementType.TYPE, Type.getType(annotationName), this.asmType.getClassName());
        annotations.addFirst(ann);
        return new ModAnnotationVisitor(ann);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new FieldVisitor(Opcodes.ASM9) {
            @Override
            public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible) {
                if (ANNOTATION_BLACKLIST.contains(annotationName)) return null;
                var ann = new ModAnnotation(ElementType.FIELD, Type.getType(annotationName), name);
                annotations.addFirst(ann);
                return new ModAnnotationVisitor(ann);
            }
        };
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodVisitor(Opcodes.ASM9) {
            @Override
            public AnnotationVisitor visitAnnotation(String annotationName, boolean runtimeVisible) {
                if (ANNOTATION_BLACKLIST.contains(annotationName)) return null;
                ModAnnotation ann = new ModAnnotation(ElementType.METHOD, Type.getType(annotationName), name + desc);
                annotations.addFirst(ann);
                return new ModAnnotationVisitor(ann);
            }
        };
    }

    public void buildData(final Set<ModFileScanData.ClassData> classes, final Set<ModFileScanData.AnnotationData> baked) {
        classes.add(new ModFileScanData.ClassData(this.asmType, this.asmSuperType, this.interfaces));
        for (var a : this.annotations) {
            //if (ModFileScanData.interestingAnnotations().test(a.getASMType())) // Always true, so who cares. Left as a note to remove this in SPI later.
            baked.add(ModAnnotation.fromModAnnotation(this.asmType, a));
        }
    }

    private final class ModAnnotationVisitor extends AnnotationVisitor {
        private final ModAnnotation annotation;
        private boolean array;
        private boolean isSubAnnotation;

        public ModAnnotationVisitor(ModAnnotation annotation) {
            super(Opcodes.ASM9);
            this.annotation = annotation;
        }

        public ModAnnotationVisitor(ModAnnotation annotation, String name) {
            this(annotation);
            this.array = true;
            annotation.addArray(name);
        }

        public ModAnnotationVisitor(ModAnnotation annotation, boolean isSubAnnotation) {
            this(annotation);
            this.isSubAnnotation = true;
        }

        @Override
        public void visit(String key, Object value) {
            annotation.addProperty(key, value);
        }

        @Override
        public void visitEnum(String name, String desc, String value) {
            annotation.addEnumProperty(name, desc, value);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            return new ModAnnotationVisitor(annotation, name);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String desc) {
            var ma = annotations.getFirst();
            var childAnnotation = ma.addChildAnnotation(name, desc);
            annotations.addFirst(childAnnotation);
            return new ModAnnotationVisitor(childAnnotation, true);
        }

        @Override
        public void visitEnd() {
            if (array)
                annotation.endArray();

            if (isSubAnnotation)
                annotations.addLast(annotations.removeFirst());
        }
    }
}
