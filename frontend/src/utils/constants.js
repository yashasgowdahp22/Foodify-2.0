/**
 * Foodify 2.0 Constants, Categories & Image Mappings
 * Realistic, high-resolution food & restaurant photography for all database records
 */

// 1. Exact Restaurant Image Mappings (By Normalized Name & ID)
export const RESTAURANT_IMAGES_BY_NAME = {
  // 1. Spice Garden - Authentic Indian food & Biryani dining ambiance
  'spice garden': 'https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=800&auto=format&fit=crop&q=80',
  // 2. Pizza Palace - Italian pizzeria with wood-fired oven & gourmet setting
  'pizza palace': 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop&q=80',
  // 3. South Kitchen - Traditional South Indian restaurant with banana leaf & brass setting
  'south kitchen': 'https://images.unsplash.com/photo-1610192244261-3f33de3f55e4?w=800&auto=format&fit=crop&q=80',
  // 4. Chinese Wok - Asian & Chinese wok bistro with glowing ambient lanterns
  'chinese wok': 'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800&auto=format&fit=crop&q=80',
  // 5. Sweet Treats - Elegant pastry shop, bakery & dessert cafe
  'sweet treats': 'https://images.unsplash.com/photo-1559925393-8be0ec4767c8?w=800&auto=format&fit=crop&q=80',
  // 6. Grill House - Rustic barbecue smokehouse & flame-grill restaurant
  'grill house': 'https://images.unsplash.com/photo-1544025162-d76694265947?w=800&auto=format&fit=crop&q=80',
};

export const RESTAURANT_IMAGES_BY_ID = {
  1: 'https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=800&auto=format&fit=crop&q=80', // Spice Garden
  2: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop&q=80', // Pizza Palace
  3: 'https://images.unsplash.com/photo-1610192244261-3f33de3f55e4?w=800&auto=format&fit=crop&q=80', // South Kitchen
  4: 'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800&auto=format&fit=crop&q=80', // Chinese Wok
  5: 'https://images.unsplash.com/photo-1559925393-8be0ec4767c8?w=800&auto=format&fit=crop&q=80', // Sweet Treats
  6: 'https://images.unsplash.com/photo-1544025162-d76694265947?w=800&auto=format&fit=crop&q=80', // Grill House
};

// 2. Exact Menu Item Image Mappings (By Normalized Dish Name & ID)
export const MENU_ITEM_IMAGES_BY_NAME = {
  // Spice Garden (Biryani & Starters)
  'chicken biryani': 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop&q=80',
  'veg biryani': 'https://images.unsplash.com/photo-1642821373181-696a54913e93?w=800&auto=format&fit=crop&q=80',
  'chicken 65': 'https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?w=800&auto=format&fit=crop&q=80',
  'paneer tikka': 'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=800&auto=format&fit=crop&q=80',

  // Pizza Palace (Pizza & Pasta)
  'margherita pizza': 'https://images.unsplash.com/photo-1604382355076-af4b0eb60143?w=800&auto=format&fit=crop&q=80',
  'farmhouse pizza': 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=800&auto=format&fit=crop&q=80',
  'chicken pizza': 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800&auto=format&fit=crop&q=80',
  'white sauce pasta': 'https://images.unsplash.com/photo-1621996346565-e3d5d6281691?w=800&auto=format&fit=crop&q=80',

  // South Kitchen (Breakfast & Meals)
  'masala dosa': 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=800&auto=format&fit=crop&q=80',
  'idli vada': 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=800&auto=format&fit=crop&q=80',
  'veg meals': 'https://images.unsplash.com/photo-1610192244261-3f33de3f55e4?w=800&auto=format&fit=crop&q=80',
  'curd rice': 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=800&auto=format&fit=crop&q=80',

  // Chinese Wok (Rice, Noodles & Starters)
  'veg fried rice': 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=800&auto=format&fit=crop&q=80',
  'chicken fried rice': 'https://images.unsplash.com/photo-1512058564366-18510be2db19?w=800&auto=format&fit=crop&q=80',
  'veg hakka noodles': 'https://images.unsplash.com/photo-1612927601601-6638404737ce?w=800&auto=format&fit=crop&q=80',
  'chilli chicken': 'https://images.unsplash.com/photo-1567620832903-9fc6debc209f?w=800&auto=format&fit=crop&q=80',

  // Sweet Treats (Desserts & Beverages)
  'chocolate cake': 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&auto=format&fit=crop&q=80',
  'brownie': 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=800&auto=format&fit=crop&q=80',
  'vanilla milkshake': 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=800&auto=format&fit=crop&q=80',
  'gulab jamun': 'https://images.unsplash.com/photo-1667207604593-1815e96a4141?w=800&auto=format&fit=crop&q=80',

  // Grill House (Grill, Kebabs & Wraps)
  'grilled chicken': 'https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=800&auto=format&fit=crop&q=80',
  'chicken kebab': 'https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=800&auto=format&fit=crop&q=80',
  'chicken wrap': 'https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=800&auto=format&fit=crop&q=80',
  'paneer wrap': 'https://images.unsplash.com/photo-1509722747041-616f39b57569?w=800&auto=format&fit=crop&q=80',
};

export const MENU_ITEM_IMAGES_BY_ID = {
  1: 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop&q=80', // Chicken Biryani
  2: 'https://images.unsplash.com/photo-1642821373181-696a54913e93?w=800&auto=format&fit=crop&q=80', // Veg Biryani
  3: 'https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?w=800&auto=format&fit=crop&q=80', // Chicken 65
  4: 'https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8?w=800&auto=format&fit=crop&q=80', // Paneer Tikka
  5: 'https://images.unsplash.com/photo-1604382355076-af4b0eb60143?w=800&auto=format&fit=crop&q=80', // Margherita Pizza
  6: 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=800&auto=format&fit=crop&q=80', // Farmhouse Pizza
  7: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800&auto=format&fit=crop&q=80', // Chicken Pizza
  8: 'https://images.unsplash.com/photo-1621996346565-e3d5d6281691?w=800&auto=format&fit=crop&q=80', // White Sauce Pasta
  9: 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=800&auto=format&fit=crop&q=80', // Masala Dosa
  10: 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=800&auto=format&fit=crop&q=80', // Idli Vada
  11: 'https://images.unsplash.com/photo-1610192244261-3f33de3f55e4?w=800&auto=format&fit=crop&q=80', // Veg Meals
  12: 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=800&auto=format&fit=crop&q=80', // Curd Rice
  13: 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=800&auto=format&fit=crop&q=80', // Veg Fried Rice
  14: 'https://images.unsplash.com/photo-1512058564366-18510be2db19?w=800&auto=format&fit=crop&q=80', // Chicken Fried Rice
  15: 'https://images.unsplash.com/photo-1612927601601-6638404737ce?w=800&auto=format&fit=crop&q=80', // Veg Hakka Noodles
  16: 'https://images.unsplash.com/photo-1567620832903-9fc6debc209f?w=800&auto=format&fit=crop&q=80', // Chilli Chicken
  17: 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&auto=format&fit=crop&q=80', // Chocolate Cake
  18: 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=800&auto=format&fit=crop&q=80', // Brownie
  19: 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=800&auto=format&fit=crop&q=80', // Vanilla Milkshake
  20: 'https://images.unsplash.com/photo-1667207604593-1815e96a4141?w=800&auto=format&fit=crop&q=80', // Gulab Jamun
  21: 'https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=800&auto=format&fit=crop&q=80', // Grilled Chicken
  22: 'https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=800&auto=format&fit=crop&q=80', // Chicken Kebab
  23: 'https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=800&auto=format&fit=crop&q=80', // Chicken Wrap
  24: 'https://images.unsplash.com/photo-1509722747041-616f39b57569?w=800&auto=format&fit=crop&q=80', // Paneer Wrap
};

// 3. Fallback Category Images (For any dynamic or newly added food items)
export const CATEGORY_FALLBACK_IMAGES = {
  biryani: 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop&q=80',
  pizza: 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=800&auto=format&fit=crop&q=80',
  pasta: 'https://images.unsplash.com/photo-1621996346565-e3d5d6281691?w=800&auto=format&fit=crop&q=80',
  burger: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800&auto=format&fit=crop&q=80',
  breakfast: 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=800&auto=format&fit=crop&q=80',
  dosa: 'https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=800&auto=format&fit=crop&q=80',
  idli: 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=800&auto=format&fit=crop&q=80',
  meals: 'https://images.unsplash.com/photo-1610192244261-3f33de3f55e4?w=800&auto=format&fit=crop&q=80',
  rice: 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=800&auto=format&fit=crop&q=80',
  noodles: 'https://images.unsplash.com/photo-1612927601601-6638404737ce?w=800&auto=format&fit=crop&q=80',
  chinese: 'https://images.unsplash.com/photo-1541696432-82c6da8ce7bf?w=800&auto=format&fit=crop&q=80',
  starter: 'https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?w=800&auto=format&fit=crop&q=80',
  starters: 'https://images.unsplash.com/photo-1610057099443-fde8c4d50f91?w=800&auto=format&fit=crop&q=80',
  grill: 'https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=800&auto=format&fit=crop&q=80',
  wraps: 'https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=800&auto=format&fit=crop&q=80',
  wrap: 'https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=800&auto=format&fit=crop&q=80',
  kebab: 'https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=800&auto=format&fit=crop&q=80',
  desserts: 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&auto=format&fit=crop&q=80',
  dessert: 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&auto=format&fit=crop&q=80',
  cake: 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800&auto=format&fit=crop&q=80',
  brownie: 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=800&auto=format&fit=crop&q=80',
  beverages: 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=800&auto=format&fit=crop&q=80',
  beverage: 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=800&auto=format&fit=crop&q=80',
  drink: 'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=800&auto=format&fit=crop&q=80',
  shake: 'https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=800&auto=format&fit=crop&q=80',
  default: 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800&auto=format&fit=crop&q=80',
};

// 4. Popular Category Filter Pills matching actual database records
export const POPULAR_CATEGORIES = [
  'All',
  'Biryani',
  'Pizza',
  'Pasta',
  'Starters',
  'Breakfast',
  'Meals',
  'Rice',
  'Noodles',
  'Grill',
  'Wraps',
  'Desserts',
  'Beverages',
];

// Fallback restaurant exterior array
export const DEFAULT_RESTAURANT_IMAGES = [
  'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop&q=80',
  'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800&auto=format&fit=crop&q=80',
  'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop&q=80',
  'https://images.unsplash.com/photo-1550966871-3ed3cdb5ed0c?w=800&auto=format&fit=crop&q=80',
  'https://images.unsplash.com/photo-1544025162-d76694265947?w=800&auto=format&fit=crop&q=80',
  'https://images.unsplash.com/photo-1537047902294-62a40c20a6ae?w=800&auto=format&fit=crop&q=80',
];

/**
 * Resolves the most realistic image for a restaurant:
 * 1. Checks if a custom imageUrl is provided.
 * 2. Matches against known restaurant names (e.g. "Spice Garden", "Pizza Palace").
 * 3. Matches against known restaurant IDs (1..6).
 * 4. Deterministic fallback from DEFAULT_RESTAURANT_IMAGES array.
 */
export const getRestaurantImage = (imageUrl, id = 1, restaurantName = '') => {
  if (imageUrl && typeof imageUrl === 'string' && imageUrl.trim().startsWith('http')) {
    return imageUrl.trim();
  }

  // Check by normalized restaurant name
  if (restaurantName) {
    const cleanName = restaurantName.trim().toLowerCase();
    if (RESTAURANT_IMAGES_BY_NAME[cleanName]) {
      return RESTAURANT_IMAGES_BY_NAME[cleanName];
    }
  }

  // Check by restaurant ID
  const numId = Number(id);
  if (RESTAURANT_IMAGES_BY_ID[numId]) {
    return RESTAURANT_IMAGES_BY_ID[numId];
  }

  // Fallback array
  const index = Math.abs(numId || 0) % DEFAULT_RESTAURANT_IMAGES.length;
  return DEFAULT_RESTAURANT_IMAGES[index];
};

/**
 * Resolves the most realistic image for a menu item:
 * Supports:
 * - getMenuItemImage(item) -> object with { imageUrl, category, id/menuItemId, name/menuItemName }
 * - getMenuItemImage(id, itemName) -> (menuItemId, menuItemName)
 * - getMenuItemImage(imageUrl, category, id, itemName) -> full parameters
 */
export const getMenuItemImage = (arg1, arg2 = '', arg3 = null, arg4 = '') => {
  let imageUrl = null;
  let category = '';
  let id = null;
  let itemName = '';

  // 1. If passed an object
  if (arg1 && typeof arg1 === 'object') {
    imageUrl = arg1.imageUrl;
    category = arg1.category;
    id = arg1.id || arg1.menuItemId;
    itemName = arg1.name || arg1.menuItemName;
  }
  // 2. If called as (id, itemName)
  else if (
    (typeof arg1 === 'number' || (typeof arg1 === 'string' && !arg1.startsWith('http') && !isNaN(arg1))) &&
    arg3 === null
  ) {
    id = Number(arg1);
    itemName = typeof arg2 === 'string' ? arg2 : '';
  }
  // 3. If called with full arguments (imageUrl, category, id, itemName)
  else {
    imageUrl = arg1;
    category = arg2;
    id = arg3;
    itemName = arg4;
  }

  if (imageUrl && typeof imageUrl === 'string' && imageUrl.trim().startsWith('http')) {
    return imageUrl.trim();
  }

  // Check by exact or partial item name
  if (itemName && typeof itemName === 'string') {
    const cleanName = itemName.trim().toLowerCase();
    if (MENU_ITEM_IMAGES_BY_NAME[cleanName]) {
      return MENU_ITEM_IMAGES_BY_NAME[cleanName];
    }
    // Partial substring match on item name
    for (const [key, url] of Object.entries(MENU_ITEM_IMAGES_BY_NAME)) {
      if (cleanName.includes(key) || key.includes(cleanName)) {
        return url;
      }
    }
  }

  // Check by menu item ID
  if (id !== null && id !== undefined) {
    const numId = Number(id);
    if (MENU_ITEM_IMAGES_BY_ID[numId]) {
      return MENU_ITEM_IMAGES_BY_ID[numId];
    }
  }

  // Check by category keyword
  if (category && typeof category === 'string') {
    const cleanCat = category.trim().toLowerCase();
    for (const [key, url] of Object.entries(CATEGORY_FALLBACK_IMAGES)) {
      if (cleanCat.includes(key)) {
        return url;
      }
    }
  }

  return CATEGORY_FALLBACK_IMAGES.default;
};

/**
 * Formats monetary amounts in Indian Rupee format (₹)
 */
export const formatPrice = (amount) => {
  const num = Number(amount) || 0;
  return `₹${num.toFixed(2)}`;
};
