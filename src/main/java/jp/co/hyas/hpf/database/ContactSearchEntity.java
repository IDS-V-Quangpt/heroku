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
import jp.co.hyas.hpf.core.formatters.HpfTimeZoneJSTConvert;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jp.co.hyas.hpf.core.serializer.HpfTimeZoneJSTDeserializer;
import jp.co.hyas.hpf.core.formatters.HpfTimeZoneJSTConvert;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class ContactSearchEntity implements Serializable, BaseSearchEntityInterface {

	private ArrayList<String> contact_id__c;  // HPF担当者ID

	private String accountid;  // 加盟店ID(SFID)

	private String corporationid__c;  // 加盟店ID

	private ArrayList<String> hpf_permission__c;  // HPF権限

	private String contact_kind__c;  // ユーザー種別

	private String invalid_reason__c;  // 無効化コメント

	private String mailingpostalcode;  // 郵便番号(法人用)

	private String mailingstate;  // 都道府県(法人用)

	private String mailingcity;  // 市区郡(法人用)

	private String mailingstreet;  // 町名・番地(法人用)

	private String buildingname__c;  // ビル・マンション名(法人用)

	private String firstname;  // 名

	private String lastname;  // 氏

	private String firstnamekana__c;  // 名カナ

	private String lastnamekana__c;  // 氏カナ

	private String title;  // 役職

	private String belongbranchoffice__c;  // 所属支店

	private String department;  // 部署

	private String phone;  // TEL

	private Boolean donotcall;  // TEL NG

	private String fax;  // Fax

	private Boolean hasoptedoutoffax;  // FAX NG

	private String email;  // Email

	private Boolean hasoptedoutofemail;  // Email NG

	private String mobilephone;  // 携帯TEL

	private Boolean mobilephoneng__c;  // 携帯TEL NG

	private String mobilemail__c;  // 携帯Email

	private Boolean mobilemailng__c;  // 携帯Email NG

	private Boolean representativename__c;  // 代表者

	private Boolean retirement__c;  // 退職

	private Boolean plaza__c;  // PLAZA

	private Boolean plazamail__c;  // PLAZAメール

	private Boolean view__c;  // View

	private Boolean viewmail__c;  // Viewメール


	private Boolean needsmailmagazinrplushouse__c;  // R+house

	private Boolean needsmailmagaziniekachiforrplusadm__c;  // 家価値（R+、ADM）

	private Boolean needsmailmagaziniekachiforippan__c;  // 家価値（一般）

	private Boolean needsmailmagaziniekachibox__c;  // iekachiBOX

	private Boolean needsmailmagazinfp__c;  // FP

	private Boolean needsmailmagazinfsm__c;  // FSM

	private Boolean needsmailmagazinadm__c;  // ADM

	private Boolean needsmailmagazinams__c;  // AMS

	private Boolean needsmailmagazincms__c;  // CMS

	private Boolean needsmailmagazinhysp__c;  // HySP

	private Boolean needsmailmagazintochisma__c;  // トチスマ

	private Boolean needsmailmagazintmd__c;  // TMD

	private Boolean needsmailmagazingg__c;  // GG

	private Boolean needsmailmagazinhc__c;  // HC

	private Boolean needsmailmagazinhih__c;  // HIH

	private Boolean needsmailmagazinws__c;  // WS

	private Boolean needsmailmagazinstay__c;  // STAY

	private Boolean needsmailmagazindecos__c;  // デコス

	private Boolean needsmailmagazinpms__c;  // PMS

	private Boolean needsmailmagazintm__c;  // TM

	private Boolean needsmailmagazinrpluskenchikuka__c;  // R+建築家

	private Boolean needsmailmagazinrplusic__c;  // R+IC

	private ArrayList<String> role_of_r_de_go__c;  // RでGo利用権限

	private ArrayList<String> role_of_cms__c;  // CMS利用権限

	private ArrayList<String> role_of_pms__c;  // PMS利用権限

	private ArrayList<String> role_of_iekachi__c;  // iekachi BOX利用権限

	private String r_house_certificate_number__c;  // R+house施工責任者修了証番号

	private String r_house_construction_supervisor__c;  // R+house施工責任者区分

	private Boolean r_house_certificate_remove__c;  // R+house施工責任者認定削除

	private Boolean is_service_delete__c;  // サービス削除フラグ

	private String sfid;  // SFID

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@HpfTimeZoneJSTConvert
	@JsonDeserialize(using = HpfTimeZoneJSTDeserializer.class)
	private String createddate;  // 作成日

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@HpfTimeZoneJSTConvert
	@JsonDeserialize(using = HpfTimeZoneJSTDeserializer.class)
	private String createddate_from;  // 作成日

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@HpfTimeZoneJSTConvert
	@JsonDeserialize(using = HpfTimeZoneJSTDeserializer.class)
	private String createddate_to;  // 作成日

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@HpfTimeZoneJSTConvert
	@JsonDeserialize(using = HpfTimeZoneJSTDeserializer.class)
	private String systemmodstamp;  // システム最終更新日

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@HpfTimeZoneJSTConvert
	@JsonDeserialize(using = HpfTimeZoneJSTDeserializer.class)
	private String systemmodstamp_date_from;                  // システム最終更新日

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@HpfTimeZoneJSTConvert
	@JsonDeserialize(using = HpfTimeZoneJSTDeserializer.class)
	private String systemmodstamp_date_to;                    // システム最終更新日

	private String created_by_service__c;  // データ作成サービス

	private String last_modified_by_service__c;  // データ更新サービス

	private Boolean isdeleted;  // 削除フラグ
}
