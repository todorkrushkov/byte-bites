import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  getCurrentUser,
  getOrdersByCustomer
} from "../api/api";
import { FaUserAlt, FaCheck } from "react-icons/fa";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import OrderStatusIndicator from "../components/OrderStatusIndicator";
import "../css/orderTracking.css";

const OrderTrackingPage = () => {
  const [user, setUser] = useState(null);
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("active");
  const [selectedOrder, setSelectedOrder] = useState(null);

  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      try {
        const { data: me } = await getCurrentUser();
        setUser(me);
        const { data: myOrders } = await getOrdersByCustomer(me.id);
        setOrders(myOrders);
      } catch {
        setError("Не сте логнати или сесията е изтекла.");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const activeOrders    = orders.filter(o => o.status !== "DELIVERED");
  const completedOrders = orders.filter(o => o.status === "DELIVERED");

  const renderCard = (order, type) => (
    <div key={order.id} className={`history-card ${type}`}>
      <img src={order.restaurant.imageUrl} alt={order.restaurant.name} className="thumb" />
      <div className="details">
        <h3 className="name">{order.restaurant.name}</h3>
        <p className="addr">{order.restaurant.address}</p>
        <p className="price">{order.totalPrice.toFixed(2)} лв.</p>
      </div>
      <div className="status">
        {type === "waiting" ? (
          <div
            className="icon-wrap waiting"
            onClick={() => setSelectedOrder(order)}
            style={{ cursor: "pointer" }}
          >
            <FaUserAlt className="icon" />
          </div>
        ) : (
          <div className="icon-wrap finished">
            <FaCheck className="icon" />
          </div>
        )}
      </div>
    </div>
  );

  // Когато е избрана поръчка, рендерираме само статус-индикатора
  if (selectedOrder) {
    // callback, ако потребителят не е логнат
    const handleLoginRequired = () => navigate("/login");

    // callback за ъпдейт на количка (тук може да е no-op, ако не позволяваш промяна)
    const handleUpdateQuantity = (itemId, newQty) => {
      console.log("Quantity change", itemId, newQty);
      // евентуално можеш да извикаш някакъв API или просто да изключиш бутоните
    };

    return (
      <div className="order-tracking-page">
        <Navbar />
        <div className="wrapper">
          <button
            className="back-button"
            onClick={() => setSelectedOrder(null)}
          >
            ← Назад към списъка
          </button>
          <OrderStatusIndicator
            order={selectedOrder}
            user={user}
            onLoginRequired={handleLoginRequired}
            onUpdateQuantity={handleUpdateQuantity}
          />
        </div>
        <Footer />
      </div>
    );
  }

  // По подразбиране – показваме list view
  return (
    <div className="order-tracking-page">
      <Navbar />
      <div className="wrapper">
        <h1 className="page-title">DELIVERY HISTORY</h1>
        <div className="tabs">
          <button
            className={activeTab === "active" ? "tab active" : "tab"}
            onClick={() => setActiveTab("active")}
          >
            Waiting
          </button>
          <button
            className={activeTab === "history" ? "tab active" : "tab"}
            onClick={() => setActiveTab("history")}
          >
            Finished
          </button>
        </div>

        {loading && <p className="info">Зареждане...</p>}
        {error   && <p className="error">{error}</p>}

        {!loading && !error && activeTab === "active" && (
          activeOrders.length
            ? activeOrders.map(o => renderCard(o, "waiting"))
            : <p className="info">Нямате чакащи поръчки.</p>
        )}

        {!loading && !error && activeTab === "history" && (
          completedOrders.length
            ? completedOrders.map(o => renderCard(o, "finished"))
            : <p className="info">Нямате завършени поръчки.</p>
        )}
      </div>
      <Footer />
    </div>
  );
};

export default OrderTrackingPage;
