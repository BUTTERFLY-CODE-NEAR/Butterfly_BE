package com.codenear.butterfly.admin.products.presentation;

import com.codenear.butterfly.admin.products.application.AdminProductService;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminProductsController {

    private final AdminProductService adminProductService;

    @GetMapping("/products")
    public String showAdminProducts(Model model) {
        List<Product> products = adminProductService.loadAllProducts();
        List<Category> categories = adminProductService.getCategories();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "admin/products/product-list";
    }

    @GetMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = adminProductService.findById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product updateRequest) {
        Product updatedProduct = adminProductService.updateProduct(id, updateRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<ResponseDTO> deleteProduct(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
        return ResponseUtil.createSuccessResponse(null);
    }
}
