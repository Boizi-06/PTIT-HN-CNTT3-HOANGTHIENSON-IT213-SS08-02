package com.rikkei.crm;

import com.rikkei.crm.config.ChunkingStrategyConfig;
import com.rikkei.crm.splitter.HierarchicalMarkdownTextSplitter;
import com.rikkei.crm.splitter.StepContextPreservingTextSplitter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {CrmChunkingApplication.class, ChunkingStrategyConfig.class})
class ChunkingStrategyConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("stepOrientedSplitter")
    private DocumentTransformer stepOrientedSplitter;

    @Autowired
    @Qualifier("hierarchicalMarkdownSplitter")
    private DocumentTransformer hierarchicalMarkdownSplitter;

    @Test
    @DisplayName("Kiểm tra đăng ký thành công cả 2 Bean TextSplitter trong Spring ApplicationContext")
    void testBeansRegisteredInContext() {
        assertNotNull(stepOrientedSplitter, "Bean 'stepOrientedSplitter' phải tồn tại trong Spring Context");
        assertNotNull(hierarchicalMarkdownSplitter, "Bean 'hierarchicalMarkdownSplitter' phải tồn tại trong Spring Context");

        assertTrue(stepOrientedSplitter instanceof StepContextPreservingTextSplitter,
                "stepOrientedSplitter phải là instance của StepContextPreservingTextSplitter");
        assertTrue(hierarchicalMarkdownSplitter instanceof HierarchicalMarkdownTextSplitter,
                "hierarchicalMarkdownSplitter phải là instance của HierarchicalMarkdownTextSplitter");
    }

    @Test
    @DisplayName("Kiểm tra StepOrientedSplitter giữ nguyên vẹn các bước và bổ sung metadata")
    void testStepOrientedSplitter_WithSteps() {
        String content = """
                # QUY TRÌNH
                - Bước 1: Tiếp nhận yêu cầu của khách hàng qua tổng đài CRM.
                - Bước 2: Kiểm tra đối chiếu số tài khoản ngân hàng và xác minh danh tính.
                - Bước 3: Phê duyệt hoàn tiền trên hệ thống và chuyển khoản.
                """;

        Document inputDoc = new Document(content);
        List<Document> chunks = stepOrientedSplitter.apply(List.of(inputDoc));

        assertNotNull(chunks);
        assertFalse(chunks.isEmpty());

        for (Document chunk : chunks) {
            assertTrue(chunk.getText().length() >= 50);
            assertNotNull(chunk.getMetadata().get("detected_steps"));
            assertEquals("STEP_ORIENTED_TOKEN_SPLITTER", chunk.getMetadata().get("splitter_strategy"));
        }
    }

    @Test
    @DisplayName("Kiểm tra HierarchicalMarkdownSplitter trích xuất breadcrumb phân cấp")
    void testHierarchicalMarkdownSplitter_WithHierarchy() {
        String content = """
                # CHƯƠNG I: QUY ĐỊNH CHUNG
                
                ## Điều 1: Phạm vi áp dụng
                Toàn bộ hội viên Rikkei Loyalty Club được áp dụng chính sách này.
                
                ## Điều 2: Quyền lợi hội viên
                Hội viên Hạng Bạc được tích lũy 2% giá trị hóa đơn.
                """;

        Document inputDoc = new Document(content);
        List<Document> chunks = hierarchicalMarkdownSplitter.apply(List.of(inputDoc));

        assertNotNull(chunks);
        assertEquals(2, chunks.size(), "Phải tách thành 2 chunks tương ứng với Điều 1 và Điều 2");

        Document chunk1 = chunks.get(0);
        assertTrue(chunk1.getText().contains("Điều 1: Phạm vi áp dụng"));
        assertEquals("HEADER_BASED_HIERARCHICAL", chunk1.getMetadata().get("splitter_strategy"));
        assertTrue(chunk1.getMetadata().get("header_hierarchy").toString().contains("CHƯƠNG I"));
    }
}
