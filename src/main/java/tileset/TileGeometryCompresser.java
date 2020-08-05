/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tileset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author Trifindo
 */
public class TileGeometryCompresser {

    private static class VertexData {

        float[] data;

        public VertexData(float[] array, int offset, int dataSize) {
            data = new float[dataSize];
            for (int i = 0; i < dataSize; i++) {
                data[i] = array[offset + i];
            }
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
            if (!Arrays.equals(this.data, other.data)) {
                return false;
            }
            return true;
        }
    }
    
    public static class CompressedObjData{
         public ArrayList<Float> data;
         public int[][] triIndices;
         public int[][] quadIndices;
         
         public CompressedObjData(ArrayList<VertexData> dataArray, int[][] triIndices, int[][] quadIndices, int coordsPerVertex){
             data = new ArrayList<>(dataArray.size() * coordsPerVertex);
             for(int i = 0; i < dataArray.size(); i++){
                 float[] dataPatch = dataArray.get(i).data;
                 for(int j = 0; j < dataPatch.length; j++){
                     data.add(dataPatch[j]);
                 }
             }
             this.triIndices = triIndices;
             this.quadIndices = quadIndices;
         }
    }
    
    
    public static void compressTile(Tile tile){
        System.out.println("Starting compression...");
        CompressedObjData vData = compressObjData(tile.getVCoordsTri(), tile.getVCoordsQuad(), 3);
        CompressedObjData tData = compressObjData(tile.getTCoordsTri(), tile.getTCoordsQuad(), 2);
        CompressedObjData nData = compressObjData(tile.getNCoordsTri(), tile.getNCoordsQuad(), 3);
        CompressedObjData cData = compressObjData(tile.getColorsTri(), tile.getColorsQuad(), 3);
        
        final int numTris = tile.getVCoordsTri().length / (3 * 3);
        ArrayList<Face> faceIndsTri = new ArrayList<>(numTris);
        for(int i = 0; i < numTris; i++){
            Face face = new Face(false);
            face.vInd = vData.triIndices[i];
            face.tInd = tData.triIndices[i];
            face.nInd = nData.triIndices[i];
            face.cInd = cData.triIndices[i];
            faceIndsTri.add(face);
        }
        
        final int numQuads = tile.getVCoordsQuad().length / (3 * 4);
        ArrayList<Face> faceIndsQuad = new ArrayList<>(numQuads);
        for(int i = 0; i < numQuads; i++){
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

    private static void addElementsToDataSet(HashSet<VertexData> dataSet, float[] data, int coordsPerVertex){
        final int numVertices = data.length / coordsPerVertex;
        for (int i = 0; i < numVertices; i++) {
            VertexData vData = new VertexData(data, i * coordsPerVertex, coordsPerVertex);
            dataSet.add(vData);
        }
    }
    
    private static int[][] generateIndices(ArrayList<VertexData> dataArray, 
            float[] data, int numFaces, int vertexPerFace, int coordsPerVertex){
        int[][] faceIndices = new int[numFaces][vertexPerFace];
        for (int i = 0; i < numFaces; i++) {
            for(int j = 0; j < vertexPerFace; j++){
                VertexData vData = new VertexData(data, i * (coordsPerVertex * vertexPerFace) + j * coordsPerVertex, coordsPerVertex);
                faceIndices[i][j] = dataArray.indexOf(vData) + 1; //+1 for the OBJ format
            }
        }
        return faceIndices;
    }
    
    public static CompressedObjData compressObjData(float[] triData, float[] quadData, int coordsPerVertex) {
        HashSet<VertexData> dataSet = new HashSet();
        addElementsToDataSet(dataSet, triData, coordsPerVertex);
        addElementsToDataSet(dataSet, quadData, coordsPerVertex);
        ArrayList<VertexData> dataArray = new ArrayList<>(dataSet);

        final int numTris = triData.length / (coordsPerVertex * 3);
        final int numQuads = quadData.length / (coordsPerVertex * 4);
        int[][] triIndices = generateIndices(dataArray, triData, numTris, 3, coordsPerVertex);
        int[][] quadIndices = generateIndices(dataArray, quadData, numQuads, 4, coordsPerVertex);
        
        return new CompressedObjData(dataArray, triIndices, quadIndices, coordsPerVertex);
    }
    
    private static ArrayList<Float> arrayToArrayList(float[] array){
        ArrayList<Float> arrayList = new ArrayList<>(array.length);
        for(int i = 0; i < array.length; i++){
            arrayList.add(array[i]);
        }
        return arrayList;
    }
    
    private static ArrayList<Integer> arrayToArrayList(int[] array){
        ArrayList<Integer> arrayList = new ArrayList<>(array.length);
        for(int i = 0; i < array.length; i++){
            arrayList.add(array[i]);
        }
        return arrayList;
    }

}
