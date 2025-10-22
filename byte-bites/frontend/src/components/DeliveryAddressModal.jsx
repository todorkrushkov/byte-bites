
import { useState } from "react";

const DeliveryAddressModal = ({ isOpen, onClose, onConfirm }) => {
  const [address, setAddress] = useState("");

  const handleConfirm = () => {
    if (!address.trim()) {
      alert("Моля, въведете адрес!");
      return;
    }
    onConfirm(address);
    setAddress("");
  };

  if (!isOpen) return null;

  return (
    <div className="custom-overlay">
      <div className="custom-modal">
        <button onClick={onClose} className="custom-close">✖</button>
        <h2>Въведете адрес за доставка</h2>
        <input
          type="text"
          placeholder="напр. ул. Витоша 15, София"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
        />
        <button onClick={handleConfirm} className="confirm-button">Изпрати поръчка</button>
      </div>
    </div>
  );
};

export default DeliveryAddressModal;
