package jp.co.hyas.hpf.portal;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.hyas.hpf.core.HpfConst;
import jp.co.hyas.hpf.core.forms.AccountWaitForm;
import jp.co.hyas.hpf.database.Account;
import jp.co.hyas.hpf.database.AccountRepository;
import jp.co.hyas.hpf.database.AccountWait;
import jp.co.hyas.hpf.database.AccountWaitService;
import jp.co.hyas.hpf.database.User;
import jp.co.hyas.hpf.database.base.Util;


@Controller
@RequestMapping("portal/corporation")
public class CorporationController extends HpfBaseController {
	Logger logger = LoggerFactory.getLogger(CorporationController.class);

	private String sessionAttributeName = "session_for_corporation_edit";

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountWaitService accountWaitService;

	@RequestMapping("/")
	public String index() {
		return "redirect:/portal/service";
	}

	// AccountWaitFormに対して値をセットして編集する実装仕様に変更
	@RequestMapping(value="/edit")
	public String edit_get(HttpServletRequest request, HttpSession session, Model model,
			@ModelAttribute @Validated AccountWaitForm form, BindingResult bind) {
		User user = getLoginUserBySession(session);
		Map<String, Object> userInfo = getLoginUserInoBySession(session);
		model.addAttribute("return", false);
		if (user == null || userInfo == null) {
			return redirectLoginPage;
		}
		setPermissionToModel(model, userInfo);

		// Account取得
		Account account = accountRepository.findOneEntityBySfId(user.getAccountid());
		if (account == null) {
			return redirectTopPage;
		}

		Account headaccount = null;
		if (account.getHeadoffice__c() != null)
			headaccount = accountRepository.findOneEntityBySfId(account.getHeadoffice__c());

		// AccountWaitを取得
		AccountWait account_wait = accountWaitService.findOneEnabled(account.getCorporationid__c());

		if (form == null)
			form = new AccountWaitForm();

		//System.err.println(request.getMethod());
		// GET:初回表示および確認画面からの戻り時
		if ("GET".equals(request.getMethod())) {
			AccountWaitForm account_form = (AccountWaitForm)session.getAttribute(sessionAttributeName);
			// 確認画面からの戻り時
			if (account_form != null && "1".equals(request.getParameter("b"))) { // セッションFORMが有効 & 戻りフラグがON
				//form = account_form;
				Util.copyProperties(account_form, form); // セッションformを復元
				model.addAttribute("return", true);
			} else {
			//if (account_form == null || "1".equals(request.getParameter("b"))) { // FORMがNULL or 戻りフラグがない
				// 初回表示時
				if (account_wait == null) { // 既存加盟店情報をformに初期設定
					// 新規申請時
					form.setName__c(account.getName());
					form.setName_kana__c(account.getNamekana__c());
					form.setBillingpostalcode__c(account.getBillingpostalcode());
					form.setBillingstate__c(account.getBillingstate());
					form.setBillingcity__c(account.getBillingcity());
					form.setBillingstreet__c(account.getBillingstreet());
					form.setBuildingname__c(account.getBuildingname__c());
					form.setPhone__c(account.getPhone());
					form.setFax__c(account.getFax());
					form.setEmail__c(account.getEmail__c());
					form.setWebsite__c(account.getWebsite());
				} else {
					// 更新申請時
					Util.copyProperties(account_wait, form); // 前回の申請内容をformに初期設定
				}
			}
		}
		// POST:編集登録時
		else if ("POST".equals(request.getMethod())) {
			// エラーが無ければ確認画面へ
			if (!bind.hasErrors()) {
				// CSRF対策用のキーコードを生成
				form.set__key_code(getKeyCode());
				session.setAttribute(sessionAttributeName, form);
				return "redirect:/portal/corporation/confirm";
			}
		}

		// セッション情報初期化
		session.removeAttribute(sessionAttributeName);

		// テンプレートへの値渡し
		model.addAttribute("user_info", userInfo);
		model.addAttribute("account", account);
		model.addAttribute("headaccount", headaccount);
		model.addAttribute("account_wait", account_wait);
		//model.addAttribute("account_form", form);
		model.addAttribute("prefectures", HpfConst.PREFECTURES);

		return "portal/corporation/edit";
	}
	@RequestMapping(value="/confirm")
	public String confirm(HttpServletRequest request, HttpSession session, Model model) {
		User user = getLoginUserBySession(session);
		Map<String, Object> userInfo = getLoginUserInoBySession(session);
		if (user == null || userInfo == null) {
			return redirectLoginPage;
		}
		setPermissionToModel(model, userInfo);

		AccountWaitForm account = (AccountWaitForm)session.getAttribute(sessionAttributeName);
		if (account == null) {
			// セッションからの復元に失敗
			logger.warn("[{}] accountform get failed from session.", user.getSfid());
			return "redirect:/portal/corporation/edit";
		}

		model.addAttribute("user_info", userInfo);
		model.addAttribute("account", account);

		return "portal/corporation/confirm";
	}

	@RequestMapping(value="/regist", method = RequestMethod.POST)
	public String regist(HttpServletRequest request, HttpSession session, Model model) {
		User user = getLoginUserBySession(session);
		if (user == null) {
			return redirectLoginPage;
		}

		AccountWaitForm account_form = (AccountWaitForm)session.getAttribute(sessionAttributeName);
		if (account_form == null) {
			// セッションからの復元に失敗
			logger.warn("[{}] accountform get failed from session.", user.getSfid());
			return "redirect:/portal/corporation/edit";
		}
		// _key_codeの整合チェック
		if (!account_form.get__key_code().equals(request.getParameter("__key_code"))) {
			// 不正リクエスト
			logger.warn("[{}] invalid keycode.", user.getSfid());
			return "redirect:/portal/corporation/edit";
		}

		Account account_rec = accountRepository.findOneEntityBySfId(user.getAccountid());
		// 自身の加盟店であることのチェック
		if (!account_rec.getCorporationid__c().equals(account_form.getCorporationid__c())) {
			// 不正データ
			logger.warn("[{}] invalid corporaion_id. needs: {} / value: {}",
					user.getSfid(), account_rec.getCorporationid__c(), account_form.getCorporationid__c());
			return "redirect:/portal/corporation/edit";
		}

		// データを移す
		AccountWait account_wait = accountWaitService.findOneEnabled(account_form.getCorporationid__c());
		if (account_wait == null) {
			account_wait = new AccountWait();
			Util.copyProperties(account_form, account_wait);
			String ident = accountWaitService.generateId();// キーを生成
			account_wait.setNaturalKey(ident); // キーを設定
			account_wait.setIs_merge_completion__c(false);
			account_wait.setAccount_id__c(account_rec.getSfid());
			//account_wait.setIs_service_delete__c(false);
			//account_wait.setIsdeleted(false);
			accountWaitService.create(account_wait);
		}
		else {
			Util.copyProperties(account_form, account_wait);
			accountWaitService.update(account_wait.getNaturalKey(), account_wait);
		}

		// セッション削除
		session.removeAttribute(sessionAttributeName);
		return "redirect:/portal/corporation/complete";
	}

	@RequestMapping("/complete")
	public String complete(HttpServletRequest request, HttpSession session, Model model) {
		Map<String, Object> userInfo = getLoginUserInoBySession(session);
		if (userInfo == null) {
			return redirectLoginPage;
		}
		setPermissionToModel(model, userInfo);

		model.addAttribute("user_info", userInfo);
		return "portal/corporation/complete";
	}
}
