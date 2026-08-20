import React from 'react';

const Invoices = () => {
  return (
    <div className="invoices-container">
      <header className="page-header">
        <h1>Invoices</h1>
        <p>Manage and view all your shipping invoices</p>
      </header>

      <div className="invoices-controls">
        <input 
          type="text" 
          placeholder="Search invoices..." 
          className="search-input"
        />
        <button className="filter-btn">Filter</button>
      </div>
    </div>
  );
};

export default Invoices;
