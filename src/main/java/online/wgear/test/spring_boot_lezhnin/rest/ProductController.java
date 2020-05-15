package online.wgear.test.spring_boot_lezhnin.rest;

import io.swagger.annotations.*;
import online.wgear.test.spring_boot_lezhnin.dao.CatalogRepository;
import online.wgear.test.spring_boot_lezhnin.dao.ProductRepository;
import online.wgear.test.spring_boot_lezhnin.model.Catalog;
import online.wgear.test.spring_boot_lezhnin.model.Product;
import online.wgear.test.spring_boot_lezhnin.rest.error.CatalogNotFoundException;
import online.wgear.test.spring_boot_lezhnin.rest.error.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@RestController
@Validated
@Api(value="Product management system")
public class ProductController {

    @Autowired
    ProductRepository productDao;

    @Autowired
    CatalogRepository catalogDao;

    @PostMapping("/product/{catalog}")
    @ApiOperation(value = "Add product", response = Catalog.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully add product"),
            @ApiResponse(code = 400, message = "Wrong parameter format"),
            @ApiResponse(code = 404, message = "Parent catalog not found")
    })
    ResponseEntity<Product> addProduct(
            @ApiParam(value = "ID of parent catalog node")
            @PathVariable(value = "catalog") Long catalogId,
            @Valid @RequestBody Product product){
        Catalog parentCatalog = catalogDao.findById(catalogId)
                .orElseThrow(()->new CatalogNotFoundException(catalogId));

        product.setCatalog(parentCatalog);
        productDao.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/product/{id}")
    ResponseEntity<Product> updateProduct(@PathVariable("id") Long id,
                                          @Valid @RequestBody Product product){
        Product item = productDao.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));

        item.setName(product.getName());
        item.setUom(product.getUom());

        productDao.save(item);

        return ResponseEntity.ok(item);
    }

    @PutMapping("/product/{id}/move/{parent}")
    ResponseEntity<Product> moveProduct(@PathVariable("id") Long id,
                                        @PathVariable("parent") Long parentId){
        Product product = productDao.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));

        Catalog catalog = catalogDao.findById(parentId)
                .orElseThrow(()->new CatalogNotFoundException(parentId));

        product.setCatalog(catalog);
        productDao.save(product);

        return ResponseEntity.ok(product);
    }

    @GetMapping("/product/{id}")
    ResponseEntity<Product> getProduct(@PathVariable("id") Long id){
        Product product = productDao.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/product/{id}")
    ResponseEntity removeProduct(@PathVariable("id")Long id){
        Product product = productDao.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));
        productDao.delete(product);
        return ResponseEntity.ok(product);
    }
}
