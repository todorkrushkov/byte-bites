import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";
import LoginModal from "./LoginModal";
import RegisterModal from "./RegisterModal";
import logo from "../images/ByteBitesLogoHorizontal.png";
import "../css/Navbar.css";
import "../css/home.css";
import "../css/Inputs.css";
import { getCurrentUser, logoutUser } from "../api/api";
import AddRestaurantModal from "./AddRestaurantModal";
import { useLocation } from "react-router-dom";

const Navbar = ({ searchQuery, setSearchQuery, allRestaurants }) => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [showLogin, setShowLogin] = useState(false);
  const [showRegister, setShowRegister] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showDropDownMenu, setDropDownMenu] = useState(false);
  const [isSearchClosing, setIsSearchClosing] = useState(false);
  const searchContainerRef = useRef(null);
  const dropdownRef = useRef(null);
  const toggleBtnRef = useRef(null);
  const searchDropdownRef = useRef(null);
  const [isClosing, setIsClosing] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const location = useLocation();
  const isHomePage = location.pathname === "/";
  const isAllRestaurantsPage = location.pathname === "/restaurants";

  useEffect(() => {
    getCurrentUser()
      .then((res) => setUser(res.data))
      .catch(() => setUser(null));
  }, []);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(e.target) &&
        toggleBtnRef.current &&
        !toggleBtnRef.current.contains(e.target)
      ) {
        setIsClosing(true);
        setTimeout(() => {
          setDropDownMenu(false);
          setIsClosing(false);
        }, 300);
      }

      if (
        searchContainerRef.current &&
        !searchContainerRef.current.contains(e.target) &&
        searchResults.length > 0
      ) {
        setIsSearchClosing(true);
        setTimeout(() => {
          setSearchResults([]);
          setIsSearchClosing(false);
        }, 200);
      }
    };

    window.addEventListener("mousedown", handleClickOutside);
    return () => {
      window.removeEventListener("mousedown", handleClickOutside);
    };
  }, [searchResults]);

  const handleLogout = async () => {
    try {
      await logoutUser();
      setUser(null);
      setDropDownMenu(false);
      window.location.reload();
    } catch (err) {
      console.error("Logout failed:", err);
    }
  };

  const handleLogoClick = () => {
    if (window.location.pathname === "/") {
      window.scrollTo({ top: 0, behavior: "smooth" });
    } else {
      navigate("/");
    }
  };

  const handleSearchChange = (e) => {
    if (!allRestaurants) return;

    const value = e.target.value;
    setSearchTerm(value);

    if (value.trim() === "") {
      setIsSearchClosing(true);
      setTimeout(() => {
        setSearchResults([]);
        setIsSearchClosing(false);
      }, 200);

      if (isAllRestaurantsPage) {
        setSearchQuery("");
      }
      return;
    }

    const filtered = allRestaurants.filter((r) =>
      r.name.toLowerCase().includes(value.toLowerCase())
    );

    if (isHomePage) {
      setSearchResults(filtered);
    } else if (isAllRestaurantsPage) {
      setSearchQuery(value);
    }
  };

  return (
    <>
      <header className="header">
        <div className="header-content">
          <img
            src={logo}
            alt="ByteBites Logo"
            className="logo"
            onClick={handleLogoClick}
            style={{ cursor: "pointer" }}
          />

          <div className="search-container" ref={searchContainerRef}>
            <div className="input-layout">
              <svg
                className="input-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="3"
                strokeLinecap="round"
                strokeLinejoin="round"
                xmlns="http://www.w3.org/2000/svg"
              >
                <circle cx="11" cy="11" r="8" />
                <line x1="21" y1="21" x2="16.65" y2="16.65" />
              </svg>

              <input
                type="text"
                required
                value={searchTerm}
                onChange={handleSearchChange}
                onFocus={() => {
                  if (isHomePage && searchTerm.trim() !== "") {
                    const filtered = allRestaurants.filter((r) =>
                      r.name.toLowerCase().includes(searchTerm.toLowerCase())
                    );
                    setSearchResults(filtered);
                  }
                }}
                className="form-input"
              />
              <div className="label">Search</div>
            </div>

            {isHomePage && (searchResults.length > 0 || isSearchClosing) && (
              <div
                className={`search-dropdown ${
                  isSearchClosing ? "closing" : ""
                }`}
                ref={searchDropdownRef}
              >
                <ul className="dropdown-menu">
                  {searchResults.map((restaurant) => (
                    <li
                      key={restaurant.id}
                      onClick={() => {
                        navigate(`/restaurant/${restaurant.id}`);
                        setSearchResults([]);
                        setSearchTerm("");
                      }}
                      className="search-result"
                    >
                      {restaurant.name}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>

          <div className="dropdown-menu-container">
            {(showDropDownMenu || isClosing) && (
              <div
                className={`dropdown-menu ${isClosing ? "closing" : ""}`}
                ref={dropdownRef}
              >
                {user ? (
                  <>
                    <button
                      onClick={() => {
                        setDropDownMenu(false);
                        navigate("/profile");
                      }}
                    >
                      View Profile
                    </button>
                    {user.role === "USER" && (
                    <button
                      onClick={() => {
                        setDropDownMenu(false);
                        navigate("/order-tracking");
                      }}
                    >
                      Order Status
                    </button>
                    )}
                    {user.role === "OWNER" && (
                      <button
                        onClick={() => {
                          setDropDownMenu(false);
                          setShowAddModal(true);
                        }}
                      >
                        Add Restaurant
                      </button>
                    )}
                    <button onClick={handleLogout}>Log out</button>
                  </>
                ) : (
                  <>
                    <button
                      onClick={() => {
                        setShowLogin(true);
                        setDropDownMenu(false);
                      }}
                    >
                      Log in
                    </button>
                    <button
                      onClick={() => {
                        setShowRegister(true);
                        setDropDownMenu(false);
                      }}
                    >
                      Create Account
                    </button>
                  </>
                )}
              </div>
            )}

            <button
              className="circle-btn"
              onClick={() => {
                if (showDropDownMenu) {
                  setIsClosing(true);
                  setTimeout(() => {
                    setDropDownMenu(false);
                    setIsClosing(false);
                  }, 300);
                } else {
                  setDropDownMenu(true);
                }
              }}
              ref={toggleBtnRef}
            >
              {user ? (
                <FaUserCircle className="circle-icon account-icon" size={18} />
              ) : (
                <svg
                  className="circle-icon"
                  viewBox="0 0 100 100"
                  fill="currentColor"
                >
                  <rect x="20" y="25" width="60" height="10" rx="5"></rect>
                  <rect x="20" y="45" width="60" height="10" rx="5"></rect>
                  <rect x="20" y="65" width="60" height="10" rx="5"></rect>
                </svg>
              )}
            </button>

            <button className="circle-btn">
              <svg
                className="circle-icon"
                viewBox="0 1 20 20"
                xmlns="http://www.w3.org/2000/svg"
              >
                <g
                  fill="none"
                  fillRule="evenodd"
                  stroke="currentColor"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  transform="translate(2 3)"
                >
                  <path d="m8 16c4.4380025 0 8-3.5262833 8-7.96428571 0-4.43800246-3.5619975-8.03571429-8-8.03571429-4.43800245 0-8 3.59771183-8 8.03571429 0 4.43800241 3.56199755 7.96428571 8 7.96428571z" />
                  <path d="m1 5h14" />
                  <path d="m1 11h14" />
                  <path d="m8 16c2.2190012 0 4-3.5262833 4-7.96428571 0-4.43800246-1.7809988-8.03571429-4-8.03571429-2.21900123 0-4 3.59771183-4 8.03571429 0 4.43800241 1.78099877 7.96428571 4 7.96428571z" />
                </g>
              </svg>
            </button>
          </div>
        </div>
      </header>

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

      <AddRestaurantModal
        isOpen={showAddModal}
        close={() => setShowAddModal(false)}
        onAddSuccess={() => window.location.reload()}
      />
    </>
  );
};

export default Navbar;
