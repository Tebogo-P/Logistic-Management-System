import { useEffect, useMemo, useState } from 'react';

const navItems = ['Dashboard', 'Shipments', 'Inventory', 'Companies', 'Invoices', 'Tracking'];

const fallbackStats = [
  { label: 'Active shipments', value: '1,248', change: '+12.4%' },
  { label: 'On-time delivery', value: '96.8%', change: '+2.1%' },
  { label: 'Warehouse utilization', value: '84%', change: '+6.5%' },
  { label: 'Fleet availability', value: '92%', change: '+4.3%' },
];

const fallbackFeatures = [
  {
    title: 'Route optimization',
    description: 'Plan faster, more efficient delivery routes with live traffic and capacity insights.',
  },
  {
    title: 'Inventory visibility',
    description: 'Track stock movement across warehouses and reduce your risk of stockouts or delays.',
  },
  {
    title: 'Invoice control',
    description: 'Monitor billing, cost tracking, and payment status from one transparent workflow.',
  },
  {
    title: 'Shipment tracking',
    description: 'Give teams and customers real-time updates on every package from dispatch to delivery.',
  },
];

const fallbackActivity = [
  'Cape Town dispatch completed for 42 pallets',
  'New inbound cargo assigned to Durban hub',
  'Invoice batch reconciliation finished successfully',
  'Three high-priority shipments flagged for rerouting',
];

const safeArray = (value) => (Array.isArray(value) ? value : []);

const formatSignedPercent = (value) => `${value >= 0 ? '+' : ''}${value.toFixed(1)}%`;

const Home = () => {
  const [shipments, setShipments] = useState([]);
  const [inventory, setInventory] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;

    const loadDashboard = async () => {
      try {
        const [shipmentsResponse, inventoryResponse, companiesResponse] = await Promise.all([
          fetch('/api/shipments').then((response) => (response.ok ? response.json() : [])).catch(() => []),
          fetch('/api/inventory/getall').then((response) => (response.ok ? response.json() : [])).catch(() => []),
          fetch('/api/companies/getall').then((response) => (response.ok ? response.json() : [])).catch(() => []),
        ]);

        if (!cancelled) {
          setShipments(safeArray(shipmentsResponse));
          setInventory(safeArray(inventoryResponse));
          setCompanies(safeArray(companiesResponse));
        }
      } catch {
        if (!cancelled) {
          setShipments([]);
          setInventory([]);
          setCompanies([]);
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    };

    loadDashboard();

    return () => {
      cancelled = true;
    };
  }, []);

  const dashboard = useMemo(() => {
    const shipmentCount = shipments.length;
    const inventoryCount = inventory.length;
    const totalQuantity = inventory.reduce((sum, item) => sum + Number(item.quantityAvailable ?? item.quantity ?? 0), 0);
    const activeCompanies = companies.filter((company) => {
      const value = company.isActive ?? company.active ?? true;
      return Boolean(value);
    }).length;

    const onTimeRate = shipmentCount
      ? shipments.reduce((sum, shipment) => {
          const hasDates = shipment.dispatchDate && shipment.estimatedDeliveryDate;
          if (!hasDates) {
            return sum + 1;
          }

          const dispatchDate = new Date(shipment.dispatchDate);
          const estimatedDate = new Date(shipment.estimatedDeliveryDate);
          return sum + (estimatedDate >= dispatchDate ? 1 : 0);
        }, 0) / shipmentCount * 100
      : 96.8;

    const warehouseUtilization = inventoryCount
      ? Math.min(99, Math.max(45, Math.round((totalQuantity / Math.max(1, totalQuantity + 900)) * 100)))
      : 84;

    const fleetAvailability = shipmentCount
      ? Math.min(99, Math.max(88, 96 - (shipmentCount % 6)))
      : 92;

    const shipmentLabel = shipmentCount ? shipmentCount.toLocaleString() : '1,248';
    const deliveryValue = `${Math.min(99.9, Math.max(85, onTimeRate)).toFixed(1)}%`;

    return {
      stats: [
        { label: 'Active shipments', value: shipmentLabel, change: formatSignedPercent(12.4) },
        { label: 'On-time delivery', value: deliveryValue, change: formatSignedPercent(2.1) },
        { label: 'Warehouse utilization', value: `${warehouseUtilization}%`, change: formatSignedPercent(6.5) },
        { label: 'Fleet availability', value: `${fleetAvailability}%`, change: formatSignedPercent(4.3) },
      ],
      hero: {
        onTime: `${Math.min(99, Math.max(88, Math.round(onTimeRate)))}%`,
        deliveries: shipmentCount ? Math.min(999, shipmentCount + 120).toLocaleString() : '328',
        avgTransit: shipmentCount ? `${Math.max(1.2, 2.4 - shipmentCount / 800).toFixed(1)}d` : '2.4d',
        serviceLevel: `${Math.min(99.9, Math.max(95, activeCompanies * 3 + 90)).toFixed(1)}%`,
      },
      activity: shipmentCount
        ? shipments.slice(0, 4).map((shipment, index) => {
            const destination = shipment.destination || 'Regional hub';
            const shipmentId = shipment.shipmentId || `SHP-${index + 1}`;
            return `${destination} dispatch updated for ${shipmentId}`;
          })
        : fallbackActivity,
      priorityQueue: [
        `${shipmentCount || 18} shipments in transit`,
        `${Math.max(1, inventoryCount || 7)} stock checks flagged`,
        `${Math.max(1, activeCompanies || 3)} active partner hubs`,
      ],
    };
  }, [companies, inventory, shipments]);

  const statCards = dashboard.stats ?? fallbackStats;
  const featureCards = fallbackFeatures;
  const activity = dashboard.activity ?? fallbackActivity;

  return (
    <div className="lms-shell">
      <aside className="lms-sidebar">
        <div className="lms-brand-wrap">
          <div className="lms-brand-mark">LM</div>
          <div>
            <span className="lms-brand-name">LogiFlow</span>
            <small>LMS Platform</small>
          </div>
        </div>

        <nav className="lms-sidebar-nav" aria-label="Main navigation">
          {navItems.map((item, index) => (
            <button type="button" key={item} className={index === 0 ? 'lms-nav-item active' : 'lms-nav-item'}>
              {item}
            </button>
          ))}
        </nav>

        <div className="lms-side-card">
          <small>Service level</small>
          <strong>{loading ? '--' : dashboard.hero.serviceLevel}</strong>
          <span>Across {Math.max(14, companies.length || 14)} regional hubs</span>
        </div>
      </aside>

      <div className="lms-main-panel">
        <header className="lms-topbar">
          <div className="lms-search-box">
            <span className="lms-search-icon">⌕</span>
            <input type="text" defaultValue={loading ? 'Loading dashboard…' : 'Search shipments'} readOnly aria-label="Search shipments" />
          </div>

          <div className="lms-topbar-actions">
            <button type="button" className="lms-ghost-btn">Export report</button>
            <button type="button" className="lms-primary-btn">New shipment</button>

            <div className="lms-user-box">
              <span className="lms-avatar">JN</span>
              <div>
                <strong>Jane Ndlovu</strong>
                <small>Logistics Lead</small>
              </div>
            </div>
          </div>
        </header>

        <main className="lms-content">
          <section className="lms-hero">
            <div className="lms-hero-copy">
              <span className="lms-eyebrow">Smarter supply chain operations</span>
              <h1>Move goods faster with full operational clarity.</h1>
              <p>
                Control inventory, shipments, invoices, and warehouse performance from one streamlined
                logistics platform designed for growth.
              </p>

              <div className="lms-hero-actions">
                <button type="button" className="lms-primary-btn">Get started</button>
                <button type="button" className="lms-secondary-btn">View dashboard</button>
              </div>

              <div className="lms-trust-line">
                <span>{loading ? 'Connecting to live system…' : 'Trusted by 180+ logistics teams'}</span>
                <div className="lms-trust-dots" aria-hidden="true">
                  <span />
                  <span />
                  <span />
                </div>
              </div>
            </div>

            <div className="lms-hero-panel">
              <div className="lms-card lms-main-card">
                <div className="lms-card-header">
                  <span>Operations status</span>
                  <span className="lms-badge success">{loading ? 'Syncing' : 'Live'}</span>
                </div>

                <div className="lms-ring">
                  <div className="lms-ring-inner">
                    <strong>{loading ? '--' : dashboard.hero.onTime}</strong>
                    <span>On time</span>
                  </div>
                </div>

                <div className="lms-route-grid">
                  <div>
                    <small>Deliveries today</small>
                    <strong>{loading ? '--' : dashboard.hero.deliveries}</strong>
                  </div>
                  <div>
                    <small>Avg. transit</small>
                    <strong>{loading ? '--' : dashboard.hero.avgTransit}</strong>
                  </div>
                </div>
              </div>

              <div className="lms-card lms-side-card">
                <span className="lms-mini-label">Priority queue</span>
                <ul>
                  {dashboard.priorityQueue.map((item, index) => (
                    <li key={item}>
                      <span className={`lms-dot ${['blue', 'green', 'amber'][index % 3]}`} />
                      {item}
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </section>

          <section className="lms-stats" aria-label="Key metrics">
            {statCards.map((stat) => (
              <article key={stat.label} className="lms-stat-card">
                <span>{stat.label}</span>
                <strong>{stat.value}</strong>
                <em>{stat.change}</em>
              </article>
            ))}
          </section>

          <section className="lms-feature-section">
            <div className="lms-section-header">
              <span className="lms-eyebrow">Built for modern logistics</span>
              <h2>Everything your operations team needs in one place.</h2>
            </div>

            <div className="lms-feature-grid">
              {featureCards.map((feature) => (
                <article key={feature.title} className="lms-feature-card">
                  <div className="lms-feature-icon" aria-hidden="true">→</div>
                  <h3>{feature.title}</h3>
                  <p>{feature.description}</p>
                </article>
              ))}
            </div>
          </section>

          <section className="lms-insights">
            <div className="lms-insight-card">
              <div className="lms-section-header compact">
                <span className="lms-eyebrow">Live insights</span>
                <h2>Operational health across the network.</h2>
              </div>

              <div className="lms-bars" aria-label="Operational performance chart">
                <span style={{ height: loading ? '38%' : `${Math.max(30, Math.min(100, (shipments.length || 18) * 4))}%` }} />
                <span style={{ height: loading ? '52%' : `${Math.max(30, Math.min(100, (inventory.length || 12) * 6))}%` }} />
                <span style={{ height: loading ? '46%' : `${Math.max(35, Math.min(100, Math.round((companies.length || 14) * 5.5)))}%` }} />
                <span style={{ height: loading ? '71%' : `${Math.max(45, Math.min(100, (shipments.length || 25) * 2.5))}%` }} />
                <span style={{ height: loading ? '62%' : `${Math.max(35, Math.min(100, (inventory.length || 17) * 3.5))}%` }} />
                <span style={{ height: loading ? '90%' : '90%' }} />
                <span style={{ height: loading ? '82%' : '82%' }} />
              </div>
            </div>

            <aside className="lms-activity-card">
              <div className="lms-card-header">
                <span>Recent activity</span>
                <span className="lms-badge muted">Today</span>
              </div>

              <ul>
                {activity.map((item) => (
                  <li key={item}>{item}</li>
                ))}
              </ul>
            </aside>
          </section>
        </main>
      </div>
    </div>
  );
};

export default Home;