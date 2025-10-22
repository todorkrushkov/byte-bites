// components/BucketPage.jsx
import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  getCurrentUser,
  createOrder
} from '../api/api';
import InlineMap from '../components/InlineMap';
import CardPaymentModal from '../components/CardPaymentModal';
import CashPaymentModal from '../components/CashPaymentModal';
import '../css/BucketPage.css';
import "../css/Buttons.css";

const BucketPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { cart, restaurantInfo } = location.state || {
    cart: [],
    restaurantInfo: { name: '', id: null }
  };

  const [items, setItems] = useState(cart);
  const [selectedAddress, setSelectedAddress] = useState('');
  const [selectedPayment, setSelectedPayment] = useState('');
  const [paymentConfirmed, setPaymentConfirmed] = useState(false);
  const [showCardModal, setShowCardModal] = useState(false);
  const [showCashModal, setShowCashModal] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);

  useEffect(() => {
    if (!restaurantInfo.id) navigate('/');
  }, [restaurantInfo, navigate]);

  const changeQty = (id, delta) => {
    setItems(items.map(i =>
      i.id === id
        ? { ...i, quantity: Math.max(1, i.quantity + delta) }
        : i
    ));
  };

  const subtotal = items.reduce((sum, i) => sum + i.price * i.quantity, 0);
  const deliveryFee = subtotal >= 100 ? 0 : 4.99;
  const tax = 0.15;
  const total = subtotal + deliveryFee + tax;

  const handleCheckout = async () => {
    const userRes = await getCurrentUser();
    const orderData = {
      deliveryAddress: selectedAddress,
      items: items.map(i => ({ menuItemId: i.id, quantity: i.quantity })),
    };
    await createOrder(userRes.data.id, restaurantInfo.id, orderData);
    setShowSuccess(true);
  };

  return (
    <div className="bucket-container">
      <div className="bucket-form">
        <h1>Check out</h1>
        <h2>Order information</h2>

        <input
          type="text"
          placeholder="Address"
          className="bucket-input"
          value={selectedAddress}
          onChange={e => setSelectedAddress(e.target.value)}
        />

        <div className="map-wrapper">
          <InlineMap onSelectAddress={addr => setSelectedAddress(addr)} />
        </div>

        <h2>Payment method</h2>
        <div className="payment-buttons">
          <button
            className={`payment-btn ${selectedPayment === 'card' ? 'selected' : ''}`}
            onClick={() => setShowCardModal(true)}
          >
            <span className="icon-cash">💳</span> Pay with card
          </button>
          <button
            className={`payment-btn ${selectedPayment === 'cash' ? 'selected' : ''}`}
            onClick={() => setShowCashModal(true)}
          >
            <span className="icon-cash">💵</span> Pay with cash
          </button>
        </div>
      </div>

      <div className="bucket-summary">
        <h2>Your cart</h2>
        <div className="summary-items">
          {items.map(i => (
            <div key={i.id} className="summary-item">
              <img src={i.foodImage} alt={i.name} />
              <div className="summary-item-info">
                <div>{i.name}</div>
                <div>{i.quantity} × {i.price.toFixed(2)} лв</div>
              </div>
              <div className="summary-item-total">
                {(i.price * i.quantity).toFixed(2)} лв
              </div>
              <div className="summary-item-controls">
                <button onClick={() => changeQty(i.id, 1)}>＋</button>
                <button onClick={() => changeQty(i.id, -1)}>−</button>
              </div>
            </div>
          ))}
        </div>

        <div className="summary-footer">
          <div className="row"><span>Products</span><span>{subtotal.toFixed(2)} лв</span></div>
          <div className="row"><span>Delivery</span><span>{deliveryFee.toFixed(2)} лв</span></div>
          <div className="row"><span>Tax</span><span>{tax.toFixed(2)} лв</span></div>
          <div className="row total"><span>Total</span><span>{total.toFixed(2)} лв</span></div>
          <button
            className="checkout-btn blue-btn"
            disabled={!selectedAddress || !paymentConfirmed}
            onClick={handleCheckout}
          >
            {selectedPayment === 'cash' ? 'Order now' : 'Pay & Order'}
          </button>
        </div>
      </div>

      {showCardModal && (
        <CardPaymentModal
          onClose={() => setShowCardModal(false)}
          onPay={data => {
            setSelectedPayment('card');
            setPaymentConfirmed(true);
            setShowCardModal(false);
          }}
        />
      )}
      {showCashModal && (
        <CashPaymentModal
          onClose={() => setShowCashModal(false)}
          onPay={() => {
            setSelectedPayment('cash');
            setPaymentConfirmed(true);
            setShowCashModal(false);
          }}
        />
      )}

      {showSuccess && (
        <div className="success-overlay">
          <div className="success-modal">
            <h2>Success!</h2>
            <p>Your order is on its way.</p>
            <button className="blue-btn" onClick={() => navigate('/order-tracking')}>
              Track Order
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default BucketPage;
