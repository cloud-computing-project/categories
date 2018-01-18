package rso.project.categories.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import rso.project.categories.Categorie;
import rso.project.categories.Product;
import rso.project.categories.services.config.RestProperties;

import com.kumuluz.ee.discovery.annotations.DiscoverService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoriesBean {

    private Logger log = LogManager.getLogger(CategoriesBean.class.getName());

    @Inject
    private EntityManager em;

    private Client httpClient;

    @Inject
    private RestProperties restProperties;

    @Inject
    private CategoriesBean categoriesBean;

    @Inject
    @DiscoverService("products")
    private Optional<String> baseUrlProducts;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }

    public List<Categorie> getCategories() {

        TypedQuery<Categorie> query = em.createNamedQuery("Categorie.getAll", Categorie.class);

        return query.getResultList();

    }

    public List<Categorie> getCategories(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Categorie.class, queryParameters);

    }

    public Categorie getCategorie(String categorieId) {
        log.info("INSIDE GET CATEGORY");
        Categorie categorie = em.find(Categorie.class, categorieId);

        if (categorie == null) {
            throw new NotFoundException();
        }
        log.info("product service enabled: " + restProperties.isProductServiceEnabled());
        if (restProperties.isProductServiceEnabled()) {
            List<Product> products = categoriesBean.getProducts(categorieId);
            log.info("list of products: " + products.toString());
            categorie.setProducts(products);
            log.info("list of products for itemSpecificId: " + categorie.getProducts().toString());
        }

        return categorie;
    }

    public List<Product> getProducts(String categorieId) {
        log.info("base url products " + baseUrlProducts);
        if (baseUrlProducts.isPresent()) {
            try {
                return httpClient
                        .target(baseUrlProducts.get() + "/v1/products?where=categoryId:EQ:" + categorieId)
                        .request().get(new GenericType<List<Product>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.error(e);
                throw new InternalServerErrorException(e);
            }
        }

        return new ArrayList<>();
    }

    public List<Product> getProductsFallback(String customerId) {
        return new ArrayList<>();
    }

    public Categorie createCategorie(Categorie categorie) {

        try {
            beginTx();
            em.persist(categorie);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return categorie;
    }

    public Categorie putCategorie(String categorieId, Categorie categorie) {

        Categorie c = em.find(Categorie.class, categorieId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            categorie.setId(c.getId());
            categorie = em.merge(categorie);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return categorie;
    }

    public boolean deleteCategorie(String categorieId) {

        Categorie categorie = em.find(Categorie.class, categorieId);

        if (categorie != null) {
            try {
                beginTx();
                em.remove(categorie);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
