import Home from './pages/home';
import { Route, Routes } from "react-router-dom";
import './App.css';
import ProfilePage from './pages/profile';
import DeliverPage from './pages/deliver';
import Restaurantpage from './pages/restaurantpage';
import OrderTrackingPage from './pages/orderTracking';
import AllRestaurants from './pages/AllRestaurants';
import BucketPage from './pages/BucketPage';
import ReportPage from './pages/ReportPage'


function App() {
  return (
    <div className='routers'>
        <Routes>
          <Route
              path="/"
              element={
                <>
                  <Home />
                </>
              }
            />

          <Route
              path="/profile"
              element={
                <>
                  <ProfilePage />
                </>
              }
            />

          <Route
              path="/deliver"
              element={
                <>
                  <DeliverPage />
                </>
              }
            />
            
            <Route
            path="/restaurant/:id"
            element={
                <>
                  <Restaurantpage/>
                </>
            }
            />

            <Route
            path="/order-tracking"
            element={
                <>
                  <OrderTrackingPage/>
                </>
            }
            />
            <Route path="/restaurants" 
            element={
            <AllRestaurants />
            } 
            />
            <Route
              path="/bucket"
              element={
                <BucketPage />
              }
            />
            <Route
              path="/reports/:restaurantId"
              element={
                <ReportPage />
              }
            />
        </Routes>
    </div>
  );
}

export default App;
