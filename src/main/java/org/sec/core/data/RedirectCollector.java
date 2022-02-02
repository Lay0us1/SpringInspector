package org.sec.core.data;

import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.core.spring.SpringMapping;
import org.sec.log.SLF4J;
import org.sec.model.ResultInfo;
import org.slf4j.Logger;

import java.util.List;

@SLF4J
public class RedirectCollector {
    private static Logger logger;

    public static void collect(BaseClassVisitor cv, SpringMapping mapping, List<ResultInfo> results) {
        if (cv.getPass("SERVLET") != null && cv.getPass("SERVLET")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("SERVLET REDIRECT");
            resultInfo.getChains().add(mapping.getController().getClassReference().getName() + "."
                    + mapping.getMethodReference().getName());
            results.add(resultInfo);
            System.out.println(resultInfo);
            logger.info("detect servlet redirect");
        }
        if (cv.getPass("SPRING") != null && cv.getPass("SPRING")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("SPRING REDIRECT");
            resultInfo.getChains().add(mapping.getController().getClassReference().getName() + "."
                    + mapping.getMethodReference().getName());
            results.add(resultInfo);
            System.out.println(resultInfo);
            logger.info("detect spring redirect");
        }
    }
}
