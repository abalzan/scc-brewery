package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class BeerRestControllerIT extends BaseIT {

    @Test
    void deleteBeerBadCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/38dfa781-3290-4fb4-ba95-a8df3dcf1fb4")
                .header("Api-Key", "spring").header("Api-Secret", "wrongPass"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void deleteBeer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/38dfa781-3290-4fb4-ba95-a8df3dcf1fb4")
                .header("Api-Key", "spring").header("Api-Secret", "test"))
                .andExpect(status().isOk());

    }

    @Test
    void deleteBeerHttpBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/38dfa781-3290-4fb4-ba95-a8df3dcf1fb4")
                .with(httpBasic("spring", "test")))
                .andExpect(status().is2xxSuccessful());

    }


    @Test
    void deleteBeerNoAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/38dfa781-3290-4fb4-ba95-a8df3dcf1fb4"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void findBeer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer"))
                .andExpect(status().isOk());

    }

    @Test
    void findBeersById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/38dfa781-3290-4fb4-ba95-a8df3dcf1fb4"))
                .andExpect(status().isOk());

    }

    @Test
    void findBeersByUPC() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beerUpc/234567890"))
                .andExpect(status().isOk());

    }

}
