package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamNotAdmin")
    void deleteBeerHttpBasicNoAuth(String user, String pwd) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/" + beerToDelete().getId())
                .with(httpBasic(user, pwd)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBeerHttpBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/beer/" + beerToDelete().getId())
                .with(httpBasic("spring", "test")))
                .andExpect(status().is2xxSuccessful());
    }

    @DisplayName("List Beers")
    @org.junit.jupiter.api.Nested
    class ListBeers {
        @Test
        void findBeers() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer"))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeersAuth(String user, String pwd) throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/")
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("Get Beer by Id")
    @org.junit.jupiter.api.Nested
    class GetBeerById {
        @Test
        void findBeersById() throws Exception {
            Beer beer = beerRepository.findAll().get(0);
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/" + beer.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeersByIdAuth(String user, String pwd) throws Exception {
            Beer beer = beerRepository.findAll().get(0);
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer/" + beer.getId())
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }

    }

    @DisplayName("Find by UPC")
    @org.junit.jupiter.api.Nested
    class FindByUPC {
        @Test
        void findBeersByUPC() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beerUpc/0631234200036"))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.BeerControllerIT#getStreamAllUsers")
        void findBeersByUPCAuth(String user, String pwd) throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beerUpc/0631234200036")
                    .with(httpBasic(user, pwd)))
                    .andExpect(status().isOk());
        }
    }

}
