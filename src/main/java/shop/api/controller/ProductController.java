package shop.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.api.DTO.*;
import shop.api.exception.InvalidInputException;
import shop.api.exception.NotFoundException;
import shop.api.models.*;
import shop.api.service.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

import static shop.api.service.CommonService.getResponsePagination;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "api/v1.0")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SaleService saleService;

    @Autowired
    private ValidateBinding validateBinding;

    @Autowired
    private VariantService variantService;

    @Autowired
    private ChoiceService choiceService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private VariantChoiceService variantChoiceService;

    @Value("${domain}")
    private String domain;

    @GetMapping("/ProductHasSale")
    public ResponseEntity<?> findProductHasSale() {
        List<Product> products = productService.findAll();
        List<ProductResponseDTO> productsResponse = new ArrayList<>();
        for (Product product : products) {
            boolean hasSale = false;
            List<Photo> photoList = photoService.getPhotoByProduct(product);
            ProductResponseDTO productResponse = new ProductResponseDTO(product, domain, photoList);
            List<Variant> variants = productService.findVariant(product.getId());
            List<VariantResponseDTO> variantsDTO = new ArrayList<>();
            for (Variant variant : variants) {
                VariantResponseDTO variantDTO = new VariantResponseDTO(variant);
                if (saleService.existedSale(LocalDateTime.now(), LocalDateTime.now(), variant.getId())) {
                    List<CategoryResponseDTO> categories = categoryService.findCategoryByProductId(product.getId());
                    productResponse.setCategory(categories);

                    Sale todaySale = saleService.getSaleByProductVariantIdToday(variant.getId());
                    List<SaleResponseDTO> salesResponse = new ArrayList<>();
                    salesResponse.add(new SaleResponseDTO(todaySale));
                    variantDTO.setSale(salesResponse);
                    hasSale = true;
                }
                variantsDTO.add(new VariantResponseDTO(variant));
            }
            productResponse.setVariantsDTO(variantsDTO);
            if (hasSale)
                productsResponse.add(productResponse);
        }

        Collections.sort(productsResponse, new Comparator<ProductResponseDTO>() {
            @Override
            public int compare(ProductResponseDTO productResponseDTO1, ProductResponseDTO productResponseDTO2) {
                long orderSort1 = productResponseDTO1.getOrderSort();
                long orderSort2 = productResponseDTO2.getOrderSort();

                return Long.compare(orderSort2, orderSort1);
            }
        });

        return new ResponseEntity<>(productsResponse, HttpStatus.OK);
    }

    @GetMapping("/Products")
    public ResponseEntity<?> findProduct(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "8") Integer pageSize
    ) {
        List<Product> products = productService.findAll();
        List<ProductResponseDTO> productsResponse = new ArrayList<>();
        for (Product product : products) {
            List<Photo> photoList = photoService.getPhotoByProduct(product);
            ProductResponseDTO productResponse = new ProductResponseDTO(product, domain, photoList);
            List<Variant> variants = productService.findVariant(product.getId());
            List<VariantResponseDTO> variantsDTO = new ArrayList<>();
            convertListVariantToVariantDTO(variants, variantsDTO);

            List<CategoryResponseDTO> categories = categoryService.findCategoryByProductId(product.getId());
            productResponse.setCategory(categories);
            productResponse.setVariantsDTO(variantsDTO);
            productsResponse.add(productResponse);
        }

        Collections.sort(productsResponse, new Comparator<ProductResponseDTO>() {
            @Override
            public int compare(ProductResponseDTO productResponseDTO1, ProductResponseDTO productResponseDTO2) {
                long orderSort1 = productResponseDTO1.getOrderSort();
                long orderSort2 = productResponseDTO2.getOrderSort();

                return Long.compare(orderSort2, orderSort1);
            }
        });


        return getResponsePagination(pageNo, pageSize, productsResponse);
    }

    public void convertListVariantToVariantDTO(List<Variant> variants, List<VariantResponseDTO> variantsDTO) {
        for (Variant variant : variants) {
            VariantResponseDTO variantResponse = new VariantResponseDTO(variant);

            List<Choice> savedChoices = choiceService.findChoiceByVariantId(variant.getId());
            List<ChoiceDTO> savedChoicesResponse = new ArrayList<>();
            if (savedChoices != null) {
                if (savedChoices.size() > 0) {
                    for (Choice choice : savedChoices) {
                        savedChoicesResponse.add(new ChoiceDTO(choice));
                    }
                    variantResponse.setChoices(savedChoicesResponse);
                }
            }


            List<Sale> sales = saleService.findSaleByProductVariantId(variant.getId());
            Collections.sort(sales, new Comparator<Sale>() {
                @Override
                public int compare(Sale sale1, Sale sale2) {
                    return sale1.getStartDate().compareTo(sale2.getStartDate());
                }
            });
            List<SaleResponseDTO> salesResponse = new ArrayList<>();
            for (Sale sale : sales) {
                salesResponse.add(new SaleResponseDTO(sale));
            }
            variantResponse.setSale(salesResponse);
            variantsDTO.add(variantResponse);
        }
    }

    @GetMapping("/ProductDetail/{idProduct}")
    public ResponseEntity<?> getbyId(@PathVariable("idProduct") Long idProduct) {
        Product product = productService.findById(idProduct);
        if (product == null) {
            throw new NotFoundException("Product is not exits");
        }
        List<Photo> photoList = photoService.getPhotoByProduct(product);
        ProductResponseDTO productResponse = new ProductResponseDTO(product, domain, photoList);

        List<CategoryResponseDTO> categories = categoryService.findCategoryByProductId(product.getId());
        productResponse.setCategory(categories);

        List<Variant> variants = productService.findVariant(idProduct);
        List<VariantResponseDTO> variantsResponse = new ArrayList<>();
        convertListVariantToVariantDTO(variants, variantsResponse);


        productResponse.setVariantsDTO(variantsResponse);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/ProductDetail/{idProduct}")
    public ResponseEntity<?> deleteProduct(@PathVariable("idProduct") Long idProduct) {
        Product product = productService.findById(idProduct);
        if (product == null) {
            throw new NotFoundException("Product is not exits");
        }
        productService.deleteProduct(idProduct);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/ProductByName/{value}")
    public ResponseEntity<?> getlistbyName(
            @PathVariable("value") String value,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "8") Integer pageSize
    ) {
        List<Product> products = productService.getListsByName(value);
        List<ProductResponseDTO> productsResponse = new ArrayList<>();
        for (Product product : products) {

            List<Photo> photoList = photoService.getPhotoByProduct(product);
            ProductResponseDTO productResponse = new ProductResponseDTO(product, domain, photoList);
            List<Variant> variants = productService.findVariant(product.getId());
            List<VariantResponseDTO> variantsDTO = new ArrayList<>();
            convertListVariantToVariantDTO(variants, variantsDTO);

            productResponse.setVariantsDTO(variantsDTO);
            productsResponse.add(productResponse);
        }

        Collections.sort(productsResponse, new Comparator<ProductResponseDTO>() {
            @Override
            public int compare(ProductResponseDTO productResponseDTO1, ProductResponseDTO productResponseDTO2) {
                long orderSort1 = productResponseDTO1.getOrderSort();
                long orderSort2 = productResponseDTO2.getOrderSort();

                return Long.compare(orderSort2, orderSort1);
            }
        });

        return getResponsePagination(pageNo, pageSize, productsResponse);
    }


    @Secured({"ROLE_ADMIN"})
    @PutMapping("/ProductDetail/{idProduct}")
    public ResponseEntity<?> update(@PathVariable("idProduct") Long idProduct, @ModelAttribute @Valid ProductRequestDTO productRequest, BindingResult bindingResult) {
        Product product = productService.findById(idProduct);
        if (product == null) {
            throw new NotFoundException("Product is not exits");
        }

        System.out.println("productRequest " + productRequest);
        Map<String, String> errorList = validateBinding.validate(bindingResult);
        if (errorList != null) {
            return new ResponseEntity<>(new ExceptionListResponse(errorList, "BAD_REQUEST", 400), HttpStatus.BAD_REQUEST);
        }

        if (productService.existProductByProductCode(productRequest.getProductCode(), idProduct))
            throw new InvalidInputException("Product Code is existed");

        Map<String, String> errorCategory = categoryService.checkCategory(productRequest.getCategoryId());
        if (errorCategory.size() > 0)
            return new ResponseEntity<>(errorCategory, HttpStatus.BAD_REQUEST);

        //xóa ảnh cũ khỏi db và lưu ảnh mới
        photoService.deletePhotoByProductId(idProduct);
        List<Photo> mediaList = new ArrayList<>();
        List<MultipartFile> photoList = new ArrayList<>();
        List<MultipartFile> videoList = new ArrayList<>();
        for (MultipartFile image : productRequest.getImage()) {
            if (!photoService.isImage(image) && !photoService.isVideo(image)){
                throw new InvalidInputException("There is an image that is incorrect format (only .jpg, .png, vv)");
            }else if (photoService.isVideo(image)) {
                videoList.add(image);
            } else if (photoService.isImage(image)) {
                photoList.add(image);
            }

        }

        for(MultipartFile video : videoList){
            Photo photo = new Photo(null, photoService.formatFileURL(video), product, "video");
            photoService.store(video);
            photoService.savePhoto(photo);
            mediaList.add(photo);
        }

        for(MultipartFile image : photoList){
            Photo photo = new Photo(null, photoService.formatFileURL(image), product, "image");
            photoService.store(image);
            photoService.savePhoto(photo);
            mediaList.add(photo);
        }

        //Lọc category
        List<Category> categories = categoryService.formatCategoryIdList(productRequest.getCategoryId());

        product.setProductName(productRequest.getProductName());

        product.setProductCode(productRequest.getProductCode());
        product.setDescription(productRequest.getDescription());

        List<VariantRequestDTO> variantsDTO = productRequest.getVariantsDTO();
        List<Variant> variants = new ArrayList<>();
        List<Variant> oldVariant = productService.findVariant(idProduct);
        //Save variant
        if(variantsDTO != null){
            //Lưu sản phẩm
            product = productService.saveProduct(product, categories);
            for (VariantRequestDTO variantDTO : variantsDTO) {
                Variant variant;
                variant = new Variant(variantDTO.getId(), variantDTO.getOriginPrice(), product);
                Variant savedVariant = variantService.saveVarient(variant);


                //Save choice of variant
                List<ChoiceDTO> choicesDTO = variantDTO.getChoices();
                if (choicesDTO != null) {
                    if (choicesDTO.size() > 0) {
                        List<Choice> choices = new ArrayList<>();
                        for (ChoiceDTO choiceDTO : choicesDTO) {
                            Choice choice = choiceService.getChoiceByNameAndValue(choiceDTO.getChoiceName(), choiceDTO.getChoiceValue());
                            if (choice == null) {
                                choice = new Choice(null, choiceDTO.getChoiceName(), choiceDTO.getChoiceValue());
                                Choice savedChoice = choiceService.save(choice);
                                choices.add(savedChoice);
                            } else choices.add(choice);
                        }
                        variantChoiceService.saveVariantChoice(variant, choices);
                    }
                }

                //Save sale of variant
                List<SaleRequestDTO> salesRequest = variantDTO.getSale();
                if (salesRequest != null) {
                    if (salesRequest.size() > 0) {
                        List<Sale> sales = new ArrayList<>();
                        for (SaleRequestDTO saleRequest : salesRequest) {
                            if (!saleService.existedSale(LocalDateTime.parse(saleRequest.getStartDate()),
                                    LocalDateTime.parse(saleRequest.getEndDate()), variant.getId())) {
                                Sale newSale = new Sale(null, saleRequest.getNumberSale(), LocalDateTime.parse(saleRequest.getStartDate()),
                                        LocalDateTime.parse(saleRequest.getEndDate()), variant);
                                sales.add(newSale);
                            } else {
                                Sale sale = saleService.findSaleBetweenDates(LocalDateTime.parse(saleRequest.getStartDate()),
                                        LocalDateTime.parse(saleRequest.getEndDate()), variant.getId());
                                sales.add(sale);
                            }
                        }
                        saleService.saveSale(sales, variant.getId());
                    }
                }


                variants.add(savedVariant);
            }
        }
        else {
            throw new InvalidInputException("Must have at least 1 variant");
        }
        for (Variant variant : oldVariant) {
            variantService.deleteVariant(variant);
        }

        List<VariantResponseDTO> variantsResponse = new ArrayList<>();

        ProductResponseDTO productResponse = new ProductResponseDTO(product, domain, mediaList);
        productResponse.setCategory(categoryService.findCategoryByProductId(product.getId()));
        convertListVariantToVariantDTO(variants, variantsResponse);
        productResponse.setVariantsDTO(variantsResponse);

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping("/Product")
    public ResponseEntity<?> updateProduct(@ModelAttribute @Valid ProductRequestDTO productRequest, BindingResult bindingResult) {

        Product product = new Product(null, productRequest.getProductCode(),
                productRequest.getProductName(), productRequest.getDescription(),
                LocalDateTime.now(), "ACTIVE", 0L);

        System.out.println("productRequest " + productRequest);
        Map<String, String> errorList = validateBinding.validate(bindingResult);
        if (errorList != null) {
            return new ResponseEntity<>(new ExceptionListResponse(errorList, "BAD_REQUEST", 400), HttpStatus.BAD_REQUEST);
        }

        if (productService.existProductByProductCode(productRequest.getProductCode()))
            throw new InvalidInputException("Product Code is existed");

        Map<String, String> errorCategory = categoryService.checkCategory(productRequest.getCategoryId());
        if (errorCategory.size() > 0)
            return new ResponseEntity<>(errorCategory, HttpStatus.BAD_REQUEST);


        //Lọc category
        List<Category> categories = categoryService.formatCategoryIdList(productRequest.getCategoryId());
        List<VariantRequestDTO> variantsDTO = productRequest.getVariantsDTO();
        List<Variant> variants = new ArrayList<>();
        //Save variant
        if(variantsDTO != null){
            //Lưu sản phẩm
            product = productService.saveProduct(product, categories);
            for (VariantRequestDTO variantDTO : variantsDTO) {
                Variant variant;
                variant = new Variant(variantDTO.getId(), variantDTO.getOriginPrice(), product);
                Variant savedVariant = variantService.saveVarient(variant);


                //Save choice of variant
                List<ChoiceDTO> choicesDTO = variantDTO.getChoices();
                if (choicesDTO != null) {
                    if (choicesDTO.size() > 0) {
                        List<Choice> choices = new ArrayList<>();
                        for (ChoiceDTO choiceDTO : choicesDTO) {
                            Choice choice = choiceService.getChoiceByNameAndValue(choiceDTO.getChoiceName(), choiceDTO.getChoiceValue());
                            if (choice == null) {
                                choice = new Choice(null, choiceDTO.getChoiceName(), choiceDTO.getChoiceValue());
                                Choice savedChoice = choiceService.save(choice);
                                choices.add(savedChoice);
                            } else choices.add(choice);
                        }
                        variantChoiceService.saveVariantChoice(variant, choices);
                    }
                }

                //Save sale of variant
                List<SaleRequestDTO> salesRequest = variantDTO.getSale();
                if (salesRequest != null) {
                    if (salesRequest.size() > 0) {
                        List<Sale> sales = new ArrayList<>();
                        for (SaleRequestDTO saleRequest : salesRequest) {
                            if (!saleService.existedSale(LocalDateTime.parse(saleRequest.getStartDate()),
                                    LocalDateTime.parse(saleRequest.getEndDate()), variant.getId())) {
                                Sale newSale = new Sale(null, saleRequest.getNumberSale(), LocalDateTime.parse(saleRequest.getStartDate()),
                                        LocalDateTime.parse(saleRequest.getEndDate()), variant);
                                sales.add(newSale);
                            } else {
                                Sale sale = saleService.findSaleBetweenDates(LocalDateTime.parse(saleRequest.getStartDate()),
                                        LocalDateTime.parse(saleRequest.getEndDate()), variant.getId());
                                sales.add(sale);
                            }
                        }
                        saleService.saveSale(sales, variant.getId());
                    }
                }


                variants.add(savedVariant);
            }
        }
        else {
            productService.deleteProduct(product.getId());
            throw new InvalidInputException("Must have at least 1 variant");
        }

        //lưu ảnh mới
        List<Photo> mediaList = new ArrayList<>();
        List<MultipartFile> photoList = new ArrayList<>();
        List<MultipartFile> videoList = new ArrayList<>();
        for (MultipartFile image : productRequest.getImage()) {
            if (!photoService.isImage(image) && !photoService.isVideo(image)){
                productService.deleteProduct(product.getId());
                throw new InvalidInputException("There is an image that is incorrect format (only .jpg, .png, vv)");
            }else if (photoService.isVideo(image)) {
                videoList.add(image);
            } else if (photoService.isImage(image)) {
                photoList.add(image);
            }

        }

        for(MultipartFile video : videoList){
            Photo photo = new Photo(null, photoService.formatFileURL(video), product, "video");
            photoService.store(video);
            photoService.savePhoto(photo);
            mediaList.add(photo);
        }

        for(MultipartFile image : photoList){
            Photo photo = new Photo(null, photoService.formatFileURL(image), product, "image");
            photoService.store(image);
            photoService.savePhoto(photo);
            mediaList.add(photo);
        }

        List<VariantResponseDTO> variantsResponse = new ArrayList<>();

        ProductResponseDTO productResponse = new ProductResponseDTO(product, domain, mediaList);
        productResponse.setCategory(categoryService.findCategoryByProductId(product.getId()));
        convertListVariantToVariantDTO(variants, variantsResponse);
        productResponse.setVariantsDTO(variantsResponse);

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @PatchMapping("changeProductStatus/{idProduct}")
    public ResponseEntity<?> changeProductStatus(@PathVariable("idProduct") Long idProduct) {

        return new ResponseEntity<>(productService.changeStatus(idProduct), HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @PatchMapping("changeOrderSort")
    public ResponseEntity<?> changeOrderSort(@RequestBody List<ProductSortDTO> productsRequest) {
        System.out.print("productsRequest");
        System.out.println(productsRequest);
        List<ProductResponseDTO> productsResponse = new ArrayList<>();

        for (ProductSortDTO productRequest : productsRequest) {
            Product product = productService.findById(productRequest.getProductId());
            product.setOrderSort(productRequest.getOrderSort());
            productService.saveProduct(product);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}