const API_BASE = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '');
const INVENTORY_URL = `${API_BASE}/inventory`;

async function request(path, options = {}) {
  const response = await fetch(`${INVENTORY_URL}${path}`, {
    ...options,
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...options.headers,
    },
  });

  if (response.status === 204) return null;
  const text = await response.text();
  let body;
  try {
    body = text ? JSON.parse(text) : null;
  } catch {
    body = null;
  }

  if (!response.ok) {
    throw new Error(body?.message || text || `Request failed (${response.status}).`);
  }
  return body;
}

export const fetchInventory = () => request('/all');
export const createInventory = (item) => request('/create', {
  method: 'POST',
  body: JSON.stringify(item),
});
export const updateInventory = (item) => request('/update', {
  method: 'PUT',
  body: JSON.stringify(item),
});
export const deleteInventory = (id) => request(`/delete/${encodeURIComponent(id)}`, {
  method: 'DELETE',
});
export const deductInventory = (id, quantity) => request(
  `/deduct/${encodeURIComponent(id)}/${quantity}`,
  { method: 'PUT' },
);
