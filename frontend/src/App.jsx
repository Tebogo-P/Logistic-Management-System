import { useEffect, useMemo, useState } from 'react';
import {
  createInventory,
  deleteInventory,
  deductInventory,
  fetchInventory,
  updateInventory,
} from './inventoryApi';

const EMPTY_FORM = {
  itemName: '',
  sku: '',
  companyId: '',
  quantityAvailable: '0',
  unitWeight: '0',
};

function App() {
  const [items, setItems] = useState([]);
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingId, setEditingId] = useState(null);
  const [search, setSearch] = useState('');
  const [deductions, setDeductions] = useState({});
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  async function refreshInventory() {
    setLoading(true);
    setError('');
    try {
      setItems(await fetchInventory());
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    refreshInventory();
  }, []);

  const visibleItems = useMemo(() => {
    const term = search.trim().toLowerCase();
    if (!term) return items;
    return items.filter((item) =>
      [item.itemName, item.sku, item.companyId, item.inventoryId]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(term)),
    );
  }, [items, search]);

  const totalUnits = items.reduce((total, item) => total + item.quantityAvailable, 0);
  const lowStockCount = items.filter((item) => item.quantityAvailable <= 5).length;

  function updateForm(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  function editItem(item) {
    setEditingId(item.inventoryId);
    setForm({
      itemName: item.itemName,
      sku: item.sku,
      companyId: item.companyId,
      quantityAvailable: String(item.quantityAvailable),
      unitWeight: String(item.unitWeight),
    });
    setNotice('');
    setError('');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  function cancelEdit() {
    setEditingId(null);
    setForm(EMPTY_FORM);
  }

  async function submitItem(event) {
    event.preventDefault();
    setBusy(true);
    setError('');
    setNotice('');

    const payload = {
      ...(editingId ? { inventoryId: editingId } : {}),
      itemName: form.itemName.trim(),
      sku: form.sku.trim(),
      companyId: form.companyId.trim(),
      quantityAvailable: Number(form.quantityAvailable),
      unitWeight: Number(form.unitWeight),
    };

    try {
      if (editingId) {
        await updateInventory(payload);
        setNotice('Inventory item updated.');
      } else {
        await createInventory(payload);
        setNotice('Inventory item added.');
      }
      cancelEdit();
      await refreshInventory();
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setBusy(false);
    }
  }

  async function removeItem(item) {
    if (!window.confirm(`Delete ${item.itemName} (${item.sku})?`)) return;
    setBusy(true);
    setError('');
    setNotice('');
    try {
      await deleteInventory(item.inventoryId);
      setItems((current) => current.filter((entry) => entry.inventoryId !== item.inventoryId));
      setNotice('Inventory item deleted.');
      if (editingId === item.inventoryId) cancelEdit();
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setBusy(false);
    }
  }

  async function deductStock(item) {
    const quantity = Number(deductions[item.inventoryId] || 1);
    if (!Number.isInteger(quantity) || quantity < 1) {
      setError('Enter a whole number of at least 1 to deduct.');
      return;
    }
    setBusy(true);
    setError('');
    setNotice('');
    try {
      const updated = await deductInventory(item.inventoryId, quantity);
      setItems((current) => current.map((entry) =>
        entry.inventoryId === updated.inventoryId ? updated : entry,
      ));
      setNotice(`${quantity} unit${quantity === 1 ? '' : 's'} deducted from ${item.itemName}.`);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="inventory-page">
      <header className="topbar">
        <a className="brand" href="/" aria-label="Logistic Management System home">
          <span className="brand-mark" aria-hidden="true">L</span>
          <span>Logistic<span className="brand-light">Flow</span></span>
        </a>
        <span className="topbar-label">OPERATIONS / INVENTORY</span>
      </header>

      <section className="page-content">
        <div className="page-heading">
          <div>
            <p className="eyebrow">STOCK CONTROL</p>
            <h1>Inventory</h1>
            <p className="subheading">Track stock levels and keep your catalogue up to date.</p>
          </div>
          <div className="live-indicator"><span /> Inventory overview</div>
        </div>

        {error && <div className="alert alert-error" role="alert">{error}</div>}
        {notice && <div className="alert alert-success" role="status">{notice}</div>}

        <section className="summary-grid" aria-label="Inventory summary">
          <article className="summary-card">
            <span className="summary-icon icon-blue" aria-hidden="true">▦</span>
            <div><p>Unique items</p><strong>{items.length}</strong></div>
          </article>
          <article className="summary-card">
            <span className="summary-icon icon-green" aria-hidden="true">▤</span>
            <div><p>Total units</p><strong>{totalUnits.toLocaleString()}</strong></div>
          </article>
          <article className="summary-card">
            <span className="summary-icon icon-amber" aria-hidden="true">!</span>
            <div><p>Low stock</p><strong>{lowStockCount}</strong></div>
          </article>
        </section>

        <section className="content-grid">
          <section className="panel form-panel">
            <div className="panel-heading">
              <div>
                <p className="eyebrow">{editingId ? 'EDIT ITEM' : 'CATALOGUE'}</p>
                <h2>{editingId ? 'Update inventory' : 'Add an item'}</h2>
              </div>
              {editingId && <button className="text-button" type="button" onClick={cancelEdit}>Cancel</button>}
            </div>
            <form onSubmit={submitItem}>
              <label>
                Item name
                <input name="itemName" value={form.itemName} onChange={updateForm} placeholder="e.g. Shipping carton" required maxLength="120" />
              </label>
              <div className="form-row">
                <label>
                  SKU
                  <input name="sku" value={form.sku} onChange={updateForm} placeholder="e.g. BOX-001" required maxLength="80" />
                </label>
                <label>
                  Company ID
                  <input name="companyId" value={form.companyId} onChange={updateForm} placeholder="e.g. COMP-01" required maxLength="80" />
                </label>
              </div>
              <div className="form-row">
                <label>
                  Quantity
                  <input name="quantityAvailable" type="number" min="0" step="1" value={form.quantityAvailable} onChange={updateForm} required />
                </label>
                <label>
                  Unit weight (kg)
                  <input name="unitWeight" type="number" min="0" step="0.01" value={form.unitWeight} onChange={updateForm} required />
                </label>
              </div>
              <button className="primary-button form-submit" type="submit" disabled={busy}>
                <span aria-hidden="true">{editingId ? '✓' : '+'}</span>
                {busy ? 'Saving…' : editingId ? 'Save changes' : 'Add to inventory'}
              </button>
            </form>
          </section>

          <section className="panel list-panel">
            <div className="panel-heading list-heading">
              <div>
                <p className="eyebrow">STOCK LIST</p>
                <h2>All inventory <span className="item-count">{items.length}</span></h2>
              </div>
              <button className="refresh-button" type="button" onClick={refreshInventory} disabled={loading} aria-label="Refresh inventory">↻</button>
            </div>
            <label className="search-box">
              <span aria-hidden="true">⌕</span>
              <input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search item, SKU or company…" aria-label="Search inventory" />
            </label>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>ITEM</th>
                    <th>SKU / COMPANY</th>
                    <th>WEIGHT</th>
                    <th>AVAILABLE</th>
                    <th><span className="sr-only">Actions</span></th>
                  </tr>
                </thead>
                <tbody>
                  {loading ? (
                    <tr><td className="table-message" colSpan="5">Loading inventory…</td></tr>
                  ) : visibleItems.length === 0 ? (
                    <tr><td className="table-message" colSpan="5">{search ? 'No items match your search.' : 'No inventory yet. Add your first item to get started.'}</td></tr>
                  ) : visibleItems.map((item) => (
                    <tr key={item.inventoryId}>
                      <td>
                        <div className="item-name">{item.itemName}</div>
                        <div className="item-id">ID: {item.inventoryId}</div>
                      </td>
                      <td>
                        <div className="sku-value">{item.sku}</div>
                        <div className="company-value">{item.companyId}</div>
                      </td>
                      <td>{Number(item.unitWeight).toLocaleString(undefined, { maximumFractionDigits: 2 })} kg</td>
                      <td>
                        <span className={`stock-badge ${item.quantityAvailable <= 5 ? 'stock-low' : 'stock-ok'}`}>
                          {item.quantityAvailable} {item.quantityAvailable === 1 ? 'unit' : 'units'}
                        </span>
                      </td>
                      <td>
                        <div className="row-actions">
                          <div className="deduct-control">
                            <input
                              type="number"
                              min="1"
                              step="1"
                              value={deductions[item.inventoryId] ?? '1'}
                              onChange={(event) => setDeductions((current) => ({ ...current, [item.inventoryId]: event.target.value }))}
                              aria-label={`Quantity to deduct from ${item.itemName}`}
                            />
                            <button type="button" onClick={() => deductStock(item)} disabled={busy} title="Deduct stock">−</button>
                          </div>
                          <button className="icon-button" type="button" onClick={() => editItem(item)} disabled={busy} aria-label={`Edit ${item.itemName}`} title="Edit">✎</button>
                          <button className="icon-button delete-button" type="button" onClick={() => removeItem(item)} disabled={busy} aria-label={`Delete ${item.itemName}`} title="Delete">×</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <div className="table-footer">
              <span>Showing {visibleItems.length} of {items.length} items</span>
              <span>Low stock threshold: 5 units</span>
            </div>
          </section>
        </section>
      </section>
    </main>
  );
}

export default App;
