package shop.Home;


//import film.api.helper.FileSystemHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.api.Storage.StorageService;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api")
public class HomeController {

    @Autowired
    private StorageService storageService;

    public String layChuoiSauDauCham(String chuoi) {
        int viTriDauCham = chuoi.lastIndexOf('.');
        if (viTriDauCham >= 0 && viTriDauCham < chuoi.length() - 1) {
            return chuoi.substring(viTriDauCham + 1);
        }
        return "";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
//    @GetMapping(
//            value = "/get-file/{fileName}",
//            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
//    )
//    public @ResponseBody byte[] getFile(@PathVariable String fileName) throws IOException {
//        Path path = Paths.get(FileSystemHelper.STATIC_FILES_DIR, fileName);
//        File f = new File(path.toString());
//        InputStream in = new FileInputStream(f);
//        return IOUtils.toByteArray(in);
//    }


}