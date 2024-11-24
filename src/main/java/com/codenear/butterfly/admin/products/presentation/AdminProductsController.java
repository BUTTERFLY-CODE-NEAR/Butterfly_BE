package com.codenear.butterfly.admin.products.presentation;

import com.codenear.butterfly.admin.products.application.AdminProductService;
import com.codenear.butterfly.product.domain.Category;
import com.codenear.butterfly.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductsController {

    private final AdminProductService adminProductService;

    @GetMapping
    public String showProductList(Model model) {
        List<Product> products = adminProductService.loadAllProducts();
        List<Category> categories = adminProductService.getCategories();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "admin/products/product-list";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = adminProductService.findById(id);
        List<Category> categories = adminProductService.getCategories();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        return "admin/products/product-edit";
    }

    @PutMapping("/{id}/edit")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product updateRequest,
            RedirectAttributes redirectAttributes) {
        try {
            adminProductService.updateProduct(id, updateRequest);
            redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 수정되었습니다.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "상품 수정에 실패했습니다: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/products";
    }

    @DeleteMapping("/{id}/delete")
    public String deleteProduct(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            adminProductService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 삭제되었습니다.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "상품 삭제에 실패했습니다: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/products";
    }
}