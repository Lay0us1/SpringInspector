package org.sec.core.data;

import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.log.SLF4J;
import org.sec.model.ResultInfo;
import org.slf4j.Logger;

import java.util.List;

@SLF4J
public class RCECollector {
    private static Logger logger;

    public static void collect(BaseClassVisitor cv, List<String> tempChain, List<ResultInfo> results) {
        if (cv.getPass("RUNTIME") != null && cv.getPass("RUNTIME")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("RUNTIME RCE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect runtime rce");
            System.out.println(resultInfo);
        }
        if (cv.getPass("PROCESS") != null && cv.getPass("PROCESS")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("PROCESS RCE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect process rce");
            System.out.println(resultInfo);
        }
        if (cv.getPass("GROOVY") != null && cv.getPass("GROOVY")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("GROOVY RCE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect groovy rce");
            System.out.println(resultInfo);
        }
    }
}
