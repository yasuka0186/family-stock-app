package com.familystock.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 食材・生活用品管理アプリのバックエンド起動クラス。
 * 今後の認証・在庫・買い物リスト機能を載せるための最小基盤を提供する。
 */
@SpringBootApplication
public class FamilyStockBackendApplication {

    /**
     * Spring Bootアプリケーションのエントリーポイント。
     * MVPフェーズ1ではバックエンド土台の起動確認を主目的とする。
     *
     * @param args 起動引数
     */
    public static void main(String[] args) {
        SpringApplication.run(FamilyStockBackendApplication.class, args);
    }
}
