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

    //============================================================
    //Definimos consturctor de Modelo a Recurso

    public IngredienteResource(IngredienteModel ingredienteModel) {

        super();

        this.id = ingredienteModel.getId();
        this.nameIngrediente = ingredienteModel.getNameIngrediente();

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

        return ingredienteModel;
    }

    //============================================================
}
