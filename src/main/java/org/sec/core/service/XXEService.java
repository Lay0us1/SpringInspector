package org.sec.core.service;

import org.sec.core.asm.XXEClassVisitor;
import org.sec.core.data.XXECollector;
import org.sec.core.service.base.BaseService;
import org.sec.core.spring.SpringController;
import org.sec.log.SLF4J;
import org.sec.model.CallGraph;
import org.sec.model.ClassFile;
import org.sec.model.MethodReference;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;


@SLF4J
public class XXEService extends BaseService {
    private static Logger logger;

    public static void start(Map<String, ClassFile> classFileByName,
                             List<SpringController> controllers,
                             Map<MethodReference.Handle, Set<CallGraph>> discoveredCalls) {
        start0(classFileByName, controllers, discoveredCalls,
                XXEClassVisitor.class, XXECollector.class, "xxe", false);
    }
}
