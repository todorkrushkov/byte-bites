import { useEffect, useState } from "react";
import "../css/AddItemModal.css";
import {
  getMenuByRestaurant,
  updateMenuItem,
  addMenuItem,
  deleteMenuItem
} from '../api/api';

const AddItemModal = ({ isOpen, close, restaurantId, reloadMenu }) => {
  const [menuItems, setMenuItems] = useState([]);
  const [newItem, setNewItem] = useState({
    name: "",
    price: "",
    category: "PIZZA",
    foodImage: ""
  });
  const [editingItemId, setEditingItemId] = useState(null);
  const [filter, setFilter] = useState("");

  const categories = ["PIZZA","PASTA","SANDWICH","SUSHI","DONER","BURGER"];

  useEffect(() => {
    if (isOpen) loadMenuItems();
  }, [isOpen]);

  const loadMenuItems = async () => {
    try {
      const res = await getMenuByRestaurant(restaurantId);
      setMenuItems(res.data);
    } catch (err) {
      console.error("Грешка при зареждане на менюто:", err);
    }
  };

  const handleAddOrUpdate = async () => {
    if (!newItem.name || !newItem.price || !newItem.category || !newItem.foodImage) {
      alert("Моля, попълнете всички полета.");
      return;
    }
    try {
      if (editingItemId) {
        await updateMenuItem(editingItemId, newItem);
      } else {
        await addMenuItem(restaurantId, newItem);
      }
      resetForm();
      loadMenuItems();
      reloadMenu?.();
    } catch (err) {
      console.error(err);
    }
  };

  const resetForm = () => {
    setNewItem({ name:"", price:"", category:"PIZZA", foodImage:"" });
    setEditingItemId(null);
  };

  const handleDelete = async (id) => {
    await deleteMenuItem(id);
    loadMenuItems();
    reloadMenu?.();
  };

  const handleEdit = (item) => {
    setNewItem({
      name: item.name,
      price: item.price,
      category: item.category,
      foodImage: item.foodImage
    });
    setEditingItemId(item.id);
  };

  if (!isOpen) return null;

  const list = filter
    ? menuItems.filter(i => i.category === filter)
    : menuItems;

  return (
    <div className="custom-overlay">
      <div className="custom-modal add-item-modal">
      <button className="close-small-btn" onClick={close} aria-label="Close">
          <svg className="close-small-icon" viewBox="-3 -3 30 30">
            <line x1="6" y1="6" x2="18" y2="18" />
            <line x1="18" y1="6" x2="6" y2="18" />
          </svg>
        </button>
        
        <div className="custom-content">

          <div className="add-form">
            <h2>Add a product</h2>
            <div className="input-layout">
            <input
                className="form-input"
                required
                type="text"
                value={newItem.name}
                onChange={e => setNewItem({...newItem, name: e.target.value})}
              />
              <div className="label">Product name</div>
            </div>
              
            <div className="input-layout">
              <input
                className="form-input"
                required
                type="number"
                value={newItem.price}
                onChange={e => setNewItem({...newItem, price: parseFloat(e.target.value)})}
              />
              <div className="label">Product price</div>
            </div>
            
            <div className="input-layout">
              <input
                className="form-input"
                required
                type="text"
                value={newItem.foodImage}
                onChange={e => setNewItem({...newItem, foodImage: e.target.value})}
              />
              <div className="label">Product image</div>
            </div>
            <div className="input-layout">
              <select
                className="select-input"
                value={newItem.category}
                onChange={e => setNewItem({...newItem, category: e.target.value})}
              >
                {categories.map(cat => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>
            <button className="blue-btn" onClick={handleAddOrUpdate}>
              {editingItemId ? "Save changes" : "Add product"}
            </button>
          </div>


          <div className="edit-menu">
            <h2>Edit menu</h2>
            <select
              className="category-filter"
              value={filter}
              onChange={e => setFilter(e.target.value)}
            >
              <option value="">All types</option>
              {categories.map(cat => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
            </select>

            <div className="menu-list">
              {list.map(item => (
                <div key={item.id} className="menu-card">
                  <img
                    src={item.foodImage}
                    alt={item.name}
                    className="menu-card-img"
                    onError={e => (e.currentTarget.src = "")}
                  />
                  <div className="menu-card-info">
                    <h3>{item.name}</h3>
                    <p>{item.price.toFixed(2)} лв.</p>
                  </div>
                  <div className="menu-card-actions">
                    <button className="group_buttons" id="edit-btn" onClick={() => handleEdit(item)}>Edit</button>
                    <button className="group_buttons" id="delete-btn"onClick={() => handleDelete(item.id)}>Delete</button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddItemModal;
