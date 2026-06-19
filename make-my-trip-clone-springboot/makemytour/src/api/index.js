import axios from "axios";

const BACKEND_URL = process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";

export const login = async (email, password) => {
  try {
    const url = `${BACKEND_URL}/user/login?email=${email}&password=${password}`;
    const res = await axios.post(url);
    const data = res.data;
    // console.log(data);
    return data;
  } catch (error) {
    throw error;
  }
};

export const signup = async (
  firstName,
  lastName,
  email,
  phoneNumber,
  password
) => {
  try {
    const res = await axios.post(`${BACKEND_URL}/user/signup`, {
      firstName,
      lastName,
      email,
      phoneNumber,
      password,
    });
    const data = res.data;
    // console.log(data);
    return data;
  } catch (error) {
    throw error;
  }
};

export const getuserbyemail = async (email) => {
  try {
    const res = await axios.get(`${BACKEND_URL}/user/email?email=${email}`);
    const data = res.data;
    return data;
  } catch (error) {
    throw error;
  }
};

export const editprofile = async (
  id,
  firstName,
  lastName,
  email,
  phoneNumber
) => {
  try {
    const res = await axios.post(`${BACKEND_URL}/user/edit?id=${id}`, {
      firstName,
      lastName,
      email,
      phoneNumber,
    });
    const data = res.data;
    return data;
  } catch (error) {}
};
export const getflight = async () => {
  try {
    const res = await axios.get(`${BACKEND_URL}/flight`);
    const data = res.data;
    return data;
  } catch (error) {
    console.error("Error fetching flights:", error);
    return [];
  }
};

export const getFlightDynamicPrice = async (flightId) => {
  const res = await axios.get(`${BACKEND_URL}/flight/${flightId}/dynamic-price`);
  return res.data;
};

export const getHotelDynamicPrice = async (hotelId) => {
  const res = await axios.get(`${BACKEND_URL}/hotel/${hotelId}/dynamic-price`);
  return res.data;
};

export const getPriceHistory = async (itemType, itemId) => {
  const res = await axios.get(`${BACKEND_URL}/price-history/${itemType}/${itemId}`);
  return res.data;
};

export const freezeFlightPrice = async (userId, flightId, quantity) => {
  const res = await axios.post(
    `${BACKEND_URL}/flight/${flightId}/price-freeze?userId=${userId}&quantity=${quantity}`
  );
  return res.data;
};

export const freezeHotelPrice = async (userId, hotelId, quantity) => {
  const res = await axios.post(
    `${BACKEND_URL}/hotel/${hotelId}/price-freeze?userId=${userId}&quantity=${quantity}`
  );
  return res.data;
};

export const addflight = async (
  flightName,
  from,
  to,
  departureTime,
  arrivalTime,
  price,
  availableSeats
) => {
  try {
    const res = await axios.post(`${BACKEND_URL}/admin/flight`, {
      flightName,
      from,
      to,
      departureTime,
      arrivalTime,
      price,
      availableSeats,
    });
    const data = res.data;
    return data;
  } catch (error) {
    console.log(error);
  }
};

export const editflight = async (
  id,
  flightName,
  from,
  to,
  departureTime,
  arrivalTime,
  price,
  availableSeats
) => {
  try {
    const res = await axios.put(`${BACKEND_URL}/admin/flight/${id}`, {
      flightName,
      from,
      to,
      departureTime,
      arrivalTime,
      price,
      availableSeats,
    });
    const data = res.data;
    return data;
  } catch (error) {
    console.log(error);
  }
};

export const gethotel = async () => {
  try {
    const res = await axios.get(`${BACKEND_URL}/hotel`);
    const data = res.data;
    return data;
  } catch (error) {
    console.log(data);
  }
};

export const addhotel = async (
  hotelName,
  location,
  pricePerNight,
  availableRooms,
  amenities
) => {
  try {
    const res = await axios.post(`${BACKEND_URL}/admin/hotel`, {
      hotelName,
      location,
      pricePerNight,
      availableRooms,
      amenities,
    });
    const data = res.data;
    return data;
  } catch (error) {
    console.log(error);
  }
};

export const edithotel = async (
  id,
  hotelName,
  location,
  pricePerNight,
  availableRooms,
  amenities
) => {
  try {
    const res = await axios.put(`${BACKEND_URL}/admin/hotel/${id}`, {
      hotelName,
      location,
      pricePerNight,
      availableRooms,
      amenities,
    });
    const data = res.data;
    return data;
  } catch (error) {
    console.log(error);
  }
};

export const handleflightbooking = async (userId, flightId, seats, price, freezeId) => {
  try {
    const freezeQuery = freezeId ? `&freezeId=${freezeId}` : "";
    const url = `${BACKEND_URL}/booking/flight?userId=${userId}&flightId=${flightId}&seats=${seats}&price=${price}${freezeQuery}`;
    const res = await axios.post(url);
    const data = res.data;
    return data;
  } catch (error) {
    console.log(error);
  }
};

export const handlehotelbooking = async (userId, hotelId, rooms, price, freezeId) => {
  try {
    const freezeQuery = freezeId ? `&freezeId=${freezeId}` : "";
    const url = `${BACKEND_URL}/booking/hotel?userId=${userId}&hotelId=${hotelId}&rooms=${rooms}&price=${price}${freezeQuery}`;
    const res = await axios.post(url);
    const data = res.data;
    return data;
  } catch (error) {
    console.log(error);
  }
};

export const cancelBooking = async (bookingId, reason) => {
  try {
    const url = `${BACKEND_URL}/cancellations/${bookingId}?reason=${encodeURIComponent(reason)}`;
    const res = await axios.post(url);
    return res.data;
  } catch (error) {
    console.error("Error cancelling booking:", error);
    throw error;
  }
};

export const getUserRefunds = async (userId) => {
  try {
    const url = `${BACKEND_URL}/cancellations/user?userId=${encodeURIComponent(userId)}`;
    const res = await axios.get(url);
    return res.data;
  } catch (error) {
    console.error("Error fetching user refunds:", error);
    return [];
  }
};
