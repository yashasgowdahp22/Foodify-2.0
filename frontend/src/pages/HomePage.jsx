import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { restaurantApi } from '../api/restaurantApi';
import RestaurantCard from '../components/restaurant/RestaurantCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import ErrorAlert from '../components/common/ErrorAlert';
import { POPULAR_CATEGORIES } from '../utils/constants';
import { Search, UtensilsCrossed, Sparkles } from 'lucide-react';

/**
 * Customer Home Page
 * 
 * Features:
 * 1. Swiggy/Zomato style Hero with quick search.
 * 2. Popular category pills.
 * 3. Responsive restaurant card grid fetched from GET /getAllRestaurants.
 * 4. Live search capability using GET /searchRestaurants.
 * 5. Complete loading, error, and empty states.
 */
const HomePage = () => {
  const navigate = useNavigate();

  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');

  // Fetch restaurants from backend: GET /getAllRestaurants
  const fetchRestaurants = async (query = '') => {
    try {
      setLoading(true);
      setError('');
      let data = [];
      if (query.trim()) {
        data = await restaurantApi.searchRestaurants(query.trim());
      } else {
        data = await restaurantApi.getAllRestaurants();
      }
      setRestaurants(data || []);
    } catch (err) {
      console.error('Error fetching restaurants:', err);
      setError('Unable to load restaurants. Please ensure the backend server is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRestaurants();
  }, []);

  // Handle hero search form submission
  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      // Navigate to dedicated Search page with query
      navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
    } else {
      fetchRestaurants();
    }
  };

  // Handle category pill click
  const handleCategoryClick = (category) => {
    setSelectedCategory(category);
    if (category === 'All') {
      fetchRestaurants();
    } else {
      // Navigate to Search page with category pre-filtered
      navigate(`/search?category=${encodeURIComponent(category)}`);
    }
  };

  return (
    <div className="min-h-screen pb-20">
      
      {/* Hero Section */}
      <section className="relative bg-gradient-to-b from-orange-50/80 via-white to-gray-50 pt-12 pb-16 px-4 sm:px-6 lg:px-8 border-b border-gray-100">
        <div className="max-w-4xl mx-auto text-center space-y-6">
          
          {/* Badge */}
          <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full bg-orange-100 text-brand-700 text-xs font-bold uppercase tracking-wider">
            <Sparkles className="w-3.5 h-3.5 fill-current" />
            <span>Discover Top Restaurants &bull; Fast Delivery</span>
          </div>

          {/* Heading */}
          <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold text-gray-900 tracking-tight leading-tight">
            Craving something delicious? <br />
            <span className="text-brand-600 bg-gradient-to-r from-brand-600 to-amber-500 bg-clip-text text-transparent">
              Order from top restaurants
            </span>
          </h1>

          <p className="text-base sm:text-lg text-gray-600 max-w-2xl mx-auto">
            Explore authentic cuisines, popular dishes, and exclusive local favorites delivered hot to your doorstep.
          </p>

          {/* Hero Search Bar */}
          <form onSubmit={handleSearchSubmit} className="max-w-2xl mx-auto pt-2">
            <div className="relative flex items-center bg-white rounded-full shadow-lg border border-gray-200/80 p-1.5 focus-within:ring-2 focus-within:ring-brand-500 focus-within:border-brand-500 transition-all">
              <Search className="w-5 h-5 text-gray-400 ml-4 flex-shrink-0" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search for restaurants, cuisines, or dishes..."
                className="w-full px-4 py-2.5 text-sm sm:text-base text-gray-800 placeholder-gray-400 bg-transparent focus:outline-none"
              />
              <button
                type="submit"
                className="px-6 py-2.5 rounded-full bg-brand-500 hover:bg-brand-600 text-white font-semibold text-sm shadow-md shadow-brand-500/20 transition-all transform hover:scale-[1.02] active:scale-100"
              >
                Search
              </button>
            </div>
          </form>

          {/* Quick Category Filter Pills */}
          <div className="pt-4 flex items-center justify-center flex-wrap gap-2">
            {POPULAR_CATEGORIES.map((category) => (
              <button
                key={category}
                onClick={() => handleCategoryClick(category)}
                className={`px-4 py-1.5 rounded-full text-xs sm:text-sm font-semibold transition-all ${
                  selectedCategory === category
                    ? 'bg-brand-500 text-white shadow-md shadow-brand-500/20'
                    : 'bg-white text-gray-600 border border-gray-200 hover:border-brand-300 hover:text-brand-600'
                }`}
              >
                {category}
              </button>
            ))}
          </div>

        </div>
      </section>

      {/* Restaurant Listing Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-12">
        
        {/* Section Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-6 mb-6 border-b border-gray-200/60 gap-2">
          <div>
            <h2 className="text-2xl sm:text-3xl font-bold text-gray-900 tracking-tight flex items-center space-x-2">
              <UtensilsCrossed className="w-6 h-6 text-brand-500" />
              <span>Available Restaurants</span>
            </h2>
            <p className="text-sm text-gray-500 mt-1">
              Top curated restaurants offering quality food and quick delivery.
            </p>
          </div>

          {!loading && !error && (
            <span className="text-xs font-semibold text-gray-500 bg-gray-100 px-3 py-1 rounded-full self-start sm:self-center">
              {restaurants.length} {restaurants.length === 1 ? 'Restaurant' : 'Restaurants'} Found
            </span>
          )}
        </div>

        {/* Loading State */}
        {loading && <LoadingSpinner message="Fetching restaurants from Foodify backend..." />}

        {/* Error State */}
        {error && !loading && (
          <ErrorAlert message={error} onRetry={() => fetchRestaurants(searchQuery)} />
        )}

        {/* Empty State */}
        {!loading && !error && restaurants.length === 0 && (
          <EmptyState
            title="No Restaurants Found"
            description={
              searchQuery
                ? `No restaurants match "${searchQuery}". Try searching for another name or location.`
                : "No restaurants are registered in the system yet. Please add a restaurant via Admin or backend."
            }
            actionText={searchQuery ? "Clear Search" : "Refresh"}
            onAction={() => {
              setSearchQuery('');
              fetchRestaurants();
            }}
          />
        )}

        {/* Restaurant Cards Grid */}
        {!loading && !error && restaurants.length > 0 && (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 sm:gap-8">
            {restaurants.map((restaurant) => (
              <RestaurantCard key={restaurant.id} restaurant={restaurant} />
            ))}
          </div>
        )}

      </section>

    </div>
  );
};

export default HomePage;
