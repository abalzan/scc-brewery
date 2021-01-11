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
public class BeerOrderControllerTest extends BaseIT {

    public static final String API_ROOT = "/api/v1/customers/";

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
    void createOrderNoAuth() throws Exception{
        BeerOrderDto beerOrderDto = buildOrderDto(stPatrickCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_ROOT+stPatrickCustomer.getId()+"/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void createOrderAdmin() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPatrickCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_ROOT+stPatrickCustomer.getId()+"/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithUserDetails(DefaultBreweryLoader.ST_PATRICK_USER)
    @Test
    void createOrderUserPatrick() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPatrickCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_ROOT+stPatrickCustomer.getId()+"/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithUserDetails(DefaultBreweryLoader.DUBLIN_WEST_USER)
    @Test
    void createOrderUserWest() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPatrickCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_ROOT+stPatrickCustomer.getId()+"/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void createOrderUserNotAuth() throws Exception {
        BeerOrderDto beerOrderDto = buildOrderDto(stPatrickCustomer, loadedBeers.get(0).getId());

        mockMvc.perform(MockMvcRequestBuilders.post(API_ROOT+stPatrickCustomer.getId()+"/orders")
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerOrderDto)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void listOrdersNoAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT + stPatrickCustomer.getId()+ "/orders"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @WithUserDetails("spring")
    @Test
    void listOrdersAdminAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT + stPatrickCustomer.getId()+ "/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithUserDetails(DefaultBreweryLoader.ST_PATRICK_USER)
    @Test
    void listOrdersCustomerAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT + stPatrickCustomer.getId()+ "/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithUserDetails(DefaultBreweryLoader.GUINNESS_USER)
    @Test
    void listOrdersCustomerNoAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT + stPatrickCustomer.getId()+ "/orders"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void listCustomersNoAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT + stPatrickCustomer.getId()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Transactional
    @Test
    void getByOrderIdNotAuth() throws Exception {
        BeerOrder beerOrder = stPatrickCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT + stPatrickCustomer.getId() + "/orders/"+beerOrder.getId()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Transactional
    @WithUserDetails("spring")
    @Test
    void getByOrderIdAdmin() throws Exception {
        BeerOrder beerOrder = stPatrickCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT + stPatrickCustomer.getId() + "/orders/"+beerOrder.getId()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.ST_PATRICK_USER)
    @Test
    void getByOrderIdCustomerAuth() throws Exception {
        BeerOrder beerOrder = stPatrickCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT + stPatrickCustomer.getId() + "/orders/"+beerOrder.getId()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Transactional
    @WithUserDetails(DefaultBreweryLoader.GUINNESS_USER)
    @Test
    void getByOrderIdCustomerNotAuth() throws Exception {
        BeerOrder beerOrder = stPatrickCustomer.getBeerOrders().stream().findFirst().orElseThrow();

        mockMvc.perform(MockMvcRequestBuilders.get(API_ROOT + stPatrickCustomer.getId() + "/orders/"+beerOrder.getId()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
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
