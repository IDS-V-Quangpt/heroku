package jp.co.hyas.hpf.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jp.co.hyas.hpf.database.base.BaseSearchEntityInterface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class OwnerSearchEntity implements Serializable, BaseSearchEntityInterface {

	private ArrayList<String> owner_id__c;  // オーナーID

	private String corporation_id__c;  // 加盟店ID

	private String account_id__c;  // 加盟店ID(SFID)

	private String last_name__c;  // オーナー姓

	private String first_name__c;  // オーナー名

	private String last_name_kana__c;  // オーナー姓カナ

	private String first_name_kana__c;  // オーナー名カナ

	private String owner_company_name__c;  // オーナー法人

	private String owner_company_name_kana__c;  // オーナー法人カナ

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthdate__c;  // 生年月日

	private String email1__c;  // Email1

	private String email2__c;  // Email2

	private String tel1__c;  // 電話番号1

	private String tel2__c;  // 電話番号2

	private String fax__c;  // FAX

	private String postal_code__c;  // 郵便番号

	private String prefecture__c;  // 都道府県

	private String city__c;  // 市町村名

	private String street__c;  // 町名・地番など

	private String building_name__c;  // ビル・マンション名

	private String address_kana__c;  // 住所カナ

	private String owner_type__c;  // オーナー種別

	private String owner_rank__c;  // オーナーランク

	private String sex__c;  // 性別

	private String job__c;  // 職業

	private String income__c;  // 所得

	private String representative__c;  // 代表者名

	private String representative_kana__c;  // 代表者名カナ

	private String industry__c;  // 業種

	private Double capital__c;       // 資本金
	private Double capital__c_from;  // 資本金（下限）
	private Double capital__c_to;    // 資本金（上限）

	private Double annual_revenue__c;       // 売上高
	private Double annual_revenue__c_from;  // 売上高（下限）
	private Double annual_revenue__c_to;    // 売上高（上限）

	private String branch__c;  // 事業所

	private Double number_of_employees__c;       // 従業員数
	private Double number_of_employees__c_from;  // 従業員数（下限）
	private Double number_of_employees__c_to;    // 従業員数（上限）

	private String login_name__c;  // ログインユーザー名

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date systemmodstamp;  // システム最終更新日

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date systemmodstamp_date_from;                  // システム最終更新日

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date systemmodstamp_date_to;                    // システム最終更新日

	private String created_by_service__c;  // データ作成サービス

	private String last_modified_by_service__c;  // データ更新サービス

	private Boolean isdeleted;  // 削除フラグ
}
