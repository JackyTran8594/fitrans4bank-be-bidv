package com.eztech.fitrans.ecommerce.service;

import com.eztech.fitrans.authentication.BundleUserDetailsService;
import com.eztech.fitrans.authentication.exception.OrderNotFoundHttpException;
import com.eztech.fitrans.ecommerce.DTO.OrderDTO;
import com.eztech.fitrans.ecommerce.entity.GridData;
import com.eztech.fitrans.ecommerce.entity.Country;
import com.eztech.fitrans.ecommerce.entity.Order;
import com.eztech.fitrans.ecommerce.entity.builder.PageableBuilder;
import com.eztech.fitrans.ecommerce.entity.builder.OrderSpecificationBuilder;
import com.eztech.fitrans.ecommerce.entity.filter.OrderGridFilter;
import com.eztech.fitrans.ecommerce.repository.CountryRepository;
import com.eztech.fitrans.ecommerce.repository.OrderRepository;
import com.eztech.fitrans.user.User;
import com.eztech.fitrans.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private CountryRepository countryRepository;
    private ModelMapper modelMapper;
    private PageableBuilder pageableBuilder;
    private UserRepository userRepository;

    @Autowired
    OrderService(OrderRepository orderRepository,
                 UserRepository userRepository,
                 CountryRepository countryRepository,
                 PageableBuilder pageableBuilder,
                 ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.countryRepository = countryRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.pageableBuilder = pageableBuilder;
    }

    @Transactional
    public boolean delete(Long id) {
        try {
            orderRepository.delete(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            throw new OrderNotFoundHttpException("Order with id: " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    public OrderDTO getOrderById(Long id) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundHttpException("User with id: " + id + " not found", HttpStatus.NOT_FOUND)
        );

        return modelMapper.map(existingOrder, OrderDTO.class);
    }

    private List<OrderDTO> mapOrdersToOrderDTO(List<Order> orders) {
        return orders.stream().map(order ->
                modelMapper.map(order, OrderDTO.class)
        ).collect(Collectors.toList());
    }

    private GridData<OrderDTO> parsePageToGridData(Page<Order> orderPages) {
        GridData<OrderDTO> gridData = new GridData<>();
        List<Order> orderList = orderPages.getContent();
        long totalCount = orderPages.getTotalElements();
        gridData.setItems(mapOrdersToOrderDTO(orderList));
        gridData.setTotalCount(totalCount);
        return gridData;
    }

    public GridData<OrderDTO> getDataForGrid(OrderGridFilter filter) {
        OrderSpecificationBuilder specificationBuilder = new OrderSpecificationBuilder();

        Pageable paginationAndSort = pageableBuilder.build(filter);
        Optional<Specification<Order>> optionalSpec = specificationBuilder.build(filter);
        Page<Order> orderPages = optionalSpec
                .map(orderSpecification -> orderRepository.findAll(orderSpecification, paginationAndSort))
                .orElseGet(() -> orderRepository.findAll(paginationAndSort));
        return parsePageToGridData(orderPages);
    }

    @Transactional
    public OrderDTO updateOrderById(Long id, OrderDTO orderDTO, Authentication auth) {
        return update(id, orderDTO, auth);
    }

    private OrderDTO update(Long id, OrderDTO orderDTO, Authentication auth) {
        Order orderFromDB = orderRepository.findById(id).orElseThrow(
                () -> new OrderNotFoundHttpException("Order with id: " + id + " not found", HttpStatus.NOT_FOUND)
        );

        User createdUser = orderFromDB.getCreatedByUserId();

        Long userId = ((BundleUserDetailsService.BundleUserDetails) auth.getPrincipal()).getUser().getId();
        Optional<User> optionalUser = userRepository.findById(userId);
        User updatedUser = optionalUser.orElse(null);

        Order order = modelMapper.map(orderDTO, Order.class);

        Long countryId = orderFromDB.getCountry().getId();

        if (countryId == 0) {
            Country country = countryRepository.getOne(countryId);
            orderFromDB.setCountry(country);
        }

        order.setUpdatedByUserId(updatedUser);
        order.setCreatedByUserId(createdUser);
        orderRepository.save(order);

        return orderDTO;
    }

    @Transactional
    public OrderDTO createOrder(Authentication auth, OrderDTO orderDTO) {
        Long userId = ((BundleUserDetailsService.BundleUserDetails) auth.getPrincipal()).getUser().getId();

        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElse(null);

        Order order = modelMapper.map(orderDTO, Order.class);

        order.setCreatedByUserId(user);
        order.setUpdatedByUserId(user);
        orderRepository.save(order);

        return orderDTO;
    }
}
