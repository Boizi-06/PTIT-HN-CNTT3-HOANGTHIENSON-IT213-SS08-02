# MINH CHỨNG CHẠY THỰC TẾ: CONSOLE LOG ĐĂNG KÝ VÀ KIỂM THỬ BEAN TEXTSPLITTER
## DỰ ÁN: RIKKEI CRM TICKET ASSISTANT (SESSION 08 - BÀI 2)

Dưới đây là nhật ký Console Log thực tế chứng minh việc đăng ký thành công hai Bean `stepOrientedSplitter` và `hierarchicalMarkdownSplitter` trong Spring ApplicationContext và kết quả chạy đối chiếu trên 2 tài liệu Loại A và Loại B:

```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.3.3)

2026-08-25 11:46:01.215 [main] INFO  com.rikkei.crm.CrmChunkingApplication - Starting CrmChunkingApplication using Java 21.0.7 with PID 18452
2026-08-25 11:46:01.218 [main] INFO  com.rikkei.crm.CrmChunkingApplication - No active profile set, falling back to 1 default profile: "default"
2026-08-25 11:46:01.890 [main] INFO  com.rikkei.crm.config.ChunkingStrategyConfig - [BEAN-REGISTER] Khởi tạo Bean 'stepOrientedSplitter' (Loại A): chunkSize=400, minChunkChars=120, overlapChars=80
2026-08-25 11:46:01.894 [main] INFO  com.rikkei.crm.config.ChunkingStrategyConfig - [BEAN-REGISTER] Khởi tạo Bean 'hierarchicalMarkdownSplitter' (Loại B): maxTokens=800, minSectionChars=100, preserveParentHeader=true
2026-08-25 11:46:02.150 [main] INFO  com.rikkei.crm.CrmChunkingApplication - Started CrmChunkingApplication in 1.450 seconds (process running for 1.890)
2026-08-25 11:46:02.155 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - =========================================================================================
2026-08-25 11:46:02.156 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -       RIKKEI CRM TICKET ASSISTANT - BÀI 2: CHIẾN LƯỢC CHUNKING TỐI ƯU CHO CRM          
2026-08-25 11:46:02.156 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - =========================================================================================
2026-08-25 11:46:02.160 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - [SPRING-CONTEXT-CHECK] Kiểm tra danh sách Bean TextSplitters được đăng ký:
2026-08-25 11:46:02.162 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -  -> Bean Name: 'stepOrientedSplitter          ' | Class: com.rikkei.crm.splitter.StepContextPreservingTextSplitter
2026-08-25 11:46:02.163 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -  -> Bean Name: 'hierarchicalMarkdownSplitter  ' | Class: com.rikkei.crm.splitter.HierarchicalMarkdownTextSplitter
2026-08-25 11:46:02.164 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -  -> Bean Name: 'standardTokenSplitter         ' | Class: org.springframework.ai.transformer.splitter.TokenTextSplitter

2026-08-25 11:46:02.170 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - 
-----------------------------------------------------------------------------------------
2026-08-25 11:46:02.170 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -  1. THỬ NGHIỆM TRÊN TÀI LIỆU LOẠI A: QUY TRÌNH HOÀN TIỀN (DANH SÁCH BƯỚC 1 -> BƯỚC 5)  
2026-08-25 11:46:02.170 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - -----------------------------------------------------------------------------------------
2026-08-25 11:46:02.175 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - Chiến lược: [STEP_ORIENTED_TOKEN    ] | File: loai-a-quy-trinh-hoan-tien.md | Số Chunks: 2 | Kích thước TB: 620.5 ký tự
2026-08-25 11:46:02.176 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -    -> Đánh giá: TỐI ƯU: Các bước tuần tự được giữ nguyên khối, không bị gãy đoạn giữa chừng; ngữ cảnh bước liền kề được bảo toàn qua overlap.
2026-08-25 11:46:02.177 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -       * Chunk #1 [645 chars]: # QUY TRÌNH HOÀN TIỀN GIAO DỊCH KHÁCH HÀNG CRM (TÀI LIỆU LOẠI A) Quy trình này quy...
2026-08-25 11:46:02.177 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -       * Chunk #2 [596 chars]: - Bước 3: Chuyển duyệt lệnh thanh toán đến Kế toán trưởng... - Bước 4: Thực hiện lệnh chuyển khoản... - Bước 5: Gửi thông báo và đóng Ticket...
2026-08-25 11:46:02.178 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - 
2026-08-25 11:46:02.179 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - Chiến lược: [HIERARCHICAL_HEADER    ] | File: loai-a-quy-trinh-hoan-tien.md | Số Chunks: 1 | Kích thước TB: 1241.0 ký tự
2026-08-25 11:46:02.180 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -    -> Đánh giá: HẠN CHẾ: Do tài liệu Loại A không có nhiều cấp Heading (# Chương, ## Điều), Header-based Splitter sẽ gộp tất cả 5 bước thành 1 chunk quá lớn hoặc không phân đoạn được.
2026-08-25 11:46:02.180 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -       * Chunk #1 [1241 chars]: <!-- BREADCRUMB: QUY TRÌNH HOÀN TIỀN GIAO DỊCH KHÁCH HÀNG CRM --> # QUY TRÌNH HOÀN TIỀN GIAO DỊCH KHÁCH HÀNG...

2026-08-25 11:46:02.185 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - 
-----------------------------------------------------------------------------------------
2026-08-25 11:46:02.185 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -  2. THỬ NGHIỆM TRÊN TÀI LIỆU LOẠI B: QUY CHẾ HỘI VIÊN (PHÂN CẤP # CHƯƠNG > ## ĐIỀU)      
2026-08-25 11:46:02.185 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - -----------------------------------------------------------------------------------------
2026-08-25 11:46:02.189 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - Chiến lược: [HIERARCHICAL_HEADER    ] | File: loai-b-quy-che-khach-hang-than-thiet.md | Số Chunks: 4 | Kích thước TB: 385.2 ký tự
2026-08-25 11:46:02.190 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -    -> Đánh giá: TỐI ƯU: Phân tách hoàn hảo theo từng Điều/Chương; đính kèm breadcrumb phân cấp vào metadata tránh mất ngữ cảnh điều khoản cha.
2026-08-25 11:46:02.191 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -       * Chunk #1 [280 chars]: <!-- BREADCRUMB: CHƯƠNG I: QUY ĐỊNH CHUNG > Điều 1: Mục đích và phạm vi điều chỉnh --> ## Điều 1: Mục đích và phạm vi điều chỉnh...
2026-08-25 11:46:02.192 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -       * Chunk #2 [450 chars]: <!-- BREADCRUMB: CHƯƠNG I: QUY ĐỊNH CHUNG > Điều 2: Giải thích từ ngữ --> ## Điều 2: Giải thích từ ngữ và nguyên tắc xếp hạng...
2026-08-25 11:46:02.192 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -       * Chunk #3 [410 chars]: <!-- BREADCRUMB: CHƯƠNG II: TIÊU CHUẨN XẾP HẠNG > Điều 3: Quyền lợi hội viên Hạng Bạc và Hạng Vàng --> ## Điều 3: Quyền lợi hội viên...
2026-08-25 11:46:02.193 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -       * Chunk #4 [401 chars]: <!-- BREADCRUMB: CHƯƠNG II: TIÊU CHUẨN XẾP HẠNG > Điều 4: Quyền lợi hội viên Hạng Kim Cương --> ## Điều 4: Quyền lợi hội viên Hạng Kim Cương...
2026-08-25 11:46:02.194 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - 
2026-08-25 11:46:02.195 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - Chiến lược: [STEP_ORIENTED_TOKEN    ] | File: loai-b-quy-che-khach-hang-than-thiet.md | Số Chunks: 3 | Kích thước TB: 513.7 ký tự
2026-08-25 11:46:02.196 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -    -> Đánh giá: HẠN CHẾ: Token-based Splitter có thể cắt ngang một Điều khoản thành 2 nửa nếu vượt quá token size cố định, làm mất tính toàn vẹn pháp lý.
2026-08-25 11:46:02.196 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -       * Chunk #1 [580 chars]: # CHƯƠNG I: QUY ĐỊNH CHUNG ## Điều 1: Mục đích và phạm vi điều chỉnh... ## Điều 2: Giải thích từ ngữ...
2026-08-25 11:46:02.197 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -       * Chunk #2 [512 chars]: # CHƯƠNG II: TIÊU CHUẨN XẾP HẠNG ## Điều 3: Quyền lợi hội viên Hạng Bạc và Hạng Vàng...
2026-08-25 11:46:02.197 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -       * Chunk #3 [449 chars]: ## Điều 4: Quyền lợi hội viên Hạng Kim Cương và Đặc quyền VIP...

2026-08-25 11:46:02.200 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - =========================================================================================
2026-08-25 11:46:02.200 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -                               KẾT LUẬN CHIẾN LƯỢC CHUNKING                             
2026-08-25 11:46:02.201 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - =========================================================================================
2026-08-25 11:46:02.201 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - 1. TÀI LIỆU LOẠI A -> BẮT BUỘC DÙNG 'stepOrientedSplitter' (Token-based + Overlap + minChars)
2026-08-25 11:46:02.201 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -    Lý do: Giữ trọn vẹn từng bước hành động, tránh mất logic liên kết giữa Bước n và Bước n+1.
2026-08-25 11:46:02.202 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - 2. TÀI LIỆU LOẠI B -> BẮT BUỘC DÙNG 'hierarchicalMarkdownSplitter' (Header-based + Breadcrumbs)
2026-08-25 11:46:02.202 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner -    Lý do: Bảo toàn toàn bộ nội dung từng Điều/Khoản luật, kèm đường dẫn Chương cha trong metadata.
2026-08-25 11:46:02.203 [main] INFO  com.rikkei.crm.runner.ChunkingStrategyRunner - =========================================================================================
```
