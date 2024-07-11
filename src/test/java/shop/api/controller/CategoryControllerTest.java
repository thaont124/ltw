package shop.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shop.api.DTO.CategoryRequestDTO;
import shop.api.DTO.CategoryResponseDTO;
import shop.api.exception.InvalidInputException;
import shop.api.exception.NotFoundException;
import shop.api.models.Category;
import shop.api.service.CategoryService;
import shop.api.service.SaleService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest {

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    //create data
    Category category1 = new Category(1L, "Category1", null);
    Category category2 = new Category(2L, "Category2", null);
    Category category11 = new Category(11L, "Category11", category1);
    Category category12 = new Category(12L, "Category12", category1);
    Category category111 = new Category(111L, "Category111", category11);



    //Category Response
    CategoryResponseDTO categoryDTO111 = new CategoryResponseDTO(category111.getId(),category111.getCategoryName(),new ArrayList<>());
    CategoryResponseDTO categoryDTO11 = new CategoryResponseDTO(category11.getId(),category11.getCategoryName(),  new ArrayList<CategoryResponseDTO>(Arrays.asList(categoryDTO111)));
    CategoryResponseDTO categoryDTO12 = new CategoryResponseDTO(category12.getId(),category12.getCategoryName(),new ArrayList<>());
    CategoryResponseDTO categoryDTO1 = new CategoryResponseDTO(category1.getId(),category1.getCategoryName(),new ArrayList<CategoryResponseDTO>(Arrays.asList(categoryDTO11, categoryDTO12)));
    CategoryResponseDTO categoryDTO2 = new CategoryResponseDTO(category2.getId(),category2.getCategoryName(),new ArrayList<>());
    List<CategoryResponseDTO> listResponse = new ArrayList<>(Arrays.asList(categoryDTO1, categoryDTO2));


    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void findList() throws Exception {

        Mockito.when(categoryService.findAllCategoryTree()).thenReturn(listResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1.0/Categories")
                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].categoryName", is("Category1")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].categoryName", is("Category11")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[1].categoryName", is("Category12")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].children[0].categoryName", is("Category111")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].children[0].children[0].children", hasSize(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].children", hasSize(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].categoryName", is("Category2")));

    }

    @Test
    void bestSellingProductByCategoryId() {
    }

    @Test
    void addCategory_success() throws Exception{
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO("Category112", 11L);


        Mockito.when(categoryService.findById(categoryRequestDTO.getParent())).thenReturn(category11);

        Category category = new Category(3L,categoryRequestDTO.getCategoryName(), category11);

        Mockito.when(categoryService.saveCategory(Mockito.any())).thenReturn(category);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/api/v1.0/Categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.categoryName", is("Category112")))
                .andExpect(jsonPath("$.parent.categoryName", is("Category11")));
    }

    @Test
    void addCategory_fail_existedCategoryName() throws Exception{
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO("Category2", 11L);

        Mockito.when(categoryService.findByCategoryName(categoryRequestDTO.getCategoryName()))
                .thenReturn((new ArrayList<>(Arrays.asList(category2))));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/api/v1.0/Categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidInputException))
                .andExpect(result -> assertEquals("Categoryname is exits", result.getResolvedException().getMessage()));
    }

    @Test
    void addCategory_fail_notExistedCategoryParent() throws Exception{
        CategoryRequestDTO categoryRequestDTO = new CategoryRequestDTO("Category113", 3L);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/api/v1.0/Categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("Category is not exits", result.getResolvedException().getMessage()));
    }

    @Test
    void getListProductByCategory(){

    }
}