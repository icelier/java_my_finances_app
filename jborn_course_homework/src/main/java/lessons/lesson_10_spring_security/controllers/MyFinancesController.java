package lessons.lesson_10_spring_security.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static lessons.lesson_10_spring_security.MyApplication.BASE_URL;

@Controller
@RequestMapping("/my-finances")
public class MyFinancesController {

    @GetMapping()
    public String getMyFinancesPage() {
        return "redirect:" + BASE_URL + "/users/login";
    }
}
