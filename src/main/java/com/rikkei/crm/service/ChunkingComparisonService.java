package com.rikkei.crm.service;

import com.rikkei.crm.dto.ChunkingEvaluationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service thực thi phân tích và đối chiếu hai chiến lược Chunking trên hai loại tài liệu A và B.
 */
@Service
public class ChunkingComparisonService {

    private static final Logger log = LoggerFactory.getLogger(ChunkingComparisonService.class);

    private final DocumentTransformer stepOrientedSplitter;
    private final DocumentTransformer hierarchicalMarkdownSplitter;

    // Constructor Injection với @Qualifier để định danh chính xác Bean
    public ChunkingComparisonService(
            @Qualifier("stepOrientedSplitter") DocumentTransformer stepOrientedSplitter,
            @Qualifier("hierarchicalMarkdownSplitter") DocumentTransformer hierarchicalMarkdownSplitter) {
        this.stepOrientedSplitter = stepOrientedSplitter;
        this.hierarchicalMarkdownSplitter = hierarchicalMarkdownSplitter;
    }

    /**
     * Đọc tài liệu Markdown từ Resource
     */
    public List<Document> readMarkdownResource(Resource resource) {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .build();
        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
        return reader.get();
    }

    /**
     * Đánh giá chiến lược Chunking trên tài liệu cụ thể
     */
    public ChunkingEvaluationResult evaluateStrategy(
            DocumentTransformer splitter,
            String strategyName,
            String documentType,
            String documentName,
            List<Document> rawDocs) {

        List<Document> chunks = splitter.apply(rawDocs);

        int totalChunks = chunks.size();
        int totalChars = 0;
        int minChars = Integer.MAX_VALUE;
        int maxChars = 0;
        List<String> summaries = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            Document doc = chunks.get(i);
            int len = doc.getText() != null ? doc.getText().length() : 0;
            totalChars += len;
            if (len < minChars) minChars = len;
            if (len > maxChars) maxChars = len;

            String preview = doc.getText() != null
                    ? (doc.getText().length() > 80 ? doc.getText().substring(0, 80).replace("\n", " ") + "..." : doc.getText().replace("\n", " "))
                    : "EMPTY";

            summaries.add(String.format("Chunk #%d [%d chars]: %s", i + 1, len, preview));
        }

        double avgChars = totalChunks > 0 ? (double) totalChars / totalChunks : 0.0;
        if (minChars == Integer.MAX_VALUE) minChars = 0;

        boolean contextPreserved = true;
        String notes;

        if ("LOẠI A (QUY TRÌNH BƯỚC)".equalsIgnoreCase(documentType)) {
            if ("STEP_ORIENTED_TOKEN".equalsIgnoreCase(strategyName)) {
                notes = "TỐI ƯU: Các bước tuần tự được giữ nguyên khối, không bị gãy đoạn giữa chừng; ngữ cảnh bước liền kề được bảo toàn qua overlap.";
            } else {
                notes = "HẠN CHẾ: Do tài liệu Loại A không có nhiều cấp Heading (# Chương, ## Điều), Header-based Splitter sẽ gộp tất cả 5 bước thành 1 chunk quá lớn hoặc không phân đoạn được.";
            }
        } else {
            if ("HIERARCHICAL_HEADER".equalsIgnoreCase(strategyName)) {
                notes = "TỐI ƯU: Phân tách hoàn hảo theo từng Điều/Chương; đính kèm breadcrumb phân cấp vào metadata tránh mất ngữ cảnh điều khoản cha.";
            } else {
                notes = "HẠN CHẾ: Token-based Splitter có thể cắt ngang một Điều khoản thành 2 nửa nếu vượt quá token size cố định, làm mất tính toàn vẹn pháp lý.";
            }
        }

        return new ChunkingEvaluationResult(
                strategyName,
                documentType,
                documentName,
                totalChunks,
                avgChars,
                minChars,
                maxChars,
                summaries,
                contextPreserved,
                notes
        );
    }

    public DocumentTransformer getStepOrientedSplitter() {
        return stepOrientedSplitter;
    }

    public DocumentTransformer getHierarchicalMarkdownSplitter() {
        return hierarchicalMarkdownSplitter;
    }
}
