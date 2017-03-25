package application.controllers;

import application.services.ServiceDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by egor on 21.03.17.
 */

@RestController
@RequestMapping(path = "/api/service")
public final class ServiceController {

    private ServiceDAO serviceServiceDAO;

    public ServiceController(ServiceDAO serviceServiceDAO) {
        this.serviceServiceDAO = serviceServiceDAO;
    }

    @RequestMapping(path = "/status", method = RequestMethod.GET)
    public ResponseEntity status() {
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("user", serviceServiceDAO.getCountUsers());
        statusMap.put("forum", serviceServiceDAO.getCountForums());
        statusMap.put("thread", serviceServiceDAO.getCountThreads());
        statusMap.put("post", serviceServiceDAO.getCountPosts());

        return ResponseEntity.ok(statusMap);
    }

    @RequestMapping(path = "/clear", method = RequestMethod.POST)
    public ResponseEntity clear() {
        serviceServiceDAO.clearDataBase();

        return ResponseEntity.ok().build();
    }


}
