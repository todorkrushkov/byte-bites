
import React, { useState } from 'react';

const CardPaymentModal = ({ onClose, onPay }) => {
  const [info, setInfo] = useState({ number: '', expiry: '', cvv: '' });
  const [errors, setErrors] = useState({});

  const handleChange = e => {
    const { name, value: raw } = e.target;
    let value = raw;

    if (name === 'number') {

      let digits = raw.replace(/\D/g, '');

      if (digits.length > 16) digits = digits.slice(0, 16);

      value = digits.replace(/(.{4})/g, '$1 ').trim();
    }

    if (name === 'expiry') {

      let clean = raw.replace(/\D/g, '');

      if (clean.length > 4) clean = clean.slice(0, 4);

      if (clean.length >= 3) {
        clean = clean.slice(0, 2) + '/' + clean.slice(2);
      }
      value = clean;
    }

    if (name === 'cvv') {
      
      let clean = raw.replace(/\D/g, '');
      if (clean.length > 4) clean = clean.slice(0, 4);
      value = clean;
    }

    setInfo(prev => ({ ...prev, [name]: value }));
    setErrors(prev => ({ ...prev, [name]: undefined }));
  };

  const validate = () => {
    const errs = {};
    const cleanNum = info.number.replace(/\s+/g, '');

    if (!/^\d{16}$/.test(cleanNum)) {
      errs.number = 'Номерът трябва да е 16 цифри';
    }
    if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(info.expiry)) {
      errs.expiry = 'Невалиден формат (MM/YY)';
    }
    if (!/^\d{3,4}$/.test(info.cvv)) {
      errs.cvv = 'CVV трябва да е 3 или 4 цифри';
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = () => {
    if (!validate()) return;
    onPay(info);
  };

  return (
    <div className="modal-overlay-bucket">
      <div className="modal-content-bucket">
        <button className="modal-close-bucket" onClick={onClose}>×</button>
        <h2>Pay with card</h2>

        <input
          name="number"
          type="text"
          placeholder="1234 5678 9012 3456"
          value={info.number}
          onChange={handleChange}
          className={errors.number ? 'error' : 'card-input'}
        />
        {errors.number && <div className="error-message">{errors.number}</div>}

        <input
          name="expiry"
          type="text"
          placeholder="MM/YY"
          value={info.expiry}
          onChange={handleChange}
          className={errors.expiry ? 'error' : 'card-input'}
        />
        {errors.expiry && <div className="error-message">{errors.expiry}</div>}

        <input
          name="cvv"
          type="password"
          placeholder="CVV"
          value={info.cvv}
          onChange={handleChange}
          className={errors.cvv ? 'error' : 'card-input'}
        />
        {errors.cvv && <div className="error-message">{errors.cvv}</div>}

        <button className="blue-btn" onClick={handleSubmit}>
          Submit Payment
        </button>
      </div>
    </div>
  );
};

export default CardPaymentModal;
