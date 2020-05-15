package online.wgear.test.spring_boot_lezhnin.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.wgear.test.spring_boot_lezhnin.dao.CatalogRepository;
import online.wgear.test.spring_boot_lezhnin.model.Catalog;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // for restTemplate
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CatalogControllerTest {

    @Autowired
    private TestRestTemplate testREST;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogRepository mockRepo;

    private static final ObjectMapper om = new ObjectMapper();

    @Before
    public void init() throws ParseException {
        Catalog catalog = new Catalog();
        catalog.setId(1L);
        catalog.setName("ROOT");

        when(mockRepo.findById(1L)).thenReturn(Optional.of(catalog));
        when(mockRepo.findById(2L)).thenReturn(Optional.empty());
    }

    @Test
    public void getCatalog_200() throws JSONException {
        String expected = "{\"id\":1,\"name\":\"ROOT\"," +
                            "\"parent\":null," +
                            "\"children\":[],\"products\":[]}";

        ResponseEntity<String> response = testREST.getForEntity("/catalog/1",String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);
        verify(mockRepo, times(1)).findById(1L);
    }

    @Test
    public void getCatalog_404() {
        ResponseEntity<CustomErrorResponse> response = testREST.getForEntity("/catalog/2", CustomErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Catalog ID not found : 2", response.getBody().getError());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
    }

    @Test
    public void getCatalog_400() {
        ResponseEntity<CustomErrorResponse> response = testREST.getForEntity("/catalog/a", CustomErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; " +
                     "nested exception is java.lang.NumberFormatException: For input string: \"a\"",
                     response.getBody().getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    }

    @Test
    public void moveCatalogToRoot_200() throws Exception {
        Catalog root = new Catalog();
        root.setId(1L);
        root.setName("Root");

        Catalog level1 = new Catalog();
        level1.setId(2L);
        level1.setName("Level 1");
        level1.setParent(root);

        when(mockRepo.findById(1L)).thenReturn(Optional.of(root));
        when(mockRepo.findById(2L)).thenReturn(Optional.of(level1));
        when(mockRepo.save(any(Catalog.class))).thenReturn(level1);

        mockMvc.perform(put("/catalog/2/move"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Level 1")))
                .andExpect(jsonPath("$.parent",is(nullValue())));

        verify(mockRepo,times(1)).save(any(Catalog.class));
    }

    @Test
    public void moveCatalogToNewParent_200() throws Exception {
        Catalog root = new Catalog();
        root.setId(1L);
        root.setName("Root");

        Catalog level1 = new Catalog();
        level1.setId(2L);
        level1.setName("Level 1");
        level1.setParent(root);

        Catalog testCatalog = new Catalog();
        testCatalog.setId(3L);
        testCatalog.setName("Moved catalog");

        when(mockRepo.findById(1L)).thenReturn(Optional.of(root));
        when(mockRepo.findById(2L)).thenReturn(Optional.of(level1));
        when(mockRepo.findById(3L)).thenReturn(Optional.of(testCatalog));
        when(mockRepo.save(any(Catalog.class))).thenReturn(testCatalog);

        mockMvc.perform(put("/catalog/3/move/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Moved catalog")))
                .andExpect(jsonPath("$.parent.id",is(2)));

        verify(mockRepo,times(1)).save(any(Catalog.class));
    }

    @Test
    public void moveCatalogToRoot_400() throws Exception {
        Catalog root = new Catalog();
        root.setId(1L);
        root.setName("Root");

        Catalog level1 = new Catalog();
        level1.setId(2L);
        level1.setName("Level 1");
        level1.setParent(root);

        when(mockRepo.findById(1L)).thenReturn(Optional.of(root));
        when(mockRepo.findById(2L)).thenReturn(Optional.of(level1));
        when(mockRepo.save(any(Catalog.class))).thenReturn(level1);

        mockMvc.perform(put("/catalog/a/move"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    public void moveCatalogToRootNotFound_404() throws Exception {
        when(mockRepo.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/catalog/1/move"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error",is("Catalog ID not found : 1")));
    }

    @Test
    public void moveCatalogToNewParent_404() throws Exception {
        when(mockRepo.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/catalog/1/move"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error",is("Catalog ID not found : 1")));
    }

    @Test
    public void moveCatalogLoopingError_400() throws Exception {
        Catalog root = new Catalog();
        root.setId(1L);
        root.setName("Root");

        Catalog level1 = new Catalog();
        level1.setId(2L);
        level1.setName("Level 1");
        level1.setParent(root);

        Catalog level2 = new Catalog();
        level2.setId(3L);
        level2.setName("Level 2");
        level2.setParent(level1);

        when(mockRepo.findById(1L)).thenReturn(Optional.of(root));
        when(mockRepo.findById(2L)).thenReturn(Optional.of(level1));
        when(mockRepo.findById(3L)).thenReturn(Optional.of(level2));

        mockMvc.perform(put("/catalog/1/move/3"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error",is("Looping error")));
    }

    @Test
    public void moveCatalogSelfLoopingError_400() throws Exception {
        Catalog root = new Catalog();
        root.setId(1L);
        root.setName("Root");

        when(mockRepo.findById(1L)).thenReturn(Optional.of(root));

        mockMvc.perform(put("/catalog/1/move/1"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error",is("Looping error")));
    }

    @Test
    public void removeCatalog_200() throws Exception {
        Catalog root = new Catalog();
        root.setId(1L);
        root.setName("Root");

        when(mockRepo.findById(1L)).thenReturn(Optional.of(root));
        doNothing().when(mockRepo).delete(any(Catalog.class));

        mockMvc.perform(delete("/catalog/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Root")));

        verify(mockRepo,times(1)).delete(any(Catalog.class));
    }

    @Test
    public void removeCatalog_404() throws Exception {
        when(mockRepo.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(delete("/catalog/1"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error",is("Catalog ID not found : 1")));
    }

    @Test
    public void updateCatalog_200() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(1L);
        catalog.setName("Catalog");

        when(mockRepo.findById(1L)).thenReturn(Optional.of(catalog));
        when(mockRepo.save(any(Catalog.class))).thenReturn(catalog);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(catalog), headers);

        ResponseEntity<Catalog> response = testREST.exchange("/catalog/1", HttpMethod.PUT, entity, Catalog.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().getName(),catalog.getName());

        verify(mockRepo,times(1)).findById(1L);
        verify(mockRepo,times(1)).save(any(Catalog.class));
    }

    @Test
    public void updateCatalog_404() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(1L);
        catalog.setName("Catalog");

        when(mockRepo.findById(1L)).thenReturn(Optional.empty());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(catalog), headers);

        ResponseEntity<CustomErrorResponse> response = testREST.exchange("/catalog/1", HttpMethod.PUT,
                entity, CustomErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(response.getBody().getStatus(),404);
        assertEquals(response.getBody().getError(),"Catalog ID not found : 1");
    }

    @Test
    public void addCatalog_200() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(1L);
        catalog.setName("Parent catalog");

        Catalog paramCatalog = new Catalog();
        paramCatalog.setId(2L);
        paramCatalog.setName("New catalog");

        Catalog afterSaveCatalog = new Catalog();
        afterSaveCatalog.setId(2L);
        afterSaveCatalog.setName("New catalog");
        afterSaveCatalog.setParent(catalog);

        when(mockRepo.findById(1L)).thenReturn(Optional.of(catalog));
        when(mockRepo.save(any(Catalog.class))).thenReturn(afterSaveCatalog);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(paramCatalog), headers);

        ResponseEntity<Catalog> response = testREST.exchange("/catalog/1", HttpMethod.POST, entity, Catalog.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(response.getBody().getName(),afterSaveCatalog.getName());
        assertEquals(response.getBody().getId(),afterSaveCatalog.getId());

        verify(mockRepo,times(1)).findById(1L);
        verify(mockRepo,times(1)).save(any(Catalog.class));
    }

    @Test
    public void addCatalogToRoot_200() throws Exception {
        Catalog catalog = new Catalog();
        catalog.setId(1L);
        catalog.setName("New catalog");

        when(mockRepo.save(any(Catalog.class))).thenReturn(catalog);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(catalog), headers);

        ResponseEntity<Catalog> response = testREST.exchange("/catalog", HttpMethod.POST, entity, Catalog.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(response.getBody().getName(),catalog.getName());
        assertEquals(response.getBody().getId(),catalog.getId());

        verify(mockRepo,times(1)).save(any(Catalog.class));
    }

}