package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
public class CustomerControllerIT extends BaseIT {

    @DisplayName("List Customers")
    @Nested
    class ListCustomers {
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

    @DisplayName("Add Customers")
    @Nested
    class AddCustomers {
        @Rollback
        @Test
        void processCreationForm() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/customers/new").with(SecurityMockMvcRequestPostProcessors.csrf())
                    .param("customer Name", "foo bar customer")
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic("spring", "test")))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        }

        @Rollback
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamNotAdmin")
        void processCreationFormNoRight(String user, String pwd) throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/customers/new").with(SecurityMockMvcRequestPostProcessors.csrf())
                    .param("customer Name", "foo bar customer 2")
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic(user, pwd)))
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        void processCreationFormNoAuth() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/customers/new").with(SecurityMockMvcRequestPostProcessors.csrf())
                    .param("customer Name", "foo bar customer"))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }
    }
}
