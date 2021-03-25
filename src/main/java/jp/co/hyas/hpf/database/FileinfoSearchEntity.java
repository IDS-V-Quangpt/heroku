package jp.co.hyas.hpf.database;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jp.co.hyas.hpf.database.base.BaseSearchEntityInterface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class FileinfoSearchEntity implements Serializable, BaseSearchEntityInterface {

	private String fileinfo_id;                  // ファイル情報管理ID(ナチュラルキー)

	private String corporation_id;               // 加盟店ID

	private String key_id;                       // キーID

	private String object_key;                   // オブジェクトキー

	private String kind_tag;                     // 情報種別タグ

	private String filename;                     // ファイル名

	private Integer scope;                       // アクセス範囲

	private String extension;                    // 拡張子

	private Integer size;                        // サイズ

	private String user_id;                      // アップロードユーザーID

	private Boolean delete_flg;                  // 削除フラグ

	private Integer file_size;

	private String tags;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created_at;                // 登録日時
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created_at_from;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date created_at_to;
}
