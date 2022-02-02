package org.sec.core.asm;

import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.model.MethodReference;

public class DOSClassVisitor extends BaseClassVisitor {
    public DOSClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(targetMethod, targetIndex, DOSMethodAdapter.class);
    }
}
