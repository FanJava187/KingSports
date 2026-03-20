package com.example.kingsports;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KingsportsApplication {

	public static void main(String[] args) {
		// 自動載入 .env 檔案中的環境變數
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing() // 如果檔案不存在（例如在 CI 環境）則忽略，避免噴錯
				.load();
		
		// 將 .env 的內容注入系統屬性，供 Spring Boot 使用
		dotenv.entries().forEach(entry -> 
			System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(KingsportsApplication.class, args);
	}

}
