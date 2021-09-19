package lessons.lesson_9_spring_boot.controllers.finances.accounts;

import lessons.lesson_9_spring_boot.MyApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = MyApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
public class IntegrationAccountControllerTest {
    @Autowired private MockMvc mockMvc;

    @Test
    public void addAccount_getStatusOk() throws Exception {
        mockMvc.perform(
                post("/my-finances/accounts/add")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"new account name\",\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$").value("Account successfully added!"));
    }

    @Test
    public void getAllAccountsByUserId_getStatusOk() throws Exception {
        mockMvc.perform(
                get("/my-finances/accounts/")
                        .param("userId", "1")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("father main salary card"))
        .andExpect(jsonPath("$[0].transactions").isArray())
        .andExpect(jsonPath("$[0].transactions", hasSize(2)))
        .andExpect(jsonPath("$[0].transactions[0].sum").value("53530.00"))
        ;
    }

    @Test
    public void updateAccount_getStatusOk() throws Exception {
        mockMvc.perform(
                post("/my-finances/accounts/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\n" +
                                "  \"name\": \"account name\",\n" +
                                "  \"total\": \"1000.00\",\n" +
                                "  \"accountType\": \"salary card\",\n" +
                                "  \"userName\": \"daddy\"\n" +
                                "}")
                        .param("id", "1")
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("account name"))
        .andExpect(jsonPath("$.total").value("1000.00"));
    }
}
