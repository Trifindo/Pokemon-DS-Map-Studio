
package formats.narc2;

import utils.exceptions.WrongFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import utils.BinaryReader;
import utils.BinaryWriter;
import utils.Utils.MutableInt;
import utils.Utils.MutableLong;

public class NarcIO {

    public static Narc loadNarc(String path) throws IOException, WrongFormatException {
        BinaryReader reader = new BinaryReader(path);

        try {
            Header header = new Header(reader);
            FileAllocationTable fatb = new FileAllocationTable(reader);
            FilenameTable filenameTable = new FilenameTable(reader);
            FileImageBlock fileImage = new FileImageBlock(reader, fatb);
            reader.close();

            final long nameTableOffset = filenameTable.directoryTable.entries.size() * 8L;
            int filesAdded = 0;
            List<NarcFolder> folders = new ArrayList<>(filenameTable.directoryTable.entries.size());
            for (int i = 0; i < filenameTable.directoryTable.entries.size(); i++) {
                folders.add(new NarcFolder());
            }
            if (filenameTable.entryNameTable != null) {
                for (int i = 0; i < filenameTable.directoryTable.entries.size(); i++) {
                    NarcFolder currentFolder = folders.get(i);
                    DirectoryEntry directoryEntry = filenameTable.directoryTable.entries.get(i);
                    if (i != 0) {
                        currentFolder.setParent(folders.get(directoryEntry.getDirectoryParentID()));
                        currentFolder.getParent().getSubfolders().add(currentFolder);
                    }
                    EntryName entryName;
                    int entryNameOffset = 0;
                    while (!(entryName = getEntryName(filenameTable.entryNameTable, directoryEntry.dirEntryStart + entryNameOffset - nameTableOffset)).isEndOfDirectory()) {
                        if (entryName.isDirectory()) {
                            folders.get(entryName.getDiretoryID()).setName(entryName.entryName);
                        } else if (!entryName.isEndOfDirectory()) {//File
                            NarcFile file = new NarcFile(entryName.entryName, currentFolder, fileImage.fileImages.get(filesAdded));
                            currentFolder.getFiles().add(file);
                            filesAdded++;
                        }
                        entryNameOffset += entryName.getDataSize();
                    }
                }
            } else {//No filenames
                for (int i = 0; i < fileImage.fileImages.size(); i++) {
                    byte[] fileData = fileImage.fileImages.get(i);
                    folders.get(0).getFiles().add(new NarcFile("", null, fileData));
                }
            }

            System.out.println("Narc loaded!");

            return new Narc(folders.get(0));
        } catch (Exception ex) {
            reader.close();
            throw ex;
        }
    }

    public static void writeNarc(Narc narc, String path) throws IOException {
        BinaryWriter writer = new BinaryWriter(path);

        narc.calculateIndices();
        List<NarcFile> files = narc.getAllFiles();
        List<NarcFolder> folders = narc.getAllFolders();

        FileAllocationTable fatb = new FileAllocationTable(files);
        FilenameTable filenameTable = new FilenameTable(folders, narc.hasNamedFiles());
        FileImageBlock fileImage = new FileImageBlock(files);
        Header header = new Header(
                fatb.getDataSize()
                        + filenameTable.getDataSize() + getBytesPadding(filenameTable.getDataSize(), 4)
                        + fileImage.getDataSize()
        );

        header.writeData(writer);
        fatb.writeData(writer);
        filenameTable.writeData(writer);
        fileImage.writeData(writer);

        writer.close();

        System.out.println("Narc written!");
    }

    //TODO: Slow method. This can be replaced by a table generated only once
    private static EntryName getEntryName(EntryNameTable entryNameTable, long offset) {
        long bytesRead = 0;
        for (int i = 0; i < entryNameTable.entries.size(); i++) {
            EntryName entry = entryNameTable.entries.get(i);
            if (bytesRead == offset) {
                return entry;
            }
            bytesRead += entry.getDataSize();
        }
        return null;
    }

    private static int getBytesPadding(long dataSize, int align) {
        int remainder = (int) (dataSize % align);
        if (remainder == 0) {
            return 0;
        } else {
            return align - remainder;
        }
    }

    private static void writePadding(BinaryWriter writer, int padding, long dataSize, int align) throws IOException {
        int nBytesPadding = getBytesPadding(dataSize, align);
        for (int j = 0; j < nBytesPadding; j++) {
            writer.writeUInt8(padding);
        }
    }

    private static class Header {

        public static final String signature = "NARC";
        public static final int byteOrder = 65534;
        public static final int version = 256;
        public long fileSize;
        public int headerSize;
        public int dataBlocks;

        private Header(BinaryReader reader) throws WrongFormatException, IOException {
            if (!reader.readString(4).equals(signature)) {
                throw new WrongFormatException("Wrong file signature");
            }
            if (reader.readUInt16() != byteOrder) { //Byte order little endian
                throw new WrongFormatException("Wrong byte order");
            }
            if (reader.readUInt16() != version) { //Version 1.0
                throw new WrongFormatException("Wrong version");
            }
            fileSize = reader.readUInt32();
            headerSize = reader.readUInt16();
            dataBlocks = reader.readUInt16();
        }

        public Header(long fileSize) {
            headerSize = (int) getDataSize();
            dataBlocks = 3;
            this.fileSize = fileSize + headerSize;
        }

        public void writeData(BinaryWriter writer) throws IOException {
            writer.writeString(signature);
            writer.writeUInt16(byteOrder);
            writer.writeUInt16(version);
            writer.writeUInt32(fileSize);
            writer.writeUInt16(headerSize);
            writer.writeUInt16(dataBlocks);
        }

        public long getDataSize() {
            return 16;
        }
    }

    private static class FileAllocationTable {

        public static final String signature = "BTAF";
        public long size;
        public int numFiles;
        public List<FileAllocationEntry> allocationTable;

        public FileAllocationTable(BinaryReader reader) throws WrongFormatException, IOException {
            if (!reader.readString(4).equals(signature)) {
                throw new WrongFormatException("Wrong file allocaton table signature");
            }
            size = reader.readUInt32();
            numFiles = reader.readUInt16();
            reader.readUInt16(); //Padding
            allocationTable = new ArrayList<>(numFiles);
            for (int i = 0; i < numFiles; i++) {
                allocationTable.add(new FileAllocationEntry(reader));
            }
        }

        public FileAllocationTable(List<NarcFile> files) {
            allocationTable = new ArrayList<>(files.size());
            numFiles = files.size();
            long bytesAdded = 0;
            for (NarcFile file : files) {
                allocationTable.add(new FileAllocationEntry(bytesAdded, bytesAdded + file.getData().length));
                bytesAdded += file.getData().length + getBytesPadding(file.getData().length, 4);
            }
            size = getDataSize();
        }

        public void writeData(BinaryWriter writer) throws IOException {
            writer.writeString(signature);
            writer.writeUInt32(size);
            writer.writeUInt16(numFiles);
            writer.writeUInt16(0);
            for (FileAllocationEntry entry : allocationTable) {
                entry.writeData(writer);
            }
        }

        public long getDataSize() {
            int sum = 12;
            for (FileAllocationEntry entry : allocationTable) {
                sum += entry.getDataSize();
            }
            return sum;
        }
    }

    private static class FileAllocationEntry {

        public long fileTop;
        public long fileBottom;

        public FileAllocationEntry(BinaryReader reader) throws IOException {
            fileTop = reader.readUInt32();
            fileBottom = reader.readUInt32();
        }

        public FileAllocationEntry(long fileTop, long fileBottom) {
            this.fileTop = fileTop;
            this.fileBottom = fileBottom;
        }

        public long getFileSize() {
            return fileBottom - fileTop;
        }

        public void writeData(BinaryWriter writer) throws IOException {
            writer.writeUInt32(fileTop);
            writer.writeUInt32(fileBottom);
        }

        public long getDataSize() {
            return 8;
        }
    }

    private static class FilenameTable {

        public static final String signature = "BTNF";
        public long size;
        public DirectoryTable directoryTable;
        public EntryNameTable entryNameTable;

        public FilenameTable(BinaryReader reader) throws IOException, WrongFormatException {
            if (!reader.readString(4).equals(signature)) {
                throw new WrongFormatException("Wrong nametable signature");
            }
            size = reader.readUInt32();
            directoryTable = new DirectoryTable(reader);

            if (directoryTable.entries.size() * 8L + 8 < size) {//Files have name
                entryNameTable = new EntryNameTable(reader, size - (directoryTable.entries.size() * 8L + 8));
            } else {//Files don't have name
                entryNameTable = null;
            }
        }

        public FilenameTable(List<NarcFolder> folders, boolean hasNamedFiles) {
            final int numDirectories = folders.size();
            if (hasNamedFiles) {

                long[] offsets = new long[numDirectories];
                int[] startFileIDs = new int[numDirectories];

                List<EntryName> nameEntries = new ArrayList<>();
                addEntriesFromFolder(
                        nameEntries,
                        startFileIDs, offsets,
                        folders.get(0), numDirectories,
                        new MutableInt(0), new MutableLong(0));

                List<DirectoryEntry> dirEntries = new ArrayList<>(numDirectories);
                dirEntries.add(new DirectoryEntry(numDirectories * 8L, numDirectories));
                for (int i = 1; i < numDirectories; i++) {
                    NarcFolder folder = folders.get(i);
                    dirEntries.add(new DirectoryEntry(numDirectories * 8L + offsets[i], startFileIDs[i], folder.getParent().getID()));
                }

                directoryTable = new DirectoryTable(dirEntries);
                entryNameTable = new EntryNameTable(nameEntries);
            } else {

                List<DirectoryEntry> dirEntries = new ArrayList<>(numDirectories);
                //dirEntries.add(new DirectoryEntry(numDirectories * 8, numDirectories));
                dirEntries.add(new DirectoryEntry(numDirectories * 4L, numDirectories));//TODO: Should be 8 instead of 4??

                directoryTable = new DirectoryTable(dirEntries);
                entryNameTable = null;
            }
            size = getDataSize();
        }

        public void addEntriesFromFolder(
                List<EntryName> nameEntries,
                int[] startFileIDs, long[] offsets,
                NarcFolder folder, final int numDirectories,
                MutableInt filesAdded, MutableLong bytesAdded) {

            offsets[folder.getID()] = bytesAdded.value;
            startFileIDs[folder.getID()] = filesAdded.value;
            for (NarcFile file : folder.getFiles()) {
                EntryName entryName = new EntryName(file);
                nameEntries.add(entryName);
                filesAdded.value++;
                bytesAdded.value += entryName.getDataSize();
            }
            for (NarcFolder subfolder : folder.getSubfolders()) {
                EntryName entryName = new EntryName(subfolder, subfolder.getID());
                nameEntries.add(entryName);
                bytesAdded.value += entryName.getDataSize();
            }
            EntryName endEntry = EntryName.getEntryEndOfDirectory();
            nameEntries.add(endEntry);
            bytesAdded.value += endEntry.getDataSize();

            for (NarcFolder subfolder : folder.getSubfolders()) {
                addEntriesFromFolder(
                        nameEntries,
                        startFileIDs, offsets,
                        subfolder, numDirectories,
                        filesAdded, bytesAdded);
            }
        }

        public void writeData(BinaryWriter writer) throws IOException {
            writer.writeString(signature);
            writer.writeUInt32(size + getBytesPadding(size, 4));
            directoryTable.writeData(writer);
            if (entryNameTable != null) {
                entryNameTable.writeData(writer);
            }
            writePadding(writer, 255, getDataSize(), 4);
        }

        public long getDataSize() {
            if (entryNameTable != null) {
                return directoryTable.getDataSize() + entryNameTable.getDataSize() + 8;
            } else {
                return directoryTable.getDataSize() + 8;
            }
        }
    }

    private static class DirectoryTable {

        public List<DirectoryEntry> entries;

        public DirectoryTable(BinaryReader reader) throws IOException {
            DirectoryEntry root = new DirectoryEntry(reader);
            final int numDirectories = root.dirParentID;
            entries = new ArrayList<>(numDirectories);
            entries.add(root);
            for (int i = 1; i < numDirectories; i++) {
                entries.add(new DirectoryEntry(reader));
            }
        }

        public DirectoryTable(List<DirectoryEntry> entries) {
            this.entries = entries;
        }

        public void writeData(BinaryWriter writer) throws IOException {
            for (DirectoryEntry entry : entries) {
                entry.writeData(writer);
            }
        }

        public long getDataSize() {
            return entries.stream().mapToLong(DirectoryEntry::getDataSize).sum();
        }
    }

    private static class DirectoryEntry {

        public long dirEntryStart;
        public int dirEntryFileID;
        public int dirParentID;

        public DirectoryEntry(BinaryReader reader) throws IOException {
            dirEntryStart = reader.readUInt32();
            dirEntryFileID = reader.readUInt16();
            dirParentID = reader.readUInt16();
        }

        public DirectoryEntry(long directoryEntryStart, int dirEntryFileID, int dirParentID) {
            this.dirEntryStart = directoryEntryStart;
            this.dirEntryFileID = dirEntryFileID;
            this.dirParentID = dirParentID + 61440;
        }

        private DirectoryEntry(long dirEntryStart, int numDirectories) {
            this.dirEntryStart = dirEntryStart;
            this.dirEntryFileID = 0;
            this.dirParentID = numDirectories;
        }

        public static DirectoryEntry getRootDirectory(long dirEntryStart, int numDirectories) {
            return new DirectoryEntry(dirEntryStart, numDirectories);
        }

        public int getDirectoryParentID() {
            return dirParentID - 61440;
        }

        public void writeData(BinaryWriter writer) throws IOException {
            writer.writeUInt32(dirEntryStart);
            writer.writeUInt16(dirEntryFileID);
            writer.writeUInt16(dirParentID);
        }

        public long getDataSize() {
            return 8;
        }
    }

    private static class EntryNameTable {

        public List<EntryName> entries;

        public EntryNameTable(BinaryReader reader, long bytesToRead) throws IOException {
            entries = new ArrayList<>();

            int bytesRead = 0;
            while (bytesRead < bytesToRead) {
                int firstByte = reader.readUInt8();
                if (firstByte == 255) {//Padding
                    bytesRead += 1;
                } else {
                    EntryName entry = new EntryName(reader, firstByte);
                    entries.add(entry);
                    bytesRead += entry.getDataSize();
                }
            }
        }

        private EntryNameTable(List<EntryName> entries) {
            this.entries = entries;
        }

        public EntryNameTable(NarcFolder root) {
            entries = new ArrayList<>();
            addEntriesFromFolder(entries, root, 0);
        }

        private static void addEntriesFromFolder(List<EntryName> entries, NarcFolder folder, int directoriesAdded) {
            for (NarcFile file : folder.getFiles()) {
                entries.add(new EntryName(file));
            }
            for (NarcFolder subfolder : folder.getSubfolders()) {
                entries.add(new EntryName(subfolder, directoriesAdded));
                directoriesAdded++;
            }
            entries.add(EntryName.getEntryEndOfDirectory());
            for (NarcFolder subfolder : folder.getSubfolders()) {
                addEntriesFromFolder(entries, subfolder, directoriesAdded);
            }
        }

        public void writeData(BinaryWriter writer) throws IOException {
            for (EntryName entry : entries) {
                entry.writeData(writer);
            }
        }

        public long getDataSize() {
            return entries.stream().mapToLong(EntryName::getDataSize).sum();
        }
    }

    private static class EntryName {

        public int firstByte;
        public String entryName;
        public int directoryID;

        public EntryName(BinaryReader reader, int firstByte) throws IOException {
            this.firstByte = firstByte;
            if (firstByte != 0) {
                entryName = reader.readString(getEntryNameLength());
                if (isDirectory()) {
                    directoryID = reader.readUInt16();
                }
            } else {
                //Last entry of directory
            }
        }

        public EntryName(NarcFolder folder, int id) {
            entryName = folder.getName();
            firstByte = entryName.length() | 0x80;
            directoryID = id + 61440;
        }

        public EntryName(NarcFile file) {
            entryName = file.getName();
            firstByte = entryName.length();
        }

        public EntryName(int firstByte, String entryName, int directoryID) {
            this.firstByte = firstByte;
            this.entryName = entryName;
            this.directoryID = directoryID;
        }

        public static EntryName getEntryEndOfDirectory() {
            return new EntryName(0, "", 0);
        }

        public void writeData(BinaryWriter writer) throws IOException {
            if (isEndOfDirectory()) {
                writer.writeUInt8(firstByte);
            } else {
                if (isDirectory()) {
                    writer.writeUInt8(firstByte);
                    writer.writeString(entryName);
                    writer.writeUInt16(directoryID);
                } else {
                    writer.writeUInt8(firstByte);
                    writer.writeString(entryName);
                }
            }
        }

        public long getDataSize() {
            if (isEndOfDirectory()) {
                return 1;
            } else {
                if (isDirectory()) {
                    return entryName.length() + 1 + 2;
                } else {
                    return entryName.length() + 1;
                }
            }
        }

        public boolean isDirectory() {
            return (firstByte & 0x80) == 0x80;
        }

        public boolean isEndOfDirectory() {
            return firstByte == 0;
        }

        public int getEntryNameLength() {
            return firstByte & 0x7F;
        }

        public int getDiretoryID() {
            return directoryID - 61440;
        }
    }

    private static class FileImageBlock {

        public static final String signature = "GMIF";
        public long size;
        public List<byte[]> fileImages;

        public FileImageBlock(BinaryReader reader, FileAllocationTable fatb) throws IOException, WrongFormatException {
            if (!reader.readString(4).equals(signature)) {
                throw new WrongFormatException("Wrong file image block signature");
            }
            size = reader.readUInt32();

            fileImages = new ArrayList<>(fatb.allocationTable.size());
            byte[] allImageData = reader.readBytes((int) size);
            try {
                for (FileAllocationEntry entry : fatb.allocationTable) {
                    fileImages.add(BinaryReader.readBytes(allImageData, (int) entry.fileTop, (int) entry.getFileSize()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new IOException();
            }
            /*
            fileImages = new ArrayList<byte[]>(fatb.allocationTable.size());
            for (FileAllocationEntry entry : fatb.allocationTable) {
                fileImages.add(reader.readBytes((int) entry.getFileSize()));
            }*/
        }

        public FileImageBlock(List<NarcFile> files) {
            fileImages = new ArrayList<>(files.size());
            for (NarcFile file : files) {
                fileImages.add(file.getData());
            }
            size = getDataSize();
        }

        public void writeData(BinaryWriter writer) throws IOException {
            writer.writeString(signature);
            writer.writeUInt32(size);
            for (int i = 0; i < fileImages.size() - 1; i++) {
                writer.writeBytes(fileImages.get(i));
                writePadding(writer, 255, fileImages.get(i).length, 4);//Padding between files
            }
            writer.writeBytes(fileImages.get(fileImages.size() - 1));
        }

        public long getDataSize() {
            long sum = 8;
            sum += IntStream.range(0, fileImages.size() - 1)
                    .mapToLong(i -> fileImages.get(i).length + getBytesPadding(fileImages.get(i).length, 4))
                    .sum();
            sum += fileImages.get(fileImages.size() - 1).length;
            return sum;
        }
    }
}
