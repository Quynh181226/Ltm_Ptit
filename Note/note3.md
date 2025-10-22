---

##  1. `implements` là gì?

* Dùng để **kế thừa một interface** (giao diện).
* Interface chỉ chứa **khai báo phương thức (method)**, **không có code thực thi**.
* Khi một class **implements** interface, nó **phải viết code (override)** cho tất cả các phương thức trong interface đó.

---

##  2. `implements Serializable` là trường hợp đặc biệt

`Serializable` là **một interface đặc biệt trong Java**, nằm trong `java.io.Serializable`.

 **Điểm đặc biệt:**
Nó là **một “marker interface”** (interface đánh dấu) — **không có phương thức nào cả**.

```java
public interface Serializable { }
```

=> Không cần phải override gì hết.
Chỉ cần ghi `implements Serializable` là **đủ để Java biết rằng class này được phép “tuần tự hóa” (serialize)**.

 Ví dụ:

```java
public class Message implements Serializable {
    private String command;
    private Object data;
}
```

Điều này cho phép em **gửi đối tượng `Message` qua Socket** bằng `ObjectOutputStream` / `ObjectInputStream`.

---

## 3. Vậy “thay bằng cái gì” được?
Dưới đây là các **interface phổ biến** tương tự:

| Interface                     | Dùng khi                                  | Ghi chú                                            |
| ----------------------------- | ----------------------------------------- | -------------------------------------------------- |
| `Serializable`                | Gửi đối tượng qua mạng, lưu file nhị phân | Không có phương thức, chỉ để “đánh dấu”            |
| `Externalizable`              | Tự quy định cách serialize/deserialize    | Phải tự viết `writeExternal()` và `readExternal()` |
| `Cloneable`                   | Cho phép sao chép đối tượng (`clone()`)   | Cũng là marker interface                           |
| `Comparable<T>`               | Dùng cho `Collections.sort()`             | Phải override `compareTo()`                        |
| `Runnable`                    | Dùng để chạy luồng (`Thread`)             | Phải override `run()`                              |
| `Closeable` / `AutoCloseable` | Dùng trong try-with-resources             | Phải override `close()`                            |

---

## 💡 4. Cụ thể 

`Message` đang dùng trong Socket ⇒ **phải serialize được**
nên:

```java
public class Message implements Serializable { ... }
```
---

## 🔒 5. Tổng kết ngắn gọn

| Từ khóa                   | Khi nào dùng                   | Ghi nhớ                                   |
| ------------------------- | ------------------------------ | ----------------------------------------- |
| `extends`                 | Kế thừa từ **class cha**       | Dùng với class                            |
| `implements`              | Kế thừa từ **interface**       | Dùng với interface                        |
| `implements Serializable` | Cho phép **gửi/lưu đối tượng** | Bắt buộc khi gửi qua `ObjectOutputStream` |

---
