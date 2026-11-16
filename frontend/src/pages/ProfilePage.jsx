import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/authApi';
import { 
  User, 
  Mail, 
  MapPin, 
  Lock, 
  ShieldCheck, 
  Clock, 
  CheckCircle, 
  AlertCircle, 
  Eye, 
  EyeOff, 
  LogOut, 
  Save, 
  KeyRound 
} from 'lucide-react';

/**
 * Customer Profile Page
 * 
 * Features:
 * 1. View Account Details: Display ID, name, email, role, and address from GET /api/auth/me.
 * 2. Update Profile Details: Edit name and address via PUT /updateCustomer/{id}.
 * 3. Change Password: Secure password update via PUT /api/auth/change-password.
 * 4. Sign Out: Discard JWT token and clear session state.
 */
const ProfilePage = () => {
  const { user, logout, refreshProfile } = useAuth();
  const navigate = useNavigate();

  // Active tab state: 'details' | 'security'
  const [activeTab, setActiveTab] = useState('details');

  // Profile Details Form State
  const [name, setName] = useState('');
  const [address, setAddress] = useState('');
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileSuccess, setProfileSuccess] = useState('');
  const [profileError, setProfileError] = useState('');

  // Change Password Form State
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showOldPassword, setShowOldPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [passwordLoading, setPasswordLoading] = useState(false);
  const [passwordSuccess, setPasswordSuccess] = useState('');
  const [passwordError, setPasswordError] = useState('');

  // Initialize form fields from current user profile
  useEffect(() => {
    if (user) {
      setName(user.name || '');
      setAddress(user.address || '');
    }
  }, [user]);

  // Handle Profile Update: PUT /updateCustomer/{Id}
  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    setProfileSuccess('');
    setProfileError('');

    if (!name.trim()) {
      setProfileError('Name cannot be empty');
      return;
    }

    try {
      setProfileLoading(true);
      // Payload expects Customer entity fields: name, email, address
      await authApi.updateProfile(user.id, {
        name: name.trim(),
        email: user.email,
        address: address.trim(),
      });

      // Sync updated user profile with AuthContext
      await refreshProfile();
      setProfileSuccess('Profile updated successfully!');
      
      // Auto clear success message after 4 seconds
      setTimeout(() => setProfileSuccess(''), 4000);
    } catch (err) {
      console.error('Update profile error:', err);
      const msg = err.response?.data?.message || 'Failed to update profile. Please try again.';
      setProfileError(msg);
    } finally {
      setProfileLoading(false);
    }
  };

  // Handle Change Password: PUT /api/auth/change-password
  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    setPasswordSuccess('');
    setPasswordError('');

    if (!oldPassword) {
      setPasswordError('Please enter your current password');
      return;
    }

    if (newPassword.length < 6) {
      setPasswordError('New password must be at least 6 characters long');
      return;
    }

    if (newPassword !== confirmPassword) {
      setPasswordError('New password and confirm password do not match');
      return;
    }

    try {
      setPasswordLoading(true);
      await authApi.changePassword({
        oldPassword,
        newPassword,
      });

      setPasswordSuccess('Password changed successfully!');
      setOldPassword('');
      setNewPassword('');
      setConfirmPassword('');

      setTimeout(() => setPasswordSuccess(''), 4000);
    } catch (err) {
      console.error('Change password error:', err);
      const msg = err.response?.data?.message || 'Failed to change password. Make sure current password is correct.';
      setPasswordError(msg);
    } finally {
      setPasswordLoading(false);
    }
  };

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      
      {/* Profile Header Card */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-gray-200/80 shadow-sm mb-8">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="flex items-center space-x-4">
            <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-brand-600 to-amber-500 text-white flex items-center justify-center font-bold text-2xl shadow-md shadow-brand-500/20">
              {user?.name ? user.name.charAt(0).toUpperCase() : 'U'}
            </div>
            <div>
              <div className="flex items-center space-x-2">
                <h1 className="text-2xl font-bold text-gray-900">{user?.name}</h1>
                <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800">
                  {user?.role || 'CUSTOMER'}
                </span>
              </div>
              <p className="text-sm text-gray-500 mt-0.5">{user?.email}</p>
              <p className="text-xs text-gray-400 mt-1">Customer ID: #{user?.id}</p>
            </div>
          </div>

          <button
            onClick={handleLogout}
            className="inline-flex items-center space-x-1.5 px-4 py-2 rounded-xl text-sm font-semibold text-red-600 hover:bg-red-50 border border-red-200 transition-colors"
          >
            <LogOut className="w-4 h-4" />
            <span>Sign Out</span>
          </button>
        </div>

        {/* Tab Buttons */}
        <div className="flex border-b border-gray-200 mt-8 space-x-8">
          <button
            onClick={() => setActiveTab('details')}
            className={`pb-3 text-sm font-semibold border-b-2 transition-colors flex items-center space-x-2 ${
              activeTab === 'details'
                ? 'border-brand-500 text-brand-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            <User className="w-4 h-4" />
            <span>Personal Details</span>
          </button>

          <button
            onClick={() => setActiveTab('security')}
            className={`pb-3 text-sm font-semibold border-b-2 transition-colors flex items-center space-x-2 ${
              activeTab === 'security'
                ? 'border-brand-500 text-brand-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            }`}
          >
            <KeyRound className="w-4 h-4" />
            <span>Security &amp; Password</span>
          </button>
        </div>
      </div>

      {/* Tab 1: Personal Details */}
      {activeTab === 'details' && (
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-gray-200/80 shadow-sm">
          <div className="mb-6">
            <h2 className="text-lg font-bold text-gray-900">Manage Your Details</h2>
            <p className="text-sm text-gray-500">Update your primary delivery address and contact name.</p>
          </div>

          {/* Alerts */}
          {profileSuccess && (
            <div className="mb-6 p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm flex items-center space-x-2">
              <CheckCircle className="w-5 h-5 flex-shrink-0 text-emerald-600" />
              <span>{profileSuccess}</span>
            </div>
          )}

          {profileError && (
            <div className="mb-6 p-4 rounded-xl bg-red-50 border border-red-200 text-red-800 text-sm flex items-center space-x-2">
              <AlertCircle className="w-5 h-5 flex-shrink-0 text-red-600" />
              <span>{profileError}</span>
            </div>
          )}

          <form onSubmit={handleProfileSubmit} className="space-y-6">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              
              {/* Full Name */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1">
                  Full Name
                </label>
                <div className="relative">
                  <User className="w-5 h-5 text-gray-400 absolute left-3 top-3" />
                  <input
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                    placeholder="John Doe"
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500 text-sm"
                  />
                </div>
              </div>

              {/* Email (Read Only Account Identifier) */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-1">
                  Email Address
                  <span className="text-xs text-gray-400 font-normal ml-2">(Account ID &amp; Login)</span>
                </label>
                <div className="relative">
                  <Mail className="w-5 h-5 text-gray-400 absolute left-3 top-3" />
                  <input
                    type="email"
                    value={user?.email || ''}
                    disabled
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-gray-200 bg-gray-50 text-gray-500 text-sm cursor-not-allowed"
                  />
                </div>
              </div>

            </div>

            {/* Delivery Address */}
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">
                Default Delivery Address
              </label>
              <div className="relative">
                <MapPin className="w-5 h-5 text-gray-400 absolute left-3 top-3" />
                <textarea
                  rows="3"
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                  placeholder="Street name, Apartment/Suite, City, Pin Code"
                  className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500 text-sm"
                />
              </div>
              <p className="text-xs text-gray-500 mt-1">
                This address will be automatically pre-filled whenever you proceed to checkout.
              </p>
            </div>

            {/* Save Button */}
            <div className="flex items-center justify-end pt-2">
              <button
                type="submit"
                disabled={profileLoading}
                className="inline-flex items-center space-x-2 px-6 py-2.5 rounded-xl bg-brand-500 hover:bg-brand-600 disabled:opacity-50 text-white font-semibold shadow-md shadow-brand-500/20 transition"
              >
                {profileLoading ? (
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                ) : (
                  <>
                    <Save className="w-4 h-4" />
                    <span>Save Changes</span>
                  </>
                )}
              </button>
            </div>
          </form>

          {/* Quick Action: Order History Link */}
          <div className="mt-10 pt-6 border-t border-gray-100 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
            <div>
              <h3 className="text-sm font-bold text-gray-800">Looking for past food orders?</h3>
              <p className="text-xs text-gray-500">View real-time status, receipts, and order tracking.</p>
            </div>
            <Link
              to="/orders"
              className="inline-flex items-center space-x-2 px-4 py-2 rounded-xl text-sm font-medium bg-gray-100 hover:bg-gray-200 text-gray-800 transition"
            >
              <Clock className="w-4 h-4 text-gray-600" />
              <span>Go to Order History</span>
            </Link>
          </div>
        </div>
      )}

      {/* Tab 2: Security & Password */}
      {activeTab === 'security' && (
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-gray-200/80 shadow-sm">
          <div className="mb-6">
            <h2 className="text-lg font-bold text-gray-900">Change Password</h2>
            <p className="text-sm text-gray-500">Ensure your account remains safe with a strong password (minimum 6 characters).</p>
          </div>

          {/* Alerts */}
          {passwordSuccess && (
            <div className="mb-6 p-4 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm flex items-center space-x-2">
              <CheckCircle className="w-5 h-5 flex-shrink-0 text-emerald-600" />
              <span>{passwordSuccess}</span>
            </div>
          )}

          {passwordError && (
            <div className="mb-6 p-4 rounded-xl bg-red-50 border border-red-200 text-red-800 text-sm flex items-center space-x-2">
              <AlertCircle className="w-5 h-5 flex-shrink-0 text-red-600" />
              <span>{passwordError}</span>
            </div>
          )}

          <form onSubmit={handlePasswordSubmit} className="space-y-5 max-w-md">
            
            {/* Current Password */}
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">
                Current Password
              </label>
              <div className="relative">
                <Lock className="w-5 h-5 text-gray-400 absolute left-3 top-3" />
                <input
                  type={showOldPassword ? 'text' : 'password'}
                  value={oldPassword}
                  onChange={(e) => setOldPassword(e.target.value)}
                  placeholder="Enter current password"
                  required
                  className="w-full pl-10 pr-10 py-2.5 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500 text-sm"
                />
                <button
                  type="button"
                  onClick={() => setShowOldPassword(!showOldPassword)}
                  className="absolute right-3 top-3 text-gray-400 hover:text-gray-600"
                >
                  {showOldPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {/* New Password */}
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">
                New Password
              </label>
              <div className="relative">
                <Lock className="w-5 h-5 text-gray-400 absolute left-3 top-3" />
                <input
                  type={showNewPassword ? 'text' : 'password'}
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  placeholder="At least 6 characters"
                  required
                  minLength={6}
                  className="w-full pl-10 pr-10 py-2.5 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500 text-sm"
                />
                <button
                  type="button"
                  onClick={() => setShowNewPassword(!showNewPassword)}
                  className="absolute right-3 top-3 text-gray-400 hover:text-gray-600"
                >
                  {showNewPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            {/* Confirm New Password */}
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-1">
                Confirm New Password
              </label>
              <div className="relative">
                <Lock className="w-5 h-5 text-gray-400 absolute left-3 top-3" />
                <input
                  type={showNewPassword ? 'text' : 'password'}
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  placeholder="Re-enter new password"
                  required
                  minLength={6}
                  className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:border-brand-500 text-sm"
                />
              </div>
            </div>

            {/* Submit Button */}
            <div className="pt-2">
              <button
                type="submit"
                disabled={passwordLoading}
                className="inline-flex items-center space-x-2 px-6 py-2.5 rounded-xl bg-brand-500 hover:bg-brand-600 disabled:opacity-50 text-white font-semibold shadow-md shadow-brand-500/20 transition"
              >
                {passwordLoading ? (
                  <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                ) : (
                  <>
                    <KeyRound className="w-4 h-4" />
                    <span>Update Password</span>
                  </>
                )}
              </button>
            </div>
          </form>

          {/* Security Information Box */}
          <div className="mt-10 p-4 rounded-2xl bg-amber-50/70 border border-amber-200 text-amber-900 text-xs flex items-start space-x-3">
            <ShieldCheck className="w-5 h-5 flex-shrink-0 text-amber-600 mt-0.5" />
            <div>
              <p className="font-semibold text-amber-950">Stateless BCrypt Encryption</p>
              <p className="mt-0.5 text-amber-800">
                Your password is salted and encrypted using Spring Security's BCryptPasswordEncoder. Neither Foodify admins nor developers can view your plaintext password.
              </p>
            </div>
          </div>
        </div>
      )}

    </div>
  );
};

export default ProfilePage;

