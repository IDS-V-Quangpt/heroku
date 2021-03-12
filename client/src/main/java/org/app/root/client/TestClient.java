package org.app.root.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.app.root.api.TestController;

@FeignClient(name = "test", url = TestClientConfiguration.TEST_CLIENT_URL)
public interface TestClient extends TestController {

}
