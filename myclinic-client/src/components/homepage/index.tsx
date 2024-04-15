import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faStethoscope} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import {Appointments, UpcomingAppointments} from "../appointments";
import {ProfileCard} from "../profile";

export const MyClinicHeader = () => {
    return (
        <div className={"my-clinic-header"}>
            <p><FontAwesomeIcon icon={faStethoscope}/> MyClinic</p>
        </div>
    )
}

export const Homepage = () => {

    const patient = {name: 'Pepper Potts', email: 'teste@mail.com', birthDate: '20/12/2001', phone: '999999999', nif: '111111111'}

    return <div className={"homepage"}>
        <div className={"left"}>
            <MyClinicHeader/>
            <ProfileCard name={patient.name}/>
        </div>

        <div className={"right"}>
            <div className={"hello-text"}>
                <p>Hello, {patient.name}!</p>
            </div>
            <div style={{display: 'flex', marginBottom: '20px'}}>
                <div style={{flexGrow: 1, marginRight: '10px'}}>
                    <p className={"titles"}>Schedule appointments</p>
                    <UpcomingAppointments/>
                </div>
                <div style={{flexGrow: 3}}>
                    <p className={"titles"}>Appointments</p>
                    <Appointments/>
                </div>
            </div>
        </div>
    </div>
}