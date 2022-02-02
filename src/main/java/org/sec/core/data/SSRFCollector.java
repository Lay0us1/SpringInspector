package org.sec.core.data;

import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.log.SLF4J;
import org.sec.model.ResultInfo;
import org.slf4j.Logger;

import java.util.List;

@SLF4J
public class SSRFCollector {
    private static Logger logger;

    public static void collect(BaseClassVisitor cv, List<String> tempChain, List<ResultInfo> results) {
        if (cv.getPass("JDK") != null && cv.getPass("JDK")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("JDK SSRF");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect jdk ssrf");
            System.out.println(resultInfo);
        }
        if (cv.getPass("APACHE") != null && cv.getPass("APACHE")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("Apache SSRF");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect apache ssrf");
            System.out.println(resultInfo);
        }
        if (cv.getPass("SOCKET") != null && cv.getPass("SOCKET")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("Socket SSRF");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect socket ssrf");
            System.out.println(resultInfo);
        }
        if (cv.getPass("OKHTTP") != null && cv.getPass("OKHTTP")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("Okhttp SSRF");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect okhttp ssrf");
            System.out.println(resultInfo);
        }
    }
}
