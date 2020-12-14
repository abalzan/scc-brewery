package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Random;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BeerRestControllerIT extends BaseIT {

    @Autowired
    private BeerRepository beerRepository;

    @DisplayName("Delete Tests")
    @Nested
    private Beer beerToDelete() {
        Random rand = new Random();
        return beerRepository.saveAndFlush(Beer.builder()
                .beerName("Delete me beer")
                .beerStyle(BeerStyleEnum.ALE)
                .minOnHand(10)
                .quantityToBrew(100)
                .upc(String.valueOf(rand.nextInt(999999999)))
                .build());
    }

    @Test
    void deleteBeerHttpBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/" + beerToDelete().getId())
                .with(httpBasic("spring", "test")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteBeerHttpBasicUserRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/" + beerToDelete().getId())
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBeerHttpBasicCustomerRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/" + beerToDelete().getId())
                .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }


    @Test
    void deleteBeerNoAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/" + beerToDelete().getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findBeer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeersById() throws Exception {
        Beer beer = beerRepository.findAll().get(0);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/" + beer.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void findBeersByUPC() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beerUpc/0631234200036"))
                .andExpect(status().isOk());
    }

    @Test
    void findBeersFormADMIN() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/beers").param("beerName", "")
                .with(httpBasic("spring", "test")))
                .andExpect(status().isOk());
    }

}
