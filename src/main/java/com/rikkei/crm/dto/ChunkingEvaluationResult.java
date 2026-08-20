package com.rikkei.crm.dto;

import java.util.List;

/**
 * DTO chứa kết quả đánh giá và thống kê của một chiến lược Chunking
 */
public class ChunkingEvaluationResult {

    private String strategyName;
    private String documentType;
    private String documentName;
    private int totalChunks;
    private double averageChunkSizeChars;
    private int minChunkSizeChars;
    private int maxChunkSizeChars;
    private List<String> chunkSummaries;
    private boolean contextPreserved;
    private String evaluationNotes;

    public ChunkingEvaluationResult() {}

    public ChunkingEvaluationResult(String strategyName, String documentType, String documentName,
                                    int totalChunks, double averageChunkSizeChars, int minChunkSizeChars,
                                    int maxChunkSizeChars, List<String> chunkSummaries,
                                    boolean contextPreserved, String evaluationNotes) {
        this.strategyName = strategyName;
        this.documentType = documentType;
        this.documentName = documentName;
        this.totalChunks = totalChunks;
        this.averageChunkSizeChars = averageChunkSizeChars;
        this.minChunkSizeChars = minChunkSizeChars;
        this.maxChunkSizeChars = maxChunkSizeChars;
        this.chunkSummaries = chunkSummaries;
        this.contextPreserved = contextPreserved;
        this.evaluationNotes = evaluationNotes;
    }

    // Getters and Setters
    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    public double getAverageChunkSizeChars() {
        return averageChunkSizeChars;
    }

    public void setAverageChunkSizeChars(double averageChunkSizeChars) {
        this.averageChunkSizeChars = averageChunkSizeChars;
    }

    public int getMinChunkSizeChars() {
        return minChunkSizeChars;
    }

    public void setMinChunkSizeChars(int minChunkSizeChars) {
        this.minChunkSizeChars = minChunkSizeChars;
    }

    public int getMaxChunkSizeChars() {
        return maxChunkSizeChars;
    }

    public void setMaxChunkSizeChars(int maxChunkSizeChars) {
        this.maxChunkSizeChars = maxChunkSizeChars;
    }

    public List<String> getChunkSummaries() {
        return chunkSummaries;
    }

    public void setChunkSummaries(List<String> chunkSummaries) {
        this.chunkSummaries = chunkSummaries;
    }

    public boolean isContextPreserved() {
        return contextPreserved;
    }

    public void setContextPreserved(boolean contextPreserved) {
        this.contextPreserved = contextPreserved;
    }

    public String getEvaluationNotes() {
        return evaluationNotes;
    }

    public void setEvaluationNotes(String evaluationNotes) {
        this.evaluationNotes = evaluationNotes;
    }
}
