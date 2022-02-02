package org.sec.core.asm;

import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.model.MethodReference;

public class LogClassVisitor extends BaseClassVisitor {
    public LogClassVisitor(MethodReference.Handle targetMethod, int targetIndex) {
        super(targetMethod, targetIndex, LogMethodAdapter.class);
    }
}
