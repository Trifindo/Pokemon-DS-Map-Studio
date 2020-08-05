/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.imd.nodes;

import editor.imd.ImdAttribute;
import editor.imd.ImdNode;
import java.util.ArrayList;

/**
 *
 * @author Trifindo
 */
public class Material extends ImdNode {

    private static final String[] texGenModes = {"none", "tex", "nrm", "pos"};

    public Material(int index, String name, boolean[] lights, int alpha,
            boolean renderBorder, int texIndex, int palIndex,
            boolean doubleFaceRender, boolean fogEnabled, int texGenModeIndex,
            int texTilingU, int texTilingV) {
        super("material");

        String faceType;
        if (doubleFaceRender) {
            faceType = "both";
        } else {
            faceType = "front";
        }

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

        attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("index", index));
                add(new ImdAttribute("name", name));//"mtl" + index));
                add(new ImdAttribute("light0", lights[0]));
                add(new ImdAttribute("light1", lights[1]));
                add(new ImdAttribute("light2", lights[2]));
                add(new ImdAttribute("light3", lights[3]));
                add(new ImdAttribute("face", faceType));
                add(new ImdAttribute("alpha", alpha)); //Check this
                add(new ImdAttribute("wire_mode", false));
                add(new ImdAttribute("polygon_mode", "modulate"));
                add(new ImdAttribute("polygon_id", polygonID));
                add(new ImdAttribute("fog_flag", fogEnabled));
                add(new ImdAttribute("depth_test_decal", false));
                add(new ImdAttribute("translucent_update_depth", false));
                add(new ImdAttribute("render_1_pixel", false));
                add(new ImdAttribute("far_clipping", false));
                add(new ImdAttribute("diffuse", new int[]{25, 25, 25}));
                add(new ImdAttribute("ambient", new int[]{31, 31, 31}));
                add(new ImdAttribute("specular", new int[]{0, 0, 0}));
                add(new ImdAttribute("emission", new int[]{0, 0, 0}));
                add(new ImdAttribute("shininess_table_flag", false));
                add(new ImdAttribute("tex_image_idx", texIndex));
                add(new ImdAttribute("tex_palette_idx", palIndex));
                add(new ImdAttribute("tex_tiling", texTilingNameU + " " + texTilingNameV));
                add(new ImdAttribute("tex_scale", new float[]{1.0f, 1.0f}));
                add(new ImdAttribute("tex_rotate", 0.0f));
                add(new ImdAttribute("tex_translate", new float[]{0.0f, 0.0f}));
                add(new ImdAttribute("tex_gen_mode", texGenModes[texGenModeIndex]));
            }
        };
    }

    private String texTilingToString(int texTiling) {
        String texTilingName;
        switch (texTiling) {
            case 0:
                texTilingName = "repeat";
                break;
            case 1:
                texTilingName = "clamp";
                break;
            case 2:
                texTilingName = "flip";
                break;
            default:
                texTilingName = "repeat";
                break;
        }
        return texTilingName;
    }

}
