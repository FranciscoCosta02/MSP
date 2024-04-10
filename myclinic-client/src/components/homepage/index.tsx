import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faStethoscope} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import {UpcomingAppointments} from "../appointments";

export const Homepage = () => {

    const homepage =
        <div className={"homepage"}>
            <div className={"left"}>
                <div className={"myclinic-text"}>
                    <FontAwesomeIcon icon={faStethoscope} style={{marginRight: '10px'}}/>
                    <p>MyClinic</p>
                </div>
                <div className={"card-profile"}>
                    <div className="textContainer">
                        <p className="name">Pepper Potts</p>
                        <button className="profile">Profile</button>
                    </div>
                </div>
            </div>

            <div className={"right"}>
                <div className={"hello-text"}>
                    <p>Hello, Pepper Potts!</p>
                </div>
                <p className={"titles"}>Upcoming appointments</p>
                <UpcomingAppointments/>
            </div>
        </div>
    return homepage
}