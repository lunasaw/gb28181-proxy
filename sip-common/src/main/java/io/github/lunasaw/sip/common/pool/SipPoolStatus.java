package io.github.lunasaw.sip.common.pool;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * SIP连接池状态信息
 * 用于监控和诊断连接池的运行状态
 *
 * @author luna
 * @date 2024/01/01
 */
@Data
public class SipPoolStatus {

    /**
     * 总连接数
     */
    private int             totalConnections;

    /**
     * 总连接池数
     */
    private int             totalPools;

    /**
     * 最大允许连接数
     */
    private int             maxConnections;

    /**
     * 连接池利用率（百分比）
     */
    private double          utilizationRate;

    /**
     * 各个连接池的详细信息
     */
    private List<PoolEntry> poolEntries = new ArrayList<>();

    /**
     * 添加连接池条目
     */
    public void addPoolEntry(PoolEntry entry) {
        poolEntries.add(entry);
        // 计算利用率
        if (maxConnections > 0) {
            utilizationRate = (double)totalConnections / maxConnections * 100;
        }
    }

    /**
     * 连接池条目信息
     */
    @Data
    public static class PoolEntry {
        /**
         * 连接池标识
         */
        private String poolKey;

        /**
         * 活跃连接数
         */
        private int    activeConnections;

        /**
         * 空闲连接数
         */
        private int    idleConnections;

        /**
         * 总借用次数
         */
        private int    totalBorrowed;

        /**
         * 连接池利用率
         */
        private double poolUtilization;

        /**
         * 计算连接池利用率
         */
        public void calculateUtilization() {
            int total = activeConnections + idleConnections;
            if (total > 0) {
                poolUtilization = (double)activeConnections / total * 100;
            }
        }
    }

    /**
     * 获取状态摘要
     */
    public String getSummary() {
        return String.format("SIP连接池状态 - 总连接: %d/%d (%.1f%%), 连接池数: %d",
            totalConnections, maxConnections, utilizationRate, totalPools);
    }

    /**
     * 获取详细状态报告
     */
    public String getDetailedReport() {
        StringBuilder report = new StringBuilder();
        report.append(getSummary()).append("\n");
        report.append("详细信息:\n");

        for (PoolEntry entry : poolEntries) {
            entry.calculateUtilization();
            report.append(String.format("  池[%s]: 活跃=%d, 空闲=%d, 借用=%d, 利用率=%.1f%%\n",
                entry.getPoolKey(), entry.getActiveConnections(), entry.getIdleConnections(),
                entry.getTotalBorrowed(), entry.getPoolUtilization()));
        }

        return report.toString();
    }
}