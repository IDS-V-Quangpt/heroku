package jp.co.hyas.hpf.portal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.hyas.hpf.core.HpfConst;
import jp.co.hyas.hpf.core.forms.ContactForm;
import jp.co.hyas.hpf.database.Contact;
import jp.co.hyas.hpf.database.ContactSearchEntity;
import jp.co.hyas.hpf.database.ContactService;
import jp.co.hyas.hpf.database.User;
import jp.co.hyas.hpf.database.base.Util;


@Controller
@RequestMapping("portal/contact")
public class ContactController extends HpfBaseController {
	Logger logger = LoggerFactory.getLogger(ContactController.class);

	private String sessionAttributeName = "session_for_contact_edit";
	private String sessionListAttributeName = "session_for_contact_list";
	private String sessionModeAttributeName = "session_for_contact_mode";

	@Autowired
	private ContactService contactService;

	@RequestMapping("/")
	public String index() {
		return "redirect:/portal/contact/list";
	}

	@RequestMapping(value="/list", method = RequestMethod.GET)
	public String list_get(HttpServletRequest request, HttpSession session, Model model,
			@PageableDefault(size = 500, sort = { "id" }) Pageable pageable) {
		User user = getLoginUserBySession(session);
		Map<String, Object> userInfo = getLoginUserInoBySession(session);
		if (user == null || userInfo == null) {
			return redirectLoginPage;
		}
		setPermissionToModel(model, userInfo);

		ContactSearchEntity entity = new ContactSearchEntity();
		entity.setCorporationid__c((String) userInfo.getOrDefault("corporationid", null));
		entity.setIsdeleted(false);
		entity.setIs_service_delete__c(false);
		entity.setHpf_permission__c(new ArrayList<String>(HpfConst.HPF_PERMISSIONS.keySet()));
		//entity.setHpf_permission__c(new ArrayList<String>(HpfConst.HPF_PERMISSIONS.values()));

		Page<Contact> contacts = contactService.search(entity, pageable);
		// キーを設定
		String keycode = getKeyCode();
		session.setAttribute(sessionListAttributeName, keycode);

		// テンプレートへの値渡し
		model.addAttribute("user_info", userInfo);
		model.addAttribute("contacts", contacts);
		model.addAttribute("keycode", keycode);

		return "portal/contact/list";
	}

	@RequestMapping(value="/list", method = RequestMethod.POST)
	public String list_post(HttpServletRequest request, HttpSession session) {
		User user = getLoginUserBySession(session);
		if (user == null) {
			return redirectLoginPage;
		}

		// _key_codeの整合チェック
		String keycode = (String) session.getAttribute(sessionListAttributeName);
		if (!keycode.equals(request.getParameter("keycode"))) {
			// 不正リクエスト
			logger.warn("[{}] invalid keycode.", user.getSfid());
			return "redirect:/portal/contact/list";
		}

		String contact_id = request.getParameter("contact_id");
		if (StringUtils.isEmpty(contact_id)) {
			// 不正リクエスト
			logger.warn("[{}] contact id not found.", user.getSfid());
			return "redirect:/portal/contact/list";
		}

		Contact contact = contactService.findOne(contact_id);
		if (contact == null) {
			logger.warn("[{}] specified contact data not found.", user.getSfid());
			return "redirect:/portal/contact/list";
		}

		// 権限をOFF
		contact.setRole_of_cms__c("利用不可");
		contact.setRole_of_iekachi__c("利用不可");
		contact.setRole_of_pms__c("利用不可");
		contact.setRole_of_r_de_go__c("利用不可");
		// 無効化要求をON
		contact.setIs_invalid__c(true);

		contactService.update(contact_id, contact);

		contactService.delete(contact_id);

		return "redirect:/portal/contact/list";
	}

	@RequestMapping(value="/edit",method=RequestMethod.GET)
	public String edit_get(HttpServletRequest request, HttpSession session, Model model,
		@ModelAttribute ContactForm form, BindingResult bind) {
		return edit(request, session, model, form, bind);
	}
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	public String edit_post(HttpServletRequest request, HttpSession session, Model model,
			@ModelAttribute @Validated ContactForm form, BindingResult bind) {
		return edit(request, session, model, form, bind);
	}

	// GET/POST共通処理
	private String edit(HttpServletRequest request, HttpSession session, Model model, ContactForm form, BindingResult bind) {
		User user = getLoginUserBySession(session);
		Map<String, Object> userInfo = getLoginUserInoBySession(session);
		if (user == null || userInfo == null) {
			return redirectLoginPage;
		}
		setPermissionToModel(model, userInfo);
		String contact_id = null;
		Contact contact = new Contact();
		if ("POST".equals(request.getMethod())) {
			// エラーが無ければ確認画面へ
			if (!bind.hasErrors()) {
				// CSRF対策用のキーコードを生成
				form.set__key_code(getKeyCode());
				session.setAttribute(sessionAttributeName, form);
				return "redirect:/portal/contact/confirm";
			} else {
				contact_id = form.getContact_id__c();
				if (!StringUtils.isEmpty(contact_id)) {
					contact = contactService.findOne(contact_id);
					if (contact == null) {
						logger.warn("[{}] contact data not found.", user.getSfid());
						return redirectTopPage;
					}
				}
			}
		} else {
			// 遷移パターン
			if ("1".equals(request.getParameter("b"))) {
				// 確認画面からの戻り -> 入力項目を適用する
				ContactForm tmpForm = (ContactForm)session.getAttribute(sessionAttributeName);
				model.addAttribute("return", true);
				if (tmpForm == null) {
					// セッションからの復元に失敗
					logger.warn("[{}] ContactForm get failed from session.", user.getSfid());
					return "redirect:/portal/contact/edit";
				}
				// form は modelAttributeですでに参照が関連づいている
				// 参照を更新するのではなく値を上書きしないとテンプレートでth:objectの値に反映されない
				Util.copyProperties(tmpForm, form);
				contact_id = form.getContact_id__c();
				if (!StringUtils.isEmpty(contact_id)) {
					contact = contactService.findOne(contact_id);
					if (contact == null) {
						logger.warn("[{}] contact data not found.", user.getSfid());
						return redirectTopPage;
					}
				}
				model.addAttribute("limitFlg", request.getParameter("limitFlg"));

			} else {
				if ("1".equals(request.getParameter("p"))) {  // プロフィール編集
					// プロファイル編集は常にログイン者の担当者情報となる
					contact_id = (String)userInfo.getOrDefault("contactid", null);
					session.setAttribute(sessionModeAttributeName, "profile");
				} else {  // 担当者新規 or 編集
					contact_id = request.getParameter("c");
					session.setAttribute(sessionModeAttributeName, "contact");
				}
				if (contact_id != null) {
					contact = contactService.findOne(contact_id);
					if (contact == null) {
						logger.warn("[{}] contact data not found.", user.getSfid());
						return redirectTopPage;
					}
					Util.copyProperties(contact, form);
				}
			}
		}

		// th記述が長くなるので事前変数化：contactService.hasPermit('retire', contact, user_info)
		//Map<String, Boolean> is_ediable = new HashMap<String, Boolean>();
		//is_ediable.put("retire", contactService.hasPermit("retire", contact, userInfo)); // 退職編集
		//is_ediable.put("edit_perm", contactService.hasPermit("edit_perm", contact, userInfo)); // 権限編集
		Map<String, Boolean> is_ediable = getEditableFlags(contact, userInfo);
		model.addAttribute("is_ediable", is_ediable);

		// 補完
		// 加盟店ID: ログイン者の加盟店ID
		if (contact.getCorporationid__c() == null) contact.setCorporationid__c((String)userInfo.get("corporationid"));
		completionContactForm(form, userInfo);

/*
		// 自分より下位の権限のみを選択可能リストとする
		// member < admin < owner
		Map<String, String> editablePerms = new LinkedHashMap<String, String>();
		List<String> levellst = new ArrayList<>(Arrays.asList("member", "admin", "owner"));
		int selfLV = levellst.indexOf(userInfo.get("hpf_admin"));
		HpfConst.HPF_PERMISSIONS.forEach((k, v) -> {
			int destLV = levellst.indexOf(k);
			if (selfLV > destLV) {
				editablePerms.put(k, v);
				System.out.printf("%s:%s%n", k, v);
			}
		});
*/
		// 選択可能なHPF_PERMISSIONSのプルダウン値を取得
		Map<String, String> editablePerms = getEditablePermOpts(contact, userInfo);

		// テンプレートへの値渡し
		model.addAttribute("user_info", userInfo);
		model.addAttribute("contact", contact);
		model.addAttribute("prefectures", HpfConst.PREFECTURES);
		model.addAttribute("service_roles", HpfConst.SERVICE_ROLES);
		model.addAttribute("contact_kinds", HpfConst.CONTACT_KINDS);
		model.addAttribute("permissions", editablePerms);
		//model.addAttribute("permissions", HpfConst.HPF_PERMISSIONS);
		model.addAttribute("view_mode", (String)session.getAttribute(sessionModeAttributeName));
		
		model.addAttribute("role_of_iekachi", (String)session.getAttribute("role_of_iekachi"));
		model.addAttribute("role_of_pms", (String)session.getAttribute("role_of_pms"));
		model.addAttribute("role_of_cms", (String)session.getAttribute("role_of_cms"));
		model.addAttribute("role_of_r_de_go", (String)session.getAttribute("role_of_r_de_go"));
		
		model.addAttribute("before_role_of_iekachi__c", contact.getRole_of_iekachi__c());
		model.addAttribute("before_role_of_pms__c", contact.getRole_of_pms__c());
		model.addAttribute("before_role_of_cms__c", contact.getRole_of_cms__c());
		model.addAttribute("before_role_of_r_de_go__c", contact.getRole_of_r_de_go__c());

		return "portal/contact/edit";
	}

	private Map<String, Boolean> getEditableFlags(Contact t, Map<String, Object> userInfo) {
		// th記述が長くなるので事前変数化：contactService.hasPermit('retire', contact, user_info)
		Map<String, Boolean> is_ediable = new HashMap<String, Boolean>();
		is_ediable.put("retire", contactService.hasPermit("retire", t, userInfo)); // 退職編集
		is_ediable.put("edit_perm", contactService.hasPermit("edit_perm", t, userInfo)); // 権限編集
		return is_ediable;
	}

	private Map<String, String> getEditablePermOpts(Contact t, Map<String, Object> userInfo) {
		Map<String, String> editablePerms = new LinkedHashMap<String, String>();

		// 自分自身のデータかどうか？
		boolean isSelfContact = false;
		String contact_id = (String) userInfo.get("contactid");
		if (contact_id != null && contact_id.equals(t.getContact_id__c())) {
			isSelfContact = true;
		}
		// 自分自身の編集時は 現在の権限のみ（変更なし）
		if (isSelfContact) {
			String hpfadmin = (String) userInfo.get("hpf_admin");
			editablePerms.put(hpfadmin, HpfConst.HPF_PERMISSIONS.get(hpfadmin));
			return editablePerms;
		}
		// 自分より下位の権限のみを選択可能リストとする
		// member < admin < owner
		List<String> levellst = new ArrayList<>(Arrays.asList("member", "admin", "owner"));
		int selfLV = levellst.indexOf(userInfo.get("hpf_admin"));
		HpfConst.HPF_PERMISSIONS.forEach((k, v) -> {
			int destLV = levellst.indexOf(k);
			if (selfLV > destLV ||(("admin".equals(userInfo.get("hpf_admin"))) && (selfLV == destLV))) {
				editablePerms.put(k, v);
				System.out.printf("%s:%s%n", k, v);
			}
		});
		return editablePerms;
	}

	private void completionContactForm(ContactForm form, Map<String, Object> userInfo) {

		// ユーザ種別: ログイン者のユーザ種別
		if (form.getContact_kind__c() == null) form.setContact_kind__c((String)userInfo.get("user_kind"));
		// HPF権限: member
		if (form.getHpf_permission__c() == null) form.setHpf_permission__c("member");
		// 代表者: off
		if (form.getRepresentativename__c() == null) form.setRepresentativename__c(false);
		// TEL NG: off
		if (form.getDonotcall() == null) form.setDonotcall(false);
		// 携帯TEL NG: off
		if (form.getMobilephoneng__c() == null) form.setMobilephoneng__c(false);
		// Fax NG: off
		if (form.getHasoptedoutoffax() == null) form.setHasoptedoutoffax(false);
		// Email NG: off
		if (form.getHasoptedoutofemail() == null) form.setHasoptedoutofemail(false);
		// 携帯Email NG: off
		if (form.getMobilemailng__c() == null) form.setMobilemailng__c(false);
		// RでGo利用権限
		if (form.getRole_of_r_de_go__c() == null) form.setRole_of_r_de_go__c("利用不可");
		// CMS利用権限
		//if (form.getRole_of_cms__c() == null) form.setRole_of_cms__c("利用不可");
		// PMS利用権限
		if (form.getRole_of_pms__c() == null) form.setRole_of_pms__c("利用不可");
		// iekachi BOX利用権限
		//if (form.getRole_of_iekachi__c() == null) form.setRole_of_iekachi__c("利用不可");
		// PLAZA
		if (form.getPlaza__c() == null) form.setPlaza__c(false);
		// PLAZAメール
		if (form.getPlazamail__c() == null) form.setPlazamail__c(false);
		// View
		if (form.getView__c() == null) form.setView__c(false);
		// Viewメール
		if (form.getViewmail__c() == null) form.setViewmail__c(false);
		
		
		// R+house
		if (form.getNeedsmailmagazinrplushouse__c() == null) form.setNeedsmailmagazinrplushouse__c(false);
		// 家価値（R+、ADM）
		if (form.getNeedsmailmagaziniekachiforrplusadm__c() == null) form.setNeedsmailmagaziniekachiforrplusadm__c(false);
		// 家価値（一般）
		if (form.getNeedsmailmagaziniekachiforippan__c() == null) form.setNeedsmailmagaziniekachiforippan__c(false);
		// iekachiBOX
		if (form.getNeedsmailmagaziniekachibox__c() == null) form.setNeedsmailmagaziniekachibox__c(false);
		// FP
		if (form.getNeedsmailmagazinfp__c() == null) form.setNeedsmailmagazinfp__c(false);
		// FSM
		if (form.getNeedsmailmagazinfsm__c() == null) form.setNeedsmailmagazinfsm__c(false);
		// ADM
		if (form.getNeedsmailmagazinadm__c() == null) form.setNeedsmailmagazinadm__c(false);
		// AMS
		if (form.getNeedsmailmagazinams__c() == null) form.setNeedsmailmagazinams__c(false);
		// CMS
		if (form.getNeedsmailmagazincms__c() == null) form.setNeedsmailmagazincms__c(false);
		// HySP
		if (form.getNeedsmailmagazinhysp__c() == null) form.setNeedsmailmagazinhysp__c(false);
		// トチスマ
		if (form.getNeedsmailmagazintochisma__c() == null) form.setNeedsmailmagazintochisma__c(false);
		// TMD
		if (form.getNeedsmailmagazintmd__c() == null) form.setNeedsmailmagazintmd__c(false);
		// GG
		if (form.getNeedsmailmagazingg__c() == null) form.setNeedsmailmagazingg__c(false);
		// HC
		if (form.getNeedsmailmagazinhc__c() == null) form.setNeedsmailmagazinhc__c(false);
		// HIH
		if (form.getNeedsmailmagazinhih__c() == null) form.setNeedsmailmagazinhih__c(false);
		// WS
		if (form.getNeedsmailmagazinws__c() == null) form.setNeedsmailmagazinws__c(false);
		// STAY
		if (form.getNeedsmailmagazinstay__c() == null) form.setNeedsmailmagazinstay__c(false);
		// デコス
		if (form.getNeedsmailmagazindecos__c() == null) form.setNeedsmailmagazindecos__c(false);
		// PMS
		if (form.getNeedsmailmagazinpms__c() == null) form.setNeedsmailmagazinpms__c(false);
		// TM
		if (form.getNeedsmailmagazintm__c() == null) form.setNeedsmailmagazintm__c(false);
		// R+建築家
		if (form.getNeedsmailmagazinrpluskenchikuka__c() == null) form.setNeedsmailmagazinrpluskenchikuka__c(false);
		// R+IC
		if (form.getNeedsmailmagazinrplusic__c() == null) form.setNeedsmailmagazinrplusic__c(false);
		
		
		// 認証有効化要求
		if (form.getIs_approval_check__c() == null) form.setIs_approval_check__c(true);
		// 認証無効化要求
		if (form.getIs_invalid__c() == null) form.setIs_invalid__c(false);
		// 退職
		if (form.getRetirement__c() == null) form.setRetirement__c(false);
	}

	@RequestMapping(value="/confirm")
	public String confirm(HttpServletRequest request, HttpSession session, Model model) {
		User user = getLoginUserBySession(session);
		Map<String, Object> userInfo = getLoginUserInoBySession(session);
		if (user == null || userInfo == null) {
			return redirectLoginPage;
		}
		setPermissionToModel(model, userInfo);

		ContactForm form = (ContactForm)session.getAttribute(sessionAttributeName);
		if (form == null) {
			// セッションからの復元に失敗
			logger.warn("[{}] ContactForm get failed from session.", user.getSfid());
			return "redirect:/portal/contact/edit";
		}
		
		Contact contact = new Contact();
		String contact_id = form.getContact_id__c();
		if (!StringUtils.isEmpty(contact_id)) {
			contact = contactService.findOne(contact_id);
		}

		Map<String, Boolean> is_ediable = getEditableFlags(contact, userInfo);
		model.addAttribute("is_ediable", is_ediable);
				
		if (StringUtils.isEmpty(contact_id)) { // 新規作成時は権限編集可能
			is_ediable.put("edit_perm", true); // 権限編集
			//非活性時処理
/*
			if(form.getRole_of_iekachi__c() == null){
				form.setRole_of_iekachi__c("利用不可");
			}
*/			
			if(form.getRole_of_pms__c() == null){
				form.setRole_of_pms__c("利用不可");
			}
/*			
			if(form.getRole_of_cms__c() == null){
				form.setRole_of_cms__c("利用不可");
			}
*/			
			if(form.getRole_of_r_de_go__c() == null){
				form.setRole_of_r_de_go__c("利用不可");
			}
			
			//権限数チェック
			if(limitchk(session,userInfo,form,true,true,true,true)){
				return "redirect:/portal/contact/edit/?b=1&limitFlg=true";
			}
			
		}else{
			//更新時
/*			
			//非活性時処理
			if(form.getRole_of_iekachi__c() == null){
				form.setRole_of_iekachi__c(form.getBefore_role_of_iekachi__c());
			}
*/			
			if(form.getRole_of_pms__c() == null){
				form.setRole_of_pms__c(form.getBefore_role_of_pms__c());
			}
/*			
			if(form.getRole_of_cms__c() == null){
				form.setRole_of_cms__c(form.getBefore_role_of_cms__c());
			}
*/			
			if(form.getRole_of_r_de_go__c() == null){
				form.setRole_of_r_de_go__c(form.getBefore_role_of_r_de_go__c());
			}	
/*
			//iekachi BOX利用権限変更チェック
			System.err.println("★iekachiチェック★" +"★変更後★"+ form.getRole_of_iekachi__c() + "★変更前★" + form.getBefore_role_of_iekachi__c());
			if(!form.getRole_of_iekachi__c().equals(form.getBefore_role_of_iekachi__c())){
				if("利用可".equals(form.getRole_of_iekachi__c()) || "管理者".equals(form.getRole_of_iekachi__c())){
					//権限数チェック
					if(limitchk(session,userInfo,form,true,false,false,false)){
						return "redirect:/portal/contact/edit/?b=1&limitFlg=true";
					}
				}
			}
*/			
			//PMS利用権限変更チェック
			if(!form.getRole_of_pms__c().equals(form.getBefore_role_of_pms__c())){
				if("利用可".equals(form.getRole_of_pms__c()) || "管理者".equals(form.getRole_of_pms__c())){
					//権限数チェック
					if(limitchk(session,userInfo,form,false,true,false,false)){
						return "redirect:/portal/contact/edit/?b=1&limitFlg=true";
					}
				}
			}
/*			
			//CMS利用権限変更チェック
			System.err.println("★CMSチェック★" +"★変更後★"+ form.getRole_of_cms__c() + "★変更前★" + form.getBefore_role_of_cms__c());
			if(!form.getRole_of_cms__c().equals(form.getBefore_role_of_cms__c())){
				if("利用可".equals(form.getRole_of_cms__c()) || "管理者".equals(form.getRole_of_cms__c())){
					//権限数チェック
					if(limitchk(session,userInfo,form,false,false,true,false)){
						return "redirect:/portal/contact/edit/?b=1&limitFlg=true";
					}
				}
			}
*/			
			//RでGo利用権限変更チェック
			if(!form.getRole_of_r_de_go__c().equals(form.getBefore_role_of_r_de_go__c())){
				if("利用可".equals(form.getRole_of_r_de_go__c()) || "管理者".equals(form.getRole_of_r_de_go__c())){
					//権限数チェック
					if(limitchk(session,userInfo,form,false,false,false,true)){
						return "redirect:/portal/contact/edit/?b=1&limitFlg=true";
					}
				}
			}
		}
        
		//start Add
		// 補完
		// 加盟店ID: ログイン者の加盟店ID
		if (contact.getCorporationid__c() == null) contact.setCorporationid__c((String)userInfo.get("corporationid"));
		
		model.addAttribute("contact", contact);
		
		//end Add
		
		model.addAttribute("user_info", userInfo);
		
		//ラベル名変換対応
/*		if("owner".equals(form.getHpf_permission__c())){
			form.setHpf_permission__c("オーナー");
		}else if("admin".equals(form.getHpf_permission__c())){
			form.setHpf_permission__c("マネージャー");
		}else if("member".equals(form.getHpf_permission__c())){
			form.setHpf_permission__c("メンバー");
		}
*/
		model.addAttribute("contact_form", form);
		model.addAttribute("view_mode", (String)session.getAttribute(sessionModeAttributeName));
		model.addAttribute("limitFlg", null);
		return "portal/contact/confirm";
	}

	@RequestMapping(value="/regist", method = RequestMethod.POST)
	public String regist(HttpServletRequest request, HttpSession session, Model model) {
		User user = getLoginUserBySession(session);
		Map<String, Object> userInfo = getLoginUserInoBySession(session);
		if (user == null || userInfo == null) {
			return redirectLoginPage;
		}
		setPermissionToModel(model, userInfo);

		ContactForm form = (ContactForm)session.getAttribute(sessionAttributeName);
		if (form == null) {
			// セッションからの復元に失敗
			logger.warn("[{}] contactform get failed from session.", user.getSfid());
			return "redirect:/portal/contact/edit";
		}
		// _key_codeの整合チェック
		if (!form.get__key_code().equals(request.getParameter("__key_code"))) {
			// 不正リクエスト
			logger.warn("[{}] invalid keycode.", user.getSfid());
			return "redirect:/portal/contact/edit";
		}

		Contact contact;
		if (StringUtils.isEmpty(form.getContact_id__c())) {
			// 新規作成
			contact = new Contact();
			Util.copyProperties(form, contact);
			contact.setNaturalKey(contactService.generateId()); // キーを設定
			contact.setCorporationid__c((String) userInfo.getOrDefault("corporationid", null));
			contact.setIs_service_delete__c(false); // 削除フラグ設定
			contact.setIsdeleted(false);
			contact.setContact_kind__c((String)userInfo.get("user_kind"));
			contact.setCreated_by_service__c(appCodeHPF);
			contact.setLast_modified_by_service__c(appCodeHPF);


			// HPF権限が未設定の場合はmemberに設定
			if (StringUtils.isEmpty(contact.getHpf_permission__c())) {
				contact.setHpf_permission__c("member");
			}
			
			//認証有効化要求判定処理
			if(!contact.getIs_approval_check__c()){
				// 無効化要求をON
				contact.setIs_invalid__c(true);
			}else{
				// 無効化要求をOFF
				contact.setIs_invalid__c(false);
			}
			
			
			// 退職フラグが未設定の場合はfalseに設定
			if (contact.getRetirement__c() == null) {
				contact.setRetirement__c(false);
			} else if (contact.getRetirement__c()) {
				// 権限をOFF
				contact.setRole_of_cms__c("利用不可");
				contact.setRole_of_iekachi__c("利用不可");
				contact.setRole_of_pms__c("利用不可");
				contact.setRole_of_r_de_go__c("利用不可");
				// 無効化要求をON
				contact.setIs_invalid__c(true);
				// 有効化要求をOFF
				contact.setIs_approval_check__c(false);
			}
			//権限数チェック
			if(limitchk(session,userInfo,form,true,true,true,true)){
				return "redirect:/portal/contact/edit/?b=1&limitFlg=true";
			}
			
			//API変換対応
/*			if("owner".equals(contact.getHpf_permission__c())){
				contact.setHpf_permission__c("オーナー");
			}else if("admin".equals(contact.getHpf_permission__c())){
				contact.setHpf_permission__c("マネージャー");
			}else if("member".equals(contact.getHpf_permission__c())){
				contact.setHpf_permission__c("メンバー");
			}
*/			
			// 登録
			contactService.create(contact);
		}
		else {
			// 更新
			contact = contactService.findOne(form.getContact_id__c());
			if (contact == null) {
				logger.warn("[{}] contact data not found.", user.getSfid());
				return redirectTopPage;
			}

			boolean beforeRetirement = (contact.getRetirement__c() == null) ? false : contact.getRetirement__c();
			Util.copyProperties(form, contact);
			boolean afterRetirement = (contact.getRetirement__c() == null) ? false : contact.getRetirement__c();
			
			//認証有効化要求判定処理
			if(!contact.getIs_approval_check__c()){
				// 無効化要求をON
				contact.setIs_invalid__c(true);
			}else{
				// 無効化要求をOFF
				contact.setIs_invalid__c(false);
			}
			// 退職フラグがOFFからONになったら
			if (!beforeRetirement && afterRetirement) {
				// 権限をOFF
				contact.setRole_of_cms__c("利用不可");
				contact.setRole_of_iekachi__c("利用不可");
				contact.setRole_of_pms__c("利用不可");
				contact.setRole_of_r_de_go__c("利用不可");
				// 無効化要求をON
				contact.setIs_invalid__c(true);
				// 有効化要求をOFF
				contact.setIs_approval_check__c(false);
			}
			contact.setLast_modified_by_service__c(appCodeHPF);
/*			
			//iekachi BOX利用権限変更チェック
			System.err.println("★iekachiチェック★" +"★変更後★"+ form.getRole_of_iekachi__c() + "★変更前★" + form.getBefore_role_of_iekachi__c());
			if(form.getRole_of_iekachi__c() != null){
				if(!form.getRole_of_iekachi__c().equals(form.getBefore_role_of_iekachi__c())){
					if("利用可".equals(form.getRole_of_iekachi__c()) || "管理者".equals(form.getRole_of_iekachi__c())){
						//権限数チェック
						if(limitchk(session,userInfo,form,true,false,false,false)){
							return "redirect:/portal/contact/edit/?b=1&limitFlg=true";
						}
					}
				}
			}	
*/			
			//PMS利用権限変更チェック
			if(form.getRole_of_pms__c() != null){
				if(!form.getRole_of_pms__c().equals(form.getBefore_role_of_pms__c())){
					if("利用可".equals(form.getRole_of_pms__c()) || "管理者".equals(form.getRole_of_pms__c())){
						//権限数チェック
						if(limitchk(session,userInfo,form,false,true,false,false)){
							return "redirect:/portal/contact/edit/?b=1&limitFlg=true";
						}
					}
				}
			}
/*
			//CMS利用権限変更チェック
			System.err.println("★CMSチェック★" +"★変更後★"+ form.getRole_of_cms__c() + "★変更前★" + form.getBefore_role_of_cms__c());
			if(form.getRole_of_cms__c() != null){
				if(!form.getRole_of_cms__c().equals(form.getBefore_role_of_cms__c())){
					if("利用可".equals(form.getRole_of_cms__c()) || "管理者".equals(form.getRole_of_cms__c())){
						//権限数チェック
						if(limitchk(session,userInfo,form,false,false,true,false)){
							return "redirect:/portal/contact/edit/?b=1&limitFlg=true";
						}
					}
				}
			}
*/			
			//RでGo利用権限変更チェック
			if(form.getRole_of_r_de_go__c() != null){
				if(!form.getRole_of_r_de_go__c().equals(form.getBefore_role_of_r_de_go__c())){
					if("利用可".equals(form.getRole_of_r_de_go__c()) || "管理者".equals(form.getRole_of_r_de_go__c())){
						//権限数チェック
						if(limitchk(session,userInfo,form,false,false,false,true)){
							return "redirect:/portal/contact/edit/?b=1&limitFlg=true";
						}
					}
				}
			}
			
			//API変換対応
/*			if("owner".equals(contact.getHpf_permission__c())){
				contact.setHpf_permission__c("オーナー");
			}else if("admin".equals(contact.getHpf_permission__c())){
				contact.setHpf_permission__c("マネージャー");
			}else if("member".equals(contact.getHpf_permission__c())){
				contact.setHpf_permission__c("メンバー");
			}
*/			
			contactService.update(contact.getNaturalKey(), contact);
		}

		// セッション削除
		session.removeAttribute(sessionAttributeName);
		return "redirect:/portal/contact/complete";
	}

	@RequestMapping("/complete")
	public String complete(HttpServletRequest request, HttpSession session, Model model) {
		Map<String, Object> userInfo = getLoginUserInoBySession(session);
		if (userInfo == null) {
			return redirectLoginPage;
		}
		setPermissionToModel(model, userInfo);

		String viewMode = (String)session.getAttribute(sessionModeAttributeName);
		session.removeAttribute(sessionModeAttributeName);
		model.addAttribute("user_info", userInfo);
		model.addAttribute("view_mode", viewMode);
		return "portal/contact/complete";
	}
	
	@RequestMapping("/inquiry")
	public String inquiry(HttpServletRequest request, HttpSession session, Model model) {
		Map<String, Object> userInfo = getLoginUserInoBySession(session);
		if (userInfo == null) {
			return redirectLoginPage;
		}
		setPermissionToModel(model, userInfo);

		String viewMode = (String)session.getAttribute(sessionModeAttributeName);
		session.removeAttribute(sessionModeAttributeName);
		model.addAttribute("user_info", userInfo);
		model.addAttribute("view_mode", viewMode);
		return "portal/contact/inquiry";
	}
	
	public Boolean limitchk(HttpSession session, Map<String, Object> userInfo, ContactForm form, Boolean ibxFlg, Boolean pmsFlg, Boolean cmsFlg, Boolean rdgFlg){
		
		Boolean errFlg = false;
		
		ContactSearchEntity entity = new ContactSearchEntity();
		entity.setCorporationid__c((String) userInfo.getOrDefault("corporationid", null));
		entity.setIsdeleted(false);
		entity.setIs_service_delete__c(false);
		
/*		
		if(ibxFlg){
			if("利用可".equals(form.getRole_of_iekachi__c()) || "管理者".equals(form.getRole_of_iekachi__c())){
				entity.setRole_of_iekachi__c(new ArrayList<String>(HpfConst.ROL_OF_COMMODITY.keySet()));
				long countIBX = contactService.count(entity);
				if((Integer)session.getAttribute("ibxMax") <= countIBX){
					errFlg = true;
				}
			}
		}
*/		
		if(pmsFlg){
			if("利用可".equals(form.getRole_of_pms__c()) || "管理者".equals(form.getRole_of_pms__c())){
				entity.setRole_of_iekachi__c(null);
				entity.setRole_of_pms__c(new ArrayList<String>(HpfConst.ROL_OF_COMMODITY.keySet()));
				long countPMS = contactService.count(entity);
				if((Integer)session.getAttribute("pmsMax") <= countPMS){
					errFlg = true;
				}
			}
		}
/*
		if(cmsFlg){
			if("利用可".equals(form.getRole_of_cms__c()) || "管理者".equals(form.getRole_of_cms__c())){
				entity.setRole_of_pms__c(null);
				entity.setRole_of_cms__c(new ArrayList<String>(HpfConst.ROL_OF_COMMODITY.keySet()));
				long countCMS = contactService.count(entity);
				if((Integer)session.getAttribute("cmsMax") <= countCMS){
					errFlg = true;
				}
			}
		}
*/
		if(rdgFlg){
			if("利用可".equals(form.getRole_of_r_de_go__c()) || "管理者".equals(form.getRole_of_r_de_go__c())){
				entity.setRole_of_cms__c(null);
				entity.setRole_of_r_de_go__c(new ArrayList<String>(HpfConst.ROL_OF_COMMODITY.keySet()));
				long countRGO = contactService.count(entity);
				if((Integer)session.getAttribute("rgoMax") <= countRGO){
					errFlg = true;
				}
			}
		}
		return errFlg;
	}
}
