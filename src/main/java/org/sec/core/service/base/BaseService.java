package org.sec.core.service.base;

import org.apache.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.sec.core.asm.DOSClassVisitor;
import org.sec.core.asm.SqlInjectClassVisitor;
import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.core.data.DoSCollector;
import org.sec.core.data.SqliCollector;
import org.sec.core.spring.SpringController;
import org.sec.core.spring.SpringMapping;
import org.sec.model.CallGraph;
import org.sec.model.ClassFile;
import org.sec.model.MethodReference;
import org.sec.model.ResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("all")
public abstract class BaseService {
    private static Logger logger;
    private static Map<MethodReference.Handle, Set<CallGraph>> allCalls;
    private static Map<String, ClassFile> classFileMap;
    private static final List<String> tempChain = new ArrayList<>();
    private static final List<ResultInfo> results = new ArrayList<>();
    private static Class<?> classVisitor;
    private static Class<?> collector;

    private static boolean isSqli;
    private static boolean hasSqlInject;

    public static void start0(Map<String, ClassFile> classFileByName,
                              List<SpringController> controllers,
                              Map<MethodReference.Handle, Set<CallGraph>> discoveredCalls,
                              Class<?> targetClassVisitor, Class<?> targetCollector,
                              String moduleName, boolean sqli) {
        results.clear();
        logger = LoggerFactory.getLogger(targetClassVisitor);
        allCalls = discoveredCalls;
        classFileMap = classFileByName;
        classVisitor = targetClassVisitor;
        collector = targetCollector;
        isSqli = sqli;
        logger.info("start analysis " + moduleName);
        for (SpringController controller : controllers) {
            for (SpringMapping mapping : controller.getMappings()) {
                MethodReference methodReference = mapping.getMethodReference();
                if (methodReference == null) {
                    continue;
                }
                Type[] argTypes = Type.getArgumentTypes(methodReference.getDesc());
                Type[] extendedArgTypes = new Type[argTypes.length + 1];
                System.arraycopy(argTypes, 0, extendedArgTypes, 1, argTypes.length);
                argTypes = extendedArgTypes;
                boolean[] vulnerableIndex = new boolean[argTypes.length];
                for (int i = 1; i < argTypes.length; i++) {
                    vulnerableIndex[i] = true;
                }
                Set<CallGraph> calls = allCalls.get(methodReference.getHandle());
                if (calls == null || calls.size() == 0) {
                    continue;
                }
                tempChain.add(methodReference.getClassReference().getName() + "." + methodReference.getName());
                for (CallGraph callGraph : calls) {
                    int callerIndex = callGraph.getCallerArgIndex();
                    if (callerIndex == -1) {
                        continue;
                    }
                    if (vulnerableIndex[callerIndex]) {
                        tempChain.add(callGraph.getTargetMethod().getClassReference().getName() + "." +
                                callGraph.getTargetMethod().getName());
                        List<MethodReference.Handle> visited = new ArrayList<>();
                        doTask(callGraph.getTargetMethod(), callGraph.getTargetArgIndex(), visited);
                        if (isSqli) {
                            if (hasSqlInject) {
                                SqliCollector.collect(tempChain, results);
                            }
                            hasSqlInject = false;
                        }
                    }
                }
                tempChain.clear();
            }
        }
    }

    public static List<ResultInfo> getResults() {
        return results;
    }

    private static void doTask(MethodReference.Handle targetMethod, int targetIndex,
                               List<MethodReference.Handle> visited) {
        if (visited.contains(targetMethod)) {
            return;
        } else {
            visited.add(targetMethod);
        }
        ClassFile file = classFileMap.get(targetMethod.getClassReference().getName());
        try {
            if (file == null) {
                return;
            }
            ClassReader cr = new ClassReader(file.getFile());
            Constructor<?> c = classVisitor.getConstructor(MethodReference.Handle.class, int.class);
            Object obj = c.newInstance(targetMethod, targetIndex);
            BaseClassVisitor cv = (BaseClassVisitor) obj;
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            if (isSqli) {
                SqlInjectClassVisitor sqlCv = (SqlInjectClassVisitor) cv;
                if (sqlCv.getSave().contains(true)) {
                    hasSqlInject = true;
                    return;
                }
            } else {
                Method method = collector.getMethod("collect", BaseClassVisitor.class, List.class, List.class);
                method.invoke(null, cv, tempChain, results);
            }
            tempChain.clear();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Set<CallGraph> calls = allCalls.get(targetMethod);
        if (calls == null || calls.size() == 0) {
            return;
        }
        for (CallGraph callGraph : calls) {
            if (callGraph.getCallerArgIndex() == targetIndex && targetIndex != -1) {
                if (visited.contains(callGraph.getTargetMethod())) {
                    return;
                }
                tempChain.add(callGraph.getTargetMethod().getClassReference().getName() + "." +
                        callGraph.getTargetMethod().getName());
                doTask(callGraph.getTargetMethod(), callGraph.getTargetArgIndex(), visited);
            }
        }
    }
}
