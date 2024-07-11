package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.api.Storage.StorageProperties;
import shop.api.Storage.StorageService;
import shop.api.models.Photo;
import shop.api.models.Product;
import shop.api.repository.PhotoRepository;
import shop.api.repository.ProductRepository;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PhotoService  extends StorageService {
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private ProductRepository productRepository;

    public PhotoService(StorageProperties storageProperties) {
        super(storageProperties);
    }

    @Override
    public void init() {

    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public void deleteAll() {

    }

    public void deletePhotoByProductId(Long idProduct){
        List<Photo> photoList = photoRepository.getPhotoByProduct(productRepository.findById(idProduct).get());
        for (Photo photo : photoList){
            File file = new File("/media/" + photo.getFileName());
            file.delete();
        }
        photoRepository.deletePhotoByProductId(idProduct);
    }

    public void savePhoto(Photo photo){
        photoRepository.save(photo);
    }

    public List<Photo> getPhotoByProduct(Product product){
        return photoRepository.getPhotoByProduct(product);
    }
}
