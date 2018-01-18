package rso.project.categories;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "categories")
@NamedQueries(value =
        {
                @NamedQuery(name = "Categorie.getAll", query = "SELECT o FROM categories o"),
                @NamedQuery(name = "Categorie.findByTitle", query = "SELECT o FROM categories o WHERE o.name = " +
                        ":name")
        })
@UuidGenerator(name = "idGenerator")
public class Categorie {

    @Id
    @GeneratedValue(generator = "idGenerator")
    private String id;

    private String name;

    private String description;

    @Transient
    private List<Product> products;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName(){return name;}

    public void setName(String name) {this.name = name;}

    public String getDescription(){return description;}

    public void setDescription(String description){this.description = description;}

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
