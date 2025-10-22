import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  getCurrentUser,
  getReadyForPickupOrders,
  getDeliveriesByDeliverer,
  acceptDelivery,
  changeDeliveryStatus,
  logoutUser,
} from "../api/api";
import Cookies from "js-cookie";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import "../css/deliverPage.css";

const DeliverPage = () => {
  const navigate = useNavigate();
  const [available, setAvailable] = useState([]);
  const [assigned, setAssigned] = useState([]);
  const [delivererId, setDelivererId] = useState(null);
  const [userName, setUserName] = useState("");
  const [expandedOrder, setExpandedOrder] = useState(null);

  useEffect(() => {
    (async () => {
      try {
        const { data: user } = await getCurrentUser();
        setDelivererId(user.id);
        setUserName(user.username.toUpperCase());
        await loadAvailable();
        await loadAssigned(user.id);
      } catch {
        navigate("/");
      }
    })();
  }, [navigate]);

  const loadAvailable = async () => {
    const { data } = await getReadyForPickupOrders();
    setAvailable(data);
  };

  const loadAssigned = async (id) => {
    const { data } = await getDeliveriesByDeliverer(id);
    setAssigned(data);
  };

  const handleAccept = async (orderId) => {
    await acceptDelivery(orderId);
    await loadAvailable();
    await loadAssigned(delivererId);
  };

  const handleDelivered = async (deliveryId) => {
    await changeDeliveryStatus(deliveryId, "COMPLETED");
    setAssigned((prev) => prev.filter((d) => d.id !== deliveryId));
  };

  const handleCall = (phone) => {
    window.open(`tel:${phone}`);
  };

  const handleLogout = async () => {
    await logoutUser();
    Cookies.remove("jwt_token");
    navigate("/");
    window.location.reload();
  };

  const toggleExpand = (id) => {
    setExpandedOrder(prev => (prev === id ? null : id));
  };

  return (
    <div className="deliver-page">
      {}
      <header className="deliver-header">
        <Navbar />
        <div className="greeting">
          Hello <span className="name">{userName}</span>
          <div className="subtitle">This is your delivery management home page!</div>
        </div>
      </header>

      {}
      <section className="section assigned-section">
        <h2>WAITING FOR YOU</h2>
        {assigned.length === 0 ? (
          <p className="empty-message">No deliveries assigned yet.</p>
        ) : (
          assigned
          .filter(d => d.status === "ASSIGNED")
          .map(delivery => (
            <div key={delivery.id} className="delivery-card waiting">
              <img src={delivery.order.restaurant.imageUrl} alt={delivery.order.restaurant.name} />
              <div className="info">
                <h3>{delivery.order.restaurant.name}</h3>
                <span className="order-number">№{delivery.order.id}</span>
                <p>FROM: {delivery.order.restaurant.address}</p>
                <p>TO: {delivery.order.deliveryAddress}</p>
              </div>
              <div className="actions">
                <button
                  className="btn delivered"
                  onClick={() => handleDelivered(delivery.id)}
                >
                  Delivered
                </button>
                <button
                  className="btn call"
                  onClick={() => handleCall(delivery.order.customer.phoneNumber)}
                >
                  Call
                </button>
              </div>
            </div>
          ))
        )}
      </section>

      {}
      <section className="section available-section">
        <h2>AVAILABLE FOR YOU</h2>
        {available.length === 0 ? (
          <p className="empty-message">No orders ready for pickup.</p>
        ) : (
          available.map(order => {
            const isExpanded = expandedOrder === order.id;
            return (
              <div
                key={order.id}
                className={`delivery-card available ${isExpanded ? "expanded" : ""}`}
              >
                <img src={order.restaurant.imageUrl} alt={order.restaurant.name} />
                <div className="info">
                  <h3>{order.restaurant.name}</h3>
                  <span className="order-number">№{order.id}</span>
                  <p>FROM: {order.restaurant.address}</p>
                  <p>TO: {order.deliveryAddress}</p>
                </div>
                <div className="actions">
                  <button
                    className="btn accept"
                    onClick={() => handleAccept(order.id)}
                  >
                     ✅
                  </button>
                  <button
                    className="btn more-info"
                    onClick={() => toggleExpand(order.id)}
                  >
                    {isExpanded ? "Less Info" : "ℹ️"}
                  </button>
                </div>
                {isExpanded && (
                  <div className="details-panel">
                    <div><strong>DELIVERY NUMBER:</strong> {order.id}</div>
                    <div><strong>RESTAURANT NAME:</strong> {order.restaurant.name}</div>
                    <div><strong>RESTAURANT ADDRESS:</strong> {order.restaurant.address}</div>
                    <div><strong>DELIVERY ADDRESS:</strong> {order.deliveryAddress}</div>
                    <div><strong>PHONE NUMBER:</strong> {order.customer.phoneNumber}</div>
                  </div>
                )}
              </div>
            );
          })
        )}
      </section>

    <Footer/>
    </div>
  );
};

export default DeliverPage;