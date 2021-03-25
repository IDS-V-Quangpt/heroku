package jp.co.hyas.hpf.database;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerLoginBean implements Serializable {

	private String owner_user_name;  // オーナーユーザー名

	private String password;         // パスワード

}
