package jp.co.hyas.hpf.database;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jp.co.hyas.hpf.database.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
//@AllArgsConstructor
public class AccountWait extends BaseEntity implements Serializable {

	// ナチュラルキーの値を設定
	public void setNaturalKey(String ident) {
		this.corporation_wait_id__c = ident;  // 法人id
	}

	// ナチュラルキーの値を取得
	@JsonIgnore
	public String getNaturalKey()
	{
		return this.getCorporation_wait_id__c();
	}

	// 論理削除フラグを設定
	public void setLogicalDeleteFlag(boolean is_del) {}

	// カラム
	private String corporation_wait_id__c;  // 加盟店待機ID
	private String corporationid__c;  // 加盟店ID
	private String account_id__c;  // 加盟店ID(SFID)
	private String name__c;  // 加盟店名
	private String name_kana__c;  // 加盟店名カナ
	private String billingpostalcode__c;  // 郵便番号
	private String billingstate__c;  // 都道府県
	private String billingcity__c;  // 市町村
	private String billingstreet__c;  // 町名・地番
	private String buildingname__c;  // 建物名
	private String email__c;  // E-Mail
	private String phone__c;  // 電話番号
	private String fax__c;  // FAX
	private String website__c;  // サイトURL
	private Boolean is_merge_completion__c;  // マージ完了フラグ
	private Boolean is_service_delete__c;  // サービス削除フラグ
	private Integer id;  // ID
	private String sfid;  // SFID
	private String createdbyid;  // 作成者ユーザーID
	private String lastmodifiedbyid;  // 更新者ユーザーID
	private Date createddate;  // 作成日
	private Date systemmodstamp;  // システム最終更新日
	private Boolean isdeleted;  // 削除フラグ

	@JsonIgnore
	private String name;  // 法人待機情報名
}
