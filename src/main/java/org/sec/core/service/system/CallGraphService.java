package org.sec.core.service.system;

import org.sec.core.asm.system.CallGraphClassVisitor;
import org.sec.log.SLF4J;
import org.sec.model.CallGraph;
import org.sec.model.ClassFile;
import org.sec.model.ClassReference;
import org.sec.model.MethodReference;
import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;

import java.util.*;

@SLF4J
public class CallGraphService {

    private static Logger logger;

    public static void start(Set<CallGraph> discoveredCalls,
                             List<MethodReference.Handle> sortedMethods,
                             Map<String, ClassFile> classFileByName,
                             Map<ClassReference.Handle, ClassReference> classMap,
                             Map<MethodReference.Handle, Set<CallGraph>> graphCallMap,
                             Map<MethodReference.Handle, MethodReference> methodMap,
                             Map<MethodReference.Handle, Set<MethodReference.Handle>> methodImplMap) {
        logger.info("build call graph");
        for (MethodReference.Handle method : sortedMethods) {
            ClassFile file = classFileByName.get(method.getClassReference().getName());
            try {
                ClassReader cr = new ClassReader(file.getFile());
                CallGraphClassVisitor cv = new CallGraphClassVisitor(discoveredCalls);
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // call graph set -> call graph list
        List<CallGraph> tempList = new ArrayList<>(discoveredCalls);
        // remove unnecessary method
        for (int i = 0; i < tempList.size(); i++) {
            MethodReference.Handle target = tempList.get(i).getTargetMethod();
            if (target.getClassReference().getName().equals("java/lang/Object")) {
                if (target.getName().equals("<init>") && target.getDesc().equals("()V")) {
                    tempList.remove(tempList.get(i));
                }
            }
        }
        // resolve interface problem: interface -> impls
        for (int i = 0; i < discoveredCalls.size(); i++) {
            if (i >= tempList.size()) {
                break;
            }
            MethodReference.Handle targetMethod = tempList.get(i).getTargetMethod();
            ClassReference.Handle handle = targetMethod.getClassReference();
            ClassReference targetClass = classMap.get(handle);
            if (targetClass == null) {
                continue;
            }
            if (targetClass.isInterface()) {
                Set<MethodReference.Handle> implSet = methodImplMap.get(targetMethod);
                if (implSet == null || implSet.size() == 0) {
                    continue;
                }
                for (MethodReference.Handle implMethod : implSet) {
                    String callerDesc = methodMap.get(implMethod).getDesc();
                    if (targetMethod.getDesc().equals(callerDesc)) {
                        tempList.add(new CallGraph(
                                targetMethod,
                                implMethod,
                                tempList.get(i).getTargetArgIndex(),
                                tempList.get(i).getTargetArgIndex()
                        ));
                    }
                }
            }
        }
        // unique
        discoveredCalls.clear();
        discoveredCalls.addAll(tempList);
        // build call graph map: method -> call graphs
        for (CallGraph graphCall : discoveredCalls) {
            MethodReference.Handle caller = graphCall.getCallerMethod();
            if (!graphCallMap.containsKey(caller)) {
                Set<CallGraph> graphCalls = new HashSet<>();
                graphCalls.add(graphCall);
                graphCallMap.put(caller, graphCalls);
            } else {
                graphCallMap.get(caller).add(graphCall);
            }
        }
    }
}
