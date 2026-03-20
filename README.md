# 🏃‍♂️ KingSports 專業跑步裝備電商平台

KingSports 是一個以 Java (Spring Boot) + MySQL + Vue 3 構建的現代化運動品牌電商系統。

## 📋 專案狀態
- **後端 (API)**: 已完成核心業務邏輯與安全性配置。
- **前端 (Vue)**: 規劃中 (即將開始)。
- **資料庫**: 已完成結構設計並導入初始測試資料。

## 🛠️ 技術棧
- **後端**: Java 17, Spring Boot 3.x, Spring Security, JWT, JPA (Hibernate), Maven.
- **資料庫**: MySQL 8.0.
- **校驗與測試**: JUnit 5, MockMvc, Jakarta Validation.

## 🚀 已實作功能
1.  **會員系統**: 
    - 支援 JWT 身份驗證與 BCrypt 密碼加密。
    - 完善的註冊校驗（名稱、Email、密碼強度）。
2.  **商品模組**: 
    - 商品分類管理、全品項展示、分類篩選。
3.  **購物車系統**: 
    - 支援登入使用者即時存取購物清單。
4.  **訂單與結帳**: 
    - 完整結帳流程、庫存自動扣除、交易事務控制。

## 📂 資料庫結構 (Database Schema)
- `users`: 會員資料與角色控管。
- `categories`: 商品分類（如路跑、越野跑）。
- `products`: 商品詳情與庫存。
- `cart_items`: 使用者購物車暫存。
- `orders` & `order_items`: 訂單主表與明細紀錄。

## 📝 開發紀錄 (2026-03-20)
### 實作內容：
- 專案名稱全面更正為 `KingSports`。
- 建立 JWT 認證機制，包含 `JwtAuthenticationFilter` 與 `JwtUtils`。
- 實作電商核心 API：`Auth`, `Product`, `Category`, `Cart`, `Order`。
- 完成 12 項後端整合測試，驗證從註冊到結帳的完整流程。

### 遇到的錯誤與修正：
- **編碼問題**: 在 Windows Maven 環境下，中文字串導致編譯失敗。
  - *修正*: 將所有 Java 回應字串與校驗訊息統一改為英文，並於 `pom.xml` 強制指定 UTF-8 編碼。
- **資料清理順序**: 測試時因外鍵約束無法刪除商品。
  - *修正*: 調整 `setUp` 順序，先刪除依賴項（購物車、訂單）再刪除商品與分類。
- **遞迴序列化**: Jackson 在處理 Order 與 OrderItem 時發生遞迴。
  - *修正*: 對 `OrderItem.order` 欄位加入 `@JsonIgnore` 以阻斷循環。
