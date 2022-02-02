package org.sec.core.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.model.MethodReference;

import java.util.ArrayList;
import java.util.List;

public class SqlInjectClassVisitor extends BaseClassVisitor {
    private final MethodReference.Handle methodHandle;
    private final int methodArgIndex;
    private final List<Boolean> save;

    public SqlInjectClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(null, 0, null);
        this.methodHandle = targetMethod;
        this.methodArgIndex = targetIndex;
        this.save = new ArrayList<>();
    }

    public List<Boolean> getSave() {
        return save;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.equals(this.methodHandle.getName())) {
            SqlInjectMethodAdapter sqlInjectMethodAdapter = new SqlInjectMethodAdapter(
                    this.methodArgIndex, this.save, Opcodes.ASM6, mv,
                    this.name, access, name, descriptor);
            return new JSRInlinerAdapter(sqlInjectMethodAdapter,
                    access, name, descriptor, signature, exceptions);
        }
        return mv;
    }
}
