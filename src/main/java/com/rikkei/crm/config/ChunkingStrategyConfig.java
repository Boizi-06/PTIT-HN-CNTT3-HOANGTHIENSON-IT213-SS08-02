package com.rikkei.crm.config;

import com.rikkei.crm.splitter.HierarchicalMarkdownTextSplitter;
import com.rikkei.crm.splitter.StepContextPreservingTextSplitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration Class định nghĩa hai Bean TextSplitter chiến lược cho hệ thống CRM:
 * 1. stepOrientedSplitter: Tối ưu cho Tài liệu Loại A (Quy trình danh sách các bước chặt chẽ).
 * 2. hierarchicalMarkdownSplitter: Tối ưu cho Tài liệu Loại B (Quy chế văn bản dài phân cấp theo Heading).
 */
@Configuration
public class ChunkingStrategyConfig {

    private static final Logger log = LoggerFactory.getLogger(ChunkingStrategyConfig.class);

    // Thuộc tính cấu hình từ application.properties cho Chiến lược Loại A
    @Value("${crm.chunking.step-oriented.chunk-size:400}")
    private int stepChunkSize;

    @Value("${crm.chunking.step-oriented.min-chunk-size-chars:120}")
    private int stepMinChunkSizeChars;

    @Value("${crm.chunking.step-oriented.min-chunk-length-to-embed:5}")
    private int stepMinChunkLengthToEmbed;

    @Value("${crm.chunking.step-oriented.max-num-chunks:10000}")
    private int stepMaxNumChunks;

    @Value("${crm.chunking.step-oriented.keep-separator:true}")
    private boolean stepKeepSeparator;

    @Value("${crm.chunking.step-oriented.overlap-chars:80}")
    private int stepOverlapChars;

    // Thuộc tính cấu hình cho Chiến lược Loại B
    @Value("${crm.chunking.hierarchical.max-tokens:800}")
    private int hierarchicalMaxTokens;

    @Value("${crm.chunking.hierarchical.min-section-chars:100}")
    private int hierarchicalMinSectionChars;

    @Value("${crm.chunking.hierarchical.preserve-parent-header:true}")
    private boolean hierarchicalPreserveParentHeader;

    /**
     * BEAN 1: Chiến lược Token-based Chunking bảo toàn bước thực hiện cho Tài liệu Loại A (Quy trình hoàn tiền)
     *
     * Cấu hình tham số:
     * - chunkSize = 400: Kích thước vừa vặn bao trọn từ 2-3 bước liên tiếp, không làm phân mảnh logic.
     * - minChunkSizeChars = 120: Ngăn chặn cắt vụn một bước thành các mẩu không có nghĩa.
     * - overlapChars = 80: Đảm bảo bước giáp ranh giữa 2 chunk có ngữ cảnh nối tiếp của bước trước.
     */
    @Bean("stepOrientedSplitter")
    @Primary
    public DocumentTransformer stepOrientedSplitter() {
        log.info("[BEAN-REGISTER] Khởi tạo Bean 'stepOrientedSplitter' (Loại A): chunkSize={}, minChunkChars={}, overlapChars={}",
                stepChunkSize, stepMinChunkSizeChars, stepOverlapChars);

        return new StepContextPreservingTextSplitter(
                stepChunkSize,
                stepMinChunkSizeChars,
                stepMinChunkLengthToEmbed,
                stepMaxNumChunks,
                stepKeepSeparator,
                stepOverlapChars
        );
    }

    /**
     * BEAN 2: Chiến lược Header-based Chunking phân cấp cho Tài liệu Loại B (Quy chế khách hàng)
     *
     * Cấu hình tham số:
     * - maxTokens = 800: Giới hạn token cho một Điều khoản lớn.
     * - minSectionChars = 100: Tự động gộp các tiểu mục nhỏ vào điều khoản chính.
     * - preserveParentHeader = true: Giữ nguyên Breadcrumb phân cấp (# Chương > ## Điều).
     */
    @Bean("hierarchicalMarkdownSplitter")
    public DocumentTransformer hierarchicalMarkdownSplitter() {
        log.info("[BEAN-REGISTER] Khởi tạo Bean 'hierarchicalMarkdownSplitter' (Loại B): maxTokens={}, minSectionChars={}, preserveParentHeader={}",
                hierarchicalMaxTokens, hierarchicalMinSectionChars, hierarchicalPreserveParentHeader);

        return new HierarchicalMarkdownTextSplitter(
                hierarchicalMaxTokens,
                hierarchicalMinSectionChars,
                hierarchicalPreserveParentHeader
        );
    }

    /**
     * Bean phụ trợ: Chuẩn Spring AI TokenTextSplitter thuần túy phục vụ đối chiếu so sánh
     */
    @Bean("standardTokenSplitter")
    public TokenTextSplitter standardTokenSplitter() {
        return new TokenTextSplitter(stepChunkSize, stepMinChunkSizeChars, stepMinChunkLengthToEmbed, stepMaxNumChunks, stepKeepSeparator);
    }
}
