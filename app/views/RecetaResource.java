package views;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.annotation.JsonIgnore;
import models.ComentarioModel;
import models.DescripcionModel;
import models.IngredienteModel;
import models.RecetaModel;
import play.data.validation.Constraints;
import play.libs.Json;
import validators.CategoriaValidator;
import validators.DificultadValidator;

import java.util.List;
import java.util.ArrayList;


public class RecetaResource
{
    //============================================================

    //Recurso RECETAS

    @JsonIgnore
    private Long id;

    @Constraints.Required(message = "recipe_name_required")
    //@Constraints.MinLength(1)
    @Constraints.MinLength(value = 2, message = "recipe_name_min")
    @Constraints.MaxLength(value = 50, message = "recipe_name_max")
    private String name;

    @Constraints.ValidateWith(CategoriaValidator.class)
    private String categoria;

    @Constraints.ValidateWith(DificultadValidator.class)
    private String dificultad;

    //Nuevo campo de la relacion 1-1
    @Constraints.Required(message = "recipe_description_required")
    @Constraints.MinLength(value = 2, message = "recipe_description_min")
    @Constraints.MaxLength(value = 100, message = "recipe_description_max")
    private String descripcion;

    //Nuevo campo relacion 1-N

    @Constraints.Required(message = "recipe_commentary_required")
    private List<String> comentario = new ArrayList<>();

    @Constraints.Required(message = "recipe_ingredients_required")
    private List<String> ingredientesNames = new ArrayList<>();

    //============================================================
    //Definimos consturctor de Modelo a Recurso

    public RecetaResource(RecetaModel recetaModel) {

        super();

        this.id = recetaModel.getId();
        this.name = recetaModel.getName();
        this.categoria = recetaModel.getCategoria();
        this.dificultad = recetaModel.getDificultad();
        this.descripcion = recetaModel.getDescripcion().getDescripcionTexto(); //Coger el texto

        //this.cocinero = recetaModel.

        for(ComentarioModel com : recetaModel.getComentarios())
        {
            comentario.add(com.getTextoComentario());
        }

        for(IngredienteModel ing : recetaModel.getIngredientes())
        {
            ingredientesNames.add(ing.getNameIngrediente());
        }
    }

    public RecetaResource() {
    }

    //============================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getComentario() {
        return comentario;
    }

    public void setComentario(List<String> comentario) {
        this.comentario = comentario;
    }

    public List<String> getIngredientesNames() {
        return ingredientesNames;
    }

    public void setIngredientesNames(List<String> ingredientesNames) {
        this.ingredientesNames = ingredientesNames;
    }

    //============================================================

    public JsonNode toJson()
    {
        return Json.toJson(this);
    }

    //============================================================

    public RecetaModel toModel()
    {
        RecetaModel recetaModel = new RecetaModel();

        recetaModel.setId(this.id);
        recetaModel.setName(this.name);
        recetaModel.setCategoria(this.categoria);
        recetaModel.setDificultad(this.dificultad);

        //-----
        //Necesito generar su descripcion

            DescripcionModel descripcion = new DescripcionModel();

            //enlazamos modelos

            descripcion.setDescripcionTexto(this.descripcion);
            descripcion.setParentReceta(recetaModel);
        //-----

        recetaModel.setDescripcion(descripcion);

        //-----
            //Necesito generar su Comentario

            for(String comentarioText : comentario)
            {
                ComentarioModel com = ComentarioModel.findComentarioPorName(comentarioText);

                if(com == null)
                {
                    com = new ComentarioModel();
                    com.setTextoComentario(comentarioText);
                }
                com.setReceta(recetaModel);
                recetaModel.getComentarios().add(com);

            }

        //-----
            //Necesito generar sus ingredientes

            for(String ing : ingredientesNames)
            {
                recetaModel.addIngrediente(ing);
            }


        //-----

        return recetaModel;
    }

    //============================================================
}
