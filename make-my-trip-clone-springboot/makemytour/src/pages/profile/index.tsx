import React, { useEffect, useState } from "react";
import {
  User,
  Phone,
  Mail,
  Edit2,
  MapPin,
  Calendar,
  CreditCard,
  X,
  Check,
  LogOut,
  Plane,
  Building2,
} from "lucide-react";
import { useDispatch, useSelector } from "react-redux";
import { useRouter } from "next/router";
import { clearUser, setUser } from "@/store";
import { cancelBooking, editprofile, getUserRefunds, getuserbyemail } from "@/api";
const index = () => {
  const dispatch = useDispatch();
  const user = useSelector((state: any) => state.user.user);
  const router = useRouter();

  const logout = () => {
    dispatch(clearUser());
    router.push("/");
  };
  const [isEditing, setIsEditing] = useState(false);
  const [reasons] = useState([
    "Change of plans",
    "Price too high",
    "Found a better deal",
    "Trip postponed",
    "Other",
  ]);
  const [selectedReasonByBooking, setSelectedReasonByBooking] = useState({} as Record<string, string>);
  const [cancellationMessage, setCancellationMessage] = useState("");
  const [refunds, setRefunds] = useState<any[]>([]);
  const [userData, setUserData] = useState({
    firstName: user?.firstName ? user?.firstName : "",
    lastName: user?.lastName ? user?.lastName : "",
    email: user?.email ? user?.email : "",
    phoneNumber: user?.phoneNumber ? user?.phoneNumber : "",
    bookings: user?.bookings || [],
  });

  const [editForm, setEditForm] = useState({ ...userData });
  const handleSave = async () => {
    try {
      const data = await editprofile(
        user?.id,
        userData.firstName,
        userData.lastName,
        userData.email,
        userData.phoneNumber
      );
      dispatch(setUser(data));
      setIsEditing(false);
    } catch (error) {
      setUserData(editForm);
      setIsEditing(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("en-IN", {
      day: "numeric",
      month: "short",
      year: "numeric",
    });
  };
  const handleEditFormChange = (field:any, value:any) => {
    setUserData((prevState) => ({
        ...prevState,
        [field]: value, // Update the specific field dynamically
      }));
  };

  useEffect(() => {
    if (user) {
      setUserData({
        firstName: user.firstName || "",
        lastName: user.lastName || "",
        email: user.email || "",
        phoneNumber: user.phoneNumber || "",
        bookings: user.bookings || [],
      });
    }
  }, [user]);

  useEffect(() => {
    const loadRefunds = async () => {
      if (!user?.id) return;
      try {
        const data = await getUserRefunds(user.id);
        setRefunds(data || []);
      } catch {
        setRefunds([]);
      }
    };
    loadRefunds();
  }, [user?.id]);

  return (
    <div className="min-h-screen bg-gray-50 pt-8 px-4">
      <div className="max-w-6xl mx-auto">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Profile Section */}
          <div className="md:col-span-1">
            <div className="bg-white rounded-xl shadow-lg p-6">
              <div className="flex justify-between items-start mb-6">
                <h2 className="text-2xl font-bold">Profile</h2>
                {!isEditing && (
                  <button
                    onClick={() => setIsEditing(true)}
                    className="text-red-600 flex items-center space-x-1 hover:text-red-700"
                  >
                    <Edit2 className="w-4 h-4" />
                    <span>Edit</span>
                  </button>
                )}
              </div>

              {isEditing ? (
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      First Name
                    </label>
                    <input
                      type="text"
                      value={userData.firstName}
                      onChange={(e) => handleEditFormChange("firstName", e.target.value)}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Last Name
                    </label>
                    <input
                      type="text"
                      value={userData.lastName}
                      onChange={(e) => handleEditFormChange("lastName", e.target.value)}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Email
                    </label>
                    <input
                      type="email"
                      value={userData.email}
                      onChange={(e) => handleEditFormChange("email", e.target.value)}
                      
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Phone Number
                    </label>
                    <input
                      type="tel"
                      value={userData.phoneNumber}
                      onChange={(e) => handleEditFormChange("phoneNumber", e.target.value)}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500"
                    />
                  </div>
                  <div className="flex space-x-3">
                    <button
                      onClick={handleSave}
                      className="flex-1 bg-red-600 text-white py-2 rounded-lg hover:bg-red-700 transition-colors flex items-center justify-center space-x-2"
                    >
                      <Check className="w-4 h-4" />
                      <span>Save</span>
                    </button>
                    <button
                      onClick={() => {
                        setIsEditing(false);
                        setEditForm({ ...user });
                      }}
                      className="flex-1 bg-gray-100 text-gray-700 py-2 rounded-lg hover:bg-gray-200 transition-colors flex items-center justify-center space-x-2"
                    >
                      <X className="w-4 h-4" />
                      <span>Cancel</span>
                    </button>
                  </div>
                </div>
              ) : (
                <div className="space-y-6">
                  <div className="flex items-center space-x-3">
                    <User className="w-5 h-5 text-gray-500" />
                    <div>
                      <p className="font-medium">
                        {user?.firstName} {user?.lastName}
                      </p>
                      {/* <p className="text-sm text-gray-500">{userData.role}</p> */}
                    </div>
                  </div>
                  <div className="flex items-center space-x-3">
                    <Mail className="w-5 h-5 text-gray-500" />
                    <p>{user?.email}</p>
                  </div>
                  <div className="flex items-center space-x-3">
                    <Phone className="w-5 h-5 text-gray-500" />
                    <p>{user?.phoneNumber}</p>
                  </div>
                  <button
                    className="w-full mt-4 flex items-center justify-center space-x-2 text-red-600 hover:text-red-700"
                    onClick={logout}
                  >
                    <LogOut className="w-4 h-4" />
                    <span>Logout</span>
                  </button>
                </div>
              )}
            </div>
          </div>

          {/* Bookings Section */}
          <div className="md:col-span-2">
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-2xl font-bold mb-6">My Bookings</h2>
              <div className="space-y-6">
                {user?.bookings.map((booking: any, index: any) => (
                  <div
                    key={index}
                    className="border rounded-lg p-4 hover:shadow-md transition-shadow"
                  >
                    <div className="flex items-start justify-between mb-4">
                      <div className="flex items-center space-x-3">
                        {booking?.type === "Flight" ? (
                          <div className="bg-blue-100 p-2 rounded-lg">
                            <Plane className="w-6 h-6 text-blue-600" />
                          </div>
                        ) : (
                          <div className="bg-green-100 p-2 rounded-lg">
                            <Building2 className="w-6 h-6 text-green-600" />
                          </div>
                        )}
                        <div>
                          <h3 className="font-semibold">{booking?.type}</h3>
                          <p className="text-sm text-gray-500">
                            Booking ID: {booking?.bookingId}
                          </p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="font-semibold">
                          ₹ {booking?.totalPrice.toLocaleString("en-IN")}
                        </p>
                        <p className="text-sm text-gray-500">{booking?.type}</p>
                      </div>
                    </div>
                    <div className="flex flex-wrap gap-4 text-sm text-gray-600">
                      <div className="flex items-center space-x-1">
                        <Calendar className="w-4 h-4" />
                        <span>{formatDate(booking?.date)}</span>
                      </div>
                      <div className="flex items-center space-x-1">
                        <MapPin className="w-4 h-4" />
                        <span>{booking?.type}</span>
                      </div>
                      <div className="flex items-center space-x-1">
                        <CreditCard className="w-4 h-4" />
                        <span>Paid</span>
                      </div>
                    </div>
                    <div className="mt-4 grid gap-3 sm:grid-cols-2">
                      {booking?.cancelled ? (
                        <div className="rounded-lg border border-orange-200 bg-orange-50 p-4">
                          <p className="text-sm font-semibold text-orange-700">Refund Status</p>
                          <p className="text-sm text-gray-600">{booking?.refundStatus || "Pending"}</p>
                          {booking?.refundAmount != null && (
                            <p className="text-sm text-gray-600">
                              Refund ₹ {booking?.refundAmount?.toLocaleString("en-IN")}
                            </p>
                          )}
                          {booking?.expectedRefundCompletionAt && (
                            <p className="text-xs text-gray-500">
                              Expected completion: {formatDate(booking?.expectedRefundCompletionAt)}
                            </p>
                          )}
                        </div>
                      ) : (
                        <div className="rounded-lg border border-blue-200 bg-blue-50 p-4">
                          <label className="block text-sm font-medium text-blue-700 mb-2">
                            Cancellation reason
                          </label>
                          <select
                            value={selectedReasonByBooking[booking.bookingId] || reasons[0]}
                            onChange={(e) =>
                              setSelectedReasonByBooking((prevState) => ({
                                ...prevState,
                                [booking.bookingId]: e.target.value,
                              }))
                            }
                            className="w-full rounded-lg border border-blue-300 p-2 text-sm"
                          >
                            {reasons.map((reason) => (
                              <option key={reason} value={reason}>
                                {reason}
                              </option>
                            ))}
                          </select>
                          <button
                            onClick={async () => {
                              const selectedReason = selectedReasonByBooking[booking.bookingId] || reasons[0];
                              try {
                                const refund = await cancelBooking(booking.bookingId, selectedReason);
                                setCancellationMessage(
                                  `Cancellation requested. Refund status: ${refund.refundStatus}`
                                );
                                const updatedBookings = user?.bookings.map((item: any) =>
                                  item.bookingId === booking.bookingId
                                    ? {
                                        ...item,
                                        cancelled: true,
                                        refundStatus: refund.refundStatus,
                                        refundAmount: refund.refundAmount,
                                        expectedRefundCompletionAt:
                                          refund.expectedCompletionAt || item.expectedRefundCompletionAt,
                                        cancellationReason: refund.cancellationReason,
                                      }
                                    : item
                                );
                                const updatedUser = { ...user, bookings: updatedBookings };
                                dispatch(setUser(updatedUser));
                                const latestRefunds = await getUserRefunds(user.id);
                                setRefunds(latestRefunds || []);
                              } catch (error) {
                                setCancellationMessage("Unable to submit cancellation. Please try again.");
                              }
                            }}
                            className="mt-3 inline-flex items-center justify-center rounded-lg bg-red-600 px-4 py-2 text-sm font-semibold text-white hover:bg-red-700"
                          >
                            Cancel Booking
                          </button>
                        </div>
                      )}
                    </div>
                    {cancellationMessage && (
                      <div className="mt-4 rounded-lg bg-green-50 border border-green-200 p-3 text-sm text-green-700">
                        {cancellationMessage}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>

            {refunds.length > 0 && (
              <div className="bg-white rounded-xl shadow-lg p-6 mt-8">
                <h2 className="text-2xl font-bold mb-4">Refund History</h2>
                <div className="space-y-4">
                  {refunds.map((refund) => (
                    <div key={refund.id || refund.bookingId} className="border rounded-lg p-4">
                      <div className="flex flex-wrap justify-between gap-4">
                        <div>
                          <p className="font-semibold">Booking ID: {refund.bookingId}</p>
                          <p className="text-sm text-gray-600">Amount: ₹ {refund.refundAmount?.toLocaleString("en-IN")}</p>
                        </div>
                        <div className="text-right">
                          <p className="text-sm font-semibold">{refund.refundStatus}</p>
                          {refund.expectedCompletionAt && (
                            <p className="text-xs text-gray-500">
                              Expected by {formatDate(refund.expectedCompletionAt)}
                            </p>
                          )}
                        </div>
                      </div>
                      <p className="text-sm text-gray-600 mt-2">Reason: {refund.cancellationReason}</p>
                      <p className="text-sm text-gray-600">Policy: {refund.refundPolicyApplied}</p>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default index;
