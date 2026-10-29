# QuickGas LPG - Instant Refill & Cylinder Ordering

A super-minimalist, high-accessibility LPG cylinder ordering app designed for all generations to order 12kg, 35kg, and 45kg gas refills with the fewest clicks possible, dispatching complete orders directly to WhatsApp.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following product configurations were confirmed and incorporated into the design:
> - **Currency & Pricing**: Display prices in Bangladeshi Taka (৳ BDT) with editable unit prices in App Settings.
> - **Ordering Mode**: Both 1-tap **Quick Refill** (direct pop-up with remembered address) and **Cart Checkout** (for multiple cylinders/packages) are supported.
> - **WhatsApp & Business Management**: In-app management screen allows business owners or users to customize the WhatsApp dispatch number, hotline phone, delivery charge, and cylinder prices.

---

### 1. Overview & Core Concept

- **What It Does**: Enables households, restaurants, and businesses to order LPG gas cylinder refills (12kg, 35kg, 45kg) and new connection packages in under 10 seconds. Customers input their delivery address once; subsequent orders can be dispatched to WhatsApp in literally 1 tap.
- **Target Audience**: All generations—from seniors needing large, high-contrast buttons and simple text to busy restaurant managers ordering bulk 35kg/45kg cylinders.
- **Key Value**: Zero account registration barrier, zero manual copy-pasting, instant pre-filled WhatsApp message generation with phone intent fallbacks, and local memory of previous addresses and orders.

---

### 2. User Experience & Visual Design

#### Key User Flows
1. **Instant Refill (1-Tap Flow)**:
   - User sees 3 large cylinder cards on Home screen: **12 kg (Domestic)**, **35 kg (Commercial)**, **45 kg (Industrial)**.
   - User taps **"Quick Refill"** on their cylinder size.
   - If address is already saved, a clean confirmation sheet appears with one-click **"Send Order to WhatsApp"**.
   - App opens WhatsApp with the business number and the exact order text pre-filled.
2. **Custom Cart Flow**:
   - User taps **"Add to Cart"** or adjusts quantities (+ / -).
   - Bottom floating cart bar shows total cylinder count and estimated cost in ৳ BDT.
   - Cart modal allows adding new regulator/hose packages or urgent delivery notes, then launches WhatsApp checkout.
3. **Contact & Packages Section**:
   - Prominent cards for **"New Cylinder Packages"**, **"Call Delivery Hotline"**, and **"Emergency Leak Support"**.
4. **Settings & Customization**:
   - Easily modify the business WhatsApp number, delivery phone hotline, standard delivery charge, and cylinder prices.

#### Visual Identity & Theme
- **Aesthetic Direction**: High-legibility Warm Industrial Minimalist. High contrast, clean cards with generous padding, large touch targets (56dp+), clear status badges, and subtle flame/amber-teal accents.
- **Color Palette**:
  - *Primary*: Deep Flame Orange (`#E65100` / `#FF6D00`) conveying gas and energy.
  - *Secondary / Accent*: Emerald WhatsApp Green (`#25D366` / `#128C7E`) for order triggers and verification.
  - *Surfaces*: Pure White (`#FFFFFF`) / Warm Light Charcoal (`#F8F9FA`) in light mode; Slate Surface (`#1E2229`) in dark mode.
- **Typography & Ergonomics**:
  - Bold headline weights for weight numbers (`12 kg`, `35 kg`, `45 kg`).
  - Clear, uncompressed text fields with auto-save for customer name, phone, and delivery address.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Direct WhatsApp Intent with Automatic Encoding**:
  - *Chosen Approach*: Uses Android `ACTION_VIEW` intent with `https://api.whatsapp.com/send?phone=...&text=...` (and standard `whatsapp://` URI resolution).
  - *Why*: Provides 100% native deep linking without third-party SDK dependencies or server overhead. If WhatsApp isn't installed, falls back gracefully to system share or SMS.
- **Decision 2: Local Persistence (Room + DataStore)**:
  - *Chosen Approach*: Local Room database for order history and saved customer delivery profiles; DataStore/SharedPreferences for configurable business WhatsApp number and unit prices.
  - *Why*: Eliminates user login friction, works 100% offline, preserves data privacy, and fulfills the user request for an independent, zero-barrier app.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                   QuickGas LPG App                     │
├────────────────────────────────────────────────────────┤
│                      UI Layer                          │
│  ┌──────────────────┐ ┌────────────────┐ ┌───────────┐ │
│  │ CylinderCardGrid │ │ QuickOrderModal│ │ CartSheet │ │
│  │ (12/35/45 kg)    │ │ (1-Tap Refill) │ │ (Multiple)│ │
│  └─────────┬────────┘ └────────┬───────┘ └─────┬─────┘ │
│            │                   │               │       │
│  ┌─────────▼───────────────────▼───────────────▼─────┐ │
│  │                 GasOrderViewModel                 │ │
│  └─────────────────────────────┬─────────────────────┘ │
├────────────────────────────────┼───────────────────────┤
│                     Data & Domain Layer                │
│  ┌─────────────────────────────▼─────────────────────┐ │
│  │               LpgRepository & Manager             │ │
│  │  - Formats order payload message for WhatsApp     │ │
│  │  - Launches WhatsApp / SMS intents                │ │
│  └─────────┬───────────────────────────┬─────────────┘ │
│            ▼                           ▼               │
│  ┌──────────────────┐         ┌──────────────────────┐ │
│  │ Room Database    │         │ WhatsApp Dispatcher  │ │
│  │ (Saved Profiles, │         │ (wa.me/phone?text=   │ │
│  │ Order History)   │         │  URI Action Intent)  │ │
│  └──────────────────┘         └──────────────────────┘ │
└────────────────────────────────────────────────────────┘
```

#### Pre-formatted WhatsApp Message Template:
```
🔥 *NEW LPG REFILL ORDER* 🔥
-------------------------------
📦 *Items Ordered:*
• 1x 12kg LPG Refill - ৳ 1,450
• 1x Safety Regulator Package - ৳ 850
-------------------------------
💰 *Estimated Total:* ৳ 2,300 (+ Delivery)
📍 *Delivery Address:* House 14, Road 5, Block B, Mirpur
👤 *Customer Name:* Karim Ullah
📞 *Phone:* 01700000000
⏰ *Delivery Time:* Urgently needed (Within 1 hour)
📝 *Special Notes:* Old empty cylinder is ready for exchange.
-------------------------------
_Sent via QuickGas LPG Refill App_
```
