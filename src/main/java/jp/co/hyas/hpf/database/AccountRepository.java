package jp.co.hyas.hpf.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.data.domain.Pageable;

import jp.co.hyas.hpf.database.base.BaseRepository;
import jp.co.hyas.hpf.database.base.Util;

@Mapper
public interface AccountRepository extends BaseRepository<Account, AccountSearchEntity> {
//public interface AccountRepository {
	final static String TABLE = "salesforce.account";
	final static String KEYCOL = "corporationid__c";
	final static String DELCOL = "is_service_delete__c";

	// salesforceのsfidでの取得
	@Select("SELECT * FROM " + TABLE + " WHERE sfid = #{sfid} ORDER BY id")
	public Map<String, Object> findOneBySfId(@Param("sfid") String sfid);

	// salesforceのcorporationid__cでの取得
	@Select("SELECT * FROM " + TABLE + " WHERE corporationid__c = #{corporationid__c} ORDER BY id")
	public Map<String, Object> findOneBySfCorporationId(@Param("corporationid__c") String corporationid__c);
	
	@Select("SELECT * FROM " + TABLE + " WHERE sfid = #{sfid} ORDER BY id")
	public Account findOneEntityBySfId(@Param("sfid") String sfid);

	@Select("SELECT * FROM " + TABLE + " ORDER BY id LIMIT 500")
	public List<Account> findAll();

	public long count(@Param("criteria") AccountSearchEntity criteria);

	public List<Account> search(
			@Param("criteria") AccountSearchEntity criteria,
			@Param("pageable") Pageable pageable);

	//@Select("SELECT * FROM " + TABLE + " WHERE " + KEYCOL + " = #{ident} ORDER BY id")
	@Select("SELECT * FROM " + TABLE + " WHERE " + KEYCOL + " = #{ident} AND " + DELCOL + " = false ORDER BY id")
	public Account findOne(@Param("ident") String ident);

	@InsertProvider(type=SqlProvider.class, method="insertSql")
	@SelectKey(statement="SELECT LASTVAL()", keyProperty="record.id", before=false, resultType=int.class)
	public int insert(@Param("record") Account account);

	@UpdateProvider(type=SqlProvider.class, method="updateSql")
	public int update(@Param("ident") String ident, @Param("record") Account account);

	@UpdateProvider(type=SqlProvider.class, method="updatePartialSql")
	public int updatePartial(@Param("ident") String ident, @Param("record") Account account, @Param("map") Map<String, Object> hs);

	//@Delete("DELETE FROM " + TABLE + " WHERE " + KEYCOL + " = #{ident}")
	@Delete("UPDATE " + TABLE + " SET " + DELCOL + " = true WHERE " + KEYCOL + " = #{ident}")
	public int delete(String ident);

	static class SqlProvider extends SQL {

		public String insertSql(HashMap<String,Object> param) {
			return Util.insertSql(TABLE,COLS);
		}
		public String updateSql(HashMap<String,Object> param) {
			return Util.updateSql(TABLE,KEYCOL,COLS,null);
		}
		public String updatePartialSql(HashMap<String,Object> param) {
			@SuppressWarnings("unchecked")
			HashMap<String,Object> map = (HashMap<String,Object>)param.get("map");
			return Util.updateSql(TABLE,KEYCOL,COLS, map);
		}

		final static String[] COLS = {
			"corporationid__c",              // 加盟店ID
			"name",                          // 加盟店名
			"namekana__c",                   // 加盟店名カナ
			"billingpostalcode",             // 郵便番号
			"billingstate",                  // 都道府県
			"billingcity",                   // 市町村
			"billingstreet",                 // 町名・地番
			"buildingname__c",               // 建物名
			"phone",                         // 電話番号
			"phoneng__c",                    // TEL NG
			"fax",                           // FAX
			"faxng__c",                      // FAX NG
			"email__c",                      // E-Mail
			"emailng__c",                    // Email NG
			"website",                       // サイトURL
			"parentid",                      // 親法人ID
			"headoffice__c",                 // 本店ID
			"foraccountingnotext__c",        // 経理用No.
			"shopnametext__c",               // 店舗名
			"shopkanatext__c",               // 店舗名カナ
			"branchofficename__c",           // 支店名
			"branchofficekana__c",           // 支店名カナ
			"contactng__c",                  // 接触NG
			"alliancearchitect__c",          // 提携建築家
			"affiliates__c",                 // 提携法人
			"notpayment__c",                 // 未入金
			"bankruptcy__c",                 // 倒産
			"is_available_r_de_go__c",       // RでGo契約
			"is_available_cms__c",           // CMS契約
			"is_available_pms__c",           // PMS契約
			"is_available_iekachi__c",       // iekachi BOX契約
			"architrend_rights__c",          // アーキトレンドCAD権利会員
			"data_migration_id__c",          // データ移行ID
			"ownerid",                       // 法人 所有者
			"is_service_delete__c",          // サービス削除フラグ
			// "id",                            // ID
			"sfid",                          // SFID
			"createdbyid",                   // 作成者ユーザーID
			"lastmodifiedbyid",              // 更新者ユーザーID
			"createddate",                   // 作成日
			"systemmodstamp",                // システム最終更新日
			"created_by_service__c",            // データ作成サービス
			"last_modified_by_service__c",      // データ更新サービス
			"isdeleted",                     // 削除
			null
		};

/*
		public String insertSql2(Account account) {
			Util.insertSql(TABLE,COLS);
			INSERT_INTO(TABLE);
			Arrays.stream(COLS).forEach(col -> {
				if (col == null) return;
				VALUES("\"" + col + "\"", "#{" + col + "}");
			});

	        Logger logger = LoggerFactory.getLogger(AccountRepository.class);
	        logger.info("SQL:" + toString());
			return toString();
		}
		public String updateSql2(HashMap<String,Object> param) {
			UPDATE(TABLE);
			Arrays.stream(COLS).forEach(col -> {
				if (col == null) return;
				SET(String.format("\"%s\" = #{record.%s}", col, col));
			});
			WHERE("id = #{ident}");

			Logger logger = LoggerFactory.getLogger(AccountRepository.class);
			logger.info("SQL:" + toString());
			return toString();
		}
*/
/*
		public String updateSql(@Param("ident") String ident, @Param("account") Account account) {
			UPDATE("salesforce.account");
			Arrays.stream(COLS).forEach(col -> {
				if (col == null) return;
				SET(String.format("\"%s\" = #{%s}", col, col));
			});
			WHERE("id = #{ident}");

	        Logger logger = LoggerFactory.getLogger(AccountRepository.class);
	        logger.info("SQL:" + toString());
			return toString();
		}
*/
	}

/*
	@Select("SELECT * FROM salesforce.account ORDER BY id")
	public List<Account> findAll();

	@SelectProvider(type = SqlProvider.class, method="findBy")
	public List<Account> findBy(Map<String, String> conditions);

	@Select("SELECT * FROM salesforce.account WHERE corporationid__c LIKE '%${corporationid}%' ORDER BY id LIMIT 500")
	public List<Account> findLike(@Param("corporationid") String corporationid);

	//@SelectProvider(type=SqlProvider.class, method="countSql")
	public long count(@Param("criteria") Map<String, String> criteria);

	//@SelectProvider(type=SqlProvider.class, method="searchSql")
	public List<Account> search(
			@Param("criteria") Map<String, String> criteria,
			@Param("pageable") Pageable pageable);

	@Insert("INSERT INTO salesforce.account (corporationid__c) VALUES(#{corporationid__c})")
	public void insert(Account account);

	@SelectProvider(type = SqlProvider.class, method="findMultiLikes")
	public List<Account> findLikes(@Param("conditions") Map<String, String> conditions);

	static class SqlProvider extends SQL {
		public String findBy(@Param("corporationid") String corporationid) {
			SELECT("*");
			FROM("salesforce.account");
			if (corporationid != null) {
				WHERE("corporationid__c LIKE '%${corporationid}%'");
			}
			return toString();
		}
		//public String findMultiLikes(@Param("conditions") Map<String, Map<String, String>> param) {
		//	return this.rawFindMultiLikes(mapLikeEscape(param.get("conditions"),"%","%"));
		//}

		final static String[] ALLOW_COLS =
			{ "name", "namekana__c", "billingstate", "corporationid__c" };
		// LIKE検索のための内部関数
		public String findMultiLikes(@Param("conditions") Map<String, Map<String, String>> param) {
		//public String rawFindMultiLikes(Map<String, String> conditions) {
			List<String> valid = Arrays.asList(ALLOW_COLS);
			Map<String, String> conditions = param.get("conditions");
			SELECT("*");
			FROM("salesforce.account");
			conditions.forEach((col,val) -> {
				if (valid.indexOf(col) < 0) return;
				WHERE(col + " LIKE #{conditions."+col+"}");
			});
	        Logger logger = LoggerFactory.getLogger(AccountRepository.class);
	        logger.info("conditions:" + conditions.toString());
	        logger.info("rawFindMultiLikes:" + toString());
			return toString() + " LIMIT 500";
		}

		public String countSql(@Param("criteria") Map<String, String> criteria) {
	        SELECT("COUNT(*)");
			FROM("salesforce.account");
			applyWhere(criteria);

	        return toString();
		}

		public String searchSql(
				@Param("criteria") Map<String, String> criteria,
				@Param("pageable") Pageable pageable) {
	        SELECT("*");
			FROM("salesforce.account");
			applyWhere(criteria);
			for (Sort.Order order : pageable.getSort()) {
				// XXX: Be validate property .
				ORDER_BY(order.getProperty() + " " + order.getDirection());
			}

			String sql = toString();
			sql += String.format(" LIMIT %d OFFSET %d", pageable.getPageSize(), pageable.getOffset());

	        return sql;
		}

		private void applyWhere(@Param("criteria") Map<String, String> criteria) {
	        Logger logger = LoggerFactory.getLogger(AccountRepository.class);
	        logger.info("criteria:" + criteria.toString());

	        if (criteria.containsKey("name")) {
				//WHERE("name = #{criteria.name}");
	        	criteria.put("name", likeEscape(criteria.get("name")));
				WHERE("name LIKE '%' || #{criteria.name} || '%'");
			}
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
    }
*/
}
