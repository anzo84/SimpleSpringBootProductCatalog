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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // for restTemplate
@ActiveProfiles("test")
public class CatalogControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

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
    public void NOT_FOUND_404_getCatalog() throws JSONException {
        ResponseEntity<CustomErrorResponse> response = restTemplate.getForEntity("/catalog/2", CustomErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Catalog ID not found : 2", response.getBody().getError());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
    }

    @Test
    public void WRONG_ARGUMENT_400_getCatalog() throws JSONException {
        ResponseEntity<CustomErrorResponse> response = restTemplate.getForEntity("/catalog/a", CustomErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; " +
                     "nested exception is java.lang.NumberFormatException: For input string: \"a\"",
                     response.getBody().getError());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    }

}