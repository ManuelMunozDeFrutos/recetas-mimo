package validators;

import play.data.validation.Constraints;
import play.libs.F;

public class DificultadValidator extends Constraints.Validator<String>
{

    @Override
    public boolean isValid(String object)
    {
        return object.equals("BAJA") || object.equals("MEDIA") || object.equals("ALTA");
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey()
    {
        return new F.Tuple<String, Object[]>(
                "recipe_dificulty_required",
                new Object[]{}
        );
    }
}
