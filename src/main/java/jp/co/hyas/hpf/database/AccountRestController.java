package jp.co.hyas.hpf.database;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.co.hyas.hpf.database.base.BaseRestController;

@RestController
@RequestMapping("api/accounts")
public class AccountRestController extends BaseRestController<Account, AccountSearchEntity, AccountRepository, AccountService> {
}
