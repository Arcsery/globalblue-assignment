# Frontend README

Angular frontend for the VAT Refund Calculation System.

## Tech Stack

* Angular 21
* Angular Material
* Angular HTTP Client
* Reactive Forms
* Angular Signals
* SCSS

## Running the Frontend

Install dependencies:

```bash
npm install
```

Start the application:

```bash
npm start
```

Frontend URL:

```text
http://localhost:4200
```

The backend must be running on:

```text
http://localhost:8080
```

## Features

### Purchase Form

The purchase form is opened from the header using the `Add new purchase` button.

The form contains:

* user email
* product name
* category
* net amount
* VAT rate
* purchase date

Validation includes:

* required fields
* valid email format
* positive net amount
* future purchase dates are disabled

On successful submit, the frontend calls:

```text
POST /api/purchases
```

### Purchases and VAT Summary View

The purchases page contains:

* user email search input
* VAT summary card
* Angular Material table
* frontend-side paginator

The summary displays:

* total net amount
* total VAT
* total refundable VAT

The table displays per-purchase breakdown:

* product name
* category
* net amount
* VAT rate
* VAT amount
* refund
* purchase date

Data is loaded from:

```text
GET /api/purchases/{userEmail}
```

### Toast Notifications

The frontend contains a shared toast notification service.

It is used for:

* successful purchase creation
* successful purchase loading
* API errors
* validation or backend error messages

Multiple toast messages can be displayed at the same time.

Responsibilities:

```text
header  -> toolbar and add purchase action
model   -> interfaces, enums and constants
pages   -> page-level components
service -> backend API services and shared services
shared  -> utilities and reusable components
```

## Build

```bash
npm run build
```

## Notes

The frontend uses Angular Material components for:

* toolbar
* dialog
* form fields
* select inputs
* datepicker
* summary card
* table
* paginator

The table paginator is frontend-side because the backend returns all purchases for a user as required by the assignment.
