package online.wgear.test.spring_boot_lezhnin.rest;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogRepository mockDao;

    @Before
    public void init() throws ParseException {
        Catalog catalog = new Catalog();
        catalog.setId(1L);
        catalog.setName("ROOT");

        when(mockDao.findById(1L)).thenReturn(Optional.of(catalog));
        when(mockDao.findById(2L)).thenReturn(Optional.empty());
    }

    @Test
    public void SUCCESS_200_getCatalog() throws JSONException {
        String expected = "{\"id\":1,\"name\":\"ROOT\"," +
                            "\"parent\":null," +
                            "\"children\":[],\"products\":[]}";

        ResponseEntity<String> response = restTemplate.getForEntity("/catalog/1",String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expected, response.getBody(), false);
        verify(mockDao, times(1)).findById(1L);
    }

    @Test
    public void NOT_FOUND_404_getCatalog() {
        ResponseEntity<CustomErrorResponse> response = restTemplate.getForEntity("/catalog/2", CustomErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Catalog ID not found : 2", response.getBody().getError());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
    }

    @Test
    public void WRONG_ARGUMENT_400_getCatalog() {
        ResponseEntity<CustomErrorResponse> response = restTemplate.getForEntity("/catalog/a", CustomErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; " +
                     "nested exception is java.lang.NumberFormatException: For input string: \"a\"",
                     response.getBody().getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    }

    @Test
    public void SUCCESS_400_moveCatalogToRoot() throws Exception {
        Catalog root = new Catalog();
        root.setId(1L);
        root.setName("Root");

        Catalog level1 = new Catalog();
        level1.setId(2L);
        level1.setName("Level 1");
        level1.setParent(root);

        when(mockDao.findById(1L)).thenReturn(Optional.of(root));
        when(mockDao.findById(2L)).thenReturn(Optional.of(level1));
        when(mockDao.save(any(Catalog.class))).thenReturn(level1);

        mockMvc.perform(put("/catalog/2/move"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Level 1")))
                .andExpect(jsonPath("$.parent",is(nullValue())));

        verify(mockDao,times(1)).save(any(Catalog.class));
    }

    @Test
    public void SUCCESS_400_moveCatalogToNewParent() throws Exception {
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

        when(mockDao.findById(1L)).thenReturn(Optional.of(root));
        when(mockDao.findById(2L)).thenReturn(Optional.of(level1));
        when(mockDao.findById(3L)).thenReturn(Optional.of(testCatalog));
        when(mockDao.save(any(Catalog.class))).thenReturn(testCatalog);

        mockMvc.perform(put("/catalog/3/move/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Moved catalog")))
                .andExpect(jsonPath("$.parent.id",is(2)));

        verify(mockDao,times(1)).save(any(Catalog.class));
    }

}