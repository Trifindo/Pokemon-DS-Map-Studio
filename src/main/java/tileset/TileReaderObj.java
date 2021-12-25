
package tileset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import utils.Utils;

/**
 * @author Trifindo
 */
public class TileReaderObj {

    public static Tile2 loadTileObj(String folderPath, String objName, Tileset tileset)
            throws IOException, IndexOutOfBoundsException, TextureNotFoundException {

        //Load OBJ file data
        InputStream inputObj = new FileInputStream(new File(folderPath + "/" + objName));
        BufferedReader brObj = new BufferedReader(new InputStreamReader(inputObj));

        List<List<Float>> vCoordsObj = new ArrayList<>();
        List<List<Float>> tCoordsObj = new ArrayList<>();
        List<List<Float>> nCoordsObj = new ArrayList<>();

        List<String> materialNames = new ArrayList<>();

        List<List<Face>> fIndsQuadArray = new ArrayList<>();
        List<List<Face>> fIndsTriArray = new ArrayList<>();

        String mtlName = "";
        int materialIndex = 0;
        String lineObj;
        while ((lineObj = brObj.readLine()) != null) {
            if (lineObj.startsWith("mtllib")) {
                mtlName = lineObj.substring(lineObj.indexOf(" ") + 1);
            } else if (lineObj.startsWith("o")) {

            } else if (lineObj.startsWith("v ")) {
                vCoordsObj.add(loadFloatLineObj(lineObj, 3, 3, 0.0f));
            } else if (lineObj.startsWith("vt")) {
                tCoordsObj.add(loadFloatLineObj(lineObj, 2, 2, 0.0f));
            } else if (lineObj.startsWith("vn")) {
                nCoordsObj.add(loadFloatLineObj(lineObj, 3, 3, 0.0f));
            } else if (lineObj.startsWith("usemtl")) {
                String name = lineObj.split(" ")[1];
                materialIndex = materialNames.indexOf(name);
                if (materialIndex == -1) {
                    fIndsQuadArray.add(new ArrayList<>());
                    fIndsTriArray.add(new ArrayList<>());
                    materialIndex = materialNames.size();
                    materialNames.add(name);
                }
            } else if (lineObj.startsWith("f")) {
                String[] splitLine = (lineObj.substring(2)).split(" ");
                int numVertex = 0;
                for (String s : splitLine) {
                    if (s.contains("/")) {
                        numVertex++;
                    }
                }
                if (numVertex > 3) {
                    fIndsQuadArray.get(materialIndex).add(loadFaceIndicesObj(splitLine, 4));
                } else {
                    fIndsTriArray.get(materialIndex).add(loadFaceIndicesObj(splitLine, 3));
                }
            }
        }
        inputObj.close();

        //Load Mtl file
        List<Integer> textureIDs = new ArrayList<>();
        int numMaterials = countNumberOfStarts(new File(folderPath + "/" + mtlName), "newmtl");
        for (int i = 0; i < numMaterials; i++) {
            textureIDs.add(0);
        }
        InputStream inputMtl = new FileInputStream(new File(folderPath + "/" + mtlName));
        BufferedReader brMtl = new BufferedReader(new InputStreamReader(inputMtl));
        int matIndex = 0;
        String lineMtl;
        while ((lineMtl = brMtl.readLine()) != null) {
            if (lineMtl.startsWith("newmtl")) {
                String matName = lineMtl.split(" ")[1];
                matIndex = materialNames.indexOf(matName);
            } else if (lineMtl.startsWith("map_Kd")) {
                String textName = lineMtl.split(" ")[1];
                int textIndex = tileset.getIndexOfMaterialByImgName(textName);
                if (textIndex == -1) { //Not found
                    if (new File(folderPath + "/" + textName).exists()) {
                        textureIDs.set(matIndex, tileset.getMaterials().size());
                        try {
                            TilesetMaterial material = new TilesetMaterial();
                            material.loadTextureImgFromPath(folderPath + "/" + textName);
                            material.setImageName(textName);
                            String textNameImd = Utils.removeExtensionFromPath(textName);
                            material.setMaterialName(textNameImd);
                            material.setTextureNameImd(textNameImd);
                            material.setPaletteNameImd(textNameImd + "_pl");
                            tileset.getMaterials().add(material);
                        } catch (IOException ex) {
                            throw new TextureNotFoundException(
                                    "Can't open texture named: \"" + textName + "\"" + "\n"
                                            + "Make sure that the texture is in the same folder as the OBJ file");
                        }
                    } else {
                        throw new TextureNotFoundException(
                                "Texture named: \"" + textName
                                        + "\" does not exist in the folder of the OBJ file");
                    }
                } else {
                    textureIDs.set(matIndex, textIndex);
                }
            }
        }
        inputMtl.close();

        //Fix material names for avoiding sub folder issues
        for (int i = 0; i < textureIDs.size(); i++) {
            TilesetMaterial material = tileset.getMaterial(textureIDs.get(i));
            String[] splittedName = material.getImageName().split("/");
            if (splittedName.length > 1) {
                String newName = splittedName[splittedName.length - 1];
                int index = tileset.getIndexOfMaterialByImgName(newName);
                if (index == -1) {
                    material.setImageName(newName);
                    newName = Utils.removeExtensionFromPath(newName);
                    material.setMaterialName(newName);
                    material.setTextureNameImd(newName);
                    material.setPaletteNameImd(newName + "_pl");
                } else {
                    textureIDs.set(i, index);
                }
            }
        }

        //Fix duplicated Texture IDs
        List<List<Face>> fIndsQuadArrayFixed = new ArrayList<>();
        List<List<Face>> fIndsTriArrayFixed = new ArrayList<>();
        List<Integer> textureIDsFixed = new ArrayList<>();
        for (int i = 0; i < textureIDs.size(); i++) {
            int id = textureIDs.get(i);
            int index = textureIDsFixed.indexOf(id);
            if (index == -1) {
                List<Face> fIndsQuad = new ArrayList<>(fIndsQuadArray.get(i));
                List<Face> fIndsTri = new ArrayList<>(fIndsTriArray.get(i));
                fIndsQuadArrayFixed.add(fIndsQuad);
                fIndsTriArrayFixed.add(fIndsTri);
                textureIDsFixed.add(id);
            } else {
                List<Face> fIndsQuad = fIndsQuadArrayFixed.get(index);
                List<Face> fIndsTri = fIndsTriArrayFixed.get(index);
                fIndsQuad.addAll(fIndsQuadArray.get(i));
                fIndsTri.addAll(fIndsTriArray.get(i));
            }
        }
        textureIDs = textureIDsFixed;
        fIndsQuadArray = fIndsQuadArrayFixed;
        fIndsTriArray = fIndsTriArrayFixed;

        /*
        //Group all indices by material into a single array
        ArrayList<Face> fIndsQuad = new ArrayList<>();
        ArrayList<Face> fIndsTri = new ArrayList<>();
        for (int i = 0, c = 0; i < fIndsQuadArray.size(); i++) {
            texOffsetsQuad.add(c);
            for (int j = 0; j < fIndsQuadArray.get(i).size(); j++, c++) {
                fIndsQuad.add(fIndsQuadArray.get(i).get(j));
            }
        }
        for (int i = 0, c = 0; i < fIndsTriArray.size(); i++) {
            texOffsetsTri.add(c);
            for (int j = 0; j < fIndsTriArray.get(i).size(); j++, c++) {
                fIndsTri.add(fIndsTriArray.get(i).get(j));
            }
        }
        */
        return null;//Change this
    }

    private static List<Float> loadFloatLineObj(String line, int minNumElemn, int maxNumElem, float defaultValue) {
        String[] splitLine = line.split(" ");
        int numElements = Math.max(Math.min(maxNumElem, splitLine.length - 1), minNumElemn);
        List<Float> floats = new ArrayList<>(numElements);
        for (int i = 0; i < numElements; i++) {
            float value;
            try {
                value = Float.parseFloat(splitLine[i + 1]);
            } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                value = defaultValue;
            }
            floats.add(value);
        }
        return floats;
    }

    private static Face loadFaceIndicesObj(String[] splitLine, int numVertex) {
        Face f = new Face(numVertex);
        for (int i = 0; i < numVertex; i++) {
            String[] sArray = splitLine[i].split("/");
            f.vInd[i] = Integer.parseInt(sArray[0]);
            f.tInd[i] = Integer.parseInt(sArray[1]);
            if (sArray.length > 2) {
                f.nInd[i] = Integer.parseInt(sArray[2]);
            } else {
                f.nInd[i] = -1;
            }
        }
        return f;
    }

    private static int countNumberOfStarts(File file, String content) throws IOException {
        InputStream input = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        return (int) br.lines()
                .filter(line -> line.startsWith(content))
                .count();
    }

    private static class Face {
        public int[] vInd;
        public int[] tInd;
        public int[] nInd;

        public Face(int nVertex) {
            vInd = new int[nVertex];
            tInd = new int[nVertex];
            nInd = new int[nVertex];
        }
    }
}
