# QUY TRÌNH HOÀN TIỀN GIAO DỊCH KHÁCH HÀNG CRM (TÀI LIỆU LOẠI A)

Quy trình này quy định các bước xử lý lệnh hoàn tiền cho khách hàng khi giao dịch phát sinh lỗi hoặc sản phẩm được chấp thuận trả hàng. Toàn bộ nhân viên CSKH và Kế toán phải tuân thủ nghiêm ngặt 5 bước dưới đây:

- **Bước 1: Tiếp nhận và xác thực yêu cầu hoàn tiền**: Nhân viên CSKH kiểm tra mã Ticket trên hệ thống Rikkei CRM, đối chiếu hóa đơn điện tử gốc và lý do hoàn tiền đã được Trưởng ca duyệt. Đảm bảo số tiền đề nghị khớp với giá trị thanh toán thực tế của khách hàng.
- **Bước 2: Kiểm tra đối chiếu số tài khoản thụ hưởng**: Nhân viên liên hệ xác nhận lại số tài khoản ngân hàng, tên chủ tài khoản và chi nhánh với khách hàng. Cập nhật thông tin vào trường `beneficiary_bank_info` trên phiếu thanh toán CRM.
- **Bước 3: Chuyển duyệt lệnh thanh toán đến Kế toán trưởng**: Trưởng bộ phận CSKH ký số duyệt điện tử trên Ticket trong thời gian tối đa 2 giờ làm việc kể từ khi hoàn tất Bước 2. Hồ sơ tự động chuyển sang hàng đợi của Phòng Tài chính Kế toán.
- **Bước 4: Thực hiện lệnh chuyển khoản liên ngân hàng**: Chuyên viên Kế toán thực hiện giao dịch chuyển tiền qua hệ thống Vietcombank Enterprise, ghi rõ nội dung chuyển khoản theo cú pháp `[HOAN TIEN CRM] - Ticket ID - SĐT Khach Hang`. Lưu mã tham chiếu giao dịch FT vào hệ thống.
- **Bước 5: Gửi thông báo và đóng Ticket hỗ trợ**: Hệ thống CRM tự động gửi SMS Brandname và Email thông báo kèm mã giao dịch cho khách hàng. Nhân viên CSKH gọi điện xác nhận khách hàng đã nhận đủ tiền và chuyển trạng thái Ticket sang `RESOLVED_AND_CLOSED`.
