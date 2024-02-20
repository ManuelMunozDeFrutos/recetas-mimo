package models;

import io.ebean.ExpressionList;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Version;

import java.sql.Timestamp;
import java.util.List;

@Entity
public class IngredienteModel extends Model
{
    //============================================================

    @Id
    private Long id;
    private String nameIngrediente;
    //private String descripcion;
    //private String tipo;

    @ManyToMany(mappedBy = "ingredientes")
    private List<RecetaModel> recetas;

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

    public static final Finder<Long, IngredienteModel> finder = new Finder<>(IngredienteModel.class);

    //============================================================

    //Metodos del Finder para busqueda  ---

    public static List<IngredienteModel> findAllIngredientes()
    {
        ExpressionList<IngredienteModel> resultado =  finder.query()
                .where();

        //me va a devolver uno por nombre

        return resultado.findList();

    }

    //------------------

    public static IngredienteModel findIngredienteByID(Long id)
    {

        return finder.byId(id);

    }

    //------------------

    public static IngredienteModel findIngredienteByName(String nameIngrediente)
    {
        ExpressionList<IngredienteModel> resultado =  finder.query()
                                                            .where()
                                                            .icontains("nameIngrediente", nameIngrediente);

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

    public String getNameIngrediente() {
        return nameIngrediente;
    }

    public void setNameIngrediente(String nameIngrediente) {
        this.nameIngrediente = nameIngrediente;
    }

    public List<RecetaModel> getRecetas() {
        return recetas;
    }

    public void setRecetas(List<RecetaModel> recetas) {
        this.recetas = recetas;
    }

}
