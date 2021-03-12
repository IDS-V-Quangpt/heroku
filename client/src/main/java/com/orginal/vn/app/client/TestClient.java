package com.orginal.vn.app.client;


import org.springframework.cloud.openfeign.FeignClient;
import com.orginal.vn.app.api.TestController;

@FeignClient(name = "test", url = TestClientConfiguration.TEST_CLIENT_URL)
public interface TestClient extends TestController {

}
