package online.wgear.test.spring_boot_lezhnin.rest;

import io.swagger.annotations.*;
import online.wgear.test.spring_boot_lezhnin.dao.CatalogRepository;
import online.wgear.test.spring_boot_lezhnin.model.Catalog;
import online.wgear.test.spring_boot_lezhnin.model.CatalogList;
import online.wgear.test.spring_boot_lezhnin.rest.error.CatalogNotFoundException;
import online.wgear.test.spring_boot_lezhnin.rest.error.LoopingErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@Validated
@Api(value="Catalog tree management system")
public class CatalogController {

    @Autowired
    CatalogRepository catalogDao;

    @PostMapping(value = {"/catalog/{parent}","/catalog"})
    @ApiOperation(value = "Add catalog", response = Catalog.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully add catalog"),
            @ApiResponse(code = 400, message = "Wrong parameter format"),
            @ApiResponse(code = 404, message = "Parent catalog not found")
    })
    ResponseEntity<Catalog> addCatalog(
            @ApiParam(value = "ID of parent catalog node. If not specified, " +
                              "then the new catalog will be added to the root")
            @PathVariable(value = "parent") Optional<Long> parent,
            @Valid @RequestBody Catalog catalog){
        if (parent.isPresent()){
            Catalog parentCatalog = catalogDao.findById(parent.get())
                    .orElseThrow(()->new CatalogNotFoundException(parent.get()));
            catalog.setParent(parentCatalog);
        }

        catalogDao.save(catalog);
        return ResponseEntity.status(HttpStatus.CREATED).body(catalog);
    }

    @PutMapping("/catalog/{id}")
    @ApiOperation(value = "Update catalog", response = Catalog.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully update catalog"),
            @ApiResponse(code = 400, message = "Wrong parameter format"),
            @ApiResponse(code = 404, message = "Catalog not found")
    })
    ResponseEntity<Catalog> updateCatalog(
            @ApiParam(value = "ID of catalog node for update.",required = true)
            @PathVariable("id") Long id,
            @Valid @RequestBody Catalog catalog){
        Catalog item = catalogDao.findById(id)
                .orElseThrow(()->new CatalogNotFoundException(id));

        item.setName(catalog.getName());
        catalogDao.save(item);

        return ResponseEntity.ok(item);
    }

    @PutMapping({"/catalog/{id}/move/{parent}","/catalog/{id}/move"})
    @ApiOperation(value = "Move catalog. (Change the parent of catalog node)", response = Catalog.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully moved catalog"),
            @ApiResponse(code = 400, message = "Wrong parameter format"),
            @ApiResponse(code = 404, message = "Catalog not found")
    })
    ResponseEntity<Catalog> moveCatalog(
            @ApiParam(value = "ID of catalog node for moving.",required = true)
            @PathVariable("id") Long id,
            @ApiParam(value = "New parent ID. If not specified, " +
                              "then the catalog will be moved to the root")
            @PathVariable("parent") Optional<Long> parentId){

        Catalog targetCatalog = catalogDao.findById(id)
                .orElseThrow(()->new CatalogNotFoundException(id));

        if (parentId.isPresent()){
            Catalog newParent = catalogDao.findById(parentId.get())
                    .orElseThrow(()->new CatalogNotFoundException(parentId.get()));

            //Check for looping
            Catalog tmpCatalog = newParent;
            while (tmpCatalog != null){
                if (tmpCatalog.getId().equals(targetCatalog.getId())){
                    throw new LoopingErrorException();
                }

                tmpCatalog = tmpCatalog.getParent();
            }

            targetCatalog.setParent(newParent);
            targetCatalog = catalogDao.save(targetCatalog);
        }
        else if (targetCatalog.getParent()!=null){
            targetCatalog.setParent(null);
            targetCatalog = catalogDao.save(targetCatalog);
        }

        return ResponseEntity.ok(targetCatalog);
    }

    @GetMapping("/catalog/{id}")
    @ApiOperation(value = "Get catalog node", response = Catalog.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully"),
            @ApiResponse(code = 400, message = "Wrong parameter format"),
            @ApiResponse(code = 404, message = "Catalog not found")
    })
    ResponseEntity<Catalog> getCatalog(
            @ApiParam(value = "ID of catalog node",required = true)
            @PathVariable("id") Long id){
        Catalog item = catalogDao.findById(id)
                .orElseThrow(()->new CatalogNotFoundException(id));

        return ResponseEntity.ok(item);
    }

    @GetMapping({"/catalogs/{parentid}","/catalogs"})
    @ApiOperation(value = "Get catalogs list by parend ID. If parent ID not specified, " +
                          "then will be will be transferred to the root nodes",
                  response = CatalogList.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully"),
            @ApiResponse(code = 400, message = "Wrong parameter format"),
            @ApiResponse(code = 404, message = "Catalog not found")
    })
    ResponseEntity<CatalogList> getCatalogs(
            @ApiParam(value = "ID of parent catalog node")
            @PathVariable(value = "parentid") Optional<Long> parentId){
        Catalog parentCatalog = null;

        if (parentId.isPresent()){
            parentCatalog = catalogDao.findById(parentId.get())
                    .orElseThrow(()->new CatalogNotFoundException(parentId.get()));
        }

        CatalogList list = catalogDao.findByParentIs(parentCatalog);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/catalog/{id}")
    @ApiOperation(value = "Remove catalog node with all products and subnodes", response = Catalog.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully removed catalog"),
            @ApiResponse(code = 400, message = "Wrong parameter format"),
            @ApiResponse(code = 404, message = "Catalog not found")
    })
    ResponseEntity removeCatalog(
            @ApiParam(value = "ID of catalog node for remove",required = true)
            @PathVariable("id") Long id){
        Catalog catalog = catalogDao.findById(id)
                .orElseThrow(()->new CatalogNotFoundException(id));

        catalogDao.delete(catalog);
        return ResponseEntity.ok(catalog);
    }
}
