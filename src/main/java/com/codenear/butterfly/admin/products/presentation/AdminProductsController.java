package com.codenear.butterfly.admin.products.presentation;

import com.codenear.butterfly.admin.products.application.AdminProductService;
import com.codenear.butterfly.admin.products.dto.ProductCreateRequest;
import com.codenear.butterfly.admin.products.dto.ProductEditResponse;
import com.codenear.butterfly.admin.products.dto.ProductUpdateRequest;
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

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        List<Category> categories = adminProductService.getCategories();
        model.addAttribute("categories", categories);
        return "admin/products/product-create";
    }

    @PostMapping("/new")
    public String createProduct(@ModelAttribute ProductCreateRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            adminProductService.createProduct(request);
            redirectAttributes.addFlashAttribute("message", "상품이 성공적으로 생성되었습니다.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "상품 생성에 실패했습니다: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/products";
    }

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
        ProductEditResponse response = adminProductService.getProductEditInfo(id);
        model.addAttribute("product", response.product());
        model.addAttribute("keywordString", response.keywordString());
        model.addAttribute("categories", adminProductService.getCategories());
        return "admin/products/product-edit";
    }

    @PostMapping("/{id}/edit")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductUpdateRequest updateRequest,
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

    @PostMapping("/{id}/delete")
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