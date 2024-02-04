package ppl.common.utils.compress.impl;

import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import ppl.common.utils.compress.Archive;
import ppl.common.utils.compress.ArchiveException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AbstractSevenZip implements Archive {
    private final String path;
    private final IInArchive archive;

    protected AbstractSevenZip(File compressFile, Creator creator) throws SevenZipException, IOException {
        this.path = compressFile.getAbsolutePath();
        this.archive = creator.create(compressFile);
    }

    @Override
    public void decompressTo(File toDir) {
        try {
            Map<Integer, Path> targetPaths = extractTargetPaths(toDir.toPath());
            if (targetPaths.isEmpty()) {
                return;
            }

            Integer[] indices = targetPaths.keySet().stream()
                    .sorted()
                    .toArray(Integer[]::new);
            archive.extract(unwrap(indices), false, new IArchiveExtractCallback() {
                @Override
                public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {
                    if (extractAskMode == ExtractAskMode.EXTRACT) {
                        Path targetPath = targetPaths.get(index);
                        if (targetPath == null) {
                            throw new SevenZipException("Unknown error. Where the item extracted to is unknown.");
                        }

                        try {
                            return new RandomAccessFileOutStream(new RandomAccessFile(targetPath.toFile(), "rw"));
                        } catch (FileNotFoundException e) {
                            throw new SevenZipException(String.format(
                                    "Couldn't open file: %s for writing", targetPath), e);
                        }
                    }
                    return null;
                }

                @Override
                public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
                }

                @Override
                public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
                }

                @Override
                public void setTotal(long total) throws SevenZipException {
                }

                @Override
                public void setCompleted(long complete) throws SevenZipException {
                }
            });
        } catch (SevenZipException e) {
            throw new ArchiveException("Couldn't create file for decompressing file: " + this.path, e);
        } catch (IOException e) {
            throw new ArchiveException("Couldn't extract file from compressed file: " + this.path, e);
        }
    }

    private Map<Integer, Path> extractTargetPaths(Path toDir) throws SevenZipException, IOException {
        Map<Integer, Path> targetPaths = new HashMap<>();
        int count = archive.getNumberOfItems();
        for (int i = 0; i < count; i++) {
            if (!(Boolean) archive.getProperty(i, PropID.IS_FOLDER)) {
                Path path = Paths.get((String) archive.getProperty(i, PropID.PATH));
                Path target = toDir.resolve(path);
                ensureParent(target);
                targetPaths.put(i, target);
            }
        }
        return targetPaths;
    }

    private void ensureParent(Path target) throws IOException {
        File parent = target.getParent().toFile();
        if (!parent.mkdirs() && !parent.isDirectory()) {
            throw new IOException("Failed to create directory: " + parent);
        }
    }

    private int[] unwrap(Integer[] indices) {
        int[] ret = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            ret[i] = indices[i];
        }
        return ret;
    }

    @Override
    public void close() throws ArchiveException {
        try {
            this.archive.close();
        } catch (IOException e) {
            throw new ArchiveException("Failed to close rar file: " + this.path);
        }
    }

    protected interface Creator {
        IInArchive create(File compressFile) throws IOException;
    }

}
