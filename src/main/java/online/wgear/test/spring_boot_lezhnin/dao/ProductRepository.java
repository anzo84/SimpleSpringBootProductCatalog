package online.wgear.test.spring_boot_lezhnin.dao;

import online.wgear.test.spring_boot_lezhnin.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
}
