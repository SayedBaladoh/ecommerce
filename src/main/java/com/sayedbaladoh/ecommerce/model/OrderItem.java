package com.sayedbaladoh.ecommerce.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(nullable = false)
	private Integer quantity;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", referencedColumnName = "id")
	private Order order;

	@OneToOne
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	private Product product;

	public OrderItem(Order order, Product product, @NotNull Integer quantity) {
		this.order = order;
		this.product = product;
		this.quantity = quantity;
	}

	@Transient
	public double getTotalPrice() {
		return getProduct().getPrice() * getQuantity();
	}

}
