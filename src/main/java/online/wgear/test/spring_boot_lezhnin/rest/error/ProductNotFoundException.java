package online.wgear.test.spring_boot_lezhnin.rest.error;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product ID not found : " + id);
    }
}
