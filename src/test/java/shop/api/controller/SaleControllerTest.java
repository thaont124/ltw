//package shop.api.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import org.junit.Before;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import shop.api.DTO.SaleResponseDTO;
//import shop.api.models.Product;
//import shop.api.models.Sale;
//import shop.api.service.SaleService;
//
//import java.time.LocalDate;
//
//@RunWith(MockitoJUnitRunner.class)
//class SaleControllerTest {
//    private MockMvc mockMvc;
//
//    ObjectMapper objectMapper = new ObjectMapper();
//    ObjectWriter objectWriter = objectMapper.writer();
//
//    @Mock
//    private SaleService saleService;
//
//    @InjectMocks
//    private SaleController saleController;
//
//    //create data
//    Product product1 = new Product(1L, "PROD-001", "Sample Product", 100.0f, "sample-image.jpg", "This is a sample product description.", LocalDate.now(), "Active");
//    Product product2 = new Product(2L, "PROD-002", "Sample Product", 100.0f, "sample-image.jpg", "This is a sample product description.", LocalDate.now(), "Active");
//
//    Sale sale1 = new Sale(1L, 40L, LocalDate.parse("2023-06-23"), LocalDate.parse("2023-07-23"), product1);
//    Sale sale2 = new Sale(2L, 60L, LocalDate.parse("2023-07-23"), LocalDate.parse("2023-08-23"), product1);
//
//
//    @Before
//    public void setUp(){
//        MockitoAnnotations.initMocks(this);
//        this.mockMvc = MockMvcBuilders.standaloneSetup(saleController).build();
//
//    }
//
//
//    SaleResponseDTO saleResponseDTO = new SaleResponseDTO();
//
//
//}