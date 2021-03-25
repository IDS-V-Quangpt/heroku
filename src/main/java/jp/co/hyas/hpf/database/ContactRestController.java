package jp.co.hyas.hpf.database;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.co.hyas.hpf.database.base.BaseRestController;

@RestController
@RequestMapping("api/contacts")
public class ContactRestController extends BaseRestController<Contact, ContactSearchEntity, ContactRepository, ContactService> {
/*
	public class ContactRestController {
	@Autowired
	ContactService service;
	// 全件取得
	@RequestMapping(method=GET)
	public List<Contact> getList(
			@RequestParam Map<String, String> queryParameters,
			@PageableDefault(size=500, sort={"id"}) Pageable pageable) {
        return service.search(queryParameters, pageable).getContent();
	}

	// 一件取得
	@RequestMapping(method=GET, value="{id}")
	public Contact get(@PathVariable Integer id) {
		return service.findOne(id);
	}

	// 一件作成
	@RequestMapping(method=POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Contact post(@RequestBody Contact contact) {
		service.create(contact);
		return contact;
	}

	// 一件更新
	@RequestMapping(method=PUT, value="{id}")
	public Contact put(@PathVariable Integer id, @RequestBody Contact contact) {
		contact.setId(id);
		service.update(contact);
		return contact;
	}

	// 一件削除
	@RequestMapping(method=DELETE, value="{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer id) {
		service.delete(id);
	}
*/
}
