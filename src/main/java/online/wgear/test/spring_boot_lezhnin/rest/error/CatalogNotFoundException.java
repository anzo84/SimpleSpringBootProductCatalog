package online.wgear.test.spring_boot_lezhnin.rest.error;

public class CatalogNotFoundException extends RuntimeException {
    public CatalogNotFoundException(Long id) {
        super("Catalog ID not found : " + id);
    }
}
