
package tileset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Trifindo
 */
public class TileGeometryCompressor {

    private static class VertexData {
        float[] data;

        public VertexData(float[] array, int offset, int dataSize) {
            data = new float[dataSize];
            System.arraycopy(array, offset, data, 0, dataSize);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Arrays.hashCode(this.data);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final VertexData other = (VertexData) obj;
            return Arrays.equals(this.data, other.data);
        }
    }

    public static class CompressedObjData {
        public List<Float> data;
        public int[][] triIndices;
        public int[][] quadIndices;

        public CompressedObjData(List<VertexData> dataArray, int[][] triIndices, int[][] quadIndices, int coordsPerVertex) {
            data = new ArrayList<>(dataArray.size() * coordsPerVertex);
            for (VertexData vertexData : dataArray) {
                float[] dataPatch = vertexData.data;
                for (float patch : dataPatch) {
                    data.add(patch);
                }
            }
            this.triIndices = triIndices;
            this.quadIndices = quadIndices;
        }
    }

    public static void compressTile(Tile tile) {
        System.out.println("Starting compression...");
        CompressedObjData vData = compressObjData(tile.getVCoordsTri(), tile.getVCoordsQuad(), 3);
        CompressedObjData tData = compressObjData(tile.getTCoordsTri(), tile.getTCoordsQuad(), 2);
        CompressedObjData nData = compressObjData(tile.getNCoordsTri(), tile.getNCoordsQuad(), 3);
        CompressedObjData cData = compressObjData(tile.getColorsTri(), tile.getColorsQuad(), 3);

        final int numTris = tile.getVCoordsTri().length / (3 * 3);
        List<Face> faceIndsTri = new ArrayList<>(numTris);
        for (int i = 0; i < numTris; i++) {
            Face face = new Face(false);
            face.vInd = vData.triIndices[i];
            face.tInd = tData.triIndices[i];
            face.nInd = nData.triIndices[i];
            face.cInd = cData.triIndices[i];
            faceIndsTri.add(face);
        }

        final int numQuads = tile.getVCoordsQuad().length / (3 * 4);
        List<Face> faceIndsQuad = new ArrayList<>(numQuads);
        for (int i = 0; i < numQuads; i++) {
            Face face = new Face(true);
            face.vInd = vData.quadIndices[i];
            face.tInd = tData.quadIndices[i];
            face.nInd = nData.quadIndices[i];
            face.cInd = cData.quadIndices[i];
            faceIndsQuad.add(face);
        }

        tile.setFaceIndsTris(faceIndsTri);
        tile.setFaceIndsQuads(faceIndsQuad);
        tile.setVertexCoordsObj(vData.data);
        tile.setTextureCoordsObj(tData.data);
        tile.setNormalCoordsObj(nData.data);
        tile.setColorsObj(cData.data);

        System.out.println("Tile compressing finished!");
    }

    private static void addElementsToDataSet(Set<VertexData> dataSet, float[] data, int coordsPerVertex) {
        final int numVertices = data.length / coordsPerVertex;
        for (int i = 0; i < numVertices; i++) {
            VertexData vData = new VertexData(data, i * coordsPerVertex, coordsPerVertex);
            dataSet.add(vData);
        }
    }

    private static int[][] generateIndices(List<VertexData> dataArray, float[] data, int numFaces, int vertexPerFace, int coordsPerVertex) {
        int[][] faceIndices = new int[numFaces][vertexPerFace];
        for (int i = 0; i < numFaces; i++) {
            for (int j = 0; j < vertexPerFace; j++) {
                VertexData vData = new VertexData(data, i * (coordsPerVertex * vertexPerFace) + j * coordsPerVertex, coordsPerVertex);
                faceIndices[i][j] = dataArray.indexOf(vData) + 1; //+1 for the OBJ format
            }
        }
        return faceIndices;
    }

    public static CompressedObjData compressObjData(float[] triData, float[] quadData, int coordsPerVertex) {
        Set<VertexData> dataSet = new HashSet<>();
        addElementsToDataSet(dataSet, triData, coordsPerVertex);
        addElementsToDataSet(dataSet, quadData, coordsPerVertex);
        List<VertexData> dataArray = new ArrayList<>(dataSet);

        final int numTris = triData.length / (coordsPerVertex * 3);
        final int numQuads = quadData.length / (coordsPerVertex * 4);
        int[][] triIndices = generateIndices(dataArray, triData, numTris, 3, coordsPerVertex);
        int[][] quadIndices = generateIndices(dataArray, quadData, numQuads, 4, coordsPerVertex);

        return new CompressedObjData(dataArray, triIndices, quadIndices, coordsPerVertex);
    }

    private static List<Float> arrayToArrayList(float[] array) {
        List<Float> arrayList = new ArrayList<>(array.length);
        for (float v : array) {
            arrayList.add(v);
        }
        return arrayList;
    }

    private static List<Integer> arrayToArrayList(int[] array) {
        return Arrays.stream(array)
                .boxed()
                .collect(Collectors.toList());
    }
}
