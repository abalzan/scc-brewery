package guru.sfg.brewery.web.controllers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.brewery.bootstrap.DefaultBreweryLoader;
import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
import guru.sfg.brewery.web.controllers.BaseIT;
import guru.sfg.brewery.web.model.BeerOrderDto;
import guru.sfg.brewery.web.model.BeerOrderLineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class BeerOrderControllerV2Test extends BaseIT {

    public static final String API_ROOT = "/api/v2/orders/";

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    Customer stPatrickCustomer;
    Customer guinnessCustomer;
    Customer dublinWestCustomer;
    List<Beer> loadedBeers;

    @BeforeEach
    void setUp() {
        stPatrickCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.ST_PATRICK_DISTRIBUTING).orElseThrow();
        guinnessCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.GUINNESS_DISTRIBUTING).orElseThrow();
        dublinWestCustomer = customerRepository.findAllByCustomerName(DefaultBreweryLoader.DUBLIN_WEST_DISTRIBUTING).orElseThrow();
        loadedBeers = beerRepository.findAll();
    }

    @Test
    void listOrdersNoAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT ))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void listOrdersAdminAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithUserDetails(DefaultBreweryLoader.ST_PATRICK_USER)
    @Test
    void listOrdersCustomerAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithUserDetails(DefaultBreweryLoader.GUINNESS_USER)
    @Test
    void listOrdersCustomerGuinnessAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private BeerOrderDto buildOrderDto(Customer customer, UUID beeerId) {
        List<BeerOrderLineDto> orderLines = Arrays.asList(BeerOrderLineDto.builder()
                .id(UUID.randomUUID())
                .beerId(beeerId)
                .orderQuantity(5)
                .build());

        return BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef("1234")
                .orderStatusCallbackUrl("https://www.google.com")
                .beerOrderLines(orderLines)

                .build();
    }
}
