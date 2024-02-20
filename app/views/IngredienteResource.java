package views;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.annotation.JsonIgnore;
import models.IngredienteModel;
import play.data.validation.Constraints;
import play.libs.Json;

public class IngredienteResource
{
    //============================================================

    //Recurso Ingrediente

    @JsonIgnore
    private Long id;

    @Constraints.Required(message = "ingredient_name_required")
    @Constraints.MinLength(value = 2, message = "ingredient_name_min")
    @Constraints.MaxLength(value = 20, message = "ingredient_name_max")
    private String nameIngrediente;

    //@Constraints.Required(message = "La descripcion del Ingrediente es Obligatoria")
    //private String descripcion;

    //@Constraints.Required(message = "El tipo del Ingrediente es Obligatorio")
    //private String tipo;

    //============================================================
    //Definimos consturctor de Modelo a Recurso

    public IngredienteResource(IngredienteModel ingredienteModel) {

        super();

        this.id = ingredienteModel.getId();
        this.nameIngrediente = ingredienteModel.getNameIngrediente();
        //this.descripcion = ingredienteModel.getDescripcion();
        //this.tipo = ingredienteModel.getTipo();
    }

    public IngredienteResource() {
    }

    //============================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameIngrediente() {
        return nameIngrediente;
    }

    public void setNameIngrediente(String nameIngrediente) {
        this.nameIngrediente = nameIngrediente;
    }

    /*
    public String getDescripcion() {

        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    */
    //============================================================

    public JsonNode toJson()
    {
        return Json.toJson(this);
    }

    //============================================================

    public IngredienteModel toModel()
    {
        IngredienteModel ingredienteModel = new IngredienteModel();

        ingredienteModel.setId(this.id);
        ingredienteModel.setNameIngrediente(this.nameIngrediente);
        //ingredienteModel.setDescripcion(this.descripcion);
        //ingredienteModel.setTipo(this.tipo);

        return ingredienteModel;
    }

    //============================================================
}
