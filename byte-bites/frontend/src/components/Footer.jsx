import React from "react";
import {
  FaFacebookF,
  FaInstagram,
  FaYoutube
} from "react-icons/fa";

import "../css/Main.css";
import "../css/Footer.css";
import "../css/Inputs.css";

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-section">
          <h3>USEFUL INFORMATION</h3>
        </div>
        <div className="contact-number">
          <a href="tel:+359 87 969 6969">📞 +359 87 969 6969</a>
        </div>

        <div className="social-links">
          <a href="#" className="social-link"><FaFacebookF /></a>
          <a href="#" className="social-link"><FaInstagram /></a>
          <a href="#" className="social-link"><FaYoutube /></a>
        </div>

        <div className="footer-btn">
          <ul>
            <li>
              <a href="#">About us</a>|
              <a href="#">Terms & Conditions</a>|
              <a href="#">Privacy policy</a>
            </li>
          </ul>
        </div>

        <div className="copyright">
          © 2025 ByteBites. All Rights Reserved
        </div>
      </div>
    </footer>
  );
};

export default Footer;
