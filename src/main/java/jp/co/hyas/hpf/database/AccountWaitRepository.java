package jp.co.hyas.hpf.database;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.jdbc.SQL;

import jp.co.hyas.hpf.database.base.BaseRepository;
import jp.co.hyas.hpf.database.base.BaseSearchEntity;
import jp.co.hyas.hpf.database.base.Util;

@Mapper
public interface AccountWaitRepository extends BaseRepository<AccountWait, BaseSearchEntity> {
//public interface AccountRepository {
	final static String TABLE = "salesforce.account_wait__c";
	final static String KEYCOL = "corporation_wait_id__c";


	@Select("SELECT * FROM " + TABLE + " WHERE corporationid__c = #{corporationid__c} AND is_merge_completion__c = false ORDER BY id")
	public AccountWait findOneEnabled(@Param("corporationid__c") String corporationid__c);


	@InsertProvider(type=SqlProvider.class, method="insertSql")
	@SelectKey(statement="SELECT LASTVAL()", keyProperty="record.id", before=false, resultType=int.class)
	public int insert(@Param("record") AccountWait account);

	@UpdateProvider(type=SqlProvider.class, method="updateSql")
	public int update(@Param("ident") String ident, @Param("record") AccountWait account);

	@UpdateProvider(type=SqlProvider.class, method="updatePartialSql")
	public int updatePartial(@Param("ident") String ident, @Param("record") AccountWait account, @Param("map") Map<String, Object> hs);


	static class SqlProvider extends SQL {

		public String insertSql(HashMap<String,Object> param) {
			return Util.insertSql(TABLE, COLS);
		}
		public String updateSql(HashMap<String,Object> param) {
			return Util.updateSql(TABLE, KEYCOL, COLS, null);
		}
		public String updatePartialSql(HashMap<String,Object> param) {
			@SuppressWarnings("unchecked")
			HashMap<String,Object> map = (HashMap<String,Object>)param.get("map");
			return Util.updateSql(TABLE, KEYCOL ,COLS, map);
		}

		final static String[] COLS = {
			"corporation_wait_id__c",  // 加盟店待機ID
			"corporationid__c",  // 加盟店ID
			"account_id__c",  // 加盟店ID(SFID)
			"name__c",  // 加盟店名
			"name_kana__c",  // 加盟店名カナ
			"billingpostalcode__c",  // 郵便番号
			"billingstate__c",  // 都道府県
			"billingcity__c",  // 市町村
			"billingstreet__c",  // 町名・地番
			"buildingname__c",  // 建物名
			"email__c",  // E-Mail
			"phone__c",  // 電話番号
			"fax__c",  // FAX
			"website__c",  // サイトURL
			"is_merge_completion__c",  // マージ完了フラグ
			"is_service_delete__c",  // サービス削除フラグ
			// "id",  // ID
			"sfid",  // SFID
			"createdbyid",  // 作成者ユーザーID
			"lastmodifiedbyid",  // 更新者ユーザーID
			"createddate",  // 作成日
			"systemmodstamp",  // システム最終更新日
			"isdeleted",  // 削除フラグ
			"name",  // 法人待機情報名
			null
		};
	}
}
