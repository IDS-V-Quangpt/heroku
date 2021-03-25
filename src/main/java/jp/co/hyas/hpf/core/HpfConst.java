package jp.co.hyas.hpf.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HpfConst {
	public static final List<String> PREFECTURES = new ArrayList<String>() {
		{
			add("北海道");
			add("青森県");
			add("岩手県");
			add("宮城県");
			add("秋田県");
			add("山形県");
			add("福島県");
			add("茨城県");
			add("栃木県");
			add("群馬県");
			add("埼玉県");
			add("千葉県");
			add("東京都");
			add("神奈川県");
			add("新潟県");
			add("富山県");
			add("石川県");
			add("福井県");
			add("山梨県");
			add("長野県");
			add("岐阜県");
			add("静岡県");
			add("愛知県");
			add("三重県");
			add("滋賀県");
			add("京都府");
			add("大阪府");
			add("兵庫県");
			add("奈良県");
			add("和歌山県");
			add("鳥取県");
			add("島根県");
			add("岡山県");
			add("広島県");
			add("山口県");
			add("徳島県");
			add("香川県");
			add("愛媛県");
			add("高知県");
			add("福岡県");
			add("佐賀県");
			add("長崎県");
			add("熊本県");
			add("大分県");
			add("宮崎県");
			add("鹿児島県");
			add("沖縄県");
		}
	};

	public static final List<String> SERVICE_ROLES = new ArrayList<String>() {
		{
			add("利用不可");
			add("利用可");
			add("管理者");
		}
	};

	public static final List<String> CONTACT_KINDS = new ArrayList<String>() {
		{
			add("加盟店");
			add("建築家");
			add("IC");
			add("HyAS");
			add("建築家／所員");
			
		}
	};

	public static final Map<String, String> HPF_PERMISSIONS = new LinkedHashMap<String, String>() {
		{
			put("member", "メンバー");
			put("admin", "マネージャー");
			put("owner", "オーナー");
		}
	};
	
	public static final Map<String, String> ROL_OF_COMMODITY = new LinkedHashMap<String, String>() {
		{
			put("利用可", "利用可");
			put("管理者", "管理者");
		}
	};
}
