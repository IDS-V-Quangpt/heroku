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
import jp.co.hyas.hpf.database.base.BaseSearchEntity;
import jp.co.hyas.hpf.database.base.BaseService;
import xyz.downgoon.snowflake.Snowflake;

@Service
public class AccountWaitService extends BaseService<AccountWait, BaseSearchEntity, AccountWaitRepository> {
//public class AccountService {
	@Autowired
	AccountWaitRepository repository;
	public AccountWaitRepository getRepository() {
		return this.repository;
	}
	public Class<AccountWait> getEntityClass() {
		return AccountWait.class;
	}
	@Override
	public void presetEntityOnCrud(String crud, String ident, AccountWait t, Map<String, Object> hs) {
		// 登録時と更新時に更新日時を自動設定
		//if (t != null && ("insert".equals(crud) || "update".equals(crud))) {
			//Timestamp now = Util.currentTimestamp();
			//t.setSystemmodstamp(now);
			//t.setLastmodifieddate(now);
			//if (hs != null) hs.put("systemmodstamp", now);
			//if (hs != null) hs.put("lastmodifieddate", now);
			//if ("insert".equals(crud)) {
			//	t.setCreateddate(now);
			//	if (hs != null) hs.put("createddate", now);
			//}
		//}
	}

	// DB登録実施後処理
	@Override
	public void postDbExecute(String crud, String ident, AccountWait t, Map<String, Object> hs) {}

	// ID生成
	static Snowflake snowflake = new Snowflake(AppConst.IDG_MACHINEID, 1);
	@Override
	public String generateId() {
		return "AWA" + snowflake.nextId();
	}

	public AccountWait findOneEnabled(String corporationid__c) {
		return repository.findOneEnabled(corporationid__c);
	}
}
