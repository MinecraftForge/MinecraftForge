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

package net.minecraftforge.fml.common.asm.transformers;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

public class ModAPITransformer implements IClassTransformer {

    private static final boolean logDebugInfo = Boolean.valueOf(System.getProperty("fml.debugAPITransformer", "false"));
    private ListMultimap<String, ASMData> optionals;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        String lookupName = name;
        if(name.endsWith("$class"))
        {
            lookupName = name.substring(0, name.length() - 6);
        }
        if (optionals == null || !optionals.containsKey(lookupName))
        {
            return basicClass;
        }
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        if (logDebugInfo) FMLLog.log.trace("Optional removal - found optionals for class {} - processing", name);
        for (ASMData optional : optionals.get(lookupName))
        {
            String modId = (String) optional.getAnnotationInfo().get("modid");

            if (Loader.isModLoaded(modId) || ModAPIManager.INSTANCE.hasAPI(modId))
            {
                if (logDebugInfo) FMLLog.log.trace("Optional removal skipped - mod present {}", modId);
                continue;
            }
            if (logDebugInfo) FMLLog.log.trace("Optional on {} triggered - mod missing {}", name, modId);

            if (optional.getAnnotationInfo().containsKey("iface"))
            {
                Boolean stripRefs = (Boolean)optional.getAnnotationInfo().get("striprefs");
                if (stripRefs == null) stripRefs = Boolean.FALSE;
                stripInterface(classNode,(String)optional.getAnnotationInfo().get("iface"), stripRefs);
            }
            else
            {
                stripMethod(classNode, optional.getObjectName());
            }

        }
        if (logDebugInfo) FMLLog.log.trace("Optional removal - class {} processed", name);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private void stripMethod(ClassNode classNode, String methodDescriptor)
    {
        if(classNode.name.endsWith("$class"))
        {
            String subName = classNode.name.substring(0, classNode.name.length() - 6);
            int pos = methodDescriptor.indexOf('(') + 1;
            methodDescriptor = methodDescriptor.substring(0, pos) + 'L' + subName + ';' + methodDescriptor.substring(pos);
        }
        for (ListIterator<MethodNode> iterator = classNode.methods.listIterator(); iterator.hasNext();)
        {
            MethodNode method = iterator.next();
            if (methodDescriptor.equals(method.name+method.desc))
            {
                iterator.remove();
                if (logDebugInfo) FMLLog.log.debug("Optional removal - method {} removed", methodDescriptor);
                return;
            }
        }
        if (logDebugInfo) FMLLog.log.debug("Optional removal - method {} NOT removed - not found", methodDescriptor);
    }

    private void stripInterface(ClassNode classNode, String interfaceName, boolean stripRefs)
    {
        final String ifaceName = interfaceName.replace('.', '/');
        boolean found = classNode.interfaces.remove(ifaceName);
        if (found && classNode.signature != null)
        {
            SignatureReader sr = new SignatureReader(classNode.signature);
            final RemovingSignatureWriter signatureWriter = new RemovingSignatureWriter(ifaceName);
            sr.accept(signatureWriter);
            classNode.signature = signatureWriter.toString();
            if (logDebugInfo) FMLLog.log.debug("Optional removal - interface {} removed from type signature", interfaceName);
        }
        if (found && logDebugInfo) FMLLog.log.debug("Optional removal - interface {} removed", interfaceName);
        if (!found && logDebugInfo) FMLLog.log.debug("Optional removal - interface {} NOT removed - not found", interfaceName);

        if (found && stripRefs)
        {
            if (logDebugInfo) FMLLog.log.debug("Optional removal - interface {} - stripping method signature references", interfaceName);
            for (Iterator<MethodNode> iterator = classNode.methods.iterator(); iterator.hasNext();)
            {
                MethodNode node = iterator.next();
                if (node.desc.contains(ifaceName))
                {
                    if (logDebugInfo) FMLLog.log.debug("Optional removal - interface {} - stripping method containing reference {}", interfaceName, node.name);
                    iterator.remove();
                }
            }
            if (logDebugInfo) FMLLog.log.debug("Optional removal - interface {} - all method signature references stripped", interfaceName);
        }
        else if (found)
        {
            if (logDebugInfo) FMLLog.log.debug("Optional removal - interface {} - NOT stripping method signature references", interfaceName);
        }
    }

    public void initTable(ASMDataTable dataTable)
    {
        optionals = ArrayListMultimap.create();
        Set<ASMData> interfaceLists = dataTable.getAll("net.minecraftforge.fml.common.Optional$InterfaceList");
        addData(unpackInterfaces(interfaceLists));
        Set<ASMData> interfaces = dataTable.getAll("net.minecraftforge.fml.common.Optional$Interface");
        addData(interfaces);
        Set<ASMData> methods = dataTable.getAll("net.minecraftforge.fml.common.Optional$Method");
        addData(methods);
    }

    private Set<ASMData> unpackInterfaces(Set<ASMData> packedInterfaces)
    {
        Set<ASMData> result = Sets.newHashSet();
        for (ASMData data : packedInterfaces)
        {
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> packedList = (List<Map<String,Object>>) data.getAnnotationInfo().get("value");
            for (Map<String,Object> packed : packedList)
            {
                ASMData newData = data.copy(packed);
                result.add(newData);
            }
        }

        return result;
    }
    private void addData(Set<ASMData> interfaces)
    {
        for (ASMData data : interfaces)
        {
            optionals.put(data.getClassName(),data);
        }
    }

    private static class RemovingSignatureWriter extends SignatureWriter
    {
        private final String ifaceName;

        RemovingSignatureWriter(String ifaceName)
        {
            this.ifaceName = ifaceName;
        }

        @Override
        public void visitClassType(String name)
        {
            if (name.equals(ifaceName)) {
                super.visitClassType("java/lang/Object");
            }
            else
            {
                super.visitClassType(name);
            }
        }
    }
}
