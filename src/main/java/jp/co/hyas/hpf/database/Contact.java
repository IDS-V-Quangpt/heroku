package jp.co.hyas.hpf.database;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jp.co.hyas.hpf.core.constraints.HpfEmail;
import jp.co.hyas.hpf.core.constraints.HpfKatakana;
import jp.co.hyas.hpf.core.constraints.HpfName;
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
public class Contact extends BaseEntity implements Serializable {
	// ナチュラルキーの値を設定
	public void setNaturalKey(String ident) {
		this.contact_id__c = ident;  // 担当者id
	}

	// ナチュラルキーの値を取得
	@JsonIgnore
	public String getNaturalKey()
	{
		return this.getContact_id__c();
	}

	// 論理削除フラグを設定
	public void setLogicalDeleteFlag(boolean is_del) {
		is_service_delete__c = is_del;  // サービス削除フラグ
	}

	// カラム

	@Size(max=30)
	private String contact_id__c;  // HPF担当者ID (Hyas側キー項目)

	@Size(max=18)
	private String accountid;  // 加盟店ID(SFID)

	@Size(max=1300)
	private String corporationid__c;  // 加盟店ID

	@Size(max=30)
	@Pattern(regexp="^owner|admin|member|$")
	private String hpf_permission__c;  // HPF権限

	@Size(max=255)
	@Pattern(regexp="^加盟店|建築家|IC|HyAS|建築家／所員|$")
	private String contact_kind__c;  // ユーザー種別

	@JsonIgnore
	private Boolean is_approval_check__c;  // 認証有効化要求フラグ

	@JsonIgnore
	private Boolean is_invalid__c;  // 認証無効化要求フラグ

	@Size(max=255)
	private String invalid_reason__c;  // 無効化コメント

	@Size(max=1333)
	@HpfZipCode
	private String mailingpostalcode;  // 郵便番号(法人用)

	@Size(max=80)
	@HpfPrefecture
	private String mailingstate;  // 都道府県(法人用)

	@Size(max=40)
	private String mailingcity;  // 市区郡(法人用)

	@Size(max=255)
	private String mailingstreet;  // 町名・番地(法人用)

	@Size(max=255)
	private String buildingname__c;  // ビル・マンション名(法人用)

	@Size(max=40)
	@HpfName
	private String firstname;  // 名

	@Size(max=40)
	@HpfName
	private String lastname;  // 氏

	@Size(max=80)
	@HpfKatakana
	private String firstnamekana__c;  // 名カナ

	@Size(max=80)
	@HpfKatakana
	private String lastnamekana__c;  // 氏カナ

	@JsonIgnore
	private String name;  // 名前

	@Size(max=128)
	private String title;  // 役職

	@Size(max=255)
	private String belongbranchoffice__c;  // 所属支店

	@Size(max=80)
	private String department;  // 部署

	@Size(max=40)
	@HpfPhone
	private String phone;  // TEL

	private Boolean donotcall;  // TEL NG

	@Size(max=40)
	@HpfPhone
	private String fax;  // Fax

	private Boolean hasoptedoutoffax;  // FAX NG

	@Size(max=80)
	@HpfEmail
	private String email;  // Email

	private Boolean hasoptedoutofemail;  // Email NG

	@Size(max=40)
	@HpfPhone
	private String mobilephone;  // 携帯TEL

	private Boolean mobilephoneng__c;  // 携帯TEL NG

	@Size(max=80)
	@HpfEmail
	private String mobilemail__c;  // 携帯Email

	private Boolean mobilemailng__c;  // 携帯Email NG

	@Size(max=255)
	private String architect_office_url__c;  // 建築家事務所URL

	@Size(max=255)
	private String profile_movie_url__c;  // 建築家プロフィール動画URL

	@Size(max=255)
	private String profile_file_key__c;  // 建築家プロフィールオブジェクトキー

	private Boolean representativename__c;  // 代表者フラグ

	private Boolean retirement__c;  // 退職

	private Boolean plaza__c;  // PLAZA

	private Boolean plazamail__c;  // PLAZAメール

	private Boolean view__c;  // View

	private Boolean viewmail__c;  // Viewメール

	@JsonIgnore
	private Boolean needsmailmagazinrplushouse__c;  // R+house
	
	@JsonIgnore
	private Boolean needsmailmagaziniekachiforrplusadm__c;  // 家価値（R+、ADM）

	@JsonIgnore
	private Boolean needsmailmagaziniekachiforippan__c;  // 家価値（一般）

	@JsonIgnore
	private Boolean needsmailmagaziniekachibox__c;  // iekachiBOX

	@JsonIgnore
	private Boolean needsmailmagazinfp__c;  // FP

	@JsonIgnore
	private Boolean needsmailmagazinfsm__c;  // FSM

	@JsonIgnore
	private Boolean needsmailmagazinadm__c;  // ADM

	@JsonIgnore
	private Boolean needsmailmagazinams__c;  // AMS

	@JsonIgnore
	private Boolean needsmailmagazincms__c;  // CMS

	@JsonIgnore
	private Boolean needsmailmagazinhysp__c;  // HySP

	@JsonIgnore
	private Boolean needsmailmagazintochisma__c;  // トチスマ

	@JsonIgnore
	private Boolean needsmailmagazintmd__c;  // TMD

	@JsonIgnore
	private Boolean needsmailmagazingg__c;  // GG

	@JsonIgnore
	private Boolean needsmailmagazinhc__c;  // HC

	@JsonIgnore
	private Boolean needsmailmagazinhih__c;  // HIH

	@JsonIgnore
	private Boolean needsmailmagazinws__c;  // WS

	@JsonIgnore
	private Boolean needsmailmagazinstay__c;  // STAY

	@JsonIgnore
	private Boolean needsmailmagazindecos__c;  // デコス

	@JsonIgnore
	private Boolean needsmailmagazinpms__c;  // PMS

	@JsonIgnore
	private Boolean needsmailmagazintm__c;  // TM

	@JsonIgnore
	private Boolean needsmailmagazinrpluskenchikuka__c;  // R+建築家

	@JsonIgnore
	private Boolean needsmailmagazinrplusic__c;  // R+IC


	@Size(max=255)
	@Pattern(regexp="^利用不可|利用可|管理者|$")
	private String role_of_r_de_go__c;  // RでGo利用権限

	@Size(max=255)
	@Pattern(regexp="^利用不可|利用可|管理者|$")
	private String role_of_cms__c;  // CMS利用権限

	@Size(max=255)
	@Pattern(regexp="^利用不可|利用可|管理者|$")
	private String role_of_pms__c;  // PMS利用権限

	@Size(max=255)
	@Pattern(regexp="^利用不可|利用可|管理者|$")
	private String role_of_iekachi__c;  // iekachi BOX利用権限

	@Size(max=255)
	private String r_house_certificate_number__c;  // R+house施工責任者修了証番号

	@Size(max=255)
	private String r_house_construction_supervisor__c;  // R+house施工責任者区分

	private Boolean r_house_certificate_remove__c;  // R+house施工責任者認定削除

	@JsonIgnore
	private String data_migration_id__c;  // データ移行ID

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

	// リレーション取得
	private HashMap<String, Object> corporation;
	
	@JsonIgnore
	private String before_role_of_r_de_go__c;  // 変更前rでgo利用権限
	@JsonIgnore
	private String before_role_of_cms__c;  // 変更前cms利用権限
	@JsonIgnore
	private String before_role_of_pms__c;  // 変更前pms利用権限
	@JsonIgnore
	private String before_role_of_iekachi__c;  // 変更前iekachi box利用権限
}
