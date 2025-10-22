import { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import AddItemModal from "../components/AddItemModal";
import DeliveryAddressModal from "../components/DeliveryAddressModal";
import OrderSummary from "../components/OrderSummary";
import "../css/RestaurantPage.css";
import Navbar from "../components/Navbar";
import RegisterModal from "../components/RegisterModal";
import EditRestaurantModal from "../components/EditRestaurantModal";
import {
  getCurrentUser,
  getRestaurantById,
  getMenuByRestaurant,
  deleteRestaurant,
  createOrder,
  updateRestaurant,
} from "../api/api";
import LoginModal from "../components/LoginModal";
import Footer from "../components/Footer";
import image from "../images/pizza-1.png";
import OrderPopup from "../components/OrderPopup";

const RestaurantPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [restaurant, setRestaurant] = useState(null);
  const [menuItems, setMenuItems] = useState([]);
  const [orderItems, setOrderItems] = useState([]);
  const [isOwner, setIsOwner] = useState(false);

  const [showLogin, setShowLogin] = useState(false);
  const [showRegister, setShowRegister] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isAddressModalOpen, setIsAddressModalOpen] = useState(false);
  const [editRestaurantModalOpen, setEditRestaurantModalOpen] = useState(false);
  const [restaurantToEdit, setRestaurantToEdit] = useState(null);
  const [orderPopupOpen, setOrderPopupOpen] = useState(false);
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);

  const [showEditDropdown, setShowEditDropdown] = useState(false);
  const [isClosingEdit, setIsClosingEdit] = useState(false);
  const editDropdownRef = useRef(null);
  const editToggleBtnRef = useRef(null);

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    let currentUser = null;
    try {
      const userRes = await getCurrentUser();
      currentUser = userRes.data;
      setUser(currentUser);
    } catch {
      setUser(null);
    }

    try {
      const restaurantRes = await getRestaurantById(id);
      const restaurantData = restaurantRes.data;
      setRestaurant(restaurantData);

      if (
        currentUser?.role === "OWNER" &&
        restaurantData?.owner?.id === currentUser.id
      ) {
        setIsOwner(true);
      }

      loadMenuItems();
    } catch (err) {
      console.error("Грешка при зареждане:", err);
    }
  };

  const loadMenuItems = async () => {
    try {
      const res = await getMenuByRestaurant(id);
      setMenuItems(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.error("Неуспешно зареждане на менюто:", err);
    }
  };

  const updateQuantity = (itemId, newQuantity) => {
    if (newQuantity === 0) {
      setOrderItems(orderItems.filter(item => item.id !== itemId));
    } else {
      setOrderItems(orderItems.map(item =>
        item.id === itemId ? { ...item, quantity: newQuantity } : item
      ));
    }
  };

  const addToOrder = (item) => {
    const existingItem = orderItems.find(i => i.id === item.id);
    if (existingItem) updateQuantity(item.id, existingItem.quantity + 1);
    else setOrderItems([...orderItems, { ...item, quantity: 1 }]);
  };

  const handleConfirmOrder = (deliveryAddress) => {
    createOrder(user.id, id, orderItems)
      .then(() => {
        alert("Поръчката е създадена успешно!");
        setOrderItems([]);
        setIsAddressModalOpen(false);
      })
      .catch(() => alert("Неуспешна поръчка."));
  };

  const handleEditToggle = () => {
    if (showEditDropdown) {
      setIsClosingEdit(true);
      setTimeout(() => {
        setShowEditDropdown(false);
        setIsClosingEdit(false);
      }, 300);
    } else {
      setShowEditDropdown(true);
    }
  };

  const handleEditMenu = () => {
    setIsModalOpen(true);
    setShowEditDropdown(false);
  };

  const handleEditRestaurant = () => {
    setRestaurantToEdit(restaurant);
    setEditRestaurantModalOpen(true);
    setShowEditDropdown(false);
  };

  const handleUpdateRestaurant = async (id, updatedData) => {
    try {
      const res = await updateRestaurant(id, updatedData);
      setRestaurant(res.data);
      setEditRestaurantModalOpen(false);
      loadInitialData();
    } catch {
      alert("Неуспешна редакция.");
    }
  };

  const handleOrdersClick = (restaurant, e) => {
    e.stopPropagation();
    setOrderPopupOpen(true);
    setSelectedRestaurant(restaurant);
  };

  const handleDelete = async (restaurantId) => {
    if (!window.confirm("Сигурни ли сте?")) return;
    try {
      await deleteRestaurant(restaurantId);
      alert("Ресторантът беше изтрит.");
      navigate("/restaurants");
    } catch {
      alert("Грешка при изтриване.");
    }
  };

  const groupedMenu = menuItems.reduce((acc, item) => {
    if (!acc[item.category]) acc[item.category] = [];
    acc[item.category].push(item);
    return acc;
  }, {});

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (
        showEditDropdown &&
        editDropdownRef.current &&
        !editDropdownRef.current.contains(e.target) &&
        editToggleBtnRef.current &&
        !editToggleBtnRef.current.contains(e.target)
      ) {
        setIsClosingEdit(true);
        setTimeout(() => {
          setShowEditDropdown(false);
          setIsClosingEdit(false);
        }, 300);
      }
    };
    window.addEventListener("mousedown", handleClickOutside);
    return () => window.removeEventListener("mousedown", handleClickOutside);
  }, [showEditDropdown]);

  return (
    <div className="restaurant-container">
      <Navbar onLoginClick={() => setShowLogin(true)} onRegisterClick={() => setShowRegister(true)} />

      <div className="restaurant-banner-section">
        <button className="back-button" onClick={() => navigate(-1)}>←</button>
        <img src={restaurant?.imageUrl} alt={restaurant?.name} className="restaurant-banner" />
        <div className="restaurant-banner-overlay">
          <h1 className="restaurant-title">{restaurant?.name}</h1>
          <p className="restaurant-subtitle">{restaurant?.description}</p>
          <p className="restaurant-address-tag">{restaurant?.address}</p>
        </div>
      </div>

      <div className="restaurant-content-wrapper">
        <div className="restaurant-menu-section">
          {isOwner && (
            <div className="restaurant-actions" style={{ position: "relative" }}>
              <button ref={editToggleBtnRef} onClick={handleEditToggle} className="green-btn">
                Edit
                {(showEditDropdown || isClosingEdit) && (
                  <div
                    ref={editDropdownRef}
                    className={`dropdown-menu ${isClosingEdit ? "closing" : ""}`}
                    style={{
                      position: "absolute",
                      top: "calc(100% + 0.5rem)",
                      zIndex: 10,
                    }}
                  >
                    <button onClick={handleEditMenu}>Menu</button>
                    <button onClick={handleEditRestaurant}>Restaurant</button>
                  </div>
                )}
              </button>

              <button className="orange-btn" onClick={(e) => handleOrdersClick(restaurant, e)}>
                Orders
              </button>
              <button className="red-btn" onClick={() => handleDelete(restaurant.id)}>
                Delete
              </button>
            </div>
          )}

          {Object.keys(groupedMenu).map((category) => (
            <div key={category} className="menu-category-section">
              <h2 className="menu-category-title">{category}</h2>
              <div className="menu-grid">
                {groupedMenu[category].map((item) => (
                  <div key={item.id} className="menu-item" onClick={() => addToOrder(item)}>
                    <div className="image-container">
                      <img
                        src={item.foodImage || image}
                        alt={item.name}
                        className="menu-item-image"
                        onError={(e) => (e.currentTarget.src = image)}
                      />
                    </div>
                    <div className="menu-item-text">
                      <h3 className="menu-item-name">{item.name}</h3>
                      <p className="menu-item-price">{item.price.toFixed(2)} лв.</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>

        <div className="restaurant-cart-section">
          <OrderSummary
            cart={orderItems}
            onUpdateQuantity={updateQuantity}
            restaurantInfo={{
              name: restaurant?.name,
              address: restaurant?.address,
              id: restaurant?.id,
            }}
            user={user}
            onLoginRequired={() => setShowLoginModal(true)}
          />
        </div>
      </div>

      <Footer />

      {showLogin && <LoginModal close={() => setShowLogin(false)} onLoginSuccess={() => window.location.reload()} openRegister={() => { setShowLogin(false); setShowRegister(true); }} />}
      {showRegister && <RegisterModal close={() => setShowRegister(false)} openLogin={() => { setShowRegister(false); setShowLogin(true); }} role="USER" />}
      {showLoginModal && <LoginModal close={() => setShowLoginModal(false)} onLoginSuccess={() => window.location.reload()} />}
      {isOwner && <AddItemModal isOpen={isModalOpen} close={() => setIsModalOpen(false)} restaurantId={id} reloadMenu={loadMenuItems} />}
      {isOwner && (
        <EditRestaurantModal
          isOpen={editRestaurantModalOpen}
          onClose={() => setEditRestaurantModalOpen(false)}
          restaurant={restaurantToEdit}
          onUpdate={handleUpdateRestaurant}
        />
      )}
      <DeliveryAddressModal isOpen={isAddressModalOpen} onClose={() => setIsAddressModalOpen(false)} onConfirm={handleConfirmOrder} />
      {orderPopupOpen && selectedRestaurant && (
        <OrderPopup id={selectedRestaurant.id} onClose={() => setOrderPopupOpen(false)} />
      )}
    </div>
  );
};

export default RestaurantPage;
