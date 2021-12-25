
package formats.imd.nodes;

import formats.imd.ImdAttribute;
import formats.imd.ImdNode;

import java.util.List;

/**
 * @author Trifindo
 */
public class Material extends ImdNode {

    private static final String[] texGenModes = {"none", "tex", "nrm", "pos"};

    public Material(int index, String name, boolean[] lights, int alpha,
                    boolean renderBorder, int texIndex, int palIndex,
                    boolean doubleFaceRender, boolean fogEnabled, int texGenModeIndex,
                    int texTilingU, int texTilingV) {
        super("material");

        String faceType = doubleFaceRender ? "both" : "front";

        int polygonID;
        if (renderBorder) {
            polygonID = 8;
        } else if (alpha < 31) {
            polygonID = 3;
        } else {
            polygonID = 0;
        }

        String texTilingNameU = texTilingToString(texTilingU);
        String texTilingNameV = texTilingToString(texTilingV);

        attributes = List.of(
                new ImdAttribute("index", index), 
                new ImdAttribute("name", name), //"mtl" + index),
                new ImdAttribute("light0", lights[0]), 
                new ImdAttribute("light1", lights[1]), 
                new ImdAttribute("light2", lights[2]), 
                new ImdAttribute("light3", lights[3]), 
                new ImdAttribute("face", faceType), 
                new ImdAttribute("alpha", alpha),  //Check this
                new ImdAttribute("wire_mode", false), 
                new ImdAttribute("polygon_mode", "modulate"), 
                new ImdAttribute("polygon_id", polygonID), 
                new ImdAttribute("fog_flag", fogEnabled), 
                new ImdAttribute("depth_test_decal", false), 
                new ImdAttribute("translucent_update_depth", false), 
                new ImdAttribute("render_1_pixel", false), 
                new ImdAttribute("far_clipping", false), 
                new ImdAttribute("diffuse", new int[]{25, 25, 25}), 
                new ImdAttribute("ambient", new int[]{31, 31, 31}), 
                new ImdAttribute("specular", new int[]{0, 0, 0}), 
                new ImdAttribute("emission", new int[]{0, 0, 0}), 
                new ImdAttribute("shininess_table_flag", false), 
                new ImdAttribute("tex_image_idx", texIndex), 
                new ImdAttribute("tex_palette_idx", palIndex), 
                new ImdAttribute("tex_tiling", texTilingNameU + " " + texTilingNameV), 
                new ImdAttribute("tex_scale", new float[]{1.0f, 1.0f}), 
                new ImdAttribute("tex_rotate", 0.0f), 
                new ImdAttribute("tex_translate", new float[]{0.0f, 0.0f}), 
                new ImdAttribute("tex_gen_mode", texGenModes[texGenModeIndex]));
    }

    private String texTilingToString(int texTiling) {
        String texTilingName;
        switch (texTiling) {
            case 1:
                texTilingName = "clamp";
                break;
            case 2:
                texTilingName = "flip";
                break;
            case 0:
            default:
                texTilingName = "repeat";
                break;
        }
        return texTilingName;
    }
}
