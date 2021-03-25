package jp.co.hyas.hpf.auth;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
/*
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
*/
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jp.co.hyas.hpf.core.exception.AuthorizeException;
import jp.co.hyas.hpf.database.AccountRepository;
import jp.co.hyas.hpf.database.ContactRepository;
import jp.co.hyas.hpf.database.UserRepository;
import jp.co.hyas.hpf.database.base.ApiResult;
import jp.co.hyas.hpf.database.base.Util;

@Service
public class AuthService {
	Logger logger = LoggerFactory.getLogger(AuthService.class);

	@Autowired
	UserRepository userRep;
	@Autowired
	ContactRepository contactRep;
	@Autowired
	AccountRepository accountRep;

	public ApiResult getAuthInfo(String userId18) {

		// 法人 ID	reference to: Account	18		accountid
		// コールセンター ID	reference to: CallCenter	18		callcenterid
		// 担当者 ID	reference to: Contact	18		contactid
		Map<String, Object> userRec = userRep.findOneBySfId(userId18);
		String username = (userRec == null) ? null :
			(String)userRec.getOrDefault("username", null);
		String contactId = (userRec == null) ? null :
			(String)userRec.getOrDefault("contactid", null);
		String accountId = (userRec == null) ? null :
			(String)userRec.getOrDefault("accountid", null);

		Map<String, Object> contactRec = null;
		Map<String, Object> accountRec = null;
		Map<String, Object> headAccountRec = null;
		String headAccountId = null;

		// // 担当者種別 ('"加盟店","建築家","IC")
		// Contactにデータがない => user_kind = 'HyAS', hpf_admin = null;
		// Contactにデータがある => user_kind = contact::user_kind, hpf_admin = contact::hpf_permission;
		String hpf_permission = null;
		String contact_kind = "HyAS";
		if (contactId != null) {
			contactRec = contactRep.findOneBySfId(contactId);
			if (contactRec == null) {
				System.err.println("contactRec: null / contactId =" + contactId);
			}
			else {
				contact_kind = (String)contactRec.getOrDefault("contact_kind__c", null);
				if (StringUtils.isEmpty(contact_kind)) { // contact_kind無し
					// System.err.println("contact_kind: null / contactId =" + contactId);
					logger.warn("[{}] contact_kind__c is empty.", userId18);
					throw new AuthorizeException("contact_kind__c is empty.");
				}
				hpf_permission = (String)contactRec.getOrDefault("hpf_permission__c", null);
				if (hpf_permission == null) { // contact_kind無し
					logger.warn("[{}] hpf_permission__c is empty.", userId18);
					throw new AuthorizeException("hpf_permission__c is empty.");
				}
			}
		}
		// contact
		// private String contact_kind__c;  // 担当者種別 ('"加盟店","建築家","IC")
		// private String invalid_reason__c;  // 無効化コメント
		// private Boolean is_approval_check__c;  // 要承認チェックフラグ
		// private Boolean is_invalid__c;  // 無効化フラグ
		// private Boolean is_service_delete__c;  // サービス削除フラグ
		// private String role_of_cms__c;  // cms利用権限
		// private String role_of_iekachi__c;  // iekachi box利用権限
		// private String role_of_pms__c;  // pms利用権限
		// private String role_of_r_de_go__c;  // rでgo利用権限

		// account
		// private Boolean is_available_cms__c;  // cms利用可 (0:利用不可 1:利用可)
		// private Boolean is_available_iekachi__c;  // iekachi box利用可 (0:利用不可 1:利用可)
		// private Boolean is_available_pms__c;  // pms利用可 (0:利用不可 1:利用可)
		// private Boolean is_available_r_de_go__c;  // rでgo利用権限 (0:利用不可 1:利用可)
		// private Boolean is_service_delete__c;  // サービス削除フラグ


		final String ADMIN = "管理者";
		final String NOUSE = "利用不可";
		ApiResult body = new ApiResult();
		body.put("user_name", username); // 認可ユーザー名
		body.put("user_id", userId18);   // ユーザーId(SFID)
		body.put("user_kind", contact_kind); // ユーザー種別
		
		//API変換対応
/*		if("オーナー".equals(hpf_permission)){
			hpf_permission = "owner";
		}else if("マネージャー".equals(hpf_permission)){
			hpf_permission = "admin";
		}else if("メンバー".equals(hpf_permission)){
			hpf_permission = "member";
		}
*/		
		body.put("hpf_admin", hpf_permission);  // HPF管理者権限

		if (contactRec == null) { // contact無し HyASユーザの場合
			body.put("usage_auth_cms", ADMIN);  // CMS利用権限 (利用不可/利用可/管理者)
			body.put("usage_auth_pms", ADMIN);  // PMS利用権限 (利用不可/利用可/管理者)
			body.put("usage_auth_r_de_go", ADMIN);  // RでGO利用権限 (利用不可/利用可/管理者)
			body.put("usage_auth_iekachi", ADMIN);  // iekachi利用権限 (利用不可/利用可/管理者)

			String _lastname = null;
			String _firstname = null;
			String _username = null;
			String _email = null;
			if (userRec != null) {
				_lastname = (String)userRec.getOrDefault("lastname", null);
				_firstname = (String)userRec.getOrDefault("firstname", null);
				if (_lastname != null) {
					_username = _username == null ? "" : _username;
					_username = _username + _lastname;
				}
				if (_firstname != null) {
					_username = _username == null ? "" : _username;
					_username = _username + _firstname;
				}
				_email = (String)userRec.getOrDefault("email", null);
			}
			body.put("contactid", null);
			body.put("corporationid", null);
			body.put("lastname", _lastname);
			body.put("firstname", _firstname);
			body.put("username", _username);
			body.put("email", _email);

		} else { //
			// accountIdが空でない
			/*
			if (!StringUtils.isEmpty(accountId)) {
				accountRec = accountRep.findOneBySfId(accountId);
				if (accountRec != null) {
					Map<String, Object> targetAccount = accountRec;
					for (int i = 0; i < 3; i++) {
						headAccountId = (String)targetAccount.getOrDefault("headoffice__c", null);
						if (StringUtils.isEmpty(headAccountId)) {
							break;
						}
						else if (i == 2) {
							// 2階層以上親がある場合
							throw new AuthorizeException("too many head office.");
						}
						headAccountRec = accountRep.findOneBySfId(headAccountId);
						if (headAccountRec == null || (Boolean)Util.ifMapNull(targetAccount, "is_service_delete__c", true)) {
							throw new AuthorizeException("head office not found.");
						}
						if ((Boolean)Util.ifMapNull(targetAccount,"is_service_delete__c", true)) {
							throw new AuthorizeException("head office already deleted.");
						}
						targetAccount = headAccountRec;
					}
				}
			}*/
			if (!StringUtils.isEmpty(accountId)) {
				accountRec = accountRep.findOneBySfId(accountId);
			}
			
			// error : debug
			if (accountId == null) { // accountId無し
				System.err.println("accountId: null / contactId =" + contactId);
			}
			if (accountId != null && accountRec == null) { // accountRec無し
				System.err.println("accountRec: null / accountId =" + accountId);
			}
			/*
			if (headAccountId != null && headAccountRec == null) { // headAccountRec無し
				System.err.println("headAccountRec: null / headoffice__c =" + headAccountId);
			}
			*/
			Map<String, Object> checkAccountRec = null;
			//checkAccountRec = headAccountRec == null ? accountRec : headAccountRec;
			checkAccountRec = accountRec;
			
			boolean is_invaliduser = false; // 無効ユーザでないか？
			if (!is_invaliduser) { // 担当者がサービス停止でないか？
				//is_invaliduser = (boolean)contactRec.getOrDefault("is_service_delete__c", true);
				is_invaliduser = (boolean)Util.ifMapNull(contactRec, "is_service_delete__c", true);
				if (is_invaliduser) System.err.println("contact.is_service_delete__c != false");
			}
			if (!is_invaliduser && checkAccountRec != null) { // 加盟店がサービス停止でないか？
				//is_invaliduser = (boolean)checkAccountRec.getOrDefault("is_service_delete__c", true);
				is_invaliduser = (boolean)Util.ifMapNull(checkAccountRec, "is_service_delete__c", true);
				if (is_invaliduser) System.err.println("account.is_service_delete__c != false");
			}
			//if (!is_invaliduser)
			//	is_invaliduser = (boolean)accountRec.getOrDefault("is_service_delete__c", true);
			//if (!is_invaliduser && headAccountRec != null)
			//	is_invaliduser = (boolean)headAccountRec.getOrDefault("is_service_delete__c", true);
			boolean is_sys_cms = false;
			boolean is_sys_pms = false;
			boolean is_sys_r_de_go = false;
			boolean is_sys_iekachi = false;

			if (checkAccountRec != null) {
				//is_sys_cms = (boolean)checkAccountRec.getOrDefault("is_available_cms__c", false);
				//is_sys_pms = (boolean)checkAccountRec.getOrDefault("is_available_pms__c", false);
				//is_sys_r_de_go = (boolean)checkAccountRec.getOrDefault("is_available_r_de_go__c", false);
				//is_sys_iekachi = (boolean)checkAccountRec.getOrDefault("is_available_iekachi__c", false);

				is_sys_cms = (boolean)Util.ifMapNull(checkAccountRec, "is_available_cms__c", false);
				is_sys_pms = (boolean)Util.ifMapNull(checkAccountRec, "is_available_pms__c", false);
				is_sys_r_de_go = (boolean)Util.ifMapNull(checkAccountRec, "is_available_r_de_go__c", false);
				is_sys_iekachi = (boolean)Util.ifMapNull(checkAccountRec, "is_available_iekachi__c", false);
			}

			Map<String, Object> ct = contactRec;
			//String v_cms = is_sys_cms ? (String)ct.getOrDefault("role_of_cms__c",NOUSE) : NOUSE;
			//String v_pms = is_sys_pms ? (String)ct.getOrDefault("role_of_pms__c",NOUSE) : NOUSE;
			//String v_r_de_go = is_sys_r_de_go ? (String)ct.getOrDefault("role_of_r_de_go__c",NOUSE) : NOUSE;
			//String v_iekachi = is_sys_iekachi ? (String)ct.getOrDefault("role_of_iekachi__c",NOUSE) : NOUSE;

			String v_cms = is_sys_cms ? (String)Util.ifMapNull(ct,"role_of_cms__c",NOUSE) : NOUSE;
			String v_pms = is_sys_pms ? (String)Util.ifMapNull(ct,"role_of_pms__c",NOUSE) : NOUSE;
			String v_r_de_go = is_sys_r_de_go ? (String)Util.ifMapNull(ct,"role_of_r_de_go__c",NOUSE) : NOUSE;
			String v_iekachi = is_sys_iekachi ? (String)Util.ifMapNull(ct,"role_of_iekachi__c",NOUSE) : NOUSE;

			// body.put("company_id",""); // 加盟店ID
			// if (headAccountId != null) { // 本店が存在する場合
			// 	body.put("headoffice",""); // 加盟店本店ID
			// }
			body.put("usage_auth_cms",v_cms);  // CMS利用権限 (利用不可/利用可/管理者)
			body.put("usage_auth_pms",v_pms);  // PMS利用権限 (利用不可/利用可/管理者)
			body.put("usage_auth_r_de_go",v_r_de_go);  // RでGO利用権限 (利用不可/利用可/管理者)
			body.put("usage_auth_iekachi",v_iekachi);  // iekachi利用権限 (利用不可/利用可/管理者)

			String _lastname = (String)contactRec.getOrDefault("lastname", null);
			String _firstname = (String)contactRec.getOrDefault("firstname", null);
			String _username = null;
			if (_lastname != null) {
				_username = _username == null ? "" : _username;
				_username = _username + _lastname;
			}
			if (_firstname != null) {
				_username = _username == null ? "" : _username;
				_username = _username + _firstname;
			}
			body.put("contactid", (String)contactRec.getOrDefault("contact_id__c", null));
			body.put("lastname", _lastname);
			body.put("firstname", _firstname);
			body.put("username", _username);
			body.put("email", (String)contactRec.getOrDefault("email__c", null));
			body.put("corporationid", accountRec == null ? null : accountRec.getOrDefault("corporationid__c", null));

			// ログイン無効の場合
			if (is_invaliduser) return null;
		}

		//Map<String, Object> ct = contactRec;
		//String v_cms = is_sys_cms ? (ct == null ? ADMIN : (String)ct.getOrDefault("role_of_cms__c",NOUSE)) : NOUSE;
		//String v_pms = is_sys_pms ? (ct == null ? ADMIN : (String)ct.getOrDefault("role_of_pms__c",NOUSE)) : NOUSE;
		//String v_r_de_go = is_sys_r_de_go ? (ct == null ? ADMIN : (String)ct.getOrDefault("role_of_r_de_go__c",NOUSE)) : NOUSE;
		//String v_iekachi = is_sys_iekachi ? (ct == null ? ADMIN : (String)ct.getOrDefault("role_of_iekachi__c",NOUSE)) : NOUSE;

		return body;
	}

}
