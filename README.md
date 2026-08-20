# RIKKEI CRM TICKET ASSISTANT - BÀI 2
## CHIẾN LƯỢC CHUNKING TỐI ƯU CHO TÀI LIỆU CRM (TOKEN-BASED VS HEADER-BASED)

---

## 📌 Giới Thiệu
Dự án **Bài 2 - Session 08** hiện thực hóa hai chiến lược Chunking chuyên sâu cho hệ sinh thái **Rikkei CRM Ticket Assistant**:
1. **Chiến lược Token-based Chunking (`stepOrientedSplitter`)**: Dành riêng cho **Tài liệu Loại A** (Quy trình hoàn tiền tuần tự các bước 1 -> 5).
2. **Chiến lược Header-based Chunking (`hierarchicalMarkdownSplitter`)**: Dành riêng cho **Tài liệu Loại B** (Quy chế khách hàng thân thiết dài, phân cấp theo `# Chương` và `## Điều`).

---

## 📁 Cấu Trúc Dự Án

```
Bai 2/
├── pom.xml                                  # File cấu hình Maven, Spring Boot 3.3.3, Spring AI
├── README.md                                # Hướng dẫn tổng quan & cài đặt
├── SO_SANH_CHI_TIET_CHUNKING.md             # Bảng so sánh ưu nhược điểm & Phân tích Context Preservation
├── LOG_MINH_CHUNG.md                        # Minh chứng log Console kiểm thử Beans
├── .gitignore                               # Git ignore rules
└── src/
    ├── main/
    │   ├── java/com/rikkei/crm/
    │   │   ├── CrmChunkingApplication.java            # Main Class Spring Boot
    │   │   ├── config/
    │   │   │   └── ChunkingStrategyConfig.java        # Định nghĩa 2 Bean TextSplitter
    │   │   ├── splitter/
    │   │   │   ├── StepContextPreservingTextSplitter.java   # Splitter bảo toàn bước (Loại A)
    │   │   │   └── HierarchicalMarkdownTextSplitter.java    # Splitter phân cấp Heading (Loại B)
    │   │   ├── dto/
    │   │   │   └── ChunkingEvaluationResult.java      # DTO thống kê chunking
    │   │   ├── service/
    │   │   │   └── ChunkingComparisonService.java     # Service phân tích & đánh giá
    │   │   └── runner/
    │   │       └── ChunkingStrategyRunner.java        # CommandLineRunner kiểm thử tự động
    │   └── resources/
    │       ├── application.properties                 # File cấu hình properties
    │       └── docs/
    │           ├── loai-a-quy-trinh-hoan-tien.md
    │           └── loai-b-quy-che-khach-hang-than-thiet.md
    └── test/
        └── java/com/rikkei/crm/
            └── ChunkingStrategyConfigTest.java        # Unit Tests kiểm tra Bean Context
```

---

## 🚀 Hướng Dẫn Chạy Dự Án

### Bước 1: Biên dịch và chạy Unit Test
```bash
cd "c:\Users\Admin\Desktop\code\IT213\Session 08\Bai 2"
mvn clean test
```

### Bước 2: Khởi chạy Spring Boot Application
```bash
mvn spring-boot:run
```

---

## 📤 Hướng Dẫn Đẩy Lên GitHub

Mở Terminal tại thư mục `Bai 2`:
```bash
cd "c:\Users\Admin\Desktop\code\IT213\Session 08\Bai 2"

# Khởi tạo git repo riêng
git init
git add .
git commit -m "feat: Initial commit for CRM Chunking Strategies (Session 08 - Bai 2)"
git branch -M main
git remote add origin https://github.com/<your-username>/crm-chunking-strategies.git
git push -u origin main
```
