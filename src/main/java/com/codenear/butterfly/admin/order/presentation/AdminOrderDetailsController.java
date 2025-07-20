package com.codenear.butterfly.admin.order.presentation;

import com.codenear.butterfly.admin.order.application.AdminOrderDetailsService;
import com.codenear.butterfly.admin.order.dto.BulkCompleteDTO;
import com.codenear.butterfly.global.dto.ResponseDTO;
import com.codenear.butterfly.global.exception.ErrorCode;
import com.codenear.butterfly.global.util.ResponseUtil;
import com.codenear.butterfly.payment.domain.OrderDetails;
import com.codenear.butterfly.payment.domain.dto.OrderStatus;
import com.codenear.butterfly.payment.domain.dto.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminOrderDetailsController {

    private final AdminOrderDetailsService adminOrderDetailsService;

    @GetMapping("/delivery-status")
    public String orderListPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, value = "orderType") String type,
            Model model
    ) {
        Page<OrderDetails> orders;
        OrderType orderType = type != null && !type.isEmpty() ? OrderType.valueOf(type) : null;

        if (status != null && !status.isEmpty()) {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            orders = adminOrderDetailsService.getOrdersByStatus(orderStatus, page, orderType);
        } else {
            orders = adminOrderDetailsService.getAllOrders(page, orderType);
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

    @PatchMapping("/orders/status")
    @ResponseBody
    public ResponseEntity<ResponseDTO> bulkCompleteOrders(@RequestBody BulkCompleteDTO bulkCompleteDTO) {
        List<Long> orderIds = bulkCompleteDTO.orderIds();
        if (orderIds == null || orderIds.isEmpty()) {
            return ResponseUtil.createErrorResponse(ErrorCode.PRODUCT_NOT_SELECTED, null);
        }

        try {
            int processedCount = adminOrderDetailsService.bulkChangeOrderStatus(orderIds, bulkCompleteDTO.status());
            String message = String.format("총 %s개의 주문이 %s로 변경되었습니다.", processedCount, bulkCompleteDTO.status());
            return ResponseUtil.createSuccessResponse(message, null);
        } catch (Exception e) {
            return ResponseUtil.createErrorResponse(ErrorCode.SERVER_ERROR, null);
        }
    }

}
