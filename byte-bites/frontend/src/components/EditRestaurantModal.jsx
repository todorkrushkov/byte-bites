import { useState, useEffect } from "react";
import "../css/EditRestaurantModal.css";
import "../css/Buttons.css";
import "../css/Inputs.css";

const EditRestaurantModal = ({ isOpen, onClose, restaurant, onUpdate }) => {
  const [form, setForm] = useState({
    name: "",
    description: "",
    address: "",
    logoImage: "",
    imageUrl: ""
  });
  const [isOpenLocal, setIsOpenLocal] = useState(false);
  const [isClosing, setIsClosing] = useState(false);

  useEffect(() => {
    if (restaurant) {
      setForm({
        name:        restaurant.name        || "",
        description: restaurant.description || "",
        address:     restaurant.address     || "",
        imageUrl:    restaurant.imageUrl    || ""
      });
    }
  }, [restaurant]);

  useEffect(() => {
    if (isOpen) {
      setIsClosing(false);
      const timer = setTimeout(() => setIsOpenLocal(true), 10);
      return () => clearTimeout(timer);
    } else if (isOpenLocal) {
      setIsOpenLocal(false);
      setIsClosing(true);
      const timer = setTimeout(() => {
        setIsClosing(false);
        onClose();
      }, 300);
      return () => clearTimeout(timer);
    }
  }, [isOpen]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async () => {
    if (!form.name || !form.description || !form.address || !form.imageUrl) {
      alert("Please fill in all fields!");
      return;
    }
    try {
      await onUpdate(restaurant.id, form);
      handleClose();
    } catch (err) {
      console.error("Editing error: ", err);
    }
  };

  const handleClose = () => {
    setIsOpenLocal(false);
    setIsClosing(true);
    setTimeout(() => {
      setIsClosing(false);
      onClose();
    }, 300);
  };

  if (!isOpen && !isOpenLocal && !isClosing) return null;

  return (
    <div
      className={`edit-modal-overlay ${isOpenLocal && !isClosing ? "open" : ""} ${isClosing ? "closing" : ""}`}
      onClick={handleClose}
    >
      <div
        className={`edit-modal ${isOpenLocal && !isClosing ? "open" : ""} ${isClosing ? "closing" : ""}`}
        onClick={(e) => e.stopPropagation()}
      >
        <button className="close-small-btn" onClick={handleClose} aria-label="Close">
          <svg className="close-small-icon" viewBox="-3 -3 30 30">
            <line x1="6" y1="6" x2="18" y2="18" />
            <line x1="18" y1="6" x2="6" y2="18" />
          </svg>
        </button>

        <h2 className="section-name">Edit restaurant</h2>

        <div className="input-layout">
          <input className="form-input"
            type="text"
            required
            name="name"
            value={form.name}
            onChange={handleChange}
          />
          <div className="label">Name</div>
        </div>

        <div className="input-layout">
          <input
            className="form-input"
            type="text"
            required
            name="description"
            value={form.description}
            onChange={handleChange}
          />
          <div className="label">Description</div>
        </div>

        <div className="input-layout">
          <input
            className="form-input"
            type="text"
            required
            name="address"
            value={form.address}
            onChange={handleChange}
          />
          <div className="label">Location</div>
        </div>

        <div className="input-layout">
          <input
            className="form-input"
            type="text"
            required
            name="imageUrl"
            value={form.imageUrl}
            onChange={handleChange}
          />
          <div className="label">Cover</div>
        </div>

        <button className="blue-btn" onClick={handleSubmit}>
          Save
        </button>
      </div>
    </div>
  );
};

export default EditRestaurantModal;