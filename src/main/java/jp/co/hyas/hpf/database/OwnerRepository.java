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
public interface OwnerRepository extends BaseRepository<Owner, OwnerSearchEntity> {
//public interface OwnerRepository {
	final static String TABLE = "salesforce.hpf_owner__c";
	final static String KEYCOL = "owner_id__c";
	final static String DELCOL = "is_service_delete__c";

	// salesforceのsfidでの取得
	@Select("SELECT * FROM " + TABLE + " WHERE sfid = #{sfid} ORDER BY id")
	public Map<String, Object> findOneBySfId(@Param("sfid") String sfid);

	@Select("SELECT * FROM " + TABLE + " ORDER BY id LIMIT 500")
	public List<Owner> findAll();

	public long count(@Param("criteria") OwnerSearchEntity criteria);

	public List<Owner> search(
			@Param("criteria") OwnerSearchEntity criteria,
			@Param("pageable") Pageable pageable);

	//@Select("SELECT * FROM " + TABLE + " WHERE " + KEYCOL + " = #{ident} ORDER BY id")
	//@Select("SELECT * FROM " + TABLE + " WHERE " + KEYCOL + " = #{ident} AND " + DELCOL + " = false ORDER BY id")
	public Owner findOne(@Param("ident") String ident);

	// 認証用
	public Owner authenticate(@Param("auth") OwnerLoginBean auth);

	// 重複チェック用
	public boolean exists(@Param("criteria") Map<String, Object> criteria, @Param("except") String except);

	@InsertProvider(type=SqlProvider.class, method="insertSql")
	@SelectKey(statement="SELECT LASTVAL()", keyProperty="record.id", before=false, resultType=int.class)
	public int insert(@Param("record") Owner owner);

	@UpdateProvider(type=SqlProvider.class, method="updateSql")
	public int update(@Param("ident") String ident, @Param("record") Owner owner);

	@UpdateProvider(type=SqlProvider.class, method="updatePartialSql")
	public int updatePartial(@Param("ident") String ident, @Param("record") Owner owner, @Param("map") Map<String, Object> hs);

	//@Delete("DELETE FROM " + TABLE + " WHERE " + KEYCOL + " = #{ident}")
	@Delete("UPDATE " + TABLE + " SET " + DELCOL + " = true WHERE " + KEYCOL + " = #{ident}")
	public int delete(String ident);

	static class SqlProvider extends SQL {

		public String insertSql(HashMap<String,Object> param) {
			return Util.insertSql(TABLE,COLS);
		}
		public String updateSql(HashMap<String,Object> param) {
			return Util.updateSql(TABLE, KEYCOL, COLS, null);
		}
		public String updatePartialSql(HashMap<String,Object> param) {
			@SuppressWarnings("unchecked")
			HashMap<String,Object> map = (HashMap<String,Object>)param.get("map");
			return Util.updateSql(TABLE, KEYCOL, COLS, map);
		}
		final static String[] COLS = {
			"owner_id__c",                   // オーナーID
			"corporation_id__c",             // 加盟店ID
			"account_id__c",                 // 加盟店ID(SFID)
			"last_name__c",                  // オーナー姓
			"first_name__c",                 // オーナー名
			"last_name_kana__c",             // オーナー姓カナ
			"first_name_kana__c",            // オーナー名カナ
			"owner_company_name__c",         // オーナー法人名
			"owner_company_name_kana__c",    // オーナー法人名カナ
			"birthdate__c",                  // 生年月日
			"email1__c",                     // Email1
			"email2__c",                     // Email2
			"tel1__c",                       // 電話番号1
			"tel2__c",                       // 電話番号2
			"fax__c",                        // FAX
			"postal_code__c",                // 郵便番号
			"prefecture__c",                 // 都道府県
			"city__c",                       // 市町村名
			"street__c",                     // 町名・地番など
			"building_name__c",              // ビル・マンション名
			"address_kana__c",               // 住所カナ
			"owner_type__c",                 // オーナー種別
			"owner_rank__c",                 // オーナーランク
			"sex__c",                        // 性別
			"job__c",                        // 職業
			"income__c",                     // 所得
			"representative__c",             // 代表者名
			"representative_kana__c",        // 代表者名カナ
			"industry__c",                   // 業種
			"capital__c",                    // 資本金
			"annual_revenue__c",             // 売上高
			"branch__c",                     // 事業所
			"number_of_employees__c",        // 従業員数
			"login_name__c",                 // ログインユーザー名
			"login_password__c",             // ログインパスワード
			"is_service_delete__c",          // サービス削除フラグ
			// "id",                            // ID
			"sfid",                          // SFID
			"createdbyid",                   // 作成者ユーザーID
			"lastmodifiedbyid",              // 更新者ユーザーID
			"createddate",                   // 作成日
			"systemmodstamp",                // システム最終更新日
			"created_by_service__c",            // データ作成サービス
			"last_modified_by_service__c",      // データ更新サービス
			"isdeleted",                     // 削除フラグ
			"name",                          // オーナー情報名
			null
		};
	}
}
