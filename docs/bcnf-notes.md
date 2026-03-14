# BCNF schema notes

Schema này bám theo sơ đồ người dùng gửi, nhưng chỉnh lại để phù hợp BCNF:

- `categories`: mọi thuộc tính phụ thuộc vào khóa `id`; `category_name` là khóa thay thế duy nhất.
- `accounts`: `id`, `username`, `email`, `phone` đều là khóa ứng viên hoặc siêu khóa nhờ ràng buộc `UNIQUE`.
- `products`: thông tin danh mục được tách ra bảng riêng và liên kết qua `category_id`.
- `carts`: một tài khoản có tối đa một giỏ hàng đang hoạt động qua `UNIQUE (user_id)`.
- `cart_items`: ngăn trùng sản phẩm trong cùng một giỏ qua `UNIQUE (cart_id, product_id)`.
- `orders`: giữ thông tin giao hàng dạng snapshot theo đơn; không lưu `total_amount` để tránh dư thừa.
- `order_items`: lưu `unit_price` tại thời điểm bán, vì giá lịch sử là thuộc tính của dòng đơn hàng, không còn phụ thuộc vào `products.price`.

Nếu cần hiển thị tổng tiền đơn hàng, hãy tính bằng:

`SUM(order_items.quantity * order_items.unit_price)`