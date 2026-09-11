/* 24/08/2026
 *App.jsx
 *App in services folder
 *Maghdie Petersen 230600204
 *  */

import './index.css';
import React, {useState} from 'react';
import LmsLayout from './components/LmsLayout.jsx';
import Companies from './pages/Companies.jsx';
import Home from './pages/Home.jsx';
import Inventory from './pages/Inventory.jsx';
import Invoices from './pages/Invoices.jsx';
import Shipments from './pages/Shipments.jsx';

function App() {
    const [activeTab, setActiveTab] = useState('companies');

    const renderContent = () => {
        switch (activeTab){
            case 'companies': return <Companies />
            case 'home': return <Home />
            case 'inventory': return <Inventory />
            case 'invoices': return <Invoices />
            case 'shipments': return <Shipments />
            default: return <Companies />
        }
    };

  return (
<<<<<<< HEAD
<<<<<<< HEAD
    <LmsLayout activeTab={activeTab} setActiveTab={setActiveTab()}>
=======
    <LmsLayout activeTab={activeTab} setActiveTab={setActiveTab}>
>>>>>>> cfe486b (Initial commit with setup fixes (Tailwind config, Java 17))
=======
    <LmsLayout activeTab={activeTab} setActiveTab={setActiveTab}>
>>>>>>> cfe486b (Initial commit with setup fixes (Tailwind config, Java 17))
        {renderContent()}
    </LmsLayout>
  );
}

export default App;
