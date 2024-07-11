package shop.api.Storage;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;
import shop.api.exception.StorageException;
import shop.api.exception.StorageFileNotFoundException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public abstract class StorageService {

    @Value("${domain}")
    private String domain;

    private final Path rootLocation;

    public StorageService(StorageProperties storageProperties) {
        this.rootLocation = Paths.get(storageProperties.getLocation());
    }

    public abstract void init();


    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Path uniqueDestinationFile = getUniqueDestinationFile(destinationFile);
                Files.copy(inputStream, uniqueDestinationFile,
                        StandardCopyOption.REPLACE_EXISTING);

            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }

    }

    public String formatFileURL(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            try (InputStream inputStream = file.getInputStream()) {
                Path uniqueDestinationFile = getUniqueDestinationFile(destinationFile);
                String filename = uniqueDestinationFile.getFileName().toString();
                String baseName = FilenameUtils.getBaseName(filename);
                String extension = FilenameUtils.getExtension(filename);

                return baseName + "." + extension;
            }
        } catch (IOException e) {
            throw new StorageException("Failed to format fileURL.", e);
        }
    }

    protected Path getUniqueDestinationFile(Path destinationFile) {
        Path uniquePath = destinationFile;
        int count = 1;
        while (Files.exists(uniquePath)) {
            String filename = destinationFile.getFileName().toString();
            String baseName = FilenameUtils.getBaseName(filename);
            String extension = FilenameUtils.getExtension(filename);

            String uniqueFilename = baseName + "(" + count + ")." + extension;
            uniquePath = destinationFile.resolveSibling(uniqueFilename);
            count++;
        }
        return uniquePath;
    }

    public boolean isVideo(MultipartFile file) {
        Tika tika = new Tika();

        try {
            String mimeType = tika.detect(file.getInputStream());

            // Check if the MIME type is a video type
            return mimeType != null && mimeType.startsWith("video/");
        } catch (IOException e) {
            return false;
        }
    }

    public boolean isImage(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {

            // Đọc dữ liệu hình ảnh và video từ InputStream
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                return false;
            }
            // Kiểm tra xem image có chứa dữ liệu hợp lệ hay không
            // Trong trường hợp này, chúng ta chỉ muốn đảm bảo rằng image/video không rỗng
            return image.getWidth() > 0 && image.getHeight() > 0;
        }
        catch (IOException e) {
            return false;
        }
    }


    public abstract Stream<Path> loadAll();

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    public abstract void deleteAll();

}