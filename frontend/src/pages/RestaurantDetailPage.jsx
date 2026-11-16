import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { restaurantApi } from '../api/restaurantApi';
import { menuApi } from '../api/menuApi';
import MenuItemCard from '../components/menu/MenuItemCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import ErrorAlert from '../components/common/ErrorAlert';
import { getRestaurantImage } from '../utils/constants';
import { 
  MapPin, 
  Phone, 
  Mail, 
  ArrowLeft, 
  UtensilsCrossed, 
  AlertTriangle 
} from 'lucide-react';

/**
 * Customer Restaurant Details & Menu Page
 * 
 * Features:
 * 1. Fetches restaurant details via GET /getRestaurant/{Id}.
 * 2. Fetches restaurant menu items via GET /getRestaurantMenu/{restaurantId}.
 * 3. Organizes menu items cleanly by Category.
 * 4. Sticky Category quick-jump navigation.
 * 5. Closed restaurant warning banner.
 * 6. Full loading, error, and empty states.
 */
const RestaurantDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [restaurant, setRestaurant] = useState(null);
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');

  const fetchRestaurantAndMenu = async () => {
    try {
      setLoading(true);
      setError('');

      // Fetch restaurant details and its menu in parallel
      const [restData, menuData] = await Promise.all([
        restaurantApi.getRestaurantById(id),
        menuApi.getRestaurantMenu(id),
      ]);

      setRestaurant(restData);
      setMenuItems(menuData || []);
    } catch (err) {
      console.error('Failed to load restaurant details or menu:', err);
      setError('Failed to load restaurant information. The restaurant may not exist or the backend is offline.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (id) {
      fetchRestaurantAndMenu();
    }
  }, [id]);

  // Extract unique categories from menu items
  const categories = ['All', ...new Set(menuItems.map((item) => item.category?.trim() || 'General'))];

  // Filter items based on selected category tab
  const displayedItems = selectedCategory === 'All'
    ? menuItems
    : menuItems.filter((item) => (item.category?.trim() || 'General') === selectedCategory);

  // Group displayed items by category for structured layout
  const groupedMenu = displayedItems.reduce((acc, item) => {
    const cat = item.category?.trim() || 'General';
    if (!acc[cat]) {
      acc[cat] = [];
    }
    acc[cat].push(item);
    return acc;
  }, {});

  if (loading) {
    return (
      <div className="min-h-screen py-16">
        <LoadingSpinner message="Loading restaurant and fresh menu items..." />
      </div>
    );
  }

  if (error || !restaurant) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-16 text-center">
        <ErrorAlert message={error || 'Restaurant not found.'} onRetry={fetchRestaurantAndMenu} />
        <Link
          to="/"
          className="mt-6 inline-flex items-center space-x-2 px-5 py-2.5 rounded-full bg-brand-500 hover:bg-brand-600 text-white font-semibold text-sm shadow-sm transition"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to All Restaurants</span>
        </Link>
      </div>
    );
  }

  const headerImage = getRestaurantImage(restaurant.imageUrl, restaurant.id, restaurant.restaurantName);

  return (
    <div className="min-h-screen pb-20 bg-gray-50">
      
      {/* Back Button */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-6 pb-4">
        <Link
          to="/"
          className="inline-flex items-center space-x-2 text-sm font-semibold text-gray-600 hover:text-brand-600 transition"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Restaurants</span>
        </Link>
      </div>

      {/* Restaurant Header Banner Card */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mb-8">
        <div className="relative bg-white rounded-3xl overflow-hidden border border-gray-200/80 shadow-sm">
          
          {/* Cover Photo */}
          <div className="relative h-56 sm:h-72 w-full bg-gray-900 overflow-hidden">
            <img
              src={headerImage}
              alt={restaurant.restaurantName}
              className="w-full h-full object-cover opacity-85"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent"></div>

            {/* Status Pill on Banner */}
            <div className="absolute top-4 right-4">
              <span
                className={`inline-flex items-center px-3.5 py-1.5 rounded-full text-xs font-bold tracking-wide shadow-md backdrop-blur-md ${
                  restaurant.open
                    ? 'bg-emerald-500/90 text-white'
                    : 'bg-rose-500/90 text-white'
                }`}
              >
                <span
                  className={`w-2 h-2 rounded-full mr-2 ${
                    restaurant.open ? 'bg-white animate-pulse' : 'bg-white/80'
                  }`}
                ></span>
                {restaurant.open ? 'Open For Orders' : 'Currently Closed'}
              </span>
            </div>

            {/* Title & Headline on Banner */}
            <div className="absolute bottom-6 left-6 right-6 text-white space-y-2">
              <h1 className="text-3xl sm:text-4xl font-extrabold tracking-tight drop-shadow-md">
                {restaurant.restaurantName}
              </h1>
              <p className="text-sm sm:text-base text-gray-200 max-w-2xl line-clamp-2">
                {restaurant.description || 'Authentic dining experience with fresh flavors and fast delivery.'}
              </p>
            </div>
          </div>

          {/* Meta Details Bar */}
          <div className="p-6 bg-white flex flex-wrap items-center justify-between gap-4 text-xs sm:text-sm text-gray-600 border-t border-gray-100">
            <div className="flex flex-wrap items-center gap-6">
              {restaurant.address && (
                <div className="flex items-center space-x-2">
                  <MapPin className="w-4 h-4 text-brand-500 flex-shrink-0" />
                  <span>{restaurant.address}</span>
                </div>
              )}
              {restaurant.contactNumber && (
                <div className="flex items-center space-x-2">
                  <Phone className="w-4 h-4 text-brand-500 flex-shrink-0" />
                  <span>{restaurant.contactNumber}</span>
                </div>
              )}
              {restaurant.email && (
                <div className="flex items-center space-x-2">
                  <Mail className="w-4 h-4 text-brand-500 flex-shrink-0" />
                  <span>{restaurant.email}</span>
                </div>
              )}
            </div>

            <div className="text-xs text-gray-400 font-medium">
              Restaurant #{restaurant.id}
            </div>
          </div>

        </div>

        {/* Closed Warning Banner */}
        {!restaurant.open && (
          <div className="mt-4 p-4 rounded-2xl bg-amber-50 border border-amber-200 text-amber-900 text-sm flex items-center space-x-3 shadow-sm">
            <AlertTriangle className="w-5 h-5 flex-shrink-0 text-amber-600" />
            <span>
              <strong>Restaurant is currently closed:</strong> You can explore the menu items, but ordering is temporarily disabled until the restaurant reopens.
            </span>
          </div>
        )}
      </section>

      {/* Menu Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        {/* Sticky Category Bar */}
        {menuItems.length > 0 && (
          <div className="sticky top-16 z-30 bg-white/95 backdrop-blur-md py-4 mb-8 border-b border-gray-200 -mx-4 sm:-mx-6 lg:-mx-8 px-4 sm:px-6 lg:px-8 shadow-sm">
            <div className="flex items-center space-x-2 overflow-x-auto no-scrollbar py-1">
              <span className="text-xs font-bold text-gray-400 uppercase tracking-wider mr-2 hidden sm:inline">
                Categories:
              </span>
              {categories.map((cat) => (
                <button
                  key={cat}
                  onClick={() => setSelectedCategory(cat)}
                  className={`px-4 py-2 rounded-full text-xs sm:text-sm font-semibold whitespace-nowrap transition-all ${
                    selectedCategory === cat
                      ? 'bg-brand-500 text-white shadow-md shadow-brand-500/20'
                      : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                  }`}
                >
                  {cat}
                </button>
              ))}
            </div>
          </div>
        )}

        {/* Empty Menu State */}
        {menuItems.length === 0 && (
          <EmptyState
            icon={UtensilsCrossed}
            title="Menu Under Construction"
            description="This restaurant has not published any menu items yet. Please check back later!"
            actionText="Browse Other Restaurants"
            onAction={() => navigate('/')}
          />
        )}

        {/* Categorized Menu Items */}
        {menuItems.length > 0 && (
          <div className="space-y-12">
            {Object.entries(groupedMenu).map(([categoryName, items]) => (
              <div key={categoryName} className="space-y-4">
                
                {/* Category Heading */}
                <div className="flex items-center space-x-3 border-b border-gray-200 pb-2">
                  <h3 className="text-xl sm:text-2xl font-bold text-gray-900 tracking-tight">
                    {categoryName}
                  </h3>
                  <span className="text-xs font-semibold text-gray-400 bg-gray-100 px-2.5 py-0.5 rounded-full">
                    {items.length} {items.length === 1 ? 'item' : 'items'}
                  </span>
                </div>

                {/* Items Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 sm:gap-6">
                  {items.map((item) => (
                    <MenuItemCard
                      key={item.id}
                      item={item}
                      isRestaurantOpen={restaurant.open}
                    />
                  ))}
                </div>

              </div>
            ))}
          </div>
        )}

      </section>

    </div>
  );
};

export default RestaurantDetailPage;

