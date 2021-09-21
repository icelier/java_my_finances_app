package lessons.lesson_9_spring_boot.controllers.users.login;

import lessons.lesson_9_spring_boot.MyApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = MyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class IntegrationLoginControllerTest {
    @Autowired private WebApplicationContext webApplicationContext;

    @Autowired private MockMvc mockMvc;

    @Test
    public void login_getStatusOk() throws Exception {
        mockMvc.perform(
                post("/my-finances/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"userName\": \"daddy\",\n" +
                                "  \"password\": \"123\"\n" +
                                "}")
        )
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userName").value("daddy"))
        .andExpect(jsonPath("$.email").value("sokol@gmail.com"));
    }

    @Test
    public void login_passIncorrectPasswordThreeTimes_throwPasswordNotMatchException_getTooManyRequestsStatus() throws Exception {
        // to send multiple requests in the same session
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(sharedHttpSession())
                .build();

        mockMvc.perform(post("/my-finances/users/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"userName\": \"daddy\",\n" +
                                "  \"password\": \"222\"\n" +
                                "}")
        ).andExpect(status().isBadRequest())
                .andExpect(header().string("Warning", "Incorrect password"))
                .andDo(result -> mockMvc.perform(
                        post("/my-finances/users/login")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content("{\n" +
                                        "  \"userName\": \"daddy\",\n" +
                                        "  \"password\": \"222\"\n" +
                                        "}")
                ).andExpect(status().isBadRequest())
                        .andExpect(header().string("Warning", "Incorrect password"))
                        .andDo(result1 ->
                                mockMvc.perform(
                                post("/my-finances/users/login")
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .content("{\n" +
                                                "  \"userName\": \"daddy\",\n" +
                                                "  \"password\": \"222\"\n" +
                                                "}")
                        ).andExpect(status().isTooManyRequests())
                )
        );
    }
}
