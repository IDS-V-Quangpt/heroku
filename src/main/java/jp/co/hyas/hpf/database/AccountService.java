package jp.co.hyas.hpf.database;

import java.util.Map;

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

import jp.co.hyas.hpf.database.base.AppConst;
import jp.co.hyas.hpf.database.base.BaseService;
import xyz.downgoon.snowflake.Snowflake;

@Service
public class AccountService extends BaseService<Account, AccountSearchEntity, AccountRepository> {
//public class AccountService {
	@Autowired
	AccountRepository repository;
	public AccountRepository getRepository() {
		return this.repository;
	}
	public Class<Account> getEntityClass() {
		return Account.class;
	}
	public void presetEntityOnCrud(String crud, String ident, Account t, Map<String, Object> hs) {
		// 認証後:insert時
		//if ("create-authorize".equals(crud)) {
		//	if (t != null) t.setCreated_by_service(appCode);
		//}
		// 認証後:update時
		//if ("modify-authorize".equals(crud)) {
		//	if (hs != null) hs.put("last_modified_by_service", appCode);
		//}

		if (t != null && "insert".equals(crud)) {
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
		else if (hs != null && t != null && "update".equals(crud)) {
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
	public void postDbExecute(String crud, String ident, Account t, Map<String, Object> hs) {}

	// ID生成
	// datacenter: AppConst.IDG_MACHINEID; workerId: 1 // 0-31
	static Snowflake snowflake = new Snowflake(AppConst.IDG_MACHINEID, 1);
	@Override
	public String generateId() {
		return "ACC" + snowflake.nextId(); //return "A" + super.generateId();
	}
/*
	// ========================================= 調査実装コード
	public List<Account> findAll() {
		return repository.findAll();
	}

	public List<Account> findBy(Map<String, String> conditions) {
		return repository.findBy(conditions);
	}

	public List<Account> findLike(String corporationid) {
		return repository.findLike(corporationid);
	}

	public List<Account> findLikes(Map<String, String> conditions) {
		// LIKE用文字列に一括変換
        Map<String, String> likesParams = mapLikeEscape(conditions,"%","%");
		Logger logger = LoggerFactory.getLogger(this.getClass());
        logger.info("likesParams:" + likesParams.toString());
		return repository.findLikes(likesParams);
	}

	public Page<Account> search(Map<String, String> criteria, Pageable pageable) {
		long total = repository.count(criteria);
		List<Account> list = Collections.emptyList();
		if (total > 0) {
			list = repository.search(criteria, pageable);
		}
		return new PageImpl<Account>(list, pageable, total);
	}

	public void create(Account account) {
		repository.insert(account);
	}

	// LIKE検索のための内部関数
	public static Map<String, String> mapLikeEscape(Map<String, String> conditions, String prefix, String suffix) {
        Logger logger = LoggerFactory.getLogger(AccountService.class);
		Set<String> keys = conditions.keySet();
		Map<String, String> conditions2 = new HashMap<String, String>();
		keys.forEach((key) -> {
			String val = conditions.get(key);
			conditions2.put(key, prefix+likeEscape(val)+suffix);
		});
        logger.info("conditions2:" + conditions2.toString());
		return conditions2;
	}
	public static String likeEscape(String before) {
		StringBuilder after = new StringBuilder();
		String esSymbol = "\\";
		char es1 = '_';
		char es2 = '%';

		for (int i = 0; i < before.length(); i++) {
			if (before.charAt(i) == es1 || before.charAt(i) == es2) {
				after.append(esSymbol);
				after.append(String.valueOf(before.charAt(i)));
				continue;
			}
			after.append(String.valueOf(before.charAt(i)));
		}
		return after.toString();
	}
*/
}
