package online.wgear.test.spring_boot_lezhnin.dao;

import online.wgear.test.spring_boot_lezhnin.model.Catalog;
import online.wgear.test.spring_boot_lezhnin.model.CatalogList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog,Long> {
    //CatalogList findAllByParentIs(Catalog parent);
    CatalogList findByParentIs(Catalog parent);
}
