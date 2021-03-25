package jp.co.hyas.hpf.core.forms;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import jp.co.hyas.hpf.core.constraints.HpfCorporationKana;
import jp.co.hyas.hpf.core.constraints.HpfCorporationName;
import jp.co.hyas.hpf.core.constraints.HpfEmail;
import jp.co.hyas.hpf.core.constraints.HpfPhone;
import jp.co.hyas.hpf.core.constraints.HpfPrefecture;
import jp.co.hyas.hpf.core.constraints.HpfZipCode;
import jp.co.hyas.hpf.core.formatters.HpfCorporationKanaConvert;
import jp.co.hyas.hpf.core.formatters.HpfCorporationNameConvert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class AccountWaitForm implements Serializable {
	// カラム
	private String corporationid__c;  // 法人id
	
	@Size(max=255, message="{max}文字以内で入力してください")
	@HpfCorporationName(message="半角英数字、ひらがな、カタカナ、漢字で入力してください")
	@HpfCorporationNameConvert
	private String name__c;  // 加盟店名

	@Size(max=255, message="{max}文字以内で入力してください")
	@HpfCorporationKana(message="半角数字、カタカナで入力してください")
	@HpfCorporationKanaConvert
	private String name_kana__c;  // 加盟店名カナ

	@Size(max=20, message="{max}文字以内で入力してください")
	@HpfZipCode(message="不正な値です")
	private String billingpostalcode__c;  // 郵便番号

	@Size(max=80, message="{max}文字以内で入力してください")
	@HpfPrefecture(message="不正な値です")
	private String billingstate__c;  // 都道府県

	@Size(max=40, message="{max}文字以内で入力してください")
	private String billingcity__c;  // 市区郡

	@Size(max=255, message="{max}文字以内で入力してください")
	private String billingstreet__c;  // 町名・番地

	@Size(max=255, message="{max}文字以内で入力してください")
	private String buildingname__c;  // ビル・マンション名

	@Size(max=40, message="{max}文字以内で入力してください")
	@HpfPhone(message="不正な値です")
	private String phone__c;  // tel

	@Size(max=40, message="{max}文字以内で入力してください")
	@HpfPhone(message="不正な値です")
	private String fax__c;  // 法人 fax

	@Size(max=80, message="{max}文字以内で入力してください")
	@HpfEmail(message="不正な値です")
	private String email__c;  // email

	@Size(max=255, message="{max}文字以内で入力してください")
	@URL(message="不正な値です")
	private String website__c;  // url

	private String __key_code;
}
