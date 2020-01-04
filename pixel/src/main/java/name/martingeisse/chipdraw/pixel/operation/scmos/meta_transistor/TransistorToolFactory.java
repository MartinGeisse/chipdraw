package name.martingeisse.chipdraw.pixel.operation.scmos.meta_transistor;

import com.google.common.collect.ImmutableList;
import name.martingeisse.chipdraw.pixel.design.ConceptSchemas;
import name.martingeisse.chipdraw.pixel.design.Material;
import name.martingeisse.chipdraw.pixel.util.UserVisibleMessageException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formulas:
 * <p>
 * 1n -- single NMOS transistor
 * 1p -- single PMOS transistor
 * 2x1n -- double-width NMOS transistor
 * 2n -- two NMOS series transistors
 * 11n -- two NMOS parallel transistors
 * 2x11n -- two NMOS parallel transistors, double width
 * 11nr -- two NMOS parallel transistors, rotated (gates run horizontally)
 */
public class TransistorToolFactory {

    public static final Pattern FORMULA_PATTERN = Pattern.compile("(?:(\\d)x)?(\\d+)([np])(r?)");

    public static ConfigurableTransistorTool create(String formula) throws UserVisibleMessageException {

        // parse formula
        Matcher matcher = FORMULA_PATTERN.matcher(formula);
        if (!matcher.matches()) {
            throw new UserVisibleMessageException("invalid formula");
        }
        String widthText = matcher.group(1);
        String gateGroupsText = matcher.group(2);
        String dopingTypeText = matcher.group(3);
        String rotatedText = matcher.group(4);

        // parse segments
        int width = (widthText == null) ? 1 : Integer.parseInt(widthText);
        int gateGroupCount = gateGroupsText.length();
        List<Integer> gateGroups = new ArrayList<>();
        for (int i = 0; i < gateGroupCount; i++) {
            gateGroups.add(gateGroupsText.charAt(i) - '0');
        }
        Material sourceDrainMaterial = (dopingTypeText.equals("p") ? ConceptSchemas.MATERIAL_PDIFF : ConceptSchemas.MATERIAL_NDIFF);
        boolean rotated = !rotatedText.isEmpty();

        // build tool
        return new ConfigurableTransistorTool(sourceDrainMaterial, width, ImmutableList.copyOf(gateGroups), rotated);

    }

}
