package validators;

import play.data.validation.Constraints;
import play.libs.F;

public class CategoriaValidator extends Constraints.Validator<String>
{

    @Override
    public boolean isValid(String object)
    {
        return object.equals("PRIMERO") || object.equals("SEGUNDO") || object.equals("POSTRE");
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey()
    {
        return new F.Tuple<String, Object[]>(
                "recipe_category_required",
                new Object[]{}
        );
    }
}
