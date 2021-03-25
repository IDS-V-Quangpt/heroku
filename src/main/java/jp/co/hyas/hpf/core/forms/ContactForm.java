package jp.co.hyas.hpf.core.forms;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import jp.co.hyas.hpf.core.constraints.HpfEmail;
import jp.co.hyas.hpf.core.constraints.HpfKatakana;
import jp.co.hyas.hpf.core.constraints.HpfName;
import jp.co.hyas.hpf.core.constraints.HpfPhone;
import jp.co.hyas.hpf.core.constraints.HpfPrefecture;
import jp.co.hyas.hpf.core.constraints.HpfZipCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class ContactForm implements Serializable {

	private String contact_id__c;  // 担当者ID (Hyas側キー項目)

	@Size(max=255)
	@Pattern(regexp="^加盟店|建築家|IC|HyAS|建築家／所員|$")
	private String contact_kind__c;  // ユーザ種別

	@Size(max=30, message="{max}文字以内で入力してください")
	@Pattern(regexp="^owner|admin|member|$", message="不正な値です")
	private String hpf_permission__c;  // HPF権限

	@Size(max=40, message="{max}文字以内で入力してください")
	@HpfName(message="不正な値です")
	private String firstname;  // 名

	@NotEmpty(message = "必ず入力してください")
	@Size(max=40, message="{max}文字以内で入力してください")
	@HpfName(message="不正な値です")
	private String lastname;  // 氏

	@Size(max=80, message="{max}文字以内で入力してください")
	@HpfKatakana(message="カタカナで入力してください")
	private String firstnamekana__c;  // 名カナ

	@Size(max=80, message="{max}文字以内で入力してください")
	@HpfKatakana(message="カタカナで入力してください")
	private String lastnamekana__c;  // 氏カナ

	@Size(max=255, message="{max}文字以内で入力してください")
	private String belongbranchoffice__c;  // 所属支店

	private Boolean representativename__c;  // 代表者

	@Size(max=80, message="{max}文字以内で入力してください")
	private String department;  // 部署

	@Size(max=128, message="{max}文字以内で入力してください")
	private String title;  // 役職

	@Size(max=1333, message="{max}文字以内で入力してください")
	@HpfZipCode(message="不正な値です")
	private String mailingpostalcode;  // 勤務先：郵便番号

	@Size(max=80, message="{max}文字以内で入力してください")
	@HpfPrefecture
	private String mailingstate;  // 勤務先：都道府県

	@Size(max=40, message="{max}文字以内で入力してください")
	private String mailingcity;  // 勤務先：市区郡

	@Size(max=255, message="{max}文字以内で入力してください")
	private String mailingstreet;  // 勤務先：町名・番地

	@Size(max=255, message="{max}文字以内で入力してください")
	private String buildingname__c;  // ビル・マンション名

	@Size(max=40, message="{max}文字以内で入力してください")
	@HpfPhone(message="不正な値です")
	private String phone;  // 勤務先：tel

	private Boolean donotcall;  // 勤務先：tel ng

	@Size(max=40, message="{max}文字以内で入力してください")
	@HpfPhone(message="不正な値です")
	private String mobilephone;  // 携帯tel

	private Boolean mobilephoneng__c;  // 携帯tel ng

	@Size(max=40, message="{max}文字以内で入力してください")
	@HpfPhone(message="不正な値です")
	private String fax;  // 勤務先：fax

	private Boolean hasoptedoutoffax;  // 勤務先：fax ng

	@NotEmpty(message = "必ず入力してください")
	@Size(max=80, message="{max}文字以内で入力してください")
	@HpfEmail(message="不正な値です")
	private String email;  // 勤務先：email

	private Boolean hasoptedoutofemail;  // 勤務先：email ng

	@Size(max=80, message="{max}文字以内で入力してください")
	@HpfEmail(message="不正な値です")
	private String mobilemail__c;  // 携帯email

	private Boolean mobilemailng__c;  // 携帯email ng

	@Size(max=255, message="{max}文字以内で入力してください")
	@Pattern(regexp="^利用不可|利用可|管理者|$", message="不正な値です")
	private String role_of_r_de_go__c;  // rでgo利用権限
/*
	@Size(max=255, message="{max}文字以内で入力してください")
	@Pattern(regexp="^利用不可|利用可|管理者|$", message="不正な値です")
	private String role_of_cms__c;  // cms利用権限
*/
	@Size(max=255, message="{max}文字以内で入力してください")
	@Pattern(regexp="^利用不可|利用可|管理者|$", message="不正な値です")
	private String role_of_pms__c;  // pms利用権限
/*
	@Size(max=255, message="{max}文字以内で入力してください")
	@Pattern(regexp="^利用不可|利用可|管理者|$", message="不正な値です")
	private String role_of_iekachi__c;  // iekachi box利用権限
*/
	private Boolean view__c;  // view

	private Boolean viewmail__c;  // viewメール

	private Boolean plaza__c;  // plaza

	private Boolean plazamail__c;  // plazaメール


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


	private Boolean is_approval_check__c;  // 認証有効化要求フラグ

	private Boolean is_invalid__c;  // 認証無効化要求フラグ

	@Size(max=255, message="{max}文字以内で入力してください")
	private String invalid_reason__c;  // 無効化コメント

	private Boolean retirement__c;  // 退職

	private String __key_code;
	
	private String before_role_of_r_de_go__c;  // 変更前rでgo利用権限

	private String before_role_of_cms__c;  // 変更前cms利用権限

	private String before_role_of_pms__c;  // 変更前pms利用権限

	private String before_role_of_iekachi__c;  // 変更前iekachi box利用権限
}
