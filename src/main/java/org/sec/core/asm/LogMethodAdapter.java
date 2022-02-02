package org.sec.core.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.sec.core.asm.base.ParamTaintMethodAdapter;

import java.util.Map;

public class LogMethodAdapter extends ParamTaintMethodAdapter {
    private final Map<String, Boolean> pass;

    public LogMethodAdapter(int methodArgIndex, Map<String, Boolean> pass, int api, MethodVisitor mv,
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
        boolean toStringCondition = owner.equals("java/lang/StringBuilder") &&
                name.equals("toString") &&
                desc.equals("()Ljava/lang/String;");
        boolean buildStrCondition = owner.equals("java/lang/StringBuilder") &&
                name.equals("append") &&
                desc.equals("(Ljava/lang/String;)Ljava/lang/StringBuilder;");

        boolean function = name.equals("info") || name.equals("warn") || name.equals("error");
        boolean slf4jCondition = owner.equals("org/slf4j/Logger") && function;
        boolean log4jCondition = owner.equals("org/apache/logging/log4j/Logger") && function;

        if (buildStrCondition) {
            if (operandStack.get(0).contains(true) ||
                    operandStack.get(1).contains(true)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, true);
                return;
            }
        }
        if (toStringCondition) {
            if (operandStack.get(0).contains(true)) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                operandStack.set(0, true);
                return;
            }
        }
        if (slf4jCondition) {
            if (operandStack.get(0).contains(true)) {
                pass.put("SLF4J", true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        if (log4jCondition) {
            if (operandStack.get(0).contains(true)) {
                pass.put("LOG4J", true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
