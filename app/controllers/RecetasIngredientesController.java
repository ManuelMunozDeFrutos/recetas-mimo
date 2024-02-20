package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.IngredienteModel;
import models.RecetaModel;
import models.DescripcionModel;
import models.ComentarioModel;

import play.cache.SyncCacheApi;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.*;
import play.twirl.api.Xml;

import views.IngredienteResource;
import views.RecetaResource;
import views.DescripcionResource;
import views.ComentarioResource;

import views.xml.ingrediente;
import views.xml.comentario;


import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecetasIngredientesController extends Controller
{
    //#============================================================#
    //#                       RECETAS-MIMO                         #
    //#                   MANUEL MUÑOZ DE FRUTOS                   #
    //#                                                            #
    //#               TECNOLOGIAS DEL LADO DEL SERVIDOR            #
    //#               CLOUD COMPUTING - SERVICIOS REST             #
    //#============================================================#

    /*

    PRUEBAS DE FUNCIONAMIENTO

    {
        "name": "Arroz con pollo",
        "categoria": "PRIMERO",
        "dificultad": "BAJA",
        "descripcion": "Arroz con pollo ligero para los amantes del Fitness",
        "comentario": [
            "Muy sencillo y facil de hacer",
            "Se hace muy rapido y queda muy bien"
        ],
        "ingredientesNames": [
            "Arroz",
            "Pollo",
            "Laurel",
            "Agua"
        ]
    }

    {
        "name": "Guiso de Patatas con Zanahorias y carne",
        "categoria": "PRIMERO",
        "dificultad": "ALTA",
        "descripcion": "Potente guiso de Patatas con Zanahorias",
        "comentario": [
            "Complicado de hacer pero buen resultado",
            "Mucho cuidado en el proceso",
            "Sofreir la carne antes de las patatas"
        ],
        "ingredientesNames": [
            "Patata",
            "Zanahoria",
            "Carne de Ternera",
            "Pimienta"
        ]
    }

    {
        "name": "Pescado al horno",
        "categoria": "SEGUNDO",
        "dificultad": "MEDIA",
        "descripcion": "Pieza de dorada al horno con patatas de acompañamiento",
        "comentario": [
            "Muy simple y muy rico",
            "Se me ha quemado"
        ],
        "ingredientesNames": [
            "Dorada",
            "Patata"
        ]
    }

    {
        "name": "Flan de Huevo",
        "categoria": "POSTRE",
        "dificultad": "BAJA",
        "descripcion": "Flan de huevo al estilo tradicional",
        "comentario": [
            "Como el que hacia mi abuela",
            "Se puede acompañar con nata",
            "No hacer si se quiere adelgazar"
        ],
        "ingredientesNames": [
            "Huevo",
            "Azucar",
            "Leche"
        ]
    }

    //Para actualizar indispensable pasarle el ID
    //Prueba de cambio git

    //Para guardar todos los cambios
    //git commit -a -m "mensaje"
    */

    // --- Factoria para formularios ---

    @Inject
    FormFactory formFactory;

    // --- Inyeccion de mensajes para I18N ---

    @Inject
    MessagesApi messagesApi;

    // --- Inyeccion para Cache ---

    @Inject
    private SyncCacheApi cache;


    //#--------------------------------#
    //#           APARTADO 1           #
    //#       GESTION DE RECETAS       #
    //#--------------------------------#

    //#--------------------------------#
    //#       METODOS GET RECETAS      #
    //#--------------------------------#

    //# ------ GET RECETAS                ------

    public Result getRecetas(Http.Request request)
    {
        System.out.println(" ");
        System.out.println("Consola - GET OBTENER RECETAS TODAS");
        System.out.println("Consola - NO SE PASAN PARAMETROS EN LA URL");
        System.out.println(" ");

        //Llamo a la funcion de busqueda por ID del MODEL

        List<RecetaModel> recetaModel = RecetaModel.findAllRecetas();

        if(recetaModel == null)
        {
            String errorMsg = messagesApi.preferred(request).at("recipes_not_found");

            ObjectNode obj = Json.newObject();
            obj.put("error", errorMsg);

            return Results.notFound(obj);
        }

        //Convertimos el modelo en recurso (toResource) Lo inverso a toModel con un Constructor

        List<RecetaResource> recetasRes = new ArrayList<>();

        for (RecetaModel i : recetaModel)
        {
            RecetaResource recetaRes = new RecetaResource(i);

            recetasRes.add(recetaRes);
        }

        //------------------
        //NEGOCIACION DE CONTENIDO

        if( request.header("Accept").isEmpty() )
        {
            return Results.notAcceptable();
        }

        if( request.accepts("application/json") )
        {
            //---------
            //PROCESAMIENTO JSON

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode json = objectMapper.valueToTree(recetasRes);

            return  ok(json)
                    .as("application/json");

            //---------
        }
        else if( request.accepts("application/xml") )
        {
            //---------
            //PROCESAMIENTO XML

            StringBuilder xmlBuilder = new StringBuilder();

            xmlBuilder.append("<recetas>");

            for (RecetaResource recetaRes : recetasRes)
            {
                xmlBuilder.append("<receta>");

                xmlBuilder.append("<id>").append(recetaRes.getId()).append("</id>");
                xmlBuilder.append("<name>").append(recetaRes.getName()).append("</name>");
                xmlBuilder.append("<categoria>").append(recetaRes.getCategoria()).append("</categoria>");
                xmlBuilder.append("<dificultad>").append(recetaRes.getDificultad()).append("</dificultad>");
                xmlBuilder.append("<comentarios>").append(recetaRes.getComentario()).append("</comentarios>");
                xmlBuilder.append("<descripcion>").append(recetaRes.getDescripcion()).append("</descripcion>");

                xmlBuilder.append("<ingredientes>");
                xmlBuilder.append(recetaRes.getIngredientesNames());
                xmlBuilder.append("</ingredientes>");

                xmlBuilder.append("</receta>");
            }

            xmlBuilder.append("</recetas>");

            return ok(xmlBuilder.toString()).as("application/xml");

            //---------
        }
        else
        {
            return Results.notAcceptable();
        }
        //------------------
    }

    //# ------ GET RECETA POR ID          ------

    public Result getRecetaId(Http.Request request, Long recetaId)
    {
        System.out.println(" ");
        System.out.println("Consola - GET OBTENER RECETA ID");
        System.out.println("Consola - ID PASADO POR PARAMETRO EN LA URL: " + recetaId );
        System.out.println(" ");

        //Leemos la cache
        Optional<Object> cachedReceta = cache.get("receta-" + recetaId);

        RecetaModel recetaModel;

        if(cachedReceta.isPresent())
        {
            System.out.println(" --- CACHE --- ");
            System.out.println("Consola - GET OBTENER RECETA ID - CACHE");
            System.out.println(" ------------- ");

            recetaModel = (RecetaModel) cachedReceta.get();
        }
        else
        {
            //Llamo a la funcion de busqueda por ID del MODEL

            recetaModel = RecetaModel.findRecetaByID(recetaId);
            if(recetaModel == null)
            {
                //No hay receta con ese ID

                String errorMsg = messagesApi.preferred(request).at("recipe_not_found");

                ObjectNode obj = Json.newObject();
                obj.put("error", errorMsg);

                return Results.notFound(obj);
            }

            //CACHE se guarda
            cache.set("receta-" + recetaId, recetaModel);
        }

        //Convertimos el modelo en recurso (toResource) Lo inverso a toModel con un Constructor

        RecetaResource recetaRes = new RecetaResource(recetaModel);

        //------------------
        //NEGOCIACION DE CONTENIDO

        if( request.header("Accept").isEmpty() )
        {
            return Results.notAcceptable();
        }

        if( request.accepts("application/json") )
        {
            //---------
            //PROCESAMIENTO JSON

            JsonNode json = recetaRes.toJson(); //En la clase IngredienteResource se procesa el Json

            return  ok(json)
                    .as("application/json");

            //---------
        }
        else if( request.accepts("application/xml") )
        {
            //---------
            //PROCESAMIENTO XML

            StringBuilder xmlBuilder = new StringBuilder();

            xmlBuilder.append("<receta>");
            xmlBuilder.append("<id>").append(recetaRes.getId()).append("</id>");
            xmlBuilder.append("<name>").append(recetaRes.getName()).append("</name>");
            xmlBuilder.append("<categoria>").append(recetaRes.getCategoria()).append("</categoria>");
            xmlBuilder.append("<dificultad>").append(recetaRes.getDificultad()).append("</dificultad>");
            xmlBuilder.append("<comentarios>").append(recetaRes.getComentario()).append("</comentarios>");
            xmlBuilder.append("<descripcion>").append(recetaRes.getDescripcion()).append("</descripcion>");

            xmlBuilder.append("<ingredientes>");
            xmlBuilder.append(recetaRes.getIngredientesNames());
            xmlBuilder.append("</ingredientes>");

            xmlBuilder.append("</receta>");


            return ok(xmlBuilder.toString()).as("application/xml");

            //---------
        }
        else
        {
            return Results.notAcceptable();
        }
        //------------------
    }

    //# ------ GET RECETA POR CATEGORIA      ------

    public Result getRecetaCategory(Http.Request request, String recetaCategory)
    {
        System.out.println(" ");
            System.out.println("Consola - GET OBTENER RECETA CATEGORIA");
            System.out.println("Consola - SE PASA EN LA URL EL NAME: " + recetaCategory);
        System.out.println(" ");

        Optional<Object> cachedReceta = cache.get("receta-" + recetaCategory);

        //Llamo a la funcion de busqueda por ID del MODEL

        List<RecetaModel> recetaModel;


        if(cachedReceta.isPresent())
        {
            System.out.println(" --- CACHE --- ");
            System.out.println("Consola - GET OBTENER RECETA CATEGORIA - CACHE");
            System.out.println(" ------------- ");

            recetaModel = (List<RecetaModel>) cachedReceta.get();
        }
        else
        {
            recetaModel = RecetaModel.findRecetaByCategory(recetaCategory);

            if(recetaModel == null)
            {
                String errorMsg = messagesApi.preferred(request).at("recipes_category_not_found");

                ObjectNode obj = Json.newObject();
                obj.put("error", errorMsg);

                return Results.notFound(obj);
            }

            cache.set("receta-" + recetaCategory, recetaModel);
        }


        List<RecetaResource> recetasRes = new ArrayList<>();

        for (RecetaModel i : recetaModel)
        {
            RecetaResource recetaRes = new RecetaResource(i);

            recetasRes.add(recetaRes);
        }

        //------------------
        //NEGOCIACION DE CONTENIDO

        if( request.header("Accept").isEmpty() )
        {
            return Results.notAcceptable();
        }

        if( request.accepts("application/json") )
        {
            //---------
            //PROCESAMIENTO JSON

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode json = objectMapper.valueToTree(recetasRes);

            return  ok(json)
                    .as("application/json");

            //---------
        }
        else if( request.accepts("application/xml") )
        {
            //---------
            //PROCESAMIENTO XML

            StringBuilder xmlBuilder = new StringBuilder();

            xmlBuilder.append("<recetas>");

            for (RecetaResource recetaRes : recetasRes)
            {
                xmlBuilder.append("<receta>");

                xmlBuilder.append("<id>").append(recetaRes.getId()).append("</id>");
                xmlBuilder.append("<name>").append(recetaRes.getName()).append("</name>");
                xmlBuilder.append("<categoria>").append(recetaRes.getCategoria()).append("</categoria>");
                xmlBuilder.append("<dificultad>").append(recetaRes.getDificultad()).append("</dificultad>");
                xmlBuilder.append("<comentarios>").append(recetaRes.getComentario()).append("</comentarios>");
                xmlBuilder.append("<descripcion>").append(recetaRes.getDescripcion()).append("</descripcion>");

                xmlBuilder.append("<ingredientes>");
                xmlBuilder.append(recetaRes.getIngredientesNames());
                xmlBuilder.append("</ingredientes>");

                xmlBuilder.append("</receta>");
            }

            xmlBuilder.append("</recetas>");

            return ok(xmlBuilder.toString()).as("application/xml");

            //---------
        }
        else
        {
            return Results.notAcceptable();
        }
        //------------------
    }

    //# ------ GET RECETA POR NOMBRE   ------

    public Result getRecetaName(Http.Request request, String recetaName)
    {
        System.out.println(" ");
        System.out.println("Consola - GET OBTENER RECETA NAME");
        System.out.println("Consola - SE PASA EN LA URL EL NAME: " + recetaName);
        System.out.println(" ");

        //Cache obtenemos el name
        Optional<Object> cachedReceta = cache.get("receta-" + recetaName);

        //Llamo a la funcion de busqueda por ID del MODEL

        List<RecetaModel> recetaModel;

        if(cachedReceta.isPresent())
        {
            System.out.println(" --- CACHE --- ");
            System.out.println("Consola - GET OBTENER RECETA NAME - CACHE");
            System.out.println(" ------------- ");

            recetaModel = (List<RecetaModel>) cachedReceta.get();
        }
        else
        {
            recetaModel = RecetaModel.findRecetaByName(recetaName);

            if(recetaModel == null)
            {
                String errorMsg = messagesApi.preferred(request).at("recipes_name_not_found");

                ObjectNode obj = Json.newObject();
                obj.put("error", errorMsg);

                return Results.notFound(obj);
            }

            //Cache
            cache.set("receta-" + recetaName, recetaModel);
        }

        //Convertimos el modelo en recurso (toResource) Lo inverso a toModel con un Constructor

        List<RecetaResource> recetasRes = new ArrayList<>();

        for (RecetaModel i : recetaModel)
        {
            RecetaResource recetaRes = new RecetaResource(i);

            recetasRes.add(recetaRes);
        }

        //------------------
        //NEGOCIACION DE CONTENIDO

        if( request.header("Accept").isEmpty() )
        {
            return Results.notAcceptable();
        }

        if( request.accepts("application/json") )
        {
            //---------
            //PROCESAMIENTO JSON

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode json = objectMapper.valueToTree(recetasRes);

            return  ok(json)
                    .as("application/json");

            //---------
        }
        else if( request.accepts("application/xml") )
        {
            //---------
            //PROCESAMIENTO XML

            StringBuilder xmlBuilder = new StringBuilder();

            xmlBuilder.append("<recetas>");

            for (RecetaResource recetaRes : recetasRes)
            {
                xmlBuilder.append("<receta>");

                xmlBuilder.append("<id>").append(recetaRes.getId()).append("</id>");
                xmlBuilder.append("<name>").append(recetaRes.getName()).append("</name>");
                xmlBuilder.append("<categoria>").append(recetaRes.getCategoria()).append("</categoria>");
                xmlBuilder.append("<dificultad>").append(recetaRes.getDificultad()).append("</dificultad>");
                xmlBuilder.append("<comentarios>").append(recetaRes.getComentario()).append("</comentarios>");
                xmlBuilder.append("<descripcion>").append(recetaRes.getDescripcion()).append("</descripcion>");

                xmlBuilder.append("<ingredientes>");
                xmlBuilder.append(recetaRes.getIngredientesNames());
                xmlBuilder.append("</ingredientes>");

                xmlBuilder.append("</receta>");
            }

            xmlBuilder.append("</recetas>");

            return ok(xmlBuilder.toString()).as("application/xml");

            //---------
        }
        else
        {
            return Results.notAcceptable();
        }
        //------------------
    }

    //#--------------------------------#
    //#       METODOS POST RECETAS     #
    //#--------------------------------#

    //# ------ POST CREAR RECETA          ------

    public Result createReceta(Http.Request request)
    {
        System.out.println(" ");
        System.out.println("Consola - POST CREAR RECETA");
        System.out.println("Consola - NO SE LE PASA NINGUN PARAMETRO EN LA URL" );
        System.out.println("Consola - Request Metodo: " + request.method() );
        System.out.println(" ------ ");

        //------------------
        //Para leer el body del POST en JSON

        JsonNode jsonBody = request.body().asJson();

        Form<RecetaResource> formReceta = formFactory.form(RecetaResource.class).bindFromRequest(request);

        //Si tiene errores

        if(formReceta.hasErrors())
        {
            JsonNode jsonErrores = formReceta.errorsAsJson();

            return Results.badRequest(jsonErrores);
        }

        RecetaResource recetaResource = formReceta.get();

        //Muestro el body por consola

        System.out.println("Consola - Body - RecetaResource - ID: " + recetaResource.getId() );
        System.out.println("Consola - Body - RecetaResource - NAME: " + recetaResource.getName() );
        System.out.println("Consola - Body - RecetaResource - CATEGORIA: " + recetaResource.getCategoria() );
        System.out.println("Consola - Body - RecetaResource - DIFICULTAD: " + recetaResource.getDificultad() );
        System.out.println(" ------ ");

        //---------
        //Para insertar en la base de datos

        //creamos la funcion toModel en IngredienteResource

        RecetaModel recetaModel = recetaResource.toModel();

        recetaModel.save();

        //---------

        RecetaResource outputRes = new RecetaResource(recetaModel);

        //---------

        //------------------

        System.out.println(" ");

        return Results
                .created(outputRes.toJson())
                .as("application/json");
    }

    //#--------------------------------#
    //#       METODOS PUT RECETAS      #
    //#--------------------------------#

    //# ------ PUT ACTUALIZAR RECETA      ------

    public Result updateReceta(Http.Request request, Long recetaId)
    {
        {
            System.out.println(" ");
            System.out.println("Consola - PUT UPDATE RECETA");
            System.out.println("Consola - ID DE LA RECETA QUE SE QUIERE ACTUALIZAR: " + recetaId );
            System.out.println("Consola - Request Metodo: " + request.method() );
            System.out.println(" ------ ");

            //------------------
            //Para leer el body del PUT en JSON

            JsonNode jsonBody = request.body().asJson();

            Form<RecetaResource> formReceta = formFactory.form(RecetaResource.class).bindFromRequest(request);

            //Si tiene errores

            if(formReceta.hasErrors())
            {
                JsonNode jsonErrores = formReceta.errorsAsJson();

                return Results.badRequest(jsonErrores);
            }

            RecetaResource recetaResource = formReceta.get();

            //Muestro el body por consola

            System.out.println("Consola - Body - RecetaResource - ID: " + recetaResource.getId() );
            System.out.println(" ------ ");

            //---------
            //Para insertar en la base de datos

            //creamos la funcion toModel en IngredienteResource

            RecetaModel recetaModel = recetaResource.toModel();

            recetaModel.update();

            //---------

            //------------------

            System.out.println(" ");

            return Results
                    .created(recetaResource.toJson())
                    .as("application/json");
        }
    }

    //#--------------------------------#
    //#       METODOS DELETE RECETAS   #
    //#--------------------------------#

    //# ------ DELETE ELIMINAR RECETA     ------

    public Result deleteReceta(Long recetaId)
    {
        System.out.println(" ");
        System.out.println("Consola - DELETE ELIMINAR RECETA ID");
        System.out.println("Consola - ID PASADO POR PARAMETRO EN LA URL: " + recetaId );
        System.out.println(" ");

        RecetaModel recetaModel = RecetaModel.findRecetaByID(recetaId);

        if(recetaModel == null)
        {
            return Results.notFound();
        }

        boolean ok = recetaModel.delete();
        return ok ? Results.ok() : Results.internalServerError();

    }

    //#============================================================
    //#============================================================

    //#------------------------------------#
    //#            APARTADO 2              #
    //#       GESTION DE INGREDIENTES      #
    //#------------------------------------#

    //#------------------------------------#
    //#       METODOS GET INGREDIENTES     #
    //#------------------------------------#

    //# ------ GET INGREDIENTES                ------

    public Result getIngredientes(Http.Request request)
    {
        System.out.println(" ");
        System.out.println("Consola - GET OBTENER INGREDIENTES TODOS");
        System.out.println("Consola - NO SE PASAN PARAMETROS EN LA URL");
        System.out.println(" ");

        //Llamo a la funcion de busqueda por ID del MODEL

        List<IngredienteModel> ingredienteModel = IngredienteModel.findAllIngredientes();

        if(ingredienteModel == null)
        {
            String errorMsg = messagesApi.preferred(request).at("ingredientes_not_found");

            ObjectNode obj = Json.newObject();
            obj.put("error", errorMsg);

            return Results.notFound(obj);
        }

        //Convertimos el modelo en recurso (toResource) Lo inverso a toModel con un Constructor

        List<IngredienteResource> ingredientesRes = new ArrayList<>();

        for (IngredienteModel i : ingredienteModel)
        {
            IngredienteResource ingredienteRes = new IngredienteResource(i);

            ingredientesRes.add(ingredienteRes);
        }

        //------------------
        //NEGOCIACION DE CONTENIDO

        if( request.header("Accept").isEmpty() )
        {
            return Results.notAcceptable();
        }

        if( request.accepts("application/json") )
        {
            //---------
            //PROCESAMIENTO JSON

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode json = objectMapper.valueToTree(ingredientesRes);

            return  ok(json)
                    .as("application/json");

            //---------
        }
        else if( request.accepts("application/xml") )
        {
            //---------
            //PROCESAMIENTO XML

            StringBuilder xmlBuilder = new StringBuilder();

            xmlBuilder.append("<ingredientes>");

            for (IngredienteResource ingredienteRes : ingredientesRes)
            {
                xmlBuilder.append("<ingrediente>");
                xmlBuilder.append("<id>").append(ingredienteRes.getId()).append("</id>");
                xmlBuilder.append("<name>").append(ingredienteRes.getNameIngrediente()).append("</name>");
                xmlBuilder.append("</ingrediente>");
            }

            xmlBuilder.append("</ingredientes>");

            return ok(xmlBuilder.toString()).as("application/xml");

            //---------
        }
        else
        {
            return Results.notAcceptable();
        }
        //------------------
    }

    //# ------ GET INGREDIENTE POR ID          ------

    public Result getIngredienteID(Http.Request request, Long ingredienteId)
    {
        System.out.println(" ");
        System.out.println("Consola - GET OBTENER INGREDIENTE ID");
        System.out.println("Consola - ID PASADO POR PARAMETRO EN LA URL: " + ingredienteId );
        System.out.println(" ");

        IngredienteModel ingredienteModel;

        Optional<Object> cachedIngrediente = cache.get("ingrediente-" + ingredienteId);

        if(cachedIngrediente.isPresent())
        {
            System.out.println(" --- CACHE --- ");
            System.out.println("Consola - GET INGREDIENTE ID - CACHE");
            System.out.println(" ------------- ");

            ingredienteModel = (IngredienteModel) cachedIngrediente.get();
        }
        else
        {
            //Llamo a la funcion de busqueda por ID del MODEL

            ingredienteModel = IngredienteModel.findIngredienteByID(ingredienteId);

            if(ingredienteModel == null)
            {
                String errorMsg = messagesApi.preferred(request).at("ingrediente_not_found");

                ObjectNode obj = Json.newObject();
                obj.put("error", errorMsg);

                return Results.notFound(obj);
            }

            cache.set("ingrediente-" + ingredienteId, ingredienteModel);
        }


        //Convertimos el modelo en recurso (toResource) Lo inverso a toModel con un Constructor

        IngredienteResource ingredienteRes = new IngredienteResource(ingredienteModel);

        //------------------
        //NEGOCIACION DE CONTENIDO

            if( request.header("Accept").isEmpty() )
            {
                return Results.notAcceptable();
            }

            if( request.accepts("application/json") )
            {
                //---------
                //PROCESAMIENTO JSON

                JsonNode json = ingredienteRes.toJson(); //En la clase IngredienteResource se procesa el Json

                //return ok("GET OBTENER INGREDIENTE ID - return Ok() ");

                return  ok(json)
                        .as("application/json");

                //---------
            }
            else if( request.accepts("application/xml") )
            {
                //---------
                //PROCESAMIENTO XML

                Xml contentXml = ingrediente.render(ingredienteRes.getId(), ingredienteRes.getNameIngrediente());

                return ok(contentXml);

                //return ok("GET OBTENER INGREDIENTE ID - return Ok() ");

                //---------
            }
            else
            {
                return Results.notAcceptable();
            }
        //------------------
    }

    //# ------ GET INGREDIENTE POR NOMBRE      ------

    public Result getIngredienteName(Http.Request request, String ingredienteName)
    {
        System.out.println(" ");
        System.out.println("Consola - GET OBTENER INGREDIENTE NAME");
        System.out.println("Consola - NAME PASADO POR PARAMETRO EN LA URL: " + ingredienteName );
        System.out.println(" ");

        IngredienteModel ingredienteModel;

        Optional<Object> cachedIngrediente = cache.get("ingrediente-" + ingredienteName);

        if(cachedIngrediente.isPresent())
        {
            System.out.println(" --- CACHE --- ");
            System.out.println("Consola - GET INGREDIENTE NAME - CACHE");
            System.out.println(" ------------- ");

            ingredienteModel = (IngredienteModel) cachedIngrediente.get();
        }
        else
        {
            //Llamo a la funcion de busqueda por ID del MODEL

            ingredienteModel = IngredienteModel.findIngredienteByName(ingredienteName);

            if(ingredienteModel == null)
            {
                String errorMsg = messagesApi.preferred(request).at("ingrediente_not_found");

                ObjectNode obj = Json.newObject();
                obj.put("error", errorMsg);

                return Results.notFound(obj);
            }

            cache.set("ingrediente-"+ ingredienteName, ingredienteModel);
        }

        //Convertimos el modelo en recurso (toResource) Lo inverso a toModel con un Constructor

        IngredienteResource ingredienteRes = new IngredienteResource(ingredienteModel);

        //------------------
        //NEGOCIACION DE CONTENIDO

        if( request.header("Accept").isEmpty() )
        {
            return Results.notAcceptable();
        }

        if( request.accepts("application/json") )
        {
            //---------
            //PROCESAMIENTO JSON

            JsonNode json = ingredienteRes.toJson(); //En la clase IngredienteResource se procesa el Json

            //return ok("GET OBTENER INGREDIENTE ID - return Ok() ");

            return  ok(json)
                    .as("application/json");

            //---------
        }
        else if( request.accepts("application/xml") )
        {
            //---------
            //PROCESAMIENTO XML

            //Xml contentXml = ingrediente.render(ingredienteRes.getId(), ingredienteRes.getName(), ingredienteRes.getDescripcion(), ingredienteRes.getTipo());
            Xml contentXml = ingrediente.render(ingredienteRes.getId(), ingredienteRes.getNameIngrediente());

            return ok(contentXml);

            //return ok("GET OBTENER INGREDIENTE ID - return Ok() ");

            //---------
        }
        else
        {
            return Results.notAcceptable();
        }
    }

    //#------------------------------------#
    //#       METODOS POST INGREDIENTES    #
    //#------------------------------------#

    //# ------ POST CREAR INGREDIENTE          ------

    public Result createIngrediente(Http.Request request)
    {
        System.out.println(" ");
        System.out.println("Consola - POST CREAR INGREDIENTE");
        System.out.println("Consola - NO SE LE PASA NINGUN PARAMETRO EN LA URL" );
        System.out.println("Consola - Request Metodo: " + request.method() );
        System.out.println(" ------ ");

        //------------------
        //Para leer el body del POST en JSON

        JsonNode jsonBody = request.body().asJson();

        Form<IngredienteResource> formIngrediente = formFactory.form(IngredienteResource.class).bindFromRequest(request);

        //Si tiene errores

        if(formIngrediente.hasErrors())
        {
            JsonNode jsonErrores = formIngrediente.errorsAsJson();

            return Results.badRequest(jsonErrores);
        }

        IngredienteResource ingredienteResource = formIngrediente.get();

        //Muestro el body por consola

        System.out.println("Consola - Body - IngredienteResource - ID: " + ingredienteResource.getId() );
        System.out.println("Consola - Body - IngredienteResource - NAME: " + ingredienteResource.getNameIngrediente() );
        System.out.println(" ------ ");

        //---------
        //Para insertar en la base de datos

            //creamos la funcion toModel en IngredienteResource

            IngredienteModel ingredienteModel = ingredienteResource.toModel();

            ingredienteModel.save();

        //---------

        //------------------

        System.out.println(" ");

        return Results
                .created(ingredienteResource.toJson())
                .as("application/json");
    }

    //#------------------------------------#
    //#       METODOS PUT INGREDIENTES     #
    //#------------------------------------#

    //# ------ PUT ACTUALIZAR INGREDIENTE      ------

    public Result updateIngrediente(Http.Request request, Long ingredienteId)
    {
        {
            System.out.println(" ");
            System.out.println("Consola - PUT UPDATE INGREDIENTE");
            System.out.println("Consola - ID DEL INGREDIENTE QUE SE QUIERE ACTUALIZAR: " + ingredienteId );
            System.out.println("Consola - Request Metodo: " + request.method() );
            System.out.println(" ------ ");

            //------------------
            //Para leer el body del PUT en JSON

            JsonNode jsonBody = request.body().asJson();

            Form<IngredienteResource> formIngrediente = formFactory.form(IngredienteResource.class).bindFromRequest(request);

            //Si tiene errores

            if(formIngrediente.hasErrors())
            {
                JsonNode jsonErrores = formIngrediente.errorsAsJson();

                return Results.badRequest(jsonErrores);
            }

            IngredienteResource ingredienteResource = formIngrediente.get();

            //Muestro el body por consola

            System.out.println("Consola - Body - IngredienteResource - ID: " + ingredienteResource.getId() );
            System.out.println("Consola - Body - IngredienteResource - NAME: " + ingredienteResource.getNameIngrediente() );
            System.out.println(" ------ ");

            //---------
            //Para insertar en la base de datos

            //creamos la funcion toModel en IngredienteResource

            IngredienteModel ingredienteModel = ingredienteResource.toModel();

            ingredienteModel.update();

            //---------

            //------------------

            System.out.println(" ");

            return Results
                    .created(ingredienteResource.toJson())
                    .as("application/json");
        }
    }

    //#------------------------------------#
    //#     METODOS DELETE INGREDIENTES    #
    //#------------------------------------#

    //# ------ DELETE ELIMINAR INGREDIENTE     ------

    public Result deleteIngrediente(Long ingredienteId)
    {
        System.out.println(" ");
        System.out.println("Consola - DELETE ELIMINAR INGREDIENTE ID");
        System.out.println("Consola - ID PASADO POR PARAMETRO EN LA URL: " + ingredienteId );
        System.out.println(" ");

        IngredienteModel ingredienteModel = IngredienteModel.findIngredienteByID(ingredienteId);

        if(ingredienteModel == null)
        {
            return Results.notFound();
        }

        boolean ok = ingredienteModel.delete();
        return ok ? Results.ok() : Results.internalServerError();

    }

    //#============================================================
    //#============================================================

        //#------------------------------------#
        //#            APARTADO 3              #
        //#       GESTION DE LOS COMENTARIOS   #
        //#------------------------------------#

    //# ------ GET COMENTARIOs                ------

    public Result getComentarios(Http.Request request)
    {
        System.out.println(" ");
        System.out.println("Consola - GET OBTENER COMENTARIOS TODOS");
        System.out.println("Consola - NO SE PASAN PARAMETROS EN LA URL");
        System.out.println(" ");

        //Llamo a la funcion de busqueda por ID del MODEL

        List<ComentarioModel> comentarioModel = ComentarioModel.findAllComentarios();

        if(comentarioModel == null)
        {
            String errorMsg = messagesApi.preferred(request).at("commentaries_not_found");

            ObjectNode obj = Json.newObject();
            obj.put("error", errorMsg);

            return Results.notFound(obj);
        }

        List<ComentarioResource> comentariosRes = new ArrayList<>();

        for (ComentarioModel i : comentarioModel)
        {
            ComentarioResource comentarioRes = new ComentarioResource(i);

            comentariosRes.add(comentarioRes);
        }

        //------------------
        //NEGOCIACION DE CONTENIDO

        if( request.header("Accept").isEmpty() )
        {
            return Results.notAcceptable();
        }

        if( request.accepts("application/json") )
        {
            //---------
            //PROCESAMIENTO JSON

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode json = objectMapper.valueToTree(comentariosRes);

            return  ok(json)
                    .as("application/json");

            //---------
        }
        else if( request.accepts("application/xml") )
        {
            //---------
            //PROCESAMIENTO XML

            StringBuilder xmlBuilder = new StringBuilder();

            xmlBuilder.append("<comentarios>");

            for (ComentarioResource comentarioRes : comentariosRes)
            {
                xmlBuilder.append("<comentario>");
                xmlBuilder.append("<id>").append(comentarioRes.getId()).append("</id>");
                xmlBuilder.append("<text>").append(comentarioRes.getTextoComentario()).append("</text>");
                xmlBuilder.append("</comentario>");
            }

            xmlBuilder.append("</comentarios>");

            return ok(xmlBuilder.toString()).as("application/xml");

            //---------
        }
        else
        {
            return Results.notAcceptable();
        }
        //------------------
    }

    //# ------ GET COMENTARIO POR TEXTO      ------

    public Result getComentarioText(Http.Request request, String comentarioName)
    {
        System.out.println(" ");
        System.out.println("Consola - GET OBTENER COMENTARIO NAME");
        System.out.println("Consola - TEXTO PASADO POR PARAMETRO EN LA URL: " + comentarioName );
        System.out.println(" ");

        ComentarioModel comentarioModel;

        Optional<Object> cachedComentario = cache.get("comentario-" + comentarioName);

        if(cachedComentario.isPresent())
        {
            System.out.println(" --- CACHE --- ");
            System.out.println("Consola - GET COMENTARIO TEXT - CACHE");
            System.out.println(" ------------- ");
            comentarioModel = (ComentarioModel) cachedComentario.get();
        }
        else
        {
            comentarioModel = ComentarioModel.findComentarioPorName(comentarioName);

            if(comentarioModel == null)
            {
                String errorMsg = messagesApi.preferred(request).at("commentary_not_found");

                ObjectNode obj = Json.newObject();
                obj.put("error", errorMsg);

                return Results.notFound(obj);
            }

            cache.set("comentario-" + comentarioName, comentarioModel);
        }

        ComentarioResource comentarioRes = new ComentarioResource(comentarioModel);

        //------------------
        //NEGOCIACION DE CONTENIDO

        if( request.header("Accept").isEmpty() )
        {
            return Results.notAcceptable();
        }

        if( request.accepts("application/json") )
        {
            //---------
            //PROCESAMIENTO JSON

            JsonNode json = comentarioRes.toJson();

            return  ok(json)
                    .as("application/json");

            //---------
        }
        else if( request.accepts("application/xml") )
        {
            //---------
            //PROCESAMIENTO XML

            Xml contentXml = comentario.render(comentarioRes.getId(), comentarioRes.getTextoComentario());

            return ok(contentXml);

            //return ok("GET OBTENER INGREDIENTE ID - return Ok() ");

            //---------
        }
        else
        {
            return Results.notAcceptable();
        }
    }

    //#============================================================
    //#============================================================
}
