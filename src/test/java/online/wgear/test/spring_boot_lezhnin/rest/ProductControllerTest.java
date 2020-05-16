package online.wgear.test.spring_boot_lezhnin.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.wgear.test.spring_boot_lezhnin.dao.CatalogRepository;
import online.wgear.test.spring_boot_lezhnin.dao.ProductRepository;
import online.wgear.test.spring_boot_lezhnin.model.Catalog;
import online.wgear.test.spring_boot_lezhnin.model.Product;
import online.wgear.test.spring_boot_lezhnin.rest.error.CustomErrorResponse;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // for restTemplate
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private TestRestTemplate testREST;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository prodMockRepo;

    @MockBean
    private CatalogRepository catMockRepo;

    private static final ObjectMapper om = new ObjectMapper();

    @Before
    public void init() throws ParseException {
    }

    @Test
    public void removeProduct_200() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("product name");
        product.setUom("ptc");

        when(prodMockRepo.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(prodMockRepo).delete(any(Product.class));

        mockMvc.perform(delete("/product/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("product name")))
                .andExpect(jsonPath("$.uom", is("ptc")));

        verify(prodMockRepo,times(1)).delete(any(Product.class));
    }

    @Test
    public void removeProduct_404() throws Exception {
        when(prodMockRepo.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(delete("/product/1"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error",is("Product ID not found : 1")));
    }

    @Test
    public void updateProduct_200() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("product name");
        product.setUom("ptc");

        when(prodMockRepo.findById(1L)).thenReturn(Optional.of(product));
        when(prodMockRepo.save(any(Product.class))).thenReturn(product);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(product), headers);

        ResponseEntity<Product> response = testREST.exchange("/product/1", HttpMethod.PUT, entity, Product.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getName(),product.getName());
        assertEquals(response.getBody().getUom(),product.getUom());

        verify(prodMockRepo,times(1)).findById(1L);
        verify(prodMockRepo,times(1)).save(any(Product.class));
    }

    @Test
    public void updateProduct_404() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("product name");
        product.setUom("ptc");

        when(prodMockRepo.findById(1L)).thenReturn(Optional.empty());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(product), headers);

        ResponseEntity<CustomErrorResponse> response = testREST.exchange("/product/1", HttpMethod.PUT,
                entity, CustomErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(response.getBody().getStatus(),404);
        assertEquals(response.getBody().getError(),"Product ID not found : 1");
    }

    @Test
    public void addProduct_200() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("product name");
        product.setUom("ptc");

        Catalog catalog = new Catalog();
        catalog.setId(2L);
        catalog.setName("New catalog");

        Product productAfter = new Product();
        productAfter.setId(1L);
        productAfter.setName("product name");
        productAfter.setUom("ptc");
        productAfter.setCatalog(catalog);

        when(prodMockRepo.save(any(Product.class))).thenReturn(productAfter);
        when(catMockRepo.findById(1L)).thenReturn(Optional.of(catalog));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(product), headers);

        ResponseEntity<Product> response = testREST.exchange("/product/1", HttpMethod.POST, entity, Product.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(response.getBody().getName(),productAfter.getName());
        assertEquals(response.getBody().getId(),productAfter.getId());

        verify(catMockRepo,times(1)).findById(1L);
        verify(prodMockRepo,times(1)).save(any(Product.class));
    }

}