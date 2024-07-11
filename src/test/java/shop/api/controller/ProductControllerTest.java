//package shop.api.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.HandlerResultMatchers;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.multipart.MultipartFile;
//import shop.api.DTO.CategoryResponseDTO;
//import shop.api.DTO.ProductRequestDTO;
//import shop.api.DTO.ProductResponseDTO;
//import shop.api.DTO.SaleDTO;
//import shop.api.exception.InvalidInputException;
//import shop.api.exception.NotFoundException;
//import shop.api.models.Category;
//import shop.api.models.CategoryProduct;
//import shop.api.models.Product;
//import shop.api.models.Sale;
//import shop.api.service.CategoryService;
//import shop.api.service.ProductService;
//import shop.api.service.SaleService;
//import shop.api.service.ValidateBinding;
//
//import java.io.InputStream;
//import java.lang.reflect.Field;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.notNullValue;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//class ProductControllerTest {
//
//    private MockMvc mockMvc;
//
//    ObjectMapper objectMapper = new ObjectMapper();
//    ObjectWriter objectWriter = objectMapper.writer();
//
//    @Mock
//    private ValidateBinding validateBinding;
//
//    @Mock
//    private ProductService productService;
//
//    @Mock
//    private CategoryService categoryService;
//
//    @Mock
//    private SaleService saleService;
//
//    @InjectMocks
//    private ProductController productController;
//
//    private String domain ;
//
//    Product product1 = new Product(1L, "PROD-001", "Sample Product1", 100.0f, "sample-image.jpg", "This is a sample product1 description.", LocalDate.parse("2022-08-23"), "Active");
//    Product product2 = new Product(2L, "PROD-002", "Sample Product2", 100.0f, "sample-image.jpg", "This is a sample product2 description.", LocalDate.parse("2021-08-23"), "Active");
//
//
//    Sale sale1 = new Sale(1L, 40L, LocalDate.parse("2023-06-23"), LocalDate.parse("2024-08-23"), product1);
//    Sale sale2 = new Sale(2L, 60L, LocalDate.parse("2023-08-23"), LocalDate.parse("2023-10-23"), product1);
//
//
//    Category category1 = new Category(1L, "Category1", null);
//    Category category2 = new Category(2L, "Category2", null);
//    Category category3 = new Category(3L, "Category3", null);
//    Category category11 = new Category(11L, "Category11", category1);
//    Category category12 = new Category(12L, "Category12", category1);
//    Category category111 = new Category(111L, "Category111", category11);
//
//
//    CategoryProduct category12_product1 = new CategoryProduct(1L,category12, product1);
//    CategoryProduct category2_product2 = new CategoryProduct(2L,category2, product2);
//    CategoryProduct category2_product1 = new CategoryProduct(3L,category2, product1);
//
//
//    @BeforeEach
//    public void setUp(){
//        MockitoAnnotations.initMocks(this);
//        this.mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
//        domain = "http://26.30.1.50:8080";
//    }
//
//
//    @Test
//    void getbyId_success_product1_hasSale() throws Exception {
//        // Lấy trường 'domain' của ProductController
//        Field domainField = ProductController.class.getDeclaredField("domain");
//
//        // Bỏ qua việc kiểm tra quyền truy cập trường (nếu trường là private)
//        domainField.setAccessible(true);
//
//        // Đặt giá trị mới cho biến domain
//        domainField.set(productController, "http://26.30.1.50:8080");
//
//
//
//        Mockito.when(productService.findById(1L)).thenReturn(product1);
//        Product product = productService.findById(1L);
//        ProductResponseDTO productDTO = new ProductResponseDTO(product, domain);
//
//        CategoryResponseDTO cate12 = new CategoryResponseDTO(category12.getId(), category12.getCategoryName(), new ArrayList<>());
//        CategoryResponseDTO cate1 = new CategoryResponseDTO(category1.getId(), category1.getCategoryName(), new ArrayList<>(Arrays.asList(cate12)));
//        CategoryResponseDTO cate2 = new CategoryResponseDTO(category2.getId(), category2.getCategoryName(), new ArrayList<>());
//        Mockito.when(categoryService.findCategoryByProductId(product.getId())).thenReturn(new ArrayList<>(Arrays.asList(cate1, cate2)));
//
//        Mockito.when(saleService.existedSale(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.eq(1L))).thenReturn(true);
//        Mockito.when(saleService.getSaleByProductVariantId(1L)).thenReturn(sale1);
//        Sale sale = saleService.getSaleByProductVariantId(1L);
//        SaleDTO saleDTO = new SaleDTO(sale);
//        productDTO.setSale(saleDTO);
//
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/api/v1.0/ProductDetail/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                ).andExpect(status().isOk())
//                .andExpect(jsonPath("$", notNullValue()))
//                .andExpect(jsonPath("$.productCode", is("PROD-001")))
//                .andExpect(jsonPath("$.productName", is("Sample Product1")))
//                .andExpect(jsonPath("$.image", is("http://26.30.1.50:8080/api/files/sample-image.jpg")))
//                .andExpect(jsonPath("$.category[0].children[0].categoryName", is("Category12")))
//                .andExpect(jsonPath("$.sale.id", is(1)));
//
////        ResultActions resultActions2 = mockMvc.perform(MockMvcRequestBuilders
////                .get("/api/v1.0/ProductDetail/1")
////                .contentType(MediaType.APPLICATION_JSON)
////        ).andExpect(status().isOk());
////        System.out.println("Response JSON: " + resultActions2.andReturn().getResponse().getContentAsString());
////        resultActions2.andDo(MockMvcResultHandlers.print());
//
//    }
//
//    @Test
//    void getbyId_success_product2_noSale() throws Exception {
//        // Lấy trường 'domain' của ProductController
//        Field domainField = ProductController.class.getDeclaredField("domain");
//
//        // Bỏ qua việc kiểm tra quyền truy cập trường (nếu trường là private)
//        domainField.setAccessible(true);
//
//        // Đặt giá trị mới cho biến domain
//        domainField.set(productController, "http://26.30.1.50:8080");
//        Mockito.when(productService.findById(2L)).thenReturn(product2);
//
//        LocalDate today = LocalDate.parse("2023-07-23");
//
//        Mockito.when(saleService.existedSale(today, today, 2L)).thenReturn(false);
//
//        ProductResponseDTO productResponse = new ProductResponseDTO(product2, domain);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/api/v1.0/ProductDetail/2")
//                        .contentType(MediaType.APPLICATION_JSON)
//                ).andExpect(status().isOk())
//                .andExpect(jsonPath("$",notNullValue()))
//                .andExpect(jsonPath("$.productCode", is("PROD-002")))
//                .andExpect(jsonPath("$.productName", is("Sample Product2")))
//                .andExpect(jsonPath("$.image", is("http://26.30.1.50:8080/api/files/sample-image.jpg")))
//                .andExpect(jsonPath("$.sale").doesNotExist());
//
//    }
//
//    @Test
//
//    void getbyId_fail_noExistedProduct() throws Exception {
//        Mockito.when(productService.findById(3L)).thenReturn(null);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/api/v1.0/ProductDetail/3")
//                        .contentType(MediaType.APPLICATION_JSON)
//                ).andExpect(status().isBadRequest())
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidInputException))
//                .andExpect(result -> assertEquals("Product is not exits", result.getResolvedException().getMessage()));;
//
//    }
//
////    @Test
////    void update_success() throws Exception{
////
////        List<Long> categoryIds = Arrays.asList(1L, 111L, 3L);
////
////        // Tạo đối tượng MultipartFile từ tệp tin ảnh thực tế
////        InputStream is = productController.getClass().getClassLoader().getResourceAsStream("DSCF6867.jpg");
////        MockMultipartFile multipartFile = new MockMultipartFile("file", "DSCF6867.jpg", "multipart/form-data", is);
////
////        //Tạo đối tượng request
////        ProductRequestDTO productRequest = new ProductRequestDTO(1L, "Sản phẩm 1", "PD1-001", 40F,
////                multipartFile, "Product description", categoryIds, "active");
////
////        //check input
////        BindingResult bindingResult = mock(BindingResult.class);
////
////        Mockito.when(validateBinding.validate(bindingResult)).thenReturn(new HashMap<>());
////        Mockito.when(productService.existProductByProductCode(productRequest.getProductCode(), 1L)).thenReturn(false);
////        Mockito.when(productService.checkImage(multipartFile)).thenReturn(true);
////
////        //tìm sp
////        Mockito.when(productService.findById(1L)).thenReturn(product1);
////
////
////        //sửa sp
////        product1.setProductName(productRequest.getProductName());
////        product1.setProductCode(productRequest.getProductCode());
////        product1.setImage(multipartFile.getOriginalFilename());
////
////        Mockito.when(productService.formatFileURL(productRequest.getImage())).thenReturn("DSCF6867.jpg");
////        product1.setDescription(productRequest.getDescription());
////
////        product1.setOriginPrice(productRequest.getOriginPrice());
////
////        //lọc category
////        Mockito.when(categoryService.formatCategoryIdList(categoryIds)).thenReturn(new ArrayList<>(Arrays.asList(category111, category3)));
////        List<Category> categories = new ArrayList<>(Arrays.asList(category111, category3));
////
////        //lưu thay đổi
////        Mockito.when(productService.saveProduct(product1,categories, multipartFile)).thenReturn(product1);
////
////        //chuyển qua DTO
////        ProductResponseDTO productResponse = new ProductResponseDTO(product1, domain);
////
////        Mockito.when(saleService.existedSale(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.eq(1L))).thenReturn(true);
////        Mockito.when(saleService.getSaleByProductId(1L)).thenReturn(sale1);
////
////        productResponse.setSale(new SaleDTO(sale1));
////
////
////        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
////                .put("/api/v1.0/ProductDetail/1")
////                .contentType(MediaType.MULTIPART_FORM_DATA)
////                .accept(MediaType.APPLICATION_JSON)
////                .content(objectWriter.writeValueAsString(productResponse));
////
////        mockMvc.perform(mockRequest)
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$", notNullValue()))
////                .andExpect(jsonPath("$.productName",is("Sản phẩm 1")))
////                .andExpect(jsonPath("$.productCode", is("PD1-001")))
////                .andExpect(jsonPath("$.image", is("http://26.30.1.50:8080/api/files/image.jpa")))
////                .andExpect(jsonPath("$.sale.id", is(1)));
////    }
//
//    @Test
//    void deleteProduct() {
//    }
//
//    @Test
//    void getlistbyName() {
//    }
//
//    @Test
//    void addProduct() {
//    }
//}