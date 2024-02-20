package views;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.annotation.JsonIgnore;
import models.DescripcionModel;
import play.data.validation.Constraints;
import play.libs.Json;

public class DescripcionResource
{
    //============================================================

    //Recurso Descripcion

    @JsonIgnore
    private Long id;

    private String descripcionTexto;


    //============================================================
    //Definimos consturctor de Modelo a Recurso

    public DescripcionResource(DescripcionModel descripcionModel) {

        super();

        this.id = descripcionModel.getId();
        this.descripcionTexto = descripcionModel.getDescripcionTexto();
    }

    public DescripcionResource() {
    }

    //============================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcionTexto() {
        return descripcionTexto;
    }

    public void setDescripcionTexto(String descripcionTexto) {
        this.descripcionTexto = descripcionTexto;
    }


    //============================================================

    public JsonNode toJson()
    {
        return Json.toJson(this);
    }

    //============================================================

    public DescripcionModel toModel()
    {
        DescripcionModel descripcionModel = new DescripcionModel();

        descripcionModel.setId(this.id);
        descripcionModel.setDescripcionTexto(this.descripcionTexto);

        return descripcionModel;
    }

    //============================================================
}
