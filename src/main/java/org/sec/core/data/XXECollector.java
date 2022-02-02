package org.sec.core.data;

import org.sec.core.asm.base.BaseClassVisitor;
import org.sec.log.SLF4J;
import org.sec.model.ResultInfo;
import org.slf4j.Logger;

import java.util.List;

@SLF4J
public class XXECollector {
    private static Logger logger;

    public static void collect(BaseClassVisitor cv, List<String> tempChain, List<ResultInfo> results) {
        if (cv.getPass("SAX-BUILDER") != null && cv.getPass("SAX-BUILDER")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("SAX BUILDER XXE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect sax builder xxe");
            System.out.println(resultInfo);
        }
        if (cv.getPass("SAX-PARSER") != null && cv.getPass("SAX-PARSER")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("SAX PARSER XXE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect sax parser xxe");
            System.out.println(resultInfo);
        }
        if (cv.getPass("SAX-TRANSFORMER-FACTORY") != null &&
                cv.getPass("SAX-TRANSFORMER-FACTORY")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("SAX TRANSFORMER FACTORY XXE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect sax transformer factory xxe");
            System.out.println(resultInfo);
        }
        if (cv.getPass("SCHEMA-FACTORY") != null &&
                cv.getPass("SCHEMA-FACTORY")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("SCHEMA FACTORY XXE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect schema factory xxe");
            System.out.println(resultInfo);
        }
        if (cv.getPass("TRANSFORMER-FACTORY") != null &&
                cv.getPass("TRANSFORMER-FACTORY")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("TRANSFORMER FACTORY XXE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect transformer factory xxe");
            System.out.println(resultInfo);
        }
        if (cv.getPass("VALIDATOR") != null &&
                cv.getPass("VALIDATOR")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("VALIDATOR XXE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect validator xxe");
            System.out.println(resultInfo);
        }
        if (cv.getPass("XML-READER") != null &&
                cv.getPass("XML-READER")) {
            ResultInfo resultInfo = new ResultInfo();
            resultInfo.setRisk(ResultInfo.MID_RISK);
            resultInfo.setVulName("XML READER XXE");
            resultInfo.getChains().addAll(tempChain);
            results.add(resultInfo);
            logger.info("detect xml reader xxe");
            System.out.println(resultInfo);
        }
    }
}