package models;

import io.ebean.ExpressionList;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
public class ComentarioModel extends Model
{
    //============================================================

    @Id
    private Long id;
    private String textoComentario;

    //Conexion con las recetas 1-N
    @ManyToOne
    private RecetaModel receta;


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

    public static final Finder<Long, ComentarioModel> finder = new Finder<>(ComentarioModel.class);

    //============================================================

    //Metodos del Finder para busqueda  ---

    public static List<ComentarioModel> findAllComentarios()
    {
        ExpressionList<ComentarioModel> resultado =  finder.query()
                .where();

        return resultado.findList();

    }

    //------------------

    public static ComentarioModel findComentarioPorID(Long id)
    {

        return finder.byId(id);
    }

    //------------------

    public static ComentarioModel findComentarioPorName(String textoComentario)
    {
        ExpressionList<ComentarioModel> resultado =  finder.query()
                .where()
                .icontains("textoComentario", textoComentario);

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

    public String getTextoComentario() {
        return textoComentario;
    }

    public void setTextoComentario(String textoComentario) {
        this.textoComentario = textoComentario;
    }

    public RecetaModel getReceta() {
        return receta;
    }

    public void setReceta(RecetaModel receta) {
        this.receta = receta;
    }
}
