import React from 'react';
import '../css/OrderStatusIndicator.css';
import { FaUserCheck } from "react-icons/fa";
import OrderSummary from '../components/OrderSummary';

const OrderStatusIndicator = ({
  order,
  user,
  onLoginRequired,
  onUpdateQuantity
}) => {
  const { status, cart = [], restaurant } = order;

  const statuses = [
    { key: 'PENDING',    label: 'Проверява се' },
    { key: 'CONFIRMED',  label: 'Приготвя се' },
    { key: 'ON_THE_WAY', label: 'На път' },
    { key: 'DELIVERED',  label: 'Завършено' }
  ];

  const statusMapping = {
    'ASSIGNED':    'CONFIRMED',
    'IN_PROGRESS': 'ON_THE_WAY',
    'COMPLETED':   'DELIVERED'
  };

  const getStatusIndex = (cur) => {
    const mapped = statusMapping[cur] || cur;
    return statuses.findIndex(s => s.key === mapped);
  };

  const currentStep = getStatusIndex(status);

  return (
    <div className='container-order-tracking'>

      <div className="order-status-wrapper">
        <div className="status-avatar">
          <FaUserCheck className="status-avatar-icon" />
        </div>

        <div className="status-steps">
          {statuses.map((s, idx) => (
            <div
              key={s.key}
              className={`step ${idx <= currentStep ? 'active' : ''}`}
            >
              <div className="step-circle">{idx + 1}</div>
              <div className="step-label">{s.label}</div>
            </div>
          ))}
        </div>

        <div className="status-progress-bar">
          <div
            className="progress-fill"
            style={{ width: `${((currentStep + 1) / statuses.length) * 100}%` }}
          />
        </div>
      </div>
    </div>
  );
};

export default OrderStatusIndicator;
