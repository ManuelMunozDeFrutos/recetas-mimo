package models;

import io.ebean.ExpressionList;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;

import java.sql.Timestamp;
import java.util.List;

@Entity
public class DescripcionModel extends Model
{
    //============================================================

    @Id
    private Long id;
    private String descripcionTexto;

    //Descripcion de la receta 1-1

    @OneToOne(mappedBy = "descripcion") //mappeado por Descripcion
    private RecetaModel parentReceta;

    //============================================================

    //Atributos de control  ---

    @Version
    private Long version;

    @WhenCreated
    private Timestamp whenCreated;

    @WhenModified
    private Timestamp whenModified;

    //============================================================

    //Objeto Finder para busqueda   ---

    public static final Finder<Long, DescripcionModel> finder = new Finder<>(DescripcionModel.class);

    //============================================================

    //Metodos del Finder para busqueda  ---

    public static List<DescripcionModel> findAllDescripciones()
    {
        ExpressionList<DescripcionModel> resultado =  finder.query()
                .where();

        //me va a devolver uno por nombre

        return resultado.findList();

    }

    public static DescripcionModel findDescripcionByName(String nameDescripcion)
    {
        ExpressionList<DescripcionModel> resultado =  finder.query()
                .where()
                .ieq("descripcionTexto", nameDescripcion);

        //me va a devolver uno por nombre

        return resultado.findOne();

    }

    //============================================================

    //Getters y Setters     ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Timestamp getWhenCreated() {
        return whenCreated;
    }

    public void setWhenCreated(Timestamp whenCreated) {
        this.whenCreated = whenCreated;
    }

    public Timestamp getWhenModified() {
        return whenModified;
    }

    public void setWhenModified(Timestamp whenModified) {
        this.whenModified = whenModified;
    }

    public String getDescripcionTexto() {
        return descripcionTexto;
    }

    public void setDescripcionTexto(String descripcionTexto) {
        this.descripcionTexto = descripcionTexto;
    }

    public RecetaModel getParentReceta() {
        return parentReceta;
    }

    public void setParentReceta(RecetaModel parentReceta) {
        this.parentReceta = parentReceta;
    }
}
