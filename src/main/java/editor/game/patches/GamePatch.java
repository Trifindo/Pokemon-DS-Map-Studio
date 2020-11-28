package editor.game.patches;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.Utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamePatch {

    private List<MultiFilePatch> patches;

    public GamePatch(String path) throws IOException, SAXException, ParserConfigurationException {
        List<MultiFilePatch> multiFilePatches = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(path));

        Element root = document.getDocumentElement();
        root.getAttributes();

        List<Node> patchNodes = getSubnodes(root);
        for(Node gamePatch : patchNodes){
            String gameCode = getSubnode(gamePatch, "GameCode").getTextContent();

            List<Node> filePatchNodes = getSubnodes(gamePatch, "FilePatch");
            List<FilePatch> patches = new ArrayList<>(filePatchNodes.size());
            for(Node filePatchNode : filePatchNodes){
                String filePath = getSubnode(filePatchNode, "FilePath").getTextContent();
                int dataOffset = Integer.parseInt(getSubnode(filePatchNode, "DataOffset").getTextContent(), 16);
                byte[] oldData = hexStringToByteArray(getSubnode(filePatchNode, "OldData").getTextContent());
                byte[] newData = hexStringToByteArray(getSubnode(filePatchNode, "NewData").getTextContent());

                FilePatch filePatch = new FilePatch(filePath, dataOffset, oldData, newData);
                patches.add(filePatch);
                System.out.println("Donete");
            }

            MultiFilePatch multiFilePatch = new MultiFilePatch(gameCode, patches);
            multiFilePatches.add(multiFilePatch);
        }
        this.patches = multiFilePatches;

        System.out.println("Done reading!");
    }

    public static byte[] hexStringToByteArray(String s) {
        s = s.replaceAll(" ", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static byte[] textBytesToByteArray(String textBytes){
        String[] splitString = textBytes.split(" ");
        List<Byte> bytes = new ArrayList<>(splitString.length);
        for(String s : splitString){
            try {
                bytes.add(Byte.parseByte(s));
            }catch(NumberFormatException ex){

            }
        }
        return Utils.toArray(bytes);
    }

    private static Node getSubnode(Node node, String name){
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node subnode = nodeList.item(i);
            if (subnode.getNodeType() == Node.ELEMENT_NODE) {
                if(subnode.getNodeName().equals(name)){
                    return subnode;
                }
            }
        }
        return null;
    }

    private static List<Node> getSubnodes(Node node, String name){
        List<Node> nodes = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node subnode = nodeList.item(i);
            if (subnode.getNodeType() == Node.ELEMENT_NODE) {
                if(subnode.getNodeName().equals(name)){
                    nodes.add(subnode);
                }
            }
        }
        return nodes;
    }

    private static List<Node> getSubnodes(Node node){
        List<Node> nodes = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node subnode = nodeList.item(i);
            if (subnode.getNodeType() == Node.ELEMENT_NODE) {
                nodes.add(subnode);
            }
        }
        return nodes;
    }
}
