package jp.co.hyas.hpf.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.time.ZonedDateTime;
import java.sql.Timestamp;

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
public class AccountSearchEntity implements Serializable, BaseSearchEntityInterface {

	private ArrayList<String> corporationid__c;  // 加盟店ID

	private String name;  // 加盟店名

	private String namekana__c;  // 加盟店名カナ

	private String billingpostalcode;  // 郵便番号

	private String billingstate;  // 都道府県

	private String billingcity;  // 市町村

	private String billingstreet;  // 町名・地番

	private String buildingname__c;  // ビル・マンション名

	private String phone;  // 電話番号

	private Boolean phoneng__c;  // TEL NG

	private String fax;  // FAX

	private Boolean faxng__c;  // FAX NG

	private String email__c;  // E-Mail

	private Boolean emailng__c;  // Email NG

	private String website;  // サイトURL

	private String parentid;  // 親法人ID

	private String headoffice__c;  // 本店ID

	private String foraccountingnotext__c;  // 経理用No.

	private String shopnametext__c;  // 店舗名

	private String shopkanatext__c;  // 店舗名カナ

	private String branchofficename__c;  // 支店名

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

	private Boolean is_service_delete__c;  // サービス削除フラグ

	private String sfid;  // SFID

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@HpfTimeZoneJSTConvert
	@JsonDeserialize(using = HpfTimeZoneJSTDeserializer.class)
	private String createddate;  // 作成日

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@HpfTimeZoneJSTConvert
	@JsonDeserialize(using = HpfTimeZoneJSTDeserializer.class)
	private String createddate_from;                  // 作成日

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@HpfTimeZoneJSTConvert
	@JsonDeserialize(using = HpfTimeZoneJSTDeserializer.class)
	private String createddate_to;                    // 作成日

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
	
	private Double ibxauthoritylimit__c;                        // IBX商材利用権限数
	
	private Double pmsauthoritylimit__c;                        // PMS商材利用権限数
	
	private Double cmsauthoritylimit__c;                        // CMS商材利用権限数

	private Double rgoauthoritylimit__c;                        // RGO商材利用権限数
	
    private Date iekachi_60years_support_contract_date__c;      //家価値60年サポート加盟契約日
	
    private Date iekachi_membership_contract_date__c;          //iekachiBOX加盟契約日
	
	private String r_houseshopnametext__c;          //R+house店舗名

	private String topaccount__c;          //Top法人

	private String tophead__c;          //TOP本店
}
