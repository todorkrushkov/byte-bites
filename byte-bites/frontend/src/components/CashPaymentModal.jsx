import React from 'react';
import '../css/PopupBucketPage.css';

const CashPaymentModal = ({ onClose, onPay }) => (
  <div className="modal-overlay-bucket">
    <div className="modal-content-bucket">
      <button className="modal-close-bucket" onClick={onClose}>×</button>
      <h2>Cash on Delivery</h2>
      <p>You will pay in cash when your order arrives.</p>
      <button className="blue-btn" onClick={onPay}>Confirm</button>
    </div>
  </div>
);

export default CashPaymentModal;