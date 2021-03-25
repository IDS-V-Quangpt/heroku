package jp.co.hyas.hpf.database;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jp.co.hyas.hpf.core.constraints.HpfCorporationKana;
import jp.co.hyas.hpf.core.constraints.HpfCorporationName;
import jp.co.hyas.hpf.core.constraints.HpfEmail;
import jp.co.hyas.hpf.core.constraints.HpfKatakana;
import jp.co.hyas.hpf.core.constraints.HpfPhone;
import jp.co.hyas.hpf.core.constraints.HpfPrefecture;
import jp.co.hyas.hpf.core.constraints.HpfZipCode;
import jp.co.hyas.hpf.core.serializer.HpfCorporationKanaDeserializer;
import jp.co.hyas.hpf.core.serializer.HpfCorporationNameDeserializer;
import jp.co.hyas.hpf.database.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
//@AllArgsConstructor
public class Account extends BaseEntity implements Serializable {

	// ナチュラルキーの値を設定
	public void setNaturalKey(String ident) {
		this.corporationid__c = ident;  // 法人id
	}

	// ナチュラルキーの値を取得
	@JsonIgnore
	public String getNaturalKey()
	{
		return this.getCorporationid__c();
	}

	// 論理削除フラグを設定
	public void setLogicalDeleteFlag(boolean is_del) {
		is_service_delete__c = is_del;  // サービス削除フラグ
	}

	// http://d.hatena.ne.jp/noritsugu/20080512/Java

	// カラム

	@Size(max=30)
	private String corporationid__c;  // 加盟店ID

	@Size(max=255)
	@HpfCorporationName
	@JsonDeserialize(using = HpfCorporationNameDeserializer.class)
	private String name;  // 加盟店名

	@Size(max=255)
	@HpfCorporationKana
	@JsonDeserialize(using = HpfCorporationKanaDeserializer.class)
	private String namekana__c;  // 加盟店名カナ

	@Size(max=20)
	@HpfZipCode
	private String billingpostalcode;  // 郵便番号

	@Size(max=80)
	@HpfPrefecture
	private String billingstate;  // 都道府県

	@Size(max=40)
	private String billingcity;  // 市町村

	@Size(max=255)
	private String billingstreet;  // 町名・地番

	@Size(max=255)
	private String buildingname__c;  // 建物名

	@Size(max=40)
	@HpfPhone
	private String phone;  // 電話番号

	private Boolean phoneng__c;  // TEL NG

	@Size(max=40)
	@HpfPhone
	private String fax;  // FAX

	private Boolean faxng__c;  // FAX NG

	@Size(max=80)
	@HpfEmail
	private String email__c;  // E-Mail

	private Boolean emailng__c;  // Email NG

	@Size(max=255)
	private String website;  // サイトURL

	@Size(max=18)
	private String parentid;  // 親法人ID

	@Size(max=18)
	private String headoffice__c;  // 本店ID

	@Size(max=255)
	private String foraccountingnotext__c;  // 経理用No.

	@Size(max=255)
	private String shopnametext__c;  // 店舗名

	@Size(max=255)
	@HpfKatakana
	private String shopkanatext__c;  // 店舗名カナ

	@Size(max=255)
	private String branchofficename__c;  // 支店名

	@Size(max=255)
	@HpfKatakana
	private String branchofficekana__c;  // 支店名カナ

	private Boolean contactng__c;  // 接触NG

	private Boolean alliancearchitect__c;  // 提携建築家

	private Boolean affiliates__c;  // 提携法人

	private Boolean notpayment__c;  // 未入金

	private Boolean bankruptcy__c;  // 倒産

	private Boolean is_available_r_de_go__c;  // RでGo契約

	private Boolean is_available_cms__c;  // CMS契約

	private Boolean is_available_pms__c;  // PMS契約

	private Boolean is_available_iekachi__c;  // iekachi BOX契約

	private Boolean architrend_rights__c;  // アーキトレンドCAD権利会員

	@JsonIgnore
	private String data_migration_id__c;  // データ移行ID

	@Size(max=18)
	private String ownerid;  // 法人 所有者

	private Boolean is_service_delete__c;  // サービス削除フラグ

	@JsonIgnore
	private Integer id;  // ID

	@Size(max=18)
	private String sfid;  // SFID

	@JsonIgnore
	private String createdbyid;  // 作成者ユーザーID

	@JsonIgnore
	private String lastmodifiedbyid;  // 更新者ユーザーID

	private Timestamp createddate;  // 作成日

	private Timestamp systemmodstamp;  // システム最終更新日

	@Size(max=30)
	private String created_by_service__c;  // データ作成サービス

	@Size(max=30)
	private String last_modified_by_service__c;  // データ更新サービス

	private Boolean isdeleted;  // 削除フラグ
	
	@JsonIgnore
	private Double ibxauthoritylimit__c;                        // IBX商材利用権限数
	
	@JsonIgnore
	private Double pmsauthoritylimit__c;                        // PMS商材利用権限数
	
	@JsonIgnore
	private Double cmsauthoritylimit__c;                        // CMS商材利用権限数
	
	@JsonIgnore
	private Double rgoauthoritylimit__c;                        // RGO商材利用権限数
	
    private Date iekachi_60years_support_contract_date__c;      //家価値60年サポート加盟契約日
	
    private Date iekachi_membership_contract_date__c;          //iekachiBOX加盟契約日
	
	private String r_houseshopnametext__c;          //R+house店舗名
	
	private String topaccount__c;          //Top法人

	private String tophead__c;          //TOP本店
}
