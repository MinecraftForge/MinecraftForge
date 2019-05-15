/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import java.util.EnumSet;
import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;

@Deprecated // TODO as this only targets a single class, it can be moved to a JS coremod
public abstract class FieldRedirectTransformer implements ILaunchPluginService
{
    private final String clsName;
    private final String TYPE;
    private final String DESC;
    private final String bypass;

    protected FieldRedirectTransformer(String cls, String type, String bypass)
    {
        this.clsName = cls;
        this.TYPE = type;
        this.DESC = "()" + type;
        this.bypass = bypass;
    }
    
    private static final EnumSet<Phase> YAY = EnumSet.of(Phase.AFTER);
    private static final EnumSet<Phase> NAY = EnumSet.noneOf(Phase.class);
    
    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty)
    {
        return classType.getClassName().equals(clsName) && !isEmpty ? YAY : NAY;
    }
    
    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType)
    {
        FieldNode fieldRef = null;
        for (FieldNode f : classNode.fields)
        {
            if (this.TYPE.equals(f.desc) && fieldRef == null)
            {
                fieldRef = f;
            }
            else if (this.TYPE.equals(f.desc))
            {
                throw new RuntimeException("Error processing " + clsName + " - found a duplicate holder field");
            }
        }
        if (fieldRef == null)
        {
            throw new RuntimeException("Error processing " + clsName + " - no holder field declared (is the code somehow obfuscated?)");
        }

        MethodNode getMethod = null;
        for (MethodNode m: classNode.methods)
        {
            if (m.name.equals(this.bypass)) continue;
            if (this.DESC.equals(m.desc) && getMethod == null)
            {
                getMethod = m;
            }
            else if (this.DESC.equals(m.desc))
            {
                throw new RuntimeException("Error processing " + clsName + " - duplicate get method found");
            }
        }
        if (getMethod == null)
        {
            throw new RuntimeException("Error processing " + clsName + " - no get method found (is the code somehow obfuscated?)");
        }

        for (MethodNode m: classNode.methods)
        {
            if (m.name.equals(this.bypass)) continue;
            for (ListIterator<AbstractInsnNode> it = m.instructions.iterator(); it.hasNext(); )
            {
                AbstractInsnNode insnNode = it.next();
                if (insnNode.getType() == AbstractInsnNode.FIELD_INSN)
                {
                    FieldInsnNode fi = (FieldInsnNode)insnNode;
                    if (fieldRef.name.equals(fi.name) && fi.getOpcode() == Opcodes.GETFIELD)
                    {
                        it.remove();
                        MethodInsnNode replace = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classNode.name, getMethod.name, getMethod.desc, false);
                        it.add(replace);
                    }
                }
            }
        }
        
        return true;
    }

    @Override
    public String name()
    {
        return "field_redirect_" + clsName + "/" + TYPE;
    }
}