import React, { useEffect, useState } from "react";
import {
  getCurrentUser,
  getRestaurantsByOwner,
  deleteRestaurant,
  updateRestaurant
} from "../api/api";
import { useNavigate } from "react-router-dom";
import EditRestaurantModal from "./EditRestaurantModal";
import "../css/OwnerDashboard.css";
import AddRestaurantModal from "./AddRestaurantModal";
import OrderPopup from "../components/OrderPopup";

const OwnerDashboard = () => {
  const [user, setUser] = useState(null);
  const [restaurants, setRestaurants] = useState([]);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [selectedRestaurant, setSelectedRestaurant] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [orderPopupOpen, setOrderPopupOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserAndRestaurants = async () => {
      try {
        const res = await getCurrentUser();
        setUser(res.data);

        if (res.data.role === "OWNER") {
          const restaurantRes = await getRestaurantsByOwner(res.data.id);
          setRestaurants(restaurantRes.data);
        }
      } catch (err) {
        setError("Error loading user or restaurants.");
      } finally {
        setLoading(false);
      }
    };

    fetchUserAndRestaurants();
  }, []);

  const handleDelete = async (restaurantId) => {
    if (!window.confirm("Are you sure you want to delete this restaurant?")) return;

    try {
      await deleteRestaurant(restaurantId);
      setRestaurants(prev => prev.filter(r => r.id !== restaurantId));
      alert("The restaurant was successfully deleted!");
    } catch (err) {
      console.error("Error while deleting:", err);
      alert("An error occurred while deleting the restaurant.");
    }
  };

  const handleEdit = (restaurant) => {
    setSelectedRestaurant(restaurant);
    setEditModalOpen(true);
  };

  const handleUpdate = async (id, updatedData) => {
    try {
      const res = await updateRestaurant(id, updatedData);
      setRestaurants(prev =>
        prev.map(r => (r.id === id ? res.data : r))
      );
    } catch (err) {
      console.error("Editing error:", err);
    }
  };

  const handleOrdersClick = (restaurant, e) => {
    e.stopPropagation();
    e.preventDefault();
    setOrderPopupOpen(true);
    setSelectedRestaurant(restaurant);
  };

  if (loading) return <div className="text-center mt-10">Loading...</div>;
  if (error) return <div className="text-center text-red-500 mt-10">{error}</div>;

  

  return (
    <div className="owner-dashboard">
        <div className="owner-texts-container">
      <h1 className="owner-greeting">
        Hello mr. <span className="owner-name">{user?.username?.toUpperCase()}</span>
      </h1>
      <p className="owner-subtitle">THIS IS YOUR RESTAURANTS MANAGEMENT HOME PAGE!</p>
      </div>
      <button className="add-restaurant-btn" onClick={() => setAddModalOpen(true)}>
      <span className="add-icon-owner">＋</span>Add restaurant
        </button>
    
      {restaurants.length === 0 ? (
        <p>You have no restaurants added.</p>
      ) : (
        <div className="restaurant-grid">
          {restaurants.map((restaurant) => (
            
            <div
              key={restaurant.id}
              className="restaurant-card"
              onClick={() => navigate(`/restaurant/${restaurant.id}`)}
            >
              <div className="restaurant-overlay">
              <img
                src={restaurant.imageUrl}
                alt={restaurant.name}
                className="restaurant-image"
              />
              <div className="restaurant-info-layout">
                
              </div>
              <div className="restaurant-info">
              <p className="restaurant-address-tag">
                <svg
                  width="24px"
                  height="18px"
                  viewBox="3 0 24 24"
                  fill="none"
                  stroke="var(--wfont-color)"
                  strokeWidth="3"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                >
                  <path d="M21 10c0 6-9 13-9 13s-9-7-9-13a9 9 0 1 1 18 0z" />
                  <circle cx="12" cy="10" r="3" />
                </svg>
                {restaurant?.address}</p>
                <h3 className="restaurant-name">{restaurant.name}</h3>
                
                <div className="restaurant-actions">
                  <button className="blue-btn" onClick={(e) => { e.stopPropagation(); navigate(`/reports/${restaurant.id}`);}}>Report</button>
                  <button className="green-btn" onClick={(e) => { e.stopPropagation(); handleEdit(restaurant); }}>Edit</button>
                  <button className="orange-btn" onClick={(e) => handleOrdersClick(restaurant, e)}>Orders</button>
                  <button className="red-btn" onClick={(e) => { e.stopPropagation(); handleDelete(restaurant.id); }}>Delete</button>
                </div>
                </div>
                
                
              </div>
            </div>
          ))}
        </div>
      )}

      <EditRestaurantModal
        isOpen={editModalOpen}
        onClose={() => setEditModalOpen(false)}
        restaurant={selectedRestaurant}
        onUpdate={handleUpdate}
      />

    <AddRestaurantModal
        isOpen={addModalOpen}
        onClose={() => setAddModalOpen(false)}
        onAddSuccess={() => {
            setAddModalOpen(false);
            getRestaurantsByOwner(user.id).then(res => setRestaurants(res.data));
          }}
      />
      

      {orderPopupOpen && selectedRestaurant && (
  <OrderPopup id={selectedRestaurant.id} onClose={() => setOrderPopupOpen(false)} />
)}
    </div>
  );
};

export default OwnerDashboard;
