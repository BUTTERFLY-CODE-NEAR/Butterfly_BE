package com.codenear.butterfly.admin.order.presentation;

import com.codenear.butterfly.admin.order.application.AdminOrderDetailsService;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.kakaoPay.domain.OrderDetails;
import com.codenear.butterfly.kakaoPay.domain.dto.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminOrderDetailsController {

    private final AdminOrderDetailsService adminOrderDetailsService;

    @GetMapping("/delivery-status")
    public String orderListPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            Model model
    ) {
        Page<OrderDetails> orders;

        if (status != null && !status.equals("ALL")) {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            orders = adminOrderDetailsService.getOrdersByStatus(orderStatus, page);
        } else {
            orders = adminOrderDetailsService.getAllOrders(page);
        }

        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", orders.getNumber());
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("orderStatuses", OrderStatus.values());

        return "admin/order/order-details";
    }

    @PostMapping("/delivery-status")
    public String updateOrderStatus(
            @RequestParam Long orderId,
            @RequestParam OrderStatus orderStatus,
            @RequestParam(required = false) String currentFilter
    ) {
        adminOrderDetailsService.updateOrderStatus(orderId, orderStatus);

        if (currentFilter != null && !currentFilter.equals("ALL")) {
            return "redirect:/admin/delivery-status?status=" + currentFilter;
        }
        return "redirect:/admin/delivery-status";
    }

    @PostMapping("/bulk-complete-orders")
    @ResponseBody
    public ResponseEntity<ResponseDTO> bulkCompleteOrders(@RequestBody Map<String, List<Long>> requestBody) {
        List<Long> orderIds = requestBody.get("orderIds");

        if (orderIds == null || orderIds.isEmpty()) {
            return ResponseUtil.createErrorResponse(ErrorCode.PRODUCT_NOT_SELECTED,null);
        }

        try {
            int processedCount = adminOrderDetailsService.bulkCompleteOrders(orderIds);
            String message = "총 " + processedCount + "개의 주문이 배송 완료 처리되었습니다.";
            return ResponseUtil.createSuccessResponse(message, null);
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.SERVER_ERROR, null);
        }
    }

}
