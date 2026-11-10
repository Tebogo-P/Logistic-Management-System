
import React from "react";
import "./ShipmentTimeline.css";

const milestones = [
    "Dispatched",
    "In Transit",
    "Customs",
    "Out for Delivery",
    "Delivered"
];

function ShipmentTimeline({ tracking }) {

    const currentStatus = tracking?.shipmentStatus || "Pending";

    const currentIndex = milestones.findIndex(
        milestone =>
            milestone.toLowerCase() === currentStatus.trim().toLowerCase()
    );

    const isDelayed = currentStatus.toLowerCase() === "delayed";

    return (
        <div className="shipment-timeline-card">

            <div className="timeline-header">
                <div>
                    <h2>Shipment Journey</h2>
                    <p>Track the progress of this shipment.</p>
                </div>

                <span className={`timeline-status ${isDelayed ? "delayed" : ""}`}>
                    {currentStatus}
                </span>
            </div>

            <div className="timeline-details">
                <div>
                    <span>Tracking Number</span>
                    <strong>{tracking.trackingNumber || "N/A"}</strong>
                </div>

                <div>
                    <span>Current Location</span>
                    <strong>{tracking.currentLocation || "N/A"}</strong>
                </div>
            </div>

            <div className="timeline">

                {milestones.map((milestone, index) => {

                    const completed = currentIndex !== -1 && index < currentIndex;
                    const current = currentIndex !== -1 && index === currentIndex;

                    return (
                        <div
                            className={`timeline-item ${
                                completed ? "completed" :
                                    current ? "current" : "pending"
                            }`}
                            key={milestone}
                        >

                            <div className="timeline-marker">
                                {completed ? "✓" : index + 1}
                            </div>

                            <div className="timeline-content">
                                <h4>{milestone}</h4>

                                <p>
                                    {completed
                                        ? "Milestone completed"
                                        : current
                                            ? "Shipment is currently here"
                                            : "Awaiting completion"}
                                </p>

                                {current && (
                                    <span className="current-label">
                                        CURRENT STAGE
                                    </span>
                                )}
                            </div>

                            {index !== milestones.length - 1 && (
                                <div className="timeline-line"></div>
                            )}

                        </div>
                    );
                })}

            </div>

            {isDelayed && (
                <p className="timeline-warning">
                    This shipment is marked as delayed. Please check its latest location.
                </p>
            )}

            {currentIndex === -1 && !isDelayed && (
                <p className="timeline-warning">
                    Shipment milestones have not started yet.
                </p>
            )}

        </div>
    );
}

export default ShipmentTimeline;
