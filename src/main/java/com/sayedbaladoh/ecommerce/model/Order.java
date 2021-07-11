package com.sayedbaladoh.ecommerce.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sayedbaladoh.ecommerce.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Valid
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	private List<OrderItem> orderItems;

	@Column(name = "session_id")
	private String sessionId;

	@ManyToOne()
	@JsonIgnore
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	@CreationTimestamp
	@Column(name = "created_date")
	private Date createdDate;

	@UpdateTimestamp
	@Column(name = "updated_date")
	private Date updatedDate;

	@Transient
	public double getTotalOrderPrice() {
		return getOrderItems().stream().mapToDouble(item -> item.getTotalPrice()).sum();
	}

	@Transient
	public int getNumberOfProducts() {
		return this.orderItems.size();
	}
}
