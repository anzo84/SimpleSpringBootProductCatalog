package online.wgear.test.spring_boot_lezhnin.dao;

import online.wgear.test.spring_boot_lezhnin.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog,Long> {
}
