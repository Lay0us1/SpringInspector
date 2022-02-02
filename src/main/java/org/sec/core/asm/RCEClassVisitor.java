package org.sec.core.asm;

import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.model.MethodReference;

public class RCEClassVisitor extends BaseClassVisitor {
    public RCEClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(targetMethod, targetIndex, RCEMethodAdapter.class);
    }
}
