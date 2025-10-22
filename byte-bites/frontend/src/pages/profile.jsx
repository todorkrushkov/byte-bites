import { useEffect, useState } from "react";
import { getCurrentUser } from "../api/api";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import LoginModal from "../components/LoginModal";
import RegisterModal from "../components/RegisterModal";
import "../css/profile.css";

const ProfilePage = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [showLogin, setShowLogin] = useState(false);
  const [showRegister, setShowRegister] = useState(false);
  const [showDeliverRegister, setShowDeliverRegister] = useState(false);
  const [showOwnerRegister, setShowOwnerRegister] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [allRestaurants, setAllRestaurants] = useState([]);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await getCurrentUser();
        setUser(res.data);
      } catch (err) {
        setError("Не сте логнати или сесията е изтекла.");
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  const handleLoginSuccess = (loggedUser) => {
    setUser(loggedUser);
    setShowLogin(false);
  };

  const getRoleIcon = () => {
    if (!user) return "";
    switch (user.role) {
      case "USER":
        return "👤";
      case "DELIVER":
        return "🚚";
      case "OWNER":
        return "👨‍💼";
      default:
        return "❓";
    }
  };

  return (
    <div className="profile-page-wrapper">
      <Navbar
        onLoginClick={() => setShowLogin(true)}
        onRegisterClick={() => setShowRegister(true)}
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        allRestaurants={allRestaurants}
      />

      <main className="page-container">
        {loading ? (
          <div className="text-center mt-10">Зареждане на профила...</div>
        ) : error ? (
          <div className="text-center text-red-500 mt-10">{error}</div>
        ) : (
          <div className="profile-container">
            <h1 className="profile-title">Profile Information</h1>

            <div className="profile-content">
              <div className="profile-icon">{getRoleIcon()}</div>

              <div className="profile-data">
                <p>
                  <strong>USERNAME:</strong> <span>{user.username}</span>
                </p>
                <p>
                  <strong>EMAIL:</strong> <span>{user.email}</span>
                </p>
                <p>
                  <strong>PHONE NUMBER:</strong>{" "}
                  <span>{user.phoneNumber}</span>
                </p>
              </div>
            </div>
          </div>
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
};

export default ProfilePage;
