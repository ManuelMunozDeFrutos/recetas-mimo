package views;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.annotation.JsonIgnore;
import models.ComentarioModel;
import play.data.validation.Constraints;
import play.libs.Json;

public class ComentarioResource
{
    //============================================================

    //Recurso Cocinero

    @JsonIgnore
    private Long id;

    @Constraints.Required(message = "commentary_text_required")
    @Constraints.MinLength(value = 2, message = "commentary_text_min")
    @Constraints.MaxLength(value = 50, message = "commentary_text_max")
    private String textoComentario;


    //============================================================
    //Definimos consturctor de Modelo a Recurso

    public ComentarioResource(ComentarioModel comentarioModel) {

        super();

        this.id = comentarioModel.getId();
        this.textoComentario = comentarioModel.getTextoComentario();

    }

    public ComentarioResource() {
    }

    //============================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextoComentario() {
        return textoComentario;
    }

    public void setTextoComentario(String textoComentario) {
        this.textoComentario = textoComentario;
    }



    //============================================================

    public JsonNode toJson()
    {
        return Json.toJson(this);
    }

    //============================================================

    public ComentarioModel toModel()
    {
        ComentarioModel comentarioModel = new ComentarioModel();

        comentarioModel.setId(this.id);
        comentarioModel.setTextoComentario(this.textoComentario);

        return comentarioModel;
    }

    //============================================================
}
