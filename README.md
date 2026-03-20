# 🏃‍♂️ KingSports 專業跑步裝備電商平台

KingSports 是一個以 Java (Spring Boot) + MySQL + Vue 3 構建的現代化運動品牌電商系統。

## 📋 專案狀態
- **後端 (API)**: 核心業務邏輯已完成，並實作管理員權限控管。
- **前端 (Vue)**: 規劃中 (即將開始)。
- **資料庫**: 結構已同步，支援自動化時間戳記與預設角色。

## 🛠️ 技術棧
- **後端**: Java 17, Spring Boot 3.x, Spring Security, JWT, JPA (Hibernate), Maven.
- **資料庫**: MySQL 8.0.
- **工具**: Lombok, Dotenv-java, GitHub Actions CI/CD.

## 🚀 已實作功能
1.  **權限與認證**: 
    - 支援 JWT 身份驗證。
    - **RBAC 權限控管**: 區分 `USER` (消費者) 與 `ADMIN` (後台管理)。
    - 使用 `@PreAuthorize` 嚴格限制敏感 API。
2.  **商品模組**: 
    - 支援管理員新增商品、訪客瀏覽商品、分類篩選。
    - 商品與使用者均具備自動化的 `createdAt` 與 `updatedAt` 紀錄。
3.  **購物車與訂單**: 
    - 支援登入使用者即時存取購物清單。
    - 完整結帳流程與庫存自動扣除。

## 📝 開發紀錄 (2026-03-21)
### 實作內容：
- 實作管理員新增商品 API (`POST /api/products`)。
- 配置 `@EnableMethodSecurity` 並實作 `USER` / `ADMIN` 角色區分。
- 使用 `@Builder.Default` 與 JPA `@PrePersist` 解決 Lombok Builder 造成的欄位預設值遺失問題。
- 同步 MySQL 資料表結構，確保 `role` 與 `created_at` 具备正確的預設值。

### 遇到的錯誤與修正：
- **時間紀錄遺失**: 使用 Lombok Builder 時預設值失效。
  - *修正*: 加入 `@Builder.Default` 註解並配合 JPA 生命週期勾子。
- **管理員存取遭拒 (403)**: 資料庫角色變更後舊 Token 依然攜帶舊權限。
  - *修正*: 重新登入以刷新 JWT Claims，並於 `SecurityConfig` 明確區分 HttpMethod (GET 公開，POST 需驗證)。
- **業務邏輯報錯 (Category not found)**: 新增商品時傳入不存在的 ID。
  - *修正*: 強化 API 回應訊息，並確認資料庫分類 ID 的一致性。
