package com.codenear.butterfly.admin.order.presentation;

import com.codenear.butterfly.admin.order.application.AdminOrderDetailsService;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminOrderDetailsController {

    private final AdminOrderDetailsService adminOrderDetailsService;

    @GetMapping("/delivery-status")
    public String orderListPage(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Page<OrderDetails> orders = adminOrderDetailsService.getAllOrders(page);

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", orders.getNumber());
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("orderStatuses", OrderStatus.values());

        return "admin/order/order-details";
    }

    @PostMapping("/delivery-status")
    public String updateOrderStatus(
            @RequestParam Long orderId,
            @RequestParam OrderStatus orderStatus
    ) {
        adminOrderDetailsService.updateOrderStatus(orderId, orderStatus);
        return "redirect:/admin/delivery-status";
    }

}
