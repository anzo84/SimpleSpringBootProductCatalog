package online.wgear.test.spring_boot_lezhnin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "catalogs")
@ApiModel(description = "Catalog folder in product tree")
public class Catalog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="catalog_gen")
    @SequenceGenerator(name="catalog_gen",sequenceName="catalog_seq", allocationSize=1)
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated catalog ID")
    @Getter @Setter
    private Long id;

    @NotEmpty(message = "Catalog cannot have empty name")
    @Size(max = 255,message = "Name of catalog cannot exceed 255 characters")
    @Column(name = "name",nullable = false)
    @ApiModelProperty(notes = "Name of catalog",required = true)
    @Getter @Setter
    private String name;

    @ManyToOne()
    @JoinColumn(name="id_parent")
    @JsonIgnoreProperties({"children","parent","products"})
    @ApiModelProperty(notes = "Parent catalog node. NULL if catalog in root level.")
    @Getter @Setter
    public Catalog parent;

    @OneToMany(mappedBy="parent",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnoreProperties({"parent","products","children"})
    @ApiModelProperty(notes = "List of children nodes of catalog")
    @Getter @Setter
    public List<Catalog> children = new ArrayList<>();

    @OneToMany(mappedBy = "catalog",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnoreProperties("catalog")
    @ApiModelProperty(notes = "Products in the catalog")
    @Getter @Setter
    private  List<Product> products = new ArrayList<>();
}
