package jp.co.hyas.hpf.database;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.co.hyas.hpf.database.base.BaseRestController;

@RestController
@RequestMapping("api/fileinfo")
public class FileinfoRestController extends BaseRestController<Fileinfo, FileinfoSearchEntity, FileinfoRepository, FileinfoService> {
}
