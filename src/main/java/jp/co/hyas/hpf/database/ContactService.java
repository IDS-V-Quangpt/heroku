package jp.co.hyas.hpf.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
/*
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
*/
import org.springframework.stereotype.Service;

import jp.co.hyas.hpf.database.base.AppConst;
import jp.co.hyas.hpf.database.base.BaseService;
import jp.co.hyas.hpf.database.base.Util;
import xyz.downgoon.snowflake.Snowflake;

@Service
public class ContactService extends BaseService<Contact, ContactSearchEntity, ContactRepository> {
//public class ContactService {
	@Autowired
	ContactRepository repository;

	@Autowired
	AccountRepository accountRepository;

	public ContactRepository getRepository(){
		return this.repository;
	}
	public Class<Contact> getEntityClass(){
		return Contact.class;
	}
	public void presetEntityOnCrud(String crud, String ident, Contact t, Map<String, Object> hs) {
		// 登録時と更新時にSFIDを自動設定
		if (t != null && "insert".equals(crud)) {
			// contact.name に contact.lastname+全角空白+contact.firstname を入れる
			if (t.getName() == null || "".equals(t.getName())) {
				t.setName(Util.toString(t.getLastname()) + "　" + Util.toString(t.getFirstname()));
			}
			if (t.getCorporationid__c() != null && t.getCorporationid__c().length() > 0) {
				Account account = accountRepository.findOne(t.getCorporationid__c());
				if (account != null) {
					t.setAccountid(account.getSfid());
				}
			}
			if (t.getIsdeleted() == null) {
				t.setIsdeleted(false);
			}
			// created_by_service => appCode
			if (appCode != null) {
				t.setCreated_by_service__c(appCode);
				t.setLast_modified_by_service__c(appCode);
			}
			// systemmodstamp => NOW
			//t.setSystemmodstamp(Util.currentTimestamp());
		}
		else if (hs != null && t != null && "update".equals(crud)) {
			// contact.name に contact.lastname+全角空白+contact.firstname を入れる
			//if (t.getName() == null || "".equals(t.getName())) {
				t.setName(Util.toString(t.getLastname()) + "　" + Util.toString(t.getFirstname()));
				hs.put("name", t.getName());
			//}
			String corporation_id = (String) hs.get("corporationid__c");
			if (corporation_id != null && corporation_id.length() > 0) {
				Account account = accountRepository.findOne(t.getCorporationid__c());
				if (account != null) {
					// 追加項目として認識させるために必要
					hs.put("accountid", account.getSfid());
					// 実際に登録される値を参照させるために必要
					t.setAccountid(account.getSfid());
				}
			}
			// last_modified_by_service => appCode
			if (appCode != null) {
				hs.put("last_modified_by_service__c", appCode);
				t.setLast_modified_by_service__c(appCode);
			}
			// systemmodstamp => NOW
			//Timestamp now = Util.currentTimestamp();
			//t.setSystemmodstamp(now);
			//hs.put("systemmodstamp", now);
		}
	}

	// DB登録実施後処理
	public void postDbExecute(String crud, String ident, Contact t, Map<String, Object> hs) {}

	// ID生成
	// datacenter: AppConst.IDG_MACHINEID; workerId: 2 // 0-31
	static Snowflake snowflake = new Snowflake(AppConst.IDG_MACHINEID, 2);
	@Override
	public String generateId() {
		return "CON" + snowflake.nextId(); //return "C" + super.generateId();
	}

	// ================================
	//  担当者一覧画面 テンプレート用
	// ================================
	public boolean hasPermit(String permit, Contact t, Map<String, Object> userInfo)
	{
		boolean isSelfContact = false;		// 自分自身のデータかどうか？
		boolean isOwner = false;		// 自分がオーナーかどうか？
		boolean isHigherPerm = false;		// 自分の方がContact tより上位権限かどうか？

		// 自分自身のデータかどうか？
		isSelfContact = false;
		String contact_id = (String) userInfo.get("contactid");
		if (contact_id != null && contact_id.equals(t.getContact_id__c())) {
			isSelfContact = true;
		}


		// 自分がオーナーかどうか？
		isOwner = false;
		if ("owner".equals(userInfo.get("hpf_admin"))) {
			isOwner = true;
		}

		// 自分が上位権限かどうか？
		isHigherPerm = false;
		// member < admin < owner
		List<String> levellst = new ArrayList<>(Arrays.asList("member", "admin", "owner"));
		int selfLV = levellst.indexOf(userInfo.get("hpf_admin"));
		int destLV = levellst.indexOf(t.getHpf_permission__c());
		if (selfLV > destLV) {
			isHigherPerm = true;
		}

		// --- 削除
		if ("delete".equals(permit)) {
			// 自分自身のデータは削除不可
			if (isSelfContact) return false;
			// 担当者削除
			// Owner:  全て可能(自分自身はNG)
			// Admin:  Memberのみ可能
			// Member:  不可
			// 自分が上位権限の場合はデータ削除権限設定可能
			if (isHigherPerm) return true;
			return false;
		}

		// --- 編集
		if ("edit".equals(permit)) {
			// 自分自身のデータは編集可
			if (isSelfContact) return true;
			// 自分が上位権限の場合は編集可
			//if (isHigherPerm) return true;
			
			// 自分が上位権限の場合(owner > admin > member) || 自分がadmin && admin権限のデータが編集可 
			if ((isHigherPerm) || (("admin".equals(userInfo.get("hpf_admin"))) && (selfLV == destLV))) return true;

			return false;
		}

		// --- 権限部分の編集
		if ("edit_perm".equals(permit)) {
			// owner は編集可
			if (isOwner) return true;
			
			// 自分自身のデータは編集不可
			//if (isSelfContact) return false;
			// 自分が上位権限の場合は編集可
			//if (isHigherPerm) return true;
			
			// 自分が上位権限の場合(owner > admin > member) || 自分がadmin && admin権限のデータが編集可 
			if ((isHigherPerm) || (("admin".equals(userInfo.get("hpf_admin"))) && (selfLV == destLV))) return true;
			
			return false;
		}

		// --- 退職
		if ("retire".equals(permit)) {
			// 自分自身のデータは編集不可
			if (isSelfContact) return false;
			// owner は編集可
			if (isOwner) return true;
			return false;
		}

		return false;
	}
/*
	// 上記関数にまとめる
	public boolean isContactDelete(Contact t, Map<String, Object> userInfo)
	{
		// 自分自身のデータは削除不可
		String contact_id = (String) userInfo.get("contactid");
		if (contact_id != null && contact_id.equals(t.getContact_id__c())) {
			return false;
		}

		// 担当者削除
		// Owner:  全て可能(自分自身はNG)
		// Admin:  Memberのみ可能
		// Member:  不可

		// データ削除権限設定
		if ("owner".equals(userInfo.get("hpf_admin"))) {
			return true;
		} else if ("admin".equals(userInfo.get("hpf_admin"))) {
			if ("member".equals(t.getHpf_permission__c())) {
				return true;
			}
		}
		return false;
	}

	public boolean isContactEdit(Contact t, Map<String, Object> userInfo)
	{
		// 自分自身のデータは編集可
		String contact_id = (String) userInfo.get("contactid");
		if (contact_id != null && contact_id.equals(t.getContact_id__c())) {
			return true;
		}

		// owner または admin は編集可
		if ("owner".equals(userInfo.get("hpf_admin")) || "admin".equals(userInfo.get("hpf_admin"))) {
			return true;
		}

		return false;
	}
*/
	//担当者件数取得
	public long count(ContactSearchEntity entity){
		
		long contactCount = repository.count(entity);
		
		return contactCount;
	}
}
