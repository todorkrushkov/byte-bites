import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import LoginModal from "../components/LoginModal";
import RegisterModal from "../components/RegisterModal";
import OwnerDashboard from "../components/OwnerDashboard";
import "../css/Main.css";
import "../css/home.css";
import "../css/Inputs.css";

import burgerDecor from "../images/burger-1.png";
import sushiBoard from "../images/sushi-1.png";
import pizzaIcon from "../images/pizza-1.png";
import burgerIcon from "../images/burger-1-2.png";
import sandwichIcon from "../images/sandwich-1.png";
import pastaIcon from "../images/pasta-1.png";
import donerIcon from "../images/doner-1.png";
import sushiIcon from "../images/sushi-2.png";
import handshake from "../images/handshake.jpg";
import rider from "../images/rider.jpg";

import {
  FaMapMarkerAlt,
  FaHamburger,
  FaTruck
} from "react-icons/fa";

import { getCurrentUser, getAllRestaurants } from "../api/api";

function Home() {
  const navigate = useNavigate();
  const [showLogin, setShowLogin] = useState(false);
  const [showRegister, setShowRegister] = useState(false);
  const [showDeliverRegister, setShowDeliverRegister] = useState(false);
  const [showOwnerRegister, setShowOwnerRegister] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [user, setUser] = useState(null);
  const [allRestaurants, setAllRestaurants] = useState([]);

  const foodTypes = [
    { icon: pizzaIcon, label: "PIZZA" },
    { icon: burgerIcon, label: "BURGER" },
    { icon: sandwichIcon, label: "SANDWICH" },
    { icon: pastaIcon, label: "PASTA" },
    { icon: donerIcon, label: "DONER" },
    { icon: sushiIcon, label: "SUSHI" }
  ];

  useEffect(() => {
    getCurrentUser()
      .then((res) => setUser(res.data))
      .catch(() => setUser(null));

    getAllRestaurants()
      .then((res) => setAllRestaurants(res.data))
      .catch((err) => console.error("Error loading restaurants", err));
  }, []);

  useEffect(() => {
    if (user?.role === "DELIVER") {
      navigate("/deliver");
    }
  }, [user, navigate]);

  const handleLoginSuccess = (loggedUser) => {
    setUser(loggedUser);
    setShowLogin(false);
  };

  const handleFoodTypeClick = (category) => {
    navigate(`/restaurants?category=${category}`);
  };

  return (
    <div className="home-container">
      <Navbar
        onLoginClick={() => setShowLogin(true)}
        onRegisterClick={() => setShowRegister(true)}
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        allRestaurants={allRestaurants}
      />

        {user?.role !== "OWNER" && (
          <>
            <img src={sushiBoard} alt="" className="background-img sushi-board" />
            <img src={burgerDecor} alt="" className="background-img burger" />
          </>
        )}
      <main className="page-container">
              {user?.role === "OWNER" ? (
          <OwnerDashboard />
        ) : (
          <>
            <section className="hero-section">
          <h1 className="hero-title">
            {user ? `Welcome back, ${user.username}` : "Welcome to ByteBites"}
          </h1>
          <p className="hero-subtitle">
            {user
              ? "ByteBites wishes you a pleasant experience and bon appétit."
              : "ByteBites is a delicious service offering a unique experience that helps you satisfy your hunger."}
          </p>
          <button className="blue-btn" onClick={() => navigate("/restaurants")}>
            SEARCH ALL RESTAURANTS
          </button>
        </section>

        <div className="content-container">
          <section className="food-types">
            <h1 className="section-title">TYPES OF FOOD</h1>
            <div className="food-grid">
              {foodTypes.map((food, index) => (
                <div
                  key={index}
                  className="food-item"
                  onClick={() => handleFoodTypeClick(food.label)}
                  style={{ cursor: "pointer" }}
                >
                  <img src={food.icon} alt={food.label} className="food-icon" />
                  <span className="food-label">{food.label}</span>
                </div>
              ))}
            </div>
          </section>

          <section className="how-to-order">
            <h2 className="section-title">HOW TO ORDER</h2>
            <div className="order-steps">
              <div className="order-step">
                <div className="step-icon"><FaMapMarkerAlt /></div>
                <p1>Share your location</p1>
              </div>
              <div className="order-step">
                <div className="step-icon"><FaHamburger /></div>
                <p1>Choose your food</p1>
              </div>
              <div className="order-step">
                <div className="step-icon"><FaTruck /></div>
                <p1>Order and track</p1>
              </div>
            </div>
          </section>

          <section className="work-with-us" id="work-with-us">
            <h3 className="section-title">WANT TO WORK WITH US?</h3>
            <div className="opportunities">
              <div className="opportunity-card">
                <div className="opportunity-image">
                  <img src={handshake} alt="Restaurant interior" />
                </div>
                <p className="opportunity-text">
                  ByteBites provides an opportunity for any restaurant-related business to expand its operations in the online space.
                </p>
                <button className="white-btn" onClick={() => setShowOwnerRegister(true)}>
                  GROW YOUR BUSINESS
                </button>
              </div>

              <div className="opportunity-card">
                <div className="opportunity-image">
                  <img src={rider} alt="Delivery person on bicycle" />
                </div>
                <p className="opportunity-text">
                  Do you like to get around the city by car, motorbike or bicycle? What's better than getting paid for it?
                </p>
                <button className="white-btn" onClick={() => setShowDeliverRegister(true)}>
                  BECOME A DELIVER
                </button>
              </div>
            </div>
          </section>
        </div>
          </>
        )}
        
      </main>

      <Footer />

      {showLogin && (
        <LoginModal
          close={() => setShowLogin(false)}
          openRegister={() => {
            setShowLogin(false);
            setShowRegister(true);
          }}
          onLoginSuccess={handleLoginSuccess}
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

      {showDeliverRegister && (
        <RegisterModal
          close={() => setShowDeliverRegister(false)}
          openLogin={() => {
            setShowDeliverRegister(false);
            setShowLogin(true);
          }}
          role="DELIVER"
        />
      )}

      {showOwnerRegister && (
        <RegisterModal
          close={() => setShowOwnerRegister(false)}
          openLogin={() => {
            setShowOwnerRegister(false);
            setShowLogin(true);
          }}
          role="OWNER"
        />
      )}
    </div>
  );
}

export default Home;
