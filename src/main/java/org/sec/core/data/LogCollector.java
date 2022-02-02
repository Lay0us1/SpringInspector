package org.sec.core.data;

import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.log.SLF4J;
import org.sec.model.ResultInfo;
import org.slf4j.Logger;

import java.util.List;

@SLF4J
public class LogCollector {
    private static Logger logger;

    public static void collect(BaseClassVisitor cv, List<String> tempChain, List<ResultInfo> results) {
        if (cv.getPass("SLF4J") != null && cv.getPass("SLF4J")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("SLF4J LOG INJECT");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect slf4j log inject");
            System.out.println(resultInfo);
        }
        if (cv.getPass("LOG4J") != null && cv.getPass("LOG4J")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("LOG4J LOG INJECT");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect log4j log inject");
            System.out.println(resultInfo);
        }
    }
}
