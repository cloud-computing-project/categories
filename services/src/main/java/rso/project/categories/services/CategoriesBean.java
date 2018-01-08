package rso.project.categories.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import rso.project.categories.Categorie;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@ApplicationScoped
public class CategoriesBean {

    @Inject
    private EntityManager em;

    public List<Categorie> getCategories(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Categorie.class, queryParameters);

    }

    public Categorie getCategorie(String categorieId) {

        Categorie categorie = em.find(Categorie.class, categorieId);

        if (categorie == null) {
            throw new NotFoundException();
        }

        return categorie;
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
