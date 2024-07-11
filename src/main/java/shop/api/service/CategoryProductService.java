package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.api.repository.CategoryProductRepository;

@Service
public class CategoryProductService {
    @Autowired
    private CategoryProductRepository categoryProductRepository;
}
