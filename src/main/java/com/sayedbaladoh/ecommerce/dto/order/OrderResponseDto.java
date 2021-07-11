package com.sayedbaladoh.ecommerce.dto.order;

import java.util.Date;
import java.util.List;

import com.sayedbaladoh.ecommerce.dto.orderitem.OrderItemResponseDto;
import com.sayedbaladoh.ecommerce.dto.user.UserSummary;
import com.sayedbaladoh.ecommerce.enums.OrderStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderResponseDto {

	@ApiModelProperty(readOnly = true)
	private Long id;
	private OrderStatus status;
	private double totalOrderPrice;
	private Date createdDate;
	private Date updatedDate;
	private UserSummary user;
	private int numberOfProducts;
	private List<OrderItemResponseDto> orderItems;
}
