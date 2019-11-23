package name.martingeisse.chipdraw.drc.concept;

import name.martingeisse.chipdraw.design.Material;
import name.martingeisse.chipdraw.design.Plane;
import name.martingeisse.chipdraw.design.Technologies;
import name.martingeisse.chipdraw.drc.DrcContext;
import name.martingeisse.chipdraw.drc.rule.AbstractPerPixelRule;

public class MinimumFieldPolyOverActiveRule extends AbstractPerPixelRule {

    private Plane diffPlane, polyPlane;

    public MinimumFieldPolyOverActiveRule() {
        super(Technologies.Concept.PLANE_POLY);
        setErrorMessage("minimum spacing between field poly and active");
    }

    @Override
    public void check(DrcContext context) {
        this.diffPlane = context.getDesign().getPlane(Technologies.Concept.PLANE_DIFF);
        this.polyPlane = context.getDesign().getPlane(Technologies.Concept.PLANE_POLY);
        super.check(context);
    }

    @Override
    protected boolean checkPixel() {
        int x = getPivotX(), y = getPivotY();

        // check field poly only
        if (diffPlane.getPixelAutoclip(x, y) != Material.NONE) {
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
        return (diffPlane.getPixelAutoclip(x, y) != Material.NONE) && (polyPlane.getPixelAutoclip(x, y) == Material.NONE);
    }

    private boolean checkDiagonal(int dx, int dy) {
        int x = getPivotX() + dx;
        int y = getPivotY() + dy;
        if (!hasDiffWithoutPoly(x, y)) {
            return true;
        }
        Material diff = diffPlane.getPixelAutoclip(x, y);
        return (diffPlane.getPixelAutoclip(getPivotX(), y) == diff || diffPlane.getPixelAutoclip(y, getPivotY()) == diff);
    }

}
