import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import '../css/OrderSummary.css';

const OrderSummary = ({ cart, onUpdateQuantity, restaurantInfo, user, onLoginRequired }) => {
  const { restaurantId } = useParams();
  const navigate = useNavigate();
  
  const calculateSubtotal = () => {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
  };
  
  const subtotal = calculateSubtotal();
  const deliveryFee = subtotal >= 100 ? 0 : 4.99;
  const serviceFee = 0.15;
  const total = subtotal + deliveryFee + serviceFee;

  const handleQuantityChange = (itemId, newQuantity) => {
    onUpdateQuantity(itemId, Math.max(0, newQuantity));
  };

  const handleCheckout = () => {
    if (!user) {
      onLoginRequired();
      return;
    }

    if (cart.length === 0) {
      alert("Количката е празна. Моля, добавете продукти преди да продължите.");
      return;
    }

    navigate('/bucket', {
      state: {
        cart,
        restaurantInfo
      }
    });
  };

  return (
    <div className="order-summary">
      <h3 className="order-title">Your cart</h3>

      <div className="order-items-restaurant">
        {cart.map((item) => (
          <div key={item.id} className="cart-item">
            <img
              src={item.foodImage}
              alt={item.name}
              className="cart-item-img"
            />
            <div className="cart-item-details">
              <span className="cart-item-name">{item.name}</span>
              <span className="cart-item-quantity-price">
                {item.quantity} x {item.price.toFixed(2)} лв. {(item.price * item.quantity).toFixed(2)} лв.
              </span>
            </div>
            <hr />
            <div className="cart-quantity-buttons">
              <button className="circle-btn small" onClick={() => handleQuantityChange(item.id, item.quantity + 1)}>+</button>
              <button className="circle-btn small" onClick={() => handleQuantityChange(item.id, item.quantity - 1)}>−</button>
            </div>
          </div>
        ))}
      </div>

      <div className="order-summary-breakdown">
        <div className="summary-row">
          <span>Products</span>
          <span>{subtotal.toFixed(2)} лв.</span>
        </div>
        <div className="summary-row">
          <span>Delivery</span>
          <span>{deliveryFee.toFixed(2)} лв.</span>
        </div>
        <div className="summary-row">
          <span>Service Fee</span>
          <span>{serviceFee.toFixed(2)} лв.</span>
        </div>
        <div className="summary-row total">
          <strong>Summary:</strong>
          <strong>{total.toFixed(2)} лв.</strong>
        </div>
      </div>

      <div className='chechout-btn-container'>
        <button className="blue-btn" onClick={handleCheckout}>
          Check out
        </button>
      </div>
    </div>
  );
};

OrderSummary.propTypes = {
  cart: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      name: PropTypes.string.isRequired,
      price: PropTypes.number.isRequired,
      quantity: PropTypes.number.isRequired,
    })
  ).isRequired,
  onUpdateQuantity: PropTypes.func.isRequired,
  restaurantInfo: PropTypes.shape({
    name: PropTypes.string.isRequired,
    address: PropTypes.string.isRequired,
  }).isRequired,
  user: PropTypes.object,
  onLoginRequired: PropTypes.func.isRequired,
};

export default OrderSummary;
