import React from 'react';
import { MapContainer, TileLayer, useMapEvents } from 'react-leaflet';

function LocationPicker({ onSelectAddress }) {
  useMapEvents({
    click(e) {
      const { lat, lng } = e.latlng;
      fetch(
        `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}`
      )
        .then(res => res.json())
        .then(data => {
          if (data.display_name) {
            onSelectAddress(data.display_name);
          }
        })
        .catch(console.error);
    },
  });
  return null;
}

const InlineMap = ({ onSelectAddress }) => (
  <MapContainer
    center={[42.6977, 23.3219]}
    zoom={13}
    className="inline-map"
  >
    <TileLayer
      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      attribution="© OpenStreetMap contributors"
    />
    <LocationPicker onSelectAddress={onSelectAddress} />
  </MapContainer>
);

export default InlineMap;
