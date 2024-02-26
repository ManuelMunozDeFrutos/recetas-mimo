package validators;

import play.data.validation.Constraints;
import play.libs.F;

public class DificultadValidator extends Constraints.Validator<String>
{

    //DEBE SER BAJA MEDIA O ALTA
    @Override
    public boolean isValid(String object)
    {
        return object.equals("BAJA") || object.equals("MEDIA") || object.equals("ALTA");
    }

    //MENSAJE DE ERROR
    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey()
    {
        return new F.Tuple<String, Object[]>(
                "recipe_dificulty_required",
                new Object[]{}
        );
    }
}
