package online.wgear.test.spring_boot_lezhnin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Table(name = "products")
@ApiModel(description = "Product of catalog")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="product_gen")
    @SequenceGenerator(name="product_gen",sequenceName="product_seq", allocationSize=1)
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated product ID")
    @Getter
    @Setter
    private Long id;

    @NotEmpty(message = "Product cannot have empty name")
    @Size(max = 255,message = "Name of product cannot exceed 255 characters")
    @Column(name = "name",nullable = false)
    @ApiModelProperty(notes = "Name of product",required = true)
    @Getter @Setter
    private String name;

    @NotEmpty(message = "Product cannot have empty UOM")
    @Size(max = 50,message = "UOM of product cannot exceed 50 characters")
    @Column(name = "uom",nullable = false)
    @ApiModelProperty(notes = "UOM of product",required = true)
    @Getter @Setter
    private String uom;

    @ManyToOne
    @JoinColumn(name = "catalog_id")
    @JsonIgnoreProperties({"children","parent","products"})
    @ApiModelProperty(notes = "Parent catalog of product",required = true)
    @Getter @Setter
    public Catalog catalog;
}
