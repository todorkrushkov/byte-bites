import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
    getRestaurantById,
  getRestaurantRevenueByPeriod,
  getDelivererRevenueForPeriod,
} from "../api/api";
import "../css/ReportPage.css";

const ReportPage = () => {
  const { restaurantId } = useParams();
  const navigate         = useNavigate();

  const [periodRev, setPeriodRev]         = useState(null);
  const [restaurantName, setRestaurantName] = useState("");
  const [delivererRevs, setDelivererRevs] = useState([]);
  const [start, setStart]                 = useState("");
  const [end, setEnd]                     = useState("");
  const [error, setError]                 = useState("");


  useEffect(() => {
    getRestaurantById(restaurantId)
      .then(({ data }) => setRestaurantName(data.name))
      .catch(() => setRestaurantName(`Restaurant #${restaurantId}`));
  }, [restaurantId]);
  
  const fetchPeriod = async () => {
    if (!start || !end) {
      setError("Моля, въведете начална и крайна дата.");
      return;
    }


    const startISO = new Date(start).toISOString();
    const endISO   = new Date(end).toISOString();

    try {
      // 1) period revenue
      const { data: pr } = await getRestaurantRevenueByPeriod(
        restaurantId,
        startISO,
        endISO
      );
      //  read totalRevenue, not total
      const total = pr.totalRevenue;
      setPeriodRev(typeof total === "number" ? total : Number(total));

      // 2) deliverer incomes
      const { data: dr } = await getDelivererRevenueForPeriod(
        restaurantId,
        startISO,
        endISO
      );
      setDelivererRevs(dr);

      setError("");
    } catch (e) {
      console.error(e);
      setError("Грешка при зареждане на периодичните данни.");
    }
  };

  return (
    <div className="report-page">
      <button className="back-btn" onClick={() => navigate(-1)}>
        ← Back
      </button>

      <h1>Reports for {restaurantName}</h1>
      {error && <div className="error">{error}</div>}

      <section className="section">
        <h2>Revenue by Period</h2>
        <div className="filters">
          <label>
            From:{" "}
            <input
              type="datetime-local"
              value={start}
              onChange={(e) => setStart(e.target.value)}
            />
          </label>
          <label style={{ marginLeft: 12 }}>
            To:{" "}
            <input
              type="datetime-local"
              value={end}
              onChange={(e) => setEnd(e.target.value)}
            />
          </label>
          <button style={{ marginLeft: 12 }} onClick={fetchPeriod}>
            Fetch
          </button>
        </div>
        {periodRev != null && !isNaN(periodRev) ? (
          <div className="stat">{periodRev.toFixed(2)} лв</div>
        ) : null}
      </section>

      <section className="section">
        <h2>Deliverer Incomes</h2>
        {delivererRevs.length > 0 ? (
          <table className="report-table">
            <thead>
              <tr>
                <th>Deliverer</th>
                <th>Total Income</th>
                <th>Bonus?</th>
              </tr>
            </thead>
            <tbody>
              {delivererRevs.map((d) => (
                <tr key={d.delivererId}>
                  <td>{d.delivererName}</td>
                  <td>{Number(d.totalIncome).toFixed(2)} лв</td>
                  <td>{d.bonusAwarded ? "✓" : "–"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>Няма данни. Изберете период по-горе.</p>
        )}
      </section>
    </div>
  );
};

export default ReportPage;
