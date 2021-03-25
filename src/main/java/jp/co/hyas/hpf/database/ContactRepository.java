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
public interface ContactRepository extends BaseRepository<Contact, ContactSearchEntity> {
//public interface ContactRepository {
	final static String TABLE = "salesforce.contact";
	final static String KEYCOL = "contact_id__c";
	final static String DELCOL = "is_service_delete__c";

	// salesforceのsfidでの取得
	@Select("SELECT * FROM " + TABLE + " WHERE sfid = #{sfid} ORDER BY id")
	public Map<String, Object> findOneBySfId(@Param("sfid") String sfid);

	@Select("SELECT * FROM " + TABLE + " ORDER BY id LIMIT 500")
	public List<Contact> findAll();

	public long count(@Param("criteria") ContactSearchEntity criteria);

	public List<Contact> search(
			@Param("criteria") ContactSearchEntity criteria,
			@Param("pageable") Pageable pageable);

	//@Select("SELECT * FROM " + TABLE + " WHERE " + KEYCOL + " = #{ident} ORDER BY id")
	//@Select("SELECT * FROM " + TABLE + " WHERE " + KEYCOL + " = #{ident} AND " + DELCOL + " = false ORDER BY id")
	public Contact findOne(@Param("ident") String ident);

	@InsertProvider(type=SqlProvider.class, method="insertSql")
	@SelectKey(statement="SELECT LASTVAL()", keyProperty="record.id", before=false, resultType=int.class)
	public int insert(@Param("record") Contact contact);

	@UpdateProvider(type=SqlProvider.class, method="updateSql")
	public int update(@Param("ident") String ident, @Param("record") Contact contact);

	@UpdateProvider(type=SqlProvider.class, method="updatePartialSql")
	public int updatePartial(@Param("ident") String ident, @Param("record") Contact contact, @Param("map") Map<String, Object> hs);

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
			"contact_id__c",                                 // HPF担当者ID (キー項目)
			"accountid",                                     // 加盟店ID(SFID)
			"corporationid__c",                              // 加盟店ID
			"hpf_permission__c",                             // HPF権限
			"contact_kind__c",                               // ユーザー種別
			"is_approval_check__c",                          // 要承認チェックフラグ
			"is_invalid__c",                                 // 無効化フラグ
			"invalid_reason__c",                             // 無効化コメント
			"mailingpostalcode",                             // 郵便番号(法人用)
			"mailingstate",                                  // 都道府県(法人用)
			"mailingcity",                                   // 市区郡(法人用)
			"mailingstreet",                                 // 町名・番地(法人用)
			"buildingname__c",                               // ビル・マンション名(法人用)
			"firstname",                                     // 名
			"lastname",                                      // 氏
			"firstnamekana__c",                              // 名カナ
			"lastnamekana__c",                               // 氏カナ
			"name",                                          // 名前
			"title",                                         // 役職
			"belongbranchoffice__c",                         // 所属支店
			"department",                                    // 部署
			"phone",                                         // TEL
			"donotcall",                                     // TEL NG
			"fax",                                           // Fax
			"hasoptedoutoffax",                              // FAX NG
			"email",                                         // Email
			"hasoptedoutofemail",                            // Email NG
			"mobilephone",                                   // 携帯TEL
			"mobilephoneng__c",                              // 携帯TEL NG
			"mobilemail__c",                                 // 携帯Email
			"mobilemailng__c",                               // 携帯Email NG
			"architect_office_url__c",                       // 建築家事務所URL
			"profile_movie_url__c",                          // 建築家プロフィール動画URL
			"profile_file_key__c",                           // 建築家プロフィールオブジェクトキー
			"representativename__c",                         // 代表者
			"retirement__c",                                 // 退職
			"plaza__c",                                      // PLAZA
			"plazamail__c",                                  // PLAZAメール
			"view__c",                                       // View
			"viewmail__c",                                   // Viewメール


			"needsmailmagazinrplushouse__c",                 // R+house
			"needsmailmagaziniekachiforrplusadm__c",         // 家価値（R+、ADM）
			"needsmailmagaziniekachiforippan__c",            // 家価値（一般）
			"needsmailmagaziniekachibox__c",                 // iekachiBOX
			"needsmailmagazinfp__c",                         // FP
			"needsmailmagazinfsm__c",                        // FSM
			"needsmailmagazinadm__c",                        // ADM
			"needsmailmagazinams__c",                        // AMS
			"needsmailmagazincms__c",                        // CMS
			"needsmailmagazinhysp__c",                       // HySP
			"needsmailmagazintochisma__c",                   // トチスマ
			"needsmailmagazintmd__c",                        // TMD
			"needsmailmagazingg__c",                         // GG
			"needsmailmagazinhc__c",                         // HC
			"needsmailmagazinhih__c",                        // HIH
			"needsmailmagazinws__c",                         // WS
			"needsmailmagazinstay__c",                       // STAY
			"needsmailmagazindecos__c",                      // デコス
			"needsmailmagazinpms__c",                        // PMS
			"needsmailmagazintm__c",                         // TM
			"needsmailmagazinrpluskenchikuka__c",            // R+建築家
			"needsmailmagazinrplusic__c",                    // R+IC


			"role_of_r_de_go__c",                            // RでGo利用権限
			"role_of_cms__c",                                // CMS利用権限
			"role_of_pms__c",                                // PMS利用権限
			"role_of_iekachi__c",                            // iekachi BOX利用権限
			"r_house_certificate_number__c",                 // R+house施工責任者修了証番号
			"r_house_construction_supervisor__c",            // R+house施工責任者区分
			"r_house_certificate_remove__c",                 // R+house施工責任者認定削除
			"data_migration_id__c",                          // データ移行ID
			"is_service_delete__c",                          // サービス削除フラグ
			// "id",                                         // ID
			"sfid",                                          // SFID
			"createdbyid",                                   // 作成者ユーザーID
			"lastmodifiedbyid",                              // 更新者ユーザーID
			"createddate",                                   // 作成日
			"systemmodstamp",                                // システム最終更新日
			"created_by_service__c",                         // データ作成サービス
			"last_modified_by_service__c",                   // データ更新サービス
			"isdeleted",                                     // 削除フラグ
			null
		};
	}
}
