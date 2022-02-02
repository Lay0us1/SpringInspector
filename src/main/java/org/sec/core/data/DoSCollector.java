package org.sec.core.data;

import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.log.SLF4J;
import org.sec.model.ResultInfo;
import org.slf4j.Logger;

import java.util.List;

@SLF4J
public class DoSCollector {
    private static Logger logger;

    public static void collect(BaseClassVisitor cv, List<String> tempChain, List<ResultInfo> results) {
        if (cv.getPass("RE-DOS") != null && cv.getPass("RE-DOS")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("RE DOS");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect re dos");
            System.out.println(resultInfo);
        }
        if (cv.getPass("ARRAY-DOS") != null && cv.getPass("ARRAY-DOS")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("ARRAY DOS");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect array dos");
            System.out.println(resultInfo);
        }
        if (cv.getPass("FOR-DOS") != null && cv.getPass("FOR-DOS")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("FOR DOS");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect for dos");
            System.out.println(resultInfo);
        }
        if (cv.getPass("LIST-DOS") != null && cv.getPass("LIST-DOS")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("LIST DOS");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect list dos");
            System.out.println(resultInfo);
        }
        if (cv.getPass("MAP-DOS") != null && cv.getPass("MAP-DOS")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("MAP DOS");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect map dos");
            System.out.println(resultInfo);
        }
    }
}
