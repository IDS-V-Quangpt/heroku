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
public class UserService extends BaseService<User, BaseSearchEntity, UserRepository> {
//public class UserService {
	@Autowired
	UserRepository repository;
	public UserRepository getRepository() {
		return this.repository;
	}
	public Class<User> getEntityClass() {
		return User.class;
	}
	public void presetEntityOnCrud(String crud, String ident, User t, Map<String, Object> hs) {}

	// DB登録実施後処理
	public void postDbExecute(String crud, String ident, User t, Map<String, Object> hs) {}

	// ID生成
	static Snowflake snowflake = new Snowflake(AppConst.IDG_MACHINEID, 1);
	@Override
	public String generateId() {
		return "USR" + snowflake.nextId(); //return "A" + super.generateId();
	}
}
