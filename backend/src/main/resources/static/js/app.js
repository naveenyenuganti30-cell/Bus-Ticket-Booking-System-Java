/**
 * SeatReserve - Luxury Intercity Bus Ticket Booking System
 * Dynamic Frontend Application Logic
 */

// Application State
const state = {
  token: localStorage.getItem('sr_token') || null,
  user: JSON.parse(localStorage.getItem('sr_user') || 'null'),
  currentTab: 'search',
  allJourneys: [],
  filteredJourneys: [],
  activeFilter: 'all',
  selectedJourney: null,
  journeySeats: null,
  currentDeck: 'LOWER',
  selectedSeats: new Map(), // journeySeatId -> seatObject
  appliedPromo: null,
  fareBreakdown: null,
  authMode: 'login' // 'login' or 'register'
};

// API Base URL
const API_BASE = '/api';

// Helper for HTTP requests
async function apiFetch(endpoint, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };
  if (state.token) {
    headers['Authorization'] = `Bearer ${state.token}`;
  }
  const config = {
    ...options,
    headers
  };
  try {
    const response = await fetch(`${API_BASE}${endpoint}`, config);
    const contentType = response.headers.get('content-type');
    let data = null;
    if (contentType && contentType.includes('application/json')) {
      data = await response.json();
    } else {
      data = await response.text();
    }
    if (!response.ok) {
      const errMsg = (data && data.message) ? data.message : (typeof data === 'string' && data ? data : `Error ${response.status}: ${response.statusText}`);
      throw new Error(errMsg);
    }
    return data;
  } catch (err) {
    console.error(`API Error on ${endpoint}:`, err);
    throw err;
  }
}

// ==========================================
// Toast Notification Utility
// ==========================================
function showToast(message, type = 'info') {
  const container = document.getElementById('toastContainer');
  if (!container) return;
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.innerHTML = `<span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

// ==========================================
// Initialization on Page Load
// ==========================================
document.addEventListener('DOMContentLoaded', async () => {
  initDateInput();
  updateAuthUI();
  await loadCityOptions();
  // Perform an automatic initial search for Bangalore to Hyderabad
  quickFillRoute('Bangalore', 'Hyderabad');
});

function initDateInput() {
  const dateInput = document.getElementById('travelDateInput');
  if (dateInput) {
    const today = new Date().toISOString().split('T')[0];
    dateInput.min = today;
    dateInput.value = today;
  }
}

// Populate Source & Destination Datalists
async function loadCityOptions() {
  try {
    const [sourceCities, destCities] = await Promise.all([
      apiFetch('/routes/cities/source'),
      apiFetch('/routes/cities/destination')
    ]);

    const srcDatalist = document.getElementById('sourceCities');
    if (srcDatalist && Array.isArray(sourceCities)) {
      srcDatalist.innerHTML = sourceCities.map(c => `<option value="${c}">`).join('');
    }

    const dstDatalist = document.getElementById('destinationCities');
    if (dstDatalist && Array.isArray(destCities)) {
      dstDatalist.innerHTML = destCities.map(c => `<option value="${c}">`).join('');
    }
  } catch (err) {
    console.warn('Could not load city list, using fallback:', err);
    const defaults = ['Bangalore', 'Hyderabad', 'Chennai', 'Mumbai', 'Pune', 'Delhi', 'Jaipur'];
    const src = document.getElementById('sourceCities');
    const dst = document.getElementById('destinationCities');
    if (src) src.innerHTML = defaults.map(c => `<option value="${c}">`).join('');
    if (dst) dst.innerHTML = defaults.map(c => `<option value="${c}">`).join('');
  }
}

// Quick fill popular route
function quickFillRoute(source, destination) {
  const srcInput = document.getElementById('sourceInput');
  const dstInput = document.getElementById('destinationInput');
  if (srcInput) srcInput.value = source;
  if (dstInput) dstInput.value = destination;
  
  // Trigger search automatically
  const searchForm = document.getElementById('searchForm');
  if (searchForm) {
    searchForm.dispatchEvent(new Event('submit'));
  }
}

// Swap source and destination
function swapCities() {
  const src = document.getElementById('sourceInput');
  const dst = document.getElementById('destinationInput');
  if (src && dst) {
    const temp = src.value;
    src.value = dst.value;
    dst.value = temp;
  }
}

// ==========================================
// Navigation & Tab Switching
// ==========================================
function switchTab(tabId) {
  state.currentTab = tabId;

  // Nav buttons
  document.querySelectorAll('.nav-btn').forEach(btn => btn.classList.remove('active'));
  const activeBtn = document.getElementById(`nav${tabId.charAt(0).toUpperCase() + tabId.slice(1)}`);
  if (activeBtn) activeBtn.classList.add('active');

  // Tab content panels
  document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
  const activePanel = document.getElementById(`tab${tabId.charAt(0).toUpperCase() + tabId.slice(1)}`);
  if (activePanel) activePanel.classList.add('active');

  // Trigger tab-specific loads
  if (tabId === 'admin') {
    loadAdminDashboard();
  }
}

// ==========================================
// Search Buses & Render Results
// ==========================================
async function handleSearch(event) {
  if (event) event.preventDefault();

  const source = document.getElementById('sourceInput').value.trim();
  const destination = document.getElementById('destinationInput').value.trim();
  const travelDate = document.getElementById('travelDateInput').value;

  if (!source || !destination) {
    showToast('Please specify both source and destination cities', 'error');
    return;
  }

  const busesList = document.getElementById('busesList');
  busesList.innerHTML = `
    <div class="empty-state">
      <div class="loader-spinner"></div>
      <h3>Finding available luxury coaches...</h3>
      <p>Querying scheduled departures from ${source} to ${destination}</p>
    </div>
  `;

  try {
    const journeys = await apiFetch(`/journeys/search?source=${encodeURIComponent(source)}&destination=${encodeURIComponent(destination)}&date=${encodeURIComponent(travelDate)}`);
    state.allJourneys = journeys || [];
    filterBuses(state.activeFilter);

    // Update results header
    const heading = document.getElementById('resultsHeading');
    const subheading = document.getElementById('resultsSubheading');
    if (heading) heading.textContent = `${source} ➔ ${destination}`;
    if (subheading) {
      const formattedDate = new Date(travelDate).toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric', year: 'numeric' });
      subheading.textContent = `${state.allJourneys.length} scheduled services available on ${formattedDate}`;
    }

    // Scroll to results
    const resultsSection = document.getElementById('resultsSection');
    if (resultsSection) {
      resultsSection.scrollIntoView({ behavior: 'smooth' });
    }
  } catch (err) {
    busesList.innerHTML = `
      <div class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
        <h3>No direct buses found</h3>
        <p>${err.message || 'Try selecting Bangalore to Hyderabad, Mumbai to Pune, or Delhi to Jaipur.'}</p>
      </div>
    `;
    showToast(err.message, 'error');
  }
}

function filterBuses(filterType) {
  state.activeFilter = filterType;
  document.querySelectorAll('.filter-chip').forEach(chip => chip.classList.remove('active'));
  event?.target?.classList?.add('active');

  if (filterType === 'sleeper') {
    state.filteredJourneys = state.allJourneys.filter(j => (j.busType && j.busType.includes('SLEEPER')) || (j.busTypeDisplay && j.busTypeDisplay.includes('Sleeper')));
  } else if (filterType === 'semi') {
    state.filteredJourneys = state.allJourneys.filter(j => (j.busType && !j.busType.includes('SLEEPER')) && (!j.busTypeDisplay || !j.busTypeDisplay.includes('Sleeper')));
  } else {
    state.filteredJourneys = [...state.allJourneys];
  }

  renderBusesList();
}

function renderBusesList() {
  const container = document.getElementById('busesList');
  if (!container) return;

  if (state.filteredJourneys.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.5 2.8C2.1 10.8 2 10.9 2 11v5c0 .6.4 1 1 1h2"/><circle cx="7" cy="17" r="2"/><circle cx="17" cy="17" r="2"/></svg>
        <h3>No services match the selected filter</h3>
        <p>Try switching to "All Buses" to see all departures.</p>
      </div>
    `;
    return;
  }

  container.innerHTML = state.filteredJourneys.map(journey => {
    const journeyId = journey.id || journey.journeyId;
    const depTime = formatTime(journey.departureDateTime);
    const arrTime = formatTime(journey.arrivalDateTime);
    const duration = calculateDuration(journey.departureDateTime, journey.arrivalDateTime);
    const amenities = (journey.amenities && journey.amenities.length > 0)
      ? journey.amenities
      : ['WiFi', 'Air Conditioned', 'Charging Port', 'Reading Light', 'Blanket', 'Live Tracking'];

    return `
      <div class="bus-card" id="journey-card-${journeyId}">
        <div class="bus-card-main">
          <div class="bus-info-col">
            <div class="bus-operator-tag">SeatReserve Express Line</div>
            <h3 class="bus-title">${journey.busName || journey.vehicleModel || 'Volvo Multi-Axle'}</h3>
            <span class="bus-type-pill">${journey.busTypeDisplay || formatBusType(journey.busType)}</span>
            <div class="bus-amenities">
              ${amenities.slice(0, 4).map(a => `<span class="amenity-badge">✓ ${a}</span>`).join('')}
            </div>
          </div>

          <div class="bus-timings">
            <div class="time-box">
              <span class="time-val">${depTime}</span>
              <span class="time-city">${journey.sourceCity}</span>
            </div>
            <div class="trip-duration">
              <span class="duration-text">${duration}</span>
              <div class="duration-line"></div>
              <span class="duration-text">Direct Highway Express</span>
            </div>
            <div class="time-box">
              <span class="time-val">${arrTime}</span>
              <span class="time-city">${journey.destinationCity}</span>
            </div>
          </div>

          <div class="bus-fare-col">
            <span class="starting-fare">Starts From</span>
            <span class="fare-price">₹${journey.baseFare?.toFixed(2) || '850.00'}</span>
            <span class="seats-avail-tag">${journey.availableSeats} Seats Left</span>
            <button class="btn btn-select-seats" onclick="openSeatModal(${journeyId})">
              Select Seats
            </button>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

function formatBusType(type) {
  if (!type) return 'Luxury AC';
  return type.replace(/_/g, ' ');
}

function formatTime(isoString) {
  if (!isoString) return '--:--';
  const d = new Date(isoString);
  return d.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });
}

function calculateDuration(startIso, endIso) {
  if (!startIso || !endIso) return '8h 00m';
  const diffMs = new Date(endIso) - new Date(startIso);
  const diffHrs = Math.floor(diffMs / (1000 * 60 * 60));
  const diffMins = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));
  return `${diffHrs}h ${diffMins > 0 ? diffMins + 'm' : ''}`;
}

// ==========================================
// Interactive Cabin Seat Selection Modal
// ==========================================
async function openSeatModal(journeyId) {
  state.selectedSeats.clear();
  state.appliedPromo = null;
  state.fareBreakdown = null;
  state.currentDeck = 'LOWER';

  const journey = state.allJourneys.find(j => (j.id || j.journeyId) === journeyId);
  state.selectedJourney = journey;

  // Set modal headers
  const titleEl = document.getElementById('modalBusTitle');
  const subtitleEl = document.getElementById('modalBusSubtitle');
  if (titleEl) titleEl.textContent = `${journey?.busName || journey?.vehicleModel || 'Express Bus'} (${journey?.busTypeDisplay || formatBusType(journey?.busType)})`;
  if (subtitleEl) subtitleEl.textContent = `${journey?.sourceCity} ➔ ${journey?.destinationCity} | Dep: ${formatTime(journey?.departureDateTime)}`;

  // Populate Boarding / Dropping Pickers
  populateStopPickers(journey);

  // Auto-fill passenger email/phone if user is logged in
  if (state.user) {
    const emailInput = document.getElementById('contactEmail');
    const phoneInput = document.getElementById('contactPhone');
    if (emailInput && !emailInput.value) emailInput.value = state.user.email || '';
    if (phoneInput && !phoneInput.value) phoneInput.value = state.user.phone || '+91 9876543210';
  }

  // Fetch live seats from API
  try {
    const seatsData = await apiFetch(`/journeys/${journeyId}/seats`);
    state.journeySeats = seatsData;

    // Check deck toggle availability
    const hasUpper = (seatsData.upperDeck && seatsData.upperDeck.length > 0) || (seatsData.upperDeckSeats && seatsData.upperDeckSeats.length > 0);
    const deckToggleContainer = document.getElementById('deckToggleContainer');
    if (deckToggleContainer) {
      deckToggleContainer.style.display = hasUpper ? 'flex' : 'none';
    }

    switchDeck('LOWER');
    updateSelectedSeatsUI();

    const modal = document.getElementById('seatModal');
    if (modal) modal.classList.remove('hidden');
  } catch (err) {
    showToast(`Failed to load cabin seats: ${err.message}`, 'error');
  }
}

function closeSeatModal() {
  const modal = document.getElementById('seatModal');
  if (modal) modal.classList.add('hidden');
}

function populateStopPickers(journey) {
  const pickupSelect = document.getElementById('pickupSelect');
  const dropoffSelect = document.getElementById('dropoffSelect');

  const boardingList = (journey?.boardingPoints && journey.boardingPoints.length > 0)
    ? journey.boardingPoints.map(b => typeof b === 'string' ? b : `${b.stopName} (${b.landmark || 'Main Point'})`)
    : [`${journey?.sourceCity} Main Bus Stand (09:00 PM)`, `${journey?.sourceCity} Electronic City Toll (09:30 PM)`];

  const droppingList = (journey?.dropoffPoints && journey.dropoffPoints.length > 0)
    ? journey.dropoffPoints.map(d => typeof d === 'string' ? d : `${d.stopName} (${d.landmark || 'Bypass Point'})`)
    : [`${journey?.destinationCity} Central Terminal (06:00 AM)`, `${journey?.destinationCity} Airport Bypass (06:30 AM)`];

  if (pickupSelect) {
    pickupSelect.innerHTML = boardingList.map(p => `<option value="${p}">${p}</option>`).join('');
  }
  if (dropoffSelect) {
    dropoffSelect.innerHTML = droppingList.map(d => `<option value="${d}">${d}</option>`).join('');
  }
}

function switchDeck(deck) {
  state.currentDeck = deck;
  document.getElementById('btnLowerDeck')?.classList.toggle('active', deck === 'LOWER');
  document.getElementById('btnUpperDeck')?.classList.toggle('active', deck === 'UPPER');
  renderCabinSeats();
}

function renderCabinSeats() {
  const grid = document.getElementById('seatsGrid');
  if (!grid || !state.journeySeats) return;

  const seats = state.currentDeck === 'LOWER' 
    ? (state.journeySeats.lowerDeck || state.journeySeats.lowerDeckSeats || []) 
    : (state.journeySeats.upperDeck || state.journeySeats.upperDeckSeats || []);

  if (seats.length === 0) {
    grid.innerHTML = `<div class="empty-tip text-center">No seats configured on this deck.</div>`;
    return;
  }

  // Group seats by seatRow (or rowNumber)
  const rowsMap = new Map();
  seats.forEach(seat => {
    const row = seat.seatRow || seat.rowNumber || 1;
    if (!rowsMap.has(row)) rowsMap.set(row, []);
    rowsMap.get(row).push(seat);
  });

  // Sort rows
  const sortedRows = Array.from(rowsMap.keys()).sort((a, b) => a - b);

  grid.innerHTML = sortedRows.map(rowNum => {
    const rowSeats = rowsMap.get(rowNum).sort((a, b) => (a.seatCol || a.columnNumber || 1) - (b.seatCol || b.columnNumber || 1));
    return `
      <div class="bus-row">
        ${rowSeats.map((seat, idx) => {
          const seatId = seat.journeySeatId || seat.seatId;
          const isSelected = state.selectedSeats.has(seatId);
          const isBooked = (seat.status !== 'AVAILABLE');
          const isSleeper = (seat.seatType === 'SLEEPER');
          const isWindow = (seat.position === 'WINDOW');
          
          let seatClasses = `seat-item ${isSleeper ? 'sleeper' : 'seater'}`;
          if (isBooked) seatClasses += ' booked';
          if (isSelected) seatClasses += ' selected';

          // Aisle spacing after column 1 or 2 depending on layout
          const addAisleSpacer = (idx === 0 && rowSeats.length > 2);

          return `
            <div class="${seatClasses}" 
                 onclick="toggleSeatSelection(${seatId})" 
                 title="Seat ${seat.seatNumber} - ₹${seat.price?.toFixed(2) || '900'} ${isBooked ? '(Booked)' : '(Available)'}">
              ${isWindow ? '<span class="seat-window-badge">W</span>' : ''}
              <span class="seat-num">${seat.seatNumber}</span>
              <span class="seat-price-tag">₹${Math.round(seat.price || 900)}</span>
            </div>
            ${addAisleSpacer ? '<div class="seat-aisle-spacer"></div>' : ''}
          `;
        }).join('')}
      </div>
    `;
  }).join('');
}

function toggleSeatSelection(seatId) {
  const allDeckSeats = [
    ...(state.journeySeats?.lowerDeck || state.journeySeats?.lowerDeckSeats || []),
    ...(state.journeySeats?.upperDeck || state.journeySeats?.upperDeckSeats || [])
  ];
  const seat = allDeckSeats.find(s => (s.journeySeatId || s.seatId) === seatId);
  if (!seat || seat.status !== 'AVAILABLE') return;

  if (state.selectedSeats.has(seatId)) {
    state.selectedSeats.delete(seatId);
  } else {
    if (state.selectedSeats.size >= 6) {
      showToast('Maximum 6 seats allowed per reservation', 'error');
      return;
    }
    state.selectedSeats.set(seatId, seat);
  }

  renderCabinSeats();
  updateSelectedSeatsUI();
  recalculateFare();
}

function updateSelectedSeatsUI() {
  const chipsContainer = document.getElementById('selectedSeatsList');
  const passengersContainer = document.getElementById('passengersFormContainer');
  const confirmBtn = document.getElementById('btnConfirmBooking');

  const selectedArray = Array.from(state.selectedSeats.values());

  if (selectedArray.length === 0) {
    chipsContainer.innerHTML = `<span class="empty-tip">No seat selected yet. Click any available seat in the bus cabin.</span>`;
    passengersContainer.innerHTML = '';
    if (confirmBtn) confirmBtn.disabled = true;
    updateFareDisplay({
      subtotalBaseFare: 0,
      totalSeatSurcharges: 0,
      taxAmount: 0,
      discountAmount: 0,
      grandTotal: 0
    });
    return;
  }

  if (confirmBtn) confirmBtn.disabled = false;

  // Render selected seat chips
  chipsContainer.innerHTML = selectedArray.map(s => {
    const sId = s.journeySeatId || s.seatId;
    return `
      <div class="selected-chip">
        <span>Seat ${s.seatNumber} (${s.deck})</span>
        <span class="chip-remove" onclick="toggleSeatSelection(${sId})">&times;</span>
      </div>
    `;
  }).join('');

  // Preserve existing passenger inputs if available
  const existingValues = [];
  passengersContainer.querySelectorAll('.passenger-row-card').forEach(card => {
    existingValues.push({
      seatId: parseInt(card.dataset.seatId),
      name: card.querySelector('.pax-name')?.value || '',
      age: card.querySelector('.pax-age')?.value || '',
      gender: card.querySelector('.pax-gender')?.value || 'MALE'
    });
  });

  // Render passenger input forms
  passengersContainer.innerHTML = selectedArray.map((seat, index) => {
    const sId = seat.journeySeatId || seat.seatId;
    const saved = existingValues.find(v => v.seatId === sId);
    const defaultName = (index === 0 && state.user?.fullName) ? state.user.fullName : (saved?.name || '');
    const defaultAge = saved?.age || (index === 0 ? '28' : '25');
    const defaultGender = saved?.gender || 'MALE';

    return `
      <div class="passenger-row-card" data-seat-id="${sId}">
        <div class="pax-header">Passenger ${index + 1} (Seat ${seat.seatNumber})</div>
        <div class="pax-fields">
          <input type="text" class="pax-name" placeholder="Full Name" value="${defaultName}" required>
          <input type="number" class="pax-age" placeholder="Age" min="1" max="100" value="${defaultAge}" required>
          <select class="pax-gender">
            <option value="MALE" ${defaultGender === 'MALE' ? 'selected' : ''}>Male</option>
            <option value="FEMALE" ${defaultGender === 'FEMALE' ? 'selected' : ''}>Female</option>
            <option value="OTHER" ${defaultGender === 'OTHER' ? 'selected' : ''}>Other</option>
          </select>
        </div>
      </div>
    `;
  }).join('');
}

// ==========================================
// Fare Calculation & Promo Codes
// ==========================================
async function recalculateFare() {
  const seatIds = Array.from(state.selectedSeats.keys());
  if (seatIds.length === 0) return;

  try {
    const payload = {
      journeyId: state.selectedJourney.id || state.selectedJourney.journeyId,
      seatIds: seatIds,
      promoCode: state.appliedPromo || null
    };
    const fare = await apiFetch('/fares/calculate', {
      method: 'POST',
      body: JSON.stringify(payload)
    });
    state.fareBreakdown = fare;
    updateFareDisplay(fare);
  } catch (err) {
    console.error('Fare calculation error:', err);
    // Fallback calculation locally
    const baseTotal = seatIds.reduce((sum, id) => sum + (state.selectedSeats.get(id)?.price || 900), 0);
    const tax = baseTotal * 0.05;
    const discount = state.appliedPromo ? baseTotal * 0.1 : 0;
    updateFareDisplay({
      subtotalBaseFare: baseTotal,
      totalSeatSurcharges: 0,
      taxAmount: tax,
      discountAmount: discount,
      grandTotal: baseTotal + tax - discount
    });
  }
}

function updateFareDisplay(fare) {
  const base = fare.subtotalBaseFare ?? fare.baseFareTotal ?? 0;
  const surcharges = fare.totalSeatSurcharges ?? fare.seatSurchargesTotal ?? 0;
  const tax = fare.taxAmount ?? 0;
  const discount = fare.discountAmount ?? 0;
  const grand = fare.grandTotal ?? 0;

  document.getElementById('fareBase').textContent = `₹${base.toFixed(2)}`;
  document.getElementById('fareSurcharges').textContent = `₹${surcharges.toFixed(2)}`;
  document.getElementById('fareTax').textContent = `₹${tax.toFixed(2)}`;
  document.getElementById('fareDiscount').textContent = `-₹${discount.toFixed(2)}`;
  document.getElementById('fareGrandTotal').textContent = `₹${grand.toFixed(2)}`;
}

async function applyPromoCode() {
  const input = document.getElementById('promoCodeInput');
  const msgEl = document.getElementById('promoMessage');
  const code = input.value.trim().toUpperCase();

  if (!code) {
    state.appliedPromo = null;
    msgEl.textContent = '';
    recalculateFare();
    return;
  }

  const validCodes = ['SAVE10', 'FIRST50', 'FESTIVE15'];
  if (!validCodes.includes(code)) {
    msgEl.className = 'promo-msg error';
    msgEl.textContent = `❌ Invalid code. Try SAVE10, FIRST50, or FESTIVE15`;
    return;
  }

  state.appliedPromo = code;
  msgEl.className = 'promo-msg success';
  msgEl.textContent = `✓ Promo ${code} applied successfully!`;
  await recalculateFare();
  showToast(`Promo ${code} applied!`, 'success');
}

// ==========================================
// Booking Submission & E-Ticket Generation
// ==========================================
async function submitBooking() {
  const confirmBtn = document.getElementById('btnConfirmBooking');
  const seatIds = Array.from(state.selectedSeats.keys());

  if (seatIds.length === 0) {
    showToast('Please select at least one seat', 'error');
    return;
  }

  const email = document.getElementById('contactEmail').value.trim();
  const phone = document.getElementById('contactPhone').value.trim();
  const pickup = document.getElementById('pickupSelect').value;
  const dropoff = document.getElementById('dropoffSelect').value;

  if (!email || !phone) {
    showToast('Please enter passenger contact email and phone', 'error');
    return;
  }

  // Collect passengers
  const passengers = [];
  let formValid = true;
  document.querySelectorAll('.passenger-row-card').forEach(card => {
    const seatId = parseInt(card.dataset.seatId);
    const fullName = card.querySelector('.pax-name').value.trim();
    const age = parseInt(card.querySelector('.pax-age').value);
    const gender = card.querySelector('.pax-gender').value;

    if (!fullName || !age) {
      formValid = false;
    }
    passengers.push({ journeySeatId: seatId, passengerName: fullName, age, gender });
  });

  if (!formValid) {
    showToast('Please fill full name and valid age for all passengers', 'error');
    return;
  }

  confirmBtn.disabled = true;
  confirmBtn.textContent = 'Securing Seats with Pessimistic Lock...';

  const journeyId = state.selectedJourney.id || state.selectedJourney.journeyId;
  const payload = {
    journeyScheduleId: journeyId,
    passengerName: passengers[0]?.passengerName || email,
    passengerEmail: email,
    passengerPhone: phone,
    pickupStop: pickup,
    dropoffStop: dropoff,
    promoCode: state.appliedPromo || null,
    passengers: passengers
  };

  try {
    const booking = await apiFetch('/bookings', {
      method: 'POST',
      body: JSON.stringify(payload)
    });

    closeSeatModal();
    renderPrintableTicket(booking);
    showToast('🎉 Booking Confirmed! E-Ticket issued.', 'success');
  } catch (err) {
    showToast(`Booking Failed: ${err.message}`, 'error');
  } finally {
    confirmBtn.disabled = false;
    confirmBtn.textContent = 'Confirm & Book Seats';
  }
}

function renderPrintableTicket(booking) {
  const container = document.getElementById('printableTicketArea');
  if (!container) return;

  const journey = state.selectedJourney;
  const busTitle = booking.busName || journey?.busName || journey?.vehicleModel || 'Orange Travels Volvo B11R AC Sleeper';
  const busType = booking.busType || journey?.busTypeDisplay || formatBusType(journey?.busType);
  const regNum = booking.registrationNumber || journey?.registrationNumber || 'KA-01-EXP-8899';
  const fromCity = booking.sourceCity || journey?.sourceCity || 'Bangalore';
  const toCity = booking.destinationCity || journey?.destinationCity || 'Hyderabad';
  const depTime = formatTime(booking.departureDateTime || journey?.departureDateTime);
  const arrTime = formatTime(booking.arrivalDateTime || journey?.arrivalDateTime);
  const depDate = booking.departureDateTime 
    ? new Date(booking.departureDateTime).toLocaleDateString('en-US', { weekday: 'long', month: 'short', day: 'numeric', year: 'numeric' }) 
    : 'Today';

  const pnr = booking.bookingReference || 'PNR-XXXXXX';
  const totalPaid = booking.totalFare ?? booking.totalAmount ?? 0;
  const ticketsList = booking.tickets || booking.passengers || [];

  container.innerHTML = `
    <div class="e-ticket-card">
      <div class="ticket-top">
        <div>
          <span class="field-label">Passenger Name Record</span>
          <div class="pnr-badge">${pnr}</div>
        </div>
        <div style="text-align: right;">
          <span class="field-label">Booking Status</span>
          <div style="color: var(--success); font-weight: 800; font-size: 1.1rem;">${booking.bookingStatus || booking.status || 'CONFIRMED'}</div>
        </div>
      </div>

      <div class="ticket-details-grid">
        <div class="ticket-field">
          <span class="field-label">Service & Bus</span>
          <span class="field-val">${busTitle} (${busType})</span>
        </div>
        <div class="ticket-field">
          <span class="field-label">Vehicle Registration</span>
          <span class="field-val">${regNum}</span>
        </div>
        <div class="ticket-field">
          <span class="field-label">Route</span>
          <span class="field-val">${fromCity} ➔ ${toCity}</span>
        </div>
        <div class="ticket-field">
          <span class="field-label">Departure Date & Time</span>
          <span class="field-val">${depDate} at ${depTime}</span>
        </div>
        <div class="ticket-field">
          <span class="field-label">Boarding Point</span>
          <span class="field-val">${booking.pickupStop || booking.boardingPoint || 'Main Terminal'}</span>
        </div>
        <div class="ticket-field">
          <span class="field-label">Dropping Point</span>
          <span class="field-val">${booking.dropoffStop || booking.droppingPoint || 'Central Bypass'}</span>
        </div>
      </div>

      <h4 style="margin-bottom: 0.5rem; font-size: 0.85rem; text-transform: uppercase; color: var(--text-muted);">Passenger Seat Allocation</h4>
      <table class="ticket-passengers-table">
        <thead>
          <tr>
            <th>Passenger Name</th>
            <th>Seat Number</th>
            <th>Age / Gender</th>
            <th>Ticket Number</th>
          </tr>
        </thead>
        <tbody>
          ${ticketsList.map(p => `
            <tr>
              <td><strong>${p.passengerName || p.fullName}</strong></td>
              <td><span class="selected-chip">${p.seatNumber}</span></td>
              <td>${p.age} yrs / ${p.gender}</td>
              <td><code>${p.ticketNumber || `TKT-${pnr}-${p.seatNumber}`}</code></td>
            </tr>
          `).join('')}
        </tbody>
      </table>

      <div class="ticket-footer-qr">
        <div>
          <span class="field-label">Total Amount Paid</span>
          <div style="font-size: 1.4rem; font-weight: 800; color: var(--accent);">₹${totalPaid.toFixed(2)}</div>
          <span style="font-size: 0.75rem; color: var(--text-muted);">Includes GST & State Passenger Tolls</span>
        </div>
        <div class="qr-code-graphic">
          <svg viewBox="0 0 100 100" fill="#0F172A">
            <rect x="5" y="5" width="25" height="25" fill="#0F172A" rx="2"/>
            <rect x="10" y="10" width="15" height="15" fill="#FFFFFF"/>
            <rect x="13" y="13" width="9" height="9" fill="#0F172A"/>
            <rect x="70" y="5" width="25" height="25" fill="#0F172A" rx="2"/>
            <rect x="75" y="10" width="15" height="15" fill="#FFFFFF"/>
            <rect x="78" y="13" width="9" height="9" fill="#0F172A"/>
            <rect x="5" y="70" width="25" height="25" fill="#0F172A" rx="2"/>
            <rect x="10" y="75" width="15" height="15" fill="#FFFFFF"/>
            <rect x="13" y="78" width="9" height="9" fill="#0F172A"/>
            <rect x="36" y="8" width="6" height="6"/>
            <rect x="48" y="8" width="6" height="6"/>
            <rect x="36" y="20" width="6" height="6"/>
            <rect x="55" y="24" width="6" height="6"/>
            <rect x="42" y="36" width="6" height="6"/>
            <rect x="54" y="42" width="6" height="6"/>
            <rect x="66" y="36" width="6" height="6"/>
            <rect x="36" y="54" width="6" height="6"/>
            <rect x="48" y="60" width="6" height="6"/>
            <rect x="66" y="66" width="6" height="6"/>
            <rect x="78" y="54" width="6" height="6"/>
            <rect x="42" y="78" width="6" height="6"/>
            <rect x="60" y="84" width="6" height="6"/>
            <rect x="84" y="78" width="6" height="6"/>
          </svg>
        </div>
      </div>
    </div>
  `;

  const modal = document.getElementById('ticketModal');
  if (modal) modal.classList.remove('hidden');
}

function closeTicketModal() {
  const modal = document.getElementById('ticketModal');
  if (modal) modal.classList.add('hidden');
}

// ==========================================
// PNR Status & Cancellation
// ==========================================
async function handlePnrLookup(event) {
  if (event) event.preventDefault();

  const pnr = document.getElementById('pnrInput').value.trim();
  const email = document.getElementById('pnrEmailInput').value.trim();
  const resultCard = document.getElementById('bookingDetailsCard');

  if (!pnr) {
    showToast('Please enter a PNR reference', 'error');
    return;
  }

  resultCard.innerHTML = `<div class="empty-state"><div class="loader-spinner"></div><h3>Searching booking record...</h3></div>`;

  try {
    let booking;
    if (email) {
      booking = await apiFetch(`/bookings/lookup?pnr=${encodeURIComponent(pnr)}&email=${encodeURIComponent(email)}`);
    } else {
      booking = await apiFetch(`/bookings/reference/${encodeURIComponent(pnr)}`);
    }
    renderBookingLookupResult(booking);
  } catch (err) {
    try {
      const booking = await apiFetch(`/bookings/reference/${encodeURIComponent(pnr)}`);
      renderBookingLookupResult(booking);
    } catch (e) {
      resultCard.innerHTML = `
        <div class="empty-state">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
          <h3>Booking Not Found</h3>
          <p>${err.message || 'Please check your PNR reference and registered email.'}</p>
        </div>
      `;
    }
  }
}

function renderBookingLookupResult(booking) {
  const resultCard = document.getElementById('bookingDetailsCard');
  if (!resultCard) return;

  const status = booking.bookingStatus || booking.status || 'CONFIRMED';
  const isCancelled = (status === 'CANCELLED');
  const statusColor = isCancelled ? 'var(--danger)' : 'var(--success)';
  const ticketsList = booking.tickets || booking.passengers || [];
  const totalPaid = booking.totalFare ?? booking.totalAmount ?? 0;
  const bookingId = booking.bookingId || booking.id;

  resultCard.innerHTML = `
    <div class="pnr-lookup-card">
      <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 1rem; margin-bottom: 1rem;">
        <div>
          <span class="field-label">PNR Reference</span>
          <h2 style="font-family: var(--font-display); color: var(--primary);">${booking.bookingReference}</h2>
        </div>
        <div style="text-align: right;">
          <span class="field-label">Status</span>
          <span style="font-weight: 800; color: ${statusColor}; font-size: 1.1rem;">${status}</span>
        </div>
      </div>

      <div class="ticket-details-grid">
        <div class="ticket-field">
          <span class="field-label">Route</span>
          <span class="field-val">${booking.sourceCity} ➔ ${booking.destinationCity}</span>
        </div>
        <div class="ticket-field">
          <span class="field-label">Departure</span>
          <span class="field-val">${booking.departureDateTime ? new Date(booking.departureDateTime).toLocaleString() : 'Scheduled'}</span>
        </div>
        <div class="ticket-field">
          <span class="field-label">Vehicle</span>
          <span class="field-val">${booking.busName || 'Express Bus'} (${booking.registrationNumber || ''})</span>
        </div>
        <div class="ticket-field">
          <span class="field-label">Contact Details</span>
          <span class="field-val">${booking.passengerEmail || booking.contactEmail || ''} | ${booking.passengerPhone || booking.contactPhone || ''}</span>
        </div>
      </div>

      <h4 style="margin: 1rem 0 0.5rem 0; font-size: 0.85rem; text-transform: uppercase; color: var(--text-muted);">Allocated Seats & Passengers</h4>
      <table class="ticket-passengers-table">
        <thead>
          <tr>
            <th>Passenger</th>
            <th>Seat</th>
            <th>Age/Gender</th>
            <th>Ticket Number</th>
          </tr>
        </thead>
        <tbody>
          ${ticketsList.map(p => `
            <tr>
              <td><strong>${p.passengerName || p.fullName}</strong></td>
              <td><span class="selected-chip">${p.seatNumber}</span></td>
              <td>${p.age} yrs / ${p.gender}</td>
              <td><code>${p.ticketNumber || `TKT-${booking.bookingReference}-${p.seatNumber}`}</code></td>
            </tr>
          `).join('')}
        </tbody>
      </table>

      <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 1.25rem; padding-top: 1rem; border-top: 1px solid var(--border);">
        <div>
          <span class="field-label">Total Booking Amount</span>
          <div style="font-size: 1.4rem; font-weight: 800; color: var(--accent);">₹${totalPaid.toFixed(2)}</div>
        </div>
        <div style="display: flex; gap: 0.75rem;">
          <button class="btn btn-outline" onclick='renderPrintableTicket(${JSON.stringify(booking)})'>
            Print E-Ticket
          </button>
          ${!isCancelled ? `
            <button class="btn btn-primary" style="background: var(--danger);" onclick="openCancelQuoteModal('${booking.bookingReference}', ${bookingId})">
              Cancel Booking
            </button>
          ` : `
            <span style="font-weight: 700; color: var(--danger); align-self: center;">Booking Cancelled & Refunded</span>
          `}
        </div>
      </div>
    </div>
  `;
}

async function openCancelQuoteModal(pnr, bookingId) {
  const modal = document.getElementById('cancelModal');
  const modalBody = document.getElementById('cancelModalBody');
  modalBody.innerHTML = `<div class="empty-state"><div class="loader-spinner"></div><h3>Calculating refund quote based on departure window...</h3></div>`;
  modal.classList.remove('hidden');

  try {
    const quote = await apiFetch(`/bookings/${pnr}/cancel-quote`);
    const originalPaid = quote.totalFare ?? quote.totalPaidAmount ?? 0;
    const cancelFee = quote.cancellationFeeAmount ?? quote.cancellationFee ?? 0;
    const refundAmt = quote.refundAmount ?? 0;

    modalBody.innerHTML = `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <div class="ticket-field">
          <span class="field-label">Booking Reference</span>
          <span class="field-val">${quote.bookingReference}</span>
        </div>
        <div class="fare-breakdown">
          <div class="fare-row"><span>Original Amount Paid:</span> <strong>₹${originalPaid.toFixed(2)}</strong></div>
          <div class="fare-row text-red"><span>Cancellation Fee:</span> <strong>-₹${cancelFee.toFixed(2)}</strong></div>
          <div class="fare-divider"></div>
          <div class="fare-row total text-green"><span>Estimated Refund:</span> <strong>₹${refundAmt.toFixed(2)}</strong></div>
        </div>
        <div class="demo-pnr-tip" style="margin: 0;">
          ℹ️ Policy: ${quote.policyDescription || quote.cancellationPolicySummary || 'Refund calculated automatically based on scheduled departure.'}
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 0.75rem; margin-top: 0.5rem;">
          <button class="btn btn-outline" onclick="closeCancelModal()">Keep Booking</button>
          <button class="btn btn-primary" style="background: var(--danger);" onclick="executeCancellation(${bookingId}, '${pnr}')">
            Confirm Cancellation
          </button>
        </div>
      </div>
    `;
  } catch (err) {
    modalBody.innerHTML = `
      <div class="empty-state">
        <h3>Unable to calculate quote</h3>
        <p>${err.message}</p>
        <button class="btn btn-outline" onclick="closeCancelModal()">Close</button>
      </div>
    `;
  }
}

function closeCancelModal() {
  const modal = document.getElementById('cancelModal');
  if (modal) modal.classList.add('hidden');
}

async function executeCancellation(bookingId, pnr) {
  try {
    const response = await apiFetch(`/bookings/${bookingId}/cancel`, { method: 'POST' });
    closeCancelModal();
    const refund = response.refundAmount || 0;
    showToast(`✓ Booking ${pnr} cancelled. Refund of ₹${refund.toFixed(2)} processed.`, 'success');
    // Refresh the booking view
    handlePnrLookup();
  } catch (err) {
    showToast(`Cancellation failed: ${err.message}`, 'error');
  }
}

// ==========================================
// Ticket Verification
// ==========================================
async function handleTicketVerify(event) {
  if (event) event.preventDefault();

  const ticketNum = document.getElementById('ticketNumInput').value.trim();
  const verifyArea = document.getElementById('ticketVerificationResult');

  if (!ticketNum) {
    showToast('Please enter ticket number to verify', 'error');
    return;
  }

  verifyArea.innerHTML = `<div class="empty-state"><div class="loader-spinner"></div><h3>Verifying ticket with cryptographic ledger...</h3></div>`;

  try {
    const ticket = await apiFetch(`/tickets/${encodeURIComponent(ticketNum)}`);
    verifyArea.innerHTML = `
      <div class="pnr-lookup-card" style="border-left: 5px solid var(--success);">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
          <div>
            <span class="field-label">Verification Result</span>
            <h3 style="color: var(--success); font-weight: 800;">✓ VALID CONFIRMED TICKET</h3>
          </div>
          <div style="text-align: right;">
            <span class="field-label">Ticket ID</span>
            <code>${ticket.ticketNumber}</code>
          </div>
        </div>

        <div class="ticket-details-grid">
          <div class="ticket-field">
            <span class="field-label">Passenger Name</span>
            <span class="field-val">${ticket.passengerName}</span>
          </div>
          <div class="ticket-field">
            <span class="field-label">Allocated Seat</span>
            <span class="field-val"><span class="selected-chip">${ticket.seatNumber}</span></span>
          </div>
          <div class="ticket-field">
            <span class="field-label">Booking Reference</span>
            <span class="field-val">${ticket.bookingReference}</span>
          </div>
          <div class="ticket-field">
            <span class="field-label">Bus Model & Type</span>
            <span class="field-val">${ticket.busName || ticket.busType}</span>
          </div>
          <div class="ticket-field">
            <span class="field-label">Route</span>
            <span class="field-val">${ticket.sourceCity || ''} ➔ ${ticket.destinationCity || ''}</span>
          </div>
          <div class="ticket-field">
            <span class="field-label">Scheduled Departure</span>
            <span class="field-val">${ticket.departureDateTime ? new Date(ticket.departureDateTime).toLocaleString() : 'On Time'}</span>
          </div>
        </div>

        <div style="margin-top: 1rem; padding-top: 0.75rem; border-top: 1px dashed var(--border); display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 0.8rem; color: var(--text-muted);">Security Checksum: <code>${ticket.qrPayload || ticket.qrValidationCode || 'SR-HASH-OK'}</code></span>
          <span style="font-size: 0.8rem; font-weight: 700; color: var(--success);">Boarding Permitted</span>
        </div>
      </div>
    `;
  } catch (err) {
    verifyArea.innerHTML = `
      <div class="pnr-lookup-card" style="border-left: 5px solid var(--danger);">
        <h3 style="color: var(--danger); font-weight: 800;">✗ INVALID OR EXPIRED TICKET</h3>
        <p style="color: var(--text-muted); margin-top: 0.5rem;">${err.message || 'Ticket not found in active manifest database.'}</p>
      </div>
    `;
  }
}

// ==========================================
// Admin Operations Dashboard
// ==========================================
async function loadAdminDashboard() {
  try {
    const stats = await apiFetch('/admin/dashboard');
    document.getElementById('statTotalBookings').textContent = stats.totalBookings || 0;
    document.getElementById('statConfirmed').textContent = stats.confirmedBookings || 0;
    document.getElementById('statCancelled').textContent = stats.cancelledBookings || 0;
    const netRev = stats.netRevenue ?? stats.netRealizedRevenue ?? 0;
    document.getElementById('statNetRevenue').textContent = `₹${netRev.toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;

    const bookings = await apiFetch('/admin/bookings');
    const tableBody = document.getElementById('adminBookingsTableBody');
    if (!tableBody) return;

    if (!bookings || bookings.length === 0) {
      tableBody.innerHTML = `<tr><td colspan="7" class="text-center">No bookings recorded yet.</td></tr>`;
      return;
    }

    tableBody.innerHTML = bookings.map(b => {
      const tickets = b.tickets || b.passengers || [];
      const pCount = tickets.length || 1;
      const seatNums = tickets.map(p => p.seatNumber).join(', ') || '-';
      const firstPax = (tickets.length > 0 && (tickets[0].passengerName || tickets[0].fullName)) 
        ? (tickets[0].passengerName || tickets[0].fullName) 
        : (b.passengerName || b.passengerEmail || b.contactEmail);
      const status = b.bookingStatus || b.status || 'CONFIRMED';
      const isConfirmed = (status === 'CONFIRMED');
      const totalFare = b.totalFare ?? b.totalAmount ?? 0;
      const src = b.sourceCity || b.journeySummary?.sourceCity || '';
      const dst = b.destinationCity || b.journeySummary?.destinationCity || '';

      return `
        <tr>
          <td><strong style="color: var(--primary);">${b.bookingReference}</strong></td>
          <td>${firstPax} ${pCount > 1 ? `<span style="font-size: 0.75rem; color: var(--text-muted);">(+${pCount - 1})</span>` : ''}</td>
          <td>${src} ➔ ${dst}</td>
          <td><span class="selected-chip" style="font-size: 0.75rem;">${seatNums}</span></td>
          <td><strong>₹${totalFare.toFixed(2)}</strong></td>
          <td>
            <span style="font-weight: 700; color: ${isConfirmed ? 'var(--success)' : 'var(--danger)'};">
              ${status}
            </span>
          </td>
          <td>${b.bookingDateTime ? new Date(b.bookingDateTime).toLocaleDateString() : 'Today'}</td>
        </tr>
      `;
    }).join('');
  } catch (err) {
    console.error('Error loading admin dashboard:', err);
    showToast(`Admin data error: ${err.message}`, 'error');
  }
}

// ==========================================
// Authentication & Quick Demo Login
// ==========================================
function updateAuthUI() {
  const guestView = document.getElementById('guestAuthView');
  const userView = document.getElementById('userAuthView');
  const avatar = document.getElementById('userAvatar');
  const nameLabel = document.getElementById('userNameLabel');
  const roleBadge = document.getElementById('userRoleBadge');

  if (state.token && state.user) {
    if (guestView) guestView.classList.add('hidden');
    if (userView) userView.classList.remove('hidden');
    if (avatar) avatar.textContent = (state.user.fullName || state.user.email || 'U').charAt(0).toUpperCase();
    if (nameLabel) nameLabel.textContent = state.user.fullName || state.user.email;
    if (roleBadge) roleBadge.textContent = state.user.role || 'ROLE_USER';
  } else {
    if (guestView) guestView.classList.remove('hidden');
    if (userView) userView.classList.add('hidden');
  }
}

async function quickDemoLogin(type = 'user') {
  const creds = (type === 'admin') 
    ? { email: 'admin@seatreserve.com', password: 'Admin@123' }
    : { email: 'demo@seatreserve.com', password: 'Demo@123' };

  try {
    const authData = await apiFetch('/auth/login', {
      method: 'POST',
      body: JSON.stringify(creds)
    });

    state.token = authData.token;
    state.user = {
      userId: authData.userId,
      email: authData.email,
      fullName: authData.fullName,
      role: authData.role
    };

    localStorage.setItem('sr_token', state.token);
    localStorage.setItem('sr_user', JSON.stringify(state.user));

    updateAuthUI();
    showToast(`Logged in as ${authData.fullName} (${authData.role})`, 'success');
  } catch (err) {
    showToast(`Quick demo login failed: ${err.message}`, 'error');
  }
}

function openLoginModal() {
  state.authMode = 'login';
  document.getElementById('authModalTitle').textContent = 'Sign In to SeatReserve';
  document.getElementById('fullNameGroup').style.display = 'none';
  document.getElementById('authSubmitBtn').textContent = 'Sign In';
  document.getElementById('authToggleText').textContent = "Don't have an account?";
  document.getElementById('authToggleBtn').textContent = 'Register';
  document.getElementById('authModal').classList.remove('hidden');
}

function toggleAuthMode() {
  state.authMode = (state.authMode === 'login') ? 'register' : 'login';
  const isRegister = (state.authMode === 'register');
  document.getElementById('authModalTitle').textContent = isRegister ? 'Create SeatReserve Account' : 'Sign In to SeatReserve';
  document.getElementById('fullNameGroup').style.display = isRegister ? 'block' : 'none';
  document.getElementById('authSubmitBtn').textContent = isRegister ? 'Create Account' : 'Sign In';
  document.getElementById('authToggleText').textContent = isRegister ? 'Already registered?' : "Don't have an account?";
  document.getElementById('authToggleBtn').textContent = isRegister ? 'Sign In' : 'Register';
}

function closeAuthModal() {
  document.getElementById('authModal').classList.add('hidden');
}

async function handleAuthSubmit(event) {
  event.preventDefault();
  const email = document.getElementById('authEmail').value.trim();
  const password = document.getElementById('authPassword').value;
  const fullName = document.getElementById('authFullName')?.value.trim();

  const endpoint = (state.authMode === 'register') ? '/auth/register' : '/auth/login';
  const payload = (state.authMode === 'register') 
    ? { email, password, fullName: fullName || 'Passenger', phone: '+91 9876543210' }
    : { email, password };

  try {
    const authData = await apiFetch(endpoint, {
      method: 'POST',
      body: JSON.stringify(payload)
    });

    state.token = authData.token;
    state.user = {
      userId: authData.userId,
      email: authData.email,
      fullName: authData.fullName,
      role: authData.role
    };

    localStorage.setItem('sr_token', state.token);
    localStorage.setItem('sr_user', JSON.stringify(state.user));

    updateAuthUI();
    closeAuthModal();
    showToast(`Welcome ${authData.fullName}!`, 'success');
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function logout() {
  state.token = null;
  state.user = null;
  localStorage.removeItem('sr_token');
  localStorage.removeItem('sr_user');
  updateAuthUI();
  showToast('Logged out successfully', 'info');
}
