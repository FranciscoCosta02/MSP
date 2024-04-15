import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faArrowLeft, faSignOutAlt, faStethoscope} from "@fortawesome/free-solid-svg-icons";
import React from "react";
import './profile.css';
import avatar from "./avatar.png";
import {Link, useNavigate} from "react-router-dom";
import {MyClinicHeader} from "../homepage";

export const ProfileButton = () => {
    const navigate = useNavigate();

    const handleProfileClick = () => {
        navigate('/profile');
    };

    return (
        <button className="simple-button" onClick={handleProfileClick}>Profile</button>
    );
}

export const ProfileCard = (patient: { name: string | number | boolean | React.ReactElement<any, string | React.JSXElementConstructor<any>> | Iterable<React.ReactNode> | React.ReactPortal | null | undefined; }) => {
    return (
        <div>
            <div className={"card-profile"}>
                <div className="profile-text-card">
                    <img src={avatar} alt="Profile" className={"profile-image-card"} style={{marginRight: "5%"}}/>
                    <p className="name">{patient.name}</p>
                    <ProfileButton/>
                    <LogoutButton/>
                </div>
            </div>
        </div>
    )
}

export const LogoutButton = () => {
    const navigate = useNavigate();

    const handleProfileClick = () => {
        navigate('/login');
    };

    return (
        <button className="light-button" onClick={handleProfileClick} style={{marginTop: "5%"}}><FontAwesomeIcon icon={faSignOutAlt} /> Logout</button>
    );
}

/*
const PrescriptionsTable = ({ prescriptions }) => {
    return (
        <table>
            <thead>
            <tr>
                <th>Date</th>
                <th>Doctor</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            {prescriptions.map((prescription, index) => (
                <tr key={index}>
                    <td>{prescription.date}</td>
                    <td>{prescription.doctor}</td>
                    <td>
                        <button onClick={() => handleOpenPrescription(prescription)}>
                            Open
                        </button>
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
};
const handleOpenPrescription = (prescription) => {
  // Implement the logic to open the prescription
  console.log('Opening prescription:', prescription);
};

*/

const PrescriptionsTable = () => {
    const examplePrescriptions = [
        { date: '2024-04-01', doctor: 'Dr. Smith' },
        { date: '2024-04-05', doctor: 'Dr. Johnson' },
        { date: '2024-04-10', doctor: 'Dr. Brown' },
        { date: '2024-04-15', doctor: 'Dr. Patel' },
    ];

    const handleOpenPrescription = () => {

    };

    return (
        <table className="prescriptions-table">
            <thead>
            <tr>
                <th>Date</th>
                <th>Doctor</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            {examplePrescriptions.map((prescription, index) => (
                <tr key={index}>
                    <td>{prescription.date}</td>
                    <td>{prescription.doctor}</td>
                    <td>
                        <button className="open-button" onClick={() => handleOpenPrescription()}>
                            Open prescription
                        </button>
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
};

export default PrescriptionsTable;


export const Profile = () => {

    const patient = {name: 'Pepper Potts', email: 'teste@mail.com', birthDate: '20/12/2001', phone: '999999999', nif: '111111111'}

    const profile =
        <div className={"profile-page"}>
            <div className={"left"}>
                <MyClinicHeader/>
                <ProfileCard name={patient.name}/>
            </div>

            <div className={"right"}>
                <div>
                    <div className={"page-title"}>
                        <p>Profile</p>
                    </div>
                    <div className={"profile-container"}>
                        <img src={avatar} alt="Profile" className={"profile-image"} style={{marginRight: "5%"}}/>
                        <div>
                            <p className={"page-sub-title"}>Name</p>
                            <p className={"page-text-info"}>Rita Lopes</p>
                            <p className={"page-sub-title"}>Email</p>
                            <p className={"page-text-info"}>rg.lopes@campus.fct.unl.pt</p>
                            <p className={"page-sub-title"}>Birth date</p>
                            <p className={"page-text-info"}>20/12/2001</p>
                            <p className={"page-sub-title"}>Phone number</p>
                            <p className={"page-text-info"}>939283352</p>
                            <p className={"page-sub-title"}>NIF</p>
                            <p className={"page-text-info"}>241975417</p>
                        </div>
                    </div>
                    <div>
                        <p className={"page-sub-title"}>Prescriptions</p>
                        <PrescriptionsTable/>
                    </div>

                    <p className={"page-sub-title"}>Medical History</p>
                    <button className={"simple-button"}>See medical history</button>
                    <p className={"page-sub-title"}>Household</p>
                    <button className={"simple-button"}>See household</button>
                    <p></p>
                    <Link to="/">
                        <button className={"light-button"}><FontAwesomeIcon icon={faArrowLeft} /> Back</button>
                    </Link>
                </div>
            </div>
        </div>

    return profile
}