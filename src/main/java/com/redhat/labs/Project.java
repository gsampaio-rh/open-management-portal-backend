package com.redhat.labs;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

@Path("/engagements")
@Operation(summary = "Path used to manage the list of engagements. A resdiency can be an engagement.",
           description = "The REST endpoint/path used to list and create zero or more `residency`"+
           "entities.  This path contains a `GET` and `POST` operation to perform the list"+
           "and create tasks, respectively. When used to list all residencies, a truncated"+
           "form of each residency called a \"stub\" is returned.")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class Project {
    @Inject
    JsonWebToken jwt;

    @Inject
    @RestClient
    OMPGitLabAPIService ompGitLabAPIService;

    @ConfigProperty(name = "configRepositoryId",defaultValue = "9407")
    String configRepositoryId;

    @GET
    @Operation(summary = "List all residencies in \"stub\" form",
           description = "Gets a list of all `residency_stub` entities")
    @APIResponse(responseCode = "200", description = "Successful response - returns an array of `residency_stub`",
             content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = "residency_stub")))
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll
    public String defaultEndpoint(@Context SecurityContext ctx) {
        Principal caller = ctx.getUserPrincipal();
        String name = caller == null ? "anonymous" : caller.getName();
        String helloReply = String.format("hello + %s, isSecure: %s, authScheme: %s", name, ctx.isSecure(), ctx.getAuthenticationScheme());
        return helloReply;
    }

    @GET
    @Path("open")
    @PermitAll
    public String openEndpoint(@Context SecurityContext ctx) {
        return Json.createObjectBuilder().add("hello", "world").build().toString();
    }

    @GET
    @Path("secure")
    @Produces(MediaType.TEXT_PLAIN)
    public String securedEndpoint(@Context SecurityContext ctx) {
        return jwt.getName();
    }

    @GET
    @Path("config")
    @PermitAll
    public String fetchConfigDataFromCache(@Context SecurityContext ctx) {
    //TODO - 🤠 Make this call to cache and not to the GitLab Api thingy to get file directly
        return ompGitLabAPIService.getFile("schema/config.yml", configRepositoryId).readEntity(String.class);
    }


    @POST
    @Path("create")
    @HeaderParam("X-APPLICATION-NONSENSE")
    public String createNewResidency(@Context SecurityContext ctx, Object request,@HeaderParam("X-APPLICATION-NONSENSE") String header ) {
        String skullyResponse = Json.createObjectBuilder().add("OK", "☠️ \uD83D\uDD25 \uD83D\uDE92 \uD83D\uDE92 \uD83D\uDD25 ☠️").add("clickMe", "https://www.myinstants.com/media/instants_images/ahahahreal.gif").build().toString();
        // TODO - tidy this up to remove the 200 status code and do a real check with a token etc....
        if (!header.equals(trustedClientKey)){
            return Response.status(Response.Status.UNAUTHORIZED).entity(skullyResponse).build().readEntity(String.class);

        } else
            return ompGitLabAPIService.createNewResidency(request).readEntity(String.class);
    }

// TODO - Add this in when needed #YOLO
//    @Inject
//    ResidencyDataCache residencyDataCache;



    @ConfigProperty(name = "trustedClientKey")
    public String trustedClientKey;



}