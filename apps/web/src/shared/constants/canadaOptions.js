export const CATEGORIES = [
  "Cultural",
  "Food",
  "Nature",
  "Adventure",
  "City",
  "Historical"
];

export const LANGUAGES = [
  "English",
  "French",
  "Punjabi",
  "Hindi",
  "Mandarin",
  "Spanish",
  "Arabic"
];

export const PROVINCES = [
  "Alberta",
  "British Columbia",
  "Manitoba",
  "New Brunswick",
  "Newfoundland and Labrador",
  "Northwest Territories",
  "Nova Scotia",
  "Nunavut",
  "Ontario",
  "Prince Edward Island",
  "Quebec",
  "Saskatchewan",
  "Yukon"
];

export const CITIES_BY_PROVINCE = {
  Alberta: ["Calgary", "Edmonton", "Red Deer", "Lethbridge", "St. Albert", "Medicine Hat", "Grande Prairie", "Airdrie"],
  "British Columbia": [
    "Vancouver",
    "Victoria",
    "Surrey",
    "Burnaby",
    "Richmond",
    "Kelowna",
    "Abbotsford",
    "Kamloops",
    "Nanaimo",
    "Prince George"
  ],
  Manitoba: ["Winnipeg", "Brandon", "Steinbach", "Thompson", "Portage la Prairie", "Winkler", "Selkirk"],
  "New Brunswick": ["Fredericton", "Moncton", "Saint John", "Miramichi", "Dieppe", "Bathurst", "Edmundston"],
  "Newfoundland and Labrador": [
    "St. John's",
    "Mount Pearl",
    "Corner Brook",
    "Gander",
    "Grand Falls-Windsor",
    "Happy Valley-Goose Bay",
    "Conception Bay South"
  ],
  "Northwest Territories": ["Yellowknife", "Inuvik", "Hay River", "Fort Smith", "Norman Wells"],
  "Nova Scotia": ["Halifax", "Sydney", "Dartmouth", "Truro", "New Glasgow", "Kentville", "Amherst"],
  Nunavut: ["Iqaluit", "Rankin Inlet", "Cambridge Bay", "Arviat", "Baker Lake"],
  Ontario: ["Toronto", "Ottawa", "Mississauga", "Brampton", "Hamilton", "London", "Kitchener", "Waterloo", "Windsor", "Kingston", "Barrie", "Guelph"],
  "Prince Edward Island": ["Charlottetown", "Summerside", "Stratford", "Cornwall", "Montague"],
  Quebec: ["Montreal", "Quebec City", "Laval", "Gatineau", "Longueuil", "Sherbrooke", "Trois-Rivieres", "Saguenay", "Drummondville"],
  Saskatchewan: ["Saskatoon", "Regina", "Prince Albert", "Moose Jaw", "Swift Current", "Yorkton", "North Battleford"],
  Yukon: ["Whitehorse", "Dawson City", "Watson Lake", "Haines Junction", "Carmacks"]
};

const ALL_CANADA_CITIES = [...new Set(Object.values(CITIES_BY_PROVINCE).flat())]
  .sort((left, right) => left.localeCompare(right));

export const DEFAULT_PROVINCE = "";

export function citiesForProvince(province) {
  if (!province) {
    return [...ALL_CANADA_CITIES];
  }
  return CITIES_BY_PROVINCE[province] || [];
}
