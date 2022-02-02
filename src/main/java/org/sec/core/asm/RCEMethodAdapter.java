package org.sec.core.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sec.core.asm.base.ParamTaintMethodAdapter;

import java.util.Map;

public class RCEMethodAdapter extends ParamTaintMethodAdapter {
    private final Map<String, Boolean> pass;

    public RCEMethodAdapter(int methodArgIndex, Map<String, Boolean> pass, int api, MethodVisitor mv,
                            String owner, int access, String name, String desc) {
        super(methodArgIndex, api, mv, owner, access, name, desc);
        this.pass = pass;
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.AASTORE) {
            super.visitInsn(opcode);
            if (operandStack.size() >= 1) {
                operandStack.set(0, true);
            }
            return;
        }
        super.visitInsn(opcode);
    }

    @Override
    @SuppressWarnings("all")
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        boolean buildStrCondition = owner.equals("java/lang/StringBuilder") &&
                name.equals("append") &&
                desc.equals("(Ljava/lang/String;)Ljava/lang/StringBuilder;");

        boolean toStringCondition = owner.equals("java/lang/StringBuilder") &&
                name.equals("toString") &&
                desc.equals("()Ljava/lang/String;");

        boolean runtimeCondition = owner.equals("java/lang/Runtime") && name.equals("exec") &&
                desc.equals("(Ljava/lang/String;)Ljava/lang/Process;");
        boolean processInitCondition = owner.equals("java/lang/ProcessBuilder") && name.equals("<init>") &&
                desc.equals("([Ljava/lang/String;)V");
        boolean processStartCondition = owner.equals("java/lang/ProcessBuilder") && name.equals("start") &&
                desc.equals("()Ljava/lang/Process;");
        boolean groovyCondition = owner.equals("groovy/lang/GroovyShell") && name.equals("evaluate") &&
                desc.equals("(Ljava/lang/String;)Ljava/lang/Object;");

        if (buildStrCondition) {
            if (operandStack.get(0).contains(true) ||
                    operandStack.get(1).contains(true)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, true);
                return;
            }
        }
        if (processInitCondition || toStringCondition) {
            if (operandStack.get(0).contains(true)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, true);
                return;
            }
        }
        if (runtimeCondition) {
            if (operandStack.get(0).contains(true)) {
                pass.put("RUNTIME", true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        if (processStartCondition) {
            if (operandStack.get(0).contains(true)) {
                pass.put("PROCESS", true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        if (groovyCondition) {
            if (operandStack.get(0).contains(true)) {
                pass.put("GROOVY", true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
