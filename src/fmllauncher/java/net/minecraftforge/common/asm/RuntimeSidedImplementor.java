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

package net.minecraftforge.common.asm;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.AdvancedLogMessageAdapter;
import net.minecraftforge.fml.loading.FMLEnvironment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;

/**
 * Modifies specified enums to allow runtime extension by making the $VALUES field non-final and
 * injecting constructor calls which are not valid in normal java code.
 */
public class RuntimeSidedImplementor implements ILaunchPluginService
{

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker DISTINJECT = MarkerManager.getMarker("DISTINJECT");
    private static String DIST;
    private static final Type IMPLEMENT_ON = Type.getType("Lnet/minecraftforge/fml/ImplementOn;");

    @Override
    public String name()
    {
        return "runtimesidedimplementer";
    }

    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.AFTER);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);

    @SuppressWarnings("unchecked")
    @Override
    public Consumer<Dist> getExtension()
    {
        return s ->
        {
            DIST = s.name();
            LOGGER.debug(DISTINJECT, "Configuring for Dist {}", DIST);
        };
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty)
    {
        return isEmpty ? NAY : YAY;
    }

    private boolean hasImplementOn(List<AnnotationNode> annotations)
    {
        return annotations != null && annotations.stream().anyMatch(annotation -> IMPLEMENT_ON.equals(Type.getType(annotation.desc)));
    }

    private AnnotationNode findImplementsOn(MethodNode node)
    {
        if (node.invisibleAnnotations != null)
        {
            AnnotationNode found = node.invisibleAnnotations.stream()
                    .filter(annotation -> IMPLEMENT_ON.equals(Type.getType(annotation.desc)))
                    .findFirst()
                    .orElse(null);

            if (found != null)
                return found;
        }

        if (node.visibleAnnotations != null)
        {
            return node.visibleAnnotations.stream()
                    .filter(annotation -> IMPLEMENT_ON.equals(Type.getType(annotation.desc)))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    private static Object getAnnotationValue(AnnotationNode annotation, String parameter)
    {
        List<Object> values = annotation.values;
        for (int i = 0, l = values.size(); i < l; i += 2)
        {
            if (values.get(i).equals(parameter))
                return values.get(i + 1);
        }
        return null;
    }

    // Scans method instructions for a call to an ImplementOn annotated method, relying on the predicate that checks that
    private static void checkMethodCall(ClassNode owner, MethodNode method, Predicate<String> implementOnCheck)
    {
        for (AbstractInsnNode insn : method.instructions)
        {
            if (insn instanceof MethodInsnNode)
            {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;

                if (owner.name.equals(methodInsn.owner) && implementOnCheck.test(methodInsn.name + methodInsn.desc))
                {
                    LOGGER.fatal(() -> new AdvancedLogMessageAdapter(sb -> {
                        sb.append("Found manual call to ImplementOn annotated method:\n");
                        sb.append("  Class: ").append(owner.name).append("\n");
                        sb.append("  In:    ").append(method.name).append(method.desc).append("\n");
                        sb.append("  To:    ").append(methodInsn.name).append(methodInsn.desc);
                    }));
                    throw new IllegalStateException("Manual ImplementOn call: " + methodInsn.name + methodInsn.desc);
                }
            }
        }
    }

    @Override
    public int processClassWithFlags(final Phase phase, final ClassNode classNode, final Type classType, final String reason)
    {
        if((classNode.access & Opcodes.ACC_ANNOTATION) != 0)
            return ComputeFlags.NO_REWRITE; // Skip annotation classes

        // Methods by name and desc, i.e. by 'methodname(Ldescriptor;)V' like strings
        Map<String, MethodNode> methodsByNameDesc = new HashMap<>();
        classNode.methods.forEach(mtd -> methodsByNameDesc.put(mtd.name + mtd.desc, mtd));

        // Methods with ImplementOn annotation
        List<MethodNode> implementations = classNode.methods.stream()
                .filter(mtd -> hasImplementOn(mtd.visibleAnnotations) || hasImplementOn(mtd.invisibleAnnotations))
                .collect(Collectors.toList());

        if (implementations.isEmpty())
        {
            return ComputeFlags.NO_REWRITE;
        }

        // A set of name-desc strings of all methods with ImplementOn annotation
        // Since we strip ImplementOn methods on wrong dists, we must first precheck if any method uses them and throw an
        // exception if so
        Set<String> implNameDescs = implementations.stream()
                .map(mtd -> mtd.name + mtd.desc)
                .collect(Collectors.toSet());

        // Since checking all instructions of a class on load is much work, do it only on development workspace
        if (!FMLEnvironment.production)
            classNode.methods.forEach(mtd -> checkMethodCall(classNode, mtd, implNameDescs::contains));

        // What methods will be delegated to what other methods
        List<Delegation> delegations = new ArrayList<>();
        List<MethodNode> removals = new ArrayList<>();

        // Targeted methods, to ensure a method is only targeted once
        Set<String> targeted = new HashSet<>();

        implementations.forEach(method ->
        {
            AnnotationNode annotation = findImplementsOn(method);
            if (annotation == null)
                return; // Annotation is never null because we filtered methods with this annotation already, still skip it so that IDE is happy

            String[] distPar = (String[]) getAnnotationValue(annotation, "dist");
            String targetName = (String) getAnnotationValue(annotation, "method");

            // The nature of ImplementOn annotation already prevents these from being null
            assert distPar != null;
            assert targetName != null;

            // Use our descriptor since they must be equal
            String nameDesc = targetName + method.desc;

            // Annotated method must be private
            if ((method.access & Opcodes.ACC_PRIVATE) == 0)
            {
                LOGGER.fatal(() -> new AdvancedLogMessageAdapter(sb -> {
                    sb.append("ImplementOn method must be private:\n");
                    sb.append("  Class:  ").append(classNode.name).append("\n");
                    sb.append("  Method: ").append(method.name).append(method.desc);
                }));
                throw new IllegalStateException("Non-private ImplementOn method: " + method.name + method.desc);
            }

            // Find target, error if not found
            // This also checks if descriptors match, which is mandatory
            MethodNode target = methodsByNameDesc.get(nameDesc);
            if (target == null)
            {
                LOGGER.fatal(() -> new AdvancedLogMessageAdapter(sb -> {
                    sb.append("Could not find matching ImplementOn method:\n");
                    sb.append("  Class:  ").append(classNode.name).append("\n");
                    sb.append("  Method: ").append(method.name).append(method.desc).append("\n");
                    sb.append("  Target: ").append(targetName).append(method.desc).append("\n");
                    sb.append("Ensure that the arguments and return type of the ImplementOn method match that of the target method");
                }));
                throw new IllegalStateException("ImplementOn target not found: " + targetName + method.desc);
            }

            // One must not implement a method that is ImplementOn itself
            if(hasImplementOn(target.invisibleAnnotations) || hasImplementOn(target.visibleAnnotations)) {
                LOGGER.fatal(() -> new AdvancedLogMessageAdapter(sb -> {
                    sb.append("Matching ImplementOn method is an ImplementOn method itself:\n");
                    sb.append("  Class:  ").append(classNode.name).append("\n");
                    sb.append("  Method: ").append(method.name).append(method.desc).append("\n");
                    sb.append("  Target: ").append(targetName).append(method.desc).append("\n");
                    sb.append("Ensure that the arguments and return type of the ImplementOn method match that of the target method");
                }));
                throw new IllegalStateException("ImplementOn target not found: " + targetName + method.desc);
            }

            // Check 'static' modifiers, which must match
            if ((target.access & Opcodes.ACC_STATIC) != (method.access & Opcodes.ACC_STATIC))
            {
                LOGGER.fatal(() -> new AdvancedLogMessageAdapter(sb -> {
                    sb.append("'static' modifiers of ImplementOn method and target method do not match:\n");
                    sb.append("  Class:  ").append(classNode.name).append("\n");
                    sb.append("  Method: ").append(method.name).append(method.desc)
                            .append(", Static: ").append((method.access & Opcodes.ACC_STATIC) != 0).append("\n");
                    sb.append("  Target: ").append(target.name).append(target.desc)
                            .append(", Static: ").append((target.access & Opcodes.ACC_STATIC) != 0);
                }));
                throw new IllegalStateException("ImplementOn target does not have same 'static' modifier: " + target.name + target.desc);
            }

            if (distPar[1].equals(DIST))
            {
                // Ensure only one ImplementOn matches target
                if (targeted.contains(nameDesc))
                {
                    LOGGER.fatal(() -> new AdvancedLogMessageAdapter(sb -> {
                        sb.append("Method is targeted twice by ImplementOn annotation from the same dist:\n");
                        sb.append("  Class:  ").append(classNode.name).append("\n");
                        sb.append("  Method: ").append(method.name).append(method.desc).append("\n");
                        sb.append("  Target: ").append(target.name).append(target.desc).append("\n");
                        sb.append("  Dist:   ").append(DIST);
                    }));
                    throw new IllegalStateException("ImplementOn target does not have same 'static' modifier: " + target.name + target.desc);
                }

                targeted.add(nameDesc);
                delegations.add(new Delegation(classNode, target, method));
            } else {
                removals.add(method);
            }

        });

        // Last skip if no delegations or removals were found - often does not happen but we go for perfection
        if (delegations.isEmpty() && removals.isEmpty())
        {
            return ComputeFlags.NO_REWRITE;
        }

        // Inject all delegations
        for (Delegation delegation : delegations)
        {
            delegation.inject();
        }

        return ComputeFlags.SIMPLE_REWRITE;
    }

    private static class Delegation
    {
        private final ClassNode owner;
        private final MethodNode incidence;
        private final MethodNode reference;

        private Delegation(ClassNode owner, MethodNode incidence, MethodNode reference)
        {
            this.owner = owner;
            this.incidence = incidence;
            this.reference = reference;
        }

        public void inject()
        {
            LOGGER.debug(DISTINJECT, "Injecting implementation {}{} into {}{} for dist {}",
                    reference.name, reference.desc, incidence.name, incidence.desc, DIST);

            boolean iface = (owner.access & Opcodes.ACC_INTERFACE) != 0;
            boolean virtual = (reference.access & Opcodes.ACC_STATIC) == 0;

            // Remove abstract or native modifier (which might be used to strip the method body)
            incidence.access &= ~Opcodes.ACC_ABSTRACT;
            incidence.access &= ~Opcodes.ACC_NATIVE;

            InsnList newInsns = new InsnList();

            // Push arguments
            Type[] args = Type.getArgumentTypes(incidence.desc);
            Type ret = Type.getReturnType(incidence.desc);

            int localIndex = 0;
            if (virtual) // Push 'this'
                newInsns.add(new VarInsnNode(Opcodes.ALOAD, localIndex++));

            for (Type arg : args)
            {
                newInsns.add(new VarInsnNode(loadOpcode(arg), localIndex));
                localIndex += arg.getSize();
            }

            // Call target method
            MethodInsnNode invoke = new MethodInsnNode(
                    virtual ? Opcodes.INVOKEVIRTUAL : Opcodes.INVOKESTATIC,
                    owner.name, reference.name, reference.desc, iface);
            newInsns.add(invoke);

            // Return value
            newInsns.add(new InsnNode(returnOpcode(ret)));

            // Set new instructions, stripping the complete method body if one was defined
            incidence.instructions = newInsns;

            // Since localIndex counted all local variable sizes, it is now the correct value of maxLocals
            incidence.maxLocals = localIndex;

            // This amount of local variables is also the size we need for the stack, but we have to ensure
            // that there is also space for the return value after calling the method, hence maxing it with
            // the return value size (max is enough since the invocation will consume everything in the stack)
            incidence.maxStack = Math.max(localIndex, ret.getSize());
        }

        // Gets the correct LOAD opcode for the given argument type
        private static int loadOpcode(Type type)
        {
            switch (type.getSort())
            {
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return Opcodes.ILOAD;
            case Type.LONG:
                return Opcodes.LLOAD;
            case Type.FLOAT:
                return Opcodes.FLOAD;
            case Type.DOUBLE:
                return Opcodes.DLOAD;
            }
            return Opcodes.ALOAD;
        }

        // Gets the correct RETURN opcode for the given return type
        private static int returnOpcode(Type type)
        {
            switch (type.getSort())
            {
            case Type.VOID:
                return Opcodes.RETURN;
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return Opcodes.IRETURN;
            case Type.LONG:
                return Opcodes.LRETURN;
            case Type.FLOAT:
                return Opcodes.FRETURN;
            case Type.DOUBLE:
                return Opcodes.DRETURN;
            }
            return Opcodes.ARETURN;
        }
    }
}
