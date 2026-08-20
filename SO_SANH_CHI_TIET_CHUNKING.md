# BÁO CÁO PHÂN TÍCH VÀ SO SÁNH CHI TIẾT CHIẾN LƯỢC CHUNKING TỐI ƯU CHO CRM
## DỰ ÁN: RIKKEI CRM TICKET ASSISTANT (SESSION 08 - BÀI 2)

---

## 1. Bảng So Sánh Chi Tiết Ưu & Nhược Điểm Của Hai Chiến Lược Chunking

Hệ thống Rikkei CRM xử lý hai nhóm tài liệu cốt lõi:
* **Tài liệu Loại A (Quy trình hoàn tiền)**: Cấu trúc dạng danh sách các bước chặt chẽ (Bước 1, Bước 2...).
* **Tài liệu Loại B (Quy chế khách hàng thân thiết)**: Cấu trúc văn bản dài, phân cấp theo đề mục lớn (`# Chương I`, `## Điều 1`...).

| Tiêu Chí So Sánh | Chiến Lược 1: Token-based Chunking (`TokenTextSplitter`) | Chiến Lược 2: Header-based Chunking (`HierarchicalMarkdownSplitter`) |
| :--- | :--- | :--- |
| **Nguyên lý hoạt động** | Cắt văn bản theo độ dài Token cố định (ví dụ 400 - 600 tokens), sử dụng dấu phân tách khoảng trắng/từ và cửa sổ gối đầu (Overlap). | Cắt văn bản dựa trên cấu trúc đề mục phân cấp Markdown (`#`, `##`, `###`), mỗi section/điều khoản tạo thành 1 chunk. |
| **Ưu điểm chung** | - Kiểm soát chính xác kích thước input token cho Embedding Model.<br>- Dễ dàng cấu hình overlap tránh mất ngữ cảnh giáp ranh.<br>- Xử lý tốt mọi loại văn bản tự do, phi cấu trúc. | - Tôn trọng tuyệt đối tính toàn vẹn ngữ nghĩa của từng Điều khoản/Chương.<br>- Không bao giờ cắt đứt giữa câu hoặc giữa một điều luật.<br>- Tự động trích xuất breadcrumb phân cấp vào metadata. |
| **Nhược điểm chung** | - Không hiểu cấu trúc phân cấp tài liệu (Heading, Chapter).<br>- Có thể cắt ngang một điều khoản dài thành nhiều mẩu rời rạc.<br>- Tiêu đề cha có thể bị mất ở các chunk phía sau. | - Kích thước chunk không đồng đều (phụ thuộc vào độ dài từng điều khoản).<br>- Nếu một điều khoản quá dài có thể vượt context window của Embedding model.<br>- Không hiệu quả nếu tài liệu không có cấu trúc Markdown chuẩn. |
| **Hiệu năng trên TÀI LIỆU LOẠI A (Quy trình các bước tuần tự)** | **RẤT TỐI ƯU (Khuyên dùng)**<br>✓ Giữ được mối liên kết liên tục giữa các bước nhờ Overlap Window.<br>✓ Các bước (Bước 1, Bước 2, Bước 3) được nhóm trọn vẹn trong một khối xử lý.<br>✓ Tham số `minChunkSizeChars` ngăn chặn việc chia cắt một bước hành động đơn lẻ. | **KÉM HIỆU QUẢ**<br>✗ Do tài liệu Loại A thường chỉ có 1 Header chính và liệt kê danh sách 5 bước, Header splitter sẽ gom toàn bộ tài liệu thành 1 chunk duy nhất hoặc không phân tách được từng nhóm bước nhỏ. |
| **Hiệu năng trên TÀI LIỆU LOẠI B (Quy chế, văn bản pháp quy)** | **HẠN CHẾ**<br>✗ Dễ cắt ngang Điều 3 thành 2 chunk: Nửa đầu nằm ở Chunk 1, nửa sau nằm ở Chunk 2.<br>✗ Khi truy vấn chỉ tìm thấy nửa sau, LLM mất bối cảnh tên Điều luật và Chương áp dụng, dẫn đến trả lời sai. | **RẤT TỐI ƯU (Khuyên dùng)**<br>✓ Mỗi Điều khoản là một Chunk hoàn chỉnh.<br>✓ Metadata chứa đầy đủ: `Chương I: Quy Định Chung > Điều 1: Phạm vi...`.<br>✓ LLM luôn nắm trọn vẹn điều kiện và đặc quyền của từng hạng thẻ hội viên. |

---

## 2. Phân Tích Chuyên Sâu Cơ Chế Bảo Vệ Ngữ Cảnh (Context Preservation)

### 2.1. Vấn đề "Mất Ngữ Cảnh" (Context Loss) trong RAG
Trong hệ thống RAG, nếu quá trình Chunking thực hiện thô bạo (Naïve Chunking), các hiện tượng sau sẽ xảy ra:
1. **Gãy đứt quy trình (Step Fragmentation)**: Khách hàng hỏi *"Bước 3 quy trình hoàn tiền cần chuyển cho ai?"*, nếu Chunk 1 chỉ chứa Bước 1, Bước 2 và kết thúc ở dòng *"Trưởng bộ phận CSKH..."*, còn Chunk 2 bắt đầu bằng *"...ký duyệt trong 2 giờ"*, câu trả lời sẽ bị cụt nghĩa.
2. **Mất nguồn gốc quy định (Orphan Rule)**: Điều 4 quy định *"Tích lũy 5% hóa đơn"*, nhưng không có ngữ cảnh gắn với *"Hội viên Kim Cương thuộc Chương II"*, AI có thể nhầm lẫn áp dụng cho tất cả hội viên.

### 2.2. Các Trụ Cột Của Cơ Chế Bảo Vệ Ngữ Cảnh Trong Thiết Kế

#### A. Cửa Sổ Gối Đầu (Overlap Window / Overlap Chars)
* Khi chia nhỏ theo token, việc cấu hình `overlapChars = 80` (hoặc 10-15% token) tạo ra một vùng đệm giao thoa giữa Chunk $N$ và Chunk $N+1$.
* Điều này bảo đảm câu cuối cùng của Chunk $N$ được lặp lại ở đầu Chunk $N+1$, giúp Embedding Model và LLM duy trì dòng tư duy (flow of thought) liên tục giữa các bước thực hiện.

#### B. Kiểm Soát Độ Dài Tối Thiểu (`minChunkSizeChars = 120`)
* Cơ chế này kiểm tra sau khi phân tách: nếu một mẩu văn bản nhỏ hơn 120 ký tự (ví dụ: dòng ghi chú ngắn, gạch đầu dòng), bộ Splitter sẽ **ngăn chặn việc phát hành chunk rác**, tự động gộp (merge) mẩu này vào đoạn trước hoặc sau.
* **Lợi ích**:
  - Tránh ô nhiễm không gian vector (Vector Noise).
  - Đảm bảo mỗi vector nạp vào Supabase đại diện cho một khối thông tin hoàn chỉnh có đủ chủ-vị ngữ.

#### C. Kế Thừa Cây Phân Cấp (Header Breadcrumbs Metadata Inheritance)
* Đối với tài liệu Loại B, `HierarchicalMarkdownTextSplitter` tự động duy trì một ngăn xếp các tiêu đề đang hiệu lực (`H1 > H2 > H3`).
* Khi phát hành Chunk cho `## Điều 3: Hạng Bạc và Vàng`, hệ thống chèn thông tin:
  ```markdown
  <!-- BREADCRUMB: CHƯƠNG II: TIÊU CHUẨN XẾP HẠNG > Điều 3: Quyền lợi... -->
  ```
  đồng thời gắn vào metadata:
  ```json
  {
    "header_hierarchy": "CHƯƠNG II: TIÊU CHUẨN XẾP HẠNG > Điều 3: Quyền lợi...",
    "heading_level": 2,
    "heading_title": "Điều 3: Quyền lợi hội viên Hạng Bạc và Hạng Vàng"
  }
  ```
* Khi LLM nhận chunk này, nó nắm ngay bối cảnh điều khoản này thuộc chương nào mà không cần đọc toàn bộ văn bản gốc.

---

## 3. Tổng Kết Kiến Trúc Cấu Hình

```
                   Tài Liệu CRM Đầu Vào
                            │
            ┌───────────────┴───────────────┐
            │                               │
            ▼                               ▼
    Tài Liệu Loại A                 Tài Liệu Loại B
 (Quy trình hoàn tiền)           (Quy chế hội viên dài)
            │                               │
            ▼                               ▼
 [stepOrientedSplitter]          [hierarchicalMarkdownSplitter]
 (Token chunkSize=400,            (Header-based Markdown Splitter
  overlap=80, minChars=120)        + Breadcrumbs Metadata)
            │                               │
            └───────────────┬───────────────┘
                            │
                            ▼
               Tập Chunks Giàu Ngữ Cảnh
                            │
                            ▼
              Supabase pgvector (Top-K RAG)
```
