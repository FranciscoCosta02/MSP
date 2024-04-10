import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faFileWaveform, faHospital, faLaptopMedical} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import './appointments.css';

export const UpcomingAppointments = () => {

    const appointments =
        <div className={"card-appoint"}>
            <div className={"inner-card"}>
                <div className={"appointments-text"}>
                    <FontAwesomeIcon icon={faHospital} style={{marginRight: '10px'}}/>
                    <p>Consultations</p>
                    <button className="schedule">Schedule</button>
                </div>
            </div>
            <div className={"inner-card"}>
                <div className={"appointments-text"}>
                    <FontAwesomeIcon icon={faLaptopMedical} style={{marginRight: '10px'}}/>
                    <p>Videoconsultations</p>
                    <button className="schedule">Schedule</button>
                </div>
            </div>
            <div className={"inner-card"}>
                <div className={"appointments-text"}>
                    <FontAwesomeIcon icon={faFileWaveform} style={{marginRight: '10px'}}/>
                    <p>Exams</p>
                    <button className="schedule">Schedule</button>
                </div>
            </div>
        </div>

    return appointments
}