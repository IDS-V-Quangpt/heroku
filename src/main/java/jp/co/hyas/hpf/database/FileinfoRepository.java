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
public interface FileinfoRepository extends BaseRepository<Fileinfo, FileinfoSearchEntity> {
	final static String TABLE = "hpf.fileinfo";
	final static String KEYCOL = "fileinfo_id";
	final static String DELCOL = "delete_flg";

	@Select("SELECT * FROM " + TABLE + " ORDER BY id LIMIT 500")
	public List<Fileinfo> findAll();

	public long count(@Param("criteria") FileinfoSearchEntity criteria, @Param("tags") String[] tags);

	public List<Fileinfo> search(
			@Param("criteria") FileinfoSearchEntity criteria,
			@Param("tags") String[] tags,
			@Param("pageable") Pageable pageable);

	//@Select("SELECT * FROM " + TABLE + " WHERE " + KEYCOL + " = #{ident} AND " + DELCOL + " = false ORDER BY id")
	public Fileinfo findOne(@Param("ident") String ident);

	@InsertProvider(type=SqlProvider.class, method="insertSql")
	@SelectKey(statement="SELECT LASTVAL()", keyProperty="record.id", before=false, resultType=int.class)
	public int insert(@Param("record") Fileinfo fileinfo);

	@UpdateProvider(type=SqlProvider.class, method="updateSql")
	public int update(@Param("ident") String ident, @Param("record") Fileinfo fileinfo);

	@UpdateProvider(type=SqlProvider.class, method="updatePartialSql")
	public int updatePartial(@Param("ident") String ident, @Param("record") Fileinfo fileinfo, @Param("map") Map<String, Object> hs);

	@Delete("UPDATE " + TABLE + " SET " + DELCOL + " = true WHERE " + KEYCOL + " = #{ident}")
	public int delete(String ident);

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
			return Util.updateSql(TABLE, KEYCOL, COLS, map);
		}

		final static String[] COLS = {
			//"id",                         // 内部ID(主キー)
			"fileinfo_id",                // ファイル情報管理ID(ナチュラルキー)
			"corporation_id",             // 加盟店ID
			"key_id",                     // キーID
			"object_key",                 // オブジェクトキー
			"kind_tag",                   // 情報種別タグ
			"filename",                   // ファイル名
			"tag_01",                     // タグ1
			"tag_02",                     // タグ2
			"tag_03",                     // タグ3
			"tag_04",                     // タグ4
			"tag_05",                     // タグ5
			"tag_06",                     // タグ6
			"tag_07",                     // タグ7
			"tag_08",                     // タグ8
			"tag_09",                     // タグ9
			"tag_10",                     // タグ10
			"scope",                      // アクセス範囲
			"extension",                  // 拡張子
			"size",                       // サイズ
			"user_id",                    // アップロードユーザーID
			"created_at",                 // 登録日時
			"updated_at",                 // 更新日時
			"delete_flg",                 // 削除フラグ
			null
		};

	}
}
