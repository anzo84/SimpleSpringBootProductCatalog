package online.wgear.test.spring_boot_lezhnin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="product_gen")
    @SequenceGenerator(name="product_gen",sequenceName="product_seq", allocationSize=1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotEmpty(message = "Product cannot have empty name")
    @Size(max = 255,message = "Name of product cannot exceed 255 characters")
    @Column(name = "name",nullable = false)
    private String name;

    @NotEmpty(message = "Product cannot have empty UOM")
    @Size(max = 50,message = "UOM of product cannot exceed 50 characters")
    @Column(name = "uom",nullable = false)
    private String uom;

    @ManyToOne
    @JoinColumn(name = "catalog_id")
    @JsonIgnoreProperties({"children","parent","products"})
    public Catalog catalog;
}
