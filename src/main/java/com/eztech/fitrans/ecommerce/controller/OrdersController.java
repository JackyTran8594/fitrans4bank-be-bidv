package com.eztech.fitrans.ecommerce.controller;

import com.eztech.fitrans.authentication.ResponseMessage;
import com.eztech.fitrans.ecommerce.DTO.OrderDTO;
import com.eztech.fitrans.ecommerce.entity.GridData;
import com.eztech.fitrans.ecommerce.entity.filter.OrderGridFilter;
import com.eztech.fitrans.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/orders")
public class OrdersController {
    private OrderService orderService;

    @Autowired
    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("")
    public ResponseEntity<GridData<OrderDTO>> getDataForGrid(OrderGridFilter orderGridFilter) {
        return ok(orderService.getDataForGrid(orderGridFilter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> get(@PathVariable Long id) {
        return ok(orderService.getOrderById(id));
    }

    @PostMapping("")
    public ResponseEntity<OrderDTO> create(Authentication auth, @Valid @RequestBody OrderDTO orderDTO) {
        return ok(orderService.createOrder(auth, orderDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity edit(Authentication auth, @PathVariable Long id, @Valid @RequestBody OrderDTO orderDTO) {
        if (!id.equals(orderDTO.getId())) {
            return new ResponseEntity<>(
                    "Id must be equal",
                    HttpStatus.BAD_REQUEST);
        }
        return ok(orderService.updateOrderById(id, orderDTO, auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ok(new ResponseMessage("Ok"));
    }
}
