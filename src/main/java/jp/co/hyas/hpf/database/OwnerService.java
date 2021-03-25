package jp.co.hyas.hpf.database;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import jp.co.hyas.hpf.database.base.AppConst;
import jp.co.hyas.hpf.database.base.BaseService;
import jp.co.hyas.hpf.database.base.Util;
import xyz.downgoon.snowflake.Snowflake;

@Service
public class OwnerService extends BaseService<Owner, OwnerSearchEntity, OwnerRepository> {
//public class OwnerService {
	@Autowired
	OwnerRepository repository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
    PasswordEncoder passwordEncoder;

    public OwnerRepository getRepository(){
		return this.repository;
	}
	public Class<Owner> getEntityClass(){
		return Owner.class;
	}

	public String lastPasswd = null; // 設定したパスワードを記憶

	public void presetEntityOnCrud(String crud, String ident, Owner t, Map<String, Object> hs) {
		// 登録時と更新時にSFIDを自動設定
		if (t != null && "insert".equals(crud)) {

			if (t.getCorporation_id__c() != null && t.getCorporation_id__c().length() > 0) {
				Account account = accountRepository.findOne(t.getCorporation_id__c());
				if (account != null) {
					t.setAccount_id__c(account.getSfid());
				}
			}
			// IDの自動生成
			if (StringUtils.isEmpty(t.getLogin_name__c())) {
				t.setLogin_name__c("HPF" + snowflake.nextId());
			}
			// パスワードのハッシュ化
			String rawPasswd = t.getLogin_password__c();
			if (rawPasswd == null || "".equals(rawPasswd)) // 未指定時はランダム生成して設定
				rawPasswd = Util.generatePasswd();
			String hashPasswd = passwordEncoder.encode(rawPasswd);
			lastPasswd = rawPasswd; // 設定したパスワードを記憶
			t.setLogin_password__c(hashPasswd);

			if (t.getIsdeleted() == null) {
				t.setIsdeleted(false);
			}
			// created_by_service => appCode
			if (appCode != null) {
				t.setCreated_by_service__c(appCode);
				t.setLast_modified_by_service__c(appCode);
			}
			// systemmodstamp => NOW
			//t.setSystemmodstamp(Util.currentTimestamp());
		}
		//else if (t != null && "update".equals(crud)) {
		else if (hs != null && t != null && "update".equals(crud)) {

			String corporation_id = (String) hs.get("corporation_id__c");
			if (corporation_id != null && corporation_id.length() > 0) {
				Account account = accountRepository.findOne(t.getCorporation_id__c());
				if (account != null) {
					// 追加項目として認識させるために必要
					hs.put("account_id__c", account.getSfid());
					// 実際に登録される値を参照させるために必要
					t.setAccount_id__c(account.getSfid());
				}
			}
			// パスワードのハッシュ化
			String rawPasswd = t.getLogin_password__c();
			if (rawPasswd != null) { // || "".equals(rawPasswd)) {
				lastPasswd = rawPasswd; // 設定したパスワードを記憶
				String hashPasswd = passwordEncoder.encode(rawPasswd);
				t.setLogin_password__c(hashPasswd);
			} // 未指定時は更新しない

			// last_modified_by_service => appCode
			if (appCode != null) {
				hs.put("last_modified_by_service__c", appCode);
				t.setLast_modified_by_service__c(appCode);
			}
			// systemmodstamp => NOW
			//Timestamp now = Util.currentTimestamp();
			//t.setSystemmodstamp(now);
			//hs.put("systemmodstamp", now);
		}
	}

	// DB登録実施後処理
	public void postDbExecute(String crud, String ident, Owner t, Map<String, Object> hs) {}

	// ID生成
	// datacenter: AppConst.IDG_MACHINEID; workerId: 3 // 0-31
	static Snowflake snowflake = new Snowflake(AppConst.IDG_MACHINEID, 3);
	@Override
	public String generateId() {
		return "OWN" + snowflake.nextId(); //return "W" + super.generateId();
	}

	public Map<String, Object> authenticate(OwnerLoginBean auth) {
		Owner owner = repository.authenticate(auth);
		if (owner == null) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("authorization_result", false);
//			return map;
			throw new AuthorizeException("owner not found.");
		}
		String hashPasswd = owner.getLogin_password__c();
		if (hashPasswd == null || "".equals(hashPasswd))
			throw new AuthorizeException("owner password not regist.");

		// ハッシュ値を使った比較
		if (!passwordEncoder.matches(auth.getPassword(), hashPasswd)) {
			throw new AuthorizeException("owner password not match.");
        }

		Account account = accountRepository.findOne(owner.getCorporation_id__c());
		if (account == null) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("authorization_result", false);
//			return map;
			throw new AuthorizeException("account not found.");
		}

		Account headAccount = null;
		Account target_account = account;
		for (int i = 0; i < 3; i++) {
			if (StringUtils.isEmpty(target_account.getHeadoffice__c())) {
				break;
			} else if (i == 2) {
				// 2階層以上親がある場合
				throw new AuthorizeException("too many head office.");
			}
			headAccount = accountRepository.findOneEntityBySfId(target_account.getHeadoffice__c());
			if (headAccount == null || headAccount.getIs_service_delete__c()) {
				throw new AuthorizeException("head office not found.");
			}
			target_account = headAccount;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("corporation_id", owner.getCorporation_id__c());
		///map.put("user_id", owner.getOwner_id__c());
		map.put("owner_id", owner.getOwner_id__c());
		map.put("lastname", owner.getLast_name__c());
		map.put("firstname", owner.getFirst_name__c());
		map.put("email", owner.getEmail1__c());
		map.put("user_kind", null); // ユーザー種別
		map.put("hpf_admin", null);  // HPF管理者権限
		if (headAccount == null) {
			map.put("usage_auth_pms", account.getIs_available_pms__c() ? "利用可" : "利用不可");
			map.put("usage_auth_iekachi", account.getIs_available_iekachi__c() ? "利用可" : "利用不可");
		} else {
			map.put("usage_auth_pms", headAccount.getIs_available_pms__c() ? "利用可" : "利用不可");
			map.put("usage_auth_iekachi", headAccount.getIs_available_iekachi__c() ? "利用可" : "利用不可");
		}
		return map;
	}
}
