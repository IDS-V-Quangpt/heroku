package jp.co.hyas.hpf.database;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jp.co.hyas.hpf.database.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class Fileinfo extends BaseEntity implements Serializable {
	// ナチュラルキーの値を設定
	public void setNaturalKey(String ident) {
		this.fileinfo_id = ident;  // ファイル情報管理ID
	}

	// ナチュラルキーの値を取得
	@JsonIgnore
	public String getNaturalKey()
	{
		return this.getFileinfo_id();
	}

	// 論理削除フラグを設定
	public void setLogicalDeleteFlag(boolean is_del) {
		delete_flg = is_del;  // 削除フラグ
	}

	// カラム

	private Integer id;                          // 内部ID(主キー)

	@Size(max=30)
	private String fileinfo_id;                  // ファイル情報管理ID(ナチュラルキー)

	@Size(max=30)
	private String corporation_id;               // 加盟店ID

	@Size(max=30)
	private String key_id;                       // キーID

	@Size(max=255)
	private String object_key;                   // オブジェクトキー

	@Size(max=255)
	@Pattern(regexp="^加盟店|オーナー|物件|案件|$")
	private String kind_tag;                     // 情報種別タグ

	@Size(max=255)
	private String filename;                     // ファイル名

	@Size(max=255)
	private String tag_01;                       // タグ1

	@Size(max=255)
	private String tag_02;                       // タグ2

	@Size(max=255)
	private String tag_03;                       // タグ3

	@Size(max=255)
	private String tag_04;                       // タグ4

	@Size(max=255)
	private String tag_05;                       // タグ5

	@Size(max=255)
	private String tag_06;                       // タグ6

	@Size(max=255)
	private String tag_07;                       // タグ7

	@Size(max=255)
	private String tag_08;                       // タグ8

	@Size(max=255)
	private String tag_09;                       // タグ9

	@Size(max=255)
	private String tag_10;                       // タグ10

	private Integer scope;                       // アクセス範囲

	@Size(max=255)
	private String extension;                    // 拡張子

	private Integer size;                        // サイズ

	@Size(max=30)
	private String user_id;                      // アップロードユーザーID

	private Timestamp created_at;                // 登録日時

	private Timestamp updated_at;                // 更新日時

	private Boolean delete_flg;                  // 削除フラグ

	// リレーション取得
	private HashMap<String, Object> corporation;
}
