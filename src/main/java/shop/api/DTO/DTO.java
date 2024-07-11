package shop.api.DTO;

import shop.api.models.Photo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTO {
    public static List<PhotoResponseDTO> convertImageToLink(List<Photo> photoList, String domain){
        List<PhotoResponseDTO> images = new ArrayList<>();
        for (Photo photo : photoList){
            images.add(new PhotoResponseDTO(domain + "/api/files/" + photo.getFileName(), photo.getType()));
        }
        return images;
    }
}
