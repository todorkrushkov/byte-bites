import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
    withCredentials: true,
  });

  const api_Not_login = axios.create({
    baseURL: "http://localhost:8080",
  });

// --- RESTAURANTS ---
export const getAllRestaurants = () => api_Not_login.get("/restaurants/all");
export const getRestaurantById = (id) => api_Not_login.get(`/restaurants/${id}`);
export const addRestaurant = (data) => api.post("/restaurants/add", data);
export const updateRestaurant = (id, data) => api.put(`/restaurants/update/${id}`, data);
export const deleteRestaurant = (id) => api.delete(`/restaurants/delete/${id}`);
export const getRestaurantsByOwner = (ownerId) => api.get(`/restaurants/owner/${ownerId}`);
export const filterRestaurantsByCategories = (categories) =>
  api_Not_login.post("/restaurants/filter", categories);

// --- AUTH ---
export const registerUser = (data) => api.post("/auth/register", data);
export const loginUser = (data) => api.post("/auth/login", data);
export const logoutUser = () => api.post("/auth/logout");
export const getCurrentUser = () => api.get("/auth/logged/user");

// --- MENU ---
export const getMenuByRestaurant = (restaurantId) =>
  api_Not_login.get(`/menu/restaurant/${restaurantId}`);
  export const addMenuItem = (restaurantId, data) =>
    api.post(`/menu/add/restaurant/${restaurantId}`, data);
  export const updateMenuItem = (itemId, data) =>
    api.put(`/menu/item/${itemId}`, data);
  export const deleteMenuItem = (itemId) => api.delete(`/menu/item/${itemId}`);

  // --- ORDERS ---
export const createOrder = (customerId, restaurantId, data) =>
    api.post(`/orders/create/customer/${customerId}/restaurant/${restaurantId}`, data);
  export const getOrderStatus = (orderId) => api.get(`/orders/status/${orderId}`);
  export const getOrdersByUser = (userId) => api.get(`/orders/user/${userId}`);
  export const getOrdersByDeliverer = (delivererId) => api.get(`/orders/deliverer/${delivererId}`);
  export const updateOrderStatus = (orderId, status) =>
    api.put(`/orders/status/${orderId}`, { status });
  export const getOrdersByCustomer = (customerId) =>
    api.get(`/orders/customer/${customerId}`);
  
  export const getOrdersByRestaurant = (restaurantId) => api.get(`/orders/restaurant/${restaurantId}/orders`);

  
  // --- Deliveries ---
  export const getAvailableDeliveries = () => api.get("/deliveries/available");
  export const getDeliveriesByDeliverer = (delivererId) => api.get(`/deliveries/${delivererId}`);
  export const acceptDelivery = (orderId) => api.post(`/deliveries/accept/order/${orderId}`);
  export const changeDeliveryStatus = (deliveryId, status) =>
    api.put(`/deliveries/${deliveryId}/status`, null, {
      params: { status },
    });
  export const acceptOrderByOwner = (orderId) =>
      api.post(`/deliveries/owner/accept/order/${orderId}`);
  export const markOrderAsReady = (orderId) =>
    api.post(`/deliveries/owner/mark-ready/${orderId}`);
  export const getReadyForPickupOrders = () =>
    api.get("/deliveries/ready-for-pickup");

  // -- Reports
  export const getOrderStats = () =>
    api.get("/reports/order-stats");

  export const getRestaurantRevenueByPeriod = (restaurantId, start, end) =>
    api.get("/reports/restaurant-revenue", {
      params: { restaurantId, start, end },
    });
  
  export const getDelivererRevenueForPeriod = (restaurantId, start, end) =>
    api.get("/reports/deliverer-revenue", {
      params: { restaurantId, start, end },
    });



export default api;


