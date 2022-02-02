package org.sec.core.asm;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.sec.core.asm.base.ParamTaintMethodAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DOSMethodAdapter extends ParamTaintMethodAdapter {
    private final Map<String, Boolean> pass;
    private static List<Boolean> flag = new ArrayList<>();

    private final List<Label> labelList = new ArrayList<>();
    private boolean forFlag;

    public DOSMethodAdapter(int methodArgIndex, Map<String, Boolean> pass, int api, MethodVisitor mv,
                            String owner, int access, String name, String desc) {
        super(methodArgIndex, api, mv, owner, access, name, desc);
        this.pass = pass;
    }

    @Override
    public void visitLabel(Label label) {
        this.labelList.add(label);
        super.visitLabel(label);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (opcode == Opcodes.GOTO) {
            if (labelList.contains(label)) {
                if (this.forFlag) {
                    pass.put("FOR-DOS", true);
                    this.forFlag = false;
                }
            }
        }
        if (opcode == Opcodes.IF_ICMPGE) {
            if (operandStack.get(0).contains(true)) {
                this.forFlag = true;
            }
        }
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        if (opcode == Opcodes.NEWARRAY) {
            if (operandStack.get(0).contains(true)) {
                pass.put("ARRAY-DOS", true);
            }
        }
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (opcode == Opcodes.ANEWARRAY) {
            if (operandStack.get(0).contains(true)) {
                pass.put("ARRAY-DOS", true);
            }
        }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        boolean patternMatches = (opcode == Opcodes.INVOKESTATIC) &&
                (owner.equals("java/util/regex/Pattern")) &&
                (name.equals("matches")) &&
                (desc.equals("(Ljava/lang/String;Ljava/lang/CharSequence;)Z"));
        if (patternMatches) {
            if (operandStack.get(0).contains(true)) {
                flag.add(true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
            if (operandStack.get(1).contains(true)) {
                flag.add(true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        if (flag.size() == 2 && !flag.contains(false)) {
            pass.put("RE-DOS", true);
            flag = new ArrayList<>();
        }

        boolean listInit = (opcode == Opcodes.INVOKESPECIAL) &&
                (owner.equals("java/util/ArrayList")) &&
                (name.equals("<init>")) &&
                (desc.equals("(I)V"));
        if (listInit) {
            if (operandStack.get(0).contains(true)) {
                pass.put("LIST-DOS", true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        boolean mapInit = (opcode == Opcodes.INVOKESPECIAL) &&
                (owner.equals("java/util/HashMap")) &&
                (name.equals("<init>")) &&
                (desc.equals("(I)V"));
        if (mapInit) {
            if (operandStack.get(0).contains(true)) {
                pass.put("MAP-DOS", true);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
}
