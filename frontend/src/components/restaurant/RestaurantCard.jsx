import React from 'react';
import { Link } from 'react-router-dom';
import { MapPin, Phone, ArrowRight } from 'lucide-react';
import { getRestaurantImage } from '../../utils/constants';

/**
 * Modern Swiggy-inspired Restaurant Card
 */
const RestaurantCard = ({ restaurant }) => {
  const {
    id,
    restaurantName,
    address,
    contactNumber,
    description,
    open,
    imageUrl,
  } = restaurant;

  const displayImage = getRestaurantImage(imageUrl, id, restaurantName);

  return (
    <Link
      to={`/restaurant/${id}`}
      className="group bg-white rounded-3xl overflow-hidden border border-gray-200/80 shadow-sm hover:shadow-xl hover:border-brand-200 transition-all duration-300 flex flex-col transform hover:-translate-y-1"
    >
      {/* Restaurant Image with Open/Closed Badge */}
      <div className="relative h-48 w-full overflow-hidden bg-gray-100">
        <img
          src={displayImage}
          alt={restaurantName}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
          loading="lazy"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent"></div>

        {/* Status Pill */}
        <div className="absolute top-3 left-3">
          <span
            className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-bold tracking-wide shadow-sm backdrop-blur-md ${
              open
                ? 'bg-emerald-500/90 text-white'
                : 'bg-rose-500/90 text-white'
            }`}
          >
            <span
              className={`w-1.5 h-1.5 rounded-full mr-1.5 ${
                open ? 'bg-white animate-pulse' : 'bg-white/80'
              }`}
            ></span>
            {open ? 'Open Now' : 'Closed'}
          </span>
        </div>

        {/* Quick Restaurant Name on Image */}
        <div className="absolute bottom-3 left-3 right-3 text-white">
          <h3 className="text-xl font-bold tracking-tight drop-shadow-sm truncate">
            {restaurantName}
          </h3>
        </div>
      </div>

      {/* Card Body */}
      <div className="p-5 flex-1 flex flex-col justify-between">
        <div>
          {/* Description */}
          <p className="text-sm text-gray-600 line-clamp-2 leading-relaxed mb-4">
            {description || 'Serving mouth-watering dishes crafted with passion and fresh ingredients.'}
          </p>

          {/* Location and Contact */}
          <div className="space-y-1.5 text-xs text-gray-500">
            {address && (
              <div className="flex items-center space-x-2 truncate">
                <MapPin className="w-3.5 h-3.5 text-gray-400 flex-shrink-0" />
                <span className="truncate">{address}</span>
              </div>
            )}
            {contactNumber && (
              <div className="flex items-center space-x-2">
                <Phone className="w-3.5 h-3.5 text-gray-400 flex-shrink-0" />
                <span>{contactNumber}</span>
              </div>
            )}
          </div>
        </div>

        {/* Footer / View Menu CTA */}
        <div className="mt-5 pt-4 border-t border-gray-100 flex items-center justify-between text-sm">
          <span className="font-semibold text-brand-600 group-hover:text-brand-700">
            Explore Menu
          </span>
          <div className="w-7 h-7 rounded-full bg-orange-50 text-brand-600 flex items-center justify-center group-hover:bg-brand-500 group-hover:text-white transition-colors duration-200">
            <ArrowRight className="w-3.5 h-3.5" />
          </div>
        </div>
      </div>
    </Link>
  );
};

export default RestaurantCard;

