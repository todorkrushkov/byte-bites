import { useState, useEffect } from "react";
import "../css/Buttons.css";
import "../css/Addrestaurant.css";
import { addRestaurant } from '../api/api';

const AddRestaurantModal = ({ isOpen, onClose, onAddSuccess }) => {
  const [restaurant, setRestaurant] = useState({
    name: "",
    description: "",
    address: "",
    imageUrl: ""
  });

  const [isOpenLocal, setIsOpenLocal] = useState(false);
  const [isClosing, setIsClosing] = useState(false);

  useEffect(() => {
    let timer;
  
    if (isOpen) {
      setIsClosing(false);
      timer = setTimeout(() => setIsOpenLocal(true), 10);
    } else if (isOpenLocal) {
      setIsClosing(true);
      timer = setTimeout(() => {
        setIsClosing(false);
        setIsOpenLocal(false);
      }, 300);
    }
  
    return () => clearTimeout(timer);
  }, [isOpen, isOpenLocal]);
  

  const handleChange = (e) => {
    setRestaurant({ ...restaurant, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    const { name, description, address, imageUrl } = restaurant;
    if (!name || !description || !address || !imageUrl) {
      alert("Please fill in all fields.");
      return;
    }

    try {
      await addRestaurant(restaurant);
      onAddSuccess();
      handleClose();
    } catch (err) {
      console.error("Error adding restaurant:", err);
    }
  };

  const handleClose = () => {
    onClose();
  };

  if (!isOpen && !isOpenLocal && !isClosing) return null;

  return (
    <div
      className={`add-restaurant-overlay ${isOpenLocal && !isClosing ? 'open' : ''} ${isClosing ? 'closing' : ''}`}
      onClick={handleClose}
    >
      <div
        className={`add-restaurant-modal ${isOpenLocal && !isClosing ? 'open' : ''} ${isClosing ? 'closing' : ''}`}
        onClick={(e) => e.stopPropagation()}
      >
        <button className="close-small-btn" onClick={handleClose} aria-label="Close">
          <svg className="close-small-icon" viewBox="-3 -3 30 30">
            <line x1="6" y1="6" x2="18" y2="18" />
            <line x1="18" y1="6" x2="6" y2="18" />
          </svg>
        </button>
        <h2 className="section-name">Add restaurant</h2>
        <div className="input-layout">
          <input 
            className="form-input"
            required
            type="text"
            name="name"
            value={restaurant.name}
            onChange={handleChange}
          />
          <div className="label">Name</div>
        </div>
        <div className="input-layout">
          <input
            className="form-input"
            required
            type="text"
            name="description"
            value={restaurant.description}
            onChange={handleChange}
          />
          <div className="label">Description</div>
        </div>
        <div className="input-layout">
          <input
            className="form-input"
            required
            type="text"
            name="address"
            value={restaurant.address}
            onChange={handleChange}
          />
          <div className="label">Address</div>
        </div>
        <div className="input-layout">
          <input
            className="form-input"
            required
            type="text"
            name="imageUrl"
            value={restaurant.imageUrl}
            onChange={handleChange}
          />
          <div className="label">Cover</div>
        </div>
        <button className="blue-btn" onClick={handleSubmit}>
          Add restaurant
        </button>
      </div>
    </div>
  );
};

export default AddRestaurantModal;
