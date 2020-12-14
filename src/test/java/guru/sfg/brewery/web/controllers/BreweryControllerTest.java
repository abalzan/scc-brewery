package guru.sfg.brewery.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BreweryControllerTest extends BaseIT {

    @Test
    void listBreweriesCustomer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/breweries")
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweriesUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/breweries")
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listBreweriesAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/breweries")
                .with(httpBasic("spring", "test")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void listBreweriesNotAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/breweries"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getBreweriesJsonCustomer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/api/v1/breweries")
                .with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getBreweriesJsonUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/api/v1/breweries")
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getBreweriesJsonAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/api/v1/breweries")
                .with(httpBasic("spring", "test")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getBreweriesJsonNotAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/brewery/api/v1/breweries"))
                .andExpect(status().isUnauthorized());
    }

}