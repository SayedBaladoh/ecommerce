package com.sayedbaladoh.ecommerce.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.sayedbaladoh.ecommerce.dto.orderitem.OrderItemDto;
import com.sayedbaladoh.ecommerce.model.Order;
import com.sayedbaladoh.ecommerce.model.OrderItem;
import com.sayedbaladoh.ecommerce.repository.OrderItemRepository;
import com.sayedbaladoh.ecommerce.service.OrderItemService;
import com.sayedbaladoh.ecommerce.util.ObjectMapperHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderItemServiceImpl implements OrderItemService {

	private final OrderItemRepository orderItemRepository;
	private final ObjectMapperHelper objectMapperHelper;

	@Override
	public OrderItem create(OrderItem orderItem) {
		return orderItemRepository.save(orderItem);
	}

	@Override
	public List<OrderItem> addOrderItems(Order order, List<OrderItemDto> orderItemDtos) {
		List<OrderItem> orderItems = orderItemDtos.stream().map(item -> {
			OrderItem orderItem = objectMapperHelper.map(item, OrderItem.class);
			orderItem.setOrder(order);
			return orderItem;
		}).collect(Collectors.toList());
		return orderItemRepository.saveAll(orderItems);
	}

}
