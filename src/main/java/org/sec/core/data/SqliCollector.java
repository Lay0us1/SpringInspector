package org.sec.core.data;

import org.sec.log.SLF4J;
import org.sec.model.ResultInfo;
import org.slf4j.Logger;

import java.util.List;

@SLF4J
public class SqliCollector {
    private static Logger logger;

    public static void collect(List<String> tempChain, List<ResultInfo> results) {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setRisk(ResultInfo.HIGH_RISK);
        resultInfo.setVulName("SQL Inject");
        resultInfo.getChains().addAll(tempChain);
        results.add(resultInfo);
        System.out.println(resultInfo);
    }
}
