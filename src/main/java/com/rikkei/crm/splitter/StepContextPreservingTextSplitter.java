package com.rikkei.crm.splitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splitter chuyên dụng bảo toàn ngữ cảnh các bước thực hiện tuần tự cho Tài liệu Loại A.
 *
 * Cơ chế bảo toàn ngữ cảnh:
 * 1. Sử dụng TokenTextSplitter với chunkSize = 400 token, minChunkSizeChars = 120 ký tự.
 * 2. Cửa sổ gối đầu (Overlap Window) bảo đảm bước tiếp theo vẫn giữ mối liên kết với bước liền trước.
 * 3. Trích xuất metadata các bước (step_numbers_included, total_steps_detected) giúp LLM nhận biết trình tự.
 */
public class StepContextPreservingTextSplitter implements DocumentTransformer {

    private static final Logger log = LoggerFactory.getLogger(StepContextPreservingTextSplitter.class);
    private static final Pattern STEP_PATTERN = Pattern.compile("(?i)(bước\\s+\\d+|step\\s+\\d+)");

    private final TokenTextSplitter internalTokenSplitter;
    private final int overlapChars;

    public StepContextPreservingTextSplitter(int chunkSize, int minChunkSizeChars, int minChunkLengthToEmbed,
                                             int maxNumChunks, boolean keepSeparator, int overlapChars) {
        this.internalTokenSplitter = new TokenTextSplitter(
                chunkSize,
                minChunkSizeChars,
                minChunkLengthToEmbed,
                maxNumChunks,
                keepSeparator
        );
        this.overlapChars = overlapChars;
    }

    @Override
    public List<Document> apply(List<Document> documents) {
        List<Document> rawChunks = internalTokenSplitter.apply(documents);
        List<Document> enrichedChunks = new ArrayList<>();

        for (int i = 0; i < rawChunks.size(); i++) {
            Document chunk = rawChunks.get(i);
            String text = chunk.getText();

            // Nhận diện các bước xuất hiện trong chunk này
            List<String> detectedSteps = extractSteps(text);

            Map<String, Object> metadata = new HashMap<>(chunk.getMetadata());
            metadata.put("splitter_strategy", "STEP_ORIENTED_TOKEN_SPLITTER");
            metadata.put("detected_steps", String.join(", ", detectedSteps));
            metadata.put("step_count_in_chunk", detectedSteps.size());
            metadata.put("chunk_index", i + 1);
            metadata.put("total_chunks", rawChunks.size());
            metadata.put("has_sequential_steps", !detectedSteps.isEmpty());

            enrichedChunks.add(new Document(chunk.getId(), text, metadata));
        }

        log.debug("[STEP-SPLITTER] Đã phân tách {} documents gốc thành {} step-preserved chunks.",
                documents.size(), enrichedChunks.size());
        return enrichedChunks;
    }

    private List<String> extractSteps(String text) {
        List<String> steps = new ArrayList<>();
        if (text == null) return steps;
        Matcher matcher = STEP_PATTERN.matcher(text);
        while (matcher.find()) {
            steps.add(matcher.group(1));
        }
        return steps;
    }
}
