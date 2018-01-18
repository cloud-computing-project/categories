package rso.project.categories.api.v1.resources;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import rso.project.categories.Categorie;
import rso.project.categories.services.CategoriesBean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@RequestScoped
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriesResource {

    @Context
    private UriInfo uriInfo;

    private Logger log = LogManager.getLogger(CategoriesBean.class.getName());

    @Inject
    private CategoriesBean categoriesBean;

    @GET
    public Response getCategories() {

        List<Categorie> categories = categoriesBean.getCategories(uriInfo);

        return Response.ok(categories).build();
    }

    @GET
    @Path("/{categorieId}")
    public Response getCategorie(@PathParam("categorieId") String categorieId) {
        log.info("get categorie resource");
        Categorie categorie = categoriesBean.getCategorie(categorieId);

        if (categorie == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(categorie).build();
    }

    @POST
    public Response createCategorie(Categorie categorie) {

        if (categorie.getName() == null || categorie.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            categorie = categoriesBean.createCategorie(categorie);
        }

        if (categorie.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(categorie).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(categorie).build();
        }
    }

    @PUT
    @Path("{categorieId}")
    public Response putCategorie(@PathParam("categorieId") String categorieId, Categorie categorie) {

        categorie = categoriesBean.putCategorie(categorieId, categorie);

        if (categorie == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (categorie.getId() != null)
                return Response.status(Response.Status.OK).entity(categorie).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @DELETE
    @Path("{categorieId}")
    public Response deleteCustomer(@PathParam("categorieId") String categorieId) {

        boolean deleted = categoriesBean.deleteCategorie(categorieId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
