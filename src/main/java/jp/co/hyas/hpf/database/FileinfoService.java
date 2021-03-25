package jp.co.hyas.hpf.database;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
/*
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
*/
import org.springframework.stereotype.Service;

import jp.co.hyas.hpf.database.base.AppConst;
import jp.co.hyas.hpf.database.base.BaseService;
import jp.co.hyas.hpf.database.base.Util;
import xyz.downgoon.snowflake.Snowflake;

@Service
public class FileinfoService extends BaseService<Fileinfo, FileinfoSearchEntity, FileinfoRepository> {
	@Autowired
	FileinfoRepository repository;

	public FileinfoRepository getRepository(){
		return this.repository;
	}

	public Class<Fileinfo> getEntityClass(){
		return Fileinfo.class;
	}

	public void presetEntityOnCrud(String crud, String ident, Fileinfo t, Map<String, Object> hs) {
		// 登録時と更新時に更新日時を自動設定
		if (t != null && ("insert".equals(crud) || "update".equals(crud))) {
			Timestamp now = Util.currentTimestamp();
			t.setUpdated_at(now);
			if (hs != null) hs.put("updated_at", now);
			if ("insert".equals(crud)) {
				t.setCreated_at(now);
				if (hs != null) hs.put("created_at", now);
			}
		}
	}

	// DB登録実施後処理
	public void postDbExecute(String crud, String ident, Fileinfo t, Map<String, Object> hs) {}

	public Page<Fileinfo> search(FileinfoSearchEntity criteria, Pageable pageable) {
		String[] tags = {};
		if (criteria.getTags() != null) {
			tags = criteria.getTags().split(",", 0);
		}
		long total = repository.count(criteria, tags);
		List<Fileinfo> list = Collections.emptyList();
		if (total > 0) {
			list = repository.search(criteria, tags, pageable);
		}
		return new PageImpl<Fileinfo>(list, pageable, total);
	}

	// ID生成
	// datacenter: AppConst.IDG_MACHINEID; workerId: 12 // 0-31
	static Snowflake snowflake = new Snowflake(AppConst.IDG_MACHINEID, 12);

	@Override
	public String generateId() {
		return "F" + snowflake.nextId(); //return "F" + super.generateId();
	}

}
