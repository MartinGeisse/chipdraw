package name.martingeisse.chipdraw.pixel.scmos.drc;

import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.design.Plane;
import name.martingeisse.chipdraw.pixel.drc.DrcContext;
import name.martingeisse.chipdraw.pixel.drc.rule.AbstractPerPixelRule;

public class MinimumFieldPolyOverActiveRule extends AbstractPerPixelRule {

    private Plane diffPlane, polyPlane;

    public MinimumFieldPolyOverActiveRule() {
        super(ConceptSchemas.PLANE_POLY);
    }

    @Override
    public String getImplicitErrorMessage() {
        return "minimum spacing between field poly and active";
    }

    @Override
    public void check(DrcContext context) {
        this.diffPlane = context.getDesign().getPlane(ConceptSchemas.PLANE_DIFF);
        this.polyPlane = context.getDesign().getPlane(ConceptSchemas.PLANE_POLY);
        super.check(context);
    }

    @Override
    protected boolean checkPixel() {
        int x = getPivotX(), y = getPivotY();

        // check field poly only
        if (diffPlane.getPixel(x, y) != Material.NONE) {
            return true;
        }

        // for direct neighbor, any diff without poly is an error
        if (hasDiffWithoutPoly(x - 1, y) || hasDiffWithoutPoly(x + 1, y) || hasDiffWithoutPoly(x, y - 1) || hasDiffWithoutPoly(x, y + 1)) {
            return false;
        }

        // For diagonal pixels, things are more complex. Those are okay even when they have diff without poly, as long
        // as one of the two connecting neighbors is a transistor pixel. We only check for diff there, since without
        // poly they will fire an error on their own.
        return checkDiagonal(-1, -1) && checkDiagonal(-1, 1) && checkDiagonal(1, -1) && checkDiagonal(1, 1);

    }

    private boolean hasDiffWithoutPoly(int x, int y) {
        return (diffPlane.getPixel(x, y) != Material.NONE) && (polyPlane.getPixel(x, y) == Material.NONE);
    }

    private boolean checkDiagonal(int dx, int dy) {
        int x = getPivotX() + dx;
        int y = getPivotY() + dy;
        if (!hasDiffWithoutPoly(x, y)) {
            return true;
        }
        Material diff = diffPlane.getPixel(x, y);
        return (diffPlane.getPixel(getPivotX(), y) == diff || diffPlane.getPixel(x, getPivotY()) == diff);
    }

}
