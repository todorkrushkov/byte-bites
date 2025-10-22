// src/pages/AllRestaurants.jsx
import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import "../css/AllRestaurants.css";

import {
  getAllRestaurants,
  filterRestaurantsByCategories,
} from "../api/api";

import Navbar from "../components/Navbar";
import LoginModal from "../components/LoginModal";
import RegisterModal from "../components/RegisterModal";
import AddRestaurantModal from "../components/AddRestaurantModal";

const categories = ["PIZZA","PASTA","BURGER","SUSHI","DONER","SANDWICH"];

const AllRestaurants = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [showLogin, setShowLogin] = useState(false);
  const [showRegister, setShowRegister] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [user, setUser]= useState(null);

  const [restaurants, setRestaurants] = useState([]);
  const [error, setError]= useState("");
  const [selectedCategories, setSelectedCategories] = useState([]);

  useEffect(() => {
    const catParam = searchParams.get("category")?.toUpperCase();
    if (catParam && categories.includes(catParam)) {
      setSelectedCategories([catParam]);
      loadFiltered([catParam]);
    } else {
      loadAll();
    }
  }, [searchParams]);

  const loadAll = async () => {
    try {
      const { data } = await getAllRestaurants();
      setRestaurants(data);
      setError("");
    } catch {
      setError("Неуспешно зареждане на ресторантите.");
    }
  };

  const loadFiltered = async (cats) => {
    try {
      const { data } = await filterRestaurantsByCategories(cats);
      setRestaurants(data);
      setError("");
    } catch {
      setError("Грешка при филтриране на ресторантите.");
    }
  };

  // При чек/унчек на категория
  const toggleCategory = (cat) => {
    const next = selectedCategories.includes(cat)
      ? selectedCategories.filter(c => c !== cat)
      : [...selectedCategories, cat];

    setSelectedCategories(next);

    if (next.length === 0) {
      loadAll();
    } else {
      loadFiltered(next);
    }
  };

  return (
    <div className="all-restaurants-page">
      <Navbar
        user={user}
        setUser={setUser}
        onLoginClick={() => setShowLogin(true)}
        onRegisterClick={() => setShowRegister(true)}
        onAddRestaurant={() => setShowAddModal(true)}
        searchQuery={""}
        setSearchQuery={()=>{}}
        allRestaurants={restaurants}
      />

      <div className="main-content-all-rest">
        <aside className="filter-sidebar">
          <h2>Filters</h2>
          <ul>
            {categories.map(cat => (
              <li key={cat}>
                <label className="filter-option">
                  <input
                    type="checkbox"
                    value={cat}
                    checked={selectedCategories.includes(cat)}
                    onChange={() => toggleCategory(cat)}
                  />
                  <span className="checkmark"></span>
                  {cat.charAt(0) + cat.slice(1).toLowerCase()}
                </label>
              </li>
            ))}
          </ul>
        </aside>

        <section className="restaurants-section-all-rest">
          <h2 className="restaurants-title-all-rest">All restaurants</h2>
          {error && <p className="text-red-500">{error}</p>}
          <div className="restaurants-grid-all-rest">
            {restaurants.map(r => (
              <div
                key={r.id}
                className="restaurant-card-all-rest"
                onClick={() => navigate(`/restaurant/${r.id}`)}
              >
                <img
                  src={r.imageUrl}
                  alt={r.name}
                  className="restaurant-banner-all-rest"
                />
                <h3 className="restaurant-name-all-rest">{r.name}</h3>
              </div>
            ))}
          </div>
        </section>
      </div>

      {showLogin && (
        <LoginModal
          close={() => setShowLogin(false)}
          openRegister={() => {
            setShowLogin(false);
            setShowRegister(true);
          }}
          onLoginSuccess={() => window.location.reload()}
        />
      )}
      {showRegister && (
        <RegisterModal
          close={() => setShowRegister(false)}
          openLogin={() => {
            setShowRegister(false);
            setShowLogin(true);
          }}
          role="USER"
        />
      )}
      {user?.role === "OWNER" && (
        <AddRestaurantModal
          isOpen={showAddModal}
          close={() => setShowAddModal(false)}
          onAddSuccess={() => window.location.reload()}
        />
      )}
    </div>
  );
};

export default AllRestaurants;
