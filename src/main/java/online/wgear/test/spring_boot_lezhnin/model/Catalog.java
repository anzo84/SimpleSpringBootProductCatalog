package online.wgear.test.spring_boot_lezhnin.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "catalogs")
@Data
@EqualsAndHashCode(exclude = {"parent", "children", "products"})
@ApiModel(description = "Catalog folder in product tree")
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="catalog_gen")
    @SequenceGenerator(name="catalog_gen",sequenceName="catalog_seq", allocationSize=1)
    @Column(name = "id", updatable = false, nullable = false)
    @ApiModelProperty(notes = "The database generated employee catalog ID")
    private Long id;

    @NotEmpty(message = "Catalog cannot have empty name")
    @Size(max = 255,message = "Name of catalog cannot exceed 255 characters")
    @Column(name = "name",nullable = false)
    @ApiModelProperty(notes = "Name of catalog")
    private String name;

    @ManyToOne()
    @JoinColumn(name="id_parent")
    @JsonIgnoreProperties({"children","parent","products"})
    @ApiModelProperty(notes = "ID of parent catalog")
    public Catalog parent;

    @OneToMany(mappedBy="parent",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnoreProperties({"parent","products","children"})
    public List<Catalog> children = new ArrayList<>();

    @OneToMany(mappedBy = "catalog",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonIgnoreProperties("catalog")
    private  List<Product> products = new ArrayList<>();
}
