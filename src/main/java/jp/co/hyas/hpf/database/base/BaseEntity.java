package jp.co.hyas.hpf.database.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
*/

@JsonIgnoreProperties(ignoreUnknown=true, value={ "id" })
abstract public class BaseEntity {
	// ナチュラルキーの値を設定
	abstract public void setNaturalKey(String ident);
	// ナチュラルキーの値を取得
	abstract public String getNaturalKey();
	// 論理削除フラグを設定
	abstract public void setLogicalDeleteFlag(boolean is_del);

}
