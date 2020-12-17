package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
public class CustomerControllerIT extends BaseIT {

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAdminCustomer")
    void testListCustomerAuth(String user, String pwd) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/customers")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(user, pwd)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testListCustomerNoAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/customers")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("user", "password")))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testListCustomerNoLoggedIn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/customers"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
