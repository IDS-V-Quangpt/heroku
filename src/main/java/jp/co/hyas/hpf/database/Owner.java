package jp.co.hyas.hpf.database;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jp.co.hyas.hpf.core.constraints.HpfEmail;
import jp.co.hyas.hpf.core.constraints.HpfKatakana;
import jp.co.hyas.hpf.core.constraints.HpfName;
import jp.co.hyas.hpf.core.constraints.HpfOwnerUniqueness;
import jp.co.hyas.hpf.core.constraints.HpfPhone;
import jp.co.hyas.hpf.core.constraints.HpfPrefecture;
import jp.co.hyas.hpf.core.constraints.HpfZipCode;
import jp.co.hyas.hpf.database.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@HpfOwnerUniqueness(
	properties={"login_name__c"},
	message="ログインIDがすでに登録されています。"
)
public class Owner extends BaseEntity implements Serializable {
	// ナチュラルキーの値を設定
	public void setNaturalKey(String ident) {
		this.owner_id__c = ident;  // オーナーid
	}

	// ナチュラルキーの値を取得
	@JsonIgnore
	public String getNaturalKey()
	{
		return this.getOwner_id__c();
	}

	// 論理削除フラグを設定
	public void setLogicalDeleteFlag(boolean is_del) {
		is_service_delete__c = is_del;  // サービス削除フラグ
	}

	// カラム

	@Size(max=30)
	private String owner_id__c;  // オーナーID

	@Size(max=30)
	private String corporation_id__c;  // 加盟店ID

	@Size(max=18)
	private String account_id__c;  // 加盟店ID(SFID)

	@Size(max=80)
	@HpfName
	private String last_name__c;  // オーナー姓

	@Size(max=80)
	@HpfName
	private String first_name__c;  // オーナー名

	@Size(max=80)
	@HpfKatakana
	private String last_name_kana__c;  // オーナー姓カナ

	@Size(max=80)
	@HpfKatakana
	private String first_name_kana__c;  // オーナー名カナ

	@Size(max=80)
	private String owner_company_name__c;  // オーナー法人名

	@Size(max=80)
	@HpfKatakana
	private String owner_company_name_kana__c;  // オーナー法人名カナ

	private Date birthdate__c;  // 生年月日

	@Size(max=80)
	@HpfEmail
	private String email1__c;  // Email1

	@Size(max=80)
	@HpfEmail
	private String email2__c;  // Email2

	@Size(max=40)
	@HpfPhone
	private String tel1__c;  // 電話番号1

	@Size(max=40)
	@HpfPhone
	private String tel2__c;  // 電話番号2

	@Size(max=40)
	@HpfPhone
	private String fax__c;  // FAX

	@Size(max=20)
	@HpfZipCode
	private String postal_code__c;  // 郵便番号

	@Size(max=80)
	@HpfPrefecture
	private String prefecture__c;  // 都道府県

	@Size(max=80)
	private String city__c;  // 市町村名

	@Size(max=80)
	private String street__c;  // 町名・地番など

	@Size(max=80)
	private String building_name__c;  // ビル・マンション名

	@Size(max=255)
	@HpfKatakana
	private String address_kana__c;  // 住所カナ

	@Size(max=80)
	@Pattern(regexp="^個人|法人|$")
	private String owner_type__c;  // オーナー種別

	@Size(max=80)
	private String owner_rank__c;  // オーナーランク

	@Size(max=255)
	@Pattern(regexp="^男|女|$")
	private String sex__c;  // 性別

	@Size(max=80)
	private String job__c;  // 職業

	@Size(max=80)
	private String income__c;  // 所得

	@Size(max=80)
	private String representative__c;  // 代表者名

	@Size(max=80)
	@HpfKatakana
	private String representative_kana__c;  // 代表者名カナ

	@Size(max=80)
	private String industry__c;  // 業種

	private Double capital__c;  // 資本金

	private Double annual_revenue__c;  // 売上高

	@Size(max=80)
	private String branch__c;  // 事業所

	private Double number_of_employees__c;  // 従業員数

	@Size(max=80)
	private String login_name__c;  // ログインユーザー名

	@Size(max=80)
	private String login_password__c;  // ログインパスワード

	private Boolean is_service_delete__c;  // サービス削除フラグ

	@JsonIgnore
	private Integer id;  // ID

	@JsonIgnore
	private String sfid;  // SFID

	@JsonIgnore
	private String createdbyid;  // 作成者ユーザーID

	@JsonIgnore
	private String lastmodifiedbyid;  // 更新者ユーザーID

	@JsonIgnore
	private Timestamp createddate;  // 作成日

	//@JsonIgnore
	private Timestamp systemmodstamp;  // システム最終更新日

	@Size(max=30)
	private String created_by_service__c;  // データ作成サービス

	@Size(max=30)
	private String last_modified_by_service__c;  // データ更新サービス

	private Boolean isdeleted;  // 削除フラグ

	@JsonIgnore
	private String name;  // オーナー情報名

	// リレーション取得
	private HashMap<String, Object> corporation;
}
