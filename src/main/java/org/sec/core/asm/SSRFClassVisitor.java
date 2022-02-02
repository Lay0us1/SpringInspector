package org.sec.core.asm;

import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.model.MethodReference;

public class SSRFClassVisitor extends BaseClassVisitor {
    public SSRFClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(targetMethod, targetIndex, SSRFMethodAdapter.class);
    }
}
