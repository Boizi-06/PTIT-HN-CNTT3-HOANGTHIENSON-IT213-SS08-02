package com.rikkei.crm.runner;

import com.rikkei.crm.dto.ChunkingEvaluationResult;
import com.rikkei.crm.service.ChunkingComparisonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CommandLineRunner tự động chứng minh:
 * 1. Đăng ký thành công các Bean TextSplitter trong Spring Context.
 * 2. Chạy thử nghiệm phân tách trên Tài liệu Loại A và Loại B.
 * 3. In bảng so sánh chi tiết và log thực tế ra Console.
 */
@Component
public class ChunkingStrategyRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ChunkingStrategyRunner.class);

    private final ChunkingComparisonService comparisonService;
    private final ResourceLoader resourceLoader;
    private final ApplicationContext applicationContext;

    public ChunkingStrategyRunner(ChunkingComparisonService comparisonService,
                                  ResourceLoader resourceLoader,
                                  ApplicationContext applicationContext) {
        this.comparisonService = comparisonService;
        this.resourceLoader = resourceLoader;
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) {
        log.info("=========================================================================================");
        log.info("      RIKKEI CRM TICKET ASSISTANT - BÀI 2: CHIẾN LƯỢC CHUNKING TỐI ƯU CHO CRM          ");
        log.info("=========================================================================================");

        // 1. Kiểm tra xác nhận các Bean đã được nạp thành công vào Spring Context
        log.info("[SPRING-CONTEXT-CHECK] Kiểm tra danh sách Bean TextSplitters được đăng ký:");
        String[] beanNames = applicationContext.getBeanNamesForType(org.springframework.ai.document.DocumentTransformer.class);
        for (String name : beanNames) {
            Object beanInstance = applicationContext.getBean(name);
            log.info(" -> Bean Name: '{:<30}' | Class: {}", name, beanInstance.getClass().getName());
        }

        try {
            // 2. Load các tài liệu mẫu
            Resource docTypeAResource = resourceLoader.getResource("classpath:docs/loai-a-quy-trinh-hoan-tien.md");
            Resource docTypeBResource = resourceLoader.getResource("classpath:docs/loai-b-quy-che-khach-hang-than-thiet.md");

            List<Document> docsA = comparisonService.readMarkdownResource(docTypeAResource);
            List<Document> docsB = comparisonService.readMarkdownResource(docTypeBResource);

            log.info("\n-----------------------------------------------------------------------------------------");
            log.info(" 1. THỬ NGHIỆM TRÊN TÀI LIỆU LOẠI A: QUY TRÌNH HOÀN TIỀN (DANH SÁCH BƯỚC 1 -> BƯỚC 5)  ");
            log.info("-----------------------------------------------------------------------------------------");

            ChunkingEvaluationResult evalA1 = comparisonService.evaluateStrategy(
                    comparisonService.getStepOrientedSplitter(),
                    "STEP_ORIENTED_TOKEN",
                    "LOẠI A (QUY TRÌNH BƯỚC)",
                    "loai-a-quy-trinh-hoan-tien.md",
                    docsA
            );

            ChunkingEvaluationResult evalA2 = comparisonService.evaluateStrategy(
                    comparisonService.getHierarchicalMarkdownSplitter(),
                    "HIERARCHICAL_HEADER",
                    "LOẠI A (QUY TRÌNH BƯỚC)",
                    "loai-a-quy-trinh-hoan-tien.md",
                    docsA
            );

            printEvaluation(evalA1);
            printEvaluation(evalA2);

            log.info("\n-----------------------------------------------------------------------------------------");
            log.info(" 2. THỬ NGHIỆM TRÊN TÀI LIỆU LOẠI B: QUY CHẾ HỘI VIÊN (PHÂN CẤP # CHƯƠNG > ## ĐIỀU)      ");
            log.info("-----------------------------------------------------------------------------------------");

            ChunkingEvaluationResult evalB1 = comparisonService.evaluateStrategy(
                    comparisonService.getHierarchicalMarkdownSplitter(),
                    "HIERARCHICAL_HEADER",
                    "LOẠI B (QUY CHẾ DÀI)",
                    "loai-b-quy-che-khach-hang-than-thiet.md",
                    docsB
            );

            ChunkingEvaluationResult evalB2 = comparisonService.evaluateStrategy(
                    comparisonService.getStepOrientedSplitter(),
                    "STEP_ORIENTED_TOKEN",
                    "LOẠI B (QUY CHẾ DÀI)",
                    "loai-b-quy-che-khach-hang-than-thiet.md",
                    docsB
            );

            printEvaluation(evalB1);
            printEvaluation(evalB2);

            log.info("\n=========================================================================================");
            log.info("                              KẾT LUẬN CHIẾN LƯỢC CHUNKING                             ");
            log.info("=========================================================================================");
            log.info("1. TÀI LIỆU LOẠI A -> BẮT BUỘC DÙNG 'stepOrientedSplitter' (Token-based + Overlap + minChars)");
            log.info("   Lý do: Giữ trọn vẹn từng bước hành động, tránh mất logic liên kết giữa Bước n và Bước n+1.");
            log.info("2. TÀI LIỆU LOẠI B -> BẮT BUỘC DÙNG 'hierarchicalMarkdownSplitter' (Header-based + Breadcrumbs)");
            log.info("   Lý do: Bảo toàn toàn bộ nội dung từng Điều/Khoản luật, kèm đường dẫn Chương cha trong metadata.");
            log.info("=========================================================================================\n");

        } catch (Exception e) {
            log.error("[RUNNER-ERROR] Lỗi trong quá trình chạy thử nghiệm Chunking: {}", e.getMessage(), e);
        }
    }

    private void printEvaluation(ChunkingEvaluationResult eval) {
        log.info("Chiến lược: [{:<22}] | File: {} | Số Chunks: {} | Kích thước TB: {:.1f} ký tự",
                eval.getStrategyName(), eval.getDocumentName(), eval.getTotalChunks(), eval.getAverageChunkSizeChars());
        log.info("   -> Đánh giá: {}", eval.getEvaluationNotes());
        for (String summary : eval.getChunkSummaries()) {
            log.info("      * {}", summary);
        }
        log.info("");
    }
}
