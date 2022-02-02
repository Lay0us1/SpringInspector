package org.sec.core.asm;

import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.model.MethodReference;

public class XXEClassVisitor extends BaseClassVisitor {
    public XXEClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(targetMethod, targetIndex, XXEMethodAdapter.class);
    }
}
