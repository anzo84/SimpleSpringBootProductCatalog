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
    @ApiOperation(value = "Add product", response = Product.class)
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
    @ApiOperation(value = "Update product", response = Product.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully update product"),
            @ApiResponse(code = 400, message = "Wrong parameter format"),
            @ApiResponse(code = 404, message = "Product not found")
    })
    ResponseEntity<Product> updateProduct(
            @ApiParam(value = "ID of product item for update",required = true)
            @PathVariable("id") Long id,
            @Valid @RequestBody Product product){
        Product item = productDao.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));

        item.setName(product.getName());
        item.setUom(product.getUom());

        productDao.save(item);

        return ResponseEntity.ok(item);
    }

    @PutMapping("/product/{id}/move/{parent}")
    @ApiOperation(value = "Move product. (Change the parent of catalog node)", response = Product.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully moved product"),
            @ApiResponse(code = 400, message = "Wrong parameter format"),
            @ApiResponse(code = 404, message = "Catalog not found"),
            @ApiResponse(code = 404, message = "Product not found")
    })
    ResponseEntity<Product> moveProduct(
            @ApiParam(value = "ID of product item for moving",required = true)
            @PathVariable("id") Long id,
            @ApiParam(value = "ID of new parent catalog",required = true)
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
    @ApiOperation(value = "Get product item", response = Product.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully"),
            @ApiResponse(code = 400, message = "Wrong parameter format"),
            @ApiResponse(code = 404, message = "Product not found")
    })
    ResponseEntity<Product> getProduct(
            @ApiParam(value = "ID of product",required = true)
            @PathVariable("id") Long id){
        Product product = productDao.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/product/{id}")
    @ApiOperation(value = "Remove product item", response = Product.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully removed product"),
            @ApiResponse(code = 400, message = "Wrong parameter format"),
            @ApiResponse(code = 404, message = "Product not found")
    })
    ResponseEntity removeProduct(
            @ApiParam(value = "ID of product",required = true)
            @PathVariable("id")Long id){
        Product product = productDao.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));
        productDao.delete(product);
        return ResponseEntity.ok(product);
    }
}
