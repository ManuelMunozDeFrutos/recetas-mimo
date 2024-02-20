package models;

import io.ebean.ExpressionList;
import io.ebean.Finder;
import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import io.ebean.annotation.WhenModified;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RecetaModel extends Model
{
    //============================================================

    @Id
    private Long id;
    private String name;
    private String categoria;
    private String dificultad;

    //Descripcion de la receta 1-1

    @OneToOne(cascade = CascadeType.ALL) //Si se borra la receta tambien se borra la descripcion
    private DescripcionModel descripcion;

    //1-N
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "receta")
    private List<ComentarioModel> comentarios = new ArrayList<>();

    //Ingredientes de la receta N-M
    @ManyToMany(cascade = CascadeType.ALL)
    private List<IngredienteModel> ingredientes = new ArrayList<>();

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

    public static final Finder<Long, RecetaModel> finder = new Finder<>(RecetaModel.class);

    //============================================================

    //Metodos del Finder para busqueda  ---

    public static List<RecetaModel> findAllRecetas()
    {
        ExpressionList<RecetaModel> resultado =  finder.query()
                .where();

        //me va a devolver uno por nombre

        return resultado.findList();

    }

    //------------------

    public static RecetaModel findRecetaByID(Long id)
    {

        return finder.byId(id);

    }

    //------------------

    public static List<RecetaModel> findRecetaByName(String name)
    {
        ExpressionList<RecetaModel> resultado =  finder.query()
                .where()
                .icontains("name", name);

        //me va a devolver uno por nombre

        return resultado.findList();

    }

    //------------------

    public static List<RecetaModel> findRecetaByCategory(String category)
    {
        ExpressionList<RecetaModel> resultado =  finder.query()
                .where()
                .ieq("categoria", category);

        return resultado.findList();

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

    public DescripcionModel getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(DescripcionModel descripcion) {
        this.descripcion = descripcion;
    }

    public List<ComentarioModel> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<ComentarioModel> comentarios) {
        this.comentarios = comentarios;
    }

    public List<IngredienteModel> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<IngredienteModel> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public void addIngrediente(String ingredienteName) {
        IngredienteModel ing = IngredienteModel.findIngredienteByName(ingredienteName);

        if (ing == null) {
            ing = new IngredienteModel();
            ing.setNameIngrediente(ingredienteName);
        }

        ing.getRecetas().add(this);
        this.ingredientes.add(ing);
    }
}
