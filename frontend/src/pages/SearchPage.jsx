import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { restaurantApi } from '../api/restaurantApi';
import { menuApi } from '../api/menuApi';
import RestaurantCard from '../components/restaurant/RestaurantCard';
import MenuItemCard from '../components/menu/MenuItemCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EmptyState from '../components/common/EmptyState';
import ErrorAlert from '../components/common/ErrorAlert';
import { POPULAR_CATEGORIES } from '../utils/constants';
import { Search, X, Store, Utensils, ArrowRight } from 'lucide-react';

/**
 * Customer Search Page
 * Supports searching both Restaurants (GET /searchRestaurants) and Dishes (GET /searchMenuItems or GET /getMenuItemsByCategory)
 */
const SearchPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  // Read initial query / category from URL params
  const initialQuery = searchParams.get('q') || '';
  const initialCategory = searchParams.get('category') || '';

  const [query, setQuery] = useState(initialQuery);
  const [activeTab, setActiveTab] = useState('all'); // 'all' | 'restaurants' | 'dishes'
  const [selectedCategory, setSelectedCategory] = useState(initialCategory || 'All');

  const [restaurants, setRestaurants] = useState([]);
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Main search function
  const executeSearch = async (searchTerm, categoryTerm) => {
    try {
      setLoading(true);
      setError('');

      const cleanQuery = searchTerm.trim();
      const isSpecificCategory = categoryTerm && categoryTerm !== 'All';

      // 1. Fetch Restaurants (using backend search endpoint)
      const restaurantPromise = restaurantApi.searchRestaurants(cleanQuery);

      // 2. Fetch Menu Items (by category or dish query)
      let menuPromise;
      if (isSpecificCategory && !cleanQuery) {
        menuPromise = menuApi.getMenuItemsByCategory(categoryTerm);
      } else {
        menuPromise = menuApi.searchMenuItems(cleanQuery);
      }

      const [restaurantData, menuData] = await Promise.all([
        restaurantPromise.catch(() => []),
        menuPromise.catch(() => []),
      ]);

      setRestaurants(restaurantData || []);
      setMenuItems(menuData || []);
    } catch (err) {
      console.error('Search failed:', err);
      setError('Search operation failed. Please check your network or backend server.');
    } finally {
      setLoading(false);
    }
  };

  // Perform search whenever URL query params change
  useEffect(() => {
    const qParam = searchParams.get('q') || '';
    const catParam = searchParams.get('category') || '';
    setQuery(qParam);
    setSelectedCategory(catParam || 'All');
    executeSearch(qParam, catParam);
  }, [searchParams]);

  const handleSubmit = (e) => {
    e.preventDefault();
    setSearchParams({ q: query.trim() });
  };

  const handleCategorySelect = (category) => {
    setSelectedCategory(category);
    if (category === 'All') {
      setSearchParams({ q: query.trim() });
    } else {
      setSearchParams({ category });
    }
  };

  const handleClear = () => {
    setQuery('');
    setSelectedCategory('All');
    setSearchParams({});
  };

  // Determine which sections to render based on activeTab
  const showRestaurants = activeTab === 'all' || activeTab === 'restaurants';
  const showDishes = activeTab === 'all' || activeTab === 'dishes';

  const totalResults = restaurants.length + menuItems.length;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 min-h-screen">
      
      {/* Header & Search Bar */}
      <div className="max-w-3xl mx-auto mb-10 text-center">
        <h1 className="text-3xl font-extrabold text-gray-900 mb-6">
          Search Restaurants &amp; Dishes
        </h1>

        <form onSubmit={handleSubmit} className="relative">
          <div className="relative flex items-center bg-white rounded-2xl shadow-md border border-gray-200 p-1.5 focus-within:ring-2 focus-within:ring-brand-500 focus-within:border-brand-500">
            <Search className="w-5 h-5 text-gray-400 ml-4 flex-shrink-0" />
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Search for 'Pizza', 'Biryani', 'Burger', or restaurant name..."
              className="w-full px-4 py-3 text-sm sm:text-base text-gray-800 placeholder-gray-400 bg-transparent focus:outline-none"
            />
            {query && (
              <button
                type="button"
                onClick={handleClear}
                className="p-1.5 mr-2 text-gray-400 hover:text-gray-600 rounded-full hover:bg-gray-100"
              >
                <X className="w-4 h-4" />
              </button>
            )}
            <button
              type="submit"
              className="px-6 py-2.5 rounded-xl bg-brand-500 hover:bg-brand-600 text-white font-semibold text-sm shadow-sm transition"
            >
              Search
            </button>
          </div>
        </form>

        {/* Category Pills */}
        <div className="pt-5 flex items-center justify-center flex-wrap gap-2">
          {POPULAR_CATEGORIES.map((category) => (
            <button
              key={category}
              onClick={() => handleCategorySelect(category)}
              className={`px-3.5 py-1.5 rounded-full text-xs font-semibold transition-all ${
                selectedCategory === category
                  ? 'bg-brand-500 text-white shadow-sm'
                  : 'bg-white text-gray-600 border border-gray-200 hover:border-brand-300 hover:text-brand-600'
              }`}
            >
              {category}
            </button>
          ))}
        </div>
      </div>

      {/* Filter Tabs (All / Restaurants / Dishes) */}
      {!loading && (restaurants.length > 0 || menuItems.length > 0) && (
        <div className="flex items-center justify-center space-x-2 mb-8 border-b border-gray-200 pb-4">
          <button
            onClick={() => setActiveTab('all')}
            className={`px-4 py-2 rounded-xl text-xs sm:text-sm font-bold transition-all ${
              activeTab === 'all'
                ? 'bg-brand-500 text-white shadow-sm'
                : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
            }`}
          >
            All Results ({totalResults})
          </button>

          <button
            onClick={() => setActiveTab('restaurants')}
            className={`px-4 py-2 rounded-xl text-xs sm:text-sm font-bold transition-all flex items-center space-x-1.5 ${
              activeTab === 'restaurants'
                ? 'bg-brand-500 text-white shadow-sm'
                : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
            }`}
          >
            <Store className="w-4 h-4" />
            <span>Restaurants ({restaurants.length})</span>
          </button>

          <button
            onClick={() => setActiveTab('dishes')}
            className={`px-4 py-2 rounded-xl text-xs sm:text-sm font-bold transition-all flex items-center space-x-1.5 ${
              activeTab === 'dishes'
                ? 'bg-brand-500 text-white shadow-sm'
                : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
            }`}
          >
            <Utensils className="w-4 h-4" />
            <span>Dishes ({menuItems.length})</span>
          </button>
        </div>
      )}

      {/* Loading State */}
      {loading && <LoadingSpinner message="Searching Foodify for restaurants &amp; delicious dishes..." />}

      {/* Error Alert */}
      {error && !loading && (
        <ErrorAlert message={error} onRetry={() => executeSearch(query, selectedCategory)} />
      )}

      {/* Empty State */}
      {!loading && !error && totalResults === 0 && (
        <EmptyState
          title="No Results Found"
          description={
            query || (selectedCategory && selectedCategory !== 'All')
              ? `We couldn't find any restaurants or dishes matching your search. Try adjusting keywords.`
              : 'Type a dish or restaurant name above to begin searching.'
          }
          actionText={query ? 'Reset Search' : undefined}
          onAction={handleClear}
        />
      )}

      {/* Results Content */}
      {!loading && !error && totalResults > 0 && (
        <div className="space-y-12">
          
          {/* Matching Restaurants Section */}
          {showRestaurants && restaurants.length > 0 && (
            <div>
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-bold text-gray-900 flex items-center space-x-2">
                  <Store className="w-5 h-5 text-brand-500" />
                  <span>Restaurants ({restaurants.length})</span>
                </h2>
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {restaurants.map((restaurant) => (
                  <RestaurantCard key={restaurant.id} restaurant={restaurant} />
                ))}
              </div>
            </div>
          )}

          {/* Matching Dishes / Menu Items Section */}
          {showDishes && menuItems.length > 0 && (
            <div>
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-bold text-gray-900 flex items-center space-x-2">
                  <Utensils className="w-5 h-5 text-brand-500" />
                  <span>Dishes &amp; Food Items ({menuItems.length})</span>
                </h2>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 sm:gap-6">
                {menuItems.map((item) => (
                  <div key={item.id} className="space-y-2">
                    <MenuItemCard item={item} />
                    {item.restaurantId && (
                      <div className="px-2 text-xs text-gray-500 flex items-center justify-between">
                        <span>Served by <strong className="text-gray-700">{item.restaurantName || 'Restaurant'}</strong></span>
                        <Link
                          to={`/restaurant/${item.restaurantId}`}
                          className="text-brand-600 hover:text-brand-700 font-semibold inline-flex items-center"
                        >
                          <span>Visit Restaurant</span>
                          <ArrowRight className="w-3 h-3 ml-1" />
                        </Link>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}

        </div>
      )}

    </div>
  );
};

export default SearchPage;

