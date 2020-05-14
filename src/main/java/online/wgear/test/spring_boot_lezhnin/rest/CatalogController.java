package online.wgear.test.spring_boot_lezhnin.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import online.wgear.test.spring_boot_lezhnin.dao.CatalogRepository;
import online.wgear.test.spring_boot_lezhnin.model.Catalog;
import online.wgear.test.spring_boot_lezhnin.rest.error.CatalogNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@Validated
@Api(value="Employee Management System", description="Operations pertaining to employee in Employee Management System")
public class CatalogController {

    @Autowired
    CatalogRepository catalogDao;

    @PostMapping(value = {"/catalog/{parent}","/catalog"})
    ResponseEntity<Catalog> addCatalog(@PathVariable(value = "parent") Optional<Long> parent,
                                       @Valid @RequestBody Catalog catalog){
        if (parent.isPresent()){
            Catalog parentCatalog = catalogDao.findById(parent.get())
                    .orElseThrow(()->new CatalogNotFoundException(parent.get()));
            catalog.setParent(parentCatalog);
        }

        catalogDao.save(catalog);
        return ResponseEntity.ok(catalog);
    }

    @PutMapping("/catalog/{id}")
    ResponseEntity<Catalog> updateCatalog(@PathVariable("id") Long id,
                                          @Valid @RequestBody Catalog catalog){
        Catalog item = catalogDao.findById(id)
                .orElseThrow(()->new CatalogNotFoundException(id));

        item.setName(catalog.getName());
        catalogDao.save(item);

        return ResponseEntity.ok(item);
    }


    @ApiOperation(value = "View a list of available employees", response = Catalog.class)
    @GetMapping("/catalog/{id}")
    ResponseEntity<Catalog> getCatalog(@PathVariable("id")Long id){
        Catalog item = catalogDao.findById(id)
                .orElseThrow(()->new CatalogNotFoundException(id));

        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/catalog/{id}")
    ResponseEntity removeCatalog(@PathVariable("id") Long id){
        Catalog catalog = catalogDao.findById(id)
                .orElseThrow(()->new CatalogNotFoundException(id));

        catalogDao.delete(catalog);
        return ResponseEntity.ok(catalog);
    }
}
