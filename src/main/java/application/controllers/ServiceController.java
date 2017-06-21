package application.controllers;


import application.models.ServiceModel;
import application.services.ServiceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/service")
public class ServiceController {
    private ServiceDAO serviceDAO;

    @Autowired
    ServiceController(ServiceDAO serviceDAO) {
        this.serviceDAO = serviceDAO;
    }

    @PostMapping(path = "/clear")
    public ResponseEntity clear() {
        try{
            serviceDAO.clear();
        } catch (Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/status")
    public ResponseEntity status() {
        ServiceModel service = new ServiceModel();
        service.setUser(serviceDAO.getCountUsers());
        service.setForum(serviceDAO.getCountForums());
        service.setThread(serviceDAO.getCountThreads());
        service.setPost(serviceDAO.getCountPost());

        return ResponseEntity.ok(service);
    }
}

