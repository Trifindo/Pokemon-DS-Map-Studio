
package formats.imd;

import formats.imd.nodes.*;
import formats.imd.nodes.ImdMaterial;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import formats.imd.nodes.Node;
import tileset.*;
import utils.Utils;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;

/**
 * @author Trifindo
 */
public class ImdModel extends ImdNode {

    //Model OpenGL
    private ArrayList<PolygonData> polygons = new ArrayList<>();

    private ArrayList<Integer> textureIDs = new ArrayList<>();
    //private ArrayList<Integer> materialIDs = new ArrayList<>();
    private ArrayList<Integer> texOffsetsTri = new ArrayList<>();
    private ArrayList<Integer> texOffsetsQuad = new ArrayList<>();

    private ArrayList<TilesetMaterial> materials = new ArrayList<>();

    private float[] minCoords;
    private float[] maxCoords;
    private float[] boxTestCoords = new float[3];
    private float[] boxTestSize = new float[3];

    private final int defaultPosScale = 6;
    private int posScale = defaultPosScale;
    private int boxTestPosScale = defaultPosScale;

    private int numVertexTotal;

    public ImdModel(String objPath) throws ParserConfigurationException,
            IOException, TransformerException, TextureNotFoundException,
            NormalsNotFoundException {
        this(objPath, objPath, null);
    }

    public ImdModel(String objPath, String savePath,
                    ArrayList<TilesetMaterial> tsetMaterials) throws
            ParserConfigurationException, ParserConfigurationException,
            IOException, TransformerException, TextureNotFoundException,
            NormalsNotFoundException {
        super("imd");

        attributes = new ArrayList<ImdAttribute>() {
            {
                add(new ImdAttribute("version", "1.6.0"));
            }
        };

        subnodes.add(new Head());

        Body body = new Body();

        try {
            loadFromObj(objPath);
        } catch (IOException ex) {
            throw new IOException();
            //Logger.getLogger(ImdModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Use tileset materials if tileset is loaded
        if (tsetMaterials != null) {
            ArrayList<TilesetMaterial> newMaterials = new ArrayList<>();
            for (int i = 0; i < tsetMaterials.size(); i++) {
                TilesetMaterial material = tsetMaterials.get(i);
                if (material.alwaysIncludeInImd()) {
                    newMaterials.add(material.clone());
                }
            }
            for (int i = 0; i < materials.size(); i++) {
                int index = getIndexOfMaterialByImgName(newMaterials, materials.get(i).getImageName());
                if (index == -1) {
                    textureIDs.set(i, newMaterials.size());
                    int tIndex = getIndexOfMaterialByImgName(tsetMaterials, materials.get(i).getImageName());
                    if (tIndex != -1) {
                        newMaterials.add(tsetMaterials.get(tIndex).clone());
                    } else {
                        newMaterials.add(materials.get(i));
                    }
                } else {
                    textureIDs.set(i, index);
                }
            }
            materials = newMaterials;

        }

        //Rotate model
        for (int i = 0; i < polygons.size(); i++) {
            polygons.get(i).rotateData();
        }

        //Calculate bounds
        minCoords = getMinCoords();
        maxCoords = getMaxCoords();

        // Model Info
        posScale = calculatePosScale();
        ModelInfo modelInfo = new ModelInfo(posScale, materials.size());//textureIDs.size());
        body.subnodes.add(modelInfo);
        //System.out.println("Pos scale: " + posScale);

        //Scale Model
        float scaleFactor = (float) (0.25f * Math.pow(2, defaultPosScale - posScale)); //0.25 is Blender factor
        for (int i = 0; i < polygons.size(); i++) {
            polygons.get(i).scale(scaleFactor);
        }
        //System.out.println("Scale factor: " + scaleFactor);

        //Fix Texture Coordinates out of range
        for (int i = 0; i < polygons.size(); i++) {
            polygons.get(i).fixTextureCoords(
                    materials.get(textureIDs.get(i)).getTextureImg().getWidth(),
                    materials.get(textureIDs.get(i)).getTextureImg().getHeight());
        }

        //Scale Textures
        for (int i = 0; i < polygons.size(); i++) {
            polygons.get(i).scaleTextures(
                    materials.get(textureIDs.get(i)).getTextureImg().getWidth(),
                    -materials.get(textureIDs.get(i)).getTextureImg().getHeight());
        }

        //Fix normals
        for (int i = 0; i < polygons.size(); i++) {
            polygons.get(i).fixNormals(0.99804f);
        }

        //Box test
        calculateBoxTest();
        BoxTest boxTest = new BoxTest(boxTestPosScale, boxTestCoords, boxTestSize);
        body.subnodes.add(boxTest);

        //Calculate imd palette array
        ImdPaletteArray imdPaletteArray = new ImdPaletteArray(materials);

        //Create imd texture array
        ArrayList<ImdTextureIndexed> imdTextureArray = new ArrayList<>();
        for (int i = 0; i < materials.size(); i++) {
            ArrayList<Color> palette = imdPaletteArray.getPalette(materials.get(i).getPaletteNameImd());
            imdTextureArray.add(new ImdTextureIndexed(materials.get(i), palette));
        }

        //Texture and Palette Indices
        int[] textureIndices = new int[materials.size()];
        int[] paletteIndices = new int[materials.size()];

        //Texture and Palette Names 
        ArrayList<String> textureNames = new ArrayList<>();
        ArrayList<String> paletteNames = new ArrayList<>();
        //TexImage array and Tex palette array
        ImdNode texImageArray = new ImdNode("tex_image_array");
        ImdNode texPaletteArray = new ImdNode("tex_palette_array");
        for (int i = 0, tCount = 0, pCount = 0; i < materials.size(); i++) {
            TilesetMaterial material = materials.get(i);
            ImdTextureIndexed imdTexture = imdTextureArray.get(i);
            int tIndex = textureNames.indexOf(material.getTextureNameImd());

            if (tIndex == -1) {
                textureIndices[i] = textureNames.size();
                textureNames.add(material.getTextureNameImd());

                String textureName = materials.get(i).getTextureNameImd();
                String paletteName = materials.get(i).getPaletteNameImd();
                TexImage texImage = new TexImage(
                        tCount, textureName, paletteName,
                        imdTexture,
                        materials.get(i).getColorFormat(), "");
                texImageArray.subnodes.add(texImage);

                tCount++;
            } else {
                textureIndices[i] = tIndex;
            }

            int pIndex = paletteNames.indexOf(material.getPaletteNameImd());
            if (pIndex == -1) {
                paletteIndices[i] = paletteNames.size();
                paletteNames.add(material.getPaletteNameImd());

                TexPalette texPalette = new TexPalette(
                        pCount, materials.get(i).getPaletteNameImd(),
                        imdTexture.getPaletteDataSize(),
                        imdTexture.getPalDataAsHexString());
                texPaletteArray.subnodes.add(texPalette);

                pCount++;
            } else {
                paletteIndices[i] = pIndex;
            }
        }
        texImageArray.attributes.add(new ImdAttribute("size", textureNames.size()));
        texPaletteArray.attributes.add(new ImdAttribute("size", paletteNames.size()));
        body.subnodes.add(texImageArray);
        body.subnodes.add(texPaletteArray);

        //Material array
        ImdNode materialArray = new ImdNode("material_array");
        materialArray.attributes.add(new ImdAttribute("size", materials.size()));
        for (int i = 0; i < materials.size(); i++) { //
            TilesetMaterial current = materials.get(i);

            //boolean[] lights;
            //if (current.areVertexColorsEnabled()) {
            //    lights = new boolean[]{false, false, false, false};
            //} else {
            //    lights = new boolean[]{
            //            current.getLight0(),
            //            current.getLight1(),
            //            current.getLight2(),
            //            current.getLight3()
            //    };
            //}
            ImdMaterial material = new ImdMaterial(
                    i,
                    current.getMaterialName(),
                    new boolean[] { current.getLight0(), current.getLight1(), current.getLight2(), current.getLight3() },
                    new int[][] { current.getDiffuse(), current.getAmbient(), current.getSpecular(), current.getEmission() },
                    current.getAlpha(),
                    current.hasRenderBorder(),
                    textureIndices[i],
                    paletteIndices[i],
                    current.renderBothFaces(),
                    current.isFogEnabled(),
                    current.getTexGenMode(),
                    current.getTexTilingU(),
                    current.getTexTilingV()
            );
            materialArray.subnodes.add(material);
        }
        body.subnodes.add(materialArray);

        //Matrix array
        ImdNode matrixArray = new ImdNode("matrix_array");
        matrixArray.attributes.add(new ImdAttribute("size", 1));
        ImdNode matrix = new ImdNode("matrix");
        matrix.attributes.add(new ImdAttribute("index", 0));
        matrix.attributes.add(new ImdAttribute("mtx_weight", 1));
        matrix.attributes.add(new ImdAttribute("node_idx", 0));
        matrixArray.subnodes.add(matrix);
        body.subnodes.add(matrixArray);

        numVertexTotal = 0;
        if (textureIDs.size() > 0) {
            //Polygon array
            ImdNode polygonArray = new ImdNode("polygon_array");
            polygonArray.attributes.add(new ImdAttribute("size", textureIDs.size()));
            for (int i = 0; i < textureIDs.size(); i++) {
                PolygonData pData = polygons.get(i);

                //Mtx prim
                ImdNode mtxPrim = new ImdNode("mtx_prim");
                mtxPrim.attributes.add(new ImdAttribute("index", 0));
                //p.subnodes.add(mtxPrim);

                //Mtx list
                ImdNode mtxList = new ImdNode("mtx_list");
                mtxList.attributes.add(new ImdAttribute("size", 1));
                mtxList.content = "0";
                mtxPrim.subnodes.add(mtxList);

                //Calculate Quad Strips
                StripeCalculator quadCalculator = new StripeCalculator(pData, true,
                        materials.get(textureIDs.get(i)).uniformNormalOrientation(),
                        materials.get(textureIDs.get(i)).areVertexColorsEnabled());
                ArrayList<PolygonData> pDataStripQuad = quadCalculator.calculateQuadStrip();

                //Calculate Triangle Strips
                TriangleStripCalculator triCalculator = new TriangleStripCalculator(pData,
                        materials.get(textureIDs.get(i)).uniformNormalOrientation(),
                        materials.get(textureIDs.get(i)).areVertexColorsEnabled());
                ArrayList<PolygonData> pDataStripTri = triCalculator.calculateTriStrip();

                //Primitive array
                ImdNode primitiveArray = new ImdNode("primitive_array");
                int primArraySize = 0;

                if (pDataStripQuad.get(0) != null) {
                    primArraySize++;
                }
                if (pDataStripQuad.size() > 1) {
                    primArraySize += pDataStripQuad.size() - 1;
                }
                if (pDataStripTri.get(0) != null) {
                    primArraySize++;
                }
                if (pDataStripTri.size() > 1) {
                    primArraySize += pDataStripTri.size() - 1;
                }

                primitiveArray.attributes.add(new ImdAttribute("size", primArraySize));
                int numPrimitives = 0;
                int vertexSize = 0;
                boolean firstPrimitive = true;
                Primitive.init();

                //Write quads
                if (pDataStripQuad.get(0) != null) {
                    pDataStripQuad.get(0).groupByNormals(true);//NEW CODE
                    Primitive primitive = new Primitive(
                            numPrimitives, "quads",
                            pDataStripQuad.get(0).vCoordsQuad.length / 3);
                    primitive.calculateElements(pDataStripQuad.get(0), firstPrimitive, true,
                            materials.get(textureIDs.get(i)).uniformNormalOrientation(),
                            materials.get(textureIDs.get(i)).areVertexColorsEnabled());//TODO: Experimental
                    firstPrimitive = false;
                    primitiveArray.subnodes.add(primitive);
                    numPrimitives++;//NEW CODE
                    vertexSize += pDataStripQuad.get(0).vCoordsQuad.length / 3;
                }

                //Write quad strips
                for (int j = 1; j < pDataStripQuad.size(); j++) {
                    Primitive primitive = new Primitive(
                            numPrimitives, "quad_strip",
                            pDataStripQuad.get(j).vCoordsQuad.length / 3);
                    primitive.calculateElementsWithStrips(pDataStripQuad.get(j), firstPrimitive, true,
                            materials.get(textureIDs.get(i)).uniformNormalOrientation(),
                            materials.get(textureIDs.get(i)).areVertexColorsEnabled());//TODO: Experimental
                    firstPrimitive = false;
                    primitiveArray.subnodes.add(primitive);
                    numPrimitives++;//NEW CODE
                    vertexSize += pDataStripQuad.get(j).vCoordsQuad.length / 3;
                }

                //Write triangles
                if (pDataStripTri.get(0) != null) {
                    pDataStripTri.get(0).groupByNormals(false);//NEW CODE
                    Primitive primitive = new Primitive(
                            numPrimitives, "triangles",
                            pDataStripTri.get(0).vCoordsTri.length / 3);
                    primitive.calculateElements(pDataStripTri.get(0), firstPrimitive, false,
                            materials.get(textureIDs.get(i)).uniformNormalOrientation(),
                            materials.get(textureIDs.get(i)).areVertexColorsEnabled());//TODO: Experimental
                    firstPrimitive = false;
                    primitiveArray.subnodes.add(primitive);
                    numPrimitives++;//NEW CODE
                    vertexSize += pDataStripTri.get(0).vCoordsTri.length / 3;
                }
                //Write triangle strips
                for (int j = 1; j < pDataStripTri.size(); j++) {
                    Primitive primitive = new Primitive(
                            numPrimitives, "triangle_strip",
                            pDataStripTri.get(j).vCoordsTri.length / 3);
                    primitive.calculateElementsWithStrips(pDataStripTri.get(j), firstPrimitive, false,
                            materials.get(textureIDs.get(i)).uniformNormalOrientation(),
                            materials.get(textureIDs.get(i)).areVertexColorsEnabled());//TODO: Experimental
                    firstPrimitive = false;
                    primitiveArray.subnodes.add(primitive);
                    numPrimitives++;//NEW CODE
                    vertexSize += pDataStripTri.get(j).vCoordsTri.length / 3;
                }

                mtxPrim.subnodes.add(primitiveArray);

                //Polygon Node
                //int vertexSize = pData.vCoordsQuad.length / 3 + pData.vCoordsTri.length / 3;
                int triangleSize = pData.vCoordsTri.length / (3 * 3);
                int quadSize = pData.vCoordsQuad.length / (3 * 4);
                int polygonSize = triangleSize + quadSize;
                Polygon p = new Polygon(
                        i, "polygon" + i,
                        vertexSize, polygonSize,
                        triangleSize, quadSize,
                        new float[]{3.0f, 3.0f, 3.0f}, // TODO: check these values
                        3.0f, 1, //TODO Check this again
                        materials.get(textureIDs.get(i)).areVertexColorsEnabled());
                polygonArray.subnodes.add(p);

                //Add mtxPrim
                p.subnodes.add(mtxPrim);
                numVertexTotal += vertexSize;
            }
            body.subnodes.add(polygonArray);

            //Node array
            ImdNode nodeArray = new ImdNode("node_array");
            nodeArray.attributes.add(new ImdAttribute("size", 1)); //TODO check this again
            body.subnodes.add(nodeArray);

            //Node
            Node node = new Node(0, textureIDs.size(), numVertexTotal/*numVertex()*/, getNumPolygons(),
                    getNumTris(), getNumQuads(), textureIDs);
            nodeArray.subnodes.add(node);
        }

        //Output info
        ImdNode outputInfo = new ImdNode("output_info");
        outputInfo.attributes.add(new ImdAttribute("vertex_size", numVertexTotal/*numVertex()*/));
        outputInfo.attributes.add(new ImdAttribute("polygon_size", getNumPolygons()));
        outputInfo.attributes.add(new ImdAttribute("triangle_size", getNumTris()));
        outputInfo.attributes.add(new ImdAttribute("quad_size", getNumQuads()));
        body.subnodes.add(outputInfo);

        //Add body
        subnodes.add(body);

        if (!savePath.endsWith("imd")) {
            savePath += ".imd";
        }
        saveToXML(savePath);
    }

    private void loadFromObj(String path)
            throws IOException, TextureNotFoundException, NormalsNotFoundException {

        ArrayList<Float> vCoordsObj = new ArrayList<>();
        ArrayList<Float> tCoordsObj = new ArrayList<>();
        ArrayList<Float> nCoordsObj = new ArrayList<>();
        ArrayList<Float> colorsObj = new ArrayList<>();

        ArrayList<Face> fIndsQuad = new ArrayList<>();
        ArrayList<Face> fIndsTri = new ArrayList<>();

        ArrayList<String> materialNames = new ArrayList();

        String folderPath = new File(path).getParent();
        String objName = new File(path).getName();

        InputStream inputObj = new FileInputStream(new File(folderPath + File.separator + objName));
        BufferedReader brObj = new BufferedReader(new InputStreamReader(inputObj));

        String mtlName = "";
        int numQuads = 0;
        int numTris = 0;

        String lineObj;
        while ((lineObj = brObj.readLine()) != null) {
            if (lineObj.startsWith("mtllib")) {
                //mtlName = lineObj.split(" ")[1];
                mtlName = lineObj.substring(lineObj.indexOf(" ") + 1);//NEW CODE
            } else if (lineObj.startsWith("o")) {

            } else if (lineObj.startsWith("v ")) {
                lineObj = lineObj.substring(2);
                for (String s : lineObj.split(" ")) {
                    vCoordsObj.add(Float.valueOf(s));
                }
            } else if (lineObj.startsWith("vt")) {
                for (String s : (lineObj.substring(3)).split(" ")) {
                    tCoordsObj.add(Float.valueOf(s));
                }
            } else if (lineObj.startsWith("vn")) {
                for (String s : (lineObj.substring(3)).split(" ")) {
                    Float nCoord = Float.valueOf(s);
                    if (nCoord.isNaN()) {
                        nCoord = 1.0f;
                    }
                    nCoordsObj.add(nCoord);
                }
            } else if (lineObj.startsWith("c")) {
                for (String s : (lineObj.substring(2)).split(" ")) {
                    colorsObj.add(Float.valueOf(s));
                }
            } else if (lineObj.startsWith("usemtl")) {
                texOffsetsQuad.add(numQuads);
                texOffsetsTri.add(numTris);
                //materialNames.add(lineObj.split(" ")[1]);
                materialNames.add(lineObj.substring(lineObj.indexOf(" ") + 1));
            } else if (lineObj.startsWith("f")) {
                String[] splitLine = (lineObj.substring(2)).split(" ");
                Face f = new Face(splitLine.length > 3);
                for (int i = 0; i < splitLine.length; i++) {
                    String[] sArray = splitLine[i].split("/");
                    f.vInd[i] = Integer.valueOf(sArray[0]);
                    f.tInd[i] = Integer.valueOf(sArray[1]);
                    if (sArray.length > 2) {
                        f.nInd[i] = Integer.valueOf(sArray[2]);
                    } else {
                        throw new NormalsNotFoundException("The OBJ file \""
                                + objName + "\" does not contain calculated normals.");
                    }
                    if (sArray.length > 3) {
                        f.cInd[i] = Integer.valueOf(sArray[3]);
                    } else {
                        f.cInd[i] = colorsObj.size() / 3 + 1;//TODO REVISE THIS!!!
                        colorsObj.add(1.0f);
                        colorsObj.add(1.0f);
                        colorsObj.add(1.0f);
                    }
                }
                if (f.isQuad) {
                    fIndsQuad.add(f);
                    numQuads++;
                } else {
                    fIndsTri.add(f);
                    numTris++;
                }
            }
        }

        brObj.close();
        inputObj.close();

        int numMaterials = countNumberOfStarts(new File(folderPath + File.separator + mtlName), "newmtl");
        for (int i = 0; i < numMaterials; i++) {
            textureIDs.add(0);
        }
        InputStream inputMtl = new FileInputStream(new File(folderPath + File.separator + mtlName));
        BufferedReader brMtl = new BufferedReader(new InputStreamReader(inputMtl));
        int matIndex = 0;
        String lineMtl;
        while ((lineMtl = brMtl.readLine()) != null) {
            if (lineMtl.startsWith("newmtl")) {
                //String matName = lineMtl.split(" ")[1];
                String matName = lineMtl.substring(lineMtl.indexOf(" ") + 1);//NEW CODE
                matIndex = materialNames.indexOf(matName);
            } else if (lineMtl.startsWith("map_Kd")) {
                //String textName = lineMtl.split(" ")[1];
                String textName = lineMtl.substring(lineMtl.indexOf(" ") + 1);
                //System.out.println("Name texture obj: " + textName);
                int textIndex = getIndexOfMaterialByImgName(materials, textName);//tileset.getTextureNames().indexOf(textName);
                if (textIndex == -1) { //Not found
                    //System.out.println("New!");
                    File file = new File(folderPath + File.separator + textName);
                    if (file.exists()) {
                        textureIDs.set(matIndex, materials.size());//tileset.getTextureNames().size());

                        try {
                            //System.out.println(file.getAbsolutePath());
                            TilesetMaterial material = new TilesetMaterial();

                            BufferedImage img = ImageIO.read(file);
                            material.setTextureImg(img);
                            String name = Utils.removeExtensionFromPath(textName);
                            material.setImageName(textName);
                            material.setMaterialName(name);
                            material.setTextureNameImd(name);
                            material.setPaletteNameImd(name + "_pl");
                            materials.add(material);
                        } catch (IOException ex) {
                            throw new TextureNotFoundException(
                                    "Can't open texture named: \"" + textName + "\"");
                        }
                    } else {
                        throw new TextureNotFoundException(
                                "Texture named: \"" + textName
                                        + "\" does not exist in the folder of the OBJ file");
                    }
                    //textures.add(loadTexture(folderPath + "/" + textName));
                } else {
                    textureIDs.set(matIndex, textIndex);
                }
            }
        }
        inputMtl.close();

        //Tranform obj vertices, textures and normals into IMD vertices, textures and normals
        objDataToPolygonData(fIndsQuad, fIndsTri, vCoordsObj, tCoordsObj, nCoordsObj, colorsObj);

        //System.out.println("Donete!");
    }

    private static int countNumberOfStarts(File file, String content) throws IOException {
        InputStream input = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        int count = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(content)) {
                count++;
            }
        }
        return count;
    }

    public void objDataToPolygonData(ArrayList<Face> fIndsQuad,
                                     ArrayList<Face> fIndsTri, ArrayList<Float> vCoordsObj,
                                     ArrayList<Float> tCoordsObj, ArrayList<Float> nCoordsObj,
                                     ArrayList<Float> colorsObj) {

        for (int i = 0; i < textureIDs.size(); i++) {
            int start, end;

            start = texOffsetsQuad.get(i);
            if (i + 1 < textureIDs.size()) {
                end = texOffsetsQuad.get(i + 1);
            } else {
                end = fIndsQuad.size();
            }
            //System.out.println("start: " + start + " end: " + end);
            PolygonData polygon = new PolygonData();

            polygon.initQuads(end - start);
            for (int j = 0, diff = end - start, nV = 0, nT = 0, nN = 0, nC = 0; j < diff; j++) {
                Face f = fIndsQuad.get(start + j);
                for (int k = 0; k < 4; k++) {//vertex
                    for (int m = 0; m < 3; m++, nV++) {//coord
                        polygon.vCoordsQuad[nV] = vCoordsObj.get((f.vInd[k] - 1) * 3 + m);
                    }
                    for (int m = 0; m < 2; m++, nT++) {
                        polygon.tCoordsQuad[nT] = tCoordsObj.get((f.tInd[k] - 1) * 2 + m);
                    }
                    for (int m = 0; m < 3; m++, nN++) {
                        polygon.nCoordsQuad[nN] = nCoordsObj.get((f.nInd[k] - 1) * 3 + m);
                    }
                    for (int m = 0; m < 3; m++, nC++) {
                        polygon.colorsQuad[nC] = colorsObj.get((f.cInd[k] - 1) * 3 + m);
                    }
                }
            }

            start = texOffsetsTri.get(i);
            if (i + 1 < textureIDs.size()) {
                end = texOffsetsTri.get(i + 1);
            } else {
                end = fIndsTri.size();
            }

            polygon.initTris(end - start);
            for (int j = 0, diff = end - start, nV = 0, nT = 0, nN = 0, nC = 0; j < diff; j++) {
                Face f = fIndsTri.get(start + j);
                for (int k = 0; k < 3; k++) {//vertex
                    for (int m = 0; m < 3; m++, nV++) {//coord
                        polygon.vCoordsTri[nV] = vCoordsObj.get((f.vInd[k] - 1) * 3 + m);
                    }
                    for (int m = 0; m < 2; m++, nT++) {
                        polygon.tCoordsTri[nT] = tCoordsObj.get((f.tInd[k] - 1) * 2 + m);
                    }
                    for (int m = 0; m < 3; m++, nN++) {
                        polygon.nCoordsTri[nN] = nCoordsObj.get((f.nInd[k] - 1) * 3 + m);
                    }
                    for (int m = 0; m < 3; m++, nC++) {
                        polygon.colorsTri[nC] = colorsObj.get((f.cInd[k] - 1) * 3 + m);
                    }
                }
            }

            polygons.add(polygon);
        }
        //System.out.println("done!");

    }

    public void saveToXML(String xmlPath) throws ParserConfigurationException,
            TransformerConfigurationException, TransformerException, IOException {
        Document dom;

        // instance of a DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // use factory to get an instance of document builder
        DocumentBuilder db = dbf.newDocumentBuilder();
        // create instance of DOM
        dom = db.newDocument();

        // create the root element
        Element rootEle = dom.createElement(nodeName);
        for (int i = 0; i < attributes.size(); i++) {
            ImdAttribute attrib = attributes.get(i);
            rootEle.setAttribute(attrib.tag, attrib.value);
        }

        for (int i = 0; i < subnodes.size(); i++) {
            ImdNode subnode = subnodes.get(i);
            printImdNode(subnode, dom, rootEle);
        }

        //printImdNode(this, dom, rootEle);
        dom.appendChild(rootEle);

        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        //tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        // send DOM to file
        //tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(xmlPath));
        StreamResult streamResult = new StreamResult(new FileOutputStream(xmlPath));
        tr.transform(new DOMSource(dom), streamResult);
        streamResult.getOutputStream().close();

        //System.out.println("IMD saved!");

    }

    private void printImdNode(ImdNode node, Document dom, Element parent) {
        Element e;

        e = dom.createElement(node.nodeName);

        for (int i = 0; i < node.attributes.size(); i++) {
            ImdAttribute attrib = node.attributes.get(i);
            e.setAttribute(attrib.tag, attrib.value);
        }

        e.appendChild(dom.createTextNode(node.content));

        for (int i = 0; i < node.subnodes.size(); i++) {
            ImdNode subnode = node.subnodes.get(i);
            printImdNode(subnode, dom, e);
        }
        parent.appendChild(e);

    }

    private float[] getMinCoords() {
        float[] minCoords = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};

        for (int i = 0; i < polygons.size(); i++) {
            PolygonData p = polygons.get(i);
            for (int j = 0; j < p.vCoordsQuad.length / 3; j++) {
                for (int k = 0; k < 3; k++) {
                    minCoords[k] = Math.min(minCoords[k], p.vCoordsQuad[j * 3 + k]);
                }
            }
            for (int j = 0; j < p.vCoordsTri.length / 3; j++) {
                for (int k = 0; k < 3; k++) {
                    minCoords[k] = Math.min(minCoords[k], p.vCoordsTri[j * 3 + k]);
                }
            }
        }
        return minCoords;
    }

    private float[] getMaxCoords() {
        float[] maxCoords = new float[]{-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
        for (int i = 0; i < polygons.size(); i++) {
            PolygonData p = polygons.get(i);
            for (int j = 0; j < p.vCoordsQuad.length / 3; j++) {
                for (int k = 0; k < 3; k++) {
                    maxCoords[k] = Math.max(maxCoords[k], p.vCoordsQuad[j * 3 + k]);
                }
            }
            for (int j = 0; j < p.vCoordsTri.length / 3; j++) {
                for (int k = 0; k < 3; k++) {
                    maxCoords[k] = Math.max(maxCoords[k], p.vCoordsTri[j * 3 + k]);
                }
            }
        }
        return maxCoords;
    }

    private static float maxAbs(float[] array) {
        float max = -Float.MAX_VALUE;
        for (int i = 0; i < array.length; i++) {
            float abs = Math.abs(array[i]);
            if (abs > max) {
                max = abs;
            }
        }
        return max;
    }

    private int calculatePosScale() {
        float maxCoord = Math.max(maxAbs(minCoords), maxAbs(maxCoords));
        maxCoord *= 0.25f; //Blender scale correction
        maxCoord *= 1 << defaultPosScale;

        final float boundsMaxCoord = 7.9f;
        try {
            return (int) Math.ceil(Math.log(maxCoord / boundsMaxCoord) / Math.log(2));
        } catch (Exception ex) {
            return defaultPosScale;
        }
    }

    private void calculateBoxTest() {
        for (int i = 0; i < 3; i++) {
            boxTestCoords[i] = minCoords[i];
            boxTestSize[i] = maxCoords[i] - minCoords[i];
        }

        boxTestPosScale = calculateBoxTestPosScale();
        float scaleFactor = (float) (0.25f * Math.pow(2, defaultPosScale - boxTestPosScale)); //0.25 is Blender factor

        for (int i = 0; i < 3; i++) {
            boxTestCoords[i] *= scaleFactor;
            boxTestSize[i] *= scaleFactor;
        }
    }

    private int calculateBoxTestPosScale() {
        float maxCoord = Math.max(maxAbs(boxTestCoords), maxAbs(boxTestSize));
        maxCoord *= 0.25f; //Blender scale correction
        maxCoord *= 1 << defaultPosScale;

        final float boundsMaxCoord = 7.9f;
        try {
            return (int) Math.ceil(Math.log(maxCoord / boundsMaxCoord) / Math.log(2));
        } catch (Exception ex) {
            return defaultPosScale;
        }
    }

    private int getIndexOfMaterialByImgName(ArrayList<TilesetMaterial> materials,
                                            String imgName) {
        for (int i = 0; i < materials.size(); i++) {
            if (materials.get(i).getImageName().equals(imgName)) {
                return i;
            }
        }
        return -1;
    }

    private int numVertex() {
        int numVertex = 0;
        for (int i = 0; i < polygons.size(); i++) {
            PolygonData pData = polygons.get(i);
            numVertex += pData.nCoordsQuad.length / 3;
            numVertex += pData.nCoordsTri.length / 3;
        }
        return numVertex;
    }

    private void incrementAll(ArrayList<Integer> array, int amount) {
        for (int i = 0; i < array.size(); i++) {
            array.set(i, array.get(i) + amount);
        }
    }

    private void incrementAllFromValue(ArrayList<Integer> array, int amount, int startValue) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) >= startValue) {
                array.set(i, array.get(i) + amount);
            }
        }
    }

    public int getNumMaterials() {
        return materials.size();
    }

    public int getNumQuads() {
        int count = 0;
        for (int i = 0; i < polygons.size(); i++) {
            count += polygons.get(i).vCoordsQuad.length / (3 * 4);
        }
        return count;
    }

    public int getNumTris() {
        int count = 0;
        for (int i = 0; i < polygons.size(); i++) {
            count += polygons.get(i).vCoordsTri.length / (3 * 3);
        }
        return count;
    }

    public int getNumPolygons() {
        return getNumQuads() + getNumTris();
    }

    public int getNumVertices() {//TODO works only when there are no strips
        return numVertexTotal;
        //return getNumQuads() * 4 + getNumTris() * 3;
    }

}
