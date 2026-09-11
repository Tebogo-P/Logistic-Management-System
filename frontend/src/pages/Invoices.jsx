import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Invoices.css';

const Invoices = () => {
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [newInvoice, setNewInvoice] = useState({
    total: '',
    paymentStatus: 'Pending',
    dateIssued: ''
  });

  useEffect(() => {
    fetchInvoices();
  }, []);

  const fetchInvoices = async () => {
    try {
      const response = await axios.get('http://localhost:8080/invoices/getall');
      setInvoices(response.data);
    } catch (error) {
      console.error('Error fetching invoices:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewInvoice({ ...newInvoice, [name]: value });
  };

  const handleCreateInvoice = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        total: parseFloat(newInvoice.total),
        paymentStatus: newInvoice.paymentStatus,
        dateIssued: newInvoice.dateIssued
      };
      await axios.post('http://localhost:8080/invoices/create', payload);
      setNewInvoice({ total: '', paymentStatus: 'Pending', dateIssued: '' });
      fetchInvoices();
    } catch (error) {
      console.error('Error creating invoice:', error);
    }
  };

  return (
    <div className="invoices-container">
      <header className="page-header">
        <h1>Invoices</h1>
        <p>Manage and view all your shipping invoices</p>
      </header>

      <div className="create-invoice-section">
        <h3>Create New Invoice</h3>
        <form onSubmit={handleCreateInvoice} className="create-invoice-form">
          <input
            type="number"
            name="total"
            placeholder="Total Amount"
            value={newInvoice.total}
            onChange={handleInputChange}
            required
            className="form-input"
          />
          <select
            name="paymentStatus"
            value={newInvoice.paymentStatus}
            onChange={handleInputChange}
            className="form-input"
          >
            <option value="Pending">Pending</option>
            <option value="Paid">Paid</option>
            <option value="Overdue">Overdue</option>
          </select>
          <input
            type="date"
            name="dateIssued"
            value={newInvoice.dateIssued}
            onChange={handleInputChange}
            required
            className="form-input"
          />
          <button type="submit" className="create-btn">Generate Invoice</button>
        </form>
      </div>

      <div className="invoices-controls">
        <input 
          type="text" 
          placeholder="Search invoices..." 
          className="search-input"
        />
        <button className="filter-btn">Filter</button>
      </div>

      <div className="invoices-list-container">
        <table className="invoices-table">
          <thead>
            <tr>
              <th>Invoice ID</th>
              <th>Date Issued</th>
              <th>Amount</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="5" className="empty-state">Loading...</td>
              </tr>
            ) : invoices.length === 0 ? (
              <tr>
                <td colSpan="5" className="empty-state">No invoices found.</td>
              </tr>
            ) : (
              invoices.map((invoice) => (
                <tr key={invoice.invoiceId}>
                  <td>{invoice.invoiceId}</td>
                  <td>{new Date(invoice.dateIssued).toLocaleDateString()}</td>
                  <td>${invoice.total.toFixed(2)}</td>
                  <td>
                    <span className={`status-badge ${invoice.paymentStatus.toLowerCase()}`}>
                      {invoice.paymentStatus}
                    </span>
                  </td>
                  <td>
                    <button className="action-btn view-btn">View</button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Invoices;
