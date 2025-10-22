import React, { useState, useEffect } from "react";
import { getOrdersByRestaurant, markOrderAsReady, acceptOrderByOwner } from "../api/api";
import "../css/OrderPopup.css";

const OrderPopup = ({ id, onClose }) => {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!id) {
      setError("Restaurant ID is missing.");
      return;
    }

    getOrdersByRestaurant(id)
      .then(response => {
        setOrders(response.data);
      })
      .catch(error => {
        console.error("Error fetching orders", error);
        setError("Error fetching orders.");
      });
  }, [id]);

  const handleChangeStatus = (orderId, newStatus) => {
    markOrderAsReady(orderId, newStatus)
      .then(response => {
        alert(response.data);
        setOrders(orders.map(order => 
          order.id === orderId ? { ...order, status: newStatus } : order
        ));
      })
      .catch(error => {
        console.error("Error changing order status", error);
        alert("Error changing order status.");
      });
  };

  const handleAcceptOrder = (orderId) => {
    acceptOrderByOwner(orderId)
      .then(response => {
        alert(response.data);
        setOrders(orders.map(order => 
          order.id === orderId ? { ...order, status: "READY_FOR_PICKUP" } : order
        ));
      })
      .catch(error => {
        console.error("Error accepting order", error);
        alert("Error accepting order.");
      });
  };

  return (
    <div className="order-popup">
      <button className="close-btn" onClick={onClose}>X</button>
      <h2>Manage Orders</h2>

      {error && <p className="error">{error}</p>}

      <div className="order-list">
        {orders.map(order => (
          <div key={order.id} className="order-item">
            <p>Order #{order.id} - {order.status}</p>
            <div>
              {order.status === "PENDING" && (
                <button onClick={() => handleAcceptOrder(order.id)}>
                  Accept Order
                </button>
              )}
              {order.status === "READY_FOR_PICKUP" && (
                <>
                  <button onClick={() => handleChangeStatus(order.id, "CONFIRMED")}>
                    Confirm Pickup
                  </button>
                </>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default OrderPopup;
